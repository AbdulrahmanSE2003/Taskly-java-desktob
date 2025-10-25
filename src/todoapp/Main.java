package todoapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/AnalyticsDashboard.fxml"));
            VBox root = loader.load();

            Scene scene = new Scene(root, 1000, 700);
            scene.getStylesheets().add(getClass().getResource("style/analytics.css").toExternalForm());

            primaryStage.setTitle("Analytics Dashboard");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            primaryStage.centerOnScreen();
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading the Analytics Dashboard: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
