package todoapp.controller;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SidebarController {

    @FXML
    private VBox menuContainer;

    @FXML
    private HBox selectedTab;

    private MainController mainController; // ✅ رابط للـ MainController

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }

    @FXML
    public void initialize() {
        // تثبيت الافتراضي
        for (var node : menuContainer.getChildren()) {
            if (node instanceof HBox hbox && "selectedTab".equals(hbox.getId())) {
                selectedTab = hbox;
                break;
            }
        }
        updateSelectedStyle();
    }

    @FXML
    private void handleTabClick(MouseEvent event) {
        if (event.getSource() instanceof HBox clickedTab) {
            selectedTab = clickedTab;
            updateSelectedStyle();

            if (mainController != null) {
                String label = ((javafx.scene.control.Label) clickedTab.getChildren().get(1)).getText();

                switch (label) {
                    case "Dashboard" -> mainController.loadView("AnalyticsDashboard.fxml");
                    case "Tasks" -> mainController.loadView("Tasks.fxml");
                    case "Pomodoro" -> mainController.loadView("Pomodoro.fxml");
                    case "Goals" -> mainController.loadView("Goals.fxml");
                }
            }
        }
    }

    // في SidebarController.java

    private void updateSelectedStyle() {
        // 1. نشيل الـ Class "active" من كل العناصر
        for (var node : menuContainer.getChildren()) {
            if (node instanceof HBox hbox) {
                // شيل الـ Class "active" من أي HBox موجود
                hbox.getStyleClass().remove("active");

                // 🚨 عشان الـ HBox اللي اسمه selectedTab في الـ FXML ميبوظش، هنشيل منه الـ ID بعد ما نخلص أول مرة
                if ("selectedTab".equals(hbox.getId())) {
                    hbox.setId(null);
                }
            }
        }

        // 2. نضيف الـ Class "active" للعنصر اللي اتحدد جديد
        if (selectedTab != null) {
            // ضيف الـ Class "active" للـ HBox اللي اخترناه
            selectedTab.getStyleClass().add("active");
        }
    }
}
