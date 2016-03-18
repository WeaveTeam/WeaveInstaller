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

package weave;

import static weave.utils.TraceUtils.STDERR;
import static weave.utils.TraceUtils.STDOUT;
import static weave.utils.TraceUtils.trace;
import static weave.utils.TraceUtils.traceln;
import static weave.utils.TraceUtils.LEVEL.DEBUG;

import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import weave.Settings.OS_ENUM;
import weave.configs.IConfig;
import weave.managers.ConfigManager;
import weave.utils.FileUtils;
import weave.utils.LaunchUtils;
import weave.utils.ReflectionUtils;
import weave.utils.StringUtils;

@SuppressWarnings("serial")
public class Launcher extends JFrame
{
	public static Launcher launcher = null;
	
//	private Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	
	public static void main( final String[] args )
	{
		if( !Desktop.isDesktopSupported() ) {
			System.out.println("!! Desktop functionality not supported.");
			System.exit(NORMAL);
		}
		
		Settings.CURRENT_PROGRAM_NAME = Settings.LAUNCHER_NAME;
		Settings.init();
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				launcher = new Launcher(args);
			}
		});
	}
	
	public Launcher(String[] args)
	{
		setSize(100, 50);
		setResizable(false);
		setLayout(null);
		setTitle(Settings.LAUNCHER_NAME);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocation(0, 0);
//		setLocation(screen.width/2 - getWidth()/2, screen.height/2 - getHeight()/2);
		setVisible(true);
		
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, getWidth(), getHeight());
		panel.setBackground(Color.BLACK);
		add(panel);

		String path = "";
		int delay = 0;
		
		setState(JFrame.ICONIFIED);

		try {
			if( args.length == 0 ) System.exit(NORMAL);
			if( args.length > 0 ) path = args[0];
			if( args.length > 1 ) delay = Integer.parseInt(args[1]);

			// Handle special cases first
			//
			// Examples:
			//		weave://reflect/weave/Settings/testAPIStr
			// 		weave://server/start
			//		weave://server/stop
			if( StringUtils.beginsWith(path, Settings.PROJECT_PROTOCOL) )
			{
				String[] params = path.substring(Settings.PROJECT_PROTOCOL.length()).split("/");
				String component = params[0];
				
				if( component.equals("reflect") )
				{
					ReflectionUtils.reflectMethod(params[1], params[2], params[3]);
				}
				else if( component.equals("server") ) 
				{
					String cmd = params[1];
					if( cmd.equals("start") )
						LaunchUtils.launchWeaveServer(delay);
					else if( cmd.equals("stop") )
						Settings.shutdown();
				} 
				else if( component.equals("updater") )
				{
					String cmd = params[1];
					if( cmd.equals("start") )
						LaunchUtils.launchWeaveUpdater(delay);
					else if( cmd.equals("stop") )
						Settings.shutdown();
				}
				else
				{
					throw new IllegalArgumentException("Invalid protocol component: " + component);
				}
			}
			
			
			// Examples:
			//
			//		C:\path\to\file\Obesity.weave
			//		/usr/var/path/to/file/saved_session.weave
			else if( StringUtils.endsWith(path, Settings.PROJECT_EXTENSION) )
			{
				ConfigManager.getConfigManager().initializeConfigs();
				IConfig cfg = ConfigManager.getConfigManager().getActiveContainer();

				if( cfg == null )
					System.exit(NORMAL);
				traceln(STDOUT, DEBUG, "cfg: " + cfg.getConfigName());
				
				File webapps = cfg.getWebappsDirectory();
				if( webapps == null )
					System.exit(NORMAL);
				traceln(STDOUT, DEBUG, "webapps: " + webapps.getAbsolutePath());
				
				File ROOT = new File(webapps, "ROOT");
				traceln(STDOUT, DEBUG, "ROOT: " + ROOT.getAbsolutePath());
				if( !ROOT.exists() )
					System.exit(NORMAL);
				
				File src = new File(path);
				File dest = new File(ROOT, src.getName());
				
				FileUtils.copy(src, dest, FileUtils.SINGLE_FILE | FileUtils.OVERWRITE);
				LaunchUtils.browse("http://" + 
						Settings.LOCALHOST + ":" +
						ConfigManager.getConfigManager().getActiveContainer().getPort() +
						"/weave.html?file=" + dest.getName(), 0);
			}
			else if( StringUtils.endsWith(path, ".jar") && Settings.OS == OS_ENUM.WINDOWS ) 
			{
				if( Settings.OS == OS_ENUM.WINDOWS )
				{
					traceln(STDOUT, DEBUG, StringUtils.rpad("Opening elevated: " + path, ".", Settings.LOG_PADDING_LENGTH));
					LaunchUtils.launchElevated(path, delay);
				}
				else
				{
					traceln(STDOUT, DEBUG, StringUtils.rpad("Opening: " + path, ".", Settings.LOG_PADDING_LENGTH));
					LaunchUtils.launch(path, delay);
				}
			}
			else
			{
				LaunchUtils.open(path, delay);
			}

		} catch (InterruptedException e) {
			trace(STDERR, e);
		} catch (IOException e) {
			trace(STDERR, e);
		} catch (Exception e) {
			trace(STDERR, e);
		}
		
		System.exit(NORMAL);
	}
}
