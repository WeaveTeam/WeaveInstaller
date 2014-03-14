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
import weave.utils.RemoteUtils;

public class Tomcat implements IPlugin 
{
	public String 	PLUGIN_NAME			= "Tomcat";
	public String 	TOMCAT_URL 			= null;
	public File 	TOMCAT_HOME			= null;
	public File 	TOMCAT_WEBAPPS		= null;
	public File 	TOMCAT_INSTALL_FILE	= null;
	public int 		TOMCAT_PORT 		= 8080;
	
	private Timer timer					= null;
	private boolean _isPluginLoaded		= false;
	
	public static Tomcat _instance = null;
	public static Tomcat instance()
	{
		if( _instance == null )
			_instance = new Tomcat();
		return _instance;
	}
	
	public Tomcat()
	{
		
	}
	
	@Override
	public void initPlugin()
	{
		if( Settings.isConnectedToInternet() )
		{
			TOMCAT_URL = getBestTomcatURL();
			TOMCAT_INSTALL_FILE = new File(Settings.EXE_DIRECTORY, "/tomcat_" + RemoteUtils.getLatestTomcatVersion()+".exe");
		}
		else
		{
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					if( Settings.isConnectedToInternet() )
					{
						TOMCAT_URL = getBestTomcatURL();
						System.out.println("TOMCAT_URL set to " + TOMCAT_URL);
						TOMCAT_INSTALL_FILE = new File(Settings.EXE_DIRECTORY, "/tomcat_" + RemoteUtils.getLatestTomcatVersion()+".exe");
					}
				}
			}, 1000, 5000);
		}
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
			JOptionPane.showMessageDialog(null, "There was an error loading the Tomcat plugin.\nAnother plugin is already loaded.", "Error", JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void unloadPlugin() 
	{
		PluginManager.instance().setContainer(null);
		_isPluginLoaded = false;
	}
	
	@Override public String getURL() 					{ return TOMCAT_URL;  }
	@Override public void setURL(String s)				{ this.TOMCAT_URL = s; }
	@Override public File getHomeDirectory()			{ return TOMCAT_HOME; }
	@Override public void setHomeDirectory(File f) 		{ this.TOMCAT_HOME = f; }
			  public void setHomeDirectory(String s) 	{ this.TOMCAT_HOME = new File(s); }
	@Override public File getWebappsDirectory() 		{ return TOMCAT_WEBAPPS; }
	@Override public void setWebappsDirectory(File f)	{ this.TOMCAT_WEBAPPS = f; }
			  public void setWebappsDirectory(String s)	{ this.TOMCAT_WEBAPPS = new File(s); }
	@Override public File getInstallFile() 				{ return TOMCAT_INSTALL_FILE; }
	@Override public void setInstallFile(File f)		{ this.TOMCAT_INSTALL_FILE = f; }
	@Override public int getPort() 						{ return TOMCAT_PORT; }
	@Override public void setPort(int i)				{ this.TOMCAT_PORT = i; }
	
	/** Pings all tomcat mirrors and returns the one with the lowest ping.
	 * 
	 * @return	string URL of mirror with lowest ping.
	 */
	public String getBestTomcatURL()
	{
		String url = RemoteUtils.getLatestTomcatURL();
		if( Settings.getLatency(url) >= 0 )
			return url;
		
		url = RemoteUtils.getLatestTomcatBackupURL();
		if( Settings.getLatency(url) >= 0 )
			return url;
		
		return null;
	}

	@Override
	public String getPluginName() {
		// TODO Auto-generated method stub
		return null;
	}

}
