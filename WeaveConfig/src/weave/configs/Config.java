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
import java.util.Map;

import javax.swing.JOptionPane;

import weave.Globals;
import weave.managers.ConfigManager;
import weave.reflect.Reflectable;
import weave.utils.BugReportUtils;
import weave.utils.ObjectUtils;
import weave.utils.TraceUtils;

public class Config extends Globals implements IConfig
{
	public static final String WEBAPPS 		= "WEBAPPS";
	public static final String HOST			= "HOST";
	public static final String PORT			= "PORT";
	public static final String VERSION		= "VERSION";
	public static final String ACTIVE		= "ACTIVE";
	
	public static final int _WEBAPPS		= ( 1 << 1 );
	public static final int _HOST			= ( 1 << 2 );
	public static final int _PORT 			= ( 1 << 3 );
	public static final int _VERSION		= ( 1 << 4 );
	public static final int _ACTIVE			= ( 1 << 5 );
	
	protected String 	CONFIG_NAME 	= "";

	protected String	_homepage		= null;
	protected String 	_downloadURL 	= null;
	protected File 		_webapps 		= null;
	protected File 		_install_file 	= null;
	protected String	_version		= null;
	protected String 	_host			= "";
	protected int 		_port 			= 0;
	protected boolean 	_loaded			= false;

	protected BufferedImage	_icon			= null;
	protected String 		_description	= "";
	protected String		_warning		= "";
	
	public Config() {
		
	}
	
	public Config(String name) {
		CONFIG_NAME = name;
	}
	
	public Config(String name, String host, int port) {
		CONFIG_NAME = name;
		_host = host;
		_port = port;
	}
	
	public Config(String name, String homepage, String url, String host, int port) {
		CONFIG_NAME = name;
		_homepage = homepage;
		_downloadURL = url;
		_host = host;
		_port = port;
	}
	
	@Override public void initConfig() {
		
	}
	
	@Override public void initConfig(int i) {
		Map<String, Object> savedCFG = ConfigManager.getConfigManager().getSavedConfigSettings(getConfigName());
		
		Class<?>[] argClasses 	= { Object.class };
		Object[] argsWebapps 	= { WEBAPPS };
		Object[] argsHost		= { HOST };
		Object[] argsPort 		= { PORT };
		Object[] argsVersion 	= { VERSION };
		Object[] argsActive 	= { ACTIVE };
		
		try {
			if( (i & _WEBAPPS) != 0 ) {
				String d = "";
				String s = (String)ObjectUtils.ternary(savedCFG, "get", d, argClasses, argsWebapps);
				setWebappsDirectory((s == null) ? d : s);
			}

			if( (i & _HOST) != 0 ) {
				String d = "localhost";
				String s = (String)ObjectUtils.ternary(savedCFG, "get", d, argClasses, argsHost); 
				setHost((s == null) ? d : s);
			}
			
			if( (i & _PORT) != 0 ) {
				String d = ""+_port;
				String s = (String)ObjectUtils.ternary(savedCFG, "get", d, argClasses, argsPort);
				setPort((s == null) ? d : s);
			}

			if( (i & _VERSION) != 0 ) {
				String d = "";
				String s = (String)ObjectUtils.ternary(savedCFG, "get", "", argClasses, argsVersion);
				setInstallVersion((s == null) ? d : s);
			}
			
			Boolean d = false;
			Boolean b = (Boolean)ObjectUtils.ternary(savedCFG, "get", d, argClasses, argsActive); 
			if( (b == null) ? d : b )
				loadConfig();
				
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
		}
	}

	@Override public boolean loadConfig() {
		_loaded = true;
		return true;
	}

	@Override public boolean unloadConfig() {
		_loaded = false;
		return true;
	}

	@Reflectable
	@Override public String getConfigName() 			{ return 	CONFIG_NAME; }
	@Reflectable
	@Override public String getHomepageURL()			{ return 	_homepage; }
	@Reflectable
	@Override public String getDownloadURL() 			{ return 	_downloadURL; }
	@Reflectable
	@Override public File getWebappsDirectory() 		{ return 	_webapps; }
	@Reflectable
	@Override public File getInstallFile() 				{ return 	_install_file; }
	
	@Reflectable
	@Override public String getInstallVersion()			{ return 	_version; }
	@Reflectable
	@Override public String getHost()					{ return	_host; }
	@Reflectable
	@Override public int getPort() 						{ return 	_port; }
	@Reflectable
	@Override public boolean isConfigLoaded() 			{ return 	_loaded; }
	@Override public void setHomepageURL(String s)		{			_homepage = s; }
	@Override public void setDownloadURL(String s) 		{ 			_downloadURL = s; }
	@Override public void setWebappsDirectory(File f) 	{ 			_webapps = f; }
	@Override public void setInstallFile(File f) 		{ 			_install_file = f; }
	@Override public void setInstallVersion(String s)	{			_version = s; }
	@Override public void setHost(String h)				{			_host = h; }
	@Override public void setPort(int i) 				{ 			_port = i; }

	@Reflectable
	@Override public String getDescription() 			{ return 	_description; }
	@Reflectable
	@Override public String getWarning()				{ return	_warning; }
	@Reflectable
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
			JOptionPane.showMessageDialog(null, 
					"Error parsing port.\n\n" +
					"\"" + s + "\" is not an integer.", "Parse Error", JOptionPane.ERROR_MESSAGE);
		}
		setPort(i);
	}
	
	@Override public String toString() {
		String ret = "\nIConfig: " + getConfigName() + "\n";
		try {
			ret += ("\tWebapps: " + (String)ObjectUtils.ternary(getWebappsDirectory(), "getAbsolutePath", "Not Set") + "\n");
			ret += ("\tHost: " + getHost() + "\n");
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
