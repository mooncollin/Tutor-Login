package collin.timescreator.selenium.utils;

import collin.timescreator.util.Procedure;

/**
 * Exception to be thrown when an action is to be executed on a Selenium 
 * WebDriver, but an interrupt status has been given.
 * @author colli
 *
 */
@SuppressWarnings("serial")
public class DriverInterruptedException extends RuntimeException
{
	/**
	 * A procedure that throws a DriverInterruptedException.
	 * @return procedure to throw a DriverInterruptedException
	 */
	public static Procedure driverInterruptedProcedure()
	{
		return () -> {
			throw new DriverInterruptedException();
		};
	}
}
