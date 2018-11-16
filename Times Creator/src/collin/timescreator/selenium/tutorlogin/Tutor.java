package collin.timescreator.selenium.tutorlogin;

import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import collin.timescreator.selenium.utils.AdvancedActions;
import collin.timescreator.selenium.utils.BrowserType;
import collin.timescreator.selenium.utils.ClickAction;
import collin.timescreator.selenium.utils.DriverUser;
import collin.timescreator.selenium.utils.KeysAction;
import collin.timescreator.selenium.utils.WaitAction;
import collin.timescreator.util.Procedure;

/**
 * Allows for interacting with the CSLC tutoring portal.
 * @author colli
 *
 */
public class Tutor extends DriverUser
{
	/**
	 * URL for the CSLC tutoring portal.
	 */
	private static final String TUTOR_URL = "https://tutoring.ist.unomaha.edu";
	
	/**
	 * The default browser to use to interact with the CSLC tutoring portal.
	 */
	private static final BrowserType DEFAULT_BROWSER = BrowserType.CHROME;
	
	/**
	 * Default amount of seconds to wait for a new page to load.
	 */
	private static final int DEFAULT_SECONDS_WAITING = 10;
	
	/**
	 * The chain of actions to perform for navigating to the tutor edit page.
	 */
	private final AdvancedActions TUTOR_WORKING_PROCESS = AdvancedActions.of(
			new ClickAction(null, By.linkText("Tutor Login")),
			new WaitAction(null, By.id("identifierId"), DEFAULT_SECONDS_WAITING),
			new KeysAction(null, By.id("identifierId"), null), // email field
			new ClickAction(null, By.id("identifierNext")),
			new WaitAction(null, By.id("username"), DEFAULT_SECONDS_WAITING),
			new KeysAction(null, By.id("username"), null), // username field
			new KeysAction(null, By.id("password"), null), // password field
			new ClickAction(null, By.name("_eventId_proceed")),
			new WaitAction(null, By.linkText("Tutor"), DEFAULT_SECONDS_WAITING),
			new ClickAction(null, By.linkText("Tutor")),
			new ClickAction(null, By.linkText("Edit User")),
			new WaitAction(null, By.id("is_working"), DEFAULT_SECONDS_WAITING)
	);
	
	/**
	 * The tutor email.
	 */
	private String email;
	
	/**
	 * The tutor username.
	 */
	private String username;
	
	/**
	 * The tutor password;
	 */
	private String password;
	
	/**
	 * The list of classes that this tutor can tutor.
	 */
	private List<String> classes;
	
	/**
	 * Tutor constructor. Sets the username, password, email, and browser type.
	 * @param usernameIn tutor username
	 * @param passwordIn tutor password
	 * @param emailIn tutor email
	 * @param browserType browser type to use with the web driver
	 */
	public Tutor(String usernameIn, String passwordIn, String emailIn, BrowserType browserType)
	{
		super(browserType);
		setUsername(usernameIn);
		setPassword(passwordIn);
		setEmail(emailIn);
	}
	
	@Override
	public void setDeadProcedure(Procedure action)
	{
		super.setDeadProcedure(action);
		TUTOR_WORKING_PROCESS.setDeadAction(action);
	}
	
	@Override
	public void setInterruptProcedure(Procedure action)
	{
		super.setInterruptProcedure(action);
		TUTOR_WORKING_PROCESS.setInterruptAction(action);
	}
	
	/**
	 * Sets the email.
	 * @param emailIn email to set
	 */
	public void setEmail(String emailIn)
	{
		if(emailIn == null)
			throw new NullPointerException();
		email = emailIn;
		((KeysAction) TUTOR_WORKING_PROCESS.findActionWithBy(By.id("identifierId"), KeysAction.class)).setKeys(email);
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
	 * Sets the username.
	 * @param usernameIn username to set
	 */
	public void setUsername(String usernameIn)
	{
		if(usernameIn == null)
			throw new NullPointerException();
		username = usernameIn;
		((KeysAction) TUTOR_WORKING_PROCESS.findActionWithBy(By.id("username"), KeysAction.class)).setKeys(username);
	}
	
	/**
	 * Sets the password.
	 * @param passwordIn password to set
	 */
	public void setPassword(String passwordIn)
	{
		if(passwordIn == null)
			throw new NullPointerException();
		password = passwordIn;
		((KeysAction) TUTOR_WORKING_PROCESS.findActionWithBy(By.id("password"), KeysAction.class)).setKeys(password);
	}
	
	/**
	 * Gets the username.
	 * @return username
	 */
	public String getUsername()
	{
		return username;
	}
	
	/**
	 * Gets the password.
	 * @return password
	 */
	public String getPassword()
	{
		return password;
	}
	
	/**
	 * Returns the browser type.
	 * @return browser type
	 */
	public BrowserType getBrowserType()
	{
		return browserType;
	}
	
	/**
	 * Attempts to set to currently working to the tutoring portal.
	 * @param work true to set the tutor as currently working, false to
	 * set the tutor as currently not working
	 * @return true if successful, false otherwise.
	 */
	public boolean working(boolean work)
	{
		if(!editUser())
			return false;
		WebElement workingCheckbox = driver.findElement(By.id("is_working"));
		if(work && !workingCheckbox.isSelected()
		   ||
		   !work && workingCheckbox.isSelected())
		{
			workingCheckbox.click();
		}
		WebElement submitButton = driver.findElement(By.cssSelector("button[type=\"submit\"]"));
		submitButton.click();
		driver.quit();
		return true;
	}
	
	/**
	 * Checks if the tutor is currently working.
	 * @return true if this tutor is currently working, false otherwise
	 */
	public boolean isWorking()
	{
		editUser();
		WebElement workingCheckbox = driver.findElement(By.id("is_working"));
		return workingCheckbox.isSelected();
	}
	
	/**
	 * Checks if the tutor is currently logged in on the WebDriver.
	 * @return true if this tutor it currently logged in, false otherwise
	 */
	public boolean isLoggedIn()
	{
		startDriver(TUTOR_URL);
		try
		{
			driver.findElement(By.linkText("Tutor Login"));
		}
		catch(org.openqa.selenium.NoSuchElementException e)
		{
			return true;
		}
		return false;
	}
	
	
	/**
	 * Gives the classes this tutor can currently tutor.
	 * @return list containing names of classes
	 */
	public List<String> tutorClasses()
	{
		if(classes != null)
			return classes;
		
		if(!editUser())
			return new ArrayList<String>();
		
		List<String> classList = new ArrayList<String>();
		List<WebElement> classFormGroups = driver.findElements(By.xpath("//h2/following::input[@type='checkbox']"));
		List<WebElement> classLabels = driver.findElements(By.xpath("//h2/following::label"));
		for(int i = 1; i < classFormGroups.size(); i++)
		{
			if(classFormGroups.get(i).isSelected())
				classList.add(classLabels.get(i).getText());
		}
		classes = classList;
		return classList;
	}
	
	/**
	 * Checks if this tutor can tutor a certain class.
	 * @param className class to check
	 * @return true if this tutor can tutor a given class, false otherwise.
	 */
	public boolean canTutorClass(String className)
	{
		return tutorClasses().stream().anyMatch(n -> n.toLowerCase().contains(className.toLowerCase()));
	}
	
	/**
	 * Checks if all the credentials given are correct.
	 * @return true if all credentials are correct, false otherwise
	 */
	public boolean checkCredentials()
	{
		try
		{
			startDriver(TUTOR_URL);
			TUTOR_WORKING_PROCESS.perform(3);
			WebElement googleSignInMessage = driver.findElement(By.cssSelector("div[class='dEOOab RxsGPe']"));
			TUTOR_WORKING_PROCESS.perform(3, 4);
			try
			{
				Thread.sleep(250);
			} catch (InterruptedException e)
			{
			}
			if(googleSignInMessage.getText().contains("Couldn't find your Google Account"))
			{
				return false;
			}
			TUTOR_WORKING_PROCESS.perform(4, 8);
		}
		catch(TimeoutException e)
		{
			return false;
		}
		
		try
		{
			Thread.sleep(250);
		} catch (InterruptedException e)
		{
		}
		try
		{
			driver.findElement(By.cssSelector("p[class='form-element form-error']"));
			return false;
		}
		catch(org.openqa.selenium.NoSuchElementException e)
		{
		}
		
		return true;
	}
	
	/**
	 * Logs the user into the tutoring portal.
	 * @return true if successful, false otherwise.
	 */
	private boolean driverLogin() throws InvalidCredentials
	{
		if(isLoggedIn())
			return true;
		try
		{
			TUTOR_WORKING_PROCESS.perform(8);
		}
		catch(TimeoutException e)
		{
			driver.quit();
			throw new InvalidCredentials();
		}
		return true;
	}
	
	/**
	 * Attemps to log the tutor into the tutoring portal and
	 * open up the "edit user" section of the website.
	 * @return true is successful, false otherwise.
	 */
	private boolean editUser()
	{
		if(!driverLogin()) return false;
		TUTOR_WORKING_PROCESS.perform(8, 12);
		return true;
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
		TUTOR_WORKING_PROCESS.setDriver(driver, true);
	}
	
	public void interrupt()
	{
		TUTOR_WORKING_PROCESS.interrupt();
	}
}
