package utils;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class RunMetrics {

    private static final List<String> PASSED = Collections.synchronizedList(new ArrayList<>());
    private static final List<String> FAILED = Collections.synchronizedList(new ArrayList<>());
    private static final AtomicLong SUCCESS_TIME_MS = new AtomicLong(0);
    private static volatile String TOTAL_ESTIMATED_HUMAN = "--";

    public static void reset() {
        PASSED.clear();
        FAILED.clear();
        SUCCESS_TIME_MS.set(0);
        TOTAL_ESTIMATED_HUMAN = "--";
    }

    public static void addPassed(String scenarioName, long durationMs) {
        if (scenarioName != null) PASSED.add(scenarioName);
        SUCCESS_TIME_MS.addAndGet(durationMs);
    }

    public static void addFailed(String scenarioName) {
        if (scenarioName != null) FAILED.add(scenarioName);
    }

    public static List<String> getPassed() { return new ArrayList<>(PASSED); }
    public static List<String> getFailed() { return new ArrayList<>(FAILED); }
    public static int getTotalExecuted() { return PASSED.size() + FAILED.size(); }
    public static long getSuccessTimeMs() { return SUCCESS_TIME_MS.get(); }

    public static void setTotalEstimatedHuman(String human) { TOTAL_ESTIMATED_HUMAN = human; }
    public static String getTotalEstimatedHuman() { return TOTAL_ESTIMATED_HUMAN; }

    /** arredonda para o segundo mais próximo */
    public static String fmtDur(long ms) {
        long seconds = Math.round(ms / 1000.0);
        long h = seconds / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;
        return String.format("%02dh%02dm%02ds", h, m, s);
    }

    /** estimativa de esforço manual (minutos por cenário configurável) */
    public static String calcularTempoEstimado(int totalCenarios, int minutosPorCenario) {
        if (minutosPorCenario <= 0) minutosPorCenario = 2;
        int totalSegundos = totalCenarios * minutosPorCenario * 60;
        long h = totalSegundos / 3600;
        long m = (totalSegundos % 3600) / 60;
        long s = totalSegundos % 60;
        return String.format("%02dh%02dm%02ds", h, m, s);
    }
}
