
package CitiKaratTestPractice;

import java.util.*;
import java.util.stream.Collectors;

public class TransactionSettlementPractice {

    enum Status {
        SETTLED, FAILED, PENDING
    }

    static class Transaction {
        private final String transactionId;
        private final String merchant;
        private final String region;
        private final Status status;
        private final int retryCount;
        private final long amountCents;

        Transaction(String transactionId, String merchant,
                    String region, Status status,
                    int retryCount, long amountCents) {
            this.transactionId = transactionId;
            this.merchant = merchant;
            this.region = region;
            this.status = status;
            this.retryCount = retryCount;
            this.amountCents = amountCents;
        }

        public String getTransactionId() { return transactionId; }
        public String getMerchant() { return merchant; }
        public String getRegion() { return region; }
        public Status getStatus() { return status; }
        public int getRetryCount() { return retryCount; }
        public long getAmountCents() { return amountCents; }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Transaction)) return false;
            Transaction other = (Transaction) obj;
            return Objects.equals(transactionId, other.transactionId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(transactionId);
        }
    }

    private final List<Transaction> transactions = new ArrayList<>();

    public TransactionSettlementPractice() {
        transactions.add(new Transaction("T101", "M_A", "APAC", Status.SETTLED, 1, 5000));
        transactions.add(new Transaction("T102", "M_A", "APAC", Status.FAILED, 3, 3000));
        transactions.add(new Transaction("T103", "M_B", "EU", Status.SETTLED, 0, 7000));
        transactions.add(new Transaction("T104", "M_A", "US", Status.SETTLED, 2, 8000));
        transactions.add(new Transaction("T105", "M_B", "EU", Status.FAILED, 2, 4000));
        transactions.add(new Transaction("T106", "M_C", "APAC", Status.SETTLED, 4, 2500));
        transactions.add(new Transaction("T107", "M_C", "US", Status.PENDING, 1, 9000));
        transactions.add(new Transaction("T108", "M_B", "EU", Status.SETTLED, 2, 4500));
        transactions.add(new Transaction("T109", "M_A", "APAC", Status.SETTLED, 0, 6000));
        transactions.add(new Transaction("T110", "M_C", "US", Status.FAILED, 0, 2000));
        transactions.add(new Transaction("T111", "M_B", "APAC", Status.SETTLED, 1, 10000));
        transactions.add(new Transaction("T112", "M_A", "EU", Status.FAILED, 2, 5500));
    }

    // =====================================================
    // PART A — BUG FIX
    // Return total amount of SETTLED transactions for a merchant.
    // =====================================================

    public long getSettledAmountByMerchant(String merchant) {
        long total = 0;

        for (Transaction transaction : transactions) {
            if (transaction.getMerchant().equals(merchant)
                    && transaction.getStatus() == Status.SETTLED) {
                total += transaction.getAmountCents();
            }
        }

        return total;
    }

    // =====================================================
    // PART B — NEW FEATURE
    // Return distinct merchants that have FAILED transactions
    // in the requested region with retryCount >= minRetries.
    // Return merchant names in alphabetical order.
    // =====================================================

    public List<String> getHighRetryFailedMerchants(
            String region, int minRetries) {
        // TODO: Implement Part B
    return transactions.stream().filter(tr->tr.getStatus()==Status.FAILED
            && Objects.equals(tr.getRegion(),region) && tr.getRetryCount()>=minRetries)
            .map(tr->tr.getMerchant())
            .distinct().sorted(Comparator.comparing(m->m)).collect(Collectors.toList());
    }

    // =====================================================
    // PART C — ADVANCED FEATURE
    // For the requested region, sum SETTLED transaction amounts
    // grouped by merchant.
    // Include only merchants whose AGGREGATED total >= minTotalCents.
    // =====================================================

    public Map<String, Long> getSettledTotalsByMerchantInRegion(
            String region, long minTotalCents) {
        // TODO: Implement Part C
        return transactions.stream().filter(tr->Objects.equals(tr.getRegion(),region)
                && tr.getStatus()==Status.SETTLED)
                .collect(Collectors.groupingBy((Transaction t)->t.getMerchant(),
                        Collectors.summingLong(tr->tr.getAmountCents())))
                .entrySet().stream().filter(tr->tr.getValue()>=minTotalCents)
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
        TransactionSettlementPractice monitor =
                new TransactionSettlementPractice();

        System.out.println("===== PART A =====");

        check("A1: M_A settled total",
                monitor.getSettledAmountByMerchant("M_A") == 19000L);
        check("A2: M_B settled total",
                monitor.getSettledAmountByMerchant("M_B") == 21500L);
        check("A3: M_C settled total",
                monitor.getSettledAmountByMerchant("M_C") == 2500L);
        check("A4: Unknown merchant has zero",
                monitor.getSettledAmountByMerchant("UNKNOWN") == 0L);

        System.out.println("\n===== PART B ===== "+monitor.getHighRetryFailedMerchants("EU", 2));

        check("B1: APAC failed merchants with >= 2 retries",
                monitor.getHighRetryFailedMerchants("APAC", 2)
                        .equals(List.of("M_A")));
        check("B2: EU failed merchants with >= 2 retries",
                monitor.getHighRetryFailedMerchants("EU", 2)
                        .equals(List.of("M_A","M_B")));
        check("B3: No merchant qualifies in US",
                monitor.getHighRetryFailedMerchants("US", 1).isEmpty());

        System.out.println("\n===== PART C =====");

        Map<String, Long> expectedC1 = new HashMap<>();
        expectedC1.put("M_A", 11000L);
        expectedC1.put("M_B", 10000L);

        check("C1: APAC settled totals >= 5000 cents",
                monitor.getSettledTotalsByMerchantInRegion("APAC", 5000L)
                        .equals(expectedC1));

        Map<String, Long> expectedC2 = new HashMap<>();
        expectedC2.put("M_B", 10000L);
        expectedC2.put("M_A", 11000L);


        check("C2: APAC settled totals >= 10000 cents",
                monitor.getSettledTotalsByMerchantInRegion("APAC", 10000L)
                        .equals(expectedC2));

        check("C3: No merchant qualifies in US at 10000 cents",
                monitor.getSettledTotalsByMerchantInRegion("US", 10000L)
                        .isEmpty());

        System.out.println("\n===== RESULTS =====");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println("Total: " + (passed + failed));
    }
}
