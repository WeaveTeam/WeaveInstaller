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

import weave.Settings;
import weave.utils.TraceUtils;

public class PluginManager
{
	public static PluginManager _instance = null;
	
	private IPluginManager container = null;
	private IPluginManager database = null;
	
	
	public static PluginManager instance()
	{
		if( _instance == null )
			_instance = new PluginManager();
		return _instance;
	}

	
	public void initializePlugins()
	{
		TraceUtils.trace(TraceUtils.STDOUT, "-> Initializing Plugins...........");

//		Jetty.instance().initPlugin();
//		Tomcat.instance().initPlugin();
//		MySQL.instance().initPlugin();

		TraceUtils.put(TraceUtils.STDOUT, "DONE");
	}

	public IPluginManager getContainer() 
	{
		return this.container;
	}
	public IPluginManager getDatabase()
	{
		return this.database;
	}
	public boolean setContainer(IPluginManager c) 
	{
		if( c == null )
		{
			container = null;
			Settings.ACTIVE_CONTAINER_PLUGIN = null;
			return true;
		}
		
		if( this.container == null ) 
		{
			this.container = c;
			
			Settings.ACTIVE_CONTAINER_PLUGIN = container;
			return true;
		}
		
		return false;
	}
	public boolean setDatabase(IPluginManager d)
	{
		if( d == null )
		{
			database = null;
			Settings.ACTIVE_DATABASE_PLUGIN = null;
			return true;
		}
		
		if( this.database == null ) 
		{
			this.database = d;
			Settings.ACTIVE_DATABASE_PLUGIN = database;
			return true;
		}
		
		return false;
	}
}
