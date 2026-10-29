
package CitiKaratTestPractice;

import java.util.*;
import java.util.stream.Collectors;

enum RequestStatus {
    SUCCESS, FAILED, PENDING
}

class RequestRecord {
    private final String requestId;
    private final String clientId;
    private final String endpoint;
    private final RequestStatus status;
    private final int retryCount;
    private final long timestamp;

    RequestRecord(String requestId, String clientId, String endpoint,
                  RequestStatus status, int retryCount, long timestamp) {
        this.requestId = requestId;
        this.clientId = clientId;
        this.endpoint = endpoint;
        this.status = status;
        this.retryCount = retryCount;
        this.timestamp = timestamp;
    }

    public String getRequestId() { return requestId; }
    public String getClientId() { return clientId; }
    public String getEndpoint() { return endpoint; }
    public RequestStatus getStatus() { return status; }
    public int getRetryCount() { return retryCount; }
    public long getTimestamp() { return timestamp; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof RequestRecord)) return false;
        RequestRecord other = (RequestRecord) obj;
        return Objects.equals(requestId, other.requestId)
                && Objects.equals(clientId, other.clientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId, clientId);
    }

    @Override
    public String toString() {
        return "RequestRecord{" + requestId + ", " + clientId
                + ", " + status + ", retries=" + retryCount + "}";
    }
}

class RequestAuditService {
    private final List<RequestRecord> records = new ArrayList<>();

    public void addRecord(RequestRecord record) {
        records.add(record);
    }

    public List<RequestRecord> getAllRecords() {
        return Collections.unmodifiableList(records);
    }

    // Helper: find records for a client.
    public List<RequestRecord> getRecordsForClient(String clientId) {
        return records.stream()
                .filter(r -> Objects.equals(r.getClientId(), clientId))
                .collect(Collectors.toList());
    }

    // PART A:
    // Return the number of FAILED records for this client
    // whose retryCount is greater than or equal to minRetries.
    public long countFailedRequestsWithRetries(
            String clientId, int minRetries) {
        return records.stream()
                .filter(r -> Objects.equals(r.getClientId(), clientId))
                .filter(r -> r.getStatus() == RequestStatus.FAILED)
                .filter(r -> r.getRetryCount() >= minRetries)
                .count();
    }

    // PART B:
    // Return SUCCESS records for this client, ordered by timestamp
    // ascending. Do not modify the original records list.
    public List<RequestRecord> getSuccessfulRequests(String clientId) {
        List<RequestRecord> chList=getAllRecords();
        return chList.stream().filter(rr->(Objects.equals(clientId,rr.getClientId()) && rr.getStatus()==RequestStatus.SUCCESS))
                .sorted(Comparator.comparingLong(RequestRecord::getTimestamp))
                .collect(Collectors.toList()); // TODO
    }

    // PART C:
    // Return clientId -> total retryCount across FAILED records.
    // Include only clients having at least one FAILED record.
    // A failed record contributes its retryCount to the total.
    public Map<String, Integer> getFailedRetryTotalsByClient() {
        List<RequestRecord> chLists=getAllRecords();
        return chLists.stream().filter(rr->rr.getStatus()==RequestStatus.FAILED)
                .collect(
                        Collectors.groupingBy((RequestRecord rr)->rr.getClientId(),
                        Collectors.summingInt((RequestRecord::getRetryCount)))); // TODO
    }

    // Existing utility method used by other code.
    public Set<String> getDistinctEndpoints(String clientId) {
        return records.stream()
                .filter(r -> Objects.equals(r.getClientId(), clientId))
                .map(RequestRecord::getEndpoint)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(TreeSet::new));
    }
}

public class RequestAuditMock {
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

    static RequestAuditService createService() {
        RequestAuditService service = new RequestAuditService();

        service.addRecord(new RequestRecord(
                "R-101", "CLIENT-A", "/orders",
                RequestStatus.FAILED, 3, 3000));

        service.addRecord(new RequestRecord(
                "R-102", "CLIENT-A", "/payments",
                RequestStatus.SUCCESS, 0, 1000));

        service.addRecord(new RequestRecord(
                "R-201", "CLIENT-B", "/orders",
                RequestStatus.FAILED, 2, 2000));

        service.addRecord(new RequestRecord(
                "R-103", "CLIENT-A", "/orders",
                RequestStatus.FAILED, 1, 4000));

        service.addRecord(new RequestRecord(
                "R-104", "CLIENT-A", "/users",
                RequestStatus.SUCCESS, 1, 5000));

        service.addRecord(new RequestRecord(
                "R-202", "CLIENT-B", "/payments",
                RequestStatus.PENDING, 4, 6000));

        service.addRecord(new RequestRecord(
                "R-301", "CLIENT-C", "/health",
                RequestStatus.SUCCESS, 0, 7000));

        service.addRecord(new RequestRecord(
                "R-203", "CLIENT-B", "/orders",
                RequestStatus.FAILED, 4, 8000));

        return service;
    }

    // ==================== PART A TESTS ====================

    static void testFailedRequestsWithRetries() {
        RequestAuditService service = createService();

        checkEquals("A1: CLIENT-A retries >= 2",
                1L, service.countFailedRequestsWithRetries("CLIENT-A", 2));

        checkEquals("A2: CLIENT-B retries >= 2",
                2L, service.countFailedRequestsWithRetries("CLIENT-B", 2));

        checkEquals("A3: CLIENT-A retries >= 1",
                2L, service.countFailedRequestsWithRetries("CLIENT-A", 1));

        checkEquals("A4: unknown client",
                0L, service.countFailedRequestsWithRetries("CLIENT-X", 0));

        checkEquals("A5: minimum retry count zero",
                2L, service.countFailedRequestsWithRetries("CLIENT-A", 0));
    }

    // ==================== PART B TESTS ====================

    static void testSuccessfulRequests() {
        RequestAuditService service = createService();

        List<RequestRecord> result =
                service.getSuccessfulRequests("CLIENT-A");

        checkEquals("B1: successful count",
                2, result.size());

        if (result.size() >= 2) {
            checkEquals("B2: earliest successful request",
                    "R-102", result.get(0).getRequestId());

            checkEquals("B3: latest successful request",
                    "R-104", result.get(1).getRequestId());
        }

        checkEquals("B4: unknown client returns empty list",
                0, service.getSuccessfulRequests("CLIENT-X").size());

        checkEquals("B5: client with no successes",
                0, service.getSuccessfulRequests("CLIENT-B").size());

        checkEquals("B6: original record count unchanged",
                8, service.getAllRecords().size());
    }

    // ==================== PART C TESTS ====================

    static void testFailedRetryTotals() {
        RequestAuditService service = createService();

        Map<String, Integer> result =
                service.getFailedRetryTotalsByClient();

        checkEquals("C1: CLIENT-A failed retry total",
                4, result.get("CLIENT-A"));

        checkEquals("C2: CLIENT-B failed retry total",
                6, result.get("CLIENT-B"));

        checkEquals("C3: number of clients included",
                2, result.size());

        checkEquals("C4: client with no failures omitted",
                false, result.containsKey("CLIENT-C"));

        RequestAuditService empty = new RequestAuditService();

        checkEquals("C5: empty service result",
                0, empty.getFailedRetryTotalsByClient().size());
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
        runTest("Part A", RequestAuditMock::testFailedRequestsWithRetries);

        System.out.println("\n========== PART B ==========");
        runTest("Part B", RequestAuditMock::testSuccessfulRequests);

        System.out.println("\n========== PART C ==========");
        runTest("Part C", RequestAuditMock::testFailedRetryTotals);

        System.out.println("\n========== FINAL SUMMARY ==========");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
    }
}
