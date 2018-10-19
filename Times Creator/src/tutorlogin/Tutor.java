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

public class Tutor
{
	private static final String TUTOR_URL = "https://tutoring.ist.unomaha.edu";
	private static final BrowserType DEFAULT_BROWSER = BrowserType.EDGE;
	
	private WebDriver driver;
	private BrowserType browserType;
	private String email;
	private String username;
	private String password;
	private List<String> classes;
	
	public Tutor(String usernameIn, String passwordIn, String emailIn, BrowserType browserType)
	{
		setUsername(usernameIn);
		setPassword(passwordIn);
		setBrowserType(browserType);
		setEmail(emailIn);
	}
	
	public void setEmail(String emailIn)
	{
		if(emailIn == null)
			throw new NullPointerException();
		email = emailIn;
	}
	
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
	
	public void setUsername(String usernameIn)
	{
		if(usernameIn == null)
			throw new NullPointerException();
		username = usernameIn;
	}
	
	public void setPassword(String passwordIn)
	{
		if(passwordIn == null)
			throw new NullPointerException();
		password = passwordIn;
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public BrowserType getBrowserType()
	{
		return browserType;
	}
	
	public boolean login()
	{
		createDriver();
		return logging(true);
	}
	
	public boolean logout()
	{
		createDriver();
		return logging(false);
	}
	
	public boolean isLoggedIn()
	{
		return false;
	}
	
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
	
	public boolean canTutorClass(String className)
	{
		return tutorClasses().stream().anyMatch(n -> n.toLowerCase().contains(className.toLowerCase()));
	}
	
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
	
	private void createDriver()
	{
		if(driver == null || driver.toString().contains("null"))
			setDriver();
	}
}
