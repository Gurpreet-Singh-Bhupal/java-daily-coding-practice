
package CitiKaratTestPractice;

import java.util.*;
import java.util.stream.*;

enum PaymentStatus {
    SUCCESS, FAILED, PENDING
}

enum PaymentType {
    PAYMENT, REFUND
}

class PaymentRecord {
    private final String paymentId;
    private final String merchantId;
    private final PaymentType type;
    private final double amount;
    private final PaymentStatus status;
    private final long timestamp;

    PaymentRecord(String paymentId, String merchantId,
                  PaymentType type, double amount,
                  PaymentStatus status, long timestamp) {
        this.paymentId = paymentId;
        this.merchantId = merchantId;
        this.type = type;
        this.amount = amount;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getPaymentId() { return paymentId; }
    public String getMerchantId() { return merchantId; }
    public PaymentType getType() { return type; }
    public double getAmount() { return amount; }
    public PaymentStatus getStatus() { return status; }
    public long getTimestamp() { return timestamp; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PaymentRecord)) return false;
        PaymentRecord other = (PaymentRecord) obj;
        return Objects.equals(paymentId, other.paymentId)
                && Objects.equals(merchantId, other.merchantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentId, merchantId);
    }

    @Override
    public String toString() {
        return paymentId + ":" + merchantId + ":"
                + type + ":" + amount + ":" + status;
    }
}

class PaymentReconciliationService {
    private final List<PaymentRecord> records = new ArrayList<>();

    public void addRecord(PaymentRecord record) {
        records.add(record);
    }

    public List<PaymentRecord> getAllRecords() {
        return Collections.unmodifiableList(records);
    }

    // PART A:
    // Return the net successful amount for a merchant.
    // PAYMENT contributes positively; REFUND contributes negatively.
    // Ignore FAILED and PENDING records.
    public double getNetSuccessfulAmount(String merchantId) {
        return records.stream()
                .filter(r -> Objects.equals(r.getMerchantId(), merchantId))
                .filter(r -> r.getStatus() == PaymentStatus.SUCCESS)
                .mapToDouble(r -> r.getType() == PaymentType.PAYMENT
                        ? r.getAmount() : -r.getAmount())
                .sum();
    }

    // PART B:
    // Return SUCCESS records for the merchant, timestamp ascending.
    // Do not modify the original records list.
    public List<PaymentRecord> getSuccessfulRecords(String merchantId) {
        List<PaymentRecord> lst= getAllRecords();
        return lst.stream().filter(pr->(Objects.equals(pr.getMerchantId(),merchantId)
                        && pr.getStatus()==PaymentStatus.SUCCESS))
                .sorted(Comparator.comparingLong(PaymentRecord::getTimestamp))
                .collect(Collectors.toList()); // TODO
    }

    // PART C:
    // Return merchantId -> net successful amount.
    // PAYMENT adds amount; REFUND subtracts amount.
    // Include only merchants with at least one SUCCESS record.
    public Map<String, Double> getNetAmountByMerchant() {
        List<PaymentRecord> lst= getAllRecords();
        return lst.stream().filter(mt->mt.getStatus()==PaymentStatus.SUCCESS)
                .collect(Collectors.groupingBy(PaymentRecord::getMerchantId,
                        Collectors.summingDouble((PaymentRecord pr)->{
                            return pr.getType()==PaymentType.PAYMENT ? pr.getAmount() : -pr.getAmount();
                        }))); // TODO
    }
}

public class PaymentReconciliationMock {
    static int passed = 0;
    static int failed = 0;

    static void checkEquals(String name, Object expected, Object actual) {
        if (Objects.equals(expected, actual)) {
            passed++;
            System.out.println("PASS: " + name);
        } else {
            failed++;
            System.out.println("FAIL: " + name
                    + " | expected=" + expected
                    + " | actual=" + actual);
        }
    }

    static void checkDouble(String name, double expected, double actual) {
        if (Math.abs(expected - actual) < 0.0001) {
            passed++;
            System.out.println("PASS: " + name);
        } else {
            failed++;
            System.out.println("FAIL: " + name
                    + " | expected=" + expected
                    + " | actual=" + actual);
        }
    }

    static PaymentReconciliationService createService() {
        PaymentReconciliationService service =
                new PaymentReconciliationService();

        service.addRecord(new PaymentRecord(
                "P-104", "MERCHANT-A", PaymentType.PAYMENT,
                300.0, PaymentStatus.SUCCESS, 4000));

        service.addRecord(new PaymentRecord(
                "P-102", "MERCHANT-A", PaymentType.PAYMENT,
                200.0, PaymentStatus.SUCCESS, 2000));

        service.addRecord(new PaymentRecord(
                "P-101", "MERCHANT-A", PaymentType.REFUND,
                50.0, PaymentStatus.SUCCESS, 1000));

        service.addRecord(new PaymentRecord(
                "P-103", "MERCHANT-A", PaymentType.PAYMENT,
                100.0, PaymentStatus.FAILED, 3000));

        service.addRecord(new PaymentRecord(
                "P-201", "MERCHANT-B", PaymentType.PAYMENT,
                500.0, PaymentStatus.SUCCESS, 5000));

        service.addRecord(new PaymentRecord(
                "P-202", "MERCHANT-B", PaymentType.REFUND,
                100.0, PaymentStatus.SUCCESS, 6000));

        service.addRecord(new PaymentRecord(
                "P-301", "MERCHANT-C", PaymentType.PAYMENT,
                700.0, PaymentStatus.PENDING, 7000));

        service.addRecord(new PaymentRecord(
                "P-203", "MERCHANT-B", PaymentType.PAYMENT,
                50.0, PaymentStatus.FAILED, 8000));

        return service;
    }

    // ==================== PART A TESTS ====================

    static void testNetSuccessfulAmount() {
        PaymentReconciliationService service = createService();

        checkDouble("A1: MERCHANT-A net successful amount",
                450.0, service.getNetSuccessfulAmount("MERCHANT-A"));

        checkDouble("A2: MERCHANT-B net successful amount",
                400.0, service.getNetSuccessfulAmount("MERCHANT-B"));

        checkDouble("A3: merchant with no successful records",
                0.0, service.getNetSuccessfulAmount("MERCHANT-C"));

        checkDouble("A4: unknown merchant",
                0.0, service.getNetSuccessfulAmount("MERCHANT-X"));

        checkDouble("A5: failed payment excluded",
                450.0, service.getNetSuccessfulAmount("MERCHANT-A"));
    }

    // ==================== PART B TESTS ====================

    static void testSuccessfulRecords() {
        PaymentReconciliationService service = createService();

        List<PaymentRecord> result =
                service.getSuccessfulRecords("MERCHANT-A");

        checkEquals("B1: successful record count", 3, result.size());

        if (result.size() >= 3) {
            checkEquals("B2: earliest record",
                    "P-101", result.get(0).getPaymentId());

            checkEquals("B3: middle record",
                    "P-102", result.get(1).getPaymentId());

            checkEquals("B4: latest record",
                    "P-104", result.get(2).getPaymentId());
        }

        checkEquals("B5: merchant with no successful records",
                0, service.getSuccessfulRecords("MERCHANT-C").size());

        checkEquals("B6: unknown merchant",
                0, service.getSuccessfulRecords("MERCHANT-X").size());

        checkEquals("B7: original record count unchanged",
                8, service.getAllRecords().size());
    }

    // ==================== PART C TESTS ====================

    static void testNetAmountByMerchant() {
        PaymentReconciliationService service = createService();

        Map<String, Double> result = service.getNetAmountByMerchant();

        checkDouble("C1: MERCHANT-A net amount",
                450.0, result.get("MERCHANT-A"));

        checkDouble("C2: MERCHANT-B net amount",
                400.0, result.get("MERCHANT-B"));

        checkEquals("C3: only merchants with successful records",
                2, result.size());

        checkEquals("C4: MERCHANT-C omitted",
                false, result.containsKey("MERCHANT-C"));

        PaymentReconciliationService empty =
                new PaymentReconciliationService();

        checkEquals("C5: empty service",
                0, empty.getNetAmountByMerchant().size());
    }

    static void runTest(String name, Runnable test) {
        try {
            test.run();
        } catch (Throwable error) {
            failed++;
            System.out.println("FAIL: " + name
                    + " | test crashed: "
                    + error.getClass().getSimpleName()
                    + ": " + error.getMessage());
        }
    }

    public static void main(String[] args) {
        passed = 0;
        failed = 0;

        System.out.println("========== PART A ==========");
        runTest("Part A", PaymentReconciliationMock::testNetSuccessfulAmount);

        System.out.println("\n========== PART B ==========");
        runTest("Part B", PaymentReconciliationMock::testSuccessfulRecords);

        System.out.println("\n========== PART C ==========");
        runTest("Part C", PaymentReconciliationMock::testNetAmountByMerchant);

        System.out.println("\n========== FINAL SUMMARY ==========");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
    }
}
