package weave.utils;

import weave.Globals;
import weave.Settings;
import weave.Settings.OS_ENUM;

public class SyscallCreatorUtils extends Globals
{
	public static String[] generate(String cmd)
	{
		String shell = "";
		String carry = "";
		
		if( Settings.OS == OS_ENUM.WINDOWS )
		{
			shell = System.getenv("ComSpec");
			carry = "/C";
		}
		else if( Settings.OS == OS_ENUM.LINUX || Settings.OS == OS_ENUM.MAC )
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
