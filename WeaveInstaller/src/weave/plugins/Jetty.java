/*
    Weave (Web-based Analysis and Visualization Environment)
    Copyright (C) 2008-2011 University of Massachusetts Lowell

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

package weave.plugins;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;

import weave.Settings;
import weave.utils.BugReportUtils;
import weave.utils.ProcessUtils;
import weave.utils.RemoteUtils;
import weave.utils.TraceUtils;

public class Jetty implements IPluginManager 
{
	
	public static String PLUGIN_NAME	= "Jetty";
	public String 	JETTY_URL 			= null;
	public File 	JETTY_HOME 			= null;
	public File 	JETTY_WEBAPPS		= null;
	public File 	JETTY_INSTALL_FILE	= null;
	public int 		JETTY_PORT			= 8084;

	private String[] START = {
		"cmd",
		"/c",
		"java -jar start.jar STOP.PORT=" + JETTY_PORT + " STOP.KEY=magic --daemon &"
	};
	private String[] STOP = {
		"cmd",
		"/c",
		"java -jar start.jar STOP.PORT=" + JETTY_PORT + " STOP.KEY=magic --stop"
	};
	
	private Timer timer 			= null;
	private boolean _isPluginLoaded = false;
	
	public static Jetty _instance = null;
	public static Jetty instance()
	{
		if( _instance == null )
			_instance = new Jetty();
		return _instance;
	}
	
	public Jetty()
	{
		
	}
	
	@Override
	public void initPlugin()
	{
		if( Settings.isConnectedToInternet() )
		{
			JETTY_URL = RemoteUtils.getConfigEntry(RemoteUtils.JETTY_URL);
		}
		else
		{
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					if( Settings.isConnectedToInternet() )
					{
						JETTY_URL = RemoteUtils.getConfigEntry(RemoteUtils.JETTY_URL);
						System.out.println("JETTY_URL set to " + JETTY_URL);
					}
				}
			}, 1000, 5000);
		}
		JETTY_HOME = new File(Settings.DEPLOYED_PLUGINS_DIRECTORY, "/jetty/");
		JETTY_WEBAPPS = new File(JETTY_HOME, "webapps");
	}
	
	@Override
	public boolean isPluginLoaded()
	{
		return _isPluginLoaded;
	}
	
	@Override
	public void loadPlugin() 
	{
		if( PluginManager.instance().setContainer(_instance) )
			_isPluginLoaded = true;
		else
			JOptionPane.showMessageDialog(null, "There was an error loading the Jetty plugin.\nAnother plugin might already be loaded.", "Error", JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void unloadPlugin() 
	{
		PluginManager.instance().setContainer(null);
		_isPluginLoaded = false;
	}

	@Override
	public String getPluginName() {
		return PLUGIN_NAME;
	}
	
	@Override public String getURL() 					{ return JETTY_URL;  }
	@Override public void setURL(String s)				{ this.JETTY_URL = s; }
	@Override public File getHomeDirectory()			{ return JETTY_HOME; }
	@Override public void setHomeDirectory(File f) 		{ this.JETTY_HOME = f; }
			  public void setHomeDirectory(String s)	{ this.JETTY_HOME = new File(s); }
	@Override public File getWebappsDirectory() 		{ return JETTY_WEBAPPS; }
	@Override public void setWebappsDirectory(File f)	{ this.JETTY_WEBAPPS = f; }
			  public void setWebappsDirectory(String s) { this.JETTY_WEBAPPS = new File(s); }
	@Override public File getInstallFile() 				{ return JETTY_INSTALL_FILE; }
	@Override public void setInstallFile(File f)		{ this.JETTY_INSTALL_FILE = f; }
	@Override public int getPort() 						{ return JETTY_PORT; }
	@Override public void setPort(int i)				{ this.JETTY_PORT = i; }
	
	public void startServer()
	{
		try {
			ProcessUtils.runAndWait(START);
		} catch (InterruptedException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		}
	}
	
	public void stopServer()
	{
		try {
			ProcessUtils.runAndWait(STOP);
		} catch (InterruptedException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		}
	}
}
