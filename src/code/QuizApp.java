import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;

import java.io.*;
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

    private static final int MAX_QUESTION_NUMBER = 10;
    private static final int START_QUESTION_NUMBER = 0;
    private static final int INIT_SCORE_VALUE = 0;
    private static final int START_MISSED_NUMBER = 0;
    private static final int INCREMENT_QUESTION_NUMBER = 1;
    private static final int CURRENT_QUES_ANS_INDEX = 1;
    private static final int SPLIT_LENGTH_NUM = 2;

    private int                 currentQuestion;
    private int                 score;
    private final TextField     answerField;
    private final Label         questionLabel;
    private final Label         scoreLabel;
    private final Button        startButton;
    private final Button        submitButton;

    private final List<String[]> missedQuestions;
    private final List<String[]> questionsList;

    // Constructor for the QuizApp class.
    public QuizApp() {
        this.missedQuestions = new ArrayList<>();
        this.questionsList = new ArrayList<>();
        this.startButton = new Button("Start Quiz");
        this.questionLabel = new Label("Press 'Start Quiz' to begin");
        this.answerField = new TextField();
        this.scoreLabel = new Label("Score: 0");
        this.submitButton = new Button("Submit");
    }

    /**
     * The main entry point for the JavaFX application.
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set.
     */
    @Override
    public void start(final Stage primaryStage){

        final Scene scene;
        final VBox root;

        // Load questions when the app starts
        loadQuestions();

        root = new VBox(10);
        root.getStyleClass().add("vbox");

        startButton.setOnAction(e -> startQuiz());
        answerField.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ENTER){
                checkAnswer();
            }
        });

        submitButton.setOnAction(e -> checkAnswer());

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

        try(final InputStream inputStream = getClass().getClassLoader().getResourceAsStream("quiz.txt")){
            assert inputStream != null;

            final BufferedReader reader;

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while((line = reader.readLine()) != null){
                String[] parts = line.split("\\|");
                if(parts.length == SPLIT_LENGTH_NUM){
                    questionsList.add(parts);
                }
            }

            // Shuffle questions to randomize them
            Collections.shuffle(questionsList);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    // Start quiz by resetting the score and question index & displaying first question.
    private void startQuiz(){
        missedQuestions.clear();
        currentQuestion = START_QUESTION_NUMBER;
        score = INIT_SCORE_VALUE;
        scoreLabel.setText("Score: 0");
        startButton.setDisable(true);

        // Shuffle questions again at the start of each quiz
        Collections.shuffle(questionsList);
        nextQuestion();
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
        correctAnswer = questionsList.get(currentQuestion)[CURRENT_QUES_ANS_INDEX];

        if(userAnswer.equalsIgnoreCase(correctAnswer)){
            score++;
        }else{
            missedQuestions.add(questionsList.get(currentQuestion));
        }

        scoreLabel.setText("Score: " +
                score);
        currentQuestion++;
        nextQuestion();
    }

    // Ends the quiz, display user final score, and show missed questions.
    private void endQuiz(){
        questionLabel.setText("Quiz Finished! Your Score: " +
                score);
        displayMissedQuestions();
        startButton.setDisable(false);
    }

    // Display list of missed questions & their correct answers in alert box.
    private void displayMissedQuestions(){

        if(!missedQuestions.isEmpty()){
            final StringBuilder missed;
            final Alert alert;
            final TextArea textArea;

            missed = new StringBuilder("Missed Questions:\n");

            for(String[] missedQuestion : missedQuestions){
                missed.append(missedQuestion[START_MISSED_NUMBER]).
                        append(" | Correct Answer: ").
                        append(missedQuestion[INCREMENT_QUESTION_NUMBER]).
                        append("\n");
            }

            textArea = new TextArea(missed.toString());
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setPrefSize(400, 200);

            final ScrollPane scrollPane = new ScrollPane(textArea);
            scrollPane.setFitToWidth(true);

            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Review Missed Questions");
            alert.getDialogPane().setContent(scrollPane);
            alert.getDialogPane().setPrefSize(420, 220);
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
