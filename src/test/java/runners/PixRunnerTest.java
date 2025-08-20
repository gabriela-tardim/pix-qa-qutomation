package runners;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import com.intuit.karate.JsonUtils;
import org.junit.jupiter.api.Test;

import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.net.URI;
import java.util.*;
import static org.junit.jupiter.api.Assertions.fail;

public class PixRunnerTest {

    static {
        System.setProperty("karate.env", "hml");
    }

    @Test
    void runSuiteAndNotify() throws Exception {

        String tagsProp = Optional.ofNullable(System.getProperty("TAGS"))
                .orElse(Optional.ofNullable(System.getenv("KARATE_TAGS")).orElse(""))
                .trim()
                .replaceAll("\\s*(?:&|and)\\s*", ",");
        String[] tags = Arrays.stream(tagsProp.split("\\s*,\\s*"))
                .filter(s -> !s.isBlank())
                .toArray(String[]::new);

        long t0 = System.nanoTime();

        Results results = Runner.path("classpath:features/suite/suitePix.feature")
        .tags(tags.length > 0 ? tags : new String[]{"~@util"})
        .parallel(1);

        long elapsedMs = (System.nanoTime() - t0) / 1_000_000L;

        System.out.println("featuresTotal=" + results.getFeaturesTotal()
                + " failCount=" + results.getFailCount()
                + " reportDir=" + results.getReportDir()
                + " elapsedMs=" + elapsedMs
                + " tags=" + Arrays.toString(tags));

        var passed = utils.RunMetrics.getPassed();
        var failed = utils.RunMetrics.getFailed();
        int total = passed.size() + failed.size();

        int minPerScenario = 2;
        try {
            String prop = System.getProperty("ESTIMATED_MIN_PER_SCENARIO");
            if (prop != null && !prop.isBlank())
                minPerScenario = Integer.parseInt(prop);
        } catch (Exception ignored) {
        }
        String estimatedHuman = utils.RunMetrics.calcularTempoEstimado(total, minPerScenario);
        String successHuman = utils.RunMetrics.fmtDur(elapsedMs);
        utils.RunMetrics.setTotalEstimatedHuman(estimatedHuman);

        Map<String, Object> payload = utils.SlackUtils.buildSummaryCard(
                total, // Casos de Teste
                estimatedHuman, // Tempo Total Estimado (manual)
                successHuman, // Tempo Execução Automatizada (suite)
                failed, // lista falhados
                passed // lista passados
        );

        String webhook = firstNonEmpty(
                System.getProperty("SLACK_WEBHOOK_URL"),
                System.getenv("SLACK_WEBHOOK_URL"),
                System.getProperty("POST"),
                System.getenv("SLACK_WEBHOOK"));
        if (webhook != null && !webhook.isBlank()) {
            String payloadJson = JsonUtils.toJson(payload);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder(URI.create(webhook))
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(payloadJson.getBytes(StandardCharsets.UTF_8)))
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            System.out.println("Slack status: " + resp.statusCode() + " body: " + resp.body());
        } else {
            System.out.println("SLACK_WEBHOOK_URL/SLACK_WEBHOOK não definido; pulando notificação.");
        }

        // === Falhar se necessário
        if (results.getFeaturesTotal() == 0) {
            fail("Nenhuma feature encontrada para as tags: " + Arrays.toString(tags));
        }
        if (results.getFailCount() > 0) {
            fail("Falhas na execução: " + results.getErrorMessages());
        }
    }

    private static String firstNonEmpty(String... vals) {
        for (String v : vals)
            if (v != null && !v.isBlank())
                return v;
        return null;
    }
    
}
