package CitiKaratTestPractice;

import java.util.*;
import java.util.stream.Collectors;

public class BackupJobMonitorPractice {

    enum Status {
        SUCCESS, FAILED, PENDING
    }

    static class BackupJob {
        private final String jobId;
        private final String owner;
        private final String region;
        private final Status status;
        private final int retryCount;
        private final long sizeBytes;

        BackupJob(String jobId, String owner, String region,
                  Status status, int retryCount, long sizeBytes) {
            this.jobId = jobId;
            this.owner = owner;
            this.region = region;
            this.status = status;
            this.retryCount = retryCount;
            this.sizeBytes = sizeBytes;
        }

        public String getJobId() { return jobId; }
        public String getOwner() { return owner; }
        public String getRegion() { return region; }
        public Status getStatus() { return status; }
        public int getRetryCount() { return retryCount; }
        public long getSizeBytes() { return sizeBytes; }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof BackupJob)) return false;
            BackupJob other = (BackupJob) obj;
            return Objects.equals(jobId, other.jobId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(jobId);
        }
    }

    private final List<BackupJob> jobs = new ArrayList<>();

    public BackupJobMonitorPractice() {
        jobs.add(new BackupJob("J101", "ALICE", "EAST", Status.SUCCESS, 0, 4000));
        jobs.add(new BackupJob("J102", "ALICE", "EAST", Status.FAILED, 2, 1000));
        jobs.add(new BackupJob("J103", "BOB", "WEST", Status.SUCCESS, 1, 6000));
        jobs.add(new BackupJob("J104", "ALICE", "WEST", Status.SUCCESS, 1, 3000));
        jobs.add(new BackupJob("J105", "BOB", "WEST", Status.FAILED, 3, 2000));
        jobs.add(new BackupJob("J106", "CAROL", "EAST", Status.SUCCESS, 0, 2500));
        jobs.add(new BackupJob("J107", "CAROL", "NORTH", Status.PENDING, 1, 9000));
        jobs.add(new BackupJob("J108", "BOB", "WEST", Status.SUCCESS, 2, 3500));
        jobs.add(new BackupJob("J109", "ALICE", "EAST", Status.SUCCESS, 1, 5000));
        jobs.add(new BackupJob("J110", "CAROL", "NORTH", Status.FAILED, 2, 1000));
        jobs.add(new BackupJob("J111", "BOB", "EAST", Status.SUCCESS, 0, 7000));
        jobs.add(new BackupJob("J112", "ALICE", "NORTH", Status.FAILED, 2, 2500));
    }

    // PART A — BUG FIX
    // Return total size of SUCCESS jobs for the specified owner.
    public long getSuccessfulSizeByOwner(String owner) {
        long total = 0;

        for (BackupJob job : jobs) {
            // BUG: This condition incorrectly includes PENDING jobs.
            if (job.getOwner().equals(owner)
                    && job.getStatus() == Status.SUCCESS) {
                total += job.getSizeBytes();
            }
        }

        return total;
    }

    // PART B — NEW FEATURE
    // Return distinct owners with FAILED jobs in the given region
    // where retryCount >= minRetries.
    // Sort owner names alphabetically.
    public List<String> getHighRetryFailedOwners(
            String region, int minRetries) {

        // TODO: Implement Part B.
        return jobs.stream().filter(jb->Objects.equals(jb.getRegion(),region)
                && jb.getStatus()==Status.FAILED && jb.getRetryCount() >=minRetries)
                .map(m->m.getOwner()).sorted(Comparator.comparing(s->s))
                .collect(Collectors.toList());
    }

    // PART C — ADVANCED FEATURE
    // Sum SUCCESS job sizes per owner in the given region.
    // Include only owners whose aggregated total >= minTotalBytes.
    public Map<String, Long> getSuccessfulTotalsByOwnerInRegion(
            String region, long minTotalBytes) {

        // TODO: Implement Part C.
        return jobs.stream().filter(jb->Objects.equals(jb.getRegion(),region)
                && jb.getStatus()==Status.SUCCESS)
                .collect(Collectors.groupingBy(BackupJob::getOwner,
                        Collectors.summingLong(jb->jb.getSizeBytes())))
                .entrySet().stream().filter(b->b.getValue()>=minTotalBytes)
                .collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue));
    }

    private static int passed = 0;
    private static int failed = 0;

    private static void check(String name, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("[PASS] " + name);
        } else {
            failed++;
            System.out.println("[FAIL] " + name);
        }
    }

    public static void main(String[] args) {
        BackupJobMonitorPractice monitor =
                new BackupJobMonitorPractice();

        System.out.println("===== PART A =====");

        check("A1: ALICE successful size",
                monitor.getSuccessfulSizeByOwner("ALICE") == 12000L);

        check("A2: BOB successful size",
                monitor.getSuccessfulSizeByOwner("BOB") == 16500L);

        check("A3: CAROL successful size",
                monitor.getSuccessfulSizeByOwner("CAROL") == 2500L);

        check("A4: Unknown owner returns zero",
                monitor.getSuccessfulSizeByOwner("UNKNOWN") == 0L);

        System.out.println("\n===== PART B =====");

        check("B1: EAST failed owners with >= 2 retries",
                monitor.getHighRetryFailedOwners("EAST", 2)
                        .equals(List.of("ALICE")));

        check("B2: WEST failed owners with >= 2 retries",
                monitor.getHighRetryFailedOwners("WEST", 2)
                        .equals(List.of("BOB")));

        check("B3: NORTH has no failed owner with >= 3 retries",
                monitor.getHighRetryFailedOwners("NORTH", 3).isEmpty());

        System.out.println("\n===== PART C =====");

        Map<String, Long> expectedC1 = new HashMap<>();
        expectedC1.put("ALICE", 9000L);
        expectedC1.put("BOB", 7000L);

        check("C1: EAST successful totals >= 7000 bytes",
                monitor.getSuccessfulTotalsByOwnerInRegion("EAST", 7000L)
                        .equals(expectedC1));

        Map<String, Long> expectedC2 = new HashMap<>();
        expectedC2.put("ALICE", 9000L);

        check("C2: EAST successful totals >= 9000 bytes",
                monitor.getSuccessfulTotalsByOwnerInRegion("EAST", 9000L)
                        .equals(expectedC2));

        check("C3: NORTH has no successful total >= 1000 bytes",
                monitor.getSuccessfulTotalsByOwnerInRegion("NORTH", 1000L)
                        .isEmpty());

        System.out.println("\n===== RESULTS =====");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println("Total: " + (passed + failed));
    }
}
