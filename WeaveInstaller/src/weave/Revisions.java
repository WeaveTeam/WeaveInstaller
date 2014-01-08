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

package weave;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import weave.utils.FileUtils;

public class Revisions 
{
	/**
	 * Check for the latest Revision updates.
	 * 
	 * @param save	flag for indicating whether we should update LAST_UPDATE_CHECK
	 * @return 0 if there is an update; 1 if there is not; -1 or -2 if there was an error.
	 */
	public static int checkForUpdates(boolean save)
	{
		/* Check for internet connection and return the corresponding error value
		 * if there isn't one.
		 */
		if( !Settings.isConnectedToInternet() )
			return -2;

		try {
			/* A URL is a pointer to a resource on the web. 
			 * UPDATE_URL is the string "https://github.com/IVPR/Weave-Binaries/zipball/master"
			 * The following line creates a URL that points to the resource of the above string. */
			URL url = new URL(Settings.BINARIES_UPDATE_URL);
			/* url.openConnection() returns a urlConnection object that represents a connection
			 * to the URL object. */
			URLConnection conn = url.openConnection();

			String urlFileName = conn.getHeaderField("Content-Disposition");
			
			int pos = urlFileName.indexOf("filename=");
			if( pos == -1 )
				return -1;

			/* ZIP_DIRECTORY == System.getenv("APPDATA")+"/WeaveUpdater/revisions/"
			 * ZIP_DIRECTORY.getPath() == C:\Users\...\WeaveUpdater\revisions + "/IVPR-Weave-Binaries...
			 * substring( pos + 9 ) skips the filename= and obtains the path string */
			String updateFileName = Settings.REVISIONS_DIRECTORY.getPath()+"/"+urlFileName.substring(pos+9);
			File updateFile = new File(updateFileName);

			/* If the save flag is checked, we update the LAST_UPDATE_CHECK with the current date
			 * and write the settings. */
			if( save ) {
				Settings.LAST_UPDATE_CHECK = new SimpleDateFormat("M/dd/yy h:mm a").format(Calendar.getInstance().getTime());
				Settings.save();
			}
			
			/* Return 0 if the updateFile exists and 1 otherwise */
			return ( updateFile.exists() ) ? 0 : 1;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		/* Code only reaches this point in the event of an exception
		 * -1 is an error flag. */
		return -1;
	}
	
	/**
	 * Extract the .zip file at fileName and display UI elements.
	 * 
	 * @param fileName	the name of the .zip file to be extracted.
	 * @param progBar	UI element progress bar of the weave installer.
	 * @param button	UI button of the weave installer.
	 */
	public static void extractZip(final String fileName, final JProgressBar progBar, final JButton button)
	{
		progBar.setStringPainted(true);
		progBar.setString("Extracting Files...");
		progBar.setIndeterminate(false);
		
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					int i = 0;
					ZipFile zipFile = new ZipFile(fileName);
					Enumeration<?> enu = zipFile.entries();
					
					progBar.setMaximum(zipFile.size());
					progBar.setValue(i);
					while (enu.hasMoreElements()) {
						ZipEntry zipEntry = (ZipEntry) enu.nextElement();

						String name = zipEntry.getName();
//						long size = zipEntry.getSize();
//						long compressedSize = zipEntry.getCompressedSize();
//						System.out.printf("%-60s | %10d | %10d\n", name, size, compressedSize);

						File file = new File(Settings.UNZIP_DIRECTORY.getPath()+"/"+name);
						if (name.endsWith("/")) {
							file.mkdirs();
							continue;
						}

						InputStream is = zipFile.getInputStream(zipEntry);
						FileOutputStream fos = new FileOutputStream(file);
						byte[] bytes = new byte[1024];
						int length;
						while ((length = is.read(bytes)) >= 0) {
							fos.write(bytes, 0, length);
						}
						is.close();
						fos.close();
						progBar.setValue(++i);
						Thread.sleep(100);
					}
					zipFile.close();
					
					progBar.setMaximum(100);
					progBar.setString("Files Extracted Successfully");
					progBar.setIndeterminate(false);
					progBar.setValue(100);
					moveExtractedFiles(fileName, progBar, button);
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Error extracting build " + getRevisionName(fileName) +". The version is currupt.\n\nPlease delete this revision.", "Error", JOptionPane.ERROR_MESSAGE);

					progBar.setStringPainted(true);
					progBar.setString("");
					progBar.setIndeterminate(false);
					progBar.setMaximum(100);
					progBar.setValue(0);
					WeaveInstaller.installer.postSP.deleteButton.setEnabled(true);
					WeaveInstaller.installer.postSP.revertButton.setEnabled(true);
				}
			}
		});
		t.start();
	}
	
	/**
	 * Moves the extracted files.
	 * 
	 * @param zipFileName	File name.
	 * @param progBar	GUI Progress Bar of the Weave Updater.
	 * @param button	GUI button element.
	 */
	private static void moveExtractedFiles(String zipFileName, JProgressBar progBar, final JButton button ) throws Exception
	{
		String[] files = Settings.UNZIP_DIRECTORY.list();
		File releaseDir = new File(Settings.UNZIP_DIRECTORY.getPath()+"/"+files[0]);
		File ROOT = new File(Settings.ACTIVE_CONTAINER_PLUGIN.getWebappsDirectory(), "ROOT");
		String[] releaseFiles = releaseDir.list();
		
		for( int i = 0; i < releaseFiles.length; i++ )
		{
			String fileName = releaseFiles[i];
			int extN = fileName.lastIndexOf('.');
			if( extN == -1 ) 
			{
				if( fileName.equals("ROOT") )
				{
					File rootDir = new File(releaseDir, "ROOT");
					String[] rootFiles = rootDir.list();
					
					progBar.setStringPainted(true);
					progBar.setString("Installing Files...");
					progBar.setMaximum(rootFiles.length);
					
					for( int j = 0; j < rootFiles.length; j++ )
					{
						String rootFileName = rootFiles[j];
						File unzipedFile = new File(rootDir, rootFileName);
						File movedFile = new File(ROOT, rootFileName);
						if( movedFile.exists() )
							movedFile.delete();
						
						unzipedFile.renameTo(movedFile);
						progBar.setValue(j+1);
						
						Thread.sleep(200);
//						System.out.println("Moved " + movedFile.getPath());
					}
				}
				continue;
			}
			String ext = fileName.substring(extN+1, fileName.length());
			if( ext.equals("war") )
			{
				File unzipedWar = new File(releaseDir, fileName);
				File movedWar = new File(Settings.ACTIVE_CONTAINER_PLUGIN.getWebappsDirectory(), fileName);
				if( movedWar.exists() )
					movedWar.delete();
				unzipedWar.renameTo(movedWar);
//				System.out.println("Moved " + movedWar.getPath());
			}
		}
		progBar.setString("Installation Finished. Please wait...");
		progBar.setIndeterminate(false);
		progBar.setMaximum(100);
		progBar.setValue(100);
		
		Settings.CURRENT_INSTALL_VER = Revisions.getRevisionName(zipFileName);

		FileUtils.recursiveDelete(Settings.UNZIP_DIRECTORY);
		Settings.save();
		button.setEnabled(true);
		WeaveInstaller.installer.cancelButton.setEnabled( true ) ;
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				button.doClick();
			}
		});
	}
	
	/** 
	 * Gets the number of revisions to date.
	 * 
	 * @return 0 if there are no revisions; else number of revisions.
	 */
	public static int getNumberOfRevisions()
	{
		if( !Settings.REVISIONS_DIRECTORY.exists() )
			return 0;

		return Settings.REVISIONS_DIRECTORY.list().length;
	}
	
	/**
	 * Get the size of the ZIP_DIRECTORY
	 * 
	 * @return Size of ZIP_DIRECTORY in bytes
	 */
	public static long getSizeOfRevisions()
	{
		if( !Settings.REVISIONS_DIRECTORY.exists() )
			return 0;
		
		long size = 0;
		File[] files = Settings.REVISIONS_DIRECTORY.listFiles();
		
		for( int i = 0; i < files.length; i++ )
			size += files[i].length();
		
		return size;
	}
	
	/**
	 * Save 4 revisions and delete all others.
	 * 
	 * @return true if completed successfully
	 */
	public static boolean pruneRevisions()
	{
		ArrayList<File> files = getRevisionData();
		Iterator<File> it = files.iterator();
		File file = null;
		int i = 0;
		long[] mods = new long[files.size()];
		
		while( it.hasNext() )	{ mods[i++] = it.next().lastModified();	}	
		
		i = 0;
		it = files.iterator();
		
		while( it.hasNext() ) 
		{
			file = it.next();
			
			// Save these files
			if( i == 0 || i == 1 )						{	i++;	continue;	}
			if( i == (Math.ceil((mods.length-2)/2)+2) )	{	i++;	continue;	}
			if( i == mods.length-2 )					{	i++;	continue;	}
			
			// Delete the others
			file.delete();
			i++;
		}
		return true;
	}
	
	/** 
	 * Extracts the version (name) out of the long file name.
	 * 
	 * @param n		The file name of the zip
	 * @return		Extracted version of the zip
	 */
	public static String getRevisionName(String n)
	{
		return n.substring(n.lastIndexOf('-')+1, n.lastIndexOf('.')).toUpperCase();
	}

	/**
	 * Find all the files in the ZIP_DIRECTORY, sort them and return a sorted ArrayList
	 * corresponding to all the revisions.
	 * 
	 * @return	Sorted list.
	 */
	public static ArrayList<File> getRevisionData()
	{
		File[] files = Settings.REVISIONS_DIRECTORY.listFiles();
		ArrayList<File> sortedFiles = new ArrayList<File>();
		
		for( int i = 0; i < files.length; i++ )
			sortedFiles.add(files[i]);
		
		Collections.sort(sortedFiles, new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				if( o1.lastModified() < o2.lastModified() ) return 1;
				if( o1.lastModified() > o2.lastModified() ) return -1;
				return 0;
			}
		});
		return sortedFiles;
	}
}
