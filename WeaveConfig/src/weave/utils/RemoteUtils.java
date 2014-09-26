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

import java.io.IOException;

import javax.swing.JOptionPane;

import weave.Settings;

public class RemoteUtils
{
	public static final String WEAVE_UPDATES_URL		= "WeaveUpdatesURL";
	public static final String WEAVE_BINARIES_URL		= "WeaveBinariesURL";
	
	public static final String WEAVE_SERVER_VERSION 	= "WeaveServerVersion";
	public static final String WEAVE_UPDATER_VERSION 	= "WeaveUpdaterVersion";
	public static final String WEAVE_LAUNCHER_VERSION	= "WeaveLauncherVersion";

	public static final String JETTY_URL 				= "JettyURL";
	public static final String JETTY_VERSION 			= "JettyVersion";
	
	public static final String AWS_URL					= "AnalystURL";
	public static final String AWS_VERSION				= "AnalystVersion";
	
	public static final String SHORTCUT_VER				= "WeaveShortcutVersion";
	public static final String WIKI_HELP_PAGE			= "WikiHelpPage";
	
	
	private static String[] getConfigFile()
	{
		String content = "";
		
		if( Settings.isOfflineMode() )
			return null;
			
		try {
			content = URLRequestUtils.request(URLRequestUtils.GET, Settings.UPDATE_CONFIG);
			return content.split(";");
		} catch (IOException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		} catch (InterruptedException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		}
		return null;
	}
	
	public static String getConfigEntry(String key)
	{
		if( Settings.isOfflineMode() )
			return null;

		if( !Settings.isConnectedToInternet ) {
			JOptionPane.showConfirmDialog(null, 
				"A connection to the internet could not be established.\n\n" +
				"Please connect to the internet and try again.", 
				"No Connection", 
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
			return null;
		}
		
		for( String s : getConfigFile() )
			if( s.contains(key) )
				return s.substring(s.indexOf(":")+1).trim();
		
		BugReportUtils.autoSubmitBugReport(new Exception("Error: " + key + " does not exist in RemoteUtils"));
		return null;
	}
	
	public static String[] getRemoteFiles()
	{
		String content = "";
		
		if( Settings.isOfflineMode() )
			return null;
		
		if( !Settings.isConnectedToInternet() ) {
			JOptionPane.showConfirmDialog(null, 
				"A connection to the internet could not be established.\n\n" +
				"Please connect to the internet and try again.", 
				"No Connection", 
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
			return null;
		}

		try {
			content = URLRequestUtils.request(URLRequestUtils.GET, Settings.UPDATE_FILES);
			return content.split(";");
		} catch (IOException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		} catch (InterruptedException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
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
			return URLRequestUtils.request(URLRequestUtils.GET, Settings.API_GET_IP);
		} catch (IOException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		} catch (InterruptedException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		}
		
		return null;
	}
}
