package selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * An action that clicks on a specific WebElement.
 * @author colli
 *
 */
public class ClickAction extends AdvancedAction
{
	/**
	 * Sets the WebDriver and By search condition.
	 * @param driver WebDriver
	 * @param by search condition
	 */
	public ClickAction(WebDriver driver, By by)
	{
		super(driver, by);
	}
	
	/**
	 * Clicks on the specific element found by the search condition.
	 */
	public void perform()
	{
		getDriver().findElement(getBy()).click();
	}
}
