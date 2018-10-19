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


public class Main extends Application
{
	public static final String MAIN_FXML = "/resources/fxml/Layout.fxml";
	public static final String TUTOR_LOGIN_FXML = "/resources/fxml/TutorLoginLayout.fxml";
	public static final String DEFAULT_ERROR_MESSAGE = "Oops! Something went wrong. Go to the 'About' section and contact the owner.";
	public static final List<String> MAIN_CSS = List.of("/resources/css/application.css");
	
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
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public static <T> Scene loadScene(Class<T> locationClass, Object controller, String fxmlFile) throws IOException
	{
		return loadScene(locationClass, controller, fxmlFile, new LinkedList<String>());
	}
	
	public static <T> Scene loadScene(Class<T> locationClass, Object controller, String fxmlFile, List<String> cssFiles) throws IOException
	{
		FXMLLoader loader = new FXMLLoader(locationClass.getResource(fxmlFile));
		loader.setController(controller);
		Region root = (Region) loader.load();
		Scene scene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());
		scene.getStylesheets().addAll(cssFiles);
		return scene;
	}
	
	public static void alert(String message, Alert.AlertType type)
	{
		Alert alert = new Alert(type, message);
		alert.show();
	}
}
