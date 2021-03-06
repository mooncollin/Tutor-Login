package collin.timescreator.selenium.firefly;

import java.util.HashMap;
import java.util.List;

import collin.timescreator.javafx.application.Main;
import collin.timescreator.selenium.utils.BrowserType;
import collin.timescreator.selenium.utils.DriverThread;
import collin.timescreator.util.LogLevel;
import collin.timescreator.util.Shift;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * A thread responsible for running the "firefly process".
 * @author colli
 *
 */
public class FireflyThread extends DriverThread<TrueYou>
{
	
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
	public FireflyThread(HashMap<String, List<Shift>> data,
			int nuid, String password, BrowserType type)
	{
		super(new TrueYou(nuid, password, type));
		this.data = data;
	}
	
	/**
	 * Runs the "firefly process".
	 */
	public void run()
	{
		LOGIN_RESULT result = LOGIN_RESULT.ERROR; // Assumes an error has occured
		try
		{
			if(isInterrupted())
				result = LOGIN_RESULT.ERROR;
			else if(getDriverUser().uploadHours(data))
				result = LOGIN_RESULT.CORRECT; // Login Credentials are correct
			else
				result = LOGIN_RESULT.INCORRECT;
		}
		catch(IllegalStateException e)
		{
			Main.log(e.getMessage(), LogLevel.DANGER);
			result = LOGIN_RESULT.ERROR;
			Platform.runLater(() -> {
				Main.alert("WebDrivers do not exist.\nPlease restart the program to reinstall drivers.", Alert.AlertType.ERROR);
			});
		}
		catch(Exception e)
		{
			Main.log(e.getMessage(), LogLevel.DANGER);
			result = LOGIN_RESULT.ERROR; // Driver was closed during the process
		}
		
		if(result == LOGIN_RESULT.ERROR)
		{
			getDriverUser().closeDriver();
		}
		if(result == LOGIN_RESULT.INCORRECT)
		{
			getDriverUser().closeDriver();
			Platform.runLater(() ->
			{
				Main.alert("Invalid Credentials", AlertType.ERROR);
			});
		}
		this.reset();
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