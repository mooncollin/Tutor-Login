package application;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import tutorlogin.BrowserType;
import tutorlogin.Tutor;
import util.OSSettings;
import util.Shift;

public class TutorLoginController
{
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
	private HashMap<String, ArrayList<Shift>> dayData;
	
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
	public TutorLoginController(HashMap<String, ArrayList<Shift>> data)
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
	public TutorLoginController(HashMap<String, ArrayList<Shift>> data,
			String email, String netID, String password)
	{
		dayData = data;
		givenEmail = email;
		givenNetID = netID;
		givenPassword = password;
	}
	
	public String getEmail()
	{
		return emailField.getText();
	}
	
	public String getNetID()
	{
		return netIDField.getText();
	}
	
	public String getPassword()
	{
		return passwordField.getText();
	}
	
	@FXML
	public void initialize()
	{
		emailField.requestFocus();
		emailField.setText(givenEmail);
		netIDField.setText(givenNetID);
		passwordField.setText(givenPassword);
	}
	
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
		tutorThread = new TutorTimedLoginThread(emailField.getText(), 
				netIDField.getText(), passwordField.getText(), 
				BrowserType.browserNameToEnum(OSSettings.getDefaultBrowser()));
		tutorThread.start();
	}
	
	public void stopThread()
	{
		if(tutorThread != null && tutorThread.isAlive())
		{
			tutorThread.interrupt();
		}
	}
	
	private void reset()
	{
		startLoginButton.setText("Start");
		emailField.getParent().setDisable(false);
	}
	
	private class TutorTimedLoginThread extends Thread
	{
		private Tutor tutor;
		
		public TutorTimedLoginThread(String emailIn, 
				String netIDIn, String passwordIn, BrowserType type)
		{
			tutor = new Tutor(netIDIn, passwordIn, emailIn, type);
		}
		
		public void run()
		{
			try
			{
				while(true)
				{
					LocalDateTime now;
					LocalDateTime later;
					Duration waitDuration;
					String weekdayToday = LocalDate.now()
										  .getDayOfWeek()
										  .getDisplayName(TextStyle.FULL, Locale.ENGLISH);
					boolean noShifts = true;
					for(Shift shift : dayData.get(weekdayToday))
					{
						if(shift == null)
						{
							continue;
						}
						noShifts = false;
						now = LocalDateTime.now();
						later = LocalDateTime.of(LocalDate.now(), shift.getStart());
						waitDuration = Duration.between(now, later);
						if(waitDuration.toMillis() > 0)
						{
							loginOutput.appendText(String.format("Need to wait %d seconds until work!\n", waitDuration.toSeconds()));
							Thread.sleep(waitDuration.toMillis());
							loginOutput.appendText("Time to work!\n");
							if(!tutor.login())
							{
								Platform.runLater(new Runnable() {
									public void run()
									{
										reset();
										loginOutput.appendText("Invalid Credentials\n");
									}
								});
								return;
							}
							now = LocalDateTime.now();
							later = LocalDateTime.of(LocalDate.now(), shift.getStop());
							waitDuration = Duration.between(now, later);
							loginOutput.appendText(String.format("Need to wait %d seconds until off work!\n", waitDuration.toSeconds()));
							Thread.sleep(waitDuration.toMillis());
							loginOutput.appendText("Time to leave work!\n");
							if(!tutor.logout())
							{
								Platform.runLater(new Runnable() {
									public void run()
									{
										reset();
										loginOutput.appendText("Invalid Credentials\n");
									}
								});
								return;
							}
							
						}
					}
					if(noShifts)
					{
						loginOutput.appendText("You do not have work today\n");
					}
					else
					{
						loginOutput.appendText("We are done with the day\n");
					}
					now = LocalDateTime.now();
					later = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIDNIGHT);
					waitDuration = Duration.between(now, later);
					Thread.sleep(waitDuration.toMillis());
				}
			}
			catch(InterruptedException e)
			{
				return;
			}
		}
	}
}
