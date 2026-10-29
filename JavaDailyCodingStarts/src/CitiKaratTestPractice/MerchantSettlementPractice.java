
package CitiKaratTestPractice;

import java.util.*;
import java.util.stream.Collectors;

public class MerchantSettlementPractice {

    enum Status {
        SUCCESS, FAILED, PENDING
    }

    static class Settlement {
        private final String settlementId;
        private final String merchant;
        private final String region;
        private final Status status;
        private final int retryCount;
        private final long amountCents;

        Settlement(String settlementId, String merchant, String region,
                   Status status, int retryCount, long amountCents) {
            this.settlementId = settlementId;
            this.merchant = merchant;
            this.region = region;
            this.status = status;
            this.retryCount = retryCount;
            this.amountCents = amountCents;
        }

        public String getSettlementId() { return settlementId; }
        public String getMerchant() { return merchant; }
        public String getRegion() { return region; }
        public Status getStatus() { return status; }
        public int getRetryCount() { return retryCount; }
        public long getAmountCents() { return amountCents; }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Settlement)) return false;
            Settlement other = (Settlement) obj;
            return Objects.equals(settlementId, other.settlementId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(settlementId);
        }
    }

    private final List<Settlement> settlements = new ArrayList<>();

    public MerchantSettlementPractice() {
        settlements.add(new Settlement("D101", "ALPHA", "NORTH", Status.SUCCESS, 0, 4000));
        settlements.add(new Settlement("D102", "ALPHA", "NORTH", Status.FAILED, 2, 1500));
        settlements.add(new Settlement("D103", "BETA", "SOUTH", Status.SUCCESS, 1, 6000));
        settlements.add(new Settlement("D104", "ALPHA", "SOUTH", Status.SUCCESS, 2, 3000));
        settlements.add(new Settlement("D105", "BETA", "SOUTH", Status.FAILED, 3, 2000));
        settlements.add(new Settlement("D106", "GAMMA", "NORTH", Status.SUCCESS, 1, 2500));
        settlements.add(new Settlement("D107", "GAMMA", "WEST", Status.PENDING, 0, 9000));
        settlements.add(new Settlement("D108", "BETA", "SOUTH", Status.SUCCESS, 2, 3500));
        settlements.add(new Settlement("D109", "ALPHA", "NORTH", Status.SUCCESS, 1, 5000));
        settlements.add(new Settlement("D110", "GAMMA", "WEST", Status.FAILED, 2, 1000));
        settlements.add(new Settlement("D111", "BETA", "NORTH", Status.SUCCESS, 0, 7000));
        settlements.add(new Settlement("D112", "ALPHA", "WEST", Status.FAILED, 2, 2500));
    }

    // PART A — BUG FIX
    // Return the total amount of SUCCESS settlements for a merchant.

    public long getSuccessfulAmountByMerchant(String merchant) {
        long total = 0;

        for (Settlement settlement : settlements) {
            if (settlement.getMerchant().equals(merchant)
                    && settlement.getStatus() == Status.SUCCESS) {
                total += settlement.getAmountCents();
            }
        }

        return total;
    }

    // PART B — NEW FEATURE
    // Return distinct merchants with FAILED settlements in a region
    // whose retryCount is >= minRetries.
    // Return the merchant names in alphabetical order.

    public List<String> getHighRetryFailedMerchants(
            String region, int minRetries) {
        // TODO: Implement Part B
        return settlements.stream().filter(st->st.getStatus()==Status.FAILED
                && Objects.equals(st.getRegion(),region) && st.getRetryCount()>=minRetries)
                .map(st->st.getMerchant()).sorted(Comparator.comparing(st->st))
                .collect(Collectors.toList());
    }

    // PART C — ADVANCED FEATURE
    // Sum SUCCESS settlement amounts per merchant in the requested region.
    // Include only merchants whose AGGREGATED amount >= minTotalCents.

    public Map<String, Long> getSuccessfulTotalsByMerchantInRegion(
            String region, long minTotalCents) {
        // TODO: Implement Part C
        return settlements.stream().filter(st-> Objects.equals(region,st.getRegion())
        && st.getStatus()==Status.SUCCESS)
                .collect(Collectors.groupingBy((Settlement s)->s.getMerchant()
                        ,Collectors.summingLong(st->st.getAmountCents())))
                .entrySet().stream().filter(st->st.getValue()>=minTotalCents)
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
        MerchantSettlementPractice monitor = new MerchantSettlementPractice();

        System.out.println("===== PART A =====");

        check("A1: ALPHA successful total",
                monitor.getSuccessfulAmountByMerchant("ALPHA") == 12000L);
        check("A2: BETA successful total",
                monitor.getSuccessfulAmountByMerchant("BETA") == 16500L);
        check("A3: GAMMA successful total",
                monitor.getSuccessfulAmountByMerchant("GAMMA") == 2500L);
        check("A4: Unknown merchant has zero",
                monitor.getSuccessfulAmountByMerchant("UNKNOWN") == 0L);

        System.out.println("\n===== PART B =====");

        check("B1: NORTH failed merchants with >= 2 retries",
                monitor.getHighRetryFailedMerchants("NORTH", 2)
                        .equals(List.of("ALPHA")));
        check("B2: SOUTH failed merchants with >= 2 retries",
                monitor.getHighRetryFailedMerchants("SOUTH", 2)
                        .equals(List.of("BETA")));
        check("B3: No NORTH failed merchant has >= 3 retries",
                monitor.getHighRetryFailedMerchants("NORTH", 3).isEmpty());

        System.out.println("\n===== PART C =====");

        Map<String, Long> expectedC1 = new HashMap<>();
        expectedC1.put("ALPHA", 9000L);
        expectedC1.put("BETA", 7000L);

        check("C1: NORTH successful totals >= 7000 cents",
                monitor.getSuccessfulTotalsByMerchantInRegion("NORTH", 7000L)
                        .equals(expectedC1));

        Map<String, Long> expectedC2 = new HashMap<>();
        expectedC2.put("ALPHA", 9000L);

        check("C2: NORTH successful totals >= 9000 cents",
                monitor.getSuccessfulTotalsByMerchantInRegion("NORTH", 9000L)
                        .equals(expectedC2));

        check("C3: No WEST successful merchant total reaches 1000 cents",
                monitor.getSuccessfulTotalsByMerchantInRegion("WEST", 1000L)
                        .isEmpty());

        System.out.println("\n===== RESULTS =====");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println("Total: " + (passed + failed));
    }
}
