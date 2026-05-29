package service;

import exceptions.CarNotFoundException;
import model.ServiceAppointment;
import model.ServiceAppointment.Status;
import repository.CarRepository;
import repository.ServiceAppointmentRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ServiceAppointmentService {
    private final ServiceAppointmentRepository appointmentRepository;
    private final CarRepository carRepository;

    public ServiceAppointmentService(CarService carService) {
        this(new ServiceAppointmentRepository(), new CarRepository());
    }

    public ServiceAppointmentService(ServiceAppointmentRepository appointmentRepository,
                                     CarRepository carRepository) {
        this.appointmentRepository = appointmentRepository;
        this.carRepository = carRepository;
    }

    public ServiceAppointment addServiceAppointment(int carId, LocalDate date, String description)
            throws CarNotFoundException {
        var car = carRepository.findById(carId)
                .orElseThrow(() -> new CarNotFoundException(carId));
        ServiceAppointment saved = appointmentRepository.insert(new ServiceAppointment(car, date, description));
        AuditService.getInstance().logAction("ADD_SERVICE_APPOINTMENT");
        return saved;
    }

    public void markAsCompleted(int appointmentId) {
        ServiceAppointment appointment = findAppointmentById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Service appointment with ID " + appointmentId + " does not exist."));
        appointment.setStatus(Status.DONE);
        appointmentRepository.update(appointment);
        AuditService.getInstance().logAction("COMPLETE_SERVICE_APPOINTMENT");
    }

    public List<ServiceAppointment> getAppointmentsByStatus(Status status) {
        return appointmentRepository.findByStatus(status);
    }

    public Optional<ServiceAppointment> findAppointmentById(int appointmentId) {
        return appointmentRepository.findById(appointmentId);
    }

    public List<ServiceAppointment> getAppointments() {
        return appointmentRepository.findAll();
    }
}
