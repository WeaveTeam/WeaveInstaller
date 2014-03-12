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
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import weave.Settings;

public class StatsUtils 
{
	public static void logUpdate()
	{
		logUpdate( false );
	}
	
	@SuppressWarnings("unused")
	public static void logUpdate( boolean forced )
	{
		URL url					= null;
		HttpURLConnection conn 	= null;
		
		String query	= "action=UPDATE&"
						+ "uniqueID=" + Settings.UNIQUE_ID + "&"
						+ "forced=" + ( ( forced ) ? "1" : "0" );
		
		try {
			url = new URL(Settings.API_STATS_LOG + "?" + query);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestProperty("Content-Type", "multipart/form-data");
			conn.setRequestProperty("charset", "utf-8");
			conn.connect();
			
			String line;
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while( (line = reader.readLine()) != null ) ;
			reader.close();
			
		} catch (IOException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
		} finally {
			if( conn != null )
				conn.disconnect();
		}
	}

	@SuppressWarnings("unused")
	public static void noop()
	{
		URL url 				= null;
		HttpURLConnection conn 	= null;
		
		String query 	= "uniqueID=" + Settings.UNIQUE_ID + "&"
						+ "os=" + Settings.getExactOS() + "&";
		
		query += "server=";
		if( Settings.ACTIVE_CONTAINER_PLUGIN == null )
			query += "NONE&";
		else
			query += Settings.ACTIVE_CONTAINER_PLUGIN.getPluginName() + "&";
		
		query += "database=";
		if( Settings.ACTIVE_DATABASE_PLUGIN == null )
			query += "NONE";
		else
			query += Settings.ACTIVE_DATABASE_PLUGIN.getPluginName();
		
		try {
			url = new URL(Settings.API_STATS_LIVE + "?" + query);
			conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestProperty("Content-Type", "multipart/form-data");
			conn.setRequestProperty("charset", "utf-8");
			conn.connect();
			
			String line;
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while( (line = reader.readLine()) != null ) ;
			reader.close();
			
		} catch (IOException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
		} finally {
			if( conn != null )
				conn.disconnect();
		}
	}
}
