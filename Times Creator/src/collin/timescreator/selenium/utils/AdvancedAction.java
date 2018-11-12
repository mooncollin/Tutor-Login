package collin.timescreator.selenium.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * An Action similar with Selenium's Action, but allows to delay the time
 * to search for a WebElement until its perform() is called.
 * @author colli
 *
 */
abstract public class AdvancedAction
{
	/**
	 * The current WebDriver.
	 */
	private WebDriver driver;
	
	/**
	 * The search condition to find the specific WebElement.
	 */
	private By by;
	
	/**
	 * Sets the WebDriver and By condition.
	 * @param driver WebDriver to find elements and interact in
	 * @param by search condition for a WebElement
	 */
	public AdvancedAction(WebDriver driver, By by)
	{
		setDriver(driver);
		setBy(by);
	}
	
	/**
	 * Sets the WebDriver
	 * @param driver WebDriver to set
	 */
	public void setDriver(WebDriver driver)
	{
		this.driver = driver;
	}
	
	/**
	 * Gets the current WebDriver
	 * @return a WebDriver
	 */
	public WebDriver getDriver()
	{
		return driver;
	}
	
	/**
	 * Sets the by search condition.
	 * @param by search condition
	 */
	public void setBy(By by)
	{
		this.by = by;
	}
	
	/**
	 * Gets the current search condition.
	 * @return search condition
	 */
	public By getBy()
	{
		return by;
	}
	
	/**
	 * Performs the action.
	 */
	abstract public void perform();
}
