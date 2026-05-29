package model;

import java.time.LocalDate;

public class Sale {
    private static int idCounter = 1;

    private final int id;
    private Client client;
    private Car car;
    private Salesperson salesperson;
    private LocalDate date;
    private double finalPrice;
    private boolean cancelled;

    public Sale(Client client, Car car, Salesperson salesperson, LocalDate date, double finalPrice) {
        this.id = idCounter++;
        this.client = client;
        this.car = car;
        this.salesperson = salesperson;
        this.date = date;
        this.finalPrice = finalPrice;
        this.cancelled = false;
    }

    public int getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Salesperson getSalesperson() {
        return salesperson;
    }

    public void setSalesperson(Salesperson salesperson) {
        this.salesperson = salesperson;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public String toString() {
        return String.format(
                "Sale{id=%d, client='%s', car=%s, salesperson='%s', date=%s, finalPrice=%.2f, cancelled=%s}",
                id,
                client.getName(),
                car.getBrand() + " " + car.getModel(),
                salesperson != null ? salesperson.getName() : "N/A",
                date,
                finalPrice,
                cancelled
        );
    }
}
