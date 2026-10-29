
package CitiKaratTestPractice;

import java.util.*;
import java.util.stream.Collectors;

enum TicketStatus {
    OPEN, RESOLVED, CANCELLED
}

class SupportTicket {
    private final String ticketId;
    private final String teamId;
    private final int priority;
    private final TicketStatus status;
    private final long updatedAt;

    SupportTicket(String ticketId, String teamId, int priority,
                  TicketStatus status, long updatedAt) {
        this.ticketId = ticketId;
        this.teamId = teamId;
        this.priority = priority;
        this.status = status;
        this.updatedAt = updatedAt;
    }

    public String getTicketId() { return ticketId; }
    public String getTeamId() { return teamId; }
    public int getPriority() { return priority; }
    public TicketStatus getStatus() { return status; }
    public long getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof SupportTicket)) return false;
        SupportTicket other = (SupportTicket) obj;
        return Objects.equals(ticketId, other.ticketId)
                && Objects.equals(teamId, other.teamId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticketId, teamId);
    }

    @Override
    public String toString() {
        return ticketId + ":" + teamId + ":" + priority + ":" + status;
    }
}

class TicketMonitor {
    private final List<SupportTicket> tickets = new ArrayList<>();

    public void addTicket(SupportTicket ticket) {
        tickets.add(ticket);
    }

    public List<SupportTicket> getAllTickets() {
        return Collections.unmodifiableList(tickets);
    }

    // PART A:
    // Count OPEN tickets for the specified team whose priority
    // is greater than or equal to minPriority.
    public long countOpenTicketsByPriority(String teamId, int minPriority) {
        return tickets.stream()
                .filter(t -> Objects.equals(t.getTeamId(), teamId))
                .filter(t -> t.getStatus() == TicketStatus.OPEN)
                .filter(t -> t.getPriority() >= minPriority)
                .count();
    }

    // PART B:
    // Return OPEN tickets for the specified team,
    // sorted by updatedAt ascending.
    // Do not modify the original list.
    public List<SupportTicket> getOpenTicketsByTeam(String teamId) {
        List<SupportTicket> lst=getAllTickets();
        return lst.stream().filter(ls->(ls.getStatus()==TicketStatus.OPEN))
                    .filter(st->Objects.equals(st.getTeamId(),teamId))
                    .sorted(Comparator.comparingLong(SupportTicket::getUpdatedAt))
                    .collect(Collectors.toList()); // TODO
    }

    // PART C:
    // Return teamId -> sum of priorities of OPEN tickets.
    // Omit teams that have no OPEN tickets.
    public Map<String, Integer> getOpenPriorityTotalsByTeam() {
        List<SupportTicket> lst=getAllTickets();
        return lst.stream().filter(st->st.getStatus()==TicketStatus.OPEN)
                .collect(Collectors.groupingBy(SupportTicket::getTeamId,Collectors.summingInt(SupportTicket::getPriority))); // TODO
    }

    // Existing helper method.
    public Set<String> getTeamsWithOpenTickets() {
        return tickets.stream()
                .filter(t -> t.getStatus() == TicketStatus.OPEN)
                .map(SupportTicket::getTeamId)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(TreeSet::new));
    }
}

public class TicketMonitorMock {
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

    static TicketMonitor createMonitor() {
        TicketMonitor monitor = new TicketMonitor();

        monitor.addTicket(new SupportTicket(
                "T-104", "TEAM-A", 3, TicketStatus.OPEN, 4000));

        monitor.addTicket(new SupportTicket(
                "T-102", "TEAM-A", 5, TicketStatus.OPEN, 3000));

        monitor.addTicket(new SupportTicket(
                "T-101", "TEAM-A", 5, TicketStatus.RESOLVED, 1000));

        monitor.addTicket(new SupportTicket(
                "T-103", "TEAM-A", 2, TicketStatus.OPEN, 2000));

        monitor.addTicket(new SupportTicket(
                "T-201", "TEAM-B", 4, TicketStatus.OPEN, 5000));

        monitor.addTicket(new SupportTicket(
                "T-202", "TEAM-B", 5, TicketStatus.OPEN, 6000));

        monitor.addTicket(new SupportTicket(
                "T-301", "TEAM-C", 5, TicketStatus.CANCELLED, 7000));

        return monitor;
    }

    // ==================== PART A TESTS ====================

    static void testOpenTicketsByPriority() {
        TicketMonitor monitor = createMonitor();

        checkEquals("A1: TEAM-A priority >= 3",
                2L, monitor.countOpenTicketsByPriority("TEAM-A", 3));

        checkEquals("A2: TEAM-B priority >= 4",
                2L, monitor.countOpenTicketsByPriority("TEAM-B", 4));

        checkEquals("A3: TEAM-A priority >= 5",
                1L, monitor.countOpenTicketsByPriority("TEAM-A", 5));

        checkEquals("A4: TEAM-A priority >= 0",
                3L, monitor.countOpenTicketsByPriority("TEAM-A", 0));

        checkEquals("A5: unknown team",
                0L, monitor.countOpenTicketsByPriority("TEAM-X", 1));
    }

    // ==================== PART B TESTS ====================

    static void testOpenTicketsByTeam() {
        TicketMonitor monitor = createMonitor();

        List<SupportTicket> result =
                monitor.getOpenTicketsByTeam("TEAM-A");

        checkEquals("B1: open ticket count", 3, result.size());

        if (result.size() >= 3) {
            checkEquals("B2: earliest ticket",
                    "T-103", result.get(0).getTicketId());

            checkEquals("B3: middle ticket",
                    "T-102", result.get(1).getTicketId());

            checkEquals("B4: latest ticket",
                    "T-104", result.get(2).getTicketId());
        }

        checkEquals("B5: team with no open tickets",
                0, monitor.getOpenTicketsByTeam("TEAM-C").size());

        checkEquals("B6: unknown team",
                0, monitor.getOpenTicketsByTeam("TEAM-X").size());

        checkEquals("B7: original ticket count unchanged",
                7, monitor.getAllTickets().size());
    }

    // ==================== PART C TESTS ====================

    static void testOpenPriorityTotalsByTeam() {
        TicketMonitor monitor = createMonitor();

        Map<String, Integer> result =
                monitor.getOpenPriorityTotalsByTeam();

        checkEquals("C1: TEAM-A priority total",
                10, result.get("TEAM-A"));

        checkEquals("C2: TEAM-B priority total",
                9, result.get("TEAM-B"));

        checkEquals("C3: number of teams included",
                2, result.size());

        checkEquals("C4: team without open tickets omitted",
                false, result.containsKey("TEAM-C"));

        TicketMonitor empty = new TicketMonitor();

        checkEquals("C5: empty monitor result",
                0, empty.getOpenPriorityTotalsByTeam().size());
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
        runTest("Part A", TicketMonitorMock::testOpenTicketsByPriority);

        System.out.println("\n========== PART B ==========");
        runTest("Part B", TicketMonitorMock::testOpenTicketsByTeam);

        System.out.println("\n========== PART C ==========");
        runTest("Part C", TicketMonitorMock::testOpenPriorityTotalsByTeam);

        System.out.println("\n========== FINAL SUMMARY ==========");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
    }
}
