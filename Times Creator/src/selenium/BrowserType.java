package selenium;

/**
 * Used to signify browser types. Can also convert
 * browser names into its enum value.
 * @author colli
 *
 */
public enum BrowserType
{
	FIREFOX, CHROME, EDGE, INTERNET_EXPLORER, OPERA, SAFARI, ANY, HTMLUNIT;
	
	/**
	 * Firefox string name.
	 */
	public static final String FIREFOX_NAME = "Firefox";
	
	/**
	 * Chrome string name.
	 */
	public static final String CHROME_NAME = "Chrome";
	
	/**
	 * Microsoft Edge string name.
	 */
	public static final String EDGE_NAME = "Microsoft Edge";
	
	/**
	 * Internet Explorer string name.
	 */
	public static final String INTERNET_EXPLORER_NAME = "Internet Explorer";
	
	/**
	 * Opera string name.
	 */
	public static final String OPERA_NAME = "Opera";
	
	/**
	 * Safari string name.
	 */
	public static final String SAFARI_NAME = "Safari";
	
	/**
	 * HTML Unit string name.
	 */
	public static final String HTML_UNIT = "HTML Unit";
	
	/**
	 * Converts a string into its equivalent enum.
	 * @param name string of browser name
	 * @return enum value or null if there is no matching string.
	 */
	public static BrowserType browserNameToEnum(String name)
	{
		BrowserType type = null;
		switch(name)
		{
			case FIREFOX_NAME:
				type = FIREFOX;
				break;
			case CHROME_NAME:
				type = CHROME;
				break;
			case EDGE_NAME:
				type = EDGE;
				break;
			case INTERNET_EXPLORER_NAME:
				type = INTERNET_EXPLORER;
				break;
			case OPERA_NAME:
				type = OPERA;
				break;
			case SAFARI_NAME:
				type = SAFARI;
				break;
			case HTML_UNIT:
				type = HTMLUNIT;
				break;
			default:
				type = null;
		}
		return type;
	}
}
