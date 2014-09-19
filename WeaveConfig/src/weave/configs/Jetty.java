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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import weave.Settings;
import weave.async.AsyncTask;
import weave.managers.ConfigManager;
import weave.managers.IconManager;
import weave.utils.BugReportUtils;
import weave.utils.ObjectUtils;
import weave.utils.ProcessUtils;
import weave.utils.RemoteUtils;
import weave.utils.TraceUtils;
import weave.utils.TransferUtils;

public class Jetty extends Config
{
	public static final String NAME = "Jetty";
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
	}

	@Override public void initConfig()
	{
		super.initConfig();
		
		File thisPluginDir = new File(Settings.DEPLOYED_PLUGINS_DIRECTORY, CONFIG_NAME);
		Map<String, Object> savedCFG = ConfigManager.getConfigManager().getSavedConfigSettings(getConfigName());
		try {
			Class<?>[] argClasses = { Object.class };
			Object[] args = { "PORT" };
			
			setPort(Integer.parseInt((String)ObjectUtils.ternary(
					savedCFG, "get", "8084", argClasses, args)));
			
			setWebappsDirectory(new File(thisPluginDir, "webapps"));
			setURL(RemoteUtils.getConfigEntry(RemoteUtils.JETTY_URL));
			setDescription(getConfigName() + " is a free and open-source project as part of the Eclipse Foundation.");
			setWarning(	"<center><b>" + getConfigName() + " is a plugin that will run inside the tool and does not require external configuration.<br>" + 
						"This is the appropriate choice for new users.</b></center>");
			setImage(ImageIO.read(IconManager.IMAGE_JETTY));
			
			if( (Boolean)ObjectUtils.ternary(savedCFG, "get", false,
					new Class<?>[] { Object.class }, 
					new Object[] { "ACTIVE" }) )
				loadConfig();
			
		} catch (IOException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
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
	
	@Override public boolean loadConfig() 
	{
		boolean result = ConfigManager.getConfigManager().setContainer(_instance);
		if( result ) {
			startServer();
			super.loadConfig();
		}
		else
			JOptionPane.showMessageDialog(null, 
					"There was an error loading the " + getConfigName() + " plugin.\n" + 
					"Another plugin might already be loaded.", 
					"Error", JOptionPane.ERROR_MESSAGE);
		return result;
	}

	@Override public boolean unloadConfig() 
	{
		boolean result = ConfigManager.getConfigManager().setContainer(null);
		stopServer();
		super.unloadConfig();
		return result;
	}
	
	
	public void startServer()
	{
		AsyncTask task = new AsyncTask() {
			@Override
			public Object doInBackground() {
				Object o = TransferUtils.FAILED;
				try {
					String basePath = (String)ObjectUtils.ternary(getWebappsDirectory(), "getAbsolutePath", "") + "/../";
					final String[] START = {
							"cmd",
							"/c",
							"java -jar \"" + basePath + "start.jar\" jetty.base=\""+basePath+"\" jetty.port=" + PORT + " STOP.PORT=" + (PORT+1) + " STOP.KEY=jetty"
					};
					o = ProcessUtils.run(START);
				} catch (InterruptedException e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
					BugReportUtils.showBugReportDialog(e);
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
				} catch (IOException e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				}
				return o;
			}
		};
		task.execute();
	}
	
	public void stopServer()
	{
		try {
			String basePath = (String)ObjectUtils.ternary(getWebappsDirectory(), "getAbsolutePath", "") + "/../"; 
			String[] STOP = {
					"cmd",
					"/c",
					"java -jar \"" + basePath + "start.jar\" jetty.base=\"" + basePath + "\" STOP.PORT=" + (PORT+1) + " STOP.KEY=jetty --stop"
			};
			ProcessUtils.run(STOP);
		} catch (InterruptedException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
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
		} catch (IOException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		}
	}
}
