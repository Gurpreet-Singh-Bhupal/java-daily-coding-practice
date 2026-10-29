
package CitiKaratTestPractice;

import java.util.*;
import java.util.stream.Collectors;

public class InventoryReservationPractice {

    enum Status {
        ACTIVE, CANCELLED, PENDING
    }

    static class Reservation {
        private final String reservationId;
        private final String warehouse;
        private final String category;
        private final Status status;
        private final int quantity;

        Reservation(String reservationId, String warehouse,
                    String category, Status status, int quantity) {
            this.reservationId = reservationId;
            this.warehouse = warehouse;
            this.category = category;
            this.status = status;
            this.quantity = quantity;
        }

        public String getReservationId() { return reservationId; }
        public String getWarehouse() { return warehouse; }
        public String getCategory() { return category; }
        public Status getStatus() { return status; }
        public int getQuantity() { return quantity; }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Reservation)) return false;
            Reservation other = (Reservation) obj;
            return Objects.equals(reservationId, other.reservationId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(reservationId);
        }
    }

    private final List<Reservation> reservations = new ArrayList<>();

    public InventoryReservationPractice() {
        reservations.add(new Reservation("R101", "WH_A", "ELECTRONICS", Status.ACTIVE, 2));
        reservations.add(new Reservation("R102", "WH_A", "BOOKS", Status.ACTIVE, 4));
        reservations.add(new Reservation("R103", "WH_B", "ELECTRONICS", Status.CANCELLED, 3));
        reservations.add(new Reservation("R104", "WH_A", "BOOKS", Status.CANCELLED, 1));
        reservations.add(new Reservation("R105", "WH_B", "BOOKS", Status.ACTIVE, 5));
        reservations.add(new Reservation("R106", "WH_C", "ELECTRONICS", Status.ACTIVE, 1));
        reservations.add(new Reservation("R107", "WH_C", "BOOKS", Status.PENDING, 6));
        reservations.add(new Reservation("R108", "WH_B", "ELECTRONICS", Status.ACTIVE, 2));
        reservations.add(new Reservation("R109", "WH_A", "ELECTRONICS", Status.ACTIVE, 3));
        reservations.add(new Reservation("R110", "WH_C", "BOOKS", Status.CANCELLED, 2));
        reservations.add(new Reservation("R111", "WH_B", "BOOKS", Status.ACTIVE, 2));
    }

    // PART A — BUG FIX
    // Return the total quantity in ACTIVE reservations for a warehouse.

    public int getActiveQuantityByWarehouse(String warehouse) {
        int total = 0;

        for (Reservation reservation : reservations) {
            if (reservation.getWarehouse().equals(warehouse)
                    && reservation.getStatus() == Status.ACTIVE) {
                total += reservation.getQuantity();
            }
        }

        return total;
    }

    // PART B — NEW FEATURE
    // Return distinct categories from ACTIVE reservations for a warehouse
    // where an individual reservation's quantity >= minQuantity.
    // Return categories in alphabetical order.

    public List<String> getActiveCategories(
            String warehouse, int minQuantity) {
        // TODO: Implement Part B
        return reservations.stream().filter(re->Objects.equals(warehouse,re.getWarehouse())
                && re.getStatus()==Status.ACTIVE && re.getQuantity()>=minQuantity)
                .map(m->m.getCategory()).distinct()
                .sorted(Comparator.comparing(m->m))
                .collect(Collectors.toList());
    }

    // PART C — ADVANCED FEATURE
    // For a category, sum ACTIVE reservation quantities per warehouse.
    // Include only warehouses whose AGGREGATED quantity >= minTotalQuantity.

    public Map<String, Integer> getActiveQuantityByCategory(
            String category, int minTotalQuantity) {
        // TODO: Implement Part C
        return reservations.stream().filter(re-> Objects.equals(re.getCategory(),category) && re.getStatus()==Status.ACTIVE)
                .collect(Collectors.groupingBy(re->re.getWarehouse(),
                        Collectors.summingInt(re->re.getQuantity())))
                .entrySet().stream().filter(re->re.getValue()>=minTotalQuantity)
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
        InventoryReservationPractice monitor =
                new InventoryReservationPractice();

        System.out.println("===== PART A =====");

        check("A1: WH_A active quantity",
                monitor.getActiveQuantityByWarehouse("WH_A") == 9);
        check("A2: WH_B active quantity",
                monitor.getActiveQuantityByWarehouse("WH_B") == 9);
        check("A3: WH_C active quantity",
                monitor.getActiveQuantityByWarehouse("WH_C") == 1);
        check("A4: Unknown warehouse",
                monitor.getActiveQuantityByWarehouse("UNKNOWN") == 0);

        System.out.println("\n===== PART B =====");

        check("B1: WH_A categories with quantity >= 2",
                monitor.getActiveCategories("WH_A", 2)
                        .equals(List.of("BOOKS", "ELECTRONICS")));
        check("B2: WH_B categories with quantity >= 2",
                monitor.getActiveCategories("WH_B", 2)
                        .equals(List.of("BOOKS", "ELECTRONICS")));
        check("B3: No reservation meets quantity >= 6",
                monitor.getActiveCategories("WH_A", 6).isEmpty());

        System.out.println("\n===== PART C =====");

        Map<String, Integer> expectedC1 = new HashMap<>();
        expectedC1.put("WH_A", 5);
        expectedC1.put("WH_B", 2);

        check("C1: ELECTRONICS active totals >= 2",
                monitor.getActiveQuantityByCategory("ELECTRONICS", 2)
                        .equals(expectedC1));

        Map<String, Integer> expectedC2 = new HashMap<>();
        expectedC2.put("WH_A", 5);

        check("C2: ELECTRONICS active totals >= 5",
                monitor.getActiveQuantityByCategory("ELECTRONICS", 5)
                        .equals(expectedC2));

        check("C3: No BOOKS warehouse total reaches 8",
                monitor.getActiveQuantityByCategory("BOOKS", 8)
                        .isEmpty());

        System.out.println("\n===== RESULTS =====");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println("Total: " + (passed + failed));
    }
}
