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

package weave.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import weave.Settings;

public class RemoteUtils
{
	public static final String WEAVE_UPDATES_URL		= "WeaveUpdatesURL";
	
	public static final String WEAVE_INSTALLER_VERSION 	= "WeaveInstallerVersion";
	public static final String WEAVE_UPDATER_VERSION 	= "WeaveUpdaterVersion";

	public static final String JETTY_URL 				= "JettyURL";
	public static final String JETTY_VERSION 			= "JettyVersion";
	
	public static final String SHORTCUT_VER				= "WeaveShortcutVersion";
	
	public static List<String> entriesToCheck			= new ArrayList<String>( Arrays.asList( WEAVE_UPDATER_VERSION, 
																								WEAVE_INSTALLER_VERSION,
																								SHORTCUT_VER));
	public static List<String> lookupEntries			= new ArrayList<String>( Arrays.asList( Settings.UPDATER_VER,
																								Settings.INSTALLER_VER,
																								Settings.SHORTCUT_VER));
	/**
	 * Assign new values to lookupEntries
	 * 
	 * Fixed SHORTCUT_VER bug
	 */
	public static void refreshLookupValues()
	{
		entriesToCheck = new ArrayList<String>( Arrays.asList( 	WEAVE_UPDATER_VERSION,
																WEAVE_INSTALLER_VERSION,
																SHORTCUT_VER));
		lookupEntries = new ArrayList<String>( Arrays.asList( 	Settings.UPDATER_VER,
																Settings.INSTALLER_VER,
																Settings.SHORTCUT_VER ));
	}
	
	/**
	 * Check to see if an update is available for download.
	 * 
	 * @return TRUE if update exists, FALSE otherwise
	 */
	public static boolean isUpdateAvailable() 
	{
		if( Settings.isConnectedToInternet() )
		{
			File tempFile;
			boolean missingFile = false;
			boolean outOfDateFile = false;
			
			refreshLookupValues();
			
			for( int i = 0; i < entriesToCheck.size(); i++ )
			{
				String value = getConfigEntry( entriesToCheck.get(i) );
				if( value == null ) return false;
				
				outOfDateFile |= !(value).equals(lookupEntries.get(i));
			}
			
			String[] files = getRemoteFiles();
			for( String f : files ) {
				tempFile = new File(Settings.WEAVE_ROOT_DIRECTORY, f.trim());
				if( !tempFile.exists() ) {
					missingFile = true;
					break;
				}
			}
			return ( missingFile || outOfDateFile );
		}
		else 
		{
			JOptionPane.showConfirmDialog(null, 
				"A connection to the internet could not be established.\n\n" +
				"Please connect to the internet and try again.", 
				"No Connection", 
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
			
			if( Settings.CURRENT_PROGRAM_NAME.equals(Settings.UPDATER_NAME) )
				Settings.shutdown( JFrame.ERROR );
		}
		return false;
	}
	
	@SuppressWarnings("deprecation")
	private static String[] getConfigFile()
	{
		String content = "";
		
		try {
			URL url = new URL(Settings.UPDATE_CONFIG);
			String line = "";
			InputStream is = url.openStream();
			DataInputStream dis = new DataInputStream(new BufferedInputStream(is));
			
			while( (line = dis.readLine()) != null )
				content += line;
		} catch (Exception e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
		}
		return content.split(";");
	}
	
	public static String getConfigEntry(String key)
	{
		for( String s : getConfigFile() )
			if( s.contains(key) )
				return s.substring(s.indexOf(":")+1).trim();
		
		BugReportUtils.autoSubmitBugReport(new Exception("Error: " + key + " does not exist in RemoteUtils"));
		return null;
	}
	
	@SuppressWarnings("deprecation")
	public static String[] getRemoteFiles()
	{
		String content = "";

		try {
			URL url = new URL(Settings.UPDATE_FILES);
			String line = "";
			InputStream is = url.openStream();
			DataInputStream dis = new DataInputStream(new BufferedInputStream(is));
			
			while( (line = dis.readLine()) != null )
				content += line;
		} catch (Exception e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
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
			
			while( (inputLine = in.readLine()) != null ) {
				response.append(inputLine);
			}
			
			in.close();
			
			return response.toString();
			
		} catch (MalformedURLException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
		} catch (IOException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
		}
		return null;
	}
}
