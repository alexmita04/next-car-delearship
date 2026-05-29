package ui;

import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainView {
    private final AppContext context;
    private final BorderPane root = new BorderPane();
    private final StackPane contentArea = new StackPane();

    private final InventoryView inventoryView;
    private final SaleView saleView;
    private final ClientView clientView;

    public MainView(AppContext context) {
        this.context = context;
        inventoryView = new InventoryView(context.getCarService());
        saleView = new SaleView(context.getSaleService(), context.getCarService(), context.getClientService());
        clientView = new ClientView(context.getClientService());

        root.setTop(createMenuBar());
        root.setCenter(contentArea);
        showInventory();
    }

    public void show(Stage stage) {
        stage.setTitle("Next Car Dealership");
        stage.setScene(new Scene(root, 960, 600));
        stage.show();
    }

    private MenuBar createMenuBar() {
        MenuItem inventoryItem = new MenuItem("Inventar");
        inventoryItem.setOnAction(e -> showInventory());

        MenuItem salesItem = new MenuItem("Vânzări");
        salesItem.setOnAction(e -> showSales());

        MenuItem clientsItem = new MenuItem("Clienți");
        clientsItem.setOnAction(e -> showClients());

        Menu menu = new Menu("Meniu");
        menu.getItems().addAll(inventoryItem, salesItem, clientsItem);

        MenuBar menuBar = new MenuBar(menu);
        return menuBar;
    }

    private void showInventory() {
        inventoryView.refresh();
        contentArea.getChildren().setAll(inventoryView);
    }

    private void showSales() {
        saleView.refresh();
        contentArea.getChildren().setAll(saleView);
    }

    private void showClients() {
        clientView.refresh();
        contentArea.getChildren().setAll(clientView);
    }

    public BorderPane getRoot() {
        return root;
    }
}
