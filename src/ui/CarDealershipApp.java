package ui;

import javafx.application.Application;
import javafx.stage.Stage;

public class CarDealershipApp extends Application {
    @Override
    public void start(Stage stage) {
        AppContext context = new AppContext();
        MainView mainView = new MainView(context);
        mainView.show(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
