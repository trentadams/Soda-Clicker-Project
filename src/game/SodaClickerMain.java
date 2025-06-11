package game;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/* So basically, this file acts as the main java file that starts the program and creates the window.
 * No actual game logic exists in this file!*/
public class SodaClickerMain extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Create the main game controller which handles UI and logic
        GameController game = new GameController();

        // Set the main scene using the game's layout
        Scene scene = new Scene(game.getView(), 790, 400);

        // Setup and show the stage (window)
        primaryStage.setTitle("Soda Clicker");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Launch the JavaFX application
        launch(args);
    }
}