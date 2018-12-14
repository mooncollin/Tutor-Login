package collin.timescreator.util;

/**
 * An exception for the Logger class to throw when attempting
 * to access logs when none exist.
 * @author colli
 *
 */
public class EmptyLogException extends RuntimeException
{
	public EmptyLogException()
	{
		super("Attempt to retrieve a log when there are no logs");
	}
}
