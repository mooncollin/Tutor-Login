package collin.timescreator.selenium.utils;

import collin.timescreator.util.Procedure;

/**
 * A thread who is specifically working with a DriverUser.
 * This helps detach the main application from the Selenium
 * WebDriver to allow for the main application to work at the
 * same time.
 * @author colli
 *
 */
public class DriverThread<T extends DriverUser> extends Thread
{
	/**
	 * The current DriverUser.
	 */
	private T driverUser;
	
	/**
	 * Procedure to perform when this thread is needed to reset 
	 * any state of the program.
	 */
	private Procedure resetProcedure;
	
	/**
	 * Sets the DriverUser to the given one
	 * @param driver a user of a Selenium WebDriver
	 */
	public DriverThread(T driver)
	{
		setDriver(driver);
	}
	
	/**
	 * Sets the reset procedure
	 * @param p Procedure to execute when this thread finishes its job. Set 
	 * to null to not do anything.
	 */
	public void setResetProcedure(Procedure p)
	{
		this.resetProcedure = p;
	}
	
	/**
	 * Sets the DriverUser's dead driver procedure when the DriverUser attempts 
	 * to perform WebDriver actions when the current WebDriver is dead.
	 * @param p Procedure to execute when the WebDriver is dead and an action is requested 
	 * to be performed. Set to null to not do anything.
	 */
	public void setDeadProcedure(Procedure p)
	{
		this.driverUser.setDeadProcedure(p);
	}
	
	/**
	 * Sets the DriverUser's interrupt procedure when the DriverUser attempts 
	 * to perform WebDriver actions when it is currently interrupted.
	 * @param p Procedure to execute when the DriverUser is interrupted and an action 
	 * is requested to be performed. Set to null to not do anything.
	 */
	public void setInterruptProcedure(Procedure p)
	{
		this.driverUser.setInterruptProcedure(p);
	}
	
	/**
	 * Returns its reset procedure.
	 * @return reset procedure
	 */
	public Procedure getResetProcedure()
	{
		return resetProcedure;
	}
	
	/**
	 * Returns the DriverUser's dead driver procedure.
	 * @return dead driver procedure
	 */
	public Procedure getDeadProcedure()
	{
		return driverUser.getDeadProcedure();
	}
	
	/**
	 * Returns the DriverUser's interrupt procedure.
	 * @return interrupt procedure
	 */
	public Procedure getInterruptProcedure()
	{
		return driverUser.getInterruptProcedure();
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
	protected void reset()
	{
		if(resetProcedure != null)
			resetProcedure.run();
	}
}
