
package CitiKaratTestPractice;

import java.util.*;
import java.util.stream.Collectors;

public class AccountActivityMonitorPractice {

    enum Status {
        COMPLETED, FAILED, PROCESSING
    }

    static class Activity {
        private final String activityId;
        private final String account;
        private final String region;
        private final Status status;
        private final int retryCount;
        private final long amountCents;

        Activity(String activityId, String account, String region,
                 Status status, int retryCount, long amountCents) {
            this.activityId = activityId;
            this.account = account;
            this.region = region;
            this.status = status;
            this.retryCount = retryCount;
            this.amountCents = amountCents;
        }

        public String getActivityId() { return activityId; }
        public String getAccount() { return account; }
        public String getRegion() { return region; }
        public Status getStatus() { return status; }
        public int getRetryCount() { return retryCount; }
        public long getAmountCents() { return amountCents; }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Activity)) return false;
            Activity other = (Activity) obj;
            return Objects.equals(activityId, other.activityId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(activityId);
        }
    }

    private final List<Activity> activities = new ArrayList<>();

    public AccountActivityMonitorPractice() {
        activities.add(new Activity("A101", "ACC_A", "NORTH", Status.COMPLETED, 0, 4000));
        activities.add(new Activity("A102", "ACC_A", "NORTH", Status.FAILED, 2, 1000));
        activities.add(new Activity("A103", "ACC_B", "SOUTH", Status.COMPLETED, 1, 6000));
        activities.add(new Activity("A104", "ACC_A", "SOUTH", Status.COMPLETED, 1, 3000));
        activities.add(new Activity("A105", "ACC_B", "SOUTH", Status.FAILED, 3, 2000));
        activities.add(new Activity("A106", "ACC_C", "NORTH", Status.COMPLETED, 0, 2500));
        activities.add(new Activity("A107", "ACC_C", "WEST", Status.PROCESSING, 1, 9000));
        activities.add(new Activity("A108", "ACC_B", "SOUTH", Status.COMPLETED, 2, 3500));
        activities.add(new Activity("A109", "ACC_A", "NORTH", Status.COMPLETED, 1, 5000));
        activities.add(new Activity("A110", "ACC_C", "WEST", Status.FAILED, 2, 1000));
        activities.add(new Activity("A111", "ACC_B", "NORTH", Status.COMPLETED, 0, 7000));
        activities.add(new Activity("A112", "ACC_A", "WEST", Status.FAILED, 2, 2500));
    }

    // PART A — BUG FIX
    // Return the total amount of COMPLETED activities for an account.

    public long getCompletedAmountByAccount(String account) {
        long total = 0;

        for (Activity activity : activities) {
            if (activity.getAccount().equals(account)
                    && activity.getStatus() == Status.COMPLETED) {
                total += activity.getAmountCents();
            }
        }

        return total;
    }

    // PART B — NEW FEATURE
    // Return distinct accounts with FAILED activities in a region
    // where retryCount >= minRetries.
    // Return account names in alphabetical order.

    public List<String> getHighRetryFailedAccounts(
            String region, int minRetries) {
        // TODO: Implement Part B
        return activities.stream().filter(ac->Objects.equals(ac.getRegion(),region)
                && ac.getStatus()==Status.FAILED && ac.getRetryCount()>=minRetries).map(ac->ac.getAccount())
                .distinct().sorted(Comparator.comparing(s->s))
                .collect(Collectors.toList());
    }

    // PART C — ADVANCED FEATURE
    // Sum COMPLETED activity amounts per account in the given region.
    // Include only accounts whose AGGREGATED total >= minTotalCents.

    public Map<String, Long> getCompletedTotalsByAccountInRegion(
            String region, long minTotalCents) {
        // TODO: Implement Part C
        return activities.stream().filter(ac->ac.getStatus()==Status.COMPLETED
                && Objects.equals(ac.getRegion(),region))
                .collect(Collectors.groupingBy((Activity a)->a.getAccount(),Collectors.summingLong(e->e.getAmountCents())))
                .entrySet().stream().filter(h->h.getValue()>=minTotalCents)
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
        AccountActivityMonitorPractice monitor =
                new AccountActivityMonitorPractice();

        System.out.println("===== PART A =====");

        check("A1: ACC_A completed total",
                monitor.getCompletedAmountByAccount("ACC_A") == 12000L);
        check("A2: ACC_B completed total",
                monitor.getCompletedAmountByAccount("ACC_B") == 16500L);
        check("A3: ACC_C completed total",
                monitor.getCompletedAmountByAccount("ACC_C") == 2500L);
        check("A4: Unknown account has zero",
                monitor.getCompletedAmountByAccount("UNKNOWN") == 0L);

        System.out.println("\n===== PART B =====");

        check("B1: NORTH failed accounts with >= 2 retries",
                monitor.getHighRetryFailedAccounts("NORTH", 2)
                        .equals(List.of("ACC_A")));
        check("B2: SOUTH failed accounts with >= 2 retries",
                monitor.getHighRetryFailedAccounts("SOUTH", 2)
                        .equals(List.of("ACC_B")));
        check("B3: No NORTH failed account has >= 3 retries",
                monitor.getHighRetryFailedAccounts("NORTH", 3).isEmpty());

        System.out.println("\n===== PART C =====");

        Map<String, Long> expectedC1 = new HashMap<>();
        expectedC1.put("ACC_A", 9000L);
        expectedC1.put("ACC_B", 7000L);

        check("C1: NORTH completed totals >= 7000 cents",
                monitor.getCompletedTotalsByAccountInRegion("NORTH", 7000L)
                        .equals(expectedC1));

        Map<String, Long> expectedC2 = new HashMap<>();
        expectedC2.put("ACC_A", 9000L);

        check("C2: NORTH completed totals >= 9000 cents",
                monitor.getCompletedTotalsByAccountInRegion("NORTH", 9000L)
                        .equals(expectedC2));

        check("C3: No WEST completed account total reaches 1000 cents",
                monitor.getCompletedTotalsByAccountInRegion("WEST", 1000L)
                        .isEmpty());

        System.out.println("\n===== RESULTS =====");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println("Total: " + (passed + failed));
    }
}
