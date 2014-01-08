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

import java.io.File;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import weave.Settings;

public class UpdateUtils 
{
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
			
			RemoteUtils.refreshLookupValues();
			
			for( int i = 0; i < RemoteUtils.entriesToCheck.size(); i++ )
			{
				String value = RemoteUtils.getConfigEntry( RemoteUtils.entriesToCheck.get(i) );
				if( value == null ) return false;
				
				outOfDateFile |= !(value).equals(RemoteUtils.lookupEntries.get(i));
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
