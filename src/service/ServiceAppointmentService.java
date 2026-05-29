package service;

import exceptions.CarNotFoundException;
import model.Car;
import model.ServiceAppointment;
import model.ServiceAppointment.Status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ServiceAppointmentService {
    private final List<ServiceAppointment> appointments = new ArrayList<>();
    private final CarService carService;

    public ServiceAppointmentService(CarService carService) {
        this.carService = carService;
    }

    /** Action 12: Add a service appointment for a car */
    public ServiceAppointment addServiceAppointment(int carId, LocalDate date, String description)
            throws CarNotFoundException {
        Car car = carService.findCarById(carId);
        ServiceAppointment appointment = new ServiceAppointment(car, date, description);
        appointments.add(appointment);
        return appointment;
    }

    /** Action 13: Mark a service appointment as completed */
    public void markAsCompleted(int appointmentId) {
        ServiceAppointment appointment = findAppointmentById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Service appointment with ID " + appointmentId + " does not exist."));
        appointment.setStatus(Status.COMPLETED);
    }

    public List<ServiceAppointment> getAppointmentsByStatus(Status status) {
        return appointments.stream()
                .filter(a -> a.getStatus() == status)
                .collect(Collectors.toList());
    }

    public Optional<ServiceAppointment> findAppointmentById(int appointmentId) {
        return appointments.stream()
                .filter(a -> a.getId() == appointmentId)
                .findFirst();
    }

    public List<ServiceAppointment> getAppointments() {
        return new ArrayList<>(appointments);
    }
}
