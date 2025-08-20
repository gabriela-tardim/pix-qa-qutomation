package utils;

import java.util.*;

public class SlackUtils {

    private static String toCodeBlock(List<String> items) {
        if (items == null || items.isEmpty()) return "—";
        return "```\n" + String.join("\n", items) + "\n```";
    }

    public static Map<String, Object> buildSummaryCard(
            String title,
            int totalCases,
            String totalEstimatedHuman,
            String successTimeHuman,
            List<String> failedScenarios,
            List<String> passedScenarios) {


        Map<String, Object> header = Map.of(
            "type", "header",
            "text", Map.of("type", "plain_text", "text", ":rocket: Testes Pix")
        );

        Map<String, Object> topFields = Map.of(
            "type", "section",
            "fields", List.of(
                Map.of("type","mrkdwn","text","*Casos de Teste:*\n" + totalCases),
                Map.of("type","mrkdwn","text","*Tempo Total Estimado de Teste Manual:*\n" + (totalEstimatedHuman == null ? "--" : totalEstimatedHuman)),
                Map.of("type","mrkdwn","text","*Tempo de Execução Automatizada:*\n" + (successTimeHuman == null ? "--" : successTimeHuman))
            )
        );

        Map<String, Object> divider = Map.of("type","divider");

        String failedMd = "*:x: Test Cases Falhados:*\n" + toCodeBlock(failedScenarios);
        String passedMd = "*:white_check_mark: Test Cases Sucesso:*\n" + toCodeBlock(passedScenarios);

        Map<String, Object> failedBlock = Map.of(
        "type","section",
        "text", Map.of("type","mrkdwn","text", failedMd)
        );

        Map<String, Object> passedBlock = Map.of(
            "type","section",
            "text", Map.of("type","mrkdwn","text", passedMd)
        );

        Map<String, Object> payload = new HashMap<>();
        payload.put("text", "Resumo de execução – Pix");
        payload.put("blocks", List.of(header, topFields, divider, failedBlock, passedBlock));
        return payload;
    }

    public static Map<String, Object> buildSummaryCard(
            int totalCases,
            String totalEstimatedHuman,
            String successTimeHuman,
            List<String> failedScenarios,
            List<String> passedScenarios) {
        return buildSummaryCard(
            ":rocket: Testes Pix",
            totalCases, totalEstimatedHuman, successTimeHuman, failedScenarios, passedScenarios
        );
    }
}
