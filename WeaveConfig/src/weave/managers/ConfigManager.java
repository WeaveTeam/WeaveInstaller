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

import static weave.utils.TraceUtils.STDERR;
import static weave.utils.TraceUtils.STDOUT;
import static weave.utils.TraceUtils.getSimpleClassAndMsg;
import static weave.utils.TraceUtils.put;
import static weave.utils.TraceUtils.trace;
import static weave.utils.TraceUtils.traceln;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weave.Globals;
import weave.Settings;
import weave.configs.Config;
import weave.configs.IConfig;
import weave.configs.JettyConfig;
import weave.configs.MySQLConfig;
import weave.configs.OracleConfig;
import weave.configs.PostgreSQLConfig;
import weave.configs.SQLiteConfig;
import weave.configs.TomcatConfig;
import weave.utils.BugReportUtils;
import weave.utils.ObjectUtils;
import weave.utils.StringUtils;

public class ConfigManager extends Globals
{
	public static final String SERVLET = "SERVLET";
	public static final String DATABASE = "DATABASE";
	
	private static IConfig ACTIVE_CONTAINER_PLUGIN 	= null;
	private static IConfig ACTIVE_DATABASE_PLUGIN 	= null;
	
	private ArrayList<Map<String, IConfig>> availableConfigs = null;
	private Map<String, Map<String, Object>> CONFIGS_MAP	= null;
	

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
		
		for( int i = 1; i <= 6; i++ )
		{
			map = new HashMap<String, IConfig>();
			switch( i ) {
				case 1: map.put(SERVLET, JettyConfig.getConfig());			break;
				case 2: map.put(SERVLET, TomcatConfig.getConfig()); 		break;
				case 3: map.put(DATABASE, SQLiteConfig.getConfig()); 		break;
				case 4: map.put(DATABASE, MySQLConfig.getConfig());			break;
				case 5: map.put(DATABASE, PostgreSQLConfig.getConfig());	break;
				case 6: map.put(DATABASE, OracleConfig.getConfig());		break;
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
	
	public Map<String, Object> getSavedConfigSettings(String name)
	{
		if( CONFIGS_MAP == null )
			return null;
		
		return CONFIGS_MAP.get(name);
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
	
	public IConfig getActiveContainer() 
	{
		return ACTIVE_CONTAINER_PLUGIN;
	}
	public IConfig getActiveDatabase()
	{
		return ACTIVE_DATABASE_PLUGIN;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	public boolean unloadAllConfigs()
	{
		for( int i = 0; i < availableConfigs.size(); i++ )
		{
			for( Map.Entry<String, IConfig> entry : availableConfigs.get(i).entrySet() )
			{
				IConfig config = entry.getValue();
				if( config.isConfigLoaded() )
					config.unloadConfig();
			}
		}
		return true;
	}
	
	public boolean setContainer(IConfig c) 
	{
		if( c == null )
		{
			traceln(STDOUT, StringUtils.rpad("-> Unloading Config Container", ".", Settings.LOG_PADDING_LENGTH) + ACTIVE_CONTAINER_PLUGIN.getConfigName());
			ACTIVE_CONTAINER_PLUGIN = null;
			return true;
		}
		
		if( ACTIVE_CONTAINER_PLUGIN == null ) 
		{
			traceln(STDOUT, StringUtils.rpad("-> Loading Config Container", ".", Settings.LOG_PADDING_LENGTH) + c.getConfigName());
			ACTIVE_CONTAINER_PLUGIN = c;
			return true;
		}
		
		return false;
	}
	public boolean setDatabase(IConfig d)
	{
		if( d == null )
		{
			traceln(STDOUT, StringUtils.rpad("-> Unloading Database Container", ".", Settings.LOG_PADDING_LENGTH) + ACTIVE_DATABASE_PLUGIN.getConfigName());
			ACTIVE_DATABASE_PLUGIN = null;
			return true;
		}
		
		if( ACTIVE_DATABASE_PLUGIN == null ) 
		{
			traceln(STDOUT, StringUtils.rpad("-> Loading Database Container", ".", Settings.LOG_PADDING_LENGTH) + d.getConfigName());
			ACTIVE_DATABASE_PLUGIN = d;
			return true;
		}
		
		return false;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	public boolean save()
	{
		try {
			traceln(STDOUT, StringUtils.rpad("-> Saving config file", ".", Settings.LOG_PADDING_LENGTH));
			
			if( !Settings.configsFileExists() )
				Settings.CONFIG_FILE.createNewFile();
			
			CONFIGS_MAP = new HashMap<String, Map<String, Object>>();
			
			for( int i = 0; i < availableConfigs.size(); ++i )
			{
				for( Map.Entry<String, IConfig> entry : availableConfigs.get(i).entrySet() )
				{
					IConfig config = entry.getValue();
					Map<String, Object> values = new HashMap<String, Object>();
					
					values.put(Config.WEBAPPS, 	ObjectUtils.ternary(config.getWebappsDirectory(), "getCanonicalPath", null));
					values.put(Config.HOST, 	config.getHost());
					values.put(Config.PORT,		"" + config.getPort());
					values.put(Config.VERSION, 	config.getInstallVersion());
					values.put(Config.ACTIVE, 	config.isConfigLoaded());
					
					CONFIGS_MAP.put(config.getConfigName(), values);
				}
			}

			ObjectOutputStream outstream = new ObjectOutputStream(new FileOutputStream(Settings.CONFIG_FILE));
			outstream.writeObject(CONFIGS_MAP);
			outstream.close();
		} catch (IOException e) {
			put(STDOUT, "FAILED (" + getSimpleClassAndMsg(e) + ")");
			trace(STDERR, e);
			BugReportUtils.showBugReportDialog(e);
			return false;
		} catch (NoSuchMethodException e) {
			put(STDOUT, "FAILED (" + getSimpleClassAndMsg(e) + ")");
			trace(STDERR, e);
			BugReportUtils.showBugReportDialog(e);
			return false;
		} catch (SecurityException e) {
			put(STDOUT, "FAILED (" + getSimpleClassAndMsg(e) + ")");
			trace(STDERR, e);
			BugReportUtils.showBugReportDialog(e);
			return false;
		} catch (IllegalAccessException e) {
			put(STDOUT, "FAILED (" + getSimpleClassAndMsg(e) + ")");
			trace(STDERR, e);
			BugReportUtils.showBugReportDialog(e);
			return false;
		} catch (IllegalArgumentException e) {
			put(STDOUT, "FAILED (" + getSimpleClassAndMsg(e) + ")");
			trace(STDERR, e);
			BugReportUtils.showBugReportDialog(e);
			return false;
		} catch (InvocationTargetException e) {
			put(STDOUT, "FAILED (" + getSimpleClassAndMsg(e) + ")");
			trace(STDERR, e);
			BugReportUtils.showBugReportDialog(e);
			return false;
		}
		put(STDOUT, "DONE");
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public boolean load()
	{
		if( !Settings.configsFileExists() )
			return false;
		
		try {
			traceln(STDOUT, StringUtils.rpad("-> Loading config file", ".", Settings.LOG_PADDING_LENGTH));

			ObjectInputStream instream = new ObjectInputStream(new FileInputStream(Settings.CONFIG_FILE));
			CONFIGS_MAP = (Map<String, Map<String, Object>>) instream.readObject();
			instream.close();
			
		} catch (IOException e) {
			put(STDOUT, "FAILED (" + getSimpleClassAndMsg(e) + ")");
			trace(STDERR, e);
			BugReportUtils.showBugReportDialog(e);
			return false;
		} catch (ClassNotFoundException e) {
			put(STDOUT, "FAILED (" + getSimpleClassAndMsg(e) + ")");
			trace(STDERR, e);
			BugReportUtils.showBugReportDialog(e);
			return false;
		}
		put(STDOUT, "DONE");
		return true;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
}
