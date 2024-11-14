import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * A Simple quiz application that presents questions to the user and tracks their score.
 *
 * @author Dalraj Bains
 * @author Anil Bronson
 * @author Farzad Farzin
 * @author Arsh Mann
 * @version 1.0
 */
public class QuizApp extends Application{

    private final int MAX_QUESTION_NUMBER = 10;
    private final int START_QUESTION_NUMBER = 1;

    private int currentQuestion = 0;
    private int score = 0;
    private final List<String[]> questionsList = new ArrayList<>();
    private TextField answerField;
    private Label questionLabel, scoreLabel;
    private Button startButton;
    private final List<String[]> missedQuestions = new ArrayList<>();

    /**
     * The main entry point for the JavaFX application.
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set.
     */
    @Override
    public void start(final Stage primaryStage){

        final Scene scene;
        final Button submitButton;

        // Load questions when the app starts
        loadQuestions();

        VBox root = new VBox(10);
        root.getStyleClass().add("vbox");

        startButton = new Button("Start Quiz");
        startButton.setOnAction(e -> startQuiz());

        questionLabel = new Label("Press 'Start Quiz' to begin");
        answerField = new TextField();
        answerField.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ENTER){
                checkAnswer();
            }
        });


        submitButton = new Button("Submit");
        submitButton.setOnAction(e -> checkAnswer());

        scoreLabel = new Label("Score: 0");

        root.getChildren().addAll(startButton, questionLabel, answerField, submitButton, scoreLabel);


        scene = new Scene(root, 400, 300);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().
                getClassLoader().getResource("styles.css")).toExternalForm());
        primaryStage.setTitle("Quiz App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Loads questions from a file into the questions list.
    private void loadQuestions(){
        final BufferedReader reader;

        try(final InputStream inputStream = getClass().getClassLoader().getResourceAsStream("quiz.txt")){
            assert inputStream != null;

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while((line = reader.readLine()) != null){
                String[] parts = line.split("\\|");
                if(parts.length == 2){
                    questionsList.add(parts);
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    // Start quiz by resetting the score and question index & displaying first question.
    private void startQuiz(){
        currentQuestion = 0;
        score = 0;
        missedQuestions.clear();
        nextQuestion();
        scoreLabel.setText("Score: 0");
        startButton.setDisable(true);
    }

    // Display next question in the list/ends quiz if all questions answered.
    private void nextQuestion(){
        if(currentQuestion < MAX_QUESTION_NUMBER &&
                currentQuestion < questionsList.size()){

            questionLabel.setText(questionsList.get(currentQuestion)[START_QUESTION_NUMBER]);
            answerField.clear();
        }else{
            endQuiz();
        }
    }

    // Checks user's answer against correct answer & updates score. If answer is incorrect, question is added to missed questions list.
    private void checkAnswer(){

        final String userAnswer;
        final String correctAnswer;

        userAnswer = answerField.getText().trim();
        correctAnswer = questionsList.get(currentQuestion)[1];

        if(userAnswer.equalsIgnoreCase(correctAnswer)){
            score++;
        }else{
            missedQuestions.add(questionsList.get(currentQuestion));
        }

        scoreLabel.setText("Score: " + score);
        currentQuestion++;
        nextQuestion();
    }

    // Ends the quiz, display user final score, and show missed questions.
    private void endQuiz(){
        questionLabel.setText("Quiz Finished! Your Score: " + score);
        displayMissedQuestions();
        startButton.setDisable(false);
    }

    // Display list of missed questions & their correct answers in alert box.
    private void displayMissedQuestions(){

        if(!missedQuestions.isEmpty()){
            final StringBuilder missed;
            final Alert alert;

            missed = new StringBuilder("Missed Questions:\n");

            for(String[] missedQuestion : missedQuestions){
                missed.append(missedQuestion[0]).
                        append(" | Correct Answer: ").
                        append(missedQuestion[1]).
                        append("\n");
            }

            alert = new Alert(Alert.AlertType.INFORMATION, missed.toString(), ButtonType.OK);
            alert.setHeaderText("Review Missed Questions");
            alert.showAndWait();
        }
    }

    /**
     *  Main method to launch the JavaFX application.
     *
     * @param args CLA
     */
    public static void main(String[] args){
        launch(args);
    }
}
