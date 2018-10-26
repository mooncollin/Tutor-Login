package selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * An action that sends text to a specific WebElement.
 * @author colli
 *
 */
public class KeysAction extends AdvancedAction
{
	/**
	 * The text to send to the WebElement.
	 */
	private String keys;
	
	/**
	 * Sets the WebDriver and By search condition.
	 * @param driver WebDriver
	 * @param by search condition
	 */
	public KeysAction(WebDriver driver, By by, String keys)
	{
		super(driver, by);
		setKeys(keys);
	}
	
	/**
	 * Sets the text.
	 * @param keys text
	 */
	public void setKeys(String keys)
	{
		this.keys = keys;
	}
	
	/**
	 * Gets the text.
	 * @return text
	 */
	public String getKeys()
	{
		return keys;
	}
	
	/**
	 * Sends text to the specific element found by the search condition.
	 */
	public void perform()
	{
		getDriver().findElement(getBy()).sendKeys(keys);
	}
}
