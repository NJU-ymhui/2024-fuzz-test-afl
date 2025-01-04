package cn.edu.nju.modules.monitor;

import cn.edu.nju.util.Log;
import cn.edu.nju.util.manager.ResourcesManager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * MonitorImpl is responsible for monitoring the execution results of the fuzzing process.
 * It logs coverage data, execution speed, and saves special test cases.
 */
public class MonitorImpl implements Monitor {
    private ResourcesManager resourcesManager;
    private BufferedWriter logWriter;
    private long startTime;
    private long endTime;

    @Override
    public void register(ResourcesManager resourcesManager) {
        this.resourcesManager = resourcesManager;
    }

    @Override
    public void setUp() {
        try {
            String logFilePath = resourcesManager.getLogPath();
            logWriter = new BufferedWriter(new FileWriter(logFilePath, true));
            startTime = System.currentTimeMillis();
            log("Monitoring started at " + LocalDateTime.now());
        } catch (IOException e) {
            Log.error("Failed to initialize log writer: " + e.getMessage());
        }
    }

    @Override
    public void tearDown() {
        endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        log("Monitoring ended at " + LocalDateTime.now());
        log("Total execution time: " + duration + " ms");
        logCoverageData();
        closeLogWriter();
    }

    /**
     * Logs a message to the log file.
     *
     * @param message The message to log.
     */
    private void log(String message) {
        try {
            if (logWriter != null) {
                logWriter.write(message);
                logWriter.newLine();
                logWriter.flush();
            }
            Log.info(message);
        } catch (IOException e) {
            Log.error("Failed to write to log: " + e.getMessage());
        }
    }

    /**
     * Logs coverage data and execution speed.
     */
    private void logCoverageData() {
        try {
            log("=== Coverage Data ===");
            resourcesManager.getCoverageData().forEach((seedPath, coverage) -> {
                log("Seed: " + seedPath + " | Coverage: " + coverage);
            });

            // Calculate execution speed (executions per second)
            // Assuming ResourcesManager tracks execution counts and time
            // For demonstration, we'll use the duration from setUp to tearDown
            long durationSeconds = (endTime - startTime) / 1000;
            if (durationSeconds == 0) durationSeconds = 1; // Avoid division by zero
            int totalExecutions = resourcesManager.getCoverageData().size(); // Placeholder
            double executionsPerSecond = (double) totalExecutions / durationSeconds;
            log("Execution Speed: " + executionsPerSecond + " executions/second");

            // Save special test cases if any
            if (!resourcesManager.getVulnerabilities().isEmpty()) {
                log("=== Vulnerabilities Detected ===");
                resourcesManager.getVulnerabilities().forEach(vuln -> {
                    log("Vulnerability: " + vuln);
                });
            }
        } catch (Exception e) {
            Log.error("Failed to log coverage data: " + e.getMessage());
        }
    }

    /**
     * Closes the log writer safely.
     */
    private void closeLogWriter() {
        try {
            if (logWriter != null) {
                logWriter.close();
            }
        } catch (IOException e) {
            Log.error("Failed to close log writer: " + e.getMessage());
        }
    }
}
