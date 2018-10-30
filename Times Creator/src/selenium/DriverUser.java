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
	/**
	 * The current WebDriver.
	 */
	protected WebDriver driver;
	
	/**
	 * The browser to use for the WebDriver.
	 */
	protected BrowserType browserType; 
	
	/**
	 * Sets the browser to the given browser type
	 * and constructs this object.
	 * @param browserType a browser type to use
	 * for the WebDriver.
	 */
	public DriverUser(BrowserType browserType)
	{
		setBrowserType(browserType);
	}
	
	/**
	 * Sets the browser to the givven browser type.
	 * @param browserType a browser type to use
	 * for the WebDriver.
	 */
	public void setBrowserType(BrowserType browserType)
	{
		this.browserType = browserType;
	}
	
	/**
	 * Gets the current browser type.
	 * @return type of browser that this
	 * user will use for its WebDriver.
	 */
	public BrowserType getBrowserType()
	{
		return browserType;
	}
	
	/**
	 * Attemps to close the current driver.
	 */
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
	 * Checks whether the current driver is not longer active.
	 * @return true if dead, false otherwise.
	 */
	public boolean isDriverDead()
	{
		return driver == null || driver.toString().contains("null");
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
