package collin.timescreator.util;

import java.time.Instant;

/**
 * A log entry that stores its creation time for log purposes.
 * @author colli
 *
 */
public class Log implements Cloneable, Comparable<Log>
{
	/**
	 * The message of this log.
	 */
	private String message;
	
	/**
	 * The log level of this log.
	 */
	private LogLevel level;
	
	/**
	 * The time of creation of this log entry.
	 */
	private Instant time;
	
	/**
	 * Constructor. Initializes message, log level, and records 
	 * its current moment in time.
	 * @param message the log message
	 * @param level the log level
	 */
	public Log(String message, LogLevel level)
	{
		time = Instant.now();
		setMessage(message);
		setLogLevel(level);
	}
	
	/**
	 * Sets this current log's message.
	 * @param message a message for this log.
	 */
	public void setMessage(String message)
	{
		this.message = message;
	}
	
	/**
	 * Sets this current log's log level.
	 * @param level a log level
	 */
	public void setLogLevel(LogLevel level)
	{
		this.level = level;
	}
	
	/**
	 * Retrieves this log's message.
	 * @return a message
	 */
	public String getMessage()
	{
		return message;
	}
	
	/**
	 * Retrieves this log's log level.
	 * @return a log level
	 */
	public LogLevel getLogLevel()
	{
		return level;
	}
	
	/**
	 * Retrieves this log's time of creation.
	 * @return an Instant time
	 */
	public Instant getTime()
	{
		return time;
	}
	
	/**
	 * Deep copy of this log. Even the time is copied over.
	 */
	@Override
	public Object clone()
	{
		Log cloned;
		try
		{
			cloned = (Log) super.clone();
		} catch (CloneNotSupportedException e)
		{
			return null;
		}
		cloned.setMessage(new String(getMessage()));
		cloned.time = Instant.from(this.time);
		return (Object) cloned;
	}
	
	/**
	 * Compares two logs. Two logs are equal if their messages, log level, 
	 * and times are equal (very unlikely). This log is less than the log 
	 * passed if its time is less, and this log is greater if its time is greater.
	 * @param o another Log
	 * @return 0 if message, log level, and times equal. -1 if time is less, and 
	 * 1 if time is greater.
	 */
	@Override
	public int compareTo(Log l)
	{
		if(equals(l))
		{
			return 0;
		}
		
		return this.time.compareTo(l.time);
	}
	
	/**
	 * Compares two Logs on their message, log level, and time.
	 * @param o another Object
	 * @return true if message, log level, and time are equal, false 
	 * otherise.
	 */
	@Override
	public boolean equals(Object o)
	{
		if(!(o instanceof Log))
		{
			return false;
		}
		
		Log other = (Log) o;
		return other.message.equals(this.message)
				&& other.level.equals(this.level)
				&& other.time.equals(this.time);
	}
}
