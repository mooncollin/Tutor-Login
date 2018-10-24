package application;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import util.Pair;
import util.Shift;
import util.ShiftPane;

/**
 * This controller contains FXML hooks into the MAIN_FXML file.
 * It is responsible for all the actions done with the main window.
 * @author colli
 *
 */
public class MainController
{
	/**
	 * Filter for saving files.
	 */
	private static final ExtensionFilter[] filter = {new ExtensionFilter("Text Files", "*.txt")};
	
	/**
	 * Default amount of shifts allowed for each day.
	 */
	private static final int DEFAULT_SHIFTS_ALLOWED = 3;
	
	/**
	 * Responsible for easy saving and loading files.
	 */
	private static final FileChooser fileChooser = new FileChooser();
	
	/**
	 * The number of gridpanes on the main window. One for each
	 * day of the month.
	 */
	private static final int NUMBER_OF_GRIDPANES = 7;
	
	/**
	 * A regex pattern that helps parse text when reading in a file.
	 */
	private static final Pattern READ_LINE_PATTERN = Pattern.compile("(\\d{1,2}):?(\\d{1,2}?)-(\\d{1,2}):?(\\d{1,2}?)(?:$|,)");
	
	/**
	 * Top-level region of the Layout.fxml layout.
	 */
	@FXML
	private BorderPane window;
	
	/**
	 * The region containing all the gridpanes.
	 */
	@FXML
	private VBox daysContainer;
	
	/**
	 * Main Layout.fxml stage.
	 */
	private Stage stage;
	
	/**
	 * The custom panes list.
	 */
	private static final List<ShiftPane> panes = new ArrayList<ShiftPane>(NUMBER_OF_GRIDPANES);
	
	/**
	 * Stage for the tutor login scene.
	 */
	private Stage tutorLoginStage;
	
	/**
	 * Stage for the about scene.
	 */
	private Stage aboutStage;
	
	/**
	 * A string to keep hold of the email to load it back 
	 * when the "tutor login" scene comes back.
	 */
	private String tutorEmail;
	
	/**
	 * A string to keep hold of the netID to load it back
	 * when the "tutor login" scene comes back.
	 */
	private String tutorNetID;
	
	/**
	 * A string to keep hold of the password to load it back 
	 * when the "tutor login" scene comes back.
	 */
	private String tutorPassword;
	
	/**
	 * This constructor sets up various maps and initializes some variables.
	 * @param stage Stage for the main layout.
	 */
	public MainController(Stage stage)
	{
		setStage(stage);
		this.stage.setOnCloseRequest(e -> {
			if(tutorLoginStage != null)
			{
				tutorLoginStage.close();
			}
		});
		tutorEmail = "";
		tutorNetID = "";
		tutorPassword = "";
		
		tutorLoginStage = new Stage();
		tutorLoginStage.setTitle("Tutor Login");
		tutorLoginStage.setResizable(false);
		
		aboutStage = new Stage();
		aboutStage.setTitle("About");
		aboutStage.setResizable(false);
		
		fileChooser.getExtensionFilters().addAll(filter);
	}
	
	/**
	 * This method is called after all FXML components
	 * have been loaded and references set. This will fill 
	 * various arrays as well as fill the textfield arraylist 
	 * with the initial textfields.
	 */
	@FXML
	private void initialize()
	{
		daysContainer.getChildren()
			  .filtered(node -> node instanceof GridPane)
			  .forEach(node -> {
				  GridPane gridPane = (GridPane) node;
				  gridPane.getChildren().forEach(gridNode -> {
					  if(GridPane.getRowIndex(gridNode) == null)
					  {
						  GridPane.setRowIndex(gridNode, 0);
					  }
				  });
				  panes.add(new ShiftPane(gridPane, DEFAULT_SHIFTS_ALLOWED, stage));
			  });
	}
	
	/**
	 * The save feature implements this onAction method. 
	 * As long as the values of the textfields are correct, 
	 * it will prompt the user to save it.
	 * @param event An ActionEvent from a node.
	 */
	@FXML
	public void saveFile(ActionEvent event)
	{
		String output = "";
		
		int counter = 1;
		boolean isEmpty = true;
		for(ShiftPane pane : panes)
		{
			List<Shift> shifts;
			try
			{
				shifts = pane.getShifts();
			}
			catch(DateTimeException e)
			{
				Main.alert("Invalid Time", AlertType.ERROR);
				return;
			}
			if(!shifts.isEmpty())
			{
				isEmpty = false;
			}
			else
			{
				output += "None";
			}
			for(int i = 0; i < shifts.size(); i++)
			{
				if(i != 0)
					output += ",";
				output += shifts.get(i);
			}
			if(counter != NUMBER_OF_GRIDPANES)
			{
				output += "\n";
			}
			counter++;
		}
		
		if(isEmpty)
		{
			Main.alert("Please complete various textfields", AlertType.ERROR);
			return;
		}
		
		fileChooser.setTitle("Save Schedule File");
		File outputFile = fileChooser.showSaveDialog(null);
		
		try
		{
			if(outputFile != null)
				Files.write(Paths.get(outputFile.getAbsolutePath()), output.getBytes());
		}
		catch (IOException e)
		{
			Main.alert("File could not be saved", AlertType.ERROR);
		}
	}
	
	/**
	 * The load feature implements this onAction method. 
	 * It will load in information and fill the textfields 
	 * with the appropriate information.
	 * @param event An ActionEvent for a node.
	 */
	@FXML
	public void loadFile(ActionEvent event)
	{
		fileChooser.setTitle("Open Schedule File");
		File selectedFile = fileChooser.showOpenDialog(null);
		if(selectedFile != null)
		{
			fillShifts(selectedFile);
		}
	}
	
	/**
	 * The clear feature implements this onAction method. 
	 * It will clear any data in the textfields.
	 * @param event An ActionEvent for a node.
	 */
	@FXML
	public void clearMenu(ActionEvent event)
	{
		panes.forEach(pane -> pane.clearTextFields());
	}
	
	/**
	 * The login feature implements this onAction method. 
	 * Creates a controller for the new stage and gives the controller 
	 * the time information and shows the "tutor login" stage.
	 * @param event An ActionEvent for a node.
	 * @throws IOException if it cannot find the TutorLoginLayout.fxml resource.
	 */
	@FXML
	private void tutorLoginMenu(ActionEvent event) throws IOException
	{
		var shifts = createShifts();
		if(shifts == null)
			return;
		
		window.setDisable(true);
		TutorLoginController controller = new TutorLoginController(shifts, tutorEmail,
				tutorNetID, tutorPassword);
		Scene scene = Main.loadScene(this.getClass(), controller, Main.TUTOR_LOGIN_FXML, Main.MAIN_CSS);
		tutorLoginStage.setScene(scene);
		tutorLoginStage.show();
		tutorLoginStage.setOnCloseRequest(e -> {
			controller.stopThread();
			window.setDisable(false);
			tutorEmail = controller.getEmail();
			tutorNetID = controller.getNetID();
			tutorPassword = controller.getPassword();
		});
	}
	
	/**
	 * The about feature implements this onAction method.
	 * It shows the 'about' information on this software.
	 * @param event An ActionEvent for a node.
	 * @throws IOException if it cannot find the About.fxml resource.
	 */
	@FXML
	private void aboutMenu(ActionEvent event) throws IOException
	{
		Scene scene = Main.loadScene(this.getClass(), null, Main.ABOUT_FXML, Main.MAIN_CSS);
		aboutStage.setScene(scene);
		aboutStage.show();
	}
	
	/**
	 * Sets the current controller's stage.
	 * @param stage A stage for this controller.
	 */
	public void setStage(Stage stage)
	{
		this.stage = stage;
	}
	
	/**
	 * Reads data from a file and fills the textfields with 
	 * that data. Will add or remove rows to fill the correct 
	 * amount of data.
	 * @param file File to read shift data from.
	 */
	private void fillShifts(File file)
	{
		if(file == null)
			return;
		
		List<String> fileLines;
		try
		{
			fileLines = Files.readAllLines(Paths.get(file.getAbsolutePath()));
		}
		catch (IOException e)
		{
			return;
		}
		
		if(fileLines.size() != NUMBER_OF_GRIDPANES)
			return;
		
		for(int i = 0; i < fileLines.size(); i++)
		{
			panes.get(i).clearTextFields();
			Matcher match = READ_LINE_PATTERN.matcher(fileLines.get(i));
			List<Pair<String, String>> data = new ArrayList<Pair<String, String>>();
			while(match.find())
			{
				String text1 = String.format("%s:%s", match.group(1), match.group(2));
				String text2 = String.format("%s:%s", match.group(3), match.group(4));
				data.add(new Pair<String, String>(text1, text2));
			}
			panes.get(i).setTextFields(data);
		}
	}
	
	/**
	 * Converts data from the shift textfields to a map that links 
	 * a day of the week with a list of shifts.
	 * @return map that links a day of the week with a list of shifts.
	 */
	private HashMap<String, List<Shift>> createShifts()
	{
		HashMap<String, List<Shift>> shifts = new HashMap<String, List<Shift>>(NUMBER_OF_GRIDPANES);
		
		for(ShiftPane pane : panes)
		{
			shifts.put(pane.getDay(), pane.getShifts());
		}
		
		return shifts;
	}
}
