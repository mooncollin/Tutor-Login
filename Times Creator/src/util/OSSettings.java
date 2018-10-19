package util;

import java.io.BufferedInputStream;
import java.io.IOException;

public class OSSettings
{
	private static final String WINDOWS_DEFAULT_BROWSER_REGISTRY_KEY = "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\Shell\\Associations\\UrlAssociations\\http\\UserChoice\\";
	private static final String FIREFOX = "FirefoxURL";
	private static final String CHROME = "ChromeHTML";
	private static final String INTERNET_EXPLORER = "IE.HTTP";
	
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
