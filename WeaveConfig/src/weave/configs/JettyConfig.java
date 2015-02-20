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

package weave.configs;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import weave.Settings;
import weave.async.AsyncCallback;
import weave.async.AsyncTask;
import weave.managers.ConfigManager;
import weave.managers.IconManager;
import weave.utils.BugReportUtils;
import weave.utils.ObjectUtils;
import weave.utils.ProcessUtils;
import weave.utils.RemoteUtils;
import weave.utils.SyscallCreatorUtils;
import weave.utils.TraceUtils;
import weave.utils.TransferUtils;

public class JettyConfig extends Config
{
	public static final String NAME = "Jetty";
	public static final String HOST = "localhost";
	public static final int PORT = 8084;
	
	public static JettyConfig _instance = null;
	public static JettyConfig getConfig()
	{
		if( _instance == null )
			_instance = new JettyConfig();
		return _instance;
	}
	
	public JettyConfig()
	{
		super(NAME, HOST, PORT);
	}

	@Override public void initConfig()
	{
		super.initConfig(_HOST | _PORT | _VERSION);
		
		File thisPluginDir = new File(Settings.DEPLOYED_PLUGINS_DIRECTORY, CONFIG_NAME);
		try {
			setWebappsDirectory(new File(thisPluginDir, "webapps"));
			setDownloadURL(RemoteUtils.getConfigEntry(RemoteUtils.JETTY_URL));
			setDescription(getConfigName() + " is a free and open-source project as part of the Eclipse Foundation.");
			setWarning(	"<center><b>" + getConfigName() + " is a plugin that will run inside the tool and does not require external configuration.<br>" + 
						"This is the appropriate choice for new users.</b></center>");
			setImage(ImageIO.read(IconManager.IMAGE_JETTY));
			
		} catch (IOException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		}
	}
	
	@Override public boolean loadConfig() 
	{
		boolean result = ConfigManager.getConfigManager().setContainer(_instance);
		
		try {
			if( !Settings.LOCK_FILE.exists() )
				return false;
			
			if( result ) {
				startServer();
				super.loadConfig();
			}
			else
				JOptionPane.showMessageDialog(null, 
						"There was an error loading the " + getConfigName() + " config.\n" + 
						"Another config might already be loaded.", 
						"Error", JOptionPane.ERROR_MESSAGE);
		} catch (NumberFormatException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		}
		
		return result;
	}

	@Override public boolean unloadConfig() 
	{
		boolean result = ConfigManager.getConfigManager().setContainer(null);
		stopServer();
		super.unloadConfig();
		return result;
	}
	
	
	public void startServer()
	{
		final AsyncTask startTask = new AsyncTask() {
			@Override
			public Object doInBackground() {
				Object o = TransferUtils.FAILED;

				TraceUtils.trace(TraceUtils.STDOUT, "-> Starting " + getConfigName() + " server..........");

				try {
					String basePath = (String)ObjectUtils.ternary(getWebappsDirectory(), "getAbsolutePath", "") + "/../";
					File logStdout = new File(basePath + Settings.F_S + "logs" + Settings.F_S, TraceUtils.getLogFile(TraceUtils.STDOUT).getName());
					File logStderr = new File(basePath + Settings.F_S + "logs" + Settings.F_S, TraceUtils.getLogFile(TraceUtils.STDERR).getName());
					String[] START = SyscallCreatorUtils.generate("java -jar \"" + basePath + "start.jar\" " +
											"jetty.logs=\"" + basePath + "/logs/\" " +
											"jetty.home=\"" + basePath + "\" " +
											"jetty.base=\"" + basePath + "\" " +
											"jetty.port=" + _port + " " +
//											"--debug " +
//											"--module=logging " +
											"STOP.PORT=" + (_port+1) + " STOP.KEY=jetty");
					
					o = ProcessUtils.run(START, logStdout, logStderr);
				} catch (InterruptedException e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				} catch (NoSuchMethodException e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				} catch (SecurityException e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				} catch (IllegalAccessException e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				} catch (IllegalArgumentException e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				} catch (InvocationTargetException e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				} catch (IOException e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				}
				return o;
			}
		};
		AsyncCallback stopCallback = new AsyncCallback() {
			@Override
			public void run(Object o) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
				}
				startTask.execute();
			}
		};
		AsyncTask stopTask = new AsyncTask() {
			@Override
			public Object doInBackground() {
				stopServer();
				return null;
			}
		};
		
		// If the service is already running, try to stop it first.
		// Might have been caused by a previous improper shutdown
		if( Settings.isServiceUp(getHost(), getPort()) )
		{
			stopTask.addCallback(stopCallback);
			stopTask.execute();
		}
		else
		{
			startTask.execute();
		}
	}
	public static String stop()
	{
		return getConfig().stopServer().toString();
	}
	public Map<String, List<String>> stopServer()
	{
		TraceUtils.trace(TraceUtils.STDOUT, "-> Stopping " + getConfigName() + " server..........");

		try {
			String basePath = (String)ObjectUtils.ternary(getWebappsDirectory(), "getAbsolutePath", "") + "/../";
			String[] STOP = SyscallCreatorUtils.generate("java -jar \"" + basePath + "start.jar\" " +
												"jetty.base=\"" + basePath + "\" " +
												"STOP.PORT=" + (_port+1) + " STOP.KEY=jetty --stop");
			return ProcessUtils.run(STOP);
		} catch (InterruptedException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		} catch (NoSuchMethodException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		} catch (SecurityException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		} catch (IllegalAccessException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		} catch (IllegalArgumentException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		} catch (InvocationTargetException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		} catch (IOException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		}
		return null;
	}
}
