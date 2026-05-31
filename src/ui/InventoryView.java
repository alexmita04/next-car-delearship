package ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import model.Car;
import model.NewCar;
import model.UsedCar;
import service.CarService;

import java.util.List;
import java.util.Optional;

public class InventoryView extends BorderPane {
    private final CarService carService;
    private final TableView<Car> table = new TableView<>();

    public InventoryView(CarService carService) {
        this.carService = carService;
        buildTable();
        setTop(createToolbar());
        setCenter(table);
        setPadding(new Insets(5));
        refresh();
    }

    private void buildTable() {
        TableColumn<Car, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));

        TableColumn<Car, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(data -> {
            Car car = data.getValue();
            String type = car instanceof NewCar ? "New" : "Used";
            return new SimpleStringProperty(type);
        });

        TableColumn<Car, String> brandCol = new TableColumn<>("Brand");
        brandCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBrand()));

        TableColumn<Car, String> modelCol = new TableColumn<>("Model");
        modelCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getModel()));

        TableColumn<Car, String> yearCol = new TableColumn<>("Year");
        yearCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getYear())));

        TableColumn<Car, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.2f", data.getValue().getPrice())));

        TableColumn<Car, String> availableCol = new TableColumn<>("Available");
        availableCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().isAvailable() ? "Yes" : "No"));

        TableColumn<Car, String> discountCol = new TableColumn<>("Discount");
        discountCol.setCellValueFactory(data -> new SimpleStringProperty(
                String.format("%.1f%%", data.getValue().getDiscountPercent())));

        table.getColumns().addAll(idCol, typeCol, brandCol, modelCol, yearCol, priceCol, availableCol, discountCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    private ToolBar createToolbar() {
        Button addButton = new Button("Add");
        Button deleteButton = new Button("Delete");
        Button editButton = new Button("Edit");
        TextField searchField = new TextField();
        searchField.setPromptText("Search by brand...");
        HBox.setHgrow(searchField, Priority.ALWAYS);

        addButton.setOnAction(e -> onAdd());
        deleteButton.setOnAction(e -> onDelete());
        editButton.setOnAction(e -> onEdit());
        searchField.setOnAction(e -> onSearch(searchField.getText()));

        ToolBar toolbar = new ToolBar(addButton, deleteButton, editButton, new Label("Search:"), searchField);
        toolbar.setPadding(new Insets(5, 0, 5, 0));
        return toolbar;
    }

    public void refresh() {
        table.setItems(FXCollections.observableArrayList(carService.getInventory()));
    }

    private void onAdd() {
        AddCarDialog dialog = new AddCarDialog(carService);
        Optional<Car> result = dialog.showAndWait();
        result.ifPresent(car -> refresh());
    }

    private void onDelete() {
        Car selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showError("Selection", "Select a car from the list.");
            return;
        }
        if (!AlertHelper.confirm("Confirmation", "Delete car " + selected.getBrand() + " " + selected.getModel() + "?")) {
            return;
        }
        try {
            carService.removeCar(selected.getId());
            refresh();
        } catch (Exception ex) {
            AlertHelper.showError("Error", ex.getMessage());
        }
    }

    private void onEdit() {
        Car selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showError("Selection", "Select a car from the list.");
            return;
        }
        EditCarDialog dialog = new EditCarDialog(selected, carService);
        dialog.showAndWait().ifPresent(car -> refresh());
    }

    private void onSearch(String brand) {
        if (brand == null || brand.isBlank()) {
            refresh();
            return;
        }
        List<Car> results = carService.searchCars(brand.trim(), null, null, null);
        table.setItems(FXCollections.observableArrayList(results));
    }
}
