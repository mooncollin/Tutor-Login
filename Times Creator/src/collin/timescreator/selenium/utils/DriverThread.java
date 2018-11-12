package collin.timescreator.selenium.utils;

/**
 * A thread who is specifically working with a DriverUser.
 * This helps detach the main application from the Selenium
 * WebDriver to allow for the main application to work at the
 * same time.
 * @author colli
 *
 */
abstract public class DriverThread<T extends DriverUser> extends Thread
{
	/**
	 * The current DriverUser.
	 */
	private T driverUser;
	
	/**
	 * A thread responsible for checking if the current WebDriver
	 * is dead and will call the reset method of this DriverThread
	 * when the WebDriver is dead.
	 */
	protected DetectDeadDriverThread<T> deadThread;
	
	/**
	 * Sets the DriverUser to the given one
	 * @param driver a user of a Selenium WebDriver
	 */
	public DriverThread(T driver)
	{
		setDriver(driver);
		this.deadThread = new DetectDeadDriverThread<T>(this);
	}
	
	/**
	 * Gets the current driver user.
	 * @return driver user.
	 */
	public T getDriverUser()
	{
		return driverUser;
	}
	
	/**
	 * Sets the given driver user.
	 * @param driver a DriverUser.
	 */
	public void setDriver(T driver)
	{
		this.driverUser = driver;
	}
	
	/**
	 * A method that is to be used when this current DriverThread
	 * is done and certain elements on the GUI application needs to be reset.
	 */
	abstract protected void reset();
}
