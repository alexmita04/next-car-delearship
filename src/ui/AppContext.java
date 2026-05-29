package ui;

import service.CarService;
import service.ClientService;
import service.SaleService;

public final class AppContext {
    private final CarService carService;
    private final ClientService clientService;
    private final SaleService saleService;

    public AppContext() {
        carService = new CarService();
        clientService = new ClientService();
        saleService = new SaleService(carService, clientService);
    }

    public CarService getCarService() {
        return carService;
    }

    public ClientService getClientService() {
        return clientService;
    }

    public SaleService getSaleService() {
        return saleService;
    }
}
