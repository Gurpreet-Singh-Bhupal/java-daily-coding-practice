
package CitiKaratTestPractice;

import java.util.*;
import java.util.stream.*;

public class OrderFulfillmentPractice {

    enum Status {
        SHIPPED, CANCELLED, PROCESSING
    }

    static class Order {
        private final String orderId;
        private final String warehouse;
        private final String category;
        private final Status status;
        private final int itemCount;
        private final long totalCents;

        Order(String orderId, String warehouse, String category,
              Status status, int itemCount, long totalCents) {
            this.orderId = orderId;
            this.warehouse = warehouse;
            this.category = category;
            this.status = status;
            this.itemCount = itemCount;
            this.totalCents = totalCents;
        }

        public String getOrderId() { return orderId; }
        public String getWarehouse() { return warehouse; }
        public String getCategory() { return category; }
        public Status getStatus() { return status; }
        public int getItemCount() { return itemCount; }
        public long getTotalCents() { return totalCents; }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Order)) return false;
            Order other = (Order) obj;
            return Objects.equals(orderId, other.orderId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(orderId);
        }
    }

    private final List<Order> orders = new ArrayList<>();

    public OrderFulfillmentPractice() {
        orders.add(new Order("O101", "WH_A", "ELECTRONICS", Status.SHIPPED, 2, 15000));
        orders.add(new Order("O102", "WH_A", "BOOKS", Status.SHIPPED, 4, 4000));
        orders.add(new Order("O103", "WH_B", "ELECTRONICS", Status.CANCELLED, 3, 12000));
        orders.add(new Order("O104", "WH_A", "BOOKS", Status.CANCELLED, 1, 2000));
        orders.add(new Order("O105", "WH_B", "BOOKS", Status.SHIPPED, 5, 5000));
        orders.add(new Order("O106", "WH_C", "ELECTRONICS", Status.SHIPPED, 1, 9000));
        orders.add(new Order("O107", "WH_C", "BOOKS", Status.PROCESSING, 6, 6000));
        orders.add(new Order("O108", "WH_B", "ELECTRONICS", Status.SHIPPED, 2, 11000));
        orders.add(new Order("O109", "WH_A", "ELECTRONICS", Status.SHIPPED, 3, 18000));
        orders.add(new Order("O110", "WH_C", "BOOKS", Status.CANCELLED, 2, 3000));
        orders.add(new Order("O111", "WH_B", "BOOKS", Status.SHIPPED, 2, 2500));
    }

    // PART A — BUG FIX
    // Return the total number of items in SHIPPED orders for a warehouse.

    public int countShippedItems(String warehouse) {
        int total = 0;

        for (Order order : orders) {
            if (order.getWarehouse().equals(warehouse)
                    && order.getStatus() == Status.SHIPPED) {
                total+=order.getItemCount();
            }
        }

        return total;
    }

    // PART B — NEW FEATURE
    // Return distinct categories of SHIPPED orders from a warehouse,
    // in alphabetical order.

    public List<String> getShippedCategories(String warehouse) {
        // TODO: Implement Part B
        return orders.stream().filter(o->Objects.equals(warehouse,o.getWarehouse())
                && o.getStatus()==Status.SHIPPED).map(o->o.getCategory())
                .distinct().sorted(Comparator.comparing(o->o))
                .collect(Collectors.toList());
    }

    // PART C — ADVANCED FEATURE
    // For a given category, calculate the total value of SHIPPED orders
    // per warehouse. Include only warehouses whose AGGREGATED value
    // is >= minTotalCents.

    public Map<String, Long> getShippedValueByWarehouse(
            String category, long minTotalCents) {
        // TODO: Implement Part C
    return orders.stream().filter(or->Objects.equals(category,or.getCategory())
            && or.getStatus()==Status.SHIPPED).collect(Collectors.groupingBy(
                    (Order or)->or.getWarehouse(),
            Collectors.summingLong(Order::getTotalCents)))
            .entrySet().stream().filter(o->o.getValue()>=minTotalCents)
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
        OrderFulfillmentPractice monitor = new OrderFulfillmentPractice();

        System.out.println("===== PART A =====");
        check("A1: WH_A shipped item count",
                monitor.countShippedItems("WH_A") == 9);
        check("A2: WH_B shipped item count",
                monitor.countShippedItems("WH_B") == 9);
        check("A3: WH_C shipped item count",
                monitor.countShippedItems("WH_C") == 1);
        check("A4: Unknown warehouse",
                monitor.countShippedItems("UNKNOWN") == 0);

        System.out.println("\n===== PART B =====");
        check("B1: WH_A shipped categories",
                monitor.getShippedCategories("WH_A")
                        .equals(List.of("BOOKS", "ELECTRONICS")));
        check("B2: WH_B shipped categories",
                monitor.getShippedCategories("WH_B")
                        .equals(List.of("BOOKS", "ELECTRONICS")));
        check("B3: Unknown warehouse",
                monitor.getShippedCategories("UNKNOWN").isEmpty());

        System.out.println("\n===== PART C =====");
        Map<String, Long> expectedC1 = new HashMap<>();
        expectedC1.put("WH_A", 19000L);
        expectedC1.put("WH_C", 9000L);

        check("C1: ELECTRONICS shipped totals >= 9000 cents",
                monitor.getShippedValueByWarehouse("ELECTRONICS", 9000L)
                        .equals(expectedC1));

        Map<String, Long> expectedC2 = new HashMap<>();
        expectedC2.put("WH_A", 33000L);

        check("C2: ELECTRONICS shipped totals >= 18000 cents",
                monitor.getShippedValueByWarehouse("ELECTRONICS", 18000L)
                        .equals(expectedC2));

        check("C3: No warehouse qualifies for BOOKS at 20000 cents",
                monitor.getShippedValueByWarehouse("BOOKS", 20000L)
                        .isEmpty());

        System.out.println("\n===== RESULTS =====");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println("Total: " + (passed + failed));
    }
}
