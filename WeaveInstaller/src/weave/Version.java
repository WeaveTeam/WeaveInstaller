package weave;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public class Version 
{
	public static void main(String args[])
	{
		Properties p = System.getProperties();
		Map<String, String> env = System.getenv();
		
		System.out.println("\nProperties");
		for( Entry<Object, Object> entry : p.entrySet() )
		{
			System.out.printf("%30s : ", entry.getKey());
			System.out.printf("%s\n", entry.getValue());
		}
		System.out.println("");
		
		System.out.println("\nEnvironment");
		for( Map.Entry<String, String> entry : env.entrySet() )
		{
			System.out.printf("%30s : ", entry.getKey());
			System.out.printf("%s\n", entry.getValue());
		}
		System.out.println("");
	}
}
