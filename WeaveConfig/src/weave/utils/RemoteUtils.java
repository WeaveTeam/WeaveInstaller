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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import weave.Settings;

public class RemoteUtils
{
	public static final String WEAVE_UPDATES_URL		= "WeaveUpdatesURL";
	public static final String WEAVE_BINARIES_URL		= "WeaveBinariesURL";
	
	public static final String WEAVE_INSTALLER_VERSION 	= "WeaveInstallerVersion";
	public static final String WEAVE_UPDATER_VERSION 	= "WeaveUpdaterVersion";

	public static final String JETTY_URL 				= "JettyURL";
	public static final String JETTY_VERSION 			= "JettyVersion";
	
	public static final String SHORTCUT_VER				= "WeaveShortcutVersion";
	
	
	private static long MAX_CONFIG_AGE		= 5; // MINUTES
	private static long	CONFIG_AGE			= 0;
	private static String[] lastKnownConfig = null;
	
	private static String[] getConfigFile()
	{
		String content = "";
		
		if( lastKnownConfig != null && ( (System.currentTimeMillis() - CONFIG_AGE) / 1000 ) < MAX_CONFIG_AGE )
			return lastKnownConfig;
		
		try {
			URL url = new URL(Settings.UPDATE_CONFIG);
			String line = "";
			InputStream is = url.openStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			
			while( (line = reader.readLine()) != null )
				content += line;
		} catch (Exception e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		}
		
		lastKnownConfig = content.split(";");
		CONFIG_AGE = System.currentTimeMillis();
		
		return lastKnownConfig;
	}
	
	public static String getConfigEntry(String key)
	{
		for( String s : getConfigFile() )
			if( s.contains(key) )
				return s.substring(s.indexOf(":")+1).trim();
		
		BugReportUtils.autoSubmitBugReport(new Exception("Error: " + key + " does not exist in RemoteUtils"));
		return null;
	}
	
	public static String[] getRemoteFiles()
	{
		String content = "";

		try {
			URL url = new URL(Settings.UPDATE_FILES);
			String line = "";
			InputStream is = url.openStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			
			while( (line = reader.readLine()) != null )
				content += line;
		} catch (Exception e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		}
		return content.split(";");
	}
	
	public static String getIP()
	{
		try {
			URL url = new URL(Settings.API_GET_IP);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder response = new StringBuilder();
			String inputLine;
			
			while( (inputLine = in.readLine()) != null )
				response.append(inputLine);
			
			in.close();
			
			return response.toString();
			
		} catch (MalformedURLException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.autoSubmitBugReport(e);
		} catch (IOException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.autoSubmitBugReport(e);
		}
		return null;
	}
}
