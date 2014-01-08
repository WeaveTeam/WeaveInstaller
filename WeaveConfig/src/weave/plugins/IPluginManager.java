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

public interface IPluginManager 
{
	public void initPlugin();
	public void loadPlugin();
	public void unloadPlugin();
	public boolean isPluginLoaded();
	
	public String getPluginName();
	
	public String getURL();
	public void setURL(String s);
	
	public File getHomeDirectory();
	public void setHomeDirectory(File f);
	
	public File getWebappsDirectory();
	public void setWebappsDirectory(File f);
	
	public File getInstallFile();
	public void setInstallFile(File f);
	
	public int getPort();
	public void setPort(int i);
}
