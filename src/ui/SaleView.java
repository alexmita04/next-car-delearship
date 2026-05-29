package ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import model.Car;
import model.Client;
import model.Sale;
import model.Salesperson;
import service.CarService;
import service.ClientService;
import service.SaleService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class SaleView extends BorderPane {
    private final SaleService saleService;
    private final CarService carService;
    private final ClientService clientService;
    private final TableView<Sale> table = new TableView<>();

    public SaleView(SaleService saleService, CarService carService, ClientService clientService) {
        this.saleService = saleService;
        this.carService = carService;
        this.clientService = clientService;
        buildTable();
        setTop(createToolbar());
        setCenter(table);
        setPadding(new Insets(5));
        refresh();
    }

    private void buildTable() {
        TableColumn<Sale, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));

        TableColumn<Sale, String> clientCol = new TableColumn<>("Client");
        clientCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getClient().getName()));

        TableColumn<Sale, String> carCol = new TableColumn<>("Mașină");
        carCol.setCellValueFactory(data -> {
            Sale sale = data.getValue();
            return new SimpleStringProperty(sale.getCar().getBrand() + " " + sale.getCar().getModel());
        });

        TableColumn<Sale, String> salespersonCol = new TableColumn<>("Vânzător");
        salespersonCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSalesperson().getName()));

        TableColumn<Sale, String> dateCol = new TableColumn<>("Data");
        dateCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDate().toString()));

        TableColumn<Sale, String> priceCol = new TableColumn<>("Preț final");
        priceCol.setCellValueFactory(data -> new SimpleStringProperty(
                String.format("%.2f", data.getValue().getFinalPrice())));

        TableColumn<Sale, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().isCancelled() ? "Anulată" : "Activă"));

        table.getColumns().addAll(idCol, clientCol, carCol, salespersonCol, dateCol, priceCol, statusCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    private ToolBar createToolbar() {
        Button registerButton = new Button("Înregistrare vânzare");
        Button cancelButton = new Button("Anulează vânzare");
        Button deleteButton = new Button("Șterge");

        registerButton.setOnAction(e -> onRegisterSale());
        cancelButton.setOnAction(e -> onCancelSale());
        deleteButton.setOnAction(e -> onDeleteSale());

        ToolBar toolbar = new ToolBar(registerButton, cancelButton, deleteButton);
        toolbar.setPadding(new Insets(5, 0, 5, 0));
        return toolbar;
    }

    public void refresh() {
        table.setItems(FXCollections.observableArrayList(saleService.getAllSales()));
    }

    private void onRegisterSale() {
        List<Client> clients = clientService.getAllClients();
        List<Car> availableCars = carService.listAvailableCarsSortedByPrice();
        List<Salesperson> employees = saleService.getActiveEmployees();

        if (clients.isEmpty() || availableCars.isEmpty() || employees.isEmpty()) {
            AlertHelper.showError("Date insuficiente",
                    "Ai nevoie de cel puțin un client, o mașină disponibilă și un vânzător activ.");
            return;
        }

        Dialog<Sale> dialog = new Dialog<>();
        dialog.setTitle("Înregistrare vânzare");
        dialog.setHeaderText("Selectează client, mașină și vânzător");

        ComboBox<Client> clientBox = new ComboBox<>(FXCollections.observableArrayList(clients));
        clientBox.setPromptText("Client");
        clientBox.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Client client) {
                return client == null ? "" : client.getName();
            }

            @Override
            public Client fromString(String string) {
                return null;
            }
        });

        ComboBox<Car> carBox = new ComboBox<>(FXCollections.observableArrayList(availableCars));
        carBox.setPromptText("Mașină");
        carBox.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Car car) {
                return car == null ? "" : car.getBrand() + " " + car.getModel() + " (" + car.getPrice() + ")";
            }

            @Override
            public Car fromString(String string) {
                return null;
            }
        });

        ComboBox<Salesperson> salespersonBox = new ComboBox<>(FXCollections.observableArrayList(employees));
        salespersonBox.setPromptText("Vânzător");
        salespersonBox.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Salesperson sp) {
                return sp == null ? "" : sp.getName();
            }

            @Override
            public Salesperson fromString(String string) {
                return null;
            }
        });

        DatePicker datePicker = new DatePicker(LocalDate.now());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));
        grid.add(new Label("Client:"), 0, 0);
        grid.add(clientBox, 1, 0);
        grid.add(new Label("Mașină:"), 0, 1);
        grid.add(carBox, 1, 1);
        grid.add(new Label("Vânzător:"), 0, 2);
        grid.add(salespersonBox, 1, 2);
        grid.add(new Label("Data:"), 0, 3);
        grid.add(datePicker, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(
                new javafx.scene.control.ButtonType("Salvează", javafx.scene.control.ButtonBar.ButtonData.OK_DONE),
                javafx.scene.control.ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button.getButtonData() != javafx.scene.control.ButtonBar.ButtonData.OK_DONE) {
                return null;
            }
            if (clientBox.getValue() == null || carBox.getValue() == null || salespersonBox.getValue() == null) {
                AlertHelper.showError("Validare", "Completează toate câmpurile.");
                return null;
            }
            try {
                return saleService.registerSale(
                        clientBox.getValue().getId(),
                        carBox.getValue().getId(),
                        salespersonBox.getValue().getId(),
                        datePicker.getValue()
                );
            } catch (Exception ex) {
                AlertHelper.showError("Eroare", ex.getMessage());
                return null;
            }
        });

        Optional<Sale> result = dialog.showAndWait();
        result.ifPresent(sale -> refresh());
    }

    private void onCancelSale() {
        Sale selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showError("Selecție", "Selectează o vânzare din listă.");
            return;
        }
        if (selected.isCancelled()) {
            AlertHelper.showInfo("Info", "Vânzarea este deja anulată.");
            return;
        }
        if (!AlertHelper.confirm("Confirmare", "Anulezi vânzarea #" + selected.getId() + "?")) {
            return;
        }
        try {
            saleService.cancelSale(selected.getId());
            refresh();
        } catch (Exception ex) {
            AlertHelper.showError("Eroare", ex.getMessage());
        }
    }

    private void onDeleteSale() {
        Sale selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showError("Selecție", "Selectează o vânzare din listă.");
            return;
        }
        String message = selected.isCancelled()
                ? "Ștergi definitiv vânzarea anulată #" + selected.getId() + "?"
                : "Ștergi definitiv vânzarea #" + selected.getId()
                + "? Mașina va redeveni disponibilă în inventar.";
        if (!AlertHelper.confirm("Confirmare", message)) {
            return;
        }
        try {
            saleService.deleteSale(selected.getId());
            refresh();
        } catch (Exception ex) {
            AlertHelper.showError("Eroare", ex.getMessage());
        }
    }
}
