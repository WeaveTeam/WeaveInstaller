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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import weave.includes.IUtils;
import weave.includes.IUtilsInfo;

public class ZipUtils implements IUtils
{
	private IUtilsInfo _func = null;
	
	private static ZipUtils _instance = null;
	private static ZipUtils instance()
	{
		if( _instance == null )
			_instance = new ZipUtils();
		return _instance;
	}
	
	@Override
	public String getID()
	{
		return "ZipUtils";
	}
	
	
	/*
	 * ZipUtils.extractZip( zip, destination )
	 * 
	 * Will extract a zip file to the destination.
	 * No stats will be supplied with this extractZip.
	 */
	public static void extractZip( String zipFileName, String destination ) throws InterruptedException
	{
		instance().extractZipWithInfo(new File(zipFileName), new File(destination));
	}
	public static void extractZip( File zipFile, File destination ) throws InterruptedException
	{
		instance().extractZipWithInfo(zipFile, destination);
	}
	
	
	/*
	 * ZipUtils.extractZipWithInfo( zip, destination )
	 * 
	 * Will extract a zip file to the destination
	 * Stats can be tracked through the `info` object.
	 */
	public void extractZipWithInfo( String zipFileName, String destination ) throws InterruptedException
	{
		extractZipWithInfo(new File(zipFileName), new File(destination));
	}
	public void extractZipWithInfo( final File zipFile, final File destination ) throws InterruptedException
	{
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ZipFile zip = new ZipFile(zipFile);
					Enumeration<?> enu = zip.entries();
					
					while (enu.hasMoreElements()) 
					{
						ZipEntry zipEntry = (ZipEntry) enu.nextElement();
						String name = zipEntry.getName();
						File outputFile = new File(destination, name);
						
						if( zipEntry.isDirectory() )
						{
							if( !outputFile.exists() )	outputFile.mkdirs();
							if( _func != null )			updateInfo(1, _func.info.max);
							Thread.sleep(100);
							continue;
						}

						InputStream is = zip.getInputStream(zipEntry);
						FileOutputStream fos = new FileOutputStream(outputFile);
						FileUtils.copy(is, fos);
						
						if( _func != null )	updateInfo(1, _func.info.max);
						
						Thread.sleep(100);
					}
					zip.close();
				} catch (ZipException e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
				} catch (IOException e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
				} catch (InterruptedException e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
				}
			}
		});
		t.start();
		t.join();
	}
	
	
	/*
	 * ZipUtils.getNumberOfEntriesInZip( zip )
	 * 
	 * Get the number of entries in the zip file.
	 */
	@SuppressWarnings("unused")
	private static int getNumberOfEntriesInZip( String zip )
	{
		return getNumberOfEntriesInZip(new File(zip));
	}
	private static int getNumberOfEntriesInZip( File zip )
	{
		if( !zip.exists() )	return 0;
		if( !zip.isFile() )	return 0;
		
		try {
			ZipFile z = new ZipFile(zip);
			return z.size();
		} catch (ZipException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
		} catch (IOException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
		}
		
		return 0;
	}
	
	public void addEventListener(IUtils parent, IUtilsInfo func, String zip)
	{
		addEventListener(parent, func, new File(zip));
	}
	public void addEventListener(IUtils parent, IUtilsInfo func, File zip)
	{
		_func = func;
		_func.info.parent = parent;
		_func.info.min = 0;
		_func.info.cur = 0;
		_func.info.max = getNumberOfEntriesInZip(zip);
		_func.info.progress = 0;
	}
	public void removeEventListener()
	{
		_func = null;
	}
	private void updateInfo(int cur, int max)
	{
		if( _func != null )
		{
			_func.info.cur += cur;
			_func.info.max = max;
			_func.info.progress = _func.info.cur * 100 / _func.info.max;
			_func.onProgressUpdate();
		}
	}
}
