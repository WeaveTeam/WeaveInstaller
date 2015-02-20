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

import java.util.ArrayList;

import weave.Globals;
import weave.plugins.AnalystWorkstationPlugin;
import weave.plugins.IPlugin;
import weave.plugins.JettyPlugin;

public class PluginManager extends Globals
{
	private ArrayList<IPlugin> availablePlugins = null;
	
	private static PluginManager _instance = null;
	public static PluginManager getPluginManager()
	{
		if( _instance == null )
			_instance = new PluginManager();
		return _instance;
	}
	
	public PluginManager()
	{
		availablePlugins = new ArrayList<IPlugin>();
		
		availablePlugins.add(AnalystWorkstationPlugin.getPlugin());
		availablePlugins.add(JettyPlugin.getPlugin());
	}
	
	public ArrayList<IPlugin> getPlugins()
	{
		return availablePlugins;
	}
	
	public IPlugin getPluginByName(String name)
	{
		IPlugin p = null;
		for( int i = 0; i < availablePlugins.size(); ++i )
			if( (p = availablePlugins.get(i)).getPluginName().equals(name) )
				return p;
		return null;
	}
}
