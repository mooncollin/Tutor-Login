package selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

/**
 * An action that clicks on a specific WebElement.
 * @author colli
 *
 */
public class ClickAction extends AdvancedAction
{
	private int amountToClick;
	
	/**
	 * Sets the WebDriver and By search condition.
	 * @param driver WebDriver
	 * @param by search condition
	 */
	public ClickAction(WebDriver driver, By by)
	{
		this(driver, by, 1);
	}
	
	public ClickAction(WebDriver driver, By by, int amountToClick)
	{
		super(driver, by);
		setClickAmount(amountToClick);
	}
	
	public void setClickAmount(int amountToClick)
	{
		this.amountToClick = amountToClick;
	}
	
	public int getClickAmount()
	{
		return amountToClick;
	}
	
	/**
	 * Clicks on the specific element found by the search condition.
	 */
	public void perform()
	{
		try
		{
			for(int i = 0; i < amountToClick; i++)
			{
				getDriver().findElement(getBy()).click();
			}
		}
		catch(WebDriverException e)
		{
			
		}
	}
}
