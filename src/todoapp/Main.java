package todoapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1️⃣ شاشة الـ Splash
            FXMLLoader splashLoader = new FXMLLoader(getClass().getResource("fxml/splash.fxml"));
            Parent splashRoot = splashLoader.load();
            Stage splashStage = new Stage();
            splashStage.initStyle(StageStyle.UNDECORATED);
            Scene splashScene = new Scene(splashRoot, 450, 300);
            splashStage.setScene(splashScene);
            splashStage.centerOnScreen();
            splashStage.show();

            // 2️⃣ بعد 2.5 ثانية نفتح الـ MainLayout (اللي فيه السايد بار)
            PauseTransition pause = new PauseTransition(Duration.seconds(2.5));
            pause.setOnFinished(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/MainLayout.fxml"));
                    Parent root = loader.load();

                    Scene scene = new Scene(root, 1000, 700);
                    scene.getStylesheets().add(getClass().getResource("style/sidebar.css").toExternalForm()); // 👈 ضفنا ده
                    scene.getStylesheets().add(getClass().getResource("style/analytics.css").toExternalForm());

                    primaryStage.setTitle("Taskly Dashboard");
                    primaryStage.setScene(scene);
                    primaryStage.setMaximized(true);
                    primaryStage.centerOnScreen();
                    primaryStage.show();

                    splashStage.close();

                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Error loading MainLayout: " + e.getMessage());
                }
            });
            pause.play();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading Splash Screen: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
