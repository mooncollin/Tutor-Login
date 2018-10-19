package util;

public class Pair<T, U>
{
	T first;
	U second;
	
	public Pair(T firstObject, U secondObject)
	{
		setFirst(firstObject);
		setSecond(secondObject);
	}
	
	public void setFirst(T firstObject)
	{
		first = firstObject;
	}
	
	public void setSecond(U secondObject)
	{
		second = secondObject;
	}
	
	public T getFirst()
	{
		return first;
	}
	
	public U getSecond()
	{
		return second;
	}
	
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
