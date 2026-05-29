package repository;

import exceptions.DatabaseException;
import model.Car;
import model.NewCar;
import model.UsedCar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CarRepository extends GenericRepository<Car> {
    private static final String BASE_SELECT = """
            SELECT c.id, c.type, c.brand, c.model, c.year, c.price, c.available,
                   nc.warranty_months, uc.km, uc.previous_owners,
                   COALESCE((
                       SELECT p.discount_percent FROM promotions p
                       WHERE p.car_id = c.id AND p.active = TRUE
                       ORDER BY p.id DESC LIMIT 1
                   ), 0) AS discount_percent
            FROM cars c
            LEFT JOIN new_cars nc ON nc.car_id = c.id
            LEFT JOIN used_cars uc ON uc.car_id = c.id
            """;

    @Override
    public Optional<Car> findById(int id) {
        return queryOne(BASE_SELECT + " WHERE c.id = ?", id);
    }

    @Override
    public List<Car> findAll() {
        return query(BASE_SELECT + " ORDER BY c.price, c.id");
    }

    public List<Car> search(String brand, Double minPrice, Double maxPrice, Integer year) {
        StringBuilder sql = new StringBuilder(BASE_SELECT + " WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (brand != null) {
            sql.append(" AND LOWER(c.brand) = LOWER(?)");
            params.add(brand);
        }
        if (minPrice != null) {
            sql.append(" AND c.price >= ?");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append(" AND c.price <= ?");
            params.add(maxPrice);
        }
        if (year != null) {
            sql.append(" AND c.year = ?");
            params.add(year);
        }
        sql.append(" ORDER BY c.price, c.id");

        return query(sql.toString(), params.toArray());
    }

    public List<Car> findAvailableSortedByPrice() {
        return query(BASE_SELECT + " WHERE c.available = TRUE ORDER BY c.price, c.id");
    }

    @Override
    public Car insert(Car car) {
        String type = car instanceof NewCar ? "NEW" : "USED";
        int id = executeUpdateReturningId(
                "INSERT INTO cars (type, brand, model, year, price, available) VALUES (?::car_type, ?, ?, ?, ?, ?)",
                type, car.getBrand(), car.getModel(), car.getYear(), car.getPrice(), car.isAvailable()
        );

        if (car instanceof NewCar newCar) {
            executeUpdate(
                    "INSERT INTO new_cars (car_id, warranty_months) VALUES (?, ?)",
                    id, newCar.getWarrantyYears() * 12
            );
            return new NewCar(id, car.getBrand(), car.getModel(), car.getYear(), car.getPrice(),
                    car.getCondition(), newCar.getWarrantyYears(), car.isAvailable(), 0);
        }

        UsedCar usedCar = (UsedCar) car;
        executeUpdate(
                "INSERT INTO used_cars (car_id, km, previous_owners) VALUES (?, ?, ?)",
                id, usedCar.getKilometers(), usedCar.getNumberOfOwners()
        );
        return new UsedCar(id, car.getBrand(), car.getModel(), car.getYear(), car.getPrice(),
                car.getCondition(), usedCar.getKilometers(), usedCar.getNumberOfOwners(),
                car.isAvailable(), 0);
    }

    @Override
    public Car update(Car car) {
        executeUpdate(
                "UPDATE cars SET brand = ?, model = ?, year = ?, price = ?, available = ? WHERE id = ?",
                car.getBrand(), car.getModel(), car.getYear(), car.getPrice(), car.isAvailable(), car.getId()
        );

        if (car instanceof UsedCar usedCar) {
            executeUpdate(
                    "UPDATE used_cars SET km = ?, previous_owners = ? WHERE car_id = ?",
                    usedCar.getKilometers(), usedCar.getNumberOfOwners(), car.getId()
            );
        }

        return findById(car.getId()).orElseThrow(() -> new DatabaseException("Car not found after update"));
    }

    @Override
    public void deleteById(int id) {
        executeUpdate("DELETE FROM cars WHERE id = ?", id);
    }

    @Override
    protected Car mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String type = rs.getString("type");
        String brand = rs.getString("brand");
        String model = rs.getString("model");
        int year = rs.getInt("year");
        double price = rs.getDouble("price");
        boolean available = rs.getBoolean("available");
        double discountPercent = rs.getDouble("discount_percent");

        if ("NEW".equals(type)) {
            int warrantyMonths = rs.getInt("warranty_months");
            int warrantyYears = warrantyMonths / 12;
            return new NewCar(id, brand, model, year, price, "New", warrantyYears, available, discountPercent);
        }

        int km = rs.getInt("km");
        int previousOwners = rs.getInt("previous_owners");
        return new UsedCar(id, brand, model, year, price, "Used", km, previousOwners, available, discountPercent);
    }

    public void setAvailability(int carId, boolean available) {
        executeUpdate("UPDATE cars SET available = ? WHERE id = ?", available, carId);
    }
}
