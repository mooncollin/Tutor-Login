package collin.timescreator.selenium.utils;

import collin.timescreator.util.Procedure;

/**
 * Exception that is to be thrown when a Selenium WebDriver is dead 
 * and therefore any actions cannot be attempted.
 * @author colli
 *
 */
@SuppressWarnings("serial")
public class DeadDriverException extends RuntimeException
{
	/**
	 * Returns a Procedure when will throw a DeadDriverException.
	 * @return procedure to throw a DeadDriverException
	 */
	public static Procedure deadDriverProcedure()
	{
		return () -> {
			throw new DeadDriverException();
		};
	}
}
