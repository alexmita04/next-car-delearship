package service;

import exceptions.CarNotFoundException;
import exceptions.InvalidPromotionException;
import model.Car;
import model.Promotion;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class CarService {
    private final List<Car> inventory = new ArrayList<>();
    private final TreeSet<Car> sortedByPrice = new TreeSet<>();

    /** Action 1: Add a new car to inventory */
    public void addCar(Car car) {
        inventory.add(car);
        sortedByPrice.add(car);
    }

    /** Action 2: Remove a car from inventory */
    public void removeCar(int carId) throws CarNotFoundException {
        Car car = findCarById(carId);
        inventory.remove(car);
        sortedByPrice.remove(car);
    }

    /** Action 3: Update a car's price */
    public void updatePrice(int carId, double newPrice) throws CarNotFoundException {
        Car car = findCarById(carId);
        sortedByPrice.remove(car);
        car.setPrice(newPrice);
        sortedByPrice.add(car);
    }

    /** Action 4: Search cars by brand / price / year */
    public List<Car> searchCars(String brand, Double minPrice, Double maxPrice, Integer year) {
        return inventory.stream()
                .filter(car -> brand == null || car.getBrand().equalsIgnoreCase(brand))
                .filter(car -> minPrice == null || car.getPrice() >= minPrice)
                .filter(car -> maxPrice == null || car.getPrice() <= maxPrice)
                .filter(car -> year == null || car.getYear() == year)
                .collect(Collectors.toList());
    }

    /** Action 15: Apply a promotional discount to a car */
    public Promotion applyPromotion(int carId, double discountPercent)
            throws CarNotFoundException, InvalidPromotionException {
        Car car = findCarById(carId);
        car.applyDiscount(discountPercent);
        return new Promotion(car, discountPercent);
    }

    /** Action 16: List all available cars sorted by price */
    public List<Car> listAvailableCarsSortedByPrice() {
        return sortedByPrice.stream()
                .filter(Car::isAvailable)
                .collect(Collectors.toList());
    }

    public Car findCarById(int carId) throws CarNotFoundException {
        return inventory.stream()
                .filter(car -> car.getId() == carId)
                .findFirst()
                .orElseThrow(() -> new CarNotFoundException(carId));
    }

    public List<Car> getInventory() {
        return new ArrayList<>(inventory);
    }
}
