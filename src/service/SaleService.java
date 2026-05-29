package service;

import exceptions.CarNotFoundException;
import exceptions.SaleAlreadyCancelledException;
import model.Sale;
import model.SalesReport;
import model.Salesperson;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SaleService {
    private final Map<Integer, Sale> sales = new HashMap<>();
    private final Map<Integer, Salesperson> employees = new HashMap<>();
    private final CarService carService;
    private final ClientService clientService;

    public SaleService(CarService carService, ClientService clientService) {
        this.carService = carService;
        this.clientService = clientService;
    }

    /** Action 7: Register a sale (client buys a car) */
    public Sale registerSale(int clientId, int carId, int salespersonId, LocalDate date)
            throws CarNotFoundException {
        var client = clientService.findClientById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client with ID " + clientId + " does not exist."));
        var car = carService.findCarById(carId);
        if (!car.isAvailable()) {
            throw new IllegalStateException("Car with ID " + carId + " is not available.");
        }
        var salesperson = findActiveSalesperson(salespersonId)
                .orElseThrow(() -> new IllegalArgumentException("Salesperson with ID " + salespersonId + " does not exist."));

        double finalPrice = car.getDiscountedPrice();
        Sale sale = new Sale(client, car, salesperson, date, finalPrice);
        car.setAvailable(false);
        sales.put(sale.getId(), sale);
        return sale;
    }

    /** Action 8: Cancel a sale */
    public void cancelSale(int saleId) throws SaleAlreadyCancelledException {
        Sale sale = findSaleById(saleId)
                .orElseThrow(() -> new IllegalArgumentException("Sale with ID " + saleId + " does not exist."));
        if (sale.isCancelled()) {
            throw new SaleAlreadyCancelledException(saleId);
        }
        sale.setCancelled(true);
        sale.getCar().setAvailable(true);
    }

    /** Action 9: Add an employee (salesperson) */
    public Salesperson addEmployee(Salesperson salesperson) {
        employees.put(salesperson.getId(), salesperson);
        return salesperson;
    }

    /** Action 10: Fire an employee */
    public boolean fireEmployee(int employeeId) {
        return findActiveSalesperson(employeeId)
                .map(sp -> {
                    sp.setActive(false);
                    return true;
                })
                .orElse(false);
    }

    /** Action 11: Assign a salesperson to a sale */
    public void assignSalespersonToSale(int saleId, int salespersonId) {
        Sale sale = findSaleById(saleId)
                .orElseThrow(() -> new IllegalArgumentException("Sale with ID " + saleId + " does not exist."));
        Salesperson salesperson = findActiveSalesperson(salespersonId)
                .orElseThrow(() -> new IllegalArgumentException("Salesperson with ID " + salespersonId + " does not exist."));
        sale.setSalesperson(salesperson);
    }

    /** Action 14: Generate sales report (total revenue, number of sales) */
    public SalesReport generateSalesReport(LocalDate startDate, LocalDate endDate) {
        List<Sale> periodSales = sales.values().stream()
                .filter(sale -> !sale.isCancelled())
                .filter(sale -> !sale.getDate().isBefore(startDate) && !sale.getDate().isAfter(endDate))
                .collect(Collectors.toList());

        double totalRevenue = periodSales.stream()
                .mapToDouble(Sale::getFinalPrice)
                .sum();

        return new SalesReport(startDate, endDate, totalRevenue, periodSales.size(), periodSales);
    }

    /** Action 17: View a client's sales history */
    public List<Sale> getClientSalesHistory(int clientId) {
        return sales.values().stream()
                .filter(sale -> sale.getClient().getId() == clientId)
                .collect(Collectors.toList());
    }

    public Optional<Sale> findSaleById(int saleId) {
        return Optional.ofNullable(sales.get(saleId));
    }

    private Optional<Salesperson> findActiveSalesperson(int id) {
        Salesperson sp = employees.get(id);
        if (sp != null && sp.isActive()) {
            return Optional.of(sp);
        }
        return Optional.empty();
    }

    public Map<Integer, Sale> getSales() {
        return new HashMap<>(sales);
    }

    public List<Salesperson> getActiveEmployees() {
        return employees.values().stream()
                .filter(Salesperson::isActive)
                .collect(Collectors.toList());
    }
}
