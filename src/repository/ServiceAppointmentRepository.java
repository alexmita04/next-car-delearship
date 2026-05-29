package repository;

import exceptions.DatabaseException;
import model.Car;
import model.ServiceAppointment;
import model.ServiceAppointment.Status;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ServiceAppointmentRepository extends GenericRepository<ServiceAppointment> {
    private final CarRepository carRepository = new CarRepository();

    private static final String BASE_SELECT = """
            SELECT id, car_id, date, description, status
            FROM service_appts
            """;

    @Override
    public Optional<ServiceAppointment> findById(int id) {
        return queryOne(BASE_SELECT + " WHERE id = ?", id);
    }

    @Override
    public List<ServiceAppointment> findAll() {
        return query(BASE_SELECT + " ORDER BY date, id");
    }

    public List<ServiceAppointment> findByStatus(Status status) {
        return query(BASE_SELECT + " WHERE status = ?::appointment_status ORDER BY date, id",
                status.name());
    }

    @Override
    public ServiceAppointment insert(ServiceAppointment appointment) {
        int id = executeUpdateReturningId(
                """
                INSERT INTO service_appts (car_id, date, description, status)
                VALUES (?, ?, ?, ?::appointment_status)
                """,
                appointment.getCar().getId(),
                Date.valueOf(appointment.getDate()),
                appointment.getDescription(),
                appointment.getStatus().name()
        );
        return new ServiceAppointment(id, appointment.getCar(), appointment.getDate(),
                appointment.getDescription(), appointment.getStatus());
    }

    @Override
    public ServiceAppointment update(ServiceAppointment appointment) {
        executeUpdate(
                """
                UPDATE service_appts
                SET car_id = ?, date = ?, description = ?, status = ?::appointment_status
                WHERE id = ?
                """,
                appointment.getCar().getId(),
                Date.valueOf(appointment.getDate()),
                appointment.getDescription(),
                appointment.getStatus().name(),
                appointment.getId()
        );
        return findById(appointment.getId())
                .orElseThrow(() -> new DatabaseException("Appointment not found after update"));
    }

    @Override
    public void deleteById(int id) {
        executeUpdate("DELETE FROM service_appts WHERE id = ?", id);
    }

    public void deleteByCarId(int carId) {
        executeUpdate("DELETE FROM service_appts WHERE car_id = ?", carId);
    }

    @Override
    protected ServiceAppointment mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int carId = rs.getInt("car_id");
        LocalDate date = rs.getDate("date").toLocalDate();
        String description = rs.getString("description");
        Status status = Status.valueOf(rs.getString("status"));

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new DatabaseException("Car " + carId + " not found for appointment " + id));

        return new ServiceAppointment(id, car, date, description, status);
    }
}
