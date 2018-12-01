package collin.timescreator.selenium.firefly;

import java.util.List;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import collin.timescreator.selenium.utils.AdvancedAction;
import collin.timescreator.selenium.utils.AdvancedActions;
import collin.timescreator.selenium.utils.BrowserType;
import collin.timescreator.selenium.utils.ClickAction;
import collin.timescreator.selenium.utils.DriverUser;
import collin.timescreator.selenium.utils.KeysAction;
import collin.timescreator.selenium.utils.WaitAction;
import collin.timescreator.util.Procedure;
import collin.timescreator.util.Shift;

/**
 * A Firefly User.
 * @author colli
 *
 */
public class TrueYou extends DriverUser
{
	/**
	 * The default browser to use.
	 */
	private static final BrowserType DEFAULT_BROWSER = BrowserType.CHROME;
	
	/**
	 * The URL for the Firefly website.
	 */
	private static final String FIREFLY_URL = "https://firefly.nebraska.edu/irj/portal/";
	
	/**
	 * Locator for the time textfields on the Firefly timesheet.
	 */
	private static final By TIME_TEXTBOX_BY = By.xpath("//tr/td/div/input[@role='combobox']");
	
	/**
	 * Locator for the button for the shift type dropdown menu.
	 */
	private static final By SHIFT_TYPE_BUTTON_BY = By.xpath("//tr/td/div/button");
	
	/**
	 * Locator for the first option for the shift type dropdown menu.
	 * This will apparently only work if its corresponding dropdown 
	 * button has previously been clicked.
	 */
	private static final By SHIFT_TYPE_OPTION_BY = By.xpath("//li[text()='Tmp/Std Work Hours']");
	
	/**
	 * Locator for the worklist checkboxes.
	 */
	private static final By WORKLIST_CHECKBOX_BY = By.xpath("//td/div/div/div");
	
	/**
	 * The default amount of seconds to wait for WaitActions.
	 */
	private static final int DEFAULT_WAITING_SECONDS = 25;
	
	/**
	 * An array of string that are in the order of how
	 * Firefly lays out its rows and are the exact
	 * keys in the shift data map.
	 */
	private static final String[] FIREFLY_ROWS_BY_DAY = {
			"Friday", "Saturday", "Sunday", "Monday",
			"Tuesday", "Wednesday", "Thursday"
	};
	
	/**
	 * The nuid of the current Firefly user.
	 */
	private int NUID;
	
	/**
	 * The password of the current Firefly user.
	 */
	private String password;
	
	
	/**
	 * The Firefly process actions chain.
	 */
	private final AdvancedActions FIREFLY_UPLOAD_PROCESS = AdvancedActions.of(
		new KeysAction(null, By.id("username"), null), // Enter NUID
		new KeysAction(null, By.id("password"), null), // Enter Password
		new ClickAction(null, By.cssSelector("button[class='button success']")), // Click login button
		new WaitAction(null, By.name("Neptune"), DEFAULT_WAITING_SECONDS), // Wait for Firefly main page
		new WaitAction(null, By.xpath("//div[starts-with(@aria-label, 'Employee Self Service')]"), DEFAULT_WAITING_SECONDS), // Wait for ESS box
		new ClickAction(null, By.xpath("//div[starts-with(@aria-label, 'Employee Self Service')]")), // Click ESS box
		new WaitAction(null, By.xpath("//div[starts-with(@aria-label, 'Timesheet Entry')]"), DEFAULT_WAITING_SECONDS), // Wait for Timesheet Entry Box
		new ClickAction(null, By.xpath("//div[starts-with(@aria-label, 'Timesheet Entry')]")), // Click Timesheet Entry Box
		new WaitAction(null, By.id("__jsview1--tableTime-listUl"), DEFAULT_WAITING_SECONDS) // Wait for hours table
	);
	
	/**
	 * Constructs this class using given arguments.
	 * @param NUID nuid for this Firefly user.
	 * @param password password for this Firefly user.
	 * @param browserType browser to use for the WebDriver.
	 * @throws NullPointerException if password given is null.
	 */
	public TrueYou(int NUID, String password, BrowserType browserType) throws NullPointerException
	{
		super(browserType);
		setNUID(NUID);
		setPassword(password);
	}
	
	/**
	 * Sets the nuid.
	 * @param NUID nuid to set.
	 */
	public void setNUID(int NUID)
	{
		this.NUID = NUID;
		((KeysAction) FIREFLY_UPLOAD_PROCESS.findActionWithBy(By.id("username"), KeysAction.class)).setKeys(String.valueOf(this.NUID));
	}
	
	@Override
	public void setDeadProcedure(Procedure action)
	{
		super.setDeadProcedure(action);
		FIREFLY_UPLOAD_PROCESS.setDeadAction(action);
	}
	
	@Override
	public void setInterruptProcedure(Procedure action)
	{
		super.setInterruptProcedure(action);
		FIREFLY_UPLOAD_PROCESS.setInterruptAction(action);
	}
	
	/**
	 * Sets the password.
	 * @param password password to set.
	 * @throws NullPointerException if password given is null.
	 */
	public void setPassword(String password) throws NullPointerException
	{
		if(password == null)
			throw new NullPointerException();
		this.password = password;
		((KeysAction) FIREFLY_UPLOAD_PROCESS.findActionWithBy(By.id("password"), KeysAction.class)).setKeys(this.password);
	}
	
	/**
	 * Gets the current nuid;
	 * @return nuid.
	 */
	public int getNUID()
	{
		return NUID;
	}
	
	/**
	 * Gets the current password;
	 * @return password;
	 */
	public String getPassword()
	{
		return password;
	}
	
	/**
	 * Opens a driver and attempts to put hours into the Firefly
	 * website.
	 * @param data shift data to put into the website.
	 * @return true if successful, false otherwise.
	 * @throws InterruptedException 
	 */
	public boolean uploadHours(Map<String, List<Shift>> data)
	{
		startDriver(FIREFLY_URL);
		FIREFLY_UPLOAD_PROCESS.perform(3);
		try
		{
			Thread.sleep(250);
			driver.findElement(By.cssSelector("div[class='error-alert']"));
			return false;
		}
		catch(org.openqa.selenium.NoSuchElementException | InterruptedException e)
		{
		}
		
		FIREFLY_UPLOAD_PROCESS.perform(3, 4);
		driver.switchTo().defaultContent();
		driver.switchTo().frame("Neptune");
		FIREFLY_UPLOAD_PROCESS.perform(4, FIREFLY_UPLOAD_PROCESS.size());
		
		for(int h = 0; h < FIREFLY_ROWS_BY_DAY.length; h++)
		{
			for(int i = 1; i < data.get(FIREFLY_ROWS_BY_DAY[h]).size(); i++)
			{
				driver.findElements(By.cssSelector("button[title='Add']")).get(h).click();
			}
		}
		
		WebElement timesTable = driver.findElement(By.id("__jsview1--tableTime-tblBody"));
		List<WebElement> rows = timesTable.findElements(By.tagName("tr"));
		
		int counter = 1;
		int dayOfWeek = 0;
		int amountMade = 0;
		for(int i = 0; i < rows.size(); i++)
		{
			if(data.get(FIREFLY_ROWS_BY_DAY[dayOfWeek]).size() != 0)
			{
				String startTime = String.valueOf(data.get(FIREFLY_ROWS_BY_DAY[dayOfWeek]).get(counter - 1).getStart());
				String endTime = String.valueOf(data.get(FIREFLY_ROWS_BY_DAY[dayOfWeek]).get(counter - 1).getStop());
				List<WebElement> checkboxes = driver.findElements(WORKLIST_CHECKBOX_BY);
				checkboxes.forEach(System.out::println);
				checkboxes.get(i).click();
				List<WebElement> buttons = driver.findElements(SHIFT_TYPE_BUTTON_BY);
				buttons.get(i).click();
				try
				{
					Thread.sleep(250);
				}
				catch(InterruptedException e)
				{
					return false;
				}
				List<WebElement> firstWorkType = driver.findElements(SHIFT_TYPE_OPTION_BY);
				firstWorkType.get(amountMade++).click();
				
				
				List<WebElement> inputs = driver.findElements(TIME_TEXTBOX_BY);

				inputs.get(i * 3 + 1).click();
				inputs = driver.findElements(TIME_TEXTBOX_BY);
				
				inputs.get(i * 3 + 1)
					.sendKeys(startTime);
				
				inputs = driver.findElements(TIME_TEXTBOX_BY);

				inputs.get(i * 3 + 2).click();
				inputs = driver.findElements(TIME_TEXTBOX_BY);
				
				inputs.get(i * 3 + 2)
					.sendKeys(endTime);
			}
			if(counter >= data.get(FIREFLY_ROWS_BY_DAY[dayOfWeek]).size())
			{
				counter = 1;
				dayOfWeek++;
			}
			else
			{
				counter++;
			}
		}
		
		return true;
	}
	
	/**
	 * Sets the browser type.
	 * @param browserType browser type to set
	 */
	@Override
	public void setBrowserType(BrowserType browserType)
	{
		if(browserType == null)
		{
			browserType = DEFAULT_BROWSER;
		}
		else
		{
			this.browserType = browserType;
		}
	}
	
	/**
	 * Gets the last action in the FIREFLY_UPLOAD_PROCESS that failed.
	 * @return the last action that failed to complete its job in the firefly process.
	 */
	public AdvancedAction getLastFailedAction()
	{
		return FIREFLY_UPLOAD_PROCESS.getLastFailedAction();
	}
	
	/**
	 * Sets the web driver to this tutor's browser type.
	 * This will cause the web driver to be alive and
	 * visual.
	 */
	@Override
	protected void setDriver()
	{
		super.setDriver();
		FIREFLY_UPLOAD_PROCESS.setDriver(driver, true);
	}
	
	/**
	 * Interrupts this DriverUser. This will interrupt its process, so that when another action 
	 * is attempted, it will instead be interrupted and run its iterrupt procedure.
	 */
	public void interrupt()
	{
		FIREFLY_UPLOAD_PROCESS.interrupt();
	}
}
