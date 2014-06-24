/*
    Weave (Web-based Analysis and Visualization Environment)
    Copyright (C) 2008-2014 University of Massachusetts Lowell

    This file is a part of Weave.

    Weave is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License, Version 3,
    as published by the Free Software Foundation.

    Weave is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Weave.  If not, see <http://www.gnu.org/licenses/>.
*/

package weave.utils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;

import weave.Settings;
import weave.managers.ConfigManager;

public class LaunchUtils
{
	public static Boolean launch(String path, int delay) throws IOException, URISyntaxException, InterruptedException
	{
		if( Desktop.isDesktopSupported() )
		{
			Thread.sleep(delay);
			Desktop.getDesktop().browse(new URI(path));
			return true;
		}
		return false;
	}
	
	private static Boolean launch(File f, int delay) throws IOException, InterruptedException
	{
		File launcher = new File(Settings.BIN_DIRECTORY, Settings.LAUNCHER_JAR);
		
		if( !launcher.exists() ) {
			TraceUtils.traceln(TraceUtils.STDOUT, "!! Program not found: \"" + launcher.getCanonicalPath() + "\"");
			JOptionPane.showMessageDialog(null, "Launch Utilities could not be found.\n\n" + 
												"If this problem persists, please make sure\n" + 
												"you are running the latest version of the tool.", "Missing File", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		Settings.releaseLock();
		
		String[] command = {
			"cmd", 
			"/c", 
			"java -jar \"" + launcher.getCanonicalPath() + "\" \"" + f.getCanonicalPath() + "\" \"" + delay + "\""
		};
		ProcessUtils.runAndWait(command);
		
 		return true;
	}
	
	public static Boolean launchWeaveUpdater() throws IOException, InterruptedException
	{
		return launchWeaveUpdater(700);
	}
	public static Boolean launchWeaveUpdater(int delay) throws IOException, InterruptedException
	{
		File updater= null;
		File WU		= new File(Settings.BIN_DIRECTORY, Settings.UPDATER_JAR);
		File WUN	= new File(Settings.BIN_DIRECTORY, Settings.UDPATER_NEW_JAR);
		
		if( WU.exists() )
			updater = WU;
		else if( WUN.exists() )
			updater = WUN;
		
		return launch(updater, delay);
	}
	
	public static Boolean launchWeaveInstaller() throws IOException, InterruptedException
	{
		return launchWeaveInstaller(700);
	}
	public static Boolean launchWeaveInstaller(int delay) throws IOException, InterruptedException
	{
		return launch(new File(Settings.BIN_DIRECTORY, Settings.INSTALLER_JAR), delay);
	}
	
	public static Boolean openAdminConsole() throws IOException, URISyntaxException, InterruptedException
	{
		return openAdminConsole(100);
	}
	public static Boolean openAdminConsole(int delay) throws IOException, URISyntaxException, InterruptedException
	{
		if( ConfigManager.getConfigManager().getContainer() == null )
		{
			JOptionPane.showMessageDialog(null,	
					"   You do not have an active servlet container.   ", 
					"Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return launch("http://" + 
				Settings.LOCALHOST + ":" + 
				ConfigManager.getConfigManager().getContainer().getPort(),
				delay);
	}
}