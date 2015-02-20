package weave.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import weave.Globals;

public class EnvironmentUtils extends Globals
{
	public static String replace(String str)
	{
		Properties javaProps = System.getProperties();
		Map<String, String> sysEnv = new HashMap<String, String>(System.getenv());
		Map<String, String> javaEnv = new HashMap<String, String>();
		
		for( String key : javaProps.stringPropertyNames() )
			javaEnv.put(key, javaProps.getProperty(key));

		for( Entry<String, String> e : javaEnv.entrySet() )
			sysEnv.put(e.getKey(), e.getValue());
		
		for( Entry<String, String> entry : sysEnv.entrySet() )
			str = str.replaceAll("\\$\\{" + entry.getKey() + "\\}", entry.getValue().replaceAll("\\\\", "/"));
		
		return str;
	}
}
