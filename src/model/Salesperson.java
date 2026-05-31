package model;

import java.time.LocalDate;

public class Salesperson extends Employee {
    private double commissionPercent;

    public Salesperson(String name, double salary, LocalDate hireDate, double commissionPercent) {
        super(name, salary, hireDate);
        this.commissionPercent = commissionPercent;
    }

    public Salesperson(int id, String name, double salary, LocalDate hireDate,
                       double commissionPercent, boolean active) {
        super(id, name, salary, hireDate, active);
        this.commissionPercent = commissionPercent;
    }

    public double getCommissionPercent() {
        return commissionPercent;
    }

    public void setCommissionPercent(double commissionPercent) {
        this.commissionPercent = commissionPercent;
    }

    @Override
    public String toString() {
        return String.format(
                "Salesperson{id=%d, name='%s', salary=%.2f, commission=%.1f%%, active=%s}",
                getId(), getName(), getSalary(), commissionPercent, isActive()
        );
    }
}
