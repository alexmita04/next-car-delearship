package builder;

import factory.CarType;
import model.Car;

public class CarBuilder {
    private CarType type;
    private String brand;
    private String model;
    private int year;
    private double price;
    private String condition;
    private boolean available = true;
    private double discountPercent = 0;

    private int warrantyYears = 3;
    private int kilometers = 0;
    private int numberOfOwners = 1;

    private CarBuilder(CarType type) {
        this.type = type;
    }

    public static CarBuilder forNewCar() {
        return new CarBuilder(CarType.NEW);
    }

    public static CarBuilder forUsedCar() {
        return new CarBuilder(CarType.USED);
    }

    public CarBuilder type(CarType type) {
        this.type = type;
        return this;
    }

    public CarBuilder brand(String brand) {
        this.brand = brand;
        return this;
    }

    public CarBuilder model(String model) {
        this.model = model;
        return this;
    }

    public CarBuilder year(int year) {
        this.year = year;
        return this;
    }

    public CarBuilder price(double price) {
        this.price = price;
        return this;
    }

    public CarBuilder condition(String condition) {
        this.condition = condition;
        return this;
    }

    public CarBuilder available(boolean available) {
        this.available = available;
        return this;
    }

    public CarBuilder discountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
        return this;
    }

    public CarBuilder warrantyYears(int warrantyYears) {
        this.warrantyYears = warrantyYears;
        return this;
    }

    public CarBuilder kilometers(int kilometers) {
        this.kilometers = kilometers;
        return this;
    }

    public CarBuilder numberOfOwners(int numberOfOwners) {
        this.numberOfOwners = numberOfOwners;
        return this;
    }

    public Car build() {
        validateRequiredFields();
        return factory.CarFactory.create(type, this);
    }

    private void validateRequiredFields() {
        if (type == null) {
            throw new IllegalStateException("Car type is required.");
        }
        if (brand == null || brand.isBlank()) {
            throw new IllegalStateException("Brand is required.");
        }
        if (model == null || model.isBlank()) {
            throw new IllegalStateException("Model is required.");
        }
        if (year <= 0) {
            throw new IllegalStateException("Year must be positive.");
        }
        if (price <= 0) {
            throw new IllegalStateException("Price must be positive.");
        }
        if (condition == null || condition.isBlank()) {
            throw new IllegalStateException("Condition is required.");
        }
    }

    public CarType getType() {
        return type;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public double getPrice() {
        return price;
    }

    public String getCondition() {
        return condition;
    }

    public boolean isAvailable() {
        return available;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public int getWarrantyYears() {
        return warrantyYears;
    }

    public int getKilometers() {
        return kilometers;
    }

    public int getNumberOfOwners() {
        return numberOfOwners;
    }
}
