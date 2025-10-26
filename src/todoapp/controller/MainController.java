package todoapp.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;

import java.io.IOException;

public class MainController {

    @FXML private BorderPane rootLayout;  // ✅ لازم يكون الـ Root في الـ Main.fxml BorderPane
    @FXML private StackPane contentArea;  // ✅ هنا بنغير المحتوى

    @FXML
    public void initialize() {
        loadSidebar();        // ✅ أولاً نحمّل السايد بار
        loadView("AnalyticsDashboard.fxml"); // ✅ الصفحة الافتراضية
    }

    private void loadSidebar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/todoapp/fxml/Sidebar.fxml"));
            Node sidebar = loader.load();

            // ✅ Link SidebarController to MainController
            SidebarController sidebarController = loader.getController();
            sidebarController.setMainController(this);

            rootLayout.setLeft(sidebar);

        } catch (IOException e) {
            System.out.println("Error Loading Sidebar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/todoapp/fxml/" + fxmlFile));
            Node view = loader.load();

            contentArea.getChildren().setAll(view);

        } catch (IOException e) {
            System.out.println("Error Loading View: " + fxmlFile);
            e.printStackTrace();
        }
    }
}
