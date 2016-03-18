/*
    Weave (Web-based Analysis and Visualization Environment)
    Copyright (C) 2008-2015 University of Massachusetts Lowell

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

package weave.utils;

import static weave.utils.TraceUtils.STDERR;
import static weave.utils.TraceUtils.trace;

import java.io.IOException;

import javax.swing.JOptionPane;

import weave.Globals;
import weave.Settings;
import weave.async.AsyncCallback;
import weave.async.AsyncFunction;
import weave.core.Function;
import weave.reflect.Reflectable;

public class RemoteUtils extends Globals
{
	public static final String WEAVE_BINARIES_URL		= "WeaveBinariesURL";
	public static final String WEAVE_TAGS_URL			= "WeaveTagsURL";
	
	public static final String WEAVE_SERVER_INSTALL_URL	= "WeaveServerInstallURL";
	public static final String WEAVE_SERVER_UPDATES_URL	= "WeaveServerUpdatesURL";
	public static final String WEAVE_SERVER_VERSION 	= "WeaveServerVersion";
	public static final String WEAVE_UPDATER_VERSION 	= "WeaveUpdaterVersion";
	public static final String WEAVE_LAUNCHER_VERSION	= "WeaveLauncherVersion";

	public static final String JETTY_URL 				= "JettyURL";
	public static final String JETTY_VERSION 			= "JettyVersion";
	
	public static final String AWS_URL					= "AnalystURL";
	public static final String AWS_VERSION				= "AnalystVersion";
	
	private static long 	configTimestamp				= 0;
	private static String[] configFile					= null;
	
	private static String[] getConfigFile()
	{
		String fileContent = null;
		
		if( Settings.isOfflineMode() )
			return null;
			
		try {
			if( configTimestamp < (System.currentTimeMillis() / 1000L) )
			{
				fileContent = URLRequestUtils.getContentBody(Settings.UPDATE_CONFIG);
				configFile = fileContent.split(";");
				configTimestamp = (System.currentTimeMillis() / 1000L) + (60 * 60 * 6);
			}
		} catch (IOException e) {
			trace(STDERR, e);
		}
		return configFile;
	}
	
	@Reflectable
	public static String getConfigEntry(String key)
	{
		if( Settings.isOfflineMode() )
			return null;

		String[] configFile = getConfigFile();
		if( configFile == null )
			return null;
		
		for( String s : configFile )
			if( s.contains(key) )
				return s.substring(s.indexOf(":")+1).trim();
		
		BugReportUtils.autoSubmitBugReport(new Exception("Error: \"" + key + "\" does not exist in RemoteUtils"));
		return null;
	}
	
	@Reflectable
	public static String[] getRemoteFiles()
	{
		if( Settings.isOfflineMode() )
			return null;
		
		try {
			return URLRequestUtils.getContentBody(Settings.UPDATE_FILES).split(";");
		} catch (IOException e) {
			trace(STDERR, e);
			JOptionPane.showConfirmDialog(null, 
				"A connection to the internet could not be established.\n\n" +
				"Please connect to the internet and try again.", 
				"No Connection", 
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
		}
		return null;
	}
	
	/**
	 * Get the remote IP of the user's computer
	 * 
	 * @return 	The external IP of the user's or <code>null</code> if they are
	 * 			in offline mode or a connection timeout is reached.
	 */
	public static String getIP()
	{
		if( Settings.isOfflineMode() )
			return null;
		
		try {
			return URLRequestUtils.getContentBody(Settings.API_GET_IP);
		} catch (IOException e) {
			trace(STDERR, e);
		}
		
		return null;
	}
	
	/**
	 * Use the Remote API to access if a service is up from outside of the network.
	 * 
	 * @param host The host/IP of the service
	 * @param port The port number of the service
	 * 
	 * @return <code>true</code> if the service is up, <code>false</code> otherwise
	 */
	@Reflectable
	public static Boolean isServiceUp(String host, Integer port)
	{
		if( Settings.isOfflineMode() )
			return false;
		
		URLRequestParams params = new URLRequestParams();
		params.add("ip", host);
		params.add("port", port);
		
		try {
			return URLRequestUtils
						.request(URLRequestUtils.GET, Settings.API_SOCKET, params)
						.getResponseContent()
						.equals("1");

		} catch (IOException e) {
			trace(STDERR, e);
		}
		return false;
	}
	
	/**
	 * Check to see if an internet connection to the {@link Settings#IWEAVE_URL} exists.<br>
	 * This executes synchronously in the UI thread and returns the result back to the caller.
	 * 
	 * @return <code>true</code> if a connection can be established, <code>false</code> otherwise
	 */
	public static boolean isConnectedToInternet()
	{
		try {
			URLRequestUtils.request(URLRequestUtils.GET, Settings.IWEAVE_URL);
			return true;
		} catch (IOException e) {
			trace(STDERR, e);
		}
		return false;
	}

	/**
	 * Check to see if an internet connection to the {@link Settings#IWEAVE_URL} exists.<br>
	 * This uses a callback implementation in a multi-threaded environment to keep the execution of
	 * long running tasks off the UI thread.
	 * 
	 * <code><pre>
	 * RemoteUtils.isConnectedToInternet(
	 * 	new Function() {
	 * 		public void run() {
	 * 			// Connected to internet
	 * 		}
	 * 	},
	 * 	new Function() {
	 * 		public void run() {
	 * 			// Not connected to internet
	 * 		}
	 * 	}
	 * );
	 * </pre></code>
	 * 
	 * @param ifTrue A {@link Function} to run if a connection to the internet <b>can</b> be established
	 * @param ifFalse A {@link Function} to run if a connection to the internet <b>can not</b> be established
	 * 
	 * @see Function
	 * @see Settings#IWEAVE_URL
	 */
	public static void isConnectedToInternet(final Function<Object, Object> ifTrue, final Function<Object, Object> ifFalse)
	{
		AsyncCallback callback = new AsyncCallback() {
			@Override
			public void run(Object o) {
				Boolean result = (Boolean) o;
				
				if( result )	ifTrue.call();
				else			ifFalse.call();
			}
		};
		AsyncFunction task = new AsyncFunction() {
			@Override
			public Object doInBackground() {
				return isConnectedToInternet();
			}
		};
		task.addCallback(callback).call();
	}
}
