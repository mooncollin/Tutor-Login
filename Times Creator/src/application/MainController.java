package application;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.DateTimeException;
import java.time.LocalTime;
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
import javafxutils.SceneUtils;
import javafxutils.ShiftPane;
import javafx.stage.Stage;
import util.Pair;
import util.Shift;

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
	 * Default alert message when a shift's start if after or equal to
	 * its end time.
	 */
	private static final String INVALID_START_AND_END_TIMES = "Start times must be before end times";
	
	/**
	 * Default alert message when no shift information has been entered.
	 */
	private static final String NO_TIMES_ENTERED = "No times entered";
	
	/**
	 * Default alert message when two or more shifts overlap.
	 */
	private static final String OVERLAP_TIMES = "Shifts cannot overlap";
	
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
	 * Stage for the firefly scene;
	 */
	private Stage fireflyStage;
	
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
	
	private int fireflyNUID;
	
	private String fireflyPassword;
	
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
				if(tutorLoginStage.getOnCloseRequest() != null)
					tutorLoginStage.getOnCloseRequest().handle(null);
				tutorLoginStage.close();
			}
			if(aboutStage != null)
			{
				aboutStage.close();
			}
			if(fireflyStage != null)
			{
				if(fireflyStage.getOnCloseRequest() != null)
					fireflyStage.getOnCloseRequest().handle(null);
				fireflyStage.close();
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
		
		fireflyStage = new Stage();
		fireflyStage.setTitle("Firefly Upload Hours");
		fireflyStage.setResizable(false);
		
		fileChooser.getExtensionFilters().addAll(filter);
		
		fireflyNUID = -1;
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
		String alertMessage = ShiftCompare.toAlertString(correctShifts());
		if(alertMessage != null)
		{
			Main.alert(alertMessage, AlertType.ERROR);
			return;
		}
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
			Main.alert(NO_TIMES_ENTERED, AlertType.ERROR);
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
		String alertMessage = ShiftCompare.toAlertString(correctShifts());
		if(alertMessage != null)
		{
			Main.alert(alertMessage, AlertType.ERROR);
			return;
		}
		var shifts = createShifts();
		boolean allEmpty = true;
		for(String key : shifts.keySet())
		{
			if(!shifts.get(key).isEmpty())
			{
				allEmpty = false;
				break;
			}
		}
		if(allEmpty)
		{
			Main.alert(NO_TIMES_ENTERED, AlertType.ERROR);
			return;
		}
		
		window.setDisable(true);
		TutorLoginController controller = new TutorLoginController(shifts, tutorEmail,
				tutorNetID, tutorPassword);
		Scene scene = SceneUtils.loadScene(this.getClass(), controller, Main.TUTOR_LOGIN_FXML, Main.MAIN_CSS);
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
		Scene scene = SceneUtils.loadScene(this.getClass(), null, Main.ABOUT_FXML, Main.MAIN_CSS);
		aboutStage.setScene(scene);
		aboutStage.show();
	}
	
	/**
	 * The firefly feature implements this onAction method.
	 * It will show the firefly scene.
	 * @param event An ActionEvent for a node.
	 * @throws IOException if it cannot find the Firefly.fxml resource.
	 */
	@FXML
	private void fireflyMenu(ActionEvent event) throws IOException
	{
		String alertMessage = ShiftCompare.toAlertString(correctShifts());
		if(alertMessage != null)
		{
			Main.alert(alertMessage, AlertType.ERROR);
			return;
		}
		var shifts = createShifts();
		boolean allEmpty = true;
		for(String key : shifts.keySet())
		{
			if(!shifts.get(key).isEmpty())
			{
				allEmpty = false;
				break;
			}
		}
		if(allEmpty)
		{
			Main.alert(NO_TIMES_ENTERED, AlertType.ERROR);
			return;
		}
		
		window.setDisable(true);
		FireflyController controller = new FireflyController(shifts, fireflyNUID, fireflyPassword);
		Scene scene = SceneUtils.loadScene(this.getClass(), controller, Main.FIREFLY_FXML, Main.MAIN_CSS);
		fireflyStage.setScene(scene);
		fireflyStage.show();
		fireflyStage.setOnCloseRequest(e -> {
			controller.stopThread();
			window.setDisable(false);
			fireflyNUID = controller.getNUID();
			fireflyPassword = controller.getPassword();
		});
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
	
	/**
	 * Checks if the current shifts have start times before end times
	 * and that there is no overlap between shifts.
	 * @return a result from comparing shifts
	 */
	private ShiftCompare correctShifts()
	{
		for(ShiftPane pane : panes)
		{
			LocalTime lastStop = null;
			int counter = 1;
			for(Shift shift : pane.getShifts())
			{
				if(shift.getStart().isAfter(shift.getStop()) || shift.getStart().equals(shift.getStop()))
					return ShiftCompare.END_BEFORE_START;
				if(counter > 1)
				{
					if(lastStop.isAfter(shift.getStart()) || lastStop.equals(shift.getStart()))
						return ShiftCompare.SHIFT_END_OVERLAP;
				}
				counter++;
				lastStop = shift.getStop();
			}
			
		}
		
		return ShiftCompare.CORRECT;
	}
	
	/**
	 * Constants that illistrate the outcomes of
	 * comparing shifts.
	 * @author colli
	 *
	 */
	private enum ShiftCompare
	{
		CORRECT, END_BEFORE_START, SHIFT_END_OVERLAP;
		
		/**
		 * Converts a ShiftCompare result to its equivalent
		 * alert message, or null if correct.
		 * @param compareValue a result from comparing all shifts
		 * from a certain day
		 * @return an alert message, or null if correct
		 */
		public static String toAlertString(ShiftCompare compareValue)
		{
			String alert = null;
			switch(compareValue)
			{
				case END_BEFORE_START:
					alert = INVALID_START_AND_END_TIMES;
					break;
				case SHIFT_END_OVERLAP:
					alert = OVERLAP_TIMES;
					break;
				case CORRECT:
					break;
			}
			
			return alert;
		}
	}
}
