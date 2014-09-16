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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import weave.Settings;
import weave.async.AsyncObserver;
import weave.async.IAsyncCallback;
import weave.async.IAsyncCallbackResult;
import weave.includes.IUtils;
import weave.includes.IUtilsInfo;

public class DownloadUtils implements IUtils
{
	public static final int FAILED		= ( 1 << 0 );
	public static final int COMPLETE	= ( 1 << 1 );
	public static final int CANCELLED 	= ( 1 << 2 );
	public static final int OFFLINE		= ( 1 << 3 );
	
	public static final int B			= 1;
	public static final int KB			= B * 1024;
	public static final int MB			= KB * 1024;
	public static final int GB			= MB * 1024;
	
	private IUtilsInfo _func = null;
	private List<IAsyncCallback> callbacks = null;
	
	private static DownloadUtils _instance = null;
	private static DownloadUtils instance()
	{
		if( _instance == null )
			_instance = new DownloadUtils();
		return _instance;
	}
	
	public DownloadUtils()
	{
		callbacks = Collections.synchronizedList(new ArrayList<IAsyncCallback>());
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
		
		while( (speed/KB) > 1 ) {
			speed = speed / KB;
			i++;
		}
		return String.format("%." + i + "f %s", speed, s.get(i));
	}
	
	
	/*
	 * DownloadUtils.download( url, destination )
	 * 
	 * This will open an input stream to the URL and download the contents to the destination.
	 * No stats will be supplied with this download.
	 */
	public static void download( String url, String destination ) throws IOException, InterruptedException
	{
		instance().downloadWithInfo(url, destination);
	}
	public static void download( URL url, File destination ) throws IOException, InterruptedException
	{
		instance().downloadWithInfo(url, destination);
	}
	
	
	public static long download1(URL url, File destination) throws IOException
	{
		return download1(url, destination, null);
	}
	public static long download1(URL url, File destination, AsyncObserver observer) throws IOException
	{
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		InputStream in = conn.getInputStream();
		OutputStream out = new FileOutputStream(destination);
		
		long size = FileUtils.copy1(in, out, observer);
		
		in.close();
		out.close();
		
		return size;
	}
	
	/*
	 * DownloadUtils.downloadWithInfo( url, destination )
	 * 
	 * This will open an input stream to the URL and download the contents to the destination.
	 * Stats can be tracked through the `info` object.
	 */
	public void downloadWithInfo( String url, String destination ) throws MalformedURLException, InterruptedException
	{
		downloadWithInfo(url, destination, 0);
	}
	public void downloadWithInfo( String url, String destination, long downloadLimit ) throws MalformedURLException, InterruptedException
	{
		downloadWithInfo(new URL(url), new File(destination), downloadLimit);
	}
	
	public void downloadWithInfo( String url, File destination ) throws MalformedURLException, InterruptedException
	{
		downloadWithInfo(url, destination, 0);
	}
	public void downloadWithInfo( String url, File destination, long downloadLimit ) throws MalformedURLException, InterruptedException
	{
		downloadWithInfo(new URL(url), destination, downloadLimit);
	}

	public void downloadWithInfo( URL url, File destination ) throws InterruptedException
	{
		downloadWithInfo(url, destination, 0);
	}
	public void downloadWithInfo( final URL url, final File destination, final long downloadLimit ) throws InterruptedException
	{
		final DownloadInternalUtils diu = new DownloadInternalUtils();
		diu.status = COMPLETE;
		
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				HttpURLConnection conn 	= null;
				InputStream in 			= null;
				OutputStream out 		= null;
				
				try {
					// Setup the connection to follow redirects
					conn = (HttpURLConnection) url.openConnection();
					conn.setInstanceFollowRedirects(true);
					conn.setRequestMethod("GET");
					conn.connect();

					// Initialize input and output streams
					in 	= conn.getInputStream();
					out = new FileOutputStream(destination);
					
					byte buffer[] 		= new byte[1024*4];
					long sizeOfDownload = conn.getContentLengthLong();
					int length = 0, cur = 0, kbps = 0, limit = 0, seconds = 0, aveDownSpeed = 1, timeleft = 0;
					long speedLongNew 	= 0, speedLongOld = System.currentTimeMillis();
					long cancelLongNew 	= 0, cancelLongOld = System.currentTimeMillis();
					
					if( _func != null ) setInfo(0, sizeOfDownload, 60);
					
					while( (length = in.read(buffer)) > 0 )
					{
						out.write(buffer, 0, length);
						
						cur += length;
						kbps += length;
						speedLongNew = System.currentTimeMillis();
						cancelLongNew = System.currentTimeMillis();
						
						if( ( speedLongNew - speedLongOld ) > 1000 )
						{
							if( _func != null ) _func.info.speed = kbps;
							kbps = 0;
							seconds++;
							speedLongOld = speedLongNew;
							aveDownSpeed = (cur/KB)/seconds;
						}
						
						// Throttle the check to see if the user has clicked the
						// cancel button to every 100 milliseconds to stop the download. 
						if( ( cancelLongNew - cancelLongOld ) > 200 ) {
							cancelLongOld = cancelLongNew;
							if( Settings.downloadCanceled == true ) 
							{
								diu.status = CANCELLED;

								if( in != null )	in.close();
								if( out != null )	out.close();
								return;
							}
						}
						timeleft = (int) ((sizeOfDownload - cur) / aveDownSpeed / KB);
						updateInfo(length, sizeOfDownload, timeleft);
						
						if( downloadLimit > 0 ) 
						{
							limit += length;
							
							// 1/10th for speedy UI updates
							if( limit >= ( downloadLimit / 10 ) ) {
								limit = 0;
								Thread.sleep(100);
							}
						}
					}
					out.flush();
					
					if( _func != null ) setInfo(sizeOfDownload, sizeOfDownload, 0);
					
				} catch ( IOException e ) {
					TraceUtils.trace(TraceUtils.STDERR, e);
					BugReportUtils.showBugReportDialog(e);
					diu.status = FAILED;
					return;
				} catch (InterruptedException e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
					BugReportUtils.showBugReportDialog(e);
					diu.status = FAILED;
					return;
				} finally {
					try {
						if( in != null )	in.close();
						if( out != null )	out.close();
						
						if( conn != null ) {
							conn.disconnect();
						}

						if( callbacks != null )
						{
							synchronized (callbacks) {
								for( int i = 0; i < callbacks.size(); i++ )
								{
									IAsyncCallbackResult res = new IAsyncCallbackResult() {
										@Override public Object getResult() {
											return null;
										}
										@Override public String getMessage() {
											return "";
										}
										@Override public int getCode() {
											return diu.status;
										}
									};
									callbacks.get(i).run(res);
								}
							}
						}
					} catch (IOException e) {
						TraceUtils.trace(TraceUtils.STDERR, e);
						BugReportUtils.showBugReportDialog(e);
					}
				}
			}
		});
		t.start();
	}
	
	public boolean addCallback(IAsyncCallback c)
	{
		return callbacks.add(c);
	}
	public boolean removeCallback(IAsyncCallback c)
	{
		return callbacks.remove(c);
	}
	public void removeAllCallbacks()
	{
		callbacks.clear();
	}
	
	public void addStatusListener( IUtils parent, IUtilsInfo func ) throws IOException
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
	
	public void removeStatusListener()
	{
		_func = null;
	}
	
	private void updateInfo(long cur, long max, int timeleft)
	{
		if( _func != null )
		{
			_func.info.cur += cur;
			_func.info.max = max;
			_func.info.timeleft = timeleft;
			_func.info.progress = (int) (_func.info.cur * 100 / _func.info.max);
			_func.onProgressUpdate();
		}
	}
	private void setInfo(long cur, long max, int timeleft)
	{
		if( _func != null )
		{
			_func.info.cur = cur;
			_func.info.max = max;
			_func.info.timeleft = timeleft;
			_func.info.progress = (int) (_func.info.cur * 100 / _func.info.max);
			_func.onProgressUpdate();
		}
	}
}

class DownloadInternalUtils
{
	int status;
}
