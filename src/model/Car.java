package model;

import exceptions.InvalidPromotionException;
import interfaces.Discountable;

public abstract class Car implements Discountable, Comparable<Car> {
    private static int idCounter = 1;

    private final int id;
    private String brand;
    private String model;
    private int year;
    private double price;
    private String condition;
    private boolean available;
    private double discountPercent;

    protected Car(String brand, String model, int year, double price, String condition) {
        this.id = idCounter++;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.price = price;
        this.condition = condition;
        this.available = true;
        this.discountPercent = 0;
    }

    public int getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public void applyDiscount(double percent) throws InvalidPromotionException {
        if (percent < 0 || percent > 100) {
            throw new InvalidPromotionException(percent);
        }
        this.discountPercent = percent;
    }

    @Override
    public double getDiscountedPrice() {
        return price * (1 - discountPercent / 100);
    }

    @Override
    public double getDiscountPercent() {
        return discountPercent;
    }

    @Override
    public int compareTo(Car other) {
        int priceComparison = Double.compare(this.price, other.price);
        if (priceComparison != 0) {
            return priceComparison;
        }
        return Integer.compare(this.id, other.id);
    }

    @Override
    public String toString() {
        return String.format(
                "Car{id=%d, brand='%s', model='%s', year=%d, price=%.2f, condition='%s', available=%s, discount=%.1f%%}",
                id, brand, model, year, price, condition, available, discountPercent
        );
    }
}
