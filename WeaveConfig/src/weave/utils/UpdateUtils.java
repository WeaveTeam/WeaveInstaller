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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import weave.Settings;

public class UpdateUtils 
{

	public static List<String> entriesToCheck	= new ArrayList<String>( Arrays.asList( RemoteUtils.WEAVE_UPDATER_VERSION,
																						RemoteUtils.WEAVE_INSTALLER_VERSION,
																						RemoteUtils.SHORTCUT_VER));
	public static List<String> lookupEntries	= new ArrayList<String>( Arrays.asList( Settings.UPDATER_VER,
																						Settings.INSTALLER_VER,
																						Settings.SHORTCUT_VER));
	/**
	 * Assign new values to lookupEntries
	 * 
	 * Fixed SHORTCUT_VER bug
	 */
	public static void refreshLookupValues()
	{
		entriesToCheck = new ArrayList<String>( Arrays.asList( 	RemoteUtils.WEAVE_UPDATER_VERSION,
																RemoteUtils.WEAVE_INSTALLER_VERSION,
																RemoteUtils.SHORTCUT_VER));
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
				String value = RemoteUtils.getConfigEntry( entriesToCheck.get(i) );
				if( value == null ) return false;
				
				outOfDateFile |= !(value).equals( lookupEntries.get(i) );
			}
			
			String[] files = RemoteUtils.getRemoteFiles();
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
	
}
