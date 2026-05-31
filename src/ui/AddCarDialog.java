package ui;

import builder.CarBuilder;
import factory.CarType;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import model.Car;
import service.CarService;

public class AddCarDialog extends Dialog<Car> {
    public AddCarDialog(CarService carService) {
        setTitle("Add car");
        setHeaderText("Enter car details");

        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        ComboBox<CarType> typeBox = new ComboBox<>();
        typeBox.getItems().addAll(CarType.NEW, CarType.USED);
        typeBox.setValue(CarType.NEW);

        TextField brandField = new TextField();
        TextField modelField = new TextField();
        TextField yearField = new TextField();
        TextField priceField = new TextField();
        TextField conditionField = new TextField("New");
        TextField warrantyField = new TextField("3");
        TextField kmField = new TextField("0");
        TextField ownersField = new TextField("1");

        Label warrantyLabel = new Label("Warranty (years):");
        Label kmLabel = new Label("Kilometers:");
        Label ownersLabel = new Label("Previous owners:");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));

        grid.add(new Label("Type:"), 0, 0);
        grid.add(typeBox, 1, 0);
        grid.add(new Label("Brand:"), 0, 1);
        grid.add(brandField, 1, 1);
        grid.add(new Label("Model:"), 0, 2);
        grid.add(modelField, 1, 2);
        grid.add(new Label("Year:"), 0, 3);
        grid.add(yearField, 1, 3);
        grid.add(new Label("Price:"), 0, 4);
        grid.add(priceField, 1, 4);
        grid.add(new Label("Condition:"), 0, 5);
        grid.add(conditionField, 1, 5);
        grid.add(warrantyLabel, 0, 6);
        grid.add(warrantyField, 1, 6);
        grid.add(kmLabel, 0, 7);
        grid.add(kmField, 1, 7);
        grid.add(ownersLabel, 0, 8);
        grid.add(ownersField, 1, 8);

        Runnable updateFields = () -> {
            boolean isNew = typeBox.getValue() == CarType.NEW;
            warrantyLabel.setVisible(isNew);
            warrantyField.setVisible(isNew);
            kmLabel.setVisible(!isNew);
            kmField.setVisible(!isNew);
            ownersLabel.setVisible(!isNew);
            ownersField.setVisible(!isNew);
            conditionField.setText(isNew ? "New" : "Used");
        };
        typeBox.setOnAction(e -> updateFields.run());
        updateFields.run();

        getDialogPane().setContent(grid);

        setResultConverter(button -> {
            if (button != saveButton) {
                return null;
            }
            try {
                CarBuilder builder = typeBox.getValue() == CarType.NEW
                        ? CarBuilder.forNewCar().warrantyYears(Integer.parseInt(warrantyField.getText().trim()))
                        : CarBuilder.forUsedCar()
                                .kilometers(Integer.parseInt(kmField.getText().trim()))
                                .numberOfOwners(Integer.parseInt(ownersField.getText().trim()));

                Car car = builder
                        .brand(brandField.getText().trim())
                        .model(modelField.getText().trim())
                        .year(Integer.parseInt(yearField.getText().trim()))
                        .price(Double.parseDouble(priceField.getText().trim()))
                        .condition(conditionField.getText().trim())
                        .build();

                return carService.addCar(car);
            } catch (Exception ex) {
                AlertHelper.showError("Error", ex.getMessage());
                return null;
            }
        });
    }
}
