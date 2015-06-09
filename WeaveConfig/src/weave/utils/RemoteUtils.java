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

package weave.utils;

import static weave.utils.TraceUtils.STDERR;
import static weave.utils.TraceUtils.trace;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.JOptionPane;

import weave.Function;
import weave.Globals;
import weave.Settings;
import weave.async.AsyncCallback;
import weave.async.AsyncTask;
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
	
	public static final String SHORTCUT_VER				= "WeaveShortcutVersion";
	
	private static long 	configTimestamp				= 0;
	private static String[] configFile					= null;
	
	private static String[] getConfigFile()
	{
		URLRequestResult result = null;
		
		if( Settings.isOfflineMode() )
			return null;
			
		try {
			if( configTimestamp < (System.currentTimeMillis() / 1000L) )
			{
				result = URLRequestUtils.request(URLRequestUtils.GET, Settings.UPDATE_CONFIG);
				configFile = result.getResponseContent().split(";");
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

		for( String s : getConfigFile() )
			if( s.contains(key) )
				return s.substring(s.indexOf(":")+1).trim();
		
		BugReportUtils.autoSubmitBugReport(new Exception("Error: \"" + key + "\" does not exist in RemoteUtils"));
		return null;
	}
	
	@Reflectable
	public static String[] getRemoteFiles()
	{
		URLRequestResult result = null;
		
		if( Settings.isOfflineMode() )
			return null;
		
		try {
			result = URLRequestUtils.request(URLRequestUtils.GET, Settings.UPDATE_FILES);
			return result.getResponseContent().split(";");
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
			return URLRequestUtils.request(URLRequestUtils.GET, Settings.API_GET_IP).getResponseContent();
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
	public static boolean isServiceUp(String host, int port)
	{
		if( Settings.isOfflineMode() )
			return false;
		
		URLRequestParams params = new URLRequestParams();
		params.add("ip", host);
		params.add("port", "" + port);
		
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
	 * Determine if an Internet connection can be established
	 * 
	 * @return TRUE if there is an Internet connection, FALSE otherwise
	 */
	public static Boolean isConnectedToInternet()
	{
		class ITC {
			boolean isConnected = false;
		}
		final ITC itc = new ITC();
		
//		trace(STDOUT, "-> Checking Internet Connection...");
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				URL url 				= null;
				HttpURLConnection conn 	= null;
				try {
					url = new URL(Settings.IWEAVE_URL);
					conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					conn.setReadTimeout(1500);
					conn.setUseCaches(false);
					conn.connect();
				} catch (ConnectException e) {
					// Don't trace error here
					itc.isConnected = false;
				} catch (IOException e) {
					trace(STDERR, e);
					itc.isConnected = false;
				} finally {
					if( conn != null )
						conn.disconnect();
				}
				itc.isConnected = true;
			}
		});
		try {
			t.start();
			t.join(2000);
			t.interrupt();
			t = null;
		} catch (InterruptedException e) {
			trace(STDERR, e);
		}
		
		Settings.isConnectedToInternet = itc.isConnected;
		
//		if( itc.isConnected )
//			put(STDOUT, "CONNECTED");
//		else
//			put(STDOUT, "FAILED");
		
		return itc.isConnected;
	}
	
	public static void isConnectedToInternet(final Function ifTrue, final Function ifFalse)
	{
		AsyncCallback callback = new AsyncCallback() {
			@Override
			public void run(Object o) {
				Boolean result = (Boolean) o;
				
				if( result )	ifTrue.call();
				else			ifFalse.call();
			}
		};
		AsyncTask task = new AsyncTask() {
			@Override
			public Object doInBackground() {
				try {
					URLRequestUtils.request(URLRequestUtils.GET, Settings.IWEAVE_URL);
					return true;
				} catch (IOException e) {
					trace(STDERR, e);
				}
				return false;
			}
		};
		task.addCallback(callback).execute();
	}
}
