package collin.timescreator.javafx.application;

import java.util.HashMap;
import java.util.List;

import collin.timescreator.selenium.tutorlogin.TutorTimedLoginThread;
import collin.timescreator.selenium.utils.BrowserType;
import collin.timescreator.util.OSSettings;
import collin.timescreator.util.Procedure;
import collin.timescreator.util.Shift;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;

/**
 * This controller contains FXML hooks into the TUTOR_LOGIN_FXML file.
 * It is responsible for all the actions done on the "tutor login" window.
 * @author colli
 *
 */
public class TutorLoginController
{
	private final Procedure THREAD_RESET_PROCEDURE = () -> {
		Platform.runLater(() -> {
			reset();
		});
	};
	
	/**
	 * Output log of the "tutor login" thread.
	 */
	@FXML 
	private TextArea loginOutput;
	
	/**
	 * Textfield for the user's email address.
	 */
	@FXML
	private TextField emailField;
	
	/**
	 * Textfield for the user's netID.
	 */
	@FXML
	private TextField netIDField;
	
	/**
	 * Passwordfield for the user's password;
	 */
	@FXML
	private PasswordField passwordField;
	
	/**
	 * Button to start the "tutor login" process.
	 */
	@FXML
	private Button startLoginButton;
	
	/**
	 * User's work shift data.
	 */
	private HashMap<String, List<Shift>> dayData;
	
	/**
	 * Thread responsible for the "tutor login" process.
	 */
	private TutorTimedLoginThread tutorThread;
	
	/**
	 * Initial email to fill into the email textfield.
	 */
	private String givenEmail;
	
	/**
	 * Initial netID to fill into the netID textfield.
	 */
	private String givenNetID;
	
	/**
	 * Initial password to fill into the password passwordfield.
	 */
	private String givenPassword;
	
	/**
	 * Default constructor. Textfields and passwordfields are set 
	 * to an empty string.
	 * @param data User's shift data.
	 */
	public TutorLoginController(HashMap<String, List<Shift>> data)
	{
		this(data, "", "", "");
	}
	
	/**
	 * Specific constructor to set the emial, netID, and password.
	 * @param data User's shift data.
	 * @param email User's email.
	 * @param netID User's netID.
	 * @param password User's password.
	 */
	public TutorLoginController(HashMap<String, List<Shift>> data,
			String email, String netID, String password)
	{
		dayData = data;
		givenEmail = email;
		givenNetID = netID;
		givenPassword = password;
	}
	
	/**
	 * Get the email address.
	 * @return email address.
	 */
	public String getEmail()
	{
		return emailField.getText();
	}
	
	/**
	 * Get the netID.
	 * @return netID.
	 */
	public String getNetID()
	{
		return netIDField.getText();
	}
	
	/**
	 * Get the password.
	 * @return password.
	 */
	public String getPassword()
	{
		return passwordField.getText();
	}
	
	/**
	 * This method is called after all FXML components
	 * have been loaded and references set. This will
	 * fill in the email, netID, and password fields.
	 */
	@FXML
	public void initialize()
	{
		emailField.requestFocus();
		emailField.setText(givenEmail);
		netIDField.setText(givenNetID);
		passwordField.setText(givenPassword);
	}
	
	/**
	 * The start button implements this onAction method.
	 * It will check requirements first and then call
	 * another method to start the thread.
	 * @param event An ActionEvent for a node.
	 */
	@FXML
	public void startButton(ActionEvent event)
	{
		Button button = (Button) event.getSource();
		if(button.getText().equals("Start"))
		{
			startDailyLogin(button);
		}
		else
		{
			reset();
			stopThread();
		}
	}
	
	/**
	 * Checks requirements and starts the "tutor login" thread
	 * if email, netID, and password fields are correct.
	 * @param button Start button.
	 */
	private void startDailyLogin(Button button)
	{
		String errorMessage = null;
		if(emailField.getText().isEmpty())
		{
			errorMessage = "Email cannot be blank!";
		}
		else if(netIDField.getText().isEmpty())
		{
			errorMessage = "NetID cannot be blank!";
		}
		else if(passwordField.getText().isEmpty())
		{
			errorMessage = "Password cannot be blank!";
		}
		if(errorMessage != null)
		{
			Main.alert(errorMessage, AlertType.ERROR);
			return;
		}
		loginOutput.clear();
		emailField.getParent().setDisable(true);
		button.setText("Stop");
		tutorThread = new TutorTimedLoginThread(THREAD_RESET_PROCEDURE, dayData, 
				loginOutput, emailField.getText(), 
				netIDField.getText(), passwordField.getText(), 
				BrowserType.browserNameToEnum(OSSettings.getDefaultBrowser()));
		tutorThread.start();
	}
	
	/**
	 * Interrupts the "login tutor" thread.
	 */
	public void stopThread()
	{
		if(tutorThread != null && tutorThread.isAlive())
		{
			tutorThread.getDriverUser().closeDriver();
			tutorThread.interrupt();
		}
	}
	
	/**
	 * Reset the fields back to normal and the button back to "start".
	 */
	public void reset()
	{
		startLoginButton.setText("Start");
		emailField.getParent().setDisable(false);
	}
}
