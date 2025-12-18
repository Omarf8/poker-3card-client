import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;


public class JavaFXTemplate extends Application {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	//feel free to remove the starter code from this method
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
        try {
            // Read file fxml and draw interface
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/clientWelcome.fxml"));
            Parent root = loader.load();
            ClientController ctrl = loader.getController(); // Get the controller made

            Scene welcome = new Scene(root, 1000,700);
            welcome.getStylesheets().add("/styles/clientWelcomeStyle.css");
            primaryStage.setTitle("3 Card Poker");
            primaryStage.setScene(welcome);
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
	}

}
