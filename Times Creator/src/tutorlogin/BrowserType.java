package tutorlogin;

public enum BrowserType
{
	FIREFOX, CHROME, EDGE, INTERNET_EXPLORER, OPERA, SAFARI, ANY, HTMLUNIT;
	
	public static final String FIREFOX_NAME = "Firefox";
	public static final String CHROME_NAME = "Chrome";
	public static final String EDGE_NAME = "Microsoft Edge";
	public static final String INTERNET_EXPLORER_NAME = "Internet Explorer";
	public static final String OPERA_NAME = "Opera";
	public static final String SAFARI_NAME = "Safari";
	public static final String HTML_UNIT = "HTML Unit";
	
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
				type = ANY;
		}
		return type;
	}
}
