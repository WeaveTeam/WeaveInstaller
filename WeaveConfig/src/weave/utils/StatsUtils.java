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

import weave.Settings;
import weave.managers.ConfigManager;

public class StatsUtils 
{
	public static void logUpdate()
	{
		logUpdate( false );
	}
	
	public static void logUpdate( boolean forced )
	{
		if( Settings.isOfflineMode() || !Settings.isConnectedToInternet() )
			return;
		
		URLRequestParams params = new URLRequestParams();
		params.add("action", 	"UPDATE");
		params.add("uniqueID",	Settings.UNIQUE_ID);
		params.add("forced", 	(forced ? "1" : "0"));
		
		try {
			URLRequestUtils.request(URLRequestUtils.GET, Settings.API_STATS_LOG, params);
		} catch (IOException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		}
	}

	public static void noop()
	{
		if( Settings.isOfflineMode() || !Settings.isConnectedToInternet() )
			return;
		
		URLRequestParams params = new URLRequestParams();
		params.add("uniqueID", 	Settings.UNIQUE_ID);
		params.add("os", 		Settings.getExactOS());
		params.add("server", 	( ConfigManager.getConfigManager().getContainer() != null ? ConfigManager.getConfigManager().getContainer().getConfigName() : "NONE"));
		params.add("database", 	( ConfigManager.getConfigManager().getDatabase() != null ?	ConfigManager.getConfigManager().getDatabase().getConfigName() 	: "NONE"));
		
		try {
			URLRequestUtils.request(URLRequestUtils.GET, Settings.API_STATS_LIVE, params);
		} catch (IOException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		}
	}
}
