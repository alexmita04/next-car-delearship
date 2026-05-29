package ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import model.Client;
import service.ClientService;

import java.util.Optional;

public class ClientView extends BorderPane {
    private final ClientService clientService;
    private final TableView<Client> table = new TableView<>();

    public ClientView(ClientService clientService) {
        this.clientService = clientService;
        buildTable();
        setTop(createToolbar());
        setCenter(table);
        setPadding(new Insets(5));
        refresh();
    }

    private void buildTable() {
        TableColumn<Client, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));

        TableColumn<Client, String> nameCol = new TableColumn<>("Nume");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        TableColumn<Client, String> phoneCol = new TableColumn<>("Telefon");
        phoneCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhone()));

        TableColumn<Client, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));

        table.getColumns().addAll(idCol, nameCol, phoneCol, emailCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    private ToolBar createToolbar() {
        Button addButton = new Button("Adaugă");
        Button deleteButton = new Button("Șterge");
        Button editButton = new Button("Editează");

        addButton.setOnAction(e -> onAdd());
        deleteButton.setOnAction(e -> onDelete());
        editButton.setOnAction(e -> onEdit());

        ToolBar toolbar = new ToolBar(addButton, deleteButton, editButton);
        toolbar.setPadding(new Insets(5, 0, 5, 0));
        return toolbar;
    }

    public void refresh() {
        table.setItems(FXCollections.observableArrayList(clientService.getAllClients()));
    }

    private void onAdd() {
        showClientDialog(null).ifPresent(client -> refresh());
    }

    private void onDelete() {
        Client selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showError("Selecție", "Selectează un client din listă.");
            return;
        }
        if (!AlertHelper.confirm("Confirmare", "Ștergi clientul " + selected.getName() + "?")) {
            return;
        }
        try {
            clientService.removeClient(selected.getId());
            refresh();
        } catch (Exception ex) {
            AlertHelper.showError("Eroare", ex.getMessage());
        }
    }

    private void onEdit() {
        Client selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showError("Selecție", "Selectează un client din listă.");
            return;
        }
        showClientDialog(selected).ifPresent(client -> refresh());
    }

    private Optional<Client> showClientDialog(Client existing) {
        Dialog<Client> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Adaugă client" : "Editează client");
        dialog.setHeaderText(existing == null ? "Date client nou" : existing.getName());

        ButtonType saveButton = new ButtonType("Salvează", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        TextField nameField = new TextField(existing != null ? existing.getName() : "");
        TextField phoneField = new TextField(existing != null ? existing.getPhone() : "");
        TextField emailField = new TextField(existing != null ? existing.getEmail() : "");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));
        grid.add(new Label("Nume:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Telefon:"), 0, 1);
        grid.add(phoneField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> {
            if (button != saveButton) {
                return null;
            }
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            if (name.isBlank() || phone.isBlank() || email.isBlank()) {
                AlertHelper.showError("Validare", "Completează toate câmpurile.");
                return null;
            }
            try {
                if (existing == null) {
                    return clientService.addClient(new Client(name, phone, email));
                }
                existing.setName(name);
                existing.setPhone(phone);
                existing.setEmail(email);
                return clientService.updateClient(existing);
            } catch (Exception ex) {
                AlertHelper.showError("Eroare", ex.getMessage());
                return null;
            }
        });

        return dialog.showAndWait();
    }
}
