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

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import weave.managers.ConfigManager;
import weave.managers.IconManager;
import weave.utils.BugReportUtils;
import weave.utils.TraceUtils;

public class TomcatConfig extends Config
{
	public static final String NAME = "Tomcat";
	public static final String HOMEPAGE = "http://tomcat.apache.org/";
	public static final String URL = HOMEPAGE;
	public static final String HOST = "localhost";
	public static final int PORT = 8080;
	
	public static TomcatConfig _instance = null;
	public static TomcatConfig getConfig()
	{
		if( _instance == null )
			_instance = new TomcatConfig();
		return _instance;
	}
	
	public TomcatConfig()
	{
		super(NAME, HOMEPAGE, URL, HOST, PORT);
	}
	
	@Override public void initConfig()
	{
		super.initConfig(_WEBAPPS | _HOST | _PORT | _VERSION);
		
		try {
			
			setDescription(	"Apache Tomcat is an open source web server and servlet container " +
							"that provides a pure Java HTTP web server environment for " +
							"Java code to run in.");
			setWarning("<center><b>" + getConfigName() + " requires the use of its external application " +
						"found <a href='" + getDownloadURL() + "'>here.</a></b></center>");
			setImage(ImageIO.read(IconManager.IMAGE_TOMCAT));

		} catch (IOException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		}
	}

	@Override public boolean loadConfig() 
	{
		boolean result = ConfigManager.getConfigManager().setContainer(_instance); 
		if( result )
			super.loadConfig();
		else
			JOptionPane.showMessageDialog(null, 
					"There was an error loading the " + getConfigName() + " config.\n" + 
					"Another config might already be loaded.", 
					"Error", JOptionPane.ERROR_MESSAGE);
		return result;
	}

	@Override public boolean unloadConfig() 
	{
		boolean result = ConfigManager.getConfigManager().setContainer(null);
		super.unloadConfig();
		return result;
	}
}
