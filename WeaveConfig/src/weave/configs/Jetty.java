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

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import weave.Settings;
import weave.managers.ConfigManager;
import weave.managers.IconManager;
import weave.utils.BugReportUtils;
import weave.utils.ProcessUtils;
import weave.utils.RemoteUtils;
import weave.utils.TraceUtils;

public class Jetty extends Config
{
	public static String NAME		= "Jetty";
	public static Jetty _instance 	= null;
	
	public static Jetty getConfig()
	{
		if( _instance == null )
			_instance = new Jetty();
		return _instance;
	}
	
	public Jetty()
	{
		super(NAME);
		
		try {
			setPort(8080);
			setTechLevel("Easy");
			setDescription("Jetty is a free and open-source project as part of the Eclipse Foundation.");
			setWarning("<center><b>" + getConfigName() + " will run inside the tool and does not require an external application.</b></center>");
			setImage(ImageIO.read(IconManager.IMAGE_JETTY));
		} catch (IOException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		}
	}

	@Override public void initConfig()
	{
		File thisPluginDir = new File(Settings.DEPLOYED_PLUGINS_DIRECTORY, CONFIG_NAME);
		if( WEBAPPS == null || WEBAPPS.length() == 0 )
			setWebappsDirectory(new File(thisPluginDir, "webapps"));
			
		setURL(RemoteUtils.getConfigEntry(RemoteUtils.JETTY_URL));
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
	
	
	
	private String[] START = {
			"cmd",
			"/c",
			"java -jar start.jar STOP.PORT=" + PORT + " STOP.KEY=jetty --daemon &"
	};
	private String[] STOP = {
			"cmd",
			"/c",
			"java -jar start.jar STOP.PORT=" + PORT + " STOP.KEY=jetty --stop"
	};

	public void startServer()
	{
		try {
			ProcessUtils.runAndWait(START);
		} catch (InterruptedException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		}
	}
	
	public void stopServer()
	{
		try {
			ProcessUtils.runAndWait(STOP);
		} catch (InterruptedException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		}
	}
}
