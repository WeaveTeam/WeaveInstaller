package weave.utils;

import weave.Globals;
import weave.Settings;
import weave.Settings.OS_ENUM;

public class SyscallCreatorUtils extends Globals
{
	/**
	 * Generates a system independent string list that can be passed to {@link ProcessUtils#run(String[])}.
	 * This follows the system's native syntax for running a command in its own shell.
	 *  
	 * @param cmd The command to run in the shell
	 * @return A string list of the shell program, shell carry, and command
	 */
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
