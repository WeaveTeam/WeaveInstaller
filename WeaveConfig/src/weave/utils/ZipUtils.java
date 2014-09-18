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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import weave.async.AsyncObserver;
import weave.includes.IUtils;

public class ZipUtils extends TransferUtils implements IUtils
{
	public static int extract( File zipFile, File destination) throws ZipException, IOException, InterruptedException
	{
		return extract(zipFile, destination, NO_FLAGS);
	}
	public static int extract( File zipFile, File destination, int flags) throws ZipException, IOException, InterruptedException
	{
		return extract(zipFile, destination, flags, null);
	}
	public static int extract( File zipFile, File destination, int flags, AsyncObserver observer) throws ZipException, IOException, InterruptedException
	{
		return extract(zipFile, destination, flags, observer, 0);
	}
	
	/**
	 * Extract a zip file to the destination location.
	 * 
	 * @param zipFile
	 * @param destination
	 * @param flags
	 * @param observer
	 * @param throttle
	 * @return
	 * @throws IOException 
	 * @throws ZipException 
	 * @throws InterruptedException 
	 */
	public static int extract( File zipFile, File destination, int flags, AsyncObserver observer, int throttle ) throws ZipException, IOException, InterruptedException
	{
		if( zipFile == null || destination == null )
			throw new NullPointerException("Zip File or Destination File cannot be null");
		
		assert zipFile != null;
		assert destination != null;
		
		ZipFile zip = new ZipFile(zipFile);
		Enumeration<?> enu = zip.entries();
		ZipEntry zipEntry = null;
		
		while (enu.hasMoreElements()) 
		{
			zipEntry = (ZipEntry) enu.nextElement();
			File outputFile = new File(destination, zipEntry.getName());
			
			if( zipEntry.isDirectory() )
			{
				if( !outputFile.exists() )	outputFile.mkdirs();
				continue;
			}
			
			if( observer != null && (flags & TransferUtils.MULTIPLE_FILES) != 0 ) {
				observer.info.cur++;
				observer.info.percent = (int) (observer.info.cur * 100 / observer.info.max);
				observer.onUpdate();
			}

			InputStream is = zip.getInputStream(zipEntry);
			FileOutputStream fos = new FileOutputStream(outputFile);
			FileUtils.copy(is, fos, flags, observer, throttle);
		}
		zip.close();
		
		return COMPLETE;
	}

	
	/*
	 * ZipUtils.getNumberOfEntriesInZip( zip )
	 * 
	 * Get the number of entries in the zip file.
	 */
	public static int getNumberOfEntriesInZip( String zip )
	{
		return getNumberOfEntriesInZip(new File(zip));
	}
	public static int getNumberOfEntriesInZip( File zip )
	{
		if( !zip.exists() )	return 0;
		if( !zip.isFile() )	return 0;
		
		try {
			ZipFile z = new ZipFile(zip);
			return z.size();
		} catch (ZipException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		} catch (IOException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		}
		
		return 0;
	}
}
