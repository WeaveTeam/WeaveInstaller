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

package weave.managers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weave.Settings;
import weave.configs.IConfig;
import weave.configs.Jetty;
import weave.configs.MySQL;
import weave.configs.SQLite;
import weave.configs.Tomcat;
import weave.utils.BugReportUtils;
import weave.utils.TraceUtils;

public class ConfigManager
{
	public static final String SERVLET = "SERVLET";
	public static final String DATABASE = "DATABASE";
	
	private static IConfig ACTIVE_CONTAINER_PLUGIN 	= null;
	private static IConfig ACTIVE_DATABASE_PLUGIN 	= null;
	
	private ArrayList<Map<String, IConfig>> availableConfigs = null;
	public static Map<String, Map<String, String>> CONFIGS_MAP	= null;
	

	/////////////////////////////////////////////////////////////////////////////////////
	
	public static ConfigManager _instance = null;
	public static ConfigManager getConfigManager()
	{
		if( _instance == null )
			_instance = new ConfigManager();
		return _instance;
	}

	public ConfigManager()
	{
		availableConfigs = new ArrayList<Map<String,IConfig>>();
		
		Map<String, IConfig> map = null;
		
		for( int i = 1; i <= 4; i++ )
		{
			map = new HashMap<String, IConfig>();
			switch( i ) {
				case 1: map.put(SERVLET, Jetty.getConfig());	break;
				case 2: map.put(SERVLET, Tomcat.getConfig()); 	break;
				case 3: map.put(DATABASE, SQLite.getConfig()); 	break;
				case 4: map.put(DATABASE, MySQL.getConfig());	break;
			}
			availableConfigs.add(map);
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	public void initializeConfigs()
	{
		load();
		
		for( int i = 0; i < availableConfigs.size(); ++i )
		{
			for( Map.Entry<String, IConfig> entry : availableConfigs.get(i).entrySet() )
			{
				IConfig config = entry.getValue();
				config.initConfig();
			}
		}

		save();
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	public ArrayList<Map<String, IConfig>> getConfigs()
	{
		return availableConfigs;
	}
	
	public IConfig getConfigByName(Object o)
	{
		return getConfigByName((String) o);
	}
	
	public IConfig getConfigByName(String name)
	{
		for( int i = 0; i < availableConfigs.size(); ++i )
			for( Map.Entry<String, IConfig> entry : availableConfigs.get(i).entrySet() )
				if( entry.getValue().getConfigName().equals(name) )
					return entry.getValue();
		return null;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	private List<IConfig> getSpecificConfig(String type)
	{
		List<IConfig> list = new ArrayList<IConfig>();
		
		for( int i = 0; i < availableConfigs.size(); ++i ) 
		{
			for( Map.Entry<String, IConfig> entry : availableConfigs.get(i).entrySet() )
			{
				String key = entry.getKey();
				IConfig config = entry.getValue();
				
				if( key.equals(type) )
					list.add(config);
			}
		}
		
		return list;
	}
	public List<IConfig> getServletConfigs()
	{
		return getSpecificConfig(SERVLET);
	}
	public List<IConfig> getDatabaseConfigs()
	{
		return getSpecificConfig(DATABASE);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	public IConfig getContainer() 
	{
		return ACTIVE_CONTAINER_PLUGIN;
	}
	public IConfig getDatabase()
	{
		return ACTIVE_DATABASE_PLUGIN;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	public boolean setContainer(IConfig c) 
	{
		if( c == null )
		{
			ACTIVE_CONTAINER_PLUGIN = null;
			TraceUtils.traceln(TraceUtils.STDOUT, "-> Config Container set to........NULL");
			return true;
		}
		
		if( ACTIVE_CONTAINER_PLUGIN == null ) 
		{
			ACTIVE_CONTAINER_PLUGIN = c;
			TraceUtils.traceln(TraceUtils.STDOUT, "-> Config Container set to........" + c.getConfigName());
			return true;
		}
		
		return false;
	}
	public boolean setDatabase(IConfig d)
	{
		if( d == null )
		{
			ACTIVE_DATABASE_PLUGIN = null;
			TraceUtils.traceln(TraceUtils.STDOUT, "-> Config Database set to.........NULL");
			return true;
		}
		
		if( ACTIVE_DATABASE_PLUGIN == null ) 
		{
			ACTIVE_DATABASE_PLUGIN = d;
			TraceUtils.traceln(TraceUtils.STDOUT, "-> Config Database set to........." + d.getConfigName());
			return true;
		}
		
		return false;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	public boolean save()
	{
		try {
			TraceUtils.traceln(TraceUtils.STDOUT, "-> Saving config file.............");
			
			if( !Settings.configsFileExists() )
				Settings.CONFIG_FILE.createNewFile();
			
			CONFIGS_MAP = new HashMap<String, Map<String, String>>();
			
			for( int i = 0; i < availableConfigs.size(); ++i )
			{
				for( Map.Entry<String, IConfig> entry : availableConfigs.get(i).entrySet() )
				{
					IConfig config = entry.getValue();
					Map<String, String> values = new HashMap<String, String>();
					
					values.put("WEBAPPS", 	( config.getWebappsDirectory() == null )? null : config.getWebappsDirectory().getCanonicalPath());
					values.put("PORT",		"" + config.getPort());
					
					CONFIGS_MAP.put(config.getConfigName(), values);
				}
			}

			ObjectOutputStream outstream = new ObjectOutputStream(new FileOutputStream(Settings.CONFIG_FILE));
			outstream.writeObject(CONFIGS_MAP);
			outstream.close();
			TraceUtils.put(TraceUtils.STDOUT, "DONE");
		} catch (IOException e) {
			TraceUtils.put(TraceUtils.STDOUT, "FAILED");
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public boolean load()
	{
		if( !Settings.configsFileExists() )
			return false;
		
		try {
			TraceUtils.traceln(TraceUtils.STDOUT, "-> Loading config file............");

			ObjectInputStream instream = new ObjectInputStream(new FileInputStream(Settings.CONFIG_FILE));
			CONFIGS_MAP = (Map<String, Map<String, String>>) instream.readObject();
			instream.close();
			
			for( int i = 0; i < availableConfigs.size(); ++i )
			{
				for( Map.Entry<String, IConfig> entry : availableConfigs.get(i).entrySet() )
				{
					IConfig config = entry.getValue();
					Map<String, String> values = CONFIGS_MAP.get(config.getConfigName());
					
					if( values == null )
					{
						config.setWebappsDirectory((String)null);
						config.setPort(null);
					}
					else
					{
						config.setWebappsDirectory( (String) values.get("WEBAPPS") );
						config.setPort( 			(String) values.get("PORT")	);
					}
				}
			}
			
		} catch (IOException e) {
			TraceUtils.put(TraceUtils.STDOUT, "FAILED");
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
			return false;
		} catch (ClassNotFoundException e) {
			TraceUtils.put(TraceUtils.STDOUT, "FAILED");
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
			return false;
		}
		TraceUtils.put(TraceUtils.STDOUT, "DONE");
		return true;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
}