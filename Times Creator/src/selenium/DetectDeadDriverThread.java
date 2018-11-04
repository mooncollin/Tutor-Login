package selenium;


/**
 * A thread responsible for checking up on the WebDriver if
 * it dies, and will reset the start button if the WebDriver
 * dies unexpectingly.
 * @author colli
 *
 */
public class DetectDeadDriverThread<T extends DriverUser> extends Thread
{
	public static final long DEFAULT_WAIT_MILLISECONDS = 6000;
	/**
	 * Whether this thread should stop.
	 */
	private boolean stopped;
	
	/**
	 * The amount of time to wait before initially checking
	 * the WebDriver.
	 */
	private long waitTime;
	
	/**
	 * The firefly thread is alert when the driver has died.
	 */
	private DriverThread<T> driverThread;
	
	/**
	 * Sets the user and sets the wait time before
	 * initially checking the WebDriver to 0 milliseconds.
	 * @param user WebDriver user
	 */
	public DetectDeadDriverThread(DriverThread<T> driverThread)
	{
		this(driverThread, DEFAULT_WAIT_MILLISECONDS);
	}
	
	/**
	 * Sets the user and sets the wait time before
	 * initially checking the WebDriver to the given wait time.
	 * @param user WebDriver user
	 * @param waitTime time to wait before initially checking
	 * the WebDriver.
	 */
	public DetectDeadDriverThread(DriverThread<T> driverThread, long waitTime)
	{
		this.driverThread = driverThread;
		this.waitTime = waitTime;
	}
	
	/**
	 * Stops this current thread.
	 */
	public void stopThread()
	{
		stopped = true;
	}
	
	/**
	 * Runs this current thread.
	 */
	public void run()
	{
		stopped = false;
		try
		{
			Thread.sleep(waitTime);
		} catch (InterruptedException e)
		{
			return;
		}
		while(!stopped)
		{
			try
			{
				Thread.sleep(500);
			} catch (InterruptedException e)
			{
				return;
			}
			if(driverThread.getDriverUser().isDriverDead())
			{
				driverThread.reset();
				stopped = true;
			}
		}			
	}
}