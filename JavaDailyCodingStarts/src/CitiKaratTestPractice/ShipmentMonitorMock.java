
package CitiKaratTestPractice;

import java.util.*;
import java.util.stream.Collectors;

enum ShipmentStatus {
    ON_TIME, DELAYED, DELIVERED, CANCELLED
}

class Shipment {
    String shipmentId;
    String carrierId;
    ShipmentStatus status;
    int delayMinutes;
    long timestamp;

    Shipment(String shipmentId, String carrierId,
             ShipmentStatus status, int delayMinutes,
             long timestamp) {
        this.shipmentId = shipmentId;
        this.carrierId = carrierId;
        this.status = status;
        this.delayMinutes = delayMinutes;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return shipmentId + ":" + carrierId + ":"
                + status + ":" + delayMinutes;
    }
}

class ShipmentMonitor {
    private final List<Shipment> shipments = new ArrayList<>();

    public void addShipment(Shipment shipment) {
        shipments.add(shipment);
    }

    public List<Shipment> getAllShipments() {
        return Collections.unmodifiableList(shipments);
    }

    // PART A:
    // Return the number of DELAYED shipments for a carrier.
    public long getDelayedShipmentCount(String carrierId) {
        return shipments.stream()
                .filter(s -> s.carrierId.equals(carrierId))
                .filter(s -> s.status == ShipmentStatus.DELAYED)
                .count();
    }

    // PART B:
    // Return shipments for the specified carrier and status.
    // Sort the result by timestamp ascending.
    public List<Shipment> getShipmentsByCarrierAndStatus(
            String carrierId, ShipmentStatus status) {
        List<Shipment> shipList=getAllShipments();

        return shipList.stream().filter(ship-> (Objects.equals(ship.carrierId,carrierId) && ship.status==status))
                .sorted(Comparator.comparingLong((Shipment s)->s.timestamp)).collect(Collectors.toList()); // TODO: implement
    }

    // PART C:
    // Return carrierId -> total delay minutes.
    // Include only DELAYED shipments.
    // Omit carriers that have no DELAYED shipments.
    public Map<String, Integer> getDelayMinutesByCarrier() {
        List<Shipment> shipments=getAllShipments();
        return shipments.stream().filter(ship->ship.status==ShipmentStatus.DELAYED)
                .collect(Collectors.groupingBy((Shipment sp)->sp.carrierId,Collectors.summingInt((Shipment s)->s.delayMinutes))); // TODO: implement
    }
}

public class ShipmentMonitorMock {

    static int passed = 0;
    static int failed = 0;

    static void checkEquals(String name, Object expected,
                            Object actual) {
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

    static void checkTrue(String name, boolean condition) {
        checkEquals(name, true, condition);
    }

    static ShipmentMonitor createMonitor() {
        ShipmentMonitor monitor = new ShipmentMonitor();

        // Deliberately not inserted in timestamp order.
        monitor.addShipment(new Shipment(
                "S-104", "CARR-A", ShipmentStatus.DELAYED, 20, 4000));

        monitor.addShipment(new Shipment(
                "S-102", "CARR-A", ShipmentStatus.DELIVERED, 0, 2000));

        monitor.addShipment(new Shipment(
                "S-201", "CARR-B", ShipmentStatus.DELAYED, 15, 3000));

        monitor.addShipment(new Shipment(
                "S-101", "CARR-A", ShipmentStatus.DELAYED, 45, 1000));

        monitor.addShipment(new Shipment(
                "S-301", "CARR-C", ShipmentStatus.ON_TIME, 0, 5000));

        monitor.addShipment(new Shipment(
                "S-202", "CARR-B", ShipmentStatus.CANCELLED, 0, 6000));

        monitor.addShipment(new Shipment(
                "S-105", "CARR-A", ShipmentStatus.ON_TIME, 0, 7000));

        return monitor;
    }

    // ==================== PART A TESTS ====================

    static void testDelayedShipmentCount() {
        ShipmentMonitor monitor = createMonitor();

        checkEquals("A1: CARR-A delayed count",
                2L, monitor.getDelayedShipmentCount("CARR-A"));

        checkEquals("A2: CARR-B delayed count",
                1L, monitor.getDelayedShipmentCount("CARR-B"));

        checkEquals("A3: CARR-C delayed count",
                0L, monitor.getDelayedShipmentCount("CARR-C"));

        checkEquals("A4: unknown carrier delayed count",
                0L, monitor.getDelayedShipmentCount("CARR-999"));
    }

    // ==================== PART B TESTS ====================

    static void testShipmentsByCarrierAndStatus() {
        ShipmentMonitor monitor = createMonitor();

        List<Shipment> result =
                monitor.getShipmentsByCarrierAndStatus(
                        "CARR-A", ShipmentStatus.DELAYED);

        checkEquals("B1: matching delayed shipment count", 2, result.size());

        if (result.size() >= 2) {
            checkEquals("B2: earliest shipment",
                    "S-101", result.get(0).shipmentId);

            checkEquals("B3: latest shipment",
                    "S-104", result.get(1).shipmentId);
        }

        checkEquals("B4: unknown carrier result",
                0, monitor.getShipmentsByCarrierAndStatus(
                        "CARR-999", ShipmentStatus.DELAYED).size());

        checkEquals("B5: unmatched status result",
                0, monitor.getShipmentsByCarrierAndStatus(
                        "CARR-A", ShipmentStatus.CANCELLED).size());
    }

    // ==================== PART C TESTS ====================

    static void testDelayMinutesByCarrier() {
        ShipmentMonitor monitor = createMonitor();

        Map<String, Integer> result =
                monitor.getDelayMinutesByCarrier();

        checkEquals("C1: CARR-A total delay",
                65, result.get("CARR-A"));

        checkEquals("C2: CARR-B total delay",
                15, result.get("CARR-B"));

        checkEquals("C3: number of included carriers",
                2, result.size());

        checkTrue("C4: carrier with no delays omitted",
                !result.containsKey("CARR-C"));

        checkTrue("C5: unknown carrier omitted",
                !result.containsKey("CARR-999"));

        ShipmentMonitor empty = new ShipmentMonitor();

        checkEquals("C6: empty monitor result",
                0, empty.getDelayMinutesByCarrier().size());
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
        runTest("Part A", ShipmentMonitorMock::testDelayedShipmentCount);

        System.out.println("\n========== PART B ==========");
        runTest("Part B",
                ShipmentMonitorMock::testShipmentsByCarrierAndStatus);

        System.out.println("\n========== PART C ==========");
        runTest("Part C",
                ShipmentMonitorMock::testDelayMinutesByCarrier);

        System.out.println("\n========== FINAL SUMMARY ==========");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
    }
}
