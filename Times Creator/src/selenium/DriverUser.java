package selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.safari.SafariDriver;

public class DriverUser
{
	protected WebDriver driver;
	protected BrowserType browserType; 
	
	public DriverUser(BrowserType browserType)
	{
		setBrowserType(browserType);
	}
	
	public void setBrowserType(BrowserType browserType)
	{
		this.browserType = browserType;
	}
	
	public BrowserType getBrowserType()
	{
		return browserType;
	}
	
	public void closeDriver()
	{
		if(driver != null && !driver.toString().contains("null"))
		{
			try
			{
				driver.quit();
			}
			catch(org.openqa.selenium.WebDriverException e)
			{
			}
		}
	}
	
	/**
	 * Starts the driver if it not already started, and will go to the specified
	 * URL.
	 * @param url URL for the driver to go to, or null to not change location
	 */
	protected void startDriver(String url)
	{
		if(driver == null || driver.toString().contains("null"))
			setDriver();
		if(url != null)
			driver.get(url);
	}
	
	
	/**
	 * Sets the web driver to this users's browser type.
	 * This will cause the web driver to be alive and
	 * visual.
	 */
	protected void setDriver()
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
}
