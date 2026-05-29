package model;

import java.time.LocalDate;

public abstract class Employee {
    private static int idCounter = 1;

    private final int id;
    private String name;
    private double salary;
    private LocalDate hireDate;
    private boolean active;

    protected Employee(String name, double salary, LocalDate hireDate) {
        this.id = idCounter++;
        this.name = name;
        this.salary = salary;
        this.hireDate = hireDate;
        this.active = true;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return String.format(
                "Employee{id=%d, name='%s', salary=%.2f, hireDate=%s, active=%s}",
                id, name, salary, hireDate, active
        );
    }
}
