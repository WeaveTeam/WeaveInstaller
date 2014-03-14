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

public class MySQL implements IPlugin 
{
	public String 	PLUGIN_NAME			= "MySQL";
	public String 	MYSQL_URL 			= null;
	public File 	MYSQL_HOME			= null;
	public File 	MYSQL_WEBAPPS		= null;
	public File 	MYSQL_INSTALL_FILE	= null;
	public int 		MYSQL_PORT 			= 3306;
	
	private Timer timer 				= null;
	private boolean _isPluginLoaded 	= false;
	
	public static MySQL _instance 		= null;
	public static MySQL instance()
	{
		if( _instance == null )
			_instance = new MySQL();
		return _instance;
	}
	
	public MySQL()
	{
		
	}

	@Override
	public String getPluginName() {
		return PLUGIN_NAME;
	}
	
	@Override
	public void initPlugin()
	{
		if( Settings.isConnectedToInternet() )
		{
			MYSQL_URL = getBestMySQLURL();
			MYSQL_INSTALL_FILE = new File(Settings.EXE_DIRECTORY, "/mysql_" + RemoteUtils.getLatestMySQLVersion()+".msi");
		}
		else
		{
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					if( Settings.isConnectedToInternet() )
					{
						MYSQL_URL = getBestMySQLURL();
						System.out.println("MYSQL_URL set to " + MYSQL_URL);
						MYSQL_INSTALL_FILE = new File(Settings.EXE_DIRECTORY, "/mysql_" + RemoteUtils.getLatestMySQLVersion()+".msi");
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
		if( PluginManager.instance().setDatabase(_instance) )
			_isPluginLoaded = true;
		else
			JOptionPane.showMessageDialog(null, "There was an error loading the MySQL plugin.\nAnother plugin is already loaded.", "Error", JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void unloadPlugin()
	{
		PluginManager.instance().setDatabase(null);
		_isPluginLoaded = false;
	}

	@Override public String getURL() 					{ return MYSQL_URL;  }
	@Override public File getHomeDirectory()			{ return MYSQL_HOME; }
	@Override public File getWebappsDirectory() 		{ return MYSQL_WEBAPPS; }
	@Override public File getInstallFile() 				{ return MYSQL_INSTALL_FILE; }
	@Override public int getPort() 						{ return MYSQL_PORT; }

	@Override public void setURL(String s)				{ this.MYSQL_URL = s; }
	@Override public void setHomeDirectory(File f) 		{ this.MYSQL_HOME = f; }
	@Override public void setWebappsDirectory(File f)	{ this.MYSQL_WEBAPPS = f; }
	@Override public void setInstallFile(File f)		{ this.MYSQL_INSTALL_FILE = f; }
	@Override public void setPort(int i)				{ this.MYSQL_PORT = i; }
	
	/** Pings all the MySQL mirrors and returns the one with the lowest ping.
	 * 
	 * @return string URL of mirror with lowest ping.
	 */
	public String getBestMySQLURL()
	{
		String url = RemoteUtils.getConfigEntry(RemoteUtils.MYSQL_URL);
		if( Settings.getLatency(url) >= 0 )
			return url;
			
		url = RemoteUtils.getConfigEntry(RemoteUtils.MYSQL_BACKUP_URL);
		if( Settings.getLatency(url) >= 0 )
			return url;
		
		return null;
	}
}
