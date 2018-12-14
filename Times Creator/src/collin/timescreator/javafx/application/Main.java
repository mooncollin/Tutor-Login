package collin.timescreator.javafx.application;
	
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import collin.timescreator.javafx.utils.SceneUtils;
import collin.timescreator.util.LogLevel;
import collin.timescreator.util.LoggingThread;
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
	public static final String MAIN_FXML = "/collin/timescreator/resources/fxml/Layout.fxml";
	
	/**
	 * Tutor Login window FXML file.
	 */
	public static final String TUTOR_LOGIN_FXML = "/collin/timescreator/resources/fxml/TutorLoginLayout.fxml";
	
	/**
	 * About window FXML file.
	 */
	public static final String ABOUT_FXML = "/collin/timescreator/resources/fxml/About.fxml";
	
	/**
	 * Firefly window FXML file.
	 */
	public static final String FIREFLY_FXML = "/collin/timescreator/resources/fxml/Firefly.fxml";
	
	/**
	 * Tutorials window FXML file.
	 */
	public static final String TUTORIAL_FXML = "/collin/timescreator/resources/fxml/Tutorials.fxml";
	
	/**
	 * If a non-user error occurs, this will be the default error message shown to the user.
	 */
	public static final String DEFAULT_ERROR_MESSAGE = "Oops! Something went wrong. Go to the 'About' section and contact the owner.";
	
	/**
	 * List of main CSS files.
	 */
	public static final List<String> MAIN_CSS = List.of("/collin/timescreator/resources/css/application.css");
	
	/**
	 * A flag to set whether debug mode should be on. This enables logging for various
	 * exceptions and disables the default error alert.
	 */
	public static final boolean DEBUG_MODE = true;
	
	/**
	 * The root directory for installing the webdrivers.
	 */
	public static final Path WEBDRIVER_ROOT_DIRECTORY = Paths.get(System.getenv("SystemDrive"), "TimesCreator");
	
	/**
	 * The path for the Firefox webdriver.
	 */
	public static final Path FIREFOX_WEBDRIVER_PATH = Paths.get(WEBDRIVER_ROOT_DIRECTORY.toString(), "geckodriver.exe");
	
	/**
	 * The path for the Chrome webdriver.
	 */
	public static final Path CHROME_WEBDRIVER_PATH = Paths.get(WEBDRIVER_ROOT_DIRECTORY.toString(), "chromedriver.exe");
	
	/**
	 * The path for the Opera webdriver.
	 */
	public static final Path OPERA_WEBDRIVER_PATH = Paths.get(WEBDRIVER_ROOT_DIRECTORY.toString(), "operadriver.exe");
	
	/**
	 * A thread to manage logging.
	 */
	public static final LoggingThread loggingThread = new LoggingThread();
	
	/**
	 * The location of the Firefox Geckodriver.
	 */
	private static final String FIREFOX_DRIVER = "/collin/timescreator/resources/webdriver/geckodriver.exe";
	
	/**
	 * The location of the Chrome driver.
	 */
	private static final String CHROME_DRIVER = "/collin/timescreator/resources/webdriver/chromedriver.exe";
	
	/**
	 * The location of the Opera driver.
	 */
	private static final String OPERA_DRIVER = "/collin/timescreator/resources/webdriver/operadriver.exe";
	
	/**
	 * Sets up the controller and the scene associated with MAIN_FXML
	 * and shows the main window.
	 * @param primaryStage first stage given by the application.
	 */
	@Override
	public void start(Stage primaryStage)
	{
		loggingThread.start();
		setup();
		MainController controller = new MainController(primaryStage);
		Scene mainScene;
		try
		{
			mainScene = SceneUtils.loadScene(Main.class, controller, MAIN_FXML, MAIN_CSS);
		}
		catch(Exception e)
		{
			if(DEBUG_MODE)
			{
				log(e.getMessage(), LogLevel.DANGER);
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
	 * Installs required WebDriver executables for Selenium.
	 */
	private void setup()
	{
		try
		{
			if(!Files.isDirectory(WEBDRIVER_ROOT_DIRECTORY))
			{
				Files.createDirectory(WEBDRIVER_ROOT_DIRECTORY);
			}
			if(!Files.exists(FIREFOX_WEBDRIVER_PATH))
			{
				Files.write(FIREFOX_WEBDRIVER_PATH, getClass().getResourceAsStream(FIREFOX_DRIVER).readAllBytes());
			}
			if(!Files.exists(CHROME_WEBDRIVER_PATH))
			{
				Files.write(CHROME_WEBDRIVER_PATH, getClass().getResourceAsStream(CHROME_DRIVER).readAllBytes());
			}
			if(!Files.exists(OPERA_WEBDRIVER_PATH))
			{
				Files.write(OPERA_WEBDRIVER_PATH, getClass().getResourceAsStream(OPERA_DRIVER).readAllBytes());
			}
		}
		catch(IOException | SecurityException e)
		{
			alert("Error installing WebDrivers. Please check folder permissions.", Alert.AlertType.ERROR);
			System.exit(1);
		}
		
		try
		{
			System.setProperty("webdriver.gecko.driver", FIREFOX_WEBDRIVER_PATH.toString());
			System.setProperty("webdriver.chrome.driver", CHROME_WEBDRIVER_PATH.toString());
			System.setProperty("webdriver.opera.driver", OPERA_WEBDRIVER_PATH.toString());
		}
		catch(SecurityException e)
		{
			alert("Error installing WebDrivers. Please check your system property permissions.", Alert.AlertType.ERROR);
			System.exit(1);
		}
	}
	
	/**
	 * Launches the application.
	 * @param args not used.
	 * @throws IOException 
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
	
	/**
	 * Adds to the logging system a log to eventually print to stdout.
	 * @param message the message to log
	 * @param level the level of concern
	 */
	public static void log(String message, LogLevel level)
	{
		loggingThread.getLogger().log(message, level);
	}
}
