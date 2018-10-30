package application;

import java.util.HashMap;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import firefly.TrueYou;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import selenium.BrowserType;
import javafx.scene.control.Alert.AlertType;
import util.OSSettings;
import util.Shift;

/**
 * The class responsible for controlling the Firefly scene.
 * @author colli
 *
 */
public class FireflyController
{
	/**
	 * A pattern that represents the characters not allowed to be typed
	 * into the NUID textfield.
	 */
	private static final Pattern UNALLOWED_CHARACTERS_NUID_PATTERN = Pattern.compile("[^1234567890]");
	
	/**
	 * The text filter for the NUID textfield.
	 */
	private static final UnaryOperator<TextFormatter.Change> TEXT_FILTER = new UnaryOperator<TextFormatter.Change>() {
		/**
		 * Forces the NUID textfield to only accept numbers, while
		 * keeping the max length at 8 numbers.
		 */
		@Override
		public TextFormatter.Change apply(TextFormatter.Change change)
		{
			String newText = change.getText();
			String possibleNewText = change.getControlNewText();
			TextFormatter.Change result = change;
			
			if(UNALLOWED_CHARACTERS_NUID_PATTERN.matcher(newText).matches())
			{
				result = null;
			}
			else if(possibleNewText.length() > 8)
			{
				result = null;
			}
			
			return result;
		}
	};
	
	/**
	 * The start button for the firefly process.
	 */
	@FXML
	private Button startButton;
	
	/**
	 * The NUID textfield.
	 */
	@FXML
	private TextField nuidField;
	
	/**
	 * The password textfield.
	 */
	@FXML
	private TextField passwordField;
	
	/**
	 * The NUID to save when a user exits the stage.
	 */
	private int givenNUID;
	
	/**
	 * The password to save when a user exits the stage.
	 */
	private String givenPassword;
	
	/**
	 * The shift data.
	 */
	private HashMap<String, List<Shift>> data;
	
	/**
	 * The thread that will run the "firefly process"
	 */
	private FireflyThread fireflyThread;
	
	/**
	 * Constructs a FireflyController object with the given parameters.
	 * @param data shift data.
	 * @param nuid user's nuid.
	 * @param password user's password.
	 */
	public FireflyController(HashMap<String, List<Shift>> data, int nuid, String password)
	{
		givenNUID = nuid;
		givenPassword = password;
		this.data = data;
	}
	
	/**
	 * Gets the current NUID in the textfield. -1 if the textfield is empty.
	 * @return nuid
	 */
	public int getNUID()
	{
		return nuidField.getText().isEmpty() ? -1 : Integer.valueOf(nuidField.getText());
	}
	
	/**
	 * Gets the current password in the textfield.
	 * @return password
	 */
	public String getPassword()
	{
		return passwordField.getText();
	}
	
	/**
	 * Sets the text formatter of the nuid textfield as well as
	 * setting both textfields to their previous value.
	 */
	@FXML
	public void initialize()
	{
		nuidField.setTextFormatter(new TextFormatter<Integer>(TEXT_FILTER));
		nuidField.setText(givenNUID < 0 ? "" : String.valueOf(givenNUID));
		passwordField.setText(givenPassword);
		nuidField.requestFocus();
	}
	
	/**
	 * This method will trigger when the start button is pressed.
	 * This starts the "firefly process".
	 * @param event An ActionEvent for a node.
	 */
	@FXML
	public void startFirefly(ActionEvent event)
	{
		String errorMessage = null;
		if(nuidField.getText().isEmpty())
		{
			errorMessage = "NUID cannot be blank!";
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
		
		Button button = (Button) event.getSource();
		if(button.getText().equals("Start"))
		{
			passwordField.getParent().setDisable(true);
			button.setText("Stop");
			fireflyThread = new FireflyThread(getNUID(), getPassword(),
					BrowserType.browserNameToEnum(OSSettings.getDefaultBrowser()));
			fireflyThread.start();
		}
		else
		{
			reset();
			this.stopThread();
		}
	}
	
	/**
	 * Reset the fields back to normal and the button back to "start".
	 */
	private void reset()
	{
		startButton.setText("Start");
		passwordField.getParent().setDisable(false);
	}
	
	/**
	 * Interrupts the "firefly process" thread.
	 */
	public void stopThread()
	{
		if(fireflyThread != null)
		{
			fireflyThread.user.closeDriver();
			fireflyThread.interrupt();
		}
	}
	
	/**
	 * A thread responsible for running the "firefly process".
	 * @author colli
	 *
	 */
	private class FireflyThread extends Thread
	{
		/**
		 * The current user for Firefly.
		 */
		private TrueYou user;
		
		private DetectDeadDriverThread deadThread;
		
		/**
		 * Creates a TrueYou user from the given parameters.
		 * @param nuid user's nuid.
		 * @param password user's password.
		 * @param type the browser type to use with selenium.
		 */
		public FireflyThread(int nuid, String password, BrowserType type)
		{
			user = new TrueYou(nuid, password, type);
			deadThread = new DetectDeadDriverThread(user, 4000);
		}
		
		/**
		 * Runs the "firefly process".
		 */
		public void run()
		{
			deadThread.start();
			int result = 0;
			try
			{
				if(user.uploadHours(data))
					result = 1;
			}
			catch(org.openqa.selenium.WebDriverException e)
			{
				result = 2;
			}
			if(result != 1)
			{
				user.closeDriver();
			}
			deadThread.stopThread();
			int threadResult = result;
			Platform.runLater(new Runnable() {
				public void run()
				{
					if(threadResult == 0)
						Main.alert("Invalid Credentials", AlertType.ERROR);
					reset();
				}
			});
		}
	}
	
	/**
	 * A thread responsible for checking up on the WebDriver if
	 * it dies, and will reset the start button if the WebDriver
	 * dies unexpectingly.
	 * @author colli
	 *
	 */
	private class DetectDeadDriverThread extends Thread
	{
		/**
		 * The current user of the WebDriver.
		 */
		private TrueYou user;
		
		/**
		 * Whether this thread should stop.
		 */
		private boolean stopped;
		
		/**
		 * The amount of time to wait before initially checking
		 * the WebDriver.
		 */
		private long waitTime;
		
		/**
		 * Sets the user and sets the wait time before
		 * initially checking the WebDriver to 0 milliseconds.
		 * @param user WebDriver user
		 */
		public DetectDeadDriverThread(TrueYou user)
		{
			this(user, 0);
		}
		
		/**
		 * Sets the user and sets the wait time before
		 * initially checking the WebDriver to the given wait time.
		 * @param user WebDriver user
		 * @param waitTime time to wait before initially checking
		 * the WebDriver.
		 */
		public DetectDeadDriverThread(TrueYou user, long waitTime)
		{
			this.user = user;
			this.waitTime = waitTime;
		}
		
		/**
		 * Stops this current thread.
		 */
		public void stopThread()
		{
			stopped = true;
		}
		
		/**
		 * Runs this current thread.
		 */
		public void run()
		{
			try
			{
				Thread.sleep(waitTime);
			} catch (InterruptedException e1)
			{
				return;
			}
			while(!stopped)
			{
				try
				{
					Thread.sleep(500);
				} catch (InterruptedException e)
				{
					return;
				}
				if(user.isDriverDead())
				{
					Platform.runLater(new Runnable() {
						public void run()
						{
							reset();
						}
					});
					stopped = true;
				}
			}			
		}
	}
}
