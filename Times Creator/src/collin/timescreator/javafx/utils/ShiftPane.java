package collin.timescreator.javafx.utils;

import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import collin.timescreator.util.Pair;
import collin.timescreator.util.Shift;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ShiftPane
{
	/**
	 * The number of rows that have been added across all ShiftPanes.
	 */
	private static int ROWS_ADDED;
	
	/**
	 * The text for the add button. 
	 */
	private static final String ADD_BUTTON_TEXT = "+";
	
	/**
	 * The text for the remove button.
	 */
	private static final String REMOVE_BUTTON_TEXT = "-";
	
	/**
	 * The default hint text for shift textfields.
	 */
	private static final String DEFAULT_PROMPT_TEXT = "(H) or (H:MM)";
	
	/**
	 * A regex pattern for unwanted characters in shift textfields.
	 */
	private static final Pattern UNALLOWED_CHARACTERS_TEXTFIELD_PATTERN = Pattern.compile("[^1234567890:]");
	
	/**
	 * A regex pattern for a bad full time format.
	 */
	private static final Pattern TIME_FORMAT_BAD = Pattern.compile("\\d{2,}:\\d{3,}");
	
	/**
	 * A regex pattern for a bad full time format.
	 */
	private static final Pattern TIME_FORMAT_BAD2 = Pattern.compile("\\d{3,}:\\d{2,}");
	
	/**
	 * A regex pattern for a bad front time format.
	 */
	private static final Pattern FRONT_TIME_FORMAT_BAD = Pattern.compile("\\d{3,}");
	
	/**
	 * A regex pattern for a bad back time format.
	 */
	private static final Pattern BACK_TIME_FORMAT_BAD = Pattern.compile(":\\d{3,}");
	
	/**
	 * A text filter that catches incoming changes to a text field and manipulates the text before it is sent to
	 * any key pressed/typed listeners and onto the screen. This is used to block unwanted characters.
	 */
	private static final UnaryOperator<TextFormatter.Change> TEXT_FILTER = new UnaryOperator<TextFormatter.Change>() {
		@Override
		public TextFormatter.Change apply(TextFormatter.Change change)
		{
			String newText = change.getText();
			String possibleNewText = change.getControlNewText();
			TextFormatter.Change result = change;
			if(UNALLOWED_CHARACTERS_TEXTFIELD_PATTERN.matcher(newText).matches())
			{
				result = null;
			}
			else if(FRONT_TIME_FORMAT_BAD.matcher(possibleNewText).matches())
			{
				result = null;
			}
			else if(BACK_TIME_FORMAT_BAD.matcher(possibleNewText).matches())
			{
				result = null;
			}
			else if(TIME_FORMAT_BAD.matcher(possibleNewText).matches()
					|| TIME_FORMAT_BAD2.matcher(possibleNewText).matches())
			{
				result = null;
			}
			else if(!possibleNewText.isEmpty())
			{
				String[] hourAndMinute = possibleNewText.split(":");
				if(hourAndMinute.length > 0)
				{
					if(hourAndMinute[0].equals(""))
					{
						result = null;
					}
					else
					{
						int hour = Integer.valueOf(hourAndMinute[0]);
						if(hour < 0 || hour > 23)
						{
							result = null;
						}
					}
				}
				if(hourAndMinute.length > 1)
				{
					int minute = Integer.valueOf(hourAndMinute[1]);
					if(minute < 0 || minute > 59)
					{
						result = null;
					}
				}
			}
			
			return result;
		}
	};
	
	/**
	 * Default margins for shift textfields.
	 */
	private static final int DEFAULT_TEXTFIELD_MARGIN = 8;
	
	/**
	 * Default amount of shifts allowed for each day.
	 */
	private static final int DEFAULT_SHIFTS_ALLOWED = 3;
	
	/**
	 * Starting size of the main stage. If it changes in the layout, 
	 * this must change as well.
	 */
	private static final double START_SIZE = 402;
	
	/**
	 * The underlying GridPane.
	 */
	private GridPane gridPane;
	
	/**
	 * The current day of the week this ShiftPane is representing.
	 */
	private String day;
	
	/**
	 * The button to add the new shift.
	 */
	private Button addButton;
	
	/**
	 * The children of the current GridPane.
	 */
	private ObservableList<Node> children;
	
	/**
	 * The TextFields of the current GridPane.
	 */
	private FilteredList<TextField> textFields;
	
	/**
	 * The maximum number of shifts allowed.
	 */
	private int shiftsAllowed;
	
	/**
	 * The stage passed from the MainController to resize when adding or
	 * removing shifts.
	 */
	private Stage mainStage;
	
	/**
	 * Constructor. Initialized variables, sets the text formatter to the current
	 * textfields, and finds the day and add button.
	 * @param gridPane the underlying GridPane
	 * @param shifts maximum number of shifts for this ShiftPane
	 * @param mainStage the stage to resize when adding or removing shifts
	 */
	@SuppressWarnings("unchecked")
	public ShiftPane(GridPane gridPane, int shifts, Stage mainStage)
	{
		this.shiftsAllowed = shifts;
		this.gridPane = gridPane;
		this.children = this.gridPane.getChildren();
		this.mainStage = mainStage;
		this.textFields = (FilteredList<TextField>) (FilteredList<?>) children.filtered(node -> node instanceof TextField);
		for(TextField field : this.textFields)
		{
			field.setTextFormatter(new TextFormatter<String>(TEXT_FILTER));
		}
		findDay();
		findAddButton();
	}
	
	/**
	 * Gets the underlying GridPane.
	 * @return the underlying GridPane
	 */
	public GridPane getGridPane()
	{
		return gridPane;
	}
	
	/**
	 * Gets the maximum number of shifts allowed.
	 * @return the number of shifts
	 */
	public int getShiftsAllowed()
	{
		return shiftsAllowed;
	}
	
	
	/**
	 * Gets the current day of the week this ShiftPane represents.
	 * @return day of the week
	 */
	public String getDay()
	{
		return day;
	}
	
	/**
	 * Clears all textfields and removes empty ones.
	 */
	public void clearTextFields()
	{
		textFields.forEach(field -> field.setText(""));
		removeEmptyTextFields();
	}
	
	/**
	 * Removes a row at a given index.
	 * @param index the index to remove the row at
	 */
	public void removeRow(int index)
	{
		if(index <= 0 || index >= gridPane.getRowCount())
			return;
		
		children.removeIf(node -> GridPane.getRowIndex(node) == index);
		addButton.setDisable(false);
		mainStage.setHeight((gridPane.getRowConstraints().get(0).getMinHeight() * 3) * --ROWS_ADDED + START_SIZE);
	}
	
	/**
	 * Deletes rows until the rows are of the given amount.
	 * If the current row size is larger than the given
	 * amount, then this will have no effect.
	 * @param rowAmount amount to delete until.
	 */
	public void deleteRowsTo(int rowAmount)
	{
		if(rowAmount <= 0)
			return;
		
		while(gridPane.getRowCount() > rowAmount)
		{
			removeRow(gridPane.getRowCount() - 1);
		}
	}
	
	/**
	 * Removes empty textfields.
	 */
	public void removeEmptyTextFields()
	{
		var emptyTextFields = textFields.filtered(field -> field.getText().isEmpty());
		var validTextFields = emptyTextFields.filtered(field -> GridPane.getRowIndex(field) != 0);
		while(!validTextFields.isEmpty())
		{
			removeRow(GridPane.getRowIndex(validTextFields.get(0)));
		}
	}
	
	/**
	 * Turns this current ShiftPane's textfield data into
	 * a list of Shift data.
	 * @return a list of Shifts
	 * @throws DateTimeException if a given string cannot be represented
	 * by the LocalTime object. Such as an hour being less than 0, or greater than 23,
	 * or minutes being less than 0, or greater than 59.
	 */
	public List<Shift> getShifts() throws DateTimeException
	{
		var emptyTextFields = textFields.filtered(field -> field.getText().isEmpty());
		if(emptyTextFields.size() == textFields.size())
			return new ArrayList<Shift>();
		List<Shift> shifts = new ArrayList<Shift>();
		
		TextField field1 = null;
		TextField field2 = null;
		
		for(TextField field : textFields)
		{
			if(field1 == null)
			{
				field1 = field;
				if(field.getText().isEmpty())
				{
					field1 = null;
				}
			}
			else if(field2 == null)
			{
				field2 = field;
				if(field2.getText().isEmpty())
				{
					field1 = null;
					field2 = null;
					continue;
				}
				String text1 = field1.getText();
				String text2 = field2.getText();
				shifts.add(new Shift(text1, text2));
				field1 = null;
				field2 = null;
			}
		}
		
		return shifts;
	}
	
	/**
	 * Sets the textfields to contain the given string data.
	 * This will automatically adjust the rows to perfectly
	 * match the given data.
	 * @param data the data to set the textfields to.
	 */
	public void setTextFields(List<Pair<String, String>> data)
	{
		deleteRowsTo(data.size());
		for(int i = gridPane.getRowCount(); i < data.size(); i++)
		{
			addRow(null);
		}
		
		var it = data.listIterator();
		while(it.hasNext())
		{
			Pair<String, String> pair = it.next();
			setTextFieldRow(it.nextIndex() - 1, pair.getFirst(), pair.getSecond());
		}
	}
	
	/**
	 * Sets a pair of textfields in a given row.
	 * @param row the row in the ShiftPane
	 * @param text1 string to set the first textfield to
	 * @param text2 string to set the second textfield to
	 */
	public void setTextFieldRow(int row, String text1, String text2)
	{
		textFields.get(row * 2).setText(text1);
		textFields.get(row * 2 + 1).setText(text2);
		
	}
	
	/**
	 * Finds the day of this ShiftPane by finding its only Label.
	 */
	private void findDay()
	{
		for(Node n : children)
		{
			if(n instanceof Label)
			{
				day = ((Label) n).getText();
				break;
			}
		}
	}
	
	/**
	 * Finds the add button of this ShiftPane by finding the only
	 * button with the "+" text.
	 */
	private void findAddButton()
	{
		for(Node n : children)
		{
			if(n instanceof Button)
			{
				Button b = (Button) n;
				if(((Button) n).getText().equals(ADD_BUTTON_TEXT))
				{
					addButton = b;
					addButton.setOnAction(this::addRow);
					break;
				}
			}
		}
	}
	
	/**
	 * Adds a new row into this ShiftPane.
	 * @param event An ActionEvent for a node.
	 */
	private void addRow(ActionEvent event)
	{
		if(gridPane.getRowCount() >= shiftsAllowed)
			return;
		
		Button newButton = new Button(REMOVE_BUTTON_TEXT);
		Label label = new Label("");
		TextField text1 = new TextField();
		TextField text2 = new TextField();
		newButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				removeRow(newButton);
			}
		});
		text1.setPromptText(DEFAULT_PROMPT_TEXT);
		text1.setTextFormatter(new TextFormatter<String>(TEXT_FILTER));
		text2.setPromptText(DEFAULT_PROMPT_TEXT);
		text2.setTextFormatter(new TextFormatter<String>(TEXT_FILTER));
		GridPane.setMargin(text1, new Insets(DEFAULT_TEXTFIELD_MARGIN - 5, DEFAULT_TEXTFIELD_MARGIN, DEFAULT_TEXTFIELD_MARGIN - 5, DEFAULT_TEXTFIELD_MARGIN));
		GridPane.setMargin(text2, new Insets(DEFAULT_TEXTFIELD_MARGIN - 5, DEFAULT_TEXTFIELD_MARGIN, DEFAULT_TEXTFIELD_MARGIN - 5, DEFAULT_TEXTFIELD_MARGIN));
		gridPane.addRow(gridPane.getRowCount(), label, newButton, text1, text2);
		if(gridPane.getRowCount() >= DEFAULT_SHIFTS_ALLOWED)
			addButton.setDisable(true);

		mainStage.setHeight((gridPane.getRowConstraints().get(0).getMinHeight() * 3) * ++ROWS_ADDED + START_SIZE);
	}
	
	/**
	 * Removes a row by using the index of the given remove button.
	 * @param removeButton the button that shares a row with
	 * the objects to remove
	 */
	private void removeRow(Button removeButton)
	{
		removeRow(GridPane.getRowIndex(removeButton));
	}
	
	
}
