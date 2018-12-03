package collin.timescreator.selenium.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.safari.SafariDriver;

import collin.timescreator.util.Procedure;

abstract public class DriverUser
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
	 * A Procedure to run when this DriverUser attempts to perform
	 * actions with a WebDriver, but the current WebDriver has been terminated.
	 */
	private Procedure deadProcedure;
	
	/**
	 * A Procedure to run when this DriverUser is interrupted.
	 */
	private Procedure interruptedProcedure;
	
	/**
	 * Tests whether the given WebDriver has been terminated.
	 * @param driver a WebDriver
	 * @return true if the driver is dead, false otherwise
	 */
	public static boolean isDriverDead(WebDriver driver)
	{
		return driver == null || driver.toString().contains("null");
	}
	
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
	 * Sets the procedure that will run when this DriverUser attempts to do 
	 * an action when the WebDriver has been terminated.
	 * @param action Procedure to run
	 */
	public void setDeadProcedure(Procedure action)
	{
		this.deadProcedure = action;
	}
	
	/**
	 * Gets the dead driver Procedure.
	 * @return dead driver procedure
	 */
	public Procedure getDeadProcedure()
	{
		return deadProcedure;
	}
	
	/**
	 * Gets the interrupt Procedure.
	 * @return interrupt procedure
	 */
	public Procedure getInterruptProcedure()
	{
		return interruptedProcedure;
	}
	
	/**
	 * Sets the procedure that will run when this DriverUser attemps to do 
	 * an action when it is interrupted.
	 * @param action
	 */
	public void setInterruptProcedure(Procedure action)
	{
		this.interruptedProcedure = action;
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
		if(!isDriverDead(driver))
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
	 * Gets the current WebDriver.
	 * @return the current WebDriver
	 */
	public WebDriver getDriver()
	{
		return driver;
	}
	
	/**
	 * Starts the driver if it not already started, and will go to the specified
	 * URL.
	 * @param url URL for the driver to go to, or null to not change location
	 */
	protected void startDriver(String url)
	{
		if(isDriverDead(driver))
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
			case HTMLUNIT:
				driver = new HtmlUnitDriver();
				break;
			default:
				driver = new ChromeDriver();
		}
	}
	
	/**
	 * Method that can be called to interrupt the this DriverUser
	 * and stop whatever is currently happening with the WebDriver.
	 */
	abstract public void interrupt();
}
