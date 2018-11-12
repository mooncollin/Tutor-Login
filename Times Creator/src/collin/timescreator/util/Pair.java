package collin.timescreator.util;

/**
 * Class that contains two objects.
 * @author colli
 *
 * @param <T> First object
 * @param <U> Second object
 */
public class Pair<T, U>
{
	/**
	 * First object.
	 */
	private T first;
	
	/**
	 * Second object.
	 */
	private U second;
	
	/**
	 * Constructor that sets the first and second object.
	 * @param firstObject first object to store
	 * @param secondObject second object to store
	 */
	public Pair(T firstObject, U secondObject)
	{
		setFirst(firstObject);
		setSecond(secondObject);
	}
	
	/**
	 * Sets the first object.
	 * @param firstObject object to set
	 */
	public void setFirst(T firstObject)
	{
		first = firstObject;
	}
	
	/**
	 * Sets the second object.
	 * @param secondObject object to set
	 */
	public void setSecond(U secondObject)
	{
		second = secondObject;
	}
	
	
	/**
	 * Gets the first object.
	 * @return first object.
	 */
	public T getFirst()
	{
		return first;
	}
	
	/**
	 * Gets the second object.
	 * @return second object.
	 */
	public U getSecond()
	{
		return second;
	}
	
	/**
	 * Compares two pair objects.
	 * @param the other object to compare to this Pair
	 * @return true if both objects equals() return true, false otherwise.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object other)
	{
		if(!(other instanceof Pair))
		{
			return false;
		}
		
		return ((Pair<T, U>)other).first.equals(first) && ((Pair<T, U>)other).second.equals(second);
	}
}
