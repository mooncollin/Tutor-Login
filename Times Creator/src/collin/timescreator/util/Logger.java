package collin.timescreator.util;

import java.util.List;
import java.util.LinkedList;

/**
 * A collection of logs that allows for extracting out
 * oldest log given (based on that log's time).
 * @author colli
 *
 */
public class Logger
{
	private List<Log> logs;
	
	public Logger()
	{
		logs = new LinkedList<Log>();
	}
	
	public void log(Log l)
	{
		logs.add(l);
		logs.sort(null);
	}
	
	public void log(String message, LogLevel level)
	{
		log(new Log(message, level));
	}
	
	public List<Log> getAllLogs()
	{
		if(logs.isEmpty())
		{
			throw new EmptyLogException();
		}
		List<Log> allLogs = logs;
		logs = new LinkedList<Log>();
		return allLogs;
	}
	
	public Log getNextLog()
	{
		if(logs.isEmpty())
		{
			throw new EmptyLogException();
		}
		return logs.remove(0);
	}
	
	public Log peekNextLog()
	{
		if(logs.isEmpty())
		{
			throw new EmptyLogException();
		}
		return logs.get(0);
	}
	
	public int size()
	{
		return logs.size();
	}
	
	public boolean isEmpty()
	{
		return size() == 0;
	}
}
