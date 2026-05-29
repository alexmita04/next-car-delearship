package model;

public class Promotion {
    private Car car;
    private double discountPercent;

    public Promotion(Car car, double discountPercent) {
        this.car = car;
        this.discountPercent = discountPercent;
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
