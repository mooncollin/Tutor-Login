package firefly;

import java.util.HashMap;
import java.util.List;
import application.Main;
import javafx.application.Platform;
import javafx.scene.control.Alert.AlertType;
import selenium.BrowserType;
import selenium.DetectDeadDriverThread;
import selenium.DriverThread;
import util.Procedure;
import util.Shift;

/**
 * A thread responsible for running the "firefly process".
 * @author colli
 *
 */
public class FireflyThread extends DriverThread<TrueYou>
{
	/**
	 * A function that runs when this thread's process is done.
	 */
	private final Procedure resetProcedure;
	
	/**
	 * The shift data.
	 */
	private HashMap<String, List<Shift>> data;
	
	/**
	 * Creates a TrueYou user from the given parameters.
	 * @param nuid user's nuid.
	 * @param password user's password.
	 * @param type the browser type to use with selenium.
	 * @param fireflyController the controller creating this thread.
	 * @param data shift data.
	 */
	public FireflyThread(Procedure givenProcedure,
			HashMap<String, List<Shift>> data,
			int nuid, String password, BrowserType type)
	{
		super(new TrueYou(nuid, password, type));
		this.resetProcedure = givenProcedure;
		deadThread = new DetectDeadDriverThread<TrueYou>(this);
		this.data = data;
	}
	
	/**
	 * Runs the "firefly process".
	 */
	public void run()
	{
		deadThread.start();
		LOGIN_RESULT result = LOGIN_RESULT.INCORRECT; // Login Credentials assumed to be wrong
		try
		{
			if(!isInterrupted() && getDriverUser().uploadHours(data))
				result = LOGIN_RESULT.CORRECT; // Login Credentials are correct
		}
		catch(org.openqa.selenium.WebDriverException e)
		{
			result = LOGIN_RESULT.ERROR; // Driver was closed during the process
		}
		this.reset();
		if(result == LOGIN_RESULT.INCORRECT)
		{
			getDriverUser().closeDriver();
			Platform.runLater(() ->
			{
				Main.alert("Invalid Credentials", AlertType.ERROR);
			});
		}
		
	}
	
	/**
	 * Stops the dead thread and triggers the reset procedure.
	 */
	protected void reset()
	{
		deadThread.stopThread(); // Stops asking whether the WebDriver is dead.
		resetProcedure.run();
	}
	
	/**
	 * Result from attempting to login to Firefly.
	 * @author colli
	 *
	 */
	private enum LOGIN_RESULT
	{
		INCORRECT, CORRECT, ERROR;
	}
}