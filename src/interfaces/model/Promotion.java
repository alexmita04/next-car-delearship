package model;

public class Promotion {
    private int id;
    private Car car;
    private double discountPercent;
    private boolean active;

    public Promotion(Car car, double discountPercent) {
        this(0, car, discountPercent, true);
    }

    public Promotion(int id, Car car, double discountPercent, boolean active) {
        this.id = id;
        this.car = car;
        this.discountPercent = discountPercent;
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    @Override
    public String toString() {
        return String.format(
                "Promotion{car=%s %s, discount=%.1f%%}",
                car.getBrand(), car.getModel(), discountPercent
        );
    }
}
