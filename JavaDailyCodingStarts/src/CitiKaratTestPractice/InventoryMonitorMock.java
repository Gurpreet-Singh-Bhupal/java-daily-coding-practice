
package CitiKaratTestPractice;

import java.util.*;
import java.util.stream.Collectors;

enum StockStatus {
    ACTIVE, INACTIVE
}

class StockRecord {
    String productId;
    String warehouseId;
    int quantity;
    StockStatus status;
    long lastUpdated;

    StockRecord(String productId, String warehouseId,
                int quantity, StockStatus status,
                long lastUpdated) {
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.quantity = quantity;
        this.status = status;
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String toString() {
        return productId + ":" + warehouseId + ":"
                + quantity + ":" + status;
    }
}

class InventoryMonitor {
    private final List<StockRecord> records = new ArrayList<>();

    public void addStock(StockRecord record) {
        records.add(record);
    }

    public List<StockRecord> getAllRecords() {
        return Collections.unmodifiableList(records);
    }

    // PART A:
    // Count ACTIVE products in the specified warehouse
    // whose quantity is less than or equal to the threshold.
    public long countLowStock(String warehouseId, int threshold) {
        return records.stream()
                .filter(r -> Objects.equals(r.warehouseId, warehouseId))
                .filter(r -> r.status == StockStatus.ACTIVE)
                .filter(r -> r.quantity <= threshold)
                .count();
    }

    // PART B:
    // Return ACTIVE stock records for the specified warehouse.
    // Sort by quantity ascending.
    public List<StockRecord> getActiveStockByWarehouse(
            String warehouseId) {
        List<StockRecord> lst=getAllRecords();
        return lst.stream().filter(sr->(sr.status==StockStatus.ACTIVE && Objects.equals(sr.warehouseId, warehouseId)))
                .sorted(Comparator.comparingInt((StockRecord s)->s.quantity))
                .collect(Collectors.toList()); // TODO: implement
    }

    // PART C:
    // Return warehouseId -> total quantity of ACTIVE stock.
    // Omit warehouses that have no ACTIVE records.
    public Map<String, Integer> getActiveQuantityByWarehouse() {
        List<StockRecord> src=getAllRecords();
        return src.stream().filter(sr->sr.status==StockStatus.ACTIVE)
                .collect(Collectors.groupingBy((StockRecord st)->st.warehouseId,Collectors.summingInt((StockRecord sr)->sr.quantity))); // TODO: implement
    }
}

public class InventoryMonitorMock {

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

    static InventoryMonitor createMonitor() {
        InventoryMonitor monitor = new InventoryMonitor();

        // WAREHOUSE-A
        monitor.addStock(new StockRecord(
                "P-104", "WAREHOUSE-A", 8,
                StockStatus.ACTIVE, 4000));

        monitor.addStock(new StockRecord(
                "P-101", "WAREHOUSE-A", 3,
                StockStatus.ACTIVE, 1000));

        monitor.addStock(new StockRecord(
                "P-102", "WAREHOUSE-A", 0,
                StockStatus.ACTIVE, 2000));

        monitor.addStock(new StockRecord(
                "P-103", "WAREHOUSE-A", 1,
                StockStatus.INACTIVE, 3000));

        // WAREHOUSE-B
        monitor.addStock(new StockRecord(
                "P-201", "WAREHOUSE-B", 2,
                StockStatus.ACTIVE, 5000));

        monitor.addStock(new StockRecord(
                "P-202", "WAREHOUSE-B", 12,
                StockStatus.ACTIVE, 6000));

        // WAREHOUSE-C has only inactive stock.
        monitor.addStock(new StockRecord(
                "P-301", "WAREHOUSE-C", 20,
                StockStatus.INACTIVE, 7000));

        return monitor;
    }

    // ==================== PART A TESTS ====================

    static void testCountLowStock() {
        InventoryMonitor monitor = createMonitor();

        checkEquals("A1: Warehouse A low-stock count",
                2L, monitor.countLowStock("WAREHOUSE-A", 5));

        checkEquals("A2: Warehouse B low-stock count",
                1L, monitor.countLowStock("WAREHOUSE-B", 5));

        checkEquals("A3: Warehouse C low-stock count",
                0L, monitor.countLowStock("WAREHOUSE-C", 5));

        checkEquals("A4: unknown warehouse low-stock count",
                0L, monitor.countLowStock("WAREHOUSE-X", 5));

        checkEquals("A5: zero threshold",
                1L, monitor.countLowStock("WAREHOUSE-A", 0));
    }

    // ==================== PART B TESTS ====================

    static void testActiveStockByWarehouse() {
        InventoryMonitor monitor = createMonitor();

        List<StockRecord> result =
                monitor.getActiveStockByWarehouse("WAREHOUSE-A");

        checkEquals("B1: active record count",
                3, result.size());

        if (result.size() >= 3) {
            checkEquals("B2: lowest quantity first",
                    "P-102", result.get(0).productId);

            checkEquals("B3: middle quantity",
                    "P-101", result.get(1).productId);

            checkEquals("B4: highest quantity last",
                    "P-104", result.get(2).productId);
        }

        checkEquals("B5: inactive-only warehouse",
                0, monitor.getActiveStockByWarehouse(
                        "WAREHOUSE-C").size());

        checkEquals("B6: unknown warehouse",
                0, monitor.getActiveStockByWarehouse(
                        "WAREHOUSE-X").size());
    }

    // ==================== PART C TESTS ====================

    static void testActiveQuantityByWarehouse() {
        InventoryMonitor monitor = createMonitor();

        Map<String, Integer> result =
                monitor.getActiveQuantityByWarehouse();

        checkEquals("C1: Warehouse A active quantity",
                11, result.get("WAREHOUSE-A"));

        checkEquals("C2: Warehouse B active quantity",
                14, result.get("WAREHOUSE-B"));

        checkEquals("C3: number of included warehouses",
                2, result.size());

        checkTrue("C4: inactive-only warehouse omitted",
                !result.containsKey("WAREHOUSE-C"));

        checkTrue("C5: unknown warehouse omitted",
                !result.containsKey("WAREHOUSE-X"));

        InventoryMonitor empty = new InventoryMonitor();

        checkEquals("C6: empty monitor",
                0, empty.getActiveQuantityByWarehouse().size());
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
        runTest("Part A", InventoryMonitorMock::testCountLowStock);

        System.out.println("\n========== PART B ==========");
        runTest("Part B",
                InventoryMonitorMock::testActiveStockByWarehouse);

        System.out.println("\n========== PART C ==========");
        runTest("Part C",
                InventoryMonitorMock::testActiveQuantityByWarehouse);

        System.out.println("\n========== FINAL SUMMARY ==========");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
    }
}
