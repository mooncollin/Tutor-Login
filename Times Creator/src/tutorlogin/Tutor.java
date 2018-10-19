package tutorlogin;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

/**
 * Allows for interacting with the CSLC tutoring portal.
 * @author colli
 *
 */
public class Tutor
{
	/**
	 * URL for the CSLC tutoring portal.
	 */
	private static final String TUTOR_URL = "https://tutoring.ist.unomaha.edu";
	
	/**
	 * The default browser to use to interact with the CSLC tutoring portal.
	 */
	private static final BrowserType DEFAULT_BROWSER = BrowserType.EDGE;
	
	/**
	 * The selenium web driver.
	 */
	private WebDriver driver;
	
	/**
	 * The browser to use for the web driver.
	 */
	private BrowserType browserType;
	
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
		setUsername(usernameIn);
		setPassword(passwordIn);
		setBrowserType(browserType);
		setEmail(emailIn);
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
	}
	
	/**
	 * Sets the browser type.
	 * @param browserType browser type to set
	 */
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
	 * @return true if successful, false otherwise.
	 */
	public boolean login()
	{
		createDriver();
		return logging(true);
	}
	
	/**
	 * Attempts to set to currently not working to the tutoring portal.
	 * @return true if successful, false otherwise.
	 */
	public boolean logout()
	{
		createDriver();
		return logging(false);
	}
	
	/**
	 * Needs to be implemented.
	 * @return needs to be implemented.
	 */
	public boolean isLoggedIn()
	{
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
		
		createDriver();
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
	 * Sets working status for this tutor base on given log.
	 * @param log true to set currently working to true, false to 
	 * set currently working to false
	 * @return true if successful, false otherwise.
	 */
	private boolean logging(boolean log)
	{
		if(!editUser()) return false;
		WebElement workingCheckbox = driver.findElement(By.id("is_working"));
		if(log && !workingCheckbox.isSelected()
		   ||
		   !log && workingCheckbox.isSelected())
		{
			workingCheckbox.click();
		}
		WebElement submitButton = driver.findElement(By.cssSelector("button[type=\"submit\"]"));
		submitButton.click();
		driver.quit();
		return true;
	}
	
	/**
	 * Waits for an element on the current web driver to appear.
	 * @param by The specific search term.
	 * @param seconds seconds to wait for the given element.
	 * @return the element found, or null if it could not
	 * find it in the given time.
	 */
	private WebElement waitForPage(By by, int seconds)
	{
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
				.withTimeout(Duration.ofSeconds(seconds))
				.pollingEvery(Duration.ofSeconds(1))
				.ignoring(org.openqa.selenium.NoSuchElementException.class);
		try
		{
			return wait.until(new Function<WebDriver, WebElement>(){
				public WebElement apply(WebDriver driver) {
					return driver.findElement(by);
				}
			});
		}
		catch(Exception e)
		{
			driver.quit();
			return null;
		}
	}
	
	/**
	 * Logs the user into the tutoring portal.
	 * @return true if successful, false otherwise.
	 */
	private boolean driverLogin()
	{
		try
		{
			driver.get(TUTOR_URL);
		}
		catch(org.openqa.selenium.NoSuchSessionException e)
		{
			setDriver();
			driver.get(TUTOR_URL);
		}
		try
		{
			WebElement loginButton = driver.findElement(By.linkText("Tutor Login"));
			loginButton.click();
			if(waitForPage(By.id("identifierId"), 10) == null) return false;
			WebElement emailTextField = driver.findElement(By.id("identifierId"));
			emailTextField.clear();
			emailTextField.sendKeys(email);
			WebElement nextButton = driver.findElement(By.id("identifierNext"));
			nextButton.click();
			if(waitForPage(By.id("username"), 10) == null) return false;
			WebElement netIDTextField = driver.findElement(By.id("username"));
			WebElement netIDPasswordField = driver.findElement(By.id("password"));
			netIDTextField.clear();
			netIDTextField.sendKeys(username);
			netIDPasswordField.clear();
			netIDPasswordField.sendKeys(password);
			WebElement tutorLoginButton = driver.findElement(By.name("_eventId_proceed"));
			tutorLoginButton.click();
			if(waitForPage(By.linkText("Tutor"), 10) == null) return false;
		}
		catch(org.openqa.selenium.NoSuchElementException e)
		{
			driver.quit();
			return false;
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
		WebElement tutorButton = driver.findElement(By.linkText("Tutor"));
		tutorButton.click();
		WebElement editUserButton = driver.findElement(By.linkText("Edit User"));
		editUserButton.click();
		if(waitForPage(By.id("is_working"), 10) == null) return false;
		return true;
	}
	
	/**
	 * Sets the web driver to this tutor's browser type.
	 * This will cause the web driver to be alive and
	 * visual.
	 */
	private void setDriver()
	{
		switch(browserType)
		{
			case FIREFOX:
				driver = new FirefoxDriver();
				break;
			case CHROME:
				driver = new ChromeDriver();
				break;
			case EDGE:
				driver = new EdgeDriver();
				break;
			case INTERNET_EXPLORER:
				driver = new InternetExplorerDriver();
				break;
			case OPERA:
				driver = new OperaDriver();
				break;
			case SAFARI:
				driver = new SafariDriver();
				break;
			default:
				driver = new ChromeDriver();
		}
	}
	
	/**
	 * Creates a web driver if one does not exist already.
	 */
	private void createDriver()
	{
		if(driver == null || driver.toString().contains("null"))
			setDriver();
	}
}
