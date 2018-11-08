package tutorlogin;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import selenium.BrowserType;
import selenium.DetectDeadDriverThread;
import selenium.DriverThread;
import util.Procedure;
import util.Shift;

/**
 * Thread that is the "tutor login" process.
 * @author colli
 *
 */
public class TutorTimedLoginThread extends DriverThread<Tutor>
{
	/**
	 * A function to run when this threads needs to stop.
	 */
	private final Procedure resetProcedure;
	
	/**
	 * The shift data.
	 */
	private HashMap<String, List<Shift>> dayData;
	
	/**
	 * Output area for user to see status.
	 */
	private TextArea loginOutput;
	
	/**
	 * Creates a tutor from parameters.
	 * @param emailIn tutor email
	 * @param netIDIn tutor netID
	 * @param passwordIn tutor password
	 * @param type type of browser to use when logging in or out
	 * @param tutorLoginController the controller creating this thread
	 * @param dayData shift data
	 */
	public TutorTimedLoginThread(Procedure givenProcedure, 
			HashMap<String, List<Shift>> dayData, TextArea loginOutput,
			String emailIn, String netIDIn, String passwordIn, BrowserType type)
	{
		super(new Tutor(netIDIn, passwordIn, emailIn, type));
		deadThread = new DetectDeadDriverThread<Tutor>(this);
		this.resetProcedure = givenProcedure;
		this.loginOutput = loginOutput;
		this.dayData = dayData;
	}
	
	protected void reset()
	{
		deadThread.stopThread();
		getDriverUser().closeDriver();
		resetProcedure.run();
	}
	
	/**
	 * Check the credentials of this tutor.
	 */
	private boolean credentialsCheck()
	{
		Platform.runLater(() ->
		{
			loginOutput.appendText("Checking Credentials...\n");
		});
		if(!getDriverUser().checkCredentials())
		{
			Platform.runLater(() ->
			{
				loginOutput.appendText("Invalid Credentials\n");
			});
			return false;
		}
		Platform.runLater(() ->
		{
			loginOutput.appendText("Credentials Correct!\n");
		});
		getDriverUser().closeDriver();
		return true;
	}
	
	/**
	 * The "tutor login" process.
	 */
	public void run()
	{
		try
		{
			if(isInterrupted())
				throw new InterruptedException();
			deadThread.start();
			if(!credentialsCheck())
			{
				reset();
				return;
			}
			deadThread.stopThread();
			deadThread = new DetectDeadDriverThread<Tutor>(this);
			while(true)
			{
				if(isInterrupted())
					throw new InterruptedException();
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
						if(isInterrupted())
							throw new InterruptedException();
						deadThread.start();
						getDriverUser().working(true);
						deadThread.stopThread();
						deadThread = new DetectDeadDriverThread<Tutor>(this);
						now = LocalDateTime.now();
						later = LocalDateTime.of(LocalDate.now(), shift.getStop());
						waitDuration = Duration.between(now, later);
						loginOutput.appendText(String.format("Need to wait %d seconds until off work!\n", waitDuration.toSeconds()));
						Thread.sleep(waitDuration.toMillis());
						loginOutput.appendText("Time to leave work!\n");
						if(isInterrupted())
							throw new InterruptedException();
						deadThread.start();
						getDriverUser().working(false);
						deadThread.stopThread();
						deadThread = new DetectDeadDriverThread<Tutor>(this);
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
		catch(org.openqa.selenium.WebDriverException e)
		{
			reset();
			Platform.runLater(() ->
			{
				loginOutput.appendText("Error Occured\n");
			});
		}
		catch(InterruptedException e)
		{
			reset();
		}
	}
}