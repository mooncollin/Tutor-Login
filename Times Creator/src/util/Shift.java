package util;

import java.time.LocalTime;

public class Shift
{
	private LocalTime start;
	private LocalTime stop;
	
	public Shift(String startStr, String stopStr)
	{
		String[] startData = startStr.split(":");
		String[] stopData = stopStr.split(":");
		setStart(Integer.parseInt(startData[0]), Integer.parseInt(startData[1]));
		setStop(Integer.parseInt(stopData[0]), Integer.parseInt(stopData[1]));
	}
	
	public void setStart(int hour, int minute)
	{
		start = LocalTime.of(hour, minute);
	}
	
	public void setStop(int hour, int minute)
	{
		stop = LocalTime.of(hour, minute);
	}
	
	public LocalTime getStart()
	{
		return start;
	}
	
	public LocalTime getStop()
	{
		return stop;
	}
}
