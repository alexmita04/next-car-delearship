package service;

import exceptions.CarNotFoundException;
import exceptions.SaleAlreadyCancelledException;
import model.Sale;
import model.SalesReport;
import model.Salesperson;
import observer.AuditSaleObserver;
import observer.SaleSubject;
import observer.SalesReportObserver;
import repository.CarRepository;
import repository.EmployeeRepository;
import repository.SaleRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SaleService {
    private final SaleRepository saleRepository;
    private final EmployeeRepository employeeRepository;
    private final CarRepository carRepository;
    private final ClientService clientService;
    private final SaleSubject saleSubject;
    private final SalesReportObserver salesReportObserver;

    public SaleService(CarService carService, ClientService clientService) {
        this(new SaleRepository(), new EmployeeRepository(), new CarRepository(), clientService);
    }

    public SaleService(SaleRepository saleRepository, EmployeeRepository employeeRepository,
                       CarRepository carRepository, ClientService clientService) {
        this.saleRepository = saleRepository;
        this.employeeRepository = employeeRepository;
        this.carRepository = carRepository;
        this.clientService = clientService;
        this.saleSubject = new SaleSubject();
        this.salesReportObserver = new SalesReportObserver();
        this.saleSubject.subscribe(new AuditSaleObserver());
        this.saleSubject.subscribe(salesReportObserver);
    }

    public Sale registerSale(int clientId, int carId, int salespersonId, LocalDate date)
            throws CarNotFoundException {
        var client = clientService.findClientById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client with ID " + clientId + " does not exist."));
        var car = carRepository.findById(carId)
                .orElseThrow(() -> new CarNotFoundException(carId));
        if (!car.isAvailable()) {
            throw new IllegalStateException("Car with ID " + carId + " is not available.");
        }
        var salesperson = findActiveSalesperson(salespersonId)
                .orElseThrow(() -> new IllegalArgumentException("Salesperson with ID " + salespersonId + " does not exist."));

        double finalPrice = car.getDiscountedPrice();
        Sale sale = saleRepository.insert(new Sale(client, car, salesperson, date, finalPrice));
        car.setAvailable(false);
        carRepository.update(car);
        saleSubject.notifySaleCompleted(sale);
        return sale;
    }

    public void cancelSale(int saleId) throws SaleAlreadyCancelledException {
        Sale sale = findSaleById(saleId)
                .orElseThrow(() -> new IllegalArgumentException("Sale with ID " + saleId + " does not exist."));
        if (sale.isCancelled()) {
            throw new SaleAlreadyCancelledException(saleId);
        }
        sale.setCancelled(true);
        saleRepository.update(sale);
        var car = sale.getCar();
        car.setAvailable(true);
        carRepository.update(car);
        AuditService.getInstance().logAction("CANCEL_SALE");
    }

    public Salesperson addEmployee(Salesperson salesperson) {
        Salesperson saved = employeeRepository.insert(salesperson);
        AuditService.getInstance().logAction("ADD_EMPLOYEE");
        return saved;
    }

    public boolean fireEmployee(int employeeId) {
        return findActiveSalesperson(employeeId)
                .map(sp -> {
                    employeeRepository.setActive(employeeId, false);
                    sp.setActive(false);
                    AuditService.getInstance().logAction("FIRE_EMPLOYEE");
                    return true;
                })
                .orElse(false);
    }

    public void assignSalespersonToSale(int saleId, int salespersonId) {
        Sale sale = findSaleById(saleId)
                .orElseThrow(() -> new IllegalArgumentException("Sale with ID " + saleId + " does not exist."));
        Salesperson salesperson = findActiveSalesperson(salespersonId)
                .orElseThrow(() -> new IllegalArgumentException("Salesperson with ID " + salespersonId + " does not exist."));
        sale.setSalesperson(salesperson);
        saleRepository.update(sale);
        AuditService.getInstance().logAction("ASSIGN_SALESPERSON");
    }

    public SalesReport generateSalesReport(LocalDate startDate, LocalDate endDate) {
        List<Sale> periodSales = saleRepository.findByDateRange(startDate, endDate).stream()
                .filter(sale -> !sale.isCancelled())
                .collect(Collectors.toList());

        double totalRevenue = periodSales.stream()
                .mapToDouble(Sale::getFinalPrice)
                .sum();

        SalesReport report = new SalesReport(startDate, endDate, totalRevenue, periodSales.size(), periodSales);
        AuditService.getInstance().logAction("GENERATE_SALES_REPORT");
        return report;
    }

    public List<Sale> getClientSalesHistory(int clientId) {
        List<Sale> history = saleRepository.findByClientId(clientId);
        AuditService.getInstance().logAction("GET_CLIENT_SALES_HISTORY");
        return history;
    }

    public Optional<Sale> findSaleById(int saleId) {
        return saleRepository.findById(saleId);
    }

    private Optional<Salesperson> findActiveSalesperson(int id) {
        return employeeRepository.findById(id).filter(Salesperson::isActive);
    }

    public Map<Integer, Sale> getSales() {
        return saleRepository.findAll().stream()
                .collect(Collectors.toMap(Sale::getId, sale -> sale, (a, b) -> a, HashMap::new));
    }

    public List<Salesperson> getActiveEmployees() {
        return employeeRepository.findActive();
    }

    public SalesReportObserver getSalesReportObserver() {
        return salesReportObserver;
    }

    public SaleSubject getSaleSubject() {
        return saleSubject;
    }
}
