package weave.utils;

import weave.Settings;
import weave.Settings.OS_TYPE;

public class SyscallCreatorUtils 
{
	public static String[] generate(String cmd)
	{
		String shell = "";
		String carry = "";
		
		if( Settings.OS == OS_TYPE.WINDOWS )
		{
			shell = System.getenv("ComSpec");
			carry = "/C";
		}
		else if( Settings.OS == OS_TYPE.LINUX || Settings.OS == OS_TYPE.MAC )
		{
			shell = System.getenv("SHELL");
			carry = "-c";
		}
		
		return new String[] {
			shell,
			carry,
			cmd
		};
	}
}
