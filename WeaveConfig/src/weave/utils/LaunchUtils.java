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

import static weave.utils.TraceUtils.*;
import static weave.utils.TraceUtils.LEVEL.*;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;

import weave.Globals;
import weave.Settings;
import weave.Settings.OS_ENUM;
import weave.managers.ConfigManager;
import weave.reflect.Reflectable;

public class LaunchUtils extends Globals
{
	
	@Reflectable
	public static Boolean browse(String path) throws IOException, URISyntaxException, InterruptedException
	{
		return browse(path, 100);
	}
	public static Boolean browse(URI path) throws IOException, InterruptedException
	{
		return browse(path, 100);
	}
	public static Boolean browse(String path, int delay) throws IOException, URISyntaxException, InterruptedException
	{
		return browse(new URI(path), delay);
	}
	public static Boolean browse(URI path, int delay) throws IOException, InterruptedException
	{
		if( !Desktop.isDesktopSupported() )
			return false;
		
		Thread.sleep(delay);
		Desktop.getDesktop().browse(path);
		return true;
	}
	
	
	
	@Reflectable
	public static Boolean open(String path) throws IOException, InterruptedException 
	{
		return open(path, 100);
	}
	public static Boolean open(String path, int delay) throws IOException, InterruptedException
	{
		return open(new File(path), delay);
	}
	public static Boolean open(File file, int delay) throws IOException, InterruptedException
	{
		if( !Desktop.isDesktopSupported() )
			return false;
		
		Thread.sleep(delay);
		Desktop.getDesktop().open(file);
		return true;
	}
	
	
	
	@Reflectable
	public static Boolean edit(String path) throws InterruptedException, IOException
	{
		return edit(path, 100);
	}
	public static Boolean edit(String path, int delay) throws InterruptedException, IOException
	{
		return edit(new File(path), delay);
	}
	public static Boolean edit(File file, int delay) throws InterruptedException, IOException
	{
		if( !Desktop.isDesktopSupported() )
			return false;
		
		Thread.sleep(delay);
		Desktop.getDesktop().edit(file);
		return true;
	}
	
	
	
	public static Boolean launchElevated(String path, int delay) throws InterruptedException, IOException
	{
		return launchElevated(new File(path), delay);
	}
	public static Boolean launchElevated(File file, int delay) throws InterruptedException, IOException
	{
		if( !Desktop.isDesktopSupported() )
			return false;
		
		if( !file.exists() ) {
			traceln(STDOUT, ERROR, "Program not found: \"" + file.getAbsolutePath() + "\"");
			JOptionPane.showMessageDialog(null, 
					"Attempt to open file: \"" + file.getAbsolutePath() + "\" failed.", 
					"File Not Found", 
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		String command = "";
		File elevate = new File(Settings.LIBS_DIRECTORY, Settings.ELEVATE_UTIL);

		if( !elevate.exists() ) {
			traceln(STDOUT, ERROR, "Elevate utility not found: \"" + elevate.getAbsolutePath() + "\"");
			JOptionPane.showMessageDialog(null,
					"A required file is missing to support this action.\n" +
					"If this problem persists, please make sure you\n" +
					"are running the latest version of the tool.", 
					"File Not Found", 
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		command += elevate.getAbsolutePath().replace("\\", "/") + " -c ";
		command += "java -jar ";
		command += file.getAbsolutePath().replace("\\", "/");

		Thread.sleep(delay);
		String[] generatedCommands = SyscallCreatorUtils.generate(command);
		traceln(STDOUT, INFO, "Elevated command: " + ObjectUtils.toString(generatedCommands));
		
		ProcessUtils.run(generatedCommands, getLogFile(STDOUT), getLogFile(STDERR));
		
		return true;
	}
	
	
	public static Boolean launch(String path, int delay) throws IOException, InterruptedException
	{
		return launch(new File(path), delay);
	}
	public static Boolean launch(File f, int delay) throws IOException, InterruptedException
	{
		String command = "";
		File launcher = new File(Settings.BIN_DIRECTORY, Settings.LAUNCHER_JAR);
		
		if( !launcher.exists() ) {
			traceln(STDOUT, ERROR, "Program not found: \"" + launcher.getAbsolutePath() + "\"");
			JOptionPane.showMessageDialog(null, "Launch Utilities could not be found.\n\n" + 
												"If this problem persists, please make sure\n" + 
												"you are running the latest version of the tool.",
												"File Not Found",
												JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		Settings.releaseLock();

		if( Settings.OS == OS_ENUM.UNKNOWN )
		{
			JOptionPane.showMessageDialog(null, 
					"Cannot launch file: " + f.getCanonicalPath() + "\n\n" +
					"Please manually launch this file.", 
					"Error", JOptionPane.ERROR_MESSAGE);
			return true;
		}
		
		command += "java -jar ";
		command += "\"" + launcher.getAbsolutePath().replace("\\", "/") + "\" ";
		command += "\"" + f.getAbsolutePath().replace("\\", "/") + "\" ";
		command += "\"" + delay + "\"";
		
		String[] generatedCommand = SyscallCreatorUtils.generate(command);
		traceln(STDOUT, INFO, "Generated command: " + ObjectUtils.toString(generatedCommand));
		
		ProcessUtils.run(generatedCommand, getLogFile(STDOUT), getLogFile(STDERR));
		
 		return true;
	}
	
	
	
	public static Boolean launchWeaveUpdater() throws IOException, InterruptedException
	{
		return launchWeaveUpdater(700);
	}
	public static Boolean launchWeaveUpdater(int delay) throws IOException, InterruptedException
	{
		File updater = null;
		File WU		 = new File(Settings.BIN_DIRECTORY, Settings.UPDATER_JAR);
		File WUN	 = new File(Settings.BIN_DIRECTORY, Settings.UPDATER_NEW_JAR);
		
		if( WU.exists() )
			updater = WU;
		else if( WUN.exists() )
			updater = WUN;
		else
			throw new FileNotFoundException("Updater file not found");
		
//		if( Settings.OS == OS_ENUM.WINDOWS )
//			return launchElevated(updater, delay);
		
		return launch(updater, delay);
	}
	
	
	
	public static Boolean launchWeaveServer() throws IOException, InterruptedException
	{
		return launchWeaveServer(700);
	}
	public static Boolean launchWeaveServer(int delay) throws IOException, InterruptedException
	{
		File server = new File(Settings.BIN_DIRECTORY, Settings.SERVER_JAR);

		if( Settings.OS == OS_ENUM.WINDOWS )
			return launchElevated(server, delay);
		
		return launch(server, delay);
	}
	
	
	
	public static Boolean openAdminConsole() throws IOException, URISyntaxException, InterruptedException
	{
		return openAdminConsole(100);
	}
	public static Boolean openAdminConsole(int delay) throws IOException, URISyntaxException, InterruptedException
	{
		if( ConfigManager.getConfigManager().getActiveContainer() == null )
		{
			JOptionPane.showMessageDialog(null,	
					"   You do not have an active servlet container.   ", 
					"Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return browse("http://" + 
				Settings.LOCALHOST + ":" + 
				ConfigManager.getConfigManager().getActiveContainer().getPort() +
				"/AdminConsole.html",
				delay);
	}
}
