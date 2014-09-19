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

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

import weave.utils.ObjectUtils;
import weave.utils.TraceUtils;

public class Config implements IConfig
{
	protected String 	CONFIG_NAME 	= "";

	protected String 	URL 			= null;
	protected File 		WEBAPPS 		= null;
	protected File 		INSTALL_FILE 	= null;
	protected int 		PORT 			= 0;
	protected boolean 	LOADED			= false;

	protected BufferedImage	_icon			= null;
	protected String 		_description	= "";
	protected String		_warning		= "";
	
	public Config() {
		
	}
	
	public Config(String name) {
		CONFIG_NAME = name;
	}
	
	public Config(String name, String url) {
		CONFIG_NAME = name;
		URL = url;
	}
	
	@Override
	public void initConfig() {

	}

	@Override
	public boolean loadConfig() {
		LOADED = true;
		return true;
	}

	@Override
	public boolean unloadConfig() {
		LOADED = false;
		return true;
	}

	@Override public String getConfigName() 			{ return 	CONFIG_NAME; }
	@Override public String getURL() 					{ return 	URL; }
	@Override public File getWebappsDirectory() 		{ return 	WEBAPPS; }
	@Override public File getInstallFile() 				{ return 	INSTALL_FILE; }
	@Override public int getPort() 						{ return 	PORT; }
	@Override public boolean isConfigLoaded() 			{ return 	LOADED; }
	@Override public void setURL(String s) 				{ 			URL = s; }
	@Override public void setWebappsDirectory(File f) 	{ 			WEBAPPS = f; }
	@Override public void setInstallFile(File f) 		{ 			INSTALL_FILE = f; }
	@Override public void setPort(int i) 				{ 			PORT = i; }

	@Override public String getDescription() 			{ return 	_description; }
	@Override public String getWarning()				{ return	_warning; }
	@Override public BufferedImage getImage() 			{ return 	_icon; }
	@Override public void setDescription(String s) 		{			_description = s; }
	@Override public void setWarning(String s)			{			_warning = s; }
	@Override public void setImage(BufferedImage i) 	{ 			_icon = i; }

	@Override public void setWebappsDirectory(String s) {
		if( s == null || s.length() == 0 )
			setWebappsDirectory((File)null);
		else
			setWebappsDirectory(new File(s));
	}

	@Override public void setPort(String s) {
		int i = 0;
		
		try {
			i = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
		}
		setPort(i);
	}
	
	@Override public String toString() {
		String ret = "\nIConfig: " + getConfigName() + "\n";
		try {
			ret += ("\tWebapps: " + (String)ObjectUtils.ternary(getWebappsDirectory(), "getAbsolutePath", "Not Set") + "\n");
			ret += ("\tPort: " + getPort() + "\n");
			ret += ("\tLoaded: " + ( isConfigLoaded() ? "TRUE" : "FALSE") + "\n");
		} catch (NoSuchMethodException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
		} catch (SecurityException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
		} catch (IllegalAccessException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
		} catch (IllegalArgumentException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
		} catch (InvocationTargetException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
		}
		return ret;
	}
}
