package todoapp.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class AnalyticsController implements Initializable {

    @FXML private Label headerTitle;
    @FXML private Label pomodoroSectionTitle;
    @FXML private HBox pomodoroCard;
    @FXML private VBox totalSessionsColumn;
    @FXML private Label totalSessionsLabel;
    @FXML private Label totalSessionsValue;
    @FXML private VBox averageDurationColumn;
    @FXML private Label averageDurationLabel;
    @FXML private Label averageDurationValue;
    @FXML private Label goalsSectionTitle;
    @FXML private HBox goalsCardsRow;
    @FXML private VBox goalsCompletedCard;
    @FXML private Label goalsCompletedLabel;
    @FXML private Label goalsCompletedValue;
    @FXML private VBox goalsInProgressCard;
    @FXML private Label goalsInProgressLabel;
    @FXML private Label goalsInProgressValue;
    @FXML private VBox overallProgressCard;
    @FXML private Label overallProgressLabel;
    @FXML private Label overallProgressValue;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeDashboard();
    }

    private void initializeDashboard() {
        updatePomodoroSessions(245, "25 min");
        updateGoals(12, 3, "80%");
    }

    public void updatePomodoroSessions(int totalSessions, String averageDuration) {
        if (totalSessionsValue != null) {
            totalSessionsValue.setText(String.valueOf(totalSessions));
        }
        if (averageDurationValue != null) {
            averageDurationValue.setText(averageDuration);
        }
    }

    public void updateGoals(int completed, int inProgress, String overallProgress) {
        if (goalsCompletedValue != null) {
            goalsCompletedValue.setText(String.valueOf(completed));
        }
        if (goalsInProgressValue != null) {
            goalsInProgressValue.setText(String.valueOf(inProgress));
        }
        if (overallProgressValue != null) {
            overallProgressValue.setText(overallProgress);
        }
    }

    public void refreshDashboard() {
        initializeDashboard();
    }

    public String[] getPomodoroData() {
        if (totalSessionsValue != null && averageDurationValue != null) {
            return new String[]{
                totalSessionsValue.getText(),
                averageDurationValue.getText()
            };
        }
        return new String[]{"0", "0 min"};
    }

    public String[] getGoalsData() {
        if (goalsCompletedValue != null && goalsInProgressValue != null && overallProgressValue != null) {
            return new String[]{
                goalsCompletedValue.getText(),
                goalsInProgressValue.getText(),
                overallProgressValue.getText()
            };
        }
        return new String[]{"0", "0", "0%"};
    }
}
