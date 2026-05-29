package repository;

import exceptions.DatabaseException;
import model.Salesperson;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class EmployeeRepository extends GenericRepository<Salesperson> {
    private static final String BASE_SELECT = """
            SELECT e.id, e.name, e.salary, e.hire_date, e.active, s.commission_rate
            FROM employees e
            JOIN salespersons s ON s.employee_id = e.id
            WHERE e.type = 'SALESPERSON'
            """;

    @Override
    public Optional<Salesperson> findById(int id) {
        return queryOne(BASE_SELECT + " AND e.id = ?", id);
    }

    @Override
    public List<Salesperson> findAll() {
        return query(BASE_SELECT + " ORDER BY e.id");
    }

    public List<Salesperson> findActive() {
        return query(BASE_SELECT + " AND e.active = TRUE ORDER BY e.id");
    }

    @Override
    public Salesperson insert(Salesperson salesperson) {
        int id = executeUpdateReturningId(
                "INSERT INTO employees (type, name, salary, hire_date, active) VALUES ('SALESPERSON', ?, ?, ?, ?)",
                salesperson.getName(),
                salesperson.getSalary(),
                Date.valueOf(salesperson.getHireDate()),
                salesperson.isActive()
        );
        executeUpdate(
                "INSERT INTO salespersons (employee_id, commission_rate) VALUES (?, ?)",
                id, salesperson.getCommissionPercent()
        );
        return new Salesperson(id, salesperson.getName(), salesperson.getSalary(),
                salesperson.getHireDate(), salesperson.getCommissionPercent(), salesperson.isActive());
    }

    @Override
    public Salesperson update(Salesperson salesperson) {
        executeUpdate(
                "UPDATE employees SET name = ?, salary = ?, hire_date = ?, active = ? WHERE id = ?",
                salesperson.getName(),
                salesperson.getSalary(),
                Date.valueOf(salesperson.getHireDate()),
                salesperson.isActive(),
                salesperson.getId()
        );
        executeUpdate(
                "UPDATE salespersons SET commission_rate = ? WHERE employee_id = ?",
                salesperson.getCommissionPercent(),
                salesperson.getId()
        );
        return findById(salesperson.getId())
                .orElseThrow(() -> new DatabaseException("Employee not found after update"));
    }

    @Override
    public void deleteById(int id) {
        executeUpdate("DELETE FROM employees WHERE id = ?", id);
    }

    public void setActive(int employeeId, boolean active) {
        executeUpdate("UPDATE employees SET active = ? WHERE id = ?", active, employeeId);
    }

    @Override
    protected Salesperson mapRow(ResultSet rs) throws SQLException {
        return new Salesperson(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getDouble("salary"),
                rs.getDate("hire_date").toLocalDate(),
                rs.getDouble("commission_rate"),
                rs.getBoolean("active")
        );
    }
}
