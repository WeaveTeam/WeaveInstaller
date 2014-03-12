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
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;

import weave.Settings;
import weave.includes.IUtils;
import weave.includes.IUtilsInfo;

public class DownloadUtils implements IUtils
{
	public static final int FAILED		= ( 1 << 0 );
	public static final int COMPLETE	= ( 1 << 1 );
	public static final int CANCELLED 	= ( 1 << 2 );
	
	private IUtilsInfo _func = null;
	
	private static DownloadUtils _instance = null;
	private static DownloadUtils instance()
	{
		if( _instance == null )
			_instance = new DownloadUtils();
		return _instance;
	}
	
	public DownloadUtils()
	{
		
	}
	
	@Override
	public String getID()
	{
		return "DownloadUtils";
	}
	
	
	/*
	 * DownloadUtils.speedify( speed )
	 * 
	 * Convert a scalar value into a downloaded speed measure.
	 */
	public static String speedify( int speed )
	{
		return speedify( (double)speed );
	}
	public static String speedify( double speed )
	{
		int i = 0; 
		List<String> s = Arrays.asList("B/s", "KB/s", "MB/s", "GB/s", "TB/s");
		
		while( (speed/1024) > 1 ) {
			speed = speed / 1024;
			i++;
		}
		return String.format("%0." + i + "f %s", s.get(i));
	}
	
	
	/*
	 * DownloadUtils.download( url, destination )
	 * 
	 * This will open an input stream to the URL and download the contents to the destination.
	 * No stats will be supplied with this download.
	 */
	public static int download( String url, String destination ) throws IOException, InterruptedException
	{
		return instance().downloadWithInfo(url, destination);
	}
	public static int download( URL url, File destination ) throws IOException, InterruptedException
	{
		return instance().downloadWithInfo(url, destination);
	}
	
	
	/*
	 * DownloadUtils.downloadWithInfo( url, destination )
	 * 
	 * This will open an input stream to the URL and download the contents to the destination.
	 * Stats can be tracked through the `info` object.
	 */
	public int downloadWithInfo( String url, File destination ) throws IOException, InterruptedException
	{
		return downloadWithInfo(new URL(url), destination);
	}
	public int downloadWithInfo( String url, String destination ) throws IOException, InterruptedException
	{
		return downloadWithInfo(new URL(url), new File(destination));
	}
	public int downloadWithInfo( final URL url, final File destination ) throws InterruptedException
	{
		final DownloadInternalUtils diu = new DownloadInternalUtils();
		diu.status = COMPLETE;
		
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					URLConnection conn 	= url.openConnection();
					InputStream in 		= conn.getInputStream();
					OutputStream out 	= new FileOutputStream(destination);
					int sizeOfDownload 	= conn.getContentLength();
					
					int length = 0, cur = 0, kbps = 0, seconds = 0, aveDownSpeed = 1, timeleft = 0;
					byte buffer[] 		= new byte[1024*4];
					long speedLongNew 	= 0, speedLongOld = System.currentTimeMillis();
					long cancelLongNew 	= 0, cancelLongOld = System.currentTimeMillis();
					
					if( _func != null ) setInfo(0, sizeOfDownload, 60);
					
					while( (length = in.read(buffer)) > 0 )
					{
						out.write(buffer, 0, length);
						
						cur += length;
						kbps += ( length / 1024 );
						speedLongNew = System.currentTimeMillis();
						cancelLongNew = System.currentTimeMillis();
						
						if( ( speedLongNew - speedLongOld ) > 1000 )
						{
							if( _func != null ) _func.info.speed = kbps;
							kbps = 0;
							seconds++;
							speedLongOld = speedLongNew;
							aveDownSpeed = (cur/1024)/seconds;
						}
						if( ( cancelLongNew - cancelLongOld ) > 200 ) {
							cancelLongOld = cancelLongNew;
							if( Settings.downloadCanceled == true ) {
								diu.status = CANCELLED;
								in.close();
								out.close();
								return;
							}
						}
						timeleft = (sizeOfDownload - cur) / aveDownSpeed / 1024;
						updateInfo(length, sizeOfDownload, timeleft);
					}
					out.flush();
					if( _func != null ) setInfo(sizeOfDownload, sizeOfDownload, 0);
					in.close();
					out.close();
				} catch ( IOException e ) {
					TraceUtils.trace(TraceUtils.STDERR, e);
					BugReportUtils.showBugReportDialog(e);
					diu.status = FAILED;
					return;
				}
			}
		});
		t.start();
		t.join();
		return diu.status;
	}
	
	
	public void addEventListener( IUtils parent, IUtilsInfo func ) throws IOException
	{
		_func = func;
		_func.info.parent = parent;
		_func.info.min = 0;
		_func.info.cur = 0;
		_func.info.max = 1;
		_func.info.speed = 0;
		_func.info.timeleft = 0;
		_func.info.progress = 0;
	}
	
	public void removeEventListener()
	{
		_func = null;
	}
	
	private void updateInfo(int cur, int max, int timeleft)
	{
		if( _func != null )
		{
			_func.info.cur += cur;
			_func.info.max = max;
			_func.info.timeleft = timeleft;
			_func.info.progress = _func.info.cur * 100 / _func.info.max;
			_func.onProgressUpdate();
		}
	}
	private void setInfo(int cur, int max, int timeleft)
	{
		if( _func != null )
		{
			_func.info.cur = cur;
			_func.info.max = max;
			_func.info.timeleft = timeleft;
			_func.info.progress = _func.info.cur * 100 / _func.info.max;
			_func.onProgressUpdate();
		}
	}
}

class DownloadInternalUtils
{
	int status;
}
