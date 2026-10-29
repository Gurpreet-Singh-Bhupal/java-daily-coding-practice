
package CitiKaratTestPractice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.*;

public class BatchJobMonitorPractice {

    enum JobStatus {
        SUCCESS, FAILED, RUNNING
    }

    static class JobRecord {
        private final String jobId;
        private final String team;
        private final String jobType;
        private final JobStatus status;
        private final int retryCount;
        private final long durationMs;

        JobRecord(
                String jobId,
                String team,
                String jobType,
                JobStatus status,
                int retryCount,
                long durationMs
        ) {
            this.jobId = jobId;
            this.team = team;
            this.jobType = jobType;
            this.status = status;
            this.retryCount = retryCount;
            this.durationMs = durationMs;
        }

        public String getJobId() {
            return jobId;
        }

        public String getTeam() {
            return team;
        }

        public String getJobType() {
            return jobType;
        }

        public JobStatus getStatus() {
            return status;
        }

        public int getRetryCount() {
            return retryCount;
        }

        public long getDurationMs() {
            return durationMs;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof JobRecord)) return false;

            JobRecord other = (JobRecord) obj;
            return Objects.equals(jobId, other.jobId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(jobId);
        }
    }

    private final List<JobRecord> records = new ArrayList<>();

    public BatchJobMonitorPractice() {
        records.add(new JobRecord(
                "J101", "PAYMENTS", "BATCH", JobStatus.FAILED, 3, 1200));
        records.add(new JobRecord(
                "J102", "PAYMENTS", "ETL", JobStatus.FAILED, 2, 900));
        records.add(new JobRecord(
                "J103", "PAYMENTS", "BATCH", JobStatus.SUCCESS, 1, 700));
        records.add(new JobRecord(
                "J104", "RISK", "ETL", JobStatus.FAILED, 4, 1800));
        records.add(new JobRecord(
                "J105", "RISK", "BATCH", JobStatus.SUCCESS, 0, 600));
        records.add(new JobRecord(
                "J106", "RISK", "ETL", JobStatus.FAILED, 1, 1500));
        records.add(new JobRecord(
                "J107", "REPORTING", "BATCH", JobStatus.RUNNING, 0, 300));
        records.add(new JobRecord(
                "J108", "REPORTING", "ETL", JobStatus.FAILED, 2, 1100));
        records.add(new JobRecord(
                "J109", "PAYMENTS", "ETL", JobStatus.SUCCESS, 0, 500));
        records.add(new JobRecord(
                "J110", "REPORTING", "BATCH", JobStatus.FAILED, 0, 800));
    }

    // =========================================================
    // PART A — BUG FIX
    // Count failed jobs for a team whose retry count meets
    // the supplied minimum retry threshold.
    // =========================================================

    public int countFailedJobsAtRetryThreshold(
            String team,
            int minRetries
    ) {
        int count = 0;

        for (JobRecord record : records) {
            if (record.getTeam().equals(team)
                    && record.getStatus() == JobStatus.FAILED
                    && record.getRetryCount() >= minRetries) {

                count++;
            }
        }

        return count;
    }

    // =========================================================
    // PART B — NEW FEATURE
    // Return the IDs of successful jobs whose duration is
    // greater than or equal to minDurationMs.
    // Preserve the original record order.
    // Do not modify the original records list.
    // =========================================================

    public List<String> getSuccessfulJobIdsOverDuration(
            long minDurationMs
    ) {
        // TODO: Implement Part B
        return records.stream().filter(rec->rec.getStatus()==JobStatus.SUCCESS
                        && rec.getDurationMs() >= minDurationMs).map(rec->rec.getJobId())
                .collect(Collectors.toList());
    }

    // =========================================================
    // PART C — ADVANCED FEATURE
    // Return total retry counts grouped by team, considering
    // FAILED jobs only.
    //
    // Include a team if it has at least one FAILED job,
    // even if that team's total retries are zero.
    // =========================================================

    public Map<String, Integer> getFailedRetryTotalsByTeam() {
        // TODO: Implement Part C
        return records.stream().filter(rec->rec.getStatus()==JobStatus.FAILED)
                .collect(Collectors.groupingBy(rec->rec.getTeam()
                        ,Collectors.summingInt(rec->rec.getRetryCount())));
    }

    // =========================================================
    // TEST HARNESS
    // =========================================================

    private static int passed = 0;
    private static int failed = 0;

    private static void check(
            String testName,
            boolean condition
    ) {
        if (condition) {
            passed++;
            System.out.println("[PASS] " + testName);
        } else {
            failed++;
            System.out.println("[FAIL] " + testName);
        }
    }

    public static void main(String[] args) {
        BatchJobMonitorPractice monitor =
                new BatchJobMonitorPractice();

        System.out.println("===== PART A: BUG FIX =====");

        check(
                "A1: PAYMENTS failed jobs with at least 2 retries",
                monitor.countFailedJobsAtRetryThreshold(
                        "PAYMENTS", 2) == 2
        );

        check(
                "A2: RISK failed jobs with at least 1 retry",
                monitor.countFailedJobsAtRetryThreshold(
                        "RISK", 1) == 2
        );

        check(
                "A3: REPORTING failed jobs with at least 0 retries",
                monitor.countFailedJobsAtRetryThreshold(
                        "REPORTING", 0) == 2
        );

        check(
                "A4: Unknown team has no failed jobs",
                monitor.countFailedJobsAtRetryThreshold(
                        "UNKNOWN", 0) == 0
        );

        System.out.println("\n===== PART B: NEW FEATURE =====");

        List<String> expectedB1 = List.of("J103", "J105");
        check(
                "B1: Successful jobs with duration >= 600 ms",
                monitor.getSuccessfulJobIdsOverDuration(600)
                        .equals(expectedB1)
        );

        List<String> expectedB2 = List.of("J103", "J105", "J109");
        check(
                "B2: Successful jobs with duration >= 500 ms",
                monitor.getSuccessfulJobIdsOverDuration(500)
                        .equals(expectedB2)
        );

        check(
                "B3: No successful jobs meet 2000 ms threshold",
                monitor.getSuccessfulJobIdsOverDuration(2000).isEmpty()
        );

        System.out.println("\n===== PART C: ADVANCED FEATURE =====");

        Map<String, Integer> expectedC1 = new HashMap<>();
        expectedC1.put("PAYMENTS", 5);
        expectedC1.put("RISK", 5);
        expectedC1.put("REPORTING", 2);

        check(
                "C1: Failed retry totals grouped by team",
                monitor.getFailedRetryTotalsByTeam().equals(expectedC1)
        );

        check(
                "C2: All three teams with failed jobs are included",
                monitor.getFailedRetryTotalsByTeam().size() == 3
        );

        check(
                "C3: REPORTING includes the failed job with zero retries",
                monitor.getFailedRetryTotalsByTeam()
                        .getOrDefault("REPORTING", -1) == 2
        );

        System.out.println("\n===== RESULTS =====");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println("Total:  " + (passed + failed));
    }
}
