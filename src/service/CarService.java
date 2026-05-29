package service;

import exceptions.CarNotFoundException;
import exceptions.InvalidPromotionException;
import model.Car;
import model.Promotion;
import repository.CarRepository;
import repository.PromotionRepository;

import java.util.List;

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
        return carRepository.insert(car);
    }

    public void removeCar(int carId) throws CarNotFoundException {
        findCarById(carId);
        carRepository.deleteById(carId);
    }

    public void updatePrice(int carId, double newPrice) throws CarNotFoundException {
        Car car = findCarById(carId);
        car.setPrice(newPrice);
        carRepository.update(car);
    }

    public List<Car> searchCars(String brand, Double minPrice, Double maxPrice, Integer year) {
        return carRepository.search(brand, minPrice, maxPrice, year);
    }

    public Promotion applyPromotion(int carId, double discountPercent)
            throws CarNotFoundException, InvalidPromotionException {
        Car car = findCarById(carId);
        if (discountPercent < 0 || discountPercent > 100) {
            throw new InvalidPromotionException(discountPercent);
        }
        Promotion promotion = promotionRepository.insert(new Promotion(car, discountPercent));
        car.applyDiscount(discountPercent);
        return promotion;
    }

    public List<Car> listAvailableCarsSortedByPrice() {
        return carRepository.findAvailableSortedByPrice();
    }

    public Car findCarById(int carId) throws CarNotFoundException {
        return carRepository.findById(carId)
                .orElseThrow(() -> new CarNotFoundException(carId));
    }

    public List<Car> getInventory() {
        return carRepository.findAll();
    }
}
