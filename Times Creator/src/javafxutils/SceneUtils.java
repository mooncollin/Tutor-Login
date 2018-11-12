package javafxutils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Region;

/**
 * Class that contains helpful static methods for loading JavaFX Scenes.
 * @author colli
 *
 */
public class SceneUtils
{
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
		return SceneUtils.loadScene(locationClass, controller, fxmlFile, new LinkedList<String>());
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

}
