package runners;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import com.intuit.karate.JsonUtils;
import org.junit.jupiter.api.Test;

import java.net.http.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.fail;

public class PixRunner {

    @Test
    void runSuiteAndNotify() throws Exception {

        // --- TAGS: aceita -DTAGS ou env KARATE_TAGS
        String envTags = System.getenv("KARATE_TAGS");
        String tagsProp = System.getProperty("TAGS", envTags == null ? "" : envTags).trim();
        String[] tags = tagsProp.isEmpty() ? new String[]{} : tagsProp.split("\\s*,\\s*");

        long t0 = System.nanoTime();

        var rb = Runner.path("classpath:features"); // evita raw type warning
        if (tags.length > 0) rb.tags(tags);

        Results results = rb.parallel(1);
        long elapsedMs = (System.nanoTime() - t0) / 1_000_000L;

        System.out.println("featuresTotal=" + results.getFeaturesTotal()
                + " failCount=" + results.getFailCount()
                + " reportDir=" + results.getReportDir()
                + " elapsedMs=" + elapsedMs
                + " tags=" + Arrays.toString(tags));

        var passed = utils.RunMetrics.getPassed();
        var failed = utils.RunMetrics.getFailed();
        int total = passed.size() + failed.size();

        // ---- Tempo Estimado Manual
        int minPerScenario = 2;
        try {
            String prop = System.getProperty("ESTIMATED_MIN_PER_SCENARIO");
            if (prop != null && !prop.isBlank()) {
                minPerScenario = Integer.parseInt(prop);
            }
        } catch (Exception ignored) {}
        String estimatedHuman = utils.RunMetrics.calcularTempoEstimado(total, minPerScenario);

        // ---- Tempo Execução Automatizada
        String successHuman = utils.RunMetrics.fmtDur(elapsedMs);
        utils.RunMetrics.setTotalEstimatedHuman(estimatedHuman);

        // ---- payload “rico”
        Map<String, Object> payload = utils.SlackUtils.buildSummaryCard(
                total,                // Casos de Teste
                estimatedHuman,       // Tempo Total Estimado (manual)
                successHuman,         // Tempo Execução Automatizada (suite)
                failed,               // lista falhados
                passed                // lista passados
        );

        // opcional: link da run do GitHub, se existir no env
        String runUrl = System.getenv("GITHUB_RUN_URL");
        if (runUrl != null && !runUrl.isBlank()) {
            payload.put("runUrl", runUrl);
        }

        String slackWebhook = resolveWebhook(); // lê SLACK_WEBHOOK_URL ou SLACK_WEBHOOK (env ou -D)
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        // 1ª tentativa: payload rico
        String payloadJson = JsonUtils.toJson(payload);
        int status = postSlack(client, slackWebhook, payloadJson);
        System.out.println("Slack (rich) status: " + status);

        // Se falhar, fallback com texto simples (sempre funciona)
        if (status != 200) {
            String text = String.format(
                    "CI – Karate Pix%nStatus: %s | Total: %d | Falhas: %d | Tempo: %s%nRun: %s",
                    results.getFailCount() > 0 ? "❌ FAIL" : "✅ OK",
                    total, results.getFailCount(), successHuman,
                    runUrl == null ? "—" : runUrl
            );
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("text", text);
            int status2 = postSlack(client, slackWebhook, JsonUtils.toJson(fallback));
            System.out.println("Slack (fallback) status: " + status2);
        }

        if (results.getFailCount() > 0) {
            fail("Falhas na execução: " + results.getErrorMessages());
        }
    }

    private static int postSlack(HttpClient client, String webhook, String json) {
        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create(webhook))
                    .header("Content-Type", "application/json; charset=utf-8")
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .timeout(Duration.ofSeconds(20))
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            System.out.println("Slack body: " + resp.body());
            return resp.statusCode();
        } catch (Exception e) {
            System.out.println("Erro ao enviar Slack: " + e);
            return -1;
        }
    }

    private static String resolveWebhook() {
        for (String key : new String[]{"SLACK_WEBHOOK_URL", "SLACK_WEBHOOK"}) {
            String v = System.getProperty(key);
            if (v != null && !v.isBlank()) return v;
            v = System.getenv(key);
            if (v != null && !v.isBlank()) return v;
        }
        throw new IllegalStateException(
            "Slack Webhook não configurado. Defina SLACK_WEBHOOK_URL (ou SLACK_WEBHOOK) via -D ou variável de ambiente."
        );
    }
}
