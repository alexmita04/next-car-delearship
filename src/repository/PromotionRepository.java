package repository;

import exceptions.DatabaseException;
import model.Car;
import model.Promotion;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class PromotionRepository extends GenericRepository<Promotion> {
    private final CarRepository carRepository = new CarRepository();

    private static final String BASE_SELECT = """
            SELECT id, car_id, discount_percent, active
            FROM promotions
            """;

    @Override
    public Optional<Promotion> findById(int id) {
        return queryOne(BASE_SELECT + " WHERE id = ?", id);
    }

    @Override
    public List<Promotion> findAll() {
        return query(BASE_SELECT + " ORDER BY id");
    }

    public Optional<Promotion> findActiveByCarId(int carId) {
        return queryOne(BASE_SELECT + " WHERE car_id = ? AND active = TRUE", carId);
    }

    public List<Promotion> findByCarId(int carId) {
        return query(BASE_SELECT + " WHERE car_id = ? ORDER BY id", carId);
    }

    @Override
    public Promotion insert(Promotion promotion) {
        deactivateExisting(promotion.getCar().getId());
        int id = executeUpdateReturningId(
                "INSERT INTO promotions (car_id, discount_percent, active) VALUES (?, ?, ?)",
                promotion.getCar().getId(),
                promotion.getDiscountPercent(),
                promotion.isActive()
        );
        return new Promotion(id, promotion.getCar(), promotion.getDiscountPercent(), promotion.isActive());
    }

    @Override
    public Promotion update(Promotion promotion) {
        executeUpdate(
                "UPDATE promotions SET car_id = ?, discount_percent = ?, active = ? WHERE id = ?",
                promotion.getCar().getId(),
                promotion.getDiscountPercent(),
                promotion.isActive(),
                promotion.getId()
        );
        return findById(promotion.getId())
                .orElseThrow(() -> new DatabaseException("Promotion not found after update"));
    }

    @Override
    public void deleteById(int id) {
        executeUpdate("DELETE FROM promotions WHERE id = ?", id);
    }

    public void deactivateExisting(int carId) {
        executeUpdate("UPDATE promotions SET active = FALSE WHERE car_id = ? AND active = TRUE", carId);
    }

    public void deleteByCarId(int carId) {
        executeUpdate("DELETE FROM promotions WHERE car_id = ?", carId);
    }

    @Override
    protected Promotion mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int carId = rs.getInt("car_id");
        double discountPercent = rs.getDouble("discount_percent");
        boolean active = rs.getBoolean("active");

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new DatabaseException("Car " + carId + " not found for promotion " + id));

        return new Promotion(id, car, discountPercent, active);
    }
}
