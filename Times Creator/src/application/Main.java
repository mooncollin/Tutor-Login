package application;
	
import java.io.IOException;
import java.util.List;

import javafx.SceneUtils;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;

/**
 * Main application. This starts the first window,
 * and also contains useful static methods.
 * @author colli
 *
 */
public class Main extends Application
{
	/**
	 * Main FXML file that will be the first window shown.
	 */
	public static final String MAIN_FXML = "/resources/fxml/Layout.fxml";
	
	/**
	 * Tutor Login window FXML file.
	 */
	public static final String TUTOR_LOGIN_FXML = "/resources/fxml/TutorLoginLayout.fxml";
	
	/**
	 * About window FXML file.
	 */
	public static final String ABOUT_FXML = "/resources/fxml/About.fxml";
	
	/**
	 * Firefly window FXML file.
	 */
	public static final String FIREFLY_FXML = "/resources/fxml/Firefly.fxml";
	
	/**
	 * If a non-user error occurs, this will be the default error message shown to the user.
	 */
	public static final String DEFAULT_ERROR_MESSAGE = "Oops! Something went wrong. Go to the 'About' section and contact the owner.";
	
	/**
	 * List of main CSS files.
	 */
	public static final List<String> MAIN_CSS = List.of("/resources/css/application.css");
	
	/**
	 * A flag to set whether debug mode should be on. This enables logging for various
	 * exceptions and disables the default error alert.
	 */
	private static final boolean DEBUG_MODE = false;
	
	/**
	 * Sets up the controller and the scene associated with MAIN_FXML
	 * and shows the main window.
	 * @param primaryStage first stage given by the application.
	 */
	@Override
	public void start(Stage primaryStage)
	{
		MainController controller = new MainController(primaryStage);
		Scene mainScene;
		try
		{
			mainScene = SceneUtils.loadScene(Main.class, controller, MAIN_FXML, MAIN_CSS);
		}
		catch(IOException e)
		{
			if(DEBUG_MODE)
			{
				e.printStackTrace();
			}
			else
			{
				alert(DEFAULT_ERROR_MESSAGE, Alert.AlertType.ERROR);
			}
			return;
		}
		primaryStage.setScene(mainScene);
		primaryStage.setResizable(false);
		primaryStage.setTitle("Times Creator");
		primaryStage.show();
	}
	
	/**
	 * Launches the application.
	 * @param args not used.
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
	/**
	 * Alerts the user with a popup of a given message and picture
	 * indicating the alert severity.
	 * @param message message the user will see.
	 * @param type severity of the alert.
	 */
	public static void alert(String message, Alert.AlertType type)
	{
		Alert alert = new Alert(type, message);
		alert.show();
	}
}
