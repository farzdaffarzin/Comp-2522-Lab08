import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;

import java.io.*;
import java.net.URL;
import java.util.*;

public class QuizApp extends Application {
    private final List<String[]> questionsList = new ArrayList<>();
    private int currentQuestion = 0;
    private int score = 0;
    private TextField answerField;
    private Label questionLabel, scoreLabel;
    private Button startButton;
    private final List<String[]> missedQuestions = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        // Load questions when the app starts
        loadQuestions();

        VBox root = new VBox(10);
        root.getStyleClass().add("vbox");

        startButton = new Button("Start Quiz");
        startButton.setOnAction(e -> startQuiz());

        questionLabel = new Label("Press 'Start Quiz' to begin");
        answerField = new TextField();
        answerField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                checkAnswer();
            }
        });

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> checkAnswer());

        scoreLabel = new Label("Score: 0");

        root.getChildren().addAll(startButton, questionLabel, answerField, submitButton, scoreLabel);

        Scene scene = new Scene(root, 400, 300);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("styles.css")).toExternalForm());
        primaryStage.setTitle("Quiz App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadQuestions() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("quiz.txt")) {
            assert inputStream != null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    questionsList.add(parts);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startQuiz() {
        currentQuestion = 0;
        score = 0;
        missedQuestions.clear();
        nextQuestion();
        scoreLabel.setText("Score: 0");
        startButton.setDisable(true);
    }

    private void nextQuestion() {
        if (currentQuestion < 10 && currentQuestion < questionsList.size()) {
            questionLabel.setText(questionsList.get(currentQuestion)[0]);
            answerField.clear();
        } else {
            endQuiz();
        }
    }

    private void checkAnswer() {
        String userAnswer = answerField.getText().trim();
        String correctAnswer = questionsList.get(currentQuestion)[1];

        if (userAnswer.equalsIgnoreCase(correctAnswer)) {
            score++;
        } else {
            missedQuestions.add(questionsList.get(currentQuestion));
        }

        scoreLabel.setText("Score: " + score);
        currentQuestion++;
        nextQuestion();
    }

    private void endQuiz() {
        questionLabel.setText("Quiz Finished! Your Score: " + score);
        displayMissedQuestions();
        startButton.setDisable(false);
    }

    private void displayMissedQuestions() {
        if (!missedQuestions.isEmpty()) {
            StringBuilder missed = new StringBuilder("Missed Questions:\n");
            for (String[] missedQuestion : missedQuestions) {
                missed.append(missedQuestion[0]).append(" | Correct Answer: ").append(missedQuestion[1]).append("\n");
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION, missed.toString(), ButtonType.OK);
            alert.setHeaderText("Review Missed Questions");
            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}