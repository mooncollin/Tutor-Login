package collin.timescreator.util;

import java.io.BufferedInputStream;
import java.io.IOException;

/**
 * Contains a static method for finding the System's default
 * web browser. ONLY WORKS FOR THE WINDOWS OPERATING SYSTEM.
 * @author colli
 *
 */
public class OSSettings
{
	/**
	 * The registry key where the default browser is located.
	 */
	private static final String WINDOWS_DEFAULT_BROWSER_REGISTRY_KEY = "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\Shell\\Associations\\UrlAssociations\\http\\UserChoice\\";
	
	/**
	 * The string that the registry sets for Firefox.
	 */
	private static final String FIREFOX = "FirefoxURL";
	
	/**
	 * The string that the registry sets for Chrome.
	 */
	private static final String CHROME = "ChromeHTML";
	
	/**
	 * The string that the registry sets for Internet Explorer.
	 */
	private static final String INTERNET_EXPLORER = "IE.HTTP";
	
	/**
	 * Gets the default browser of the current system.
	 * @return a string that represents the system's current browser.
	 */
	public static String getDefaultBrowser()
	{
		if(!System.getProperty("os.name").toLowerCase().contains("win"))
			return "";
		
		Process process;
		byte[] bytes;
		String processResults;
		try {
			process = Runtime.getRuntime().exec("reg query " +
						WINDOWS_DEFAULT_BROWSER_REGISTRY_KEY);
			process.waitFor();
			BufferedInputStream br = new BufferedInputStream(process.getInputStream());
			bytes = new byte[br.available()];
			br.read(bytes);
			processResults = new String(bytes);
		} catch (IOException | InterruptedException e) {
			return "";
		}
		
		String output = "";
		if(processResults.contains(FIREFOX))
			output = "Firefox";
		else if(processResults.contains(CHROME))
			output = "Chrome";
		else if(processResults.contains(INTERNET_EXPLORER))
			output = "Internet Explorer";
		return output;
	}
}
