package todoapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1️⃣ سبلاش سكريـن
            FXMLLoader splashLoader = new FXMLLoader(getClass().getResource("fxml/splash.fxml"));
            VBox splashRoot = splashLoader.load();
            Stage splashStage = new Stage();
            splashStage.initStyle(StageStyle.UNDECORATED);
            Scene splashScene = new Scene(splashRoot, 450, 300); // عرض 450px وارتفاع 300px
            splashStage.setScene(splashScene);
            splashStage.centerOnScreen(); // خلي السبلاش في النص
            splashStage.show();

            // 2️⃣ بعد 2.5 ثانية افتح الداشبورد
            PauseTransition pause = new PauseTransition(Duration.seconds(2.5));
            pause.setOnFinished(event -> {
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

                    splashStage.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Error loading the Analytics Dashboard: " + e.getMessage());
                }
            });
            pause.play();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading the Splash Screen: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
