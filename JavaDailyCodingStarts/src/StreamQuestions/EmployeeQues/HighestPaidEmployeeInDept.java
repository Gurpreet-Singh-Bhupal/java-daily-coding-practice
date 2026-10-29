package StreamQuestions.EmployeeQues;

import java.util.*;
import java.util.stream.*;

class Employee {
    int id;
    String employeeName;
    String department;
    int salary;
    List<String> skills;

    Employee(int id, String employeeName, String department,
             int salary, List<String> skills) {
        this.id = id;
        this.employeeName = employeeName;
        this.department = department;
        this.salary = salary;
        this.skills = skills;
    }
}

public class HighestPaidEmployeeInDept {
    public static void main(String[] args) {

        List<Employee> emps = List.of(
                new Employee(1, "Gurpreet", "Engineering", 90000,
                        List.of("Java", "Spring")),
                new Employee(2, "Amit", "Engineering", 85000,
                        List.of("Java", "AWS")),
                new Employee(3, "Sara", "HR", 60000,
                        List.of("Hiring", "Payroll")),
                new Employee(4, "Neha", "HR", 65000,
                        List.of("Hiring")),
                new Employee(5, "Vikram", "Sales", 70000,
                        List.of("Negotiation")),
                new Employee(6, "Rohan", "Engineering", 95000,
                        List.of("Java", "Spring", "AWS"))
        );

        Map<String, String> result = highestPaidEmployeePerDept(emps);

        result.forEach((department, name) ->
                System.out.println(department + " -> " + name)
        );
    }

    public static Map<String, String> highestPaidEmployeePerDept(List<Employee> emps) {

        // Write your logic here

        return emps.stream().collect(Collectors.groupingBy(e->e.department,Collectors.collectingAndThen(Collectors.maxBy(Comparator.comparingInt((Employee e)->e.salary)),optional->optional.get().employeeName)));

    }
}
