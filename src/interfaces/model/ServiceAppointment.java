package model;

import java.time.LocalDate;

public class ServiceAppointment {
    private static int idCounter = 1;

    public enum Status {
        PENDING,
        DONE
    }

    private final int id;
    private Car car;
    private LocalDate date;
    private String description;
    private Status status;

    public ServiceAppointment(Car car, LocalDate date, String description) {
        this(idCounter++, car, date, description, Status.PENDING);
    }

    public ServiceAppointment(int id, Car car, LocalDate date, String description, Status status) {
        this.id = id;
        this.car = car;
        this.date = date;
        this.description = description;
        this.status = status;
        if (id >= idCounter) {
            idCounter = id + 1;
        }
    }

    public int getId() {
        return id;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format(
                "ServiceAppointment{id=%d, car=%s %s, date=%s, description='%s', status=%s}",
                id, car.getBrand(), car.getModel(), date, description, status
        );
    }
}
