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

package weave.configs;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import weave.managers.ConfigManager;
import weave.managers.IconManager;
import weave.utils.BugReportUtils;
import weave.utils.TraceUtils;

public class Tomcat extends Config
{
	public static String NAME		= "Tomcat";
	public static String URL		= "http://tomcat.apache.org/";
	public static Tomcat _instance 	= null;
	
	public static Tomcat getConfig()
	{
		if( _instance == null )
			_instance = new Tomcat();
		return _instance;
	}
	
	public Tomcat()
	{
		super(NAME, URL);
		
		try {
			setPort(8080);
			setTechLevel("Advanced");
			setDescription(	"Apache Tomcat is an open source web server and servlet container " +
							"that provides a pure Java HTTP web server environment for " +
							"Java code to run in.");
			setWarning("<center><b>" + getConfigName() + " requires the use of its external application " +
						"found <a href='" + getURL() + "'>here.</a></b></center>");
			setImage(ImageIO.read(IconManager.IMAGE_TOMCAT));
		} catch (IOException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		}
	}

	@Override public void loadConfig() 
	{
		if( ConfigManager.getConfigManager().setContainer(_instance) )
			super.loadConfig();
		else
			JOptionPane.showMessageDialog(null, 
					"There was an error loading the " + CONFIG_NAME + " plugin.\n" + 
					"Another plugin might already be loaded.", 
					"Error", JOptionPane.ERROR_MESSAGE);
	}

	@Override public void unloadConfig() 
	{
		ConfigManager.getConfigManager().setContainer(null);
		super.unloadConfig();
	}
}
