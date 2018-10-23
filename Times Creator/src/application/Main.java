package application;
	
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Region;
import javafx.fxml.FXMLLoader;

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
	 * If a non-user error occurs, this will be the default error message shown to the user.
	 */
	public static final String DEFAULT_ERROR_MESSAGE = "Oops! Something went wrong. Go to the 'About' section and contact the owner.";
	
	/**
	 * List of main CSS files.
	 */
	public static final List<String> MAIN_CSS = List.of("/resources/css/application.css");
	
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
			mainScene = loadScene(Main.class, controller, MAIN_FXML, MAIN_CSS);
		}
		catch(IOException e)
		{
			alert(DEFAULT_ERROR_MESSAGE, Alert.AlertType.ERROR);
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
	 * Loads a scene from a given location, with a given controller, and FXML file.
	 * This method is to be called when no CSS is applied to the scene.
	 * @param locationClass Class that is calling this method.
	 * @param controller An object that contains FXML hooks into the FXML file.
	 * @param fxmlFile Resource location of the FXML file in relation to the locationClass.
	 * @return a scene constructed from the FXML file, controller, and no CSS.
	 * @throws IOException If the FXML file cannot be found.
	 */
	public static <T> Scene loadScene(Class<T> locationClass, Object controller, String fxmlFile) throws IOException
	{
		return loadScene(locationClass, controller, fxmlFile, new LinkedList<String>());
	}
	
	/**
	 * Loads a scene from a given location, with a given controller, a given FXML file, and
	 * a given list of CSS files.
	 * @param locationClass Class that is calling this method.
	 * @param controller An object that contains FXML hooks into the FXML file.
	 * @param fxmlFile Resource location of the FXML file in relation to the locationClass.
	 * @param cssFiles List of resource locations of CSS files in relation to the locationClass.
	 * @return a scene constructed from the FXML file, controller, and CSS files.
	 * @throws IOException If the FXML file or any of the CSS files cannot be found.
	 */
	public static <T> Scene loadScene(Class<T> locationClass, Object controller, String fxmlFile, List<String> cssFiles) throws IOException
	{
		FXMLLoader loader = new FXMLLoader(locationClass.getResource(fxmlFile));
		loader.setController(controller);
		Region root = (Region) loader.load();
		Scene scene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());
		scene.getStylesheets().addAll(cssFiles);
		return scene;
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
