package utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class LogUtil {

    private static PrintWriter writer;
    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd_HH-mm-ss";
    private static final String LINE_FORMAT      = "HH:mm:ss";

    // counters — atomic for thread safety
    private static final AtomicInteger apiPassed  = new AtomicInteger(0);
    private static final AtomicInteger apiFailed  = new AtomicInteger(0);
    private static final AtomicInteger uiPassed   = new AtomicInteger(0);
    private static final AtomicInteger uiFailed   = new AtomicInteger(0);

    private LogUtil() {}

    public static void init() {
        try {
            String dir = ConfigManager.getInstance().getLogDir();
            Files.createDirectories(Paths.get(dir));

            String fileName = dir + "test-execution-"
                    + LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT))
                    + ".log";

            writer = new PrintWriter(new FileWriter(fileName, true));

            log("═".repeat(70));
            log("  MissionQA Test Execution Log");
            log("  Started : " + LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            log("  Browser : " + ConfigManager.getInstance().getBrowser());
            log("  Java    : " + System.getProperty("java.version"));
            log("  OS      : " + System.getProperty("os.name"));
            log("═".repeat(70));
            writer.flush();

        } catch (IOException e) {
            System.err.println("LogUtil init failed: " + e.getMessage());
        }
    }

    public static void log(String message) {
        if (writer == null) return;
        writer.println(timestamp() + " " + message);
        writer.flush();
    }

    public static void scenarioStart(String name, String tags) {
        log("");
        log("┌─ SCENARIO: " + name);
        log("│  Tags    : " + tags);
        log("│  Started : " + timestamp());
    }

    // tags param used to increment correct counter
    public static void scenarioEnd(String name,
                                   String status,
                                   long durationMs,
                                   String tags) {
        log("│  Status  : " + status);
        log("│  Duration: " + durationMs + "ms");
        log("└─ END: " + name);

        boolean isApi = tags.contains("@api");

        if (isApi) {
            if (status.equalsIgnoreCase("PASSED")) apiPassed.incrementAndGet();
            else                                   apiFailed.incrementAndGet();
        } else {
            if (status.equalsIgnoreCase("PASSED")) uiPassed.incrementAndGet();
            else                                   uiFailed.incrementAndGet();
        }
    }

    public static void screenshot(String path) {
        log("│  Screenshot: " + path);
    }

    public static void close() {
        if (writer == null) return;

        // ── Summary ──────────────────────────────────────────
        log("");
        log("═".repeat(70));
        log("  RESULTS SUMMARY");
        log("  ─────────────────────────────────────────────");
        log("  API  —  Passed: " + apiPassed.get()
                + "   Failed: " + apiFailed.get()
                + "   Total: " + (apiPassed.get() + apiFailed.get()));
        log("  UI   —  Passed: " + uiPassed.get()
                + "   Failed: " + uiFailed.get()
                + "   Total: " + (uiPassed.get() + uiFailed.get()));
        log("  ─────────────────────────────────────────────");
        log("  ALL  —  Passed: " + (apiPassed.get() + uiPassed.get())
                + "   Failed: " + (apiFailed.get() + uiFailed.get())
                + "   Total: " + (apiPassed.get() + apiFailed.get()
                + uiPassed.get()  + uiFailed.get()));
        log("═".repeat(70));
        log("  Execution Completed: "
                + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        log("═".repeat(70));
        writer.flush();
        writer.close();
    }

    private static String timestamp() {
        return "[" + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern(LINE_FORMAT)) + "]";
    }
}