package util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ShiftPane
{
	private static int ROWS_ADDED;
	private static final String ADD_BUTTON_TEXT = "+";
	private static final String REMOVE_BUTTON_TEXT = "-";
	private static final String DEFAULT_PROMPT_TEXT = "(H) or (H:MM)";
	private static final Pattern UNALLOWED_CHARACTERS_TEXTFIELD_PATTERN = Pattern.compile("[^1234567890:]");
	
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
	
	private GridPane gridPane;
	private String day;
	private Button addButton;
	private ObservableList<Node> children;
	private FilteredList<TextField> textFields;
	private FilteredList<TextField> emptyTextFields;
	private int shiftsAllowed;
	private Stage mainStage;
	
	@SuppressWarnings("unchecked")
	public ShiftPane(GridPane gridPane, int shifts, Stage mainStage)
	{
		this.shiftsAllowed = shifts;
		this.gridPane = gridPane;
		this.children = this.gridPane.getChildren();
		this.mainStage = mainStage;
		this.textFields = (FilteredList<TextField>) (FilteredList<?>) children.filtered(node -> node instanceof TextField);
		this.emptyTextFields = this.textFields.filtered(field -> field.getText().isEmpty());
		findDay();
		findAddButton();
	}
	
	public GridPane getGridPane()
	{
		return gridPane;
	}
	
	public int getShiftsAllowed()
	{
		return shiftsAllowed;
	}
	
	public String getDay()
	{
		return day;
	}
	
	public void clearTextFields()
	{
		textFields.forEach(field -> field.setText(""));
		removeEmptyTextFields();
	}
	
	public void removeRow(int index)
	{
		if(index <= 0 || index >= gridPane.getRowCount())
			return;
		
		children.removeIf(node -> GridPane.getRowIndex(node) == index);
		addButton.setDisable(false);
		mainStage.setHeight((gridPane.getRowConstraints().get(0).getMinHeight() * 3) * --ROWS_ADDED + START_SIZE);
	}
	
	public void deleteRowsTo(int rowAmount)
	{
		if(rowAmount <= 0)
			return;
		
		while(gridPane.getRowCount() > rowAmount)
		{
			removeRow(gridPane.getRowCount() - 1);
		}
	}
	
	public void removeEmptyTextFields()
	{
		var validTextFields = emptyTextFields.filtered(field -> GridPane.getRowIndex(field) != 0);
		while(!validTextFields.isEmpty())
		{
			removeRow(GridPane.getRowIndex(validTextFields.get(0)));
		}
	}
	
	public List<Shift> getShifts()
	{
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
			}
			else if(field2 == null)
			{
				field2 = field;
				String text1 = field1.getText();
				String text2 = field2.getText();
				if(UNALLOWED_CHARACTERS_TEXTFIELD_PATTERN.matcher(text1).matches()
					|| UNALLOWED_CHARACTERS_TEXTFIELD_PATTERN.matcher(text2).matches())
				{
					return null;
				}
				else
				{
					shifts.add(new Shift(text1, text2));
					field1 = null;
					field2 = null;
				}
			}
		}
		
		return shifts;
	}
	
	public void setTextFields(List<Pair<String, String>> data)
	{
		deleteRowsTo(data.size());
		for(int i = gridPane.getRowCount() - 1; i < data.size(); i++)
		{
			addTimes(null);
		}
		
		var it = data.listIterator();
		while(it.hasNext())
		{
			Pair<String, String> pair = it.next();
			setTextFieldRow(it.nextIndex() - 1, pair.getFirst(), pair.getSecond());
		}
	}
	
	public void setTextFieldRow(int row, String text1, String text2)
	{
		textFields.get(row * 2).setText(text1);
		textFields.get(row * 2 + 1).setText(text2);
		
	}
	
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
					addButton.setOnAction(this::addTimes);
					break;
				}
			}
		}
	}
	
	private void addTimes(ActionEvent event)
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
		text2.setPromptText(DEFAULT_PROMPT_TEXT);
		GridPane.setMargin(text1, new Insets(DEFAULT_TEXTFIELD_MARGIN - 5, DEFAULT_TEXTFIELD_MARGIN, DEFAULT_TEXTFIELD_MARGIN - 5, DEFAULT_TEXTFIELD_MARGIN));
		GridPane.setMargin(text2, new Insets(DEFAULT_TEXTFIELD_MARGIN - 5, DEFAULT_TEXTFIELD_MARGIN, DEFAULT_TEXTFIELD_MARGIN - 5, DEFAULT_TEXTFIELD_MARGIN));
		gridPane.addRow(gridPane.getRowCount(), label, newButton, text1, text2);
		if(gridPane.getRowCount() >= DEFAULT_SHIFTS_ALLOWED)
			addButton.setDisable(true);

		mainStage.setHeight((gridPane.getRowConstraints().get(0).getMinHeight() * 3) * ++ROWS_ADDED + START_SIZE);
	}
	
	private void removeRow(Button removeButton)
	{
		removeRow(GridPane.getRowIndex(removeButton));
	}
}
