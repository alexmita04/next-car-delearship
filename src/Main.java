import exceptions.CarNotFoundException;
import exceptions.InvalidPromotionException;
import exceptions.SaleAlreadyCancelledException;
import model.Client;
import model.NewCar;
import model.Salesperson;
import model.UsedCar;
import service.CarService;
import service.ClientService;
import service.SaleService;
import service.ServiceAppointmentService;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        CarService carService = new CarService();
        ClientService clientService = new ClientService();
        SaleService saleService = new SaleService(carService, clientService);
        ServiceAppointmentService appointmentService = new ServiceAppointmentService(carService);

        try {
            demo(carService, clientService, saleService, appointmentService);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void demo(CarService carService, ClientService clientService,
                             SaleService saleService, ServiceAppointmentService appointmentService)
            throws CarNotFoundException, InvalidPromotionException, SaleAlreadyCancelledException {

        // 1 — Add cars to inventory
        NewCar bmw = new NewCar("BMW", "3 Series", 2024, 45000, "New", 3);
        UsedCar audi = new UsedCar("Audi", "A4", 2020, 28000, "Good", 65000, 2);
        NewCar mercedes = new NewCar("Mercedes", "C-Class", 2024, 52000, "New", 3);
        carService.addCar(bmw);
        carService.addCar(audi);
        carService.addCar(mercedes);
        System.out.println("=== Inventory added ===");

        // 15 — Apply promotional discount
        carService.applyPromotion(audi.getId(), 10);
        System.out.println("10% discount applied to Audi A4");

        // 16 — List available cars sorted by price
        System.out.println("\n=== Available cars (sorted by price) ===");
        carService.listAvailableCarsSortedByPrice().forEach(System.out::println);

        // 4 — Search cars
        System.out.println("\n=== Search BMW ===");
        carService.searchCars("BMW", null, null, null).forEach(System.out::println);

        // 5 — Add clients
        Client john = new Client("John Smith", "555-0001", "john@email.com");
        Client maria = new Client("Maria Johnson", "555-0002", "maria@email.com");
        clientService.addClient(john);
        clientService.addClient(maria);
        System.out.println("\n=== Clients added ===");

        // 9 — Add salespersons
        Salesperson andrew = new Salesperson("Andrew Seller", 5000, LocalDate.of(2023, 3, 1), 5.0);
        Salesperson elena = new Salesperson("Elena Seller", 4800, LocalDate.of(2022, 6, 15), 4.5);
        saleService.addEmployee(andrew);
        saleService.addEmployee(elena);
        System.out.println("Employees added");

        // 7 — Register sale
        var sale = saleService.registerSale(john.getId(), audi.getId(), andrew.getId(), LocalDate.now());
        System.out.println("\n=== Sale registered ===");
        System.out.println(sale);

        // 11 — Assign salesperson (re-assignment)
        saleService.assignSalespersonToSale(sale.getId(), elena.getId());
        System.out.println("Salesperson re-assigned: " + elena.getName());

        // 12 — Service appointment
        var appointment = appointmentService.addServiceAppointment(
                bmw.getId(), LocalDate.now().plusDays(7), "Routine maintenance");
        System.out.println("\n=== Service appointment ===");
        System.out.println(appointment);

        // 13 — Mark appointment as completed
        appointmentService.markAsCompleted(appointment.getId());
        System.out.println("Appointment marked as completed");

        // 14 — Sales report
        System.out.println("\n" + saleService.generateSalesReport(
                LocalDate.now().minusMonths(1), LocalDate.now()).generateReport());

        // 17 — Client sales history
        System.out.println("=== John Smith sales history ===");
        saleService.getClientSalesHistory(john.getId()).forEach(System.out::println);

        // 3 — Update price
        carService.updatePrice(mercedes.getId(), 49000);
        System.out.println("\nMercedes price updated to 49000");

        // 8 — Cancel sale
        saleService.cancelSale(sale.getId());
        System.out.println("Sale cancelled — Audi A4 available again");

        // 6 — Remove client
        clientService.removeClient(maria.getId());
        System.out.println("Client Maria removed");

        // 10 — Fire employee
        saleService.fireEmployee(elena.getId());
        System.out.println("Employee Elena fired");

        // 2 — Remove car
        carService.removeCar(mercedes.getId());
        System.out.println("Mercedes removed from inventory");

        System.out.println("\n=== Demo completed successfully ===");
    }
}
