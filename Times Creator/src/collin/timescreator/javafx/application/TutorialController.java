package collin.timescreator.javafx.application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class TutorialController
{
	/**
	 * The CSS class for when a hyperlink is selected.
	 */
	private static final String SELECTED_CLASS = "selected_hyperlink";
	
	/**
	 * The CSS class for when a hyperlink is unselected.
	 */
	private static final String UNSELECTED_CLASS = "unselected_hyperlink";
	
	/**
	 * The CSS class for underlying something.
	 */
	private static final String UNDERLINED_CLASS = "underlined";
	
	/**
	 * The CSS class for un-underlying something.
	 */
	private static final String NOT_UNDERLINED_CLASS = "not_underlined";
	
	/**
	 * The maximum height for the ImageView.
	 */
	private static final int IMAGE_HEIGHT = 350;
	
	/**
	 * The help text that corresponds to different slides in each tutorial. Each set corresponds to one 
	 * tutorial, and each one to a different slide.
	 */
	private static final String[][] HELP_TEXT = {
			{"\tWelcome to the tutorials\nThe options on the left are tutorials"},
			{"Click the load button", "Select an already saved text file", "Your times should now be loaded"},
			{"Enter in your desired times", "Click the save button"},
			{"This is the area to enter in your hours", "The left textbox is the start of a shift\nThe right textbox is the end of your shift", "Each day can have up to three shifts", "Data can be in many different formats"},
			{"Have your hours entered", "Click the Tutor Login button", "Enter in your information", "Press the Start button", "Press the Stop button to stop"},
			{"Have your hours entered", "Click the Firefly Hours button", "Enter in your information", "Press the Start button", "Press the Stop button to stop"}
	};
	
	/**
	 * The images that corresponds to different slides in each tutorial. Each set corresponds to one 
	 * tutorial, and each one to a different slide.
	 */
	private static final String[][] IMAGES = {
			{null},
			{"/collin/timescreator/javafx/resources/images/LoadFile1.png", "/collin/timescreator/javafx/resources/images/LoadFile2.png", "/collin/timescreator/javafx/resources/images/LoadFile3.JPG"},
			{"/collin/timescreator/javafx/resources/images/SaveFile1.JPG", "/collin/timescreator/javafx/resources/images/SaveFile2.png"},
			{"/collin/timescreator/javafx/resources/images/DataInput1.JPG", "/collin/timescreator/javafx/resources/images/DataInput2.JPG", "/collin/timescreator/javafx/resources/images/DataInput3.JPG", "/collin/timescreator/javafx/resources/images/DataInput4.JPG"},
			{"/collin/timescreator/javafx/resources/images/SaveFile1.JPG", "/collin/timescreator/javafx/resources/images/Tutor1.JPG", "/collin/timescreator/javafx/resources/images/Tutor2.JPG", "/collin/timescreator/javafx/resources/images/Tutor3.JPG", "/collin/timescreator/javafx/resources/images/Tutor4.JPG"},
			{"/collin/timescreator/javafx/resources/images/SaveFile1.JPG", "/collin/timescreator/javafx/resources/images/Firefly1.JPG", "/collin/timescreator/javafx/resources/images/Firefly2.JPG", "/collin/timescreator/javafx/resources/images/Firefly3.JPG", "/collin/timescreator/javafx/resources/images/Firefly4.JPG"}
	};
	
	/**
	 * The first hyperlink.
	 */
	@FXML
	private Hyperlink welcomeLink;
	
	/**
	 * The container that contains the hyperlinks.
	 */
	@FXML
	private VBox linksContainer;
	
	/**
	 * The ImageView containing the tutorial images.
	 */
	@FXML
	private ImageView imageView;
	
	/**
	 * The label that displays the help text.
	 */
	@FXML
	private Label helpText;
	
	/**
	 * The page selector.
	 */
	@FXML
	private Pagination pagination;
	
	/**
	 * The currently selected tutorial.
	 */
	private Hyperlink currentlySelectedLink;
	
	@FXML
	public void initialize()
	{
		currentlySelectedLink = welcomeLink;
		setTutorial(0, 0);
		pagination.setPageFactory(num -> {
			setTutorial(linksContainer.getChildren().indexOf(currentlySelectedLink), num);
			return new VBox();
		});
		imageView.setPreserveRatio(true);
	}
	
	/**
	 * All hyperlinks implements this onAction method. 
	 * It changes to the clicked tutorial.
	 * @param event an ActionEvent
	 */
	@FXML
	public void select(ActionEvent event)
	{
		if(event.getSource() instanceof Hyperlink && (Hyperlink) event.getSource() != currentlySelectedLink)
		{
			currentlySelectedLink.getStyleClass().remove(SELECTED_CLASS);
			currentlySelectedLink.getStyleClass().remove(UNDERLINED_CLASS);
			currentlySelectedLink.getStyleClass().add(UNSELECTED_CLASS);
			currentlySelectedLink = (Hyperlink) event.getSource();
			currentlySelectedLink.getStyleClass().add(SELECTED_CLASS);
			currentlySelectedLink.getStyleClass().remove(UNSELECTED_CLASS);
			int section = linksContainer.getChildren().indexOf(currentlySelectedLink);
			pagination.currentPageIndexProperty().set(0);
			pagination.pageCountProperty().set(IMAGES[section].length);
			setTutorial(section, pagination.getCurrentPageIndex());
		}
	}
	
	/**
	 * All hyperlinks implements this mouseOver method. 
	 * It will change the look of the currently moused over link.
	 * @param event a MouseEvent
	 */
	@FXML
	public void mouseOverLink(MouseEvent event)
	{
		if(event.getSource() instanceof Hyperlink)
		{
			Hyperlink currentLink = (Hyperlink) event.getSource();
			if(currentLink != currentlySelectedLink)
			{
				currentLink.getStyleClass().add(UNDERLINED_CLASS);
				currentLink.getStyleClass().remove(NOT_UNDERLINED_CLASS);
			}
		}
	}
	
	/**
	 * All hyperlinks implements this mouseExit method. 
	 * It will change the look of a hyperlink that the mouse 
	 * just exited from.
	 * @param event a MouseEvent
	 */
	@FXML
	public void mouseExitLink(MouseEvent event)
	{
		if(event.getSource() instanceof Hyperlink)
		{
			Hyperlink currentLink = (Hyperlink) event.getSource();
			if(currentLink != currentlySelectedLink)
			{
				currentLink.getStyleClass().add(NOT_UNDERLINED_CLASS);
				currentLink.getStyleClass().remove(UNDERLINED_CLASS);
			}
		}
	}
	
	/**
	 * Sets the image and help text to display the tutorial to the 
	 * specific section and part.
	 * @param section The specific tutorial
	 * @param part The part in the specific tutorial
	 */
	private void setTutorial(int section, int part)
	{
		if(IMAGES[section][part] == null)
		{
			imageView.setImage(null);
			imageView.setFitHeight(0);
		}
		else
		{
			imageView.setImage(new Image(IMAGES[section][part]));
			imageView.setFitHeight(IMAGE_HEIGHT);
		}
		helpText.setText(HELP_TEXT[section][part]);
	}
}
