package ui;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import model.Car;
import model.UsedCar;
import service.CarService;

public class EditCarDialog extends Dialog<Car> {
    public EditCarDialog(Car car, CarService carService) {
        setTitle("Editează mașină");
        setHeaderText(car.getBrand() + " " + car.getModel());

        ButtonType saveButton = new ButtonType("Salvează", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        TextField brandField = new TextField(car.getBrand());
        TextField modelField = new TextField(car.getModel());
        TextField yearField = new TextField(String.valueOf(car.getYear()));
        TextField priceField = new TextField(String.valueOf(car.getPrice()));
        TextField conditionField = new TextField(car.getCondition());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));

        int row = 0;
        grid.add(new Label("Marcă:"), 0, row);
        grid.add(brandField, 1, row++);
        grid.add(new Label("Model:"), 0, row);
        grid.add(modelField, 1, row++);
        grid.add(new Label("An:"), 0, row);
        grid.add(yearField, 1, row++);
        grid.add(new Label("Preț:"), 0, row);
        grid.add(priceField, 1, row++);
        grid.add(new Label("Stare:"), 0, row);
        grid.add(conditionField, 1, row++);

        TextField kmField = null;
        TextField ownersField = null;
        if (car instanceof UsedCar usedCar) {
            kmField = new TextField(String.valueOf(usedCar.getKilometers()));
            ownersField = new TextField(String.valueOf(usedCar.getNumberOfOwners()));
            grid.add(new Label("Kilometri:"), 0, row);
            grid.add(kmField, 1, row++);
            grid.add(new Label("Proprietari:"), 0, row);
            grid.add(ownersField, 1, row++);
        }

        getDialogPane().setContent(grid);

        TextField finalKmField = kmField;
        TextField finalOwnersField = ownersField;
        setResultConverter(button -> {
            if (button != saveButton) {
                return null;
            }
            try {
                car.setBrand(brandField.getText().trim());
                car.setModel(modelField.getText().trim());
                car.setYear(Integer.parseInt(yearField.getText().trim()));
                car.setPrice(Double.parseDouble(priceField.getText().trim()));
                car.setCondition(conditionField.getText().trim());

                if (car instanceof UsedCar usedCar && finalKmField != null && finalOwnersField != null) {
                    usedCar.setKilometers(Integer.parseInt(finalKmField.getText().trim()));
                    usedCar.setNumberOfOwners(Integer.parseInt(finalOwnersField.getText().trim()));
                }

                return carService.updateCar(car);
            } catch (Exception ex) {
                AlertHelper.showError("Eroare", ex.getMessage());
                return null;
            }
        });
    }
}
