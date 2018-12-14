package collin.timescreator.util;

import java.util.List;

/**
 * A Thread that uses a Logger to log information to stdout. 
 * @author colli
 *
 */
public class LoggingThread extends Thread
{
	/**
	 * A logger that stores logs.
	 */
	private Logger logger;
	
	/**
	 * Constructor. Makes a default logger.
	 */
	public LoggingThread()
	{
		this(new Logger());
	}
	
	/**
	 * Constructor. Uses a user defined logger.
	 * @param l a given logger
	 * @throws NullPointerException if the given logger is null
	 */
	public LoggingThread(Logger l) throws NullPointerException
	{
		setLogger(l);
	}
	
	/**
	 * Sets this thread's logger.
	 * @param l a given logger
	 * @throws NullPointerException if the given logger is null
	 */
	public void setLogger(Logger l) throws NullPointerException
	{
		if(l == null)
		{
			throw new NullPointerException();
		}
		this.logger = l;
	}
	
	/**
	 * Retrieves this thread's logger.
	 * @return a logger
	 */
	public Logger getLogger()
	{
		return logger;
	}
	
	/**
	 * Method to run when this thread starts.
	 */
	@Override
	public void run()
	{
		while(!isInterrupted())
		{
			try
			{
				Thread.sleep(100);
			} catch (InterruptedException e)
			{
				return;
			}
			if(!logger.isEmpty())
			{
				List<Log> logs = logger.getAllLogs();
				for(Log l : logs)
				{
					String[] lines = l.getMessage().split("\n");
					System.out.printf("LOG-- %30s [%s]:\n", l.getTime(), l.getLogLevel());
					for(int i = 0; i < lines.length; i++)
					{
						System.out.printf("\t%s\n", lines[i]);
					}
				}
			}
		}
	}
}
