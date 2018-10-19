package util;

import java.time.LocalTime;

/**
 * Contains information about a shift's start and stop time.
 * @author colli
 *
 */
public class Shift
{
	/**
	 * Start time of this shift.
	 */
	private LocalTime start;
	
	/**
	 * Stop time of this shift.
	 */
	private LocalTime stop;
	
	/**
	 * Attempts to take a start and stop string and parse them for their time.
	 * Each one is split by a ':' character and each side of the ':' character
	 * is parsed for their integer value. The first number must be within the
	 * 24 hour range and the second number must be within the 60 minute range.
	 * @param startStr start time as a string
	 * @param stopStr stop time as a string
	 */
	public Shift(String startStr, String stopStr)
	{
		String[] startData = startStr.split(":");
		String[] stopData = stopStr.split(":");
		setStart(Integer.parseInt(startData[0]), Integer.parseInt(startData[1]));
		setStop(Integer.parseInt(stopData[0]), Integer.parseInt(stopData[1]));
	}
	
	/**
	 * Sets the start time.
	 * @param hour hour to set
	 * @param minute minute to set
	 */
	public void setStart(int hour, int minute)
	{
		start = LocalTime.of(hour, minute);
	}
	
	/**
	 * Sets the stop time.
	 * @param hour hour to set
	 * @param minute minute to set
	 */
	public void setStop(int hour, int minute)
	{
		stop = LocalTime.of(hour, minute);
	}
	
	/**
	 * Gets the start time.
	 * @return start time
	 */
	public LocalTime getStart()
	{
		return start;
	}
	
	/**
	 * Gets the stop time.
	 * @return stop time
	 */
	public LocalTime getStop()
	{
		return stop;
	}
}
