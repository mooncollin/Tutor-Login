package collin.timescreator.selenium.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import collin.timescreator.util.Procedure;

/**
 * A collection of AdvancedActions that allows for specific chains
 * to be executed in different manners.
 * @author colli
 *
 */
public class AdvancedActions implements Iterable<AdvancedAction>
{
	/**
	 * Current WebDriver to interact with.
	 */
	private WebDriver driver;
	
	/**
	 * List of AdvancedActions to execute.
	 */
	private List<AdvancedAction> actions;
	
	/**
	 * The last failed action that failed to succeed when performed.
	 */
	private AdvancedAction lastFailedAction;
	
	/**
	 * Procedure to perform when actions are requested to be performed, but 
	 * the current WebDriver is dead.
	 */
	private Procedure deadAction;
	
	/**
	 * Procedure to perform when actions are requested to be performed, but 
	 * this object is currently interrupted.
	 */
	private Procedure interruptAction;
	
	/**
	 * Interrupted flag.
	 */
	private boolean interrupted;
	
	/**
	 * Creates an AdvancedActions based on a driver and a given sequence
	 * of actions.
	 * @param driver WebDriver
	 * @param actions a sequence of actions
	 * @return AdvancedActions that contain the given actions
	 */
	public static AdvancedActions of(WebDriver driver, AdvancedAction ...actions)
	{
		return new AdvancedActions(driver, Arrays.asList(actions));
	}
	
	/**
	 * Creates an AdvancedActions with a null driver and a given sequence
	 * of actions.
	 * @param actions a sequence of actions
	 * @return AdvancedActions that contain the given actions
	 */
	public static AdvancedActions of(AdvancedAction ...actions)
	{
		return of(null, actions);
	}
	
	/**
	 * Sets the WebDriver given and initializes the actions list
	 * to an empty list.
	 * @param driver WebDriver
	 */
	public AdvancedActions(WebDriver driver)
	{
		this(driver, new ArrayList<AdvancedAction>());
	}
	
	/**
	 * Sets the WebDriver given and uses the current actions inside the
	 * given list.
	 * @param driver WebDriver
	 * @param actions defined list of actions
	 */
	public AdvancedActions(WebDriver driver, List<AdvancedAction> actions)
	{
		setDriver(driver);
		setActions(actions);
		interrupted = false;
	}
	
	/**
	 * Sets the current WebDriver.
	 * @param driver WebDriver
	 */
	public void setDriver(WebDriver driver)
	{
		setDriver(driver, false);
	}
	
	/**
	 * Sets the action that will execute when attempting to perform actions 
	 * find the current WebDriver to not be alive.
	 * @param action Procedure
	 */
	public void setDeadAction(Procedure action)
	{
		this.deadAction = action;
	}
	
	/**
	 * Sets the action that will execute when attempting to perform actions 
	 * and is currently interrupted.
	 * @param action Procedure
	 */
	public void setInterruptAction(Procedure action)
	{
		this.interruptAction = action;
	}
	
	/**
	 * Sets the current WebDriver. Will also change each action's current
	 * WebDriver if given a true.
	 * @param driver WebDriver
	 * @param changeElements true to change all actions to the given WebDriver,
	 * false to keep the old drivers.
	 */
	public void setDriver(WebDriver driver, boolean changeElements)
	{
		this.driver = driver;
		if(changeElements)
		{
			for(AdvancedAction a : actions)
			{
				a.setDriver(driver);
			}
		}
	}
	
	/**
	 * Sets the current actions to the given list of actions.
	 * @param actions list of actions
	 */
	public void setActions(List<AdvancedAction> actions)
	{
		this.actions = actions;
	}
	
	/**
	 * Gets the current WebDriver.
	 * @return WebDriver
	 */
	public WebDriver getDriver()
	{
		return driver;
	}
	
	/**
	 * Gets the current list of actions.
	 * @return list of actions
	 */
	public List<AdvancedAction> getActions()
	{
		return actions;
	}
	
	/**
	 * Gets an iterator over the current actions.
	 * @return an iterator over the current actions
	 */
	@Override
	public Iterator<AdvancedAction> iterator()
	{
		return actions.iterator();
	}
	
	/**
	 * Find an action that has the same search condition and class type.
	 * @param by search condition
	 * @param classType type of class
	 * @return first AdvancedAction that equal the search condition
	 * and class type
	 */
	public AdvancedAction findActionWithBy(By by, Class<?> classType)
	{
		for(AdvancedAction a : actions)
		{
			if(a.getBy().equals(by) && a.getClass().equals(classType))
				return a;
		}
		return null;
	}
	
	/**
	 * Interrupts any further actions. The next call to perform actions will 
	 * fail and instead run its interrupt action and then get reset back to 
	 * an uniterrupted state.
	 */
	public void interrupt()
	{
		interrupted = true;
	}
	
	/**
	 * Checks if this AdvancedActions is currently interrupted.
	 * @return true is it is interrupted, false otherwise
	 */
	public boolean isInterrupted()
	{
		return interrupted;
	}
	
	/**
	 * Undos the interruption method.
	 */
	public void uninterrupt()
	{
		interrupted = false;
	}
	
	/**
	 * Clears the current list of actions.
	 */
	public void clear()
	{
		actions.clear();
	}
	
	/**
	 * Gets the size of the current actions.
	 * @return the amount of currently stored actions.
	 */
	public int size()
	{
		return actions.size();
	}
	
	/**
	 * Gets the last failed action.
	 * @return the last action that failed to success its perform method.
	 */
	public AdvancedAction getLastFailedAction()
	{
		return lastFailedAction;
	}
	
	/**
	 * Creates and adds a WaitAction on the given search condition and seconds
	 * to wait.
	 * @param by search condition
	 * @param seconds seconds to wait
	 * @return itself
	 */
	public AdvancedActions waitForElement(By by, int seconds)
	{
		addAction(new WaitAction(driver, by, seconds));
		return this;
	}
	
	/**
	 * Creates and adds a KeysAction on the given search condition and
	 * keys to send.
	 * @param by search condition
	 * @param keys text to send
	 * @return itself
	 */
	public AdvancedActions sendKeys(By by, String keys)
	{
		addAction(new KeysAction(driver, by, keys));
		return this;
	}
	
	/**
	 * Creates and adds a ClickAction on the given search condition
	 * @param by search condition
	 * @return itself
	 */
	public AdvancedActions click(By by)
	{
		addAction(new ClickAction(driver, by));
		return this;
	}
	
	/**
	 * Adds an action.
	 * @param action an action to add
	 * @return itself
	 */
	public AdvancedActions addAction(AdvancedAction action)
	{
		actions.add(action);
		return this;
	}
	
	/**
	 * Perfoms all actions. If 
	 * it is currently interrupted, it will attempt to run its interrupt action. If 
	 * it is running through actions and gets interrupted, it will run its interrupt action 
	 * before the next action will execute, and will stop executing actions from this call. If 
	 * the driver is dead, then its dead action will execute, and no further actions will be executed.
	 * @throws NullPointerException If the current driver is null
	 * or the actions list is null.
	 */
	public void perform() throws NullPointerException
	{	
		perform(actions.size());
	}
	
	/**
	 * Peforms actions from the start and up to an index (exclusive). If 
	 * it is currently interrupted, it will attempt to run its interrupt action. If 
	 * it is running through actions and gets interrupted, it will run its interrupt action 
	 * before the next action will execute, and will stop executing actions from this call. If 
	 * the driver is dead, then its dead action will execute, and no further actions will be executed.
	 * @param to index in the actions list to perform up to.
	 * @throws NullPointerException If the current driver is null
	 * or the actions list is null.
	 */
	public void perform(int to) throws NullPointerException
	{
		perform(0, to);
	}
	
	/**
	 * Performs actions from a given index and up to another index (exclusive). If 
	 * it is currently interrupted, it will attempt to run its interrupt action. If 
	 * it is running through actions and gets interrupted, it will run its interrupt action 
	 * before the next action will execute, and will stop executing actions from this call. If 
	 * the driver is dead, then its dead action will execute, and no further actions will be executed.
	 * @param from index in the actions list to start performing actions
	 * @param to index in the actions list to perform up to
	 * @throws IndexOutOfBoundsException If the given indexes are out
	 * of range of the current actions list.
	 * @throws IllegalArgumentException If the given from parameter is greater
	 * than or equal to the given to parameter.
	 * @throws NullPointerException If the current driver is null
	 * or the actions list is null.
	 */
	public void perform(int from, int to) throws IndexOutOfBoundsException, IllegalArgumentException, NullPointerException
	{
		if(deadAction != null && DriverUser.isDriverDead(driver))
		{
			deadAction.run();
			return;
		}
		if(interrupted)
		{
			interruptAction.run();
			uninterrupt();
			return;
		}
		if(driver == null || actions == null)
			throw new NullPointerException();
		if(from < 0 || from >= actions.size())
			throw new IndexOutOfBoundsException(String.valueOf(from));
		if(to < 0 || to > actions.size())
			throw new IndexOutOfBoundsException(String.valueOf(to));
		if(from >= to)
			throw new IllegalArgumentException();
		
		var listIter = actions.listIterator();
		while(listIter.nextIndex() < from)
		{
			listIter.next();
		}
		while(listIter.nextIndex() < to)
		{
			AdvancedAction action = listIter.next();
			try
			{
				if(deadAction != null && DriverUser.isDriverDead(driver))
				{
					lastFailedAction = action;
					deadAction.run();
					return;
				}
				if(interrupted)
				{
					lastFailedAction = action;
					interruptAction.run();
					uninterrupt();
					return;
				}
				action.perform();
			}
			catch(org.openqa.selenium.WebDriverException e)
			{
				lastFailedAction = action;
				return;
			}
		}
	}
}
