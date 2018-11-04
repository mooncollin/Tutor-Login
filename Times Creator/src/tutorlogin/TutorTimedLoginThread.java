package tutorlogin;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import application.TutorLoginController;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import selenium.BrowserType;
import selenium.DetectDeadDriverThread;
import selenium.DriverThread;
import util.Shift;

/**
 * Thread that is the "tutor login" process.
 * @author colli
 *
 */
public class TutorTimedLoginThread extends DriverThread<Tutor>
{
	/**
	 * The controller that calls this thread.
	 */
	private final TutorLoginController tutorLoginController;
	
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
	public TutorTimedLoginThread(TutorLoginController tutorLoginController, 
			HashMap<String, List<Shift>> dayData, TextArea loginOutput,
			String emailIn, String netIDIn, String passwordIn, BrowserType type)
	{
		super(new Tutor(netIDIn, passwordIn, emailIn, type));
		this.tutorLoginController = tutorLoginController;
		deadThread = new DetectDeadDriverThread<Tutor>(this);
		this.loginOutput = loginOutput;
		this.dayData = dayData;
	}
	
	protected void reset()
	{
		deadThread.stopThread();
		getDriverUser().closeDriver();
		Platform.runLater(() ->
		{
			this.tutorLoginController.reset();
		});
	}
	
	/**
	 * Check the credentials of this tutor.
	 */
	private void credentialsCheck()
	{
		Platform.runLater(() ->
		{
			loginOutput.appendText("Checking Credentials...\n");
		});
		if(!getDriverUser().checkCredentials())
		{
			reset();
			Platform.runLater(() ->
			{
				loginOutput.appendText("Invalid Credentials\n");
			});
			return;
		}
		Platform.runLater(() ->
		{
			loginOutput.appendText("Credentials Correct!\n");
		});
		getDriverUser().closeDriver();
	}
	
	/**
	 * The "tutor login" process.
	 */
	public void run()
	{
		deadThread.start();
		try
		{
			if(isInterrupted())
				throw new InterruptedException();
			credentialsCheck();
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
						getDriverUser().working(true);
						now = LocalDateTime.now();
						later = LocalDateTime.of(LocalDate.now(), shift.getStop());
						waitDuration = Duration.between(now, later);
						loginOutput.appendText(String.format("Need to wait %d seconds until off work!\n", waitDuration.toSeconds()));
						Thread.sleep(waitDuration.toMillis());
						loginOutput.appendText("Time to leave work!\n");
						if(isInterrupted())
							throw new InterruptedException();
						getDriverUser().working(false);
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
		catch(InterruptedException | org.openqa.selenium.WebDriverException e)
		{
			reset();
			Platform.runLater(() ->
			{
				loginOutput.appendText("Error Occured\n");
			});
			return;
		}
	}
}