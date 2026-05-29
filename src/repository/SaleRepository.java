package repository;

import exceptions.DatabaseException;
import model.Car;
import model.Client;
import model.Sale;
import model.Salesperson;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class SaleRepository extends GenericRepository<Sale> {
    private final CarRepository carRepository = new CarRepository();
    private final ClientRepository clientRepository = new ClientRepository();
    private final EmployeeRepository employeeRepository = new EmployeeRepository();

    private static final String BASE_SELECT = """
            SELECT id, car_id, client_id, salesperson_id, date, final_price, cancelled
            FROM sales
            """;

    @Override
    public Optional<Sale> findById(int id) {
        return queryOne(BASE_SELECT + " WHERE id = ?", id);
    }

    @Override
    public List<Sale> findAll() {
        return query(BASE_SELECT + " ORDER BY id");
    }

    public List<Sale> findByClientId(int clientId) {
        return query(BASE_SELECT + " WHERE client_id = ? ORDER BY date DESC", clientId);
    }

    public List<Sale> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return query(
                BASE_SELECT + " WHERE date >= ? AND date <= ? ORDER BY date",
                Date.valueOf(startDate),
                Date.valueOf(endDate)
        );
    }

    @Override
    public Sale insert(Sale sale) {
        int id = executeUpdateReturningId(
                """
                INSERT INTO sales (car_id, client_id, salesperson_id, date, final_price, cancelled)
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                sale.getCar().getId(),
                sale.getClient().getId(),
                sale.getSalesperson().getId(),
                Date.valueOf(sale.getDate()),
                sale.getFinalPrice(),
                sale.isCancelled()
        );
        return new Sale(id, sale.getClient(), sale.getCar(), sale.getSalesperson(),
                sale.getDate(), sale.getFinalPrice(), sale.isCancelled());
    }

    @Override
    public Sale update(Sale sale) {
        executeUpdate(
                """
                UPDATE sales
                SET car_id = ?, client_id = ?, salesperson_id = ?, date = ?, final_price = ?, cancelled = ?
                WHERE id = ?
                """,
                sale.getCar().getId(),
                sale.getClient().getId(),
                sale.getSalesperson().getId(),
                Date.valueOf(sale.getDate()),
                sale.getFinalPrice(),
                sale.isCancelled(),
                sale.getId()
        );
        return findById(sale.getId())
                .orElseThrow(() -> new DatabaseException("Sale not found after update"));
    }

    @Override
    public void deleteById(int id) {
        executeUpdate("DELETE FROM sales WHERE id = ?", id);
    }

    @Override
    protected Sale mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int carId = rs.getInt("car_id");
        int clientId = rs.getInt("client_id");
        int salespersonId = rs.getInt("salesperson_id");
        LocalDate date = rs.getDate("date").toLocalDate();
        double finalPrice = rs.getDouble("final_price");
        boolean cancelled = rs.getBoolean("cancelled");

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new DatabaseException("Car " + carId + " not found for sale " + id));
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new DatabaseException("Client " + clientId + " not found for sale " + id));
        Salesperson salesperson = employeeRepository.findById(salespersonId)
                .orElseThrow(() -> new DatabaseException("Salesperson " + salespersonId + " not found for sale " + id));

        return new Sale(id, client, car, salesperson, date, finalPrice, cancelled);
    }
}
