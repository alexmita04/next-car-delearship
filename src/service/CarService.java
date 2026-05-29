package service;

import decorator.PromotionDecorator;
import exceptions.CarNotFoundException;
import exceptions.InvalidPromotionException;
import model.Car;
import model.Promotion;
import repository.CarRepository;
import repository.PromotionRepository;

import java.util.List;
import java.util.stream.Collectors;

public class CarService {
    private final CarRepository carRepository;
    private final PromotionRepository promotionRepository;

    public CarService() {
        this(new CarRepository(), new PromotionRepository());
    }

    public CarService(CarRepository carRepository, PromotionRepository promotionRepository) {
        this.carRepository = carRepository;
        this.promotionRepository = promotionRepository;
    }

    public Car addCar(Car car) {
        Car saved = carRepository.insert(car);
        AuditService.getInstance().logAction("ADD_CAR");
        return saved;
    }

    public void removeCar(int carId) throws CarNotFoundException {
        findCarById(carId);
        carRepository.deleteById(carId);
        AuditService.getInstance().logAction("REMOVE_CAR");
    }

    public void updatePrice(int carId, double newPrice) throws CarNotFoundException {
        Car car = findCarById(carId);
        car.setPrice(newPrice);
        carRepository.update(car);
        AuditService.getInstance().logAction("UPDATE_PRICE");
    }

    public List<Car> searchCars(String brand, Double minPrice, Double maxPrice, Integer year) {
        List<Car> results = carRepository.search(brand, minPrice, maxPrice, year);
        AuditService.getInstance().logAction("SEARCH_CARS");
        return results;
    }

    public Promotion applyPromotion(int carId, double discountPercent)
            throws CarNotFoundException, InvalidPromotionException {
        Car car = findCarById(carId);
        if (discountPercent < 0 || discountPercent > 100) {
            throw new InvalidPromotionException(discountPercent);
        }
        Promotion promotion = promotionRepository.insert(new Promotion(car, discountPercent));
        car.applyDiscount(discountPercent);
        AuditService.getInstance().logAction("APPLY_PROMOTION");
        return promotion;
    }

    public List<Car> listAvailableCarsSortedByPrice() {
        List<Car> cars = carRepository.findAvailableSortedByPrice();
        AuditService.getInstance().logAction("LIST_AVAILABLE_CARS");
        return cars;
    }

    public List<String> listAvailableCarsWithPromotionDisplay() {
        return carRepository.findAvailableSortedByPrice().stream()
                .map(car -> car.getDiscountPercent() > 0
                        ? new PromotionDecorator(car).toString()
                        : car.toString())
                .collect(Collectors.toList());
    }

    public Car findCarById(int carId) throws CarNotFoundException {
        return carRepository.findById(carId)
                .orElseThrow(() -> new CarNotFoundException(carId));
    }

    public List<Car> getInventory() {
        return carRepository.findAll();
    }
}
