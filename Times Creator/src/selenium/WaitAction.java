package selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * An action that waits for a specific WebElement to appear on
 * the screen.
 * @author colli
 *
 */
public class WaitAction extends AdvancedAction
{
	/**
	 * Amount of seconds to wait.
	 */
	private int seconds;
	
	/**
	 * Sets the WebDriver and By search condition.
	 * @param driver WebDriver
	 * @param by search condition
	 */
	public WaitAction(WebDriver driver, By by, int seconds)
	{
		super(driver, by);
		setSeconds(seconds);
	}
	
	/**
	 * Sets the amount of seconds to wait.
	 * @param seconds seconds to wait
	 */
	public void setSeconds(int seconds)
	{
		this.seconds = seconds;
	}
	
	/**
	 * Gets the amount of seconds to wait.
	 * @return seconds to wait
	 */
	public int getSeconds()
	{
		return seconds;
	}
	
	/**
	 * Waits for the specific element found by the search condition
	 * for the amount of seconds given.
	 */
	public void perform()
	{
		WebDriverWait wait = new WebDriverWait(getDriver(), this.seconds);
		wait.until(ExpectedConditions.visibilityOfElementLocated(getBy()));
	}
}
