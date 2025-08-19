package runners;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import com.intuit.karate.JsonUtils;
import org.junit.jupiter.api.Test;

import java.net.http.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.fail;

public class PixRunner {

    @Test
    void runSuiteAndNotify() throws Exception {

        // --- TAGS:
        String envTags = System.getenv("KARATE_TAGS");
        String tagsProp = System.getProperty("TAGS", envTags == null ? "" : envTags).trim();
        String[] tags = tagsProp.isEmpty() ? new String[]{} : tagsProp.split("\\s*,\\s*");

        long t0 = System.nanoTime();

        var rb = Runner.path("classpath:features");
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

        // ---- monta o payload padrão para o Slack
        Map<String, Object> payload = utils.SlackUtils.buildSummaryCard(
                total,                // Casos de Teste
                estimatedHuman,       // Tempo Total Estimado (manual)
                successHuman,         // Tempo Execução Automatizada (suite)
                failed,               // lista falhados
                passed                // lista passados
        );

        // incluir link da run do GitHub se vier por env
        String runUrl = System.getenv("GITHUB_RUN_URL");
        if (runUrl != null && !runUrl.isBlank()) {
            payload.put("runUrl", runUrl);
        }

        // ---- envia para o Slack
        String slackWebhook = resolveWebhook();
        String payloadJson = JsonUtils.toJson(payload);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder(URI.create(slackWebhook))
                .header("Content-Type", "application/json; charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(payloadJson, StandardCharsets.UTF_8))
                .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

        System.out.println("Slack status: " + resp.statusCode() + " body: " + resp.body());

        if (results.getFailCount() > 0) {
            fail("Falhas na execução: " + results.getErrorMessages());
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