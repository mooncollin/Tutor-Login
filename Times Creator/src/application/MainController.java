package application;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
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
	
	private static final int NUMBER_OF_GRIDPANES = 7;
	
	/**
	 * Top-level region of the Layout.fxml layout.
	 */
	@FXML
	private BorderPane window;
	
	@FXML
	private VBox daysContainer;
	
	/**
	 * Main Layout.fxml stage.
	 */
	private Stage stage;
	
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
//		if(!checkTextFields())
//		{
//			return;
//		}
//		
//		String output = "";
//		for(int i = 0; i < DAYS_OF_WEEKS.length; i++)
//		{
//			ArrayList<Pair<TextField, TextField>> list = dayTextFields.get(DAYS_OF_WEEKS[i]);
//			boolean isEmpty = true;
//			for(int j = 0; j < list.size(); j++)
//			{
//				String textFirst = list.get(j).getFirst().getText();
//				String textSecond = list.get(j).getSecond().getText();
//				String[] firstParse = textFirst.split(":");
//				String[] secondParse = textSecond.split(":");
//				if(!textFirst.isEmpty() && !textSecond.isEmpty() && firstParse.length != 0 && secondParse.length != 0)
//				{
//					isEmpty = false;
//					output += String.format("%s:%02d-%s:%02d",
//							firstParse[0],
//							firstParse.length == 1 ? 0 : Integer.parseInt(firstParse[1]),
//							secondParse[0],
//							secondParse.length == 1 ? 0 : Integer.parseInt(secondParse[1]));
//				}
//				if(j < list.size() - 1 && !list.get(j + 1).getFirst().getText().isEmpty() && !list.get(j + 1).getSecond().getText().isEmpty())
//					output += ",";
//			}
//			if(isEmpty)
//				output += "None";
//			if(i < DAYS_OF_WEEKS.length - 1)
//				output += "\n";
//		}
//		
//		fileChooser.setTitle("Save Schedule File");
//		File outputFile = fileChooser.showSaveDialog(null);
//		
//		try
//		{
//			if(outputFile != null)
//				Files.write(Paths.get(outputFile.getAbsolutePath()), output.getBytes());
//		}
//		catch (IOException e)
//		{
//			Main.alert("File could not be saved", AlertType.ERROR);
//		}
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
	 * @throws IOException Exception that will throw if it cannot 
	 * find the TutorLoginLayout.fxml resource.
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
	 * Data validates the shift textfields and will alert 
	 * the user if anything is wrong.
	 * @return true if textfields pass data validation, 
	 * false otherwise.
	 */
	private boolean checkTextFields()
	{
//		boolean allEmpty = true;
//		for(int i = 0; i < DAYS_OF_WEEKS.length; i++)
//		{
//			ArrayList<Pair<TextField, TextField>> list = dayTextFields.get(DAYS_OF_WEEKS[i]);
//			for(int j = 0; j < list.size(); j++)
//			{
//				String textFirst = list.get(j).getFirst().getText();
//				String textSecond = list.get(j).getSecond().getText();
//				if(!textFirst.isEmpty() || !textSecond.isEmpty())
//					allEmpty = false;
//				try
//				{
//					String errorFirstText = checkText(textFirst);
//					if(errorFirstText != null)
//					{
//						Main.alert(errorFirstText, AlertType.ERROR);
//						return false;
//					}
//					String errorSecondText = checkText(textSecond);
//					if(errorSecondText != null)
//					{
//						Main.alert(errorSecondText, AlertType.ERROR);
//						return false;
//					}
//					if(textFirst.isEmpty() && !textSecond.isEmpty()
//							||
//					   !textFirst.isEmpty() && textSecond.isEmpty())
//					{
//						Main.alert("A shift must have a start and end", AlertType.ERROR);
//						return false;
//					}
//					double firstText = calculateTimeAmount(textFirst);
//					double secondText = calculateTimeAmount(textSecond);
//					if(firstText >= secondText && firstText != 0 && secondText != 0)
//					{
//						Main.alert("Start time must be before finish time", AlertType.ERROR);
//						return false;
//					}
//				}
//				catch(NumberFormatException e)
//				{
//					Main.alert("Invalid Time", AlertType.ERROR);
//					return false;
//				}
//			}
//		}
//		if(allEmpty)
//		{
//			Main.alert("No times entered", AlertType.WARNING);
//			return false;
//		}
		return true;
	}
	
	/**
	 * Calculates from a string the time amount in 24 
	 * hour format.
	 * @param text The string to extract the time amount from.
	 * @return the time amount.
	 */
	private double calculateTimeAmount(String text)
	{
		String[] hourAndMinute = text.split(":");
		double amount = 0;
		if(hourAndMinute.length > 0 && !hourAndMinute[0].isEmpty())
			amount += Integer.valueOf(hourAndMinute[0]);
		if(hourAndMinute.length > 1)
			amount += Integer.valueOf(hourAndMinute[1]) / 60.0;
		return amount;
	}
	
	/**
	 * Data validates a string of text.
	 * @param text string to validate text.
	 * @return an error string, or null if no error occurs.
	 * @throws NumberFormatException If the string contains a character 
	 * that is not a number (ignoring any ':' characters).
	 */
	private String checkText(String text) throws NumberFormatException
	{
		if(text == null)
			throw new NullPointerException();
		if(text.equals(""))
			return null;
		
		String error = null;
		String[] minuteAndHour = text.split(":");
		if(text.matches("[^1234567890:]*"))
		{
			error = "Time Fields cannot contain those symbols";
		}
		else if(text.length() > 5)
		{
			error = "Invalid Time"; 
		}
		else if(minuteAndHour.length >= 1)
		{
			if(Integer.valueOf(minuteAndHour[0]) > 24 || Integer.valueOf(minuteAndHour[0]) < 0)
			{
				error = "Invalid Time";
			}
			if(minuteAndHour.length >= 2)
			{
				if(Integer.valueOf(minuteAndHour[1]) > 60 || Integer.valueOf(minuteAndHour[1]) < 0)
				{
					error = "Invalid Time";
				}
			}
		}
		return error;
	}
	
	/**
	 * Reads data from a file and fills the textfields with 
	 * that data. Will add or remove rows to fill the correct 
	 * amount of data.
	 * @param file File to read shift data from.
	 */
	private void fillShifts(File file)
	{
//		if(file == null)
//			return;
//		
//		List<String> fileLines;
//		try
//		{
//			fileLines = Files.readAllLines(Paths.get(file.getAbsolutePath()));
//		}
//		catch (IOException e)
//		{
//			return;
//		}
//		
//		if(fileLines.size() != 7)
//			return;
//		
//		
//		
//		for(int i = 0; i < fileLines.size(); i++)
//		{
//			String times = fileLines.get(i);
//			if(!times.equals("None"))
//			{
//				String[] shiftsStr = times.split(",");
//				while(dayTextFields.get(DAYS_OF_WEEKS[i]).size() < shiftsStr.length
//						&& dayTextFields.get(DAYS_OF_WEEKS[i]).size() < DEFAULT_SHIFTS_ALLOWED)
//				{ 
//					addTimes(buttons[i]);
//				}
//				removeRows(DAYS_OF_WEEKS[i], shiftsStr.length);
//				for(int j = 0; j < shiftsStr.length; j++)
//				{
//					String[] startAndEnd = shiftsStr[j].split("-");
//					dayTextFields.get(DAYS_OF_WEEKS[i]).get(j).getFirst().setText(startAndEnd[0]);
//					dayTextFields.get(DAYS_OF_WEEKS[i]).get(j).getSecond().setText(startAndEnd[1]);
//				}
//			}
//			else
//			{
//				for(int rowRemove = dayTextFields.get(DAYS_OF_WEEKS[i]).size() - 1; rowRemove > 0; rowRemove--)
//				{
//					removeRow(DAYS_OF_WEEKS[i], rowRemove);
//				}
//			}
//		}
	}
	
	/**
	 * Converts data from the shift textfields to a map that links 
	 * a day of the week with a list of shifts.
	 * @return map that links a day of the week with a list of shifts.
	 */
	private HashMap<String, ArrayList<Shift>> createShifts()
	{
		if(!checkTextFields())
			return null;
		
//		HashMap<String, ArrayList<Shift>> shifts = new HashMap<String, ArrayList<Shift>>(DAYS_OF_WEEKS.length);
		
//		for(String key : DAYS_OF_WEEKS)
//		{
//			shifts.put(key, new ArrayList<Shift>(dayTextFields.get(key).size()));
//		}
//		
//		for(String key : dayTextFields.keySet())
//		{
//			for(Pair<TextField, TextField> pair : dayTextFields.get(key))
//			{
//				Shift shift;
//				String start = pair.getFirst().getText();
//				String stop = pair.getSecond().getText();
//				String[] first = start.split(":");
//				String[] second = stop.split(":");
//				
//				if(first.length == 1)
//					start = String.join(":", first[0], "00");
//				if(second.length == 1)
//					stop = String.join(":", second[0], "00");
//				if(first[0].isEmpty() || second[0].isEmpty())
//				{
//					shift = null;
//				}
//				else
//				{
//					shift = new Shift(start, stop);
//				}
//				shifts.get(key).add(shift);
//			}
//		}
		
//		return shifts;
		return null;
	}
}
