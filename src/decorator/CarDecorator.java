package decorator;

import exceptions.InvalidPromotionException;
import interfaces.Discountable;
import model.Car;

public abstract class CarDecorator implements Discountable {
    protected final Car car;

    protected CarDecorator(Car car) {
        this.car = car;
    }

    public Car getCar() {
        return car;
    }

    public int getId() {
        return car.getId();
    }

    public String getBrand() {
        return car.getBrand();
    }

    public String getModel() {
        return car.getModel();
    }

    public int getYear() {
        return car.getYear();
    }

    public double getPrice() {
        return car.getPrice();
    }

    public String getCondition() {
        return car.getCondition();
    }

    public boolean isAvailable() {
        return car.isAvailable();
    }

    @Override
    public void applyDiscount(double percent) throws InvalidPromotionException {
        car.applyDiscount(percent);
    }

    @Override
    public double getDiscountedPrice() {
        return car.getDiscountedPrice();
    }

    @Override
    public double getDiscountPercent() {
        return car.getDiscountPercent();
    }
}
