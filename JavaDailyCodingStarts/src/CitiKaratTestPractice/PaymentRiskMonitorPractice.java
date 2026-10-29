
package CitiKaratTestPractice;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.*;

public class PaymentRiskMonitorPractice {

    enum Status {
        APPROVED, DECLINED, PENDING
    }

    static class Payment {
        private final String paymentId;
        private final String merchant;
        private final String region;
        private final Status status;
        private final int retryCount;
        private final long amountCents;

        Payment(String paymentId, String merchant, String region,
                Status status, int retryCount, long amountCents) {
            this.paymentId = paymentId;
            this.merchant = merchant;
            this.region = region;
            this.status = status;
            this.retryCount = retryCount;
            this.amountCents = amountCents;
        }

        public String getPaymentId() { return paymentId; }
        public String getMerchant() { return merchant; }
        public String getRegion() { return region; }
        public Status getStatus() { return status; }
        public int getRetryCount() { return retryCount; }
        public long getAmountCents() { return amountCents; }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Payment)) return false;
            Payment other = (Payment) obj;
            return Objects.equals(paymentId, other.paymentId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(paymentId);
        }
    }

    private final List<Payment> payments = new ArrayList<>();

    public PaymentRiskMonitorPractice() {
        payments.add(new Payment("P101", "SHOP_A", "APAC", Status.DECLINED, 3, 5000));
        payments.add(new Payment("P102", "SHOP_A", "APAC", Status.APPROVED, 2, 8000));
        payments.add(new Payment("P103", "SHOP_B", "EU", Status.DECLINED, 2, 3000));
        payments.add(new Payment("P104", "SHOP_A", "US", Status.DECLINED, 1, 7000));
        payments.add(new Payment("P105", "SHOP_B", "EU", Status.APPROVED, 0, 12000));
        payments.add(new Payment("P106", "SHOP_C", "APAC", Status.DECLINED, 4, 2500));
        payments.add(new Payment("P107", "SHOP_C", "US", Status.PENDING, 2, 9000));
        payments.add(new Payment("P108", "SHOP_B", "EU", Status.DECLINED, 3, 4500));
        payments.add(new Payment("P109", "SHOP_A", "APAC", Status.APPROVED, 1, 6000));
        payments.add(new Payment("P110", "SHOP_C", "US", Status.DECLINED, 0, 2000));
        payments.add(new Payment("P111", "SHOP_B", "APAC", Status.APPROVED, 1, 10000));
        payments.add(new Payment("P112", "SHOP_A", "EU", Status.DECLINED, 2, 5500));
    }

    // =====================================================
    // PART A — BUG FIX
    // Return the total amount of APPROVED payments for a
    // merchant. Amounts are stored in cents.
    // =====================================================

    public long getApprovedAmountByMerchant(String merchant) {
        long total = 0;

        for (Payment payment : payments) {
            if (payment.getMerchant().equals(merchant)
                    && payment.getStatus() == Status.APPROVED) {
                total += payment.getAmountCents();
            }
        }

        return total;
    }

    // =====================================================
    // PART B — NEW FEATURE
    // Return distinct regions containing DECLINED payments
    // whose retryCount is >= minRetries.
    // Return regions in alphabetical order.
    // =====================================================

    public List<String> getHighRetryDeclinedRegions(int minRetries) {
        // TODO: Implement Part B
        return payments.stream().filter(pay->pay.getStatus()==Status.DECLINED && pay.getRetryCount()>=minRetries)
                .sorted(Comparator.comparing((Payment::getRegion))).map(n->n.getRegion())
                .distinct().collect(Collectors.toList());
    }

    // =====================================================
    // PART C — ADVANCED FEATURE
    // For each merchant, calculate the TOTAL AMOUNT in cents
    // of APPROVED payments in a specified region.
    //
    // Include only merchants whose qualifying approved
    // amount is >= minAmountCents.
    // Return Map<merchant, approvedAmountCents>.
    // =====================================================

    public Map<String, Long> getApprovedTotalsByMerchantInRegion(
            String region, long minAmountCents) {
        // TODO: Implement Part C
        return payments.stream().filter(p->p.getStatus()==Status.APPROVED
                && p.getAmountCents() >= minAmountCents && Objects.equals(p.getRegion(),region))
                .collect(Collectors.groupingBy((Payment::getMerchant),
                        Collectors.summingLong(p->p.getAmountCents())));
    }

    // =====================================================
    // TEST HARNESS
    // =====================================================

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
        PaymentRiskMonitorPractice monitor = new PaymentRiskMonitorPractice();

        System.out.println("===== PART A: BUG FIX =====");

        check("A1: SHOP_A approved total",
                monitor.getApprovedAmountByMerchant("SHOP_A") == 14000L);

        check("A2: SHOP_B approved total",
                monitor.getApprovedAmountByMerchant("SHOP_B") == 22000L);

        check("A3: SHOP_C has no approved payments",
                monitor.getApprovedAmountByMerchant("SHOP_C") == 0L);

        check("A4: Unknown merchant has zero total",
                monitor.getApprovedAmountByMerchant("UNKNOWN") == 0L);

        System.out.println("\n===== PART B: NEW FEATURE =====");

        check("B1: Regions with declined payments and >= 2 retries",
                monitor.getHighRetryDeclinedRegions(2)
                        .equals(List.of("APAC", "EU")));

        check("B2: Regions with declined payments and >= 3 retries",
                monitor.getHighRetryDeclinedRegions(3)
                        .equals(List.of("APAC", "EU")));

        check("B3: No regions meet retry threshold",
                monitor.getHighRetryDeclinedRegions(5).isEmpty());

        System.out.println("\n===== PART C: ADVANCED FEATURE =====");

        Map<String, Long> expectedC1 = new HashMap<>();
        expectedC1.put("SHOP_A", 8000L);

        check("C1: APAC approved totals >= 7000 cents",
                monitor.getApprovedTotalsByMerchantInRegion("APAC", 7000L)
                        .equals(expectedC1));

        Map<String, Long> expectedC2 = new HashMap<>();
        expectedC2.put("SHOP_B", 10000L);

        check("C2: APAC approved totals >= 10000 cents",
                monitor.getApprovedTotalsByMerchantInRegion("APAC", 10000L)
                        .equals(expectedC2));

        check("C3: No merchant qualifies in US at 5000 cents",
                monitor.getApprovedTotalsByMerchantInRegion("US", 5000L)
                        .isEmpty());

        System.out.println("\n===== RESULTS =====");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println("Total: " + (passed + failed));
    }
}
