package todoapp.controller; // 🚨 تم تعديل الباكج ليتناسب مع todoapp.controller

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.geometry.Pos;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller class for the Pomodoro Timer feature, containing
 * all related utilities (API service, Notification, and Task data model).
 */
public class PomodoroController {

    // UI Components (هذه يجب أن تتطابق مع fx:id في Pomodoro.fxml)
    public Label pageTitle;
    public VBox timerContainer;
    public Button startBtn;
    public Button pauseBtn;
    public Button resetBtn;
    public VBox formContainer;
    public VBox sessionsCount;

    @FXML private ComboBox<Task> taskComboBox;
    @FXML private TextField workField;
    @FXML private TextField breakField;
    @FXML private Label timerLabel;
    @FXML private Label sessionCountLabel;
    @FXML private Label sessionTypeLabel;

    // Timer variables
    private Timeline timeline;
    private int secondsRemaining;
    private int workSeconds;
    private int breakSeconds;
    private boolean isWorkSession = true;
    private boolean isPaused = false;
    private boolean waitingForNext = false;

    // Currently selected task
    private Task selectedTask;

    // -----------------------------------------------------------
    // Controller Lifecycle Methods
    // -----------------------------------------------------------

    @FXML
    public void initialize() {
        System.out.println("[Pomodoro] Fetching tasks from backend...");

        new Thread(() -> {
            List<Task> tasks = ApiService.fetchTodos();
            javafx.application.Platform.runLater(() -> {
                taskComboBox.getItems().clear();
                taskComboBox.getItems().addAll(tasks);
                taskComboBox.setPromptText("Select a task");

                if (tasks.isEmpty()) {
                    System.err.println("[Pomodoro] No tasks received from backend.");
                } else {
                    System.out.println("[Pomodoro] Loaded " + tasks.size() + " tasks successfully.");
                }
            });
        }).start();

        // When the user selects a task
        taskComboBox.setOnAction(e -> {
            selectedTask = taskComboBox.getValue();
            if (selectedTask != null) {
                sessionCountLabel.setText(
                        "Sessions for " + selectedTask.getName() + ": " + selectedTask.getSessionCount()
                );
                System.out.println("[Pomodoro] Selected task: " + selectedTask.getName() + " (ID=" + selectedTask.getId() + ")");
            }
        });
    }

    // -----------------------------------------------------------
    // Timer Actions
    // -----------------------------------------------------------

    @FXML
    protected void startTimer() {
        try {
            if (taskComboBox.getValue() == null) {
                showNotification("Select Task", "Please select a task before starting.", 5, "error");
                return;
            }

            if (waitingForNext) waitingForNext = false;

            if (timeline != null && timeline.getStatus() == Timeline.Status.RUNNING) return;

            if (timeline == null || secondsRemaining == 0) {
                int workMinutes = Integer.parseInt(workField.getText());
                int breakMinutes = Integer.parseInt(breakField.getText());

                workSeconds = workMinutes * 60;
                breakSeconds = breakMinutes * 60;

                secondsRemaining = isWorkSession ? workSeconds : breakSeconds;
            }

            workField.setDisable(true);
            breakField.setDisable(true);

            if (timeline != null) timeline.stop();

            timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateTimer()));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();

            updateSessionLabel();

        } catch (NumberFormatException e) {
            showNotification("Error", "Invalid number. Please enter valid values.", 10, "error");
        }
    }

    @FXML
    protected void pauseTimer() {
        if (timeline == null) return;
        if (isPaused) timeline.play();
        else timeline.pause();
        isPaused = !isPaused;
    }

    @FXML
    protected void resetTimer() {
        if (timeline != null) timeline.stop();
        secondsRemaining = 0;
        timerLabel.setText("00:00");
        sessionTypeLabel.setText("");
        isWorkSession = true;
        isPaused = false;
        waitingForNext = false;
        workField.setDisable(false);
        breakField.setDisable(false);
    }

    // -----------------------------------------------------------
    // Private Utility Methods
    // -----------------------------------------------------------

    private void updateTimer() {
        int minutes = secondsRemaining / 60;
        int seconds = secondsRemaining % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));

        if (secondsRemaining > 0) {
            secondsRemaining--;
        } else {
            System.out.println("[Pomodoro] Timer reached zero. Current session: " + (isWorkSession ? "WORK" : "BREAK"));
            timeline.stop();

            if (isWorkSession) {
                // Work session completed
                selectedTask.incrementSessions();
                sessionCountLabel.setText(
                        "Sessions for " + selectedTask.getName() + ": " + selectedTask.getSessionCount()
                );
                timerLabel.setText("00:00");
                showNotification("Time's Up!", "Take a break now, you've earned it!", 5, "success");

                // Log session to backend
                if (selectedTask.getId() != null && !selectedTask.getId().isEmpty()) {
                    final String taskId = selectedTask.getId();
                    final int sessions = selectedTask.getSessionCount();

                    System.out.println("[Pomodoro] Logging session for task ID=" + taskId + ", total sessions=" + sessions);

                    new Thread(() -> {
                        boolean ok = ApiService.logPomodoroSession(taskId, sessions);
                        if (ok)
                            System.out.println("[Pomodoro] Successfully logged session for task " + taskId);
                        else
                            System.err.println("[Pomodoro] Failed to log session for task " + taskId);
                    }).start();
                } else {
                    System.err.println("[Pomodoro] Task ID is empty. Cannot log session.");
                }

            } else {
                // Break session completed
                timerLabel.setText("00:00");
                showNotification("Break Finished", "Let's get back to work!", 5, "success");
            }

            isWorkSession = !isWorkSession;
            waitingForNext = true;
            updateSessionLabel();
            workField.setDisable(false);
            breakField.setDisable(false);
        }
    }

    private void updateSessionLabel() {
        if (isWorkSession) {
            sessionTypeLabel.setText("WORK SESSION");
            sessionTypeLabel.setTextFill(Color.RED);
        } else {
            sessionTypeLabel.setText("BREAK SESSION");
            sessionTypeLabel.setTextFill(Color.GREEN);
        }
    }

    private void showNotification(String title, String message, int seconds, String type) {
        Duration duration = Duration.seconds(seconds);
        Notification notification = new Notification(title, message, duration, type);
        notification.show();
    }

    // -----------------------------------------------------------
    // Inner Classes (ApiService, Notification, Task)
    // -----------------------------------------------------------

    /**
     * Data model for a task.
     */
    public static class Task {
        private String id;
        private String name;
        private int sessionCount;

        public Task(String id, String name) {
            this.id = id;
            this.name = name;
            this.sessionCount = 0;
        }

        public Task(String name) {
            this("", name);
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public int getSessionCount() { return sessionCount; }
        public void incrementSessions() { sessionCount++; }

        @Override
        public String toString() { return name; }
    }

    /**
     * Handles communication with the backend API.
     */
    private static class ApiService {

        private static final String BASE_URL = "https://tasky-platform-api-copy-production.up.railway.app";
        private static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0YXNreXRlYW1AdGVzdC5jb20iLCJpYXQiOjE3NjExNTQwNzF9.XNLI0L_CHg2NLda2E2Jyz0QwAtoceaEh30lnrL87JFs";

        public static List<Task> fetchTodos() {
            List<Task> tasks = new ArrayList<>();

            // ... (كود fetchTodos كما هو، يستخدم Inner Task) ...
            try {
                URL url = new URL(BASE_URL + "/todo");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + TOKEN);
                conn.setRequestProperty("Content-Type", "application/json");

                int responseCode = conn.getResponseCode();

                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)
                    );
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    System.out.println("[API] /todo raw response: " + response);

                    JSONObject responseJson = new JSONObject(response.toString());
                    JSONArray jsonArray;

                    if (responseJson.has("todos")) {
                        jsonArray = responseJson.getJSONArray("todos");
                    } else if (responseJson.has("data")) {
                        jsonArray = responseJson.getJSONArray("data");
                    } else {
                        jsonArray = new JSONArray(response.toString());
                    }

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);

                        String id = String.valueOf(obj.optInt("id", 0));
                        String title = obj.optString("title", "Untitled Task");

                        tasks.add(new Task(id, title));
                    }

                    System.out.println("[API] Successfully fetched " + tasks.size() + " tasks.");

                } else {
                    System.err.println("[API] Failed to fetch todos. Status code: " + responseCode);
                }

            } catch (Exception e) {
                System.err.println("[API] Error while fetching todos:");
                e.printStackTrace();
            }

            return tasks;
        }

        public static boolean logPomodoroSession(String todoId, int sessionCount) {
            // ... (كود logPomodoroSession كما هو) ...
            try {
                URL url = new URL(BASE_URL + "/pomodoro");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "Bearer " + TOKEN);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject jsonBody = new JSONObject();
                jsonBody.put("todo_id", todoId);
                jsonBody.put("sessions_completed", sessionCount);

                System.out.println("[API] Sending Pomodoro session log...");
                System.out.println("[API] Request body: " + jsonBody);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonBody.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int code = conn.getResponseCode();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                (code >= 200 && code < 300)
                                        ? conn.getInputStream()
                                        : conn.getErrorStream(),
                                StandardCharsets.UTF_8
                        )
                );

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) response.append(line);
                reader.close();

                System.out.println("[API] Pomodoro log response (" + code + "): " + response);

                return code >= 200 && code < 300;

            } catch (Exception e) {
                System.err.println("[API] Error while logging Pomodoro session:");
                e.printStackTrace();
                return false;
            }
        }
    }

    /**
     * Custom notification popup.
     */
    public static class Notification extends Stage {

        public Notification(String title, String message, Duration duration, String type) {
            // --- Title label ---
            Label titleLabel = new Label(title);
            // ... (Styles) ...
            titleLabel.setStyle("""
                    -fx-font-weight: bold;
                    -fx-font-size: 16px;
                    -fx-text-fill: white;
                    -fx-font-family: 'Onest', sans-serif;
                    """);

            // --- Message label ---
            Label messageLabel = new Label(message);
            // ... (Styles) ...
            messageLabel.setStyle("""
                    -fx-text-fill: #E0E0E0;
                    -fx-font-size: 14px;
                    -fx-font-family: 'Onest', sans-serif;
                    -fx-font-weight: 700;
                    """);

            // --- Container layout ---
            VBox root = new VBox(10, titleLabel, messageLabel);
            root.setAlignment(Pos.CENTER_LEFT);
            root.setPadding(new javafx.geometry.Insets(15));
            root.setStyle("-fx-background-radius: 5px;");
            root.getStyleClass().add("notification-" + type);

            // --- Scene setup ---
            Scene scene = new Scene(root, 330, 80);

            // 🚨 التعديل هنا: استخدام الـ Path الصحيح لـ pomodoro.css
            // الـ Path ده بيفترض إن فولدر style موجود في الـ Root بتاع الـ Resources
            scene.getStylesheets().add(getClass().getResource("/style/pomodoro.css").toExternalForm());

            // دي عشان الـ Font، هنسيبها زي ما هي
            scene.getStylesheets().add("https://fonts.googleapis.com/css2?family=Onest:wght@100..900&display=swap");


            // --- Stage configuration ---
            setScene(scene);
            initStyle(StageStyle.UNDECORATED);
            setAlwaysOnTop(true);

            // --- Position bottom-right of screen ---
            double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
            double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
            setX(screenWidth - 350);
            setY(screenHeight - 100);

            // --- Auto-close after specified duration ---
            Timeline timeline = new Timeline(new KeyFrame(duration, e -> this.close()));
            timeline.play();
        }
    }
}