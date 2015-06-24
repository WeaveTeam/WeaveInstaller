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

package weave.configs;

import static weave.utils.TraceUtils.STDERR;
import static weave.utils.TraceUtils.trace;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import weave.managers.ConfigManager;
import weave.managers.ResourceManager;
import weave.utils.BugReportUtils;


public class PostgreSQLConfig extends Config 
{
	public static final String NAME = "PostgreSQL";
	public static final String HOMEPAGE = "http://www.postgresql.org/download/";
	public static final String URL = HOMEPAGE;
	public static final String HOST = "localhost";
	public static final int PORT = 5432;
	
	public static PostgreSQLConfig _instance = null;
	public static PostgreSQLConfig getConfig()
	{
		if( _instance == null )
			_instance = new PostgreSQLConfig();
		return _instance;
	}
	
	public PostgreSQLConfig()
	{
		super(NAME, HOMEPAGE, URL, HOST, PORT);
	}

	@Override public void initConfig()
	{
		super.initConfig(_HOST | _PORT);
		
		try {
			
			setDescription(getConfigName() + " is a powerful, open source object-relational database system. It has more than 15 years of active development and a proven architecture that has earned it a strong reputation for reliability, data integrity, and correctness.");
			setWarning("<center><b>" + getConfigName() + " requires the use of its external application found " + 
						"<a href='" + getDownloadURL() + "'>here.</a></b></center>");
			setImage(ImageIO.read(ResourceManager.IMAGE_POSTGRESQL));

		} catch (NumberFormatException | IOException e) {
			trace(STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		}
	}
	@Override public boolean loadConfig() 
	{
		boolean result = ConfigManager.getConfigManager().setDatabase(_instance); 
		if( result )
			super.loadConfig();
		else
			JOptionPane.showMessageDialog(null, 
					"There was an error loading the " + getConfigName() + " config.\n" + 
					"Another config might already be loaded.", 
					"Error", JOptionPane.ERROR_MESSAGE);
		return result;
	}

	@Override public boolean unloadConfig()
	{
		boolean result = ConfigManager.getConfigManager().setDatabase(null);
		super.unloadConfig();
		return result;
	}
}
