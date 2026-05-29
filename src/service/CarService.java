package service;

import decorator.PromotionDecorator;
import exceptions.CarDeletionException;
import exceptions.CarNotFoundException;
import exceptions.InvalidPromotionException;
import model.Car;
import model.Promotion;
import repository.CarRepository;
import repository.PromotionRepository;
import repository.SaleRepository;
import repository.ServiceAppointmentRepository;

import java.util.List;
import java.util.stream.Collectors;

public class CarService {
    private final CarRepository carRepository;
    private final PromotionRepository promotionRepository;
    private final SaleRepository saleRepository;
    private final ServiceAppointmentRepository serviceAppointmentRepository;

    public CarService() {
        this(new CarRepository(), new PromotionRepository(), new SaleRepository(), new ServiceAppointmentRepository());
    }

    public CarService(CarRepository carRepository, PromotionRepository promotionRepository) {
        this(carRepository, promotionRepository, new SaleRepository(), new ServiceAppointmentRepository());
    }

    public CarService(CarRepository carRepository, PromotionRepository promotionRepository,
                      SaleRepository saleRepository, ServiceAppointmentRepository serviceAppointmentRepository) {
        this.carRepository = carRepository;
        this.promotionRepository = promotionRepository;
        this.saleRepository = saleRepository;
        this.serviceAppointmentRepository = serviceAppointmentRepository;
    }

    public Car addCar(Car car) {
        Car saved = carRepository.insert(car);
        AuditService.getInstance().logAction("ADD_CAR");
        return saved;
    }

    public void removeCar(int carId) throws CarNotFoundException, CarDeletionException {
        findCarById(carId);
        if (saleRepository.hasSalesForCar(carId)) {
            throw new CarDeletionException(
                    "Mașina nu poate fi ștearsă deoarece are vânzări înregistrate.");
        }
        promotionRepository.deleteByCarId(carId);
        serviceAppointmentRepository.deleteByCarId(carId);
        carRepository.deleteById(carId);
        AuditService.getInstance().logAction("REMOVE_CAR");
    }

    public void updatePrice(int carId, double newPrice) throws CarNotFoundException {
        Car car = findCarById(carId);
        car.setPrice(newPrice);
        carRepository.update(car);
        AuditService.getInstance().logAction("UPDATE_PRICE");
    }

    public Car updateCar(Car car) throws CarNotFoundException {
        findCarById(car.getId());
        Car updated = carRepository.update(car);
        AuditService.getInstance().logAction("UPDATE_CAR");
        return updated;
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
