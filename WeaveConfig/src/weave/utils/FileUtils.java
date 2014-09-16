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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import weave.callbacks.ICallback;
import weave.includes.IUtils;
import weave.includes.IUtilsInfo;

public class FileUtils implements IUtils
{
	public static final int OVERWRITE				= ( 1 << 1 );
	public static final int OPTION_SINGLE_FILE 		= ( 1 << 2 );
	public static final int OPTION_MULTIPLE_FILES 	= ( 1 << 3 );
	private static final int OPTION_DEFAULT			= OPTION_SINGLE_FILE;
	
	public static final int FAILED					= 0;
	public static final int COMPLETE				= 1;
	public static final int CANCELLED				= 2;
	
	private IUtilsInfo _func = null;
	private List<ICallback> callbacks = null;
	
	private static FileUtils _instance = null;
	private static FileUtils instance()
	{
		if( _instance == null )
			_instance = new FileUtils();
		return _instance;
	}
	
	public FileUtils()
	{
		callbacks = Collections.synchronizedList(new ArrayList<ICallback>());
	}
	
	@Override
	public String getID()
	{
		return "FileUtils";
	}

	/*
	 * FileUtils.copy( source, destination )
	 * 
	 * Will copy a file from the source location to the destination location.
	 * No stats will be supplied on this copy.
	 */
	public static int copy( String source, String destination ) throws IOException, InterruptedException
	{
		return copy( source, destination, OPTION_DEFAULT );
	}
	public static int copy( File source, File destination ) throws IOException, InterruptedException
	{
		return copy( source, destination, OPTION_DEFAULT );
	}
	public static int copy( InputStream source, OutputStream destination ) throws IOException, InterruptedException
	{
		return copy( source, destination, OPTION_DEFAULT );
	}

	/*
	 * FileUtils.copy( source, destination, flags )
	 * 
	 * Will copy a file from the source location to the destination location
	 * with a overwrite flag bit.
	 * No stats will be supplied on this copy.
	 */
	public static int copy( String source, String destination, int flags ) throws IOException, InterruptedException
	{
		return instance().copyWithInfo(source, destination, flags);
	}
	public static int copy( File source, File destination, int flags ) throws IOException, InterruptedException
	{
		return instance().copyWithInfo(source, destination, flags);
	}
	public static int copy( InputStream source, OutputStream destination, int flags ) throws IOException, InterruptedException
	{
		return instance().copyWithInfo(source, destination, flags);
	}

	
	/*
	 * FileUtils.renameTo( source, destination )
	 * 
	 * Will rename a file from the source location to the destination location.
	 * Default operation is to not overwrite if destination file exists.
	 */
	public static boolean renameTo( String source, String destination )
	{
		return renameTo(source, destination, 0);
	}
	public static boolean renameTo( File source, File destination )
	{
		return renameTo(source, destination, 0);
	}
	
	
	/*
	 * FileUtils.renameTo( source, destination, flags )
	 * 
	 * Will rename a file from the source location to the destination location.
	 * The user is given the option to supply an overwrite flag to overwrite
	 * if the destination file already exists.
	 */
	public static boolean renameTo( String source, String destination, int flags )
	{
		return renameTo(new File(source), new File(destination), flags);
	}
	public static boolean renameTo( File source, File destination, int flags )
	{
		if( destination.exists() )
			if( (flags & OVERWRITE ) != 0 )
				recursiveDelete(destination);
			else
				return false;
		
		return source.renameTo(destination);
	}
	
	
	/*
	 * FileUtils.getClassPath( class )
	 * 
	 * Get the current class path of the specified class.
	 */
	public static String getClassPath(Class<?> c)
	{
		return c.getProtectionDomain().getCodeSource().getLocation().getPath();
	}
	
	
	/*
	 * FileUtils.getFileExtension( file )
	 * 
	 * Get the extension of the specified file
	 */
	public static String getFileExtension(String s)
	{
		return s.substring(s.lastIndexOf(".")+1);
	}
	public static String getFileExtension(File f)
	{
		return getFileExtension(f.getAbsolutePath());
	}

	
	/*
	 * FileUtils.getFileContents( file )
	 * 
	 * This can read a text file line by line and return it as a string.
	 */
	public static String getFileContents(String f) throws IOException
	{
		return getFileContents(new File(f));
	}
	public static String getFileContents(File f) throws IOException
	{
		return getFileContents(new FileInputStream(f));
	}
	public static String getFileContents(InputStream in) throws IOException
	{
		String contents = "";
		String line = "";
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		while( (line = reader.readLine()) != null )
			contents += line;
		reader.close();
		
		return contents;
	}
	

	/*
	 * FileUtils.sizeify( size )
	 * 
	 * Will return a String representation of the size given in bytes.
	 */
	public static String sizeify( long size )
	{
		return sizeify( (double)size );
	}
	public static String sizeify( int size )
	{
		double d = size;
		return sizeify(d);
	}
	public static String sizeify( double size )
	{
		int i = 0;
		List<String> s = Arrays.asList("B", "KB", "MB", "GB", "TB");
		
		while( (size/1024) > 1 )
		{
			size = size / 1024;
			i++;
		}
		return String.format("%." + i + "f %s", size, s.get(i));
	}
	
	
	/*
	 * FileUtils.recursiveDelete( loc )
	 * 
	 * IF loc is of type FILE
	 * 		loc will be deleted
	 * 
	 * IF loc is of type DIRECTORY
	 * 		loc and all subdirectories and files of loc will be deleted
	 */
	public static boolean recursiveDelete( String loc )
	{
		return recursiveDelete(new File(loc));
	}
	public static boolean recursiveDelete( File loc )
	{
		if( loc.isDirectory() )
		{
			String[] children = loc.list();
			for( int i = 0; i < children.length; i++ )
			{
				recursiveDelete(new File(loc, children[i]));
			}
		}
		return loc.delete();
	}

	
	/*
	 * FileUtils.getNumberOfFilesInDirectory( dir )
	 * 
	 * Get the total number of files in a directory.
	 * If the directory is actually a file, the return value is 1.
	 */
	public static int getNumberOfFilesInDirectory(String dir)
	{
		return getNumberOfFilesInDirectory(new File(dir));
	}
	public static int getNumberOfFilesInDirectory(File dir)
	{
		if( !dir.exists() ) 		return 0;
		if( !dir.isDirectory() )	return 1;

		int ret = 0;
		String files[] = dir.list();
		for( String file : files )
			ret += getNumberOfFilesInDirectory(new File(dir, file));
		
		return ret;
	}


	/*
	 * FileUtils.copyWithInfo( source, destination, flags )
	 * 
	 * Will copy a file from the source location to the destination location.
	 * Stats can be tracked through the `info` object.
	 * 
	 * IF flags & MULTIPLE_FILES
	 * 		stats will be ( files moved / total files )
	 * 
	 * IF flags & SINGLE_FILE
	 * 		stats will be ( bytes moved / total bytes )
	 */
	public int copyWithInfo(String source, String destination, int flags) throws IOException, InterruptedException
	{
		return copyWithInfo(new File(source), new File(destination), flags);
	}
	public int copyWithInfo(File source, File destination, int flags) throws IOException, InterruptedException
	{
		FileInternalUtils fiu = new FileInternalUtils();
		fiu.status = COMPLETE;
		
		if( source.isDirectory() ) {
			if( !destination.exists() ) destination.mkdirs();
			
			String files[] = source.list();
			
			for( String file : files )
			{
				File srcFile = new File( source, file );
				File destFile = new File( destination, file );
				
				fiu.status &= copyWithInfo( srcFile, destFile, flags );
			}
			
		} else {
			
			InputStream in = new FileInputStream(source);
			OutputStream out = new FileOutputStream(destination, true);

			if( destination.exists() )
				if( ((flags & OVERWRITE) != 0 ) )
					destination.delete();
				else
					return FAILED;
			
			destination.createNewFile();
			
			fiu.status &= copyWithInfo(in, out, flags);
			if( ((flags & OPTION_MULTIPLE_FILES) != 0) && (_func != null) ) 	updateInfo(1, _func.info.max);
		}
		return fiu.status;
	}
	public int copyWithInfo(final InputStream in, final OutputStream out, final int flags) throws InterruptedException
	{
		final FileInternalUtils fiu = new FileInternalUtils();
		fiu.status = COMPLETE;
		
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					long length = 0;
					byte[] buffer = new byte[4*1024*1024];
					
					while (in.read(buffer) > 0)
					{
						length = buffer.length;
						out.write(buffer);
						out.flush();
						
						if( ((flags & OPTION_SINGLE_FILE) != 0) && (_func != null) ) 	updateInfo(length, _func.info.max);
					}

					if( ((flags & OPTION_SINGLE_FILE) != 0) && (_func != null) )		setInfo(_func.info.max, _func.info.max);
					
					if( callbacks != null && ((flags & OPTION_SINGLE_FILE) != 0) )
					{
						synchronized (callbacks) {
							for( int i = 0; i < callbacks.size(); i++ )
								callbacks.get(i).runCallback(null);
						}
					}
				} catch(IOException e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
					fiu.status = FAILED;
				} finally {
					try {
						if( in != null )	in.close();
						if( out != null )	out.close();
					} catch (IOException e) {
						TraceUtils.trace(TraceUtils.STDERR, e);
					}
				}
			}
		});
		t.start();
		t.join();
		return fiu.status;
	}
	
	public boolean addCallback(ICallback c)
	{
		return callbacks.add(c);
	}
	public boolean removeCallback(ICallback c)
	{
		return callbacks.remove(c);
	}
	public void removeAllCallbacks()
	{
		callbacks.clear();
	}
	
	public void addStatusListener(IUtils parent, IUtilsInfo func, String source, int flags)
	{
		addStatusListener(parent, func, new File(source), flags);
	}
	public void addStatusListener(IUtils parent, IUtilsInfo func, File source, int flags)
	{
		_func = func;
		_func.info.parent = parent;
		_func.info.min = 0;
		_func.info.cur = 0;
		if( (flags & OPTION_MULTIPLE_FILES) != 0 ) 		_func.info.max = getNumberOfFilesInDirectory(source);
		else if( (flags & OPTION_SINGLE_FILE) != 0 )	_func.info.max = (int) source.length();	// only up to 2GB
		_func.info.progress = 0;
	}
	public void removeStatusListener()
	{
		_func = null;
	}
	private void updateInfo(long cur, long max)
	{
		if( _func != null )
		{
			_func.info.cur += cur;
			_func.info.max = max;
			_func.info.progress = (int) (_func.info.cur * 100 / _func.info.max);
			_func.onProgressUpdate();
		}
	}
	private void setInfo(long cur, long max)
	{
		if( _func != null )
		{
			_func.info.cur = cur;
			_func.info.max = max;
			_func.info.progress = (int) (_func.info.cur * 100 / _func.info.max);
			_func.onProgressUpdate();
		}
	}
}

class FileInternalUtils
{
	int status;
}
