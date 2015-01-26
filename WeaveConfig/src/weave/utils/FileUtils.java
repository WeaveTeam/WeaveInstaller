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

package weave.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.util.Arrays;
import java.util.List;

import weave.Settings;
import weave.async.AsyncObserver;
import weave.async.AsyncTask;
import weave.reflect.Reflectable;

public class FileUtils extends TransferUtils
{
	private static final int BUFFER_SIZE			= 8 * TransferUtils.KB;
	
	
	/**
	 * Copy the source file to the destination file
	 * <br><br>
	 * Example Usage:
	 * <code>
	 * <pre>
	 * 	File a = new File( "path/to/file", name );
	 * 	File b = new File( "other/path/to/file", name );
	 * 
	 * 	FileUtils.copy( a, b );
	 * </pre>
	 * </code>
	 * 
	 * @param source The source file
	 * @param destination The destination file
	 * @return The result status <code>COMPLETE, CANCELLED, FAILED</code>
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static int copy(File source, File destination) throws FileNotFoundException, IOException, InterruptedException
	{
		return copy(source, destination, NO_FLAGS);
	}

	
	
	/**
	 * Copy the source file to the destination file
	 * <br><br>
	 * Example Usage:
	 * <code>
	 * <pre>
	 * 	File a = new File( "path/to/file", name );
	 * 	File b = new File( "other/path/to/file", name );
	 * 
	 * 	FileUtils.copy( a, b, {@link FileUtils#SINGLE_FILE} | {@link FileUtils#OVERWRITE} );
	 * </pre>
	 * </code>
	 * 
	 * @param source The source file
	 * @param destination The destination file
	 * @param flags Class flags <code>OVERWRITE, OPTION_SINGLE_FILE, OPTION_MULTIPLE_FILES</code>
	 * @return The result status <code>COMPLETE, CANCELLED, FAILED</code>
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static int copy(File source, File destination, int flags) throws FileNotFoundException, IOException, InterruptedException
	{
		return copy(source, destination, flags, null);
	}
	
	
	
	/**
	 * Copy the source file to the destination file
	 * <br><br>
	 * Example Usage:
	 * <code>
	 * <pre>
	 * 	final File a = new File( src, file );
	 * 	final File b = new File( dest, file );
	 * 
	 * 	{@link AsyncObserver} observer = new AsyncObserver() {
	 * 		public void onUpdate() {
	 * 			// RUN UPDATE CODE HERE
	 * 			progressBar.setProgress( info.progress );
	 * 		}
	 * 	};
	 * 	IAsyncCallback callback = new IAsyncCallback() {
	 * 		public void run(Object o) {
	 * 			// RUN CALLBACK CODE HERE
	 * 			//
	 * 			// The result Object o will be the return value
	 * 			// from the call to {@link AsyncTask#doInBackground()} 
	 * 		}
	 * 	};
	 * 	{@link AsyncTask} task = new AsyncTask() {
	 * 		public Object doInBackground() {
	 * 			return FileUtils.copy(a, b, {@link FileUtils#OVERWRITE} | {@link FileUtils#SINGLE_FILE}, observer);
	 * 		}
	 * 	};
	 * 	task.addCallback( callback );
	 * 	task.execute();
	 * </pre>
	 * </code>
	 * 
	 * @param source The source file
	 * @param destination The destination file
	 * @param flags Class flags <code>OVERWRITE, OPTION_SINGLE_FILE, OPTION_MULTIPLE_FILES</code>
	 * @param observer The observer to provide stats to
	 * @return The result status <code>COMPLETE, CANCELLED, FAILED</code>
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static int copy(File source, File destination, int flags, AsyncObserver observer) throws FileNotFoundException, IOException, InterruptedException
	{
		return copy(source, destination, flags, observer, 0);
	}
	
	
	
	/**
	 * Copy the source file to the destination file.
	 * <br><br>
	 * Example Usage:
	 * <code>
	 * <pre>
	 * 	final File a = new File( src, file );
	 * 	final File b = new File( dest, file );
	 * 
	 * 	AsyncObserver observer = new AsyncObserver() {
	 * 		public void onUpdate() {
	 * 			// RUN UPDATE CODE HERE
	 * 			progressBar.setProgress( info.progress );
	 * 		}
	 * 	};
	 * 	IAsyncCallback callback = new IAsyncCallback() {
	 * 		public void run(Object o) {
	 * 			// RUN CALLBACK CODE HERE
	 * 			//
	 * 			// The result Object o will be the return value
	 * 			// from the call to {@link AsyncTask#doInBackground()} 
	 * 		}
	 * 	};
	 * 	AsyncTask task = new AsyncTask() {
	 * 		public Object doInBackground() {
	 * 			return FileUtils.copy(a, b, {@link FileUtils#OVERWRITE} | {@link FileUtils#SINGLE_FILE}, observer, 2 * {@link FileUtils#MB});
	 * 		}
	 * 	};
	 * 	task.addCallback( callback );
	 * 	task.execute();
	 * </pre>
	 * </code>
	 * 
	 * @param source The source file
	 * @param destination The destination file
	 * @param flags Class flags <code>OVERWRITE, OPTION_SINGLE_FILE, OPTION_MULTIPLE_FILES</code>
	 * @param observer The observer to provide stats to
	 * @param throttle The max transfer speed
	 * @return The result status <code>COMPLETE, CANCELLED, FAILED</code>
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static int copy(File source, File destination, int flags, AsyncObserver observer, int throttle) throws FileNotFoundException, IOException, InterruptedException
	{
		long timestamp = 0;
		
		// Check to see if either of the file(s) are null and throw an error if they are
		if( source == null || destination == null )
			throw new NullPointerException("Source File or Destination File cannot be null");
		
		// If no error was thrown, we can then assert that both files are non-null
		assert source != null;
		assert destination != null;

		// We need to check to see if the destination file already exists and
		// to see if the OVERWRITE flag bit was passed.
		// If it exists, and OVERWRITE bit wasn't passed we need to throw an exception
		if( destination.exists() && (flags & OVERWRITE) == 0 )
			throw new FileAlreadyExistsException("Overwrite bit not set for: " + destination.getAbsolutePath());
		
		int status = COMPLETE;
		
		if( source.isDirectory() ) 
		{
			destination.mkdirs();
			
			String files[] = source.list();
			for( String file : files )
				status &= copy(new File(source, file), new File(destination, file), flags, observer, throttle);
		}
		else
		{
			if( (flags & PRESERVE) > 0 ) timestamp = source.lastModified();
			
			status &= copy(new FileInputStream(source), new FileOutputStream(destination), observer, throttle);
			
			if( (flags & PRESERVE) > 0 ) destination.setLastModified(timestamp);
		}
		return status;
	}

	
	
	/**
	 * Copy the contents of the InputStream to the OutputStream
	 * 
	 * @param in The InputStream to read from
	 * @param out The OutputStream to write to
	 * @param flags Class flags <code>OVERWRITE, OPTION_SINGLE_FILE, OPTION_MULTIPLE_FILES</code>
	 * @return The result status <code>COMPLETE, CANCELLED, FAILED</code>
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static int copy(InputStream in, OutputStream out) throws IOException, InterruptedException
	{
		return copy(in, out, null);
	}
	
	
	
	/**
	 * Copy the contents of the InputStream to the OutputStream
	 * 
	 * @param in The InputStream to read from
	 * @param out The OutputStream to write to
	 * @param flags Class flags <code>OVERWRITE, OPTION_SINGLE_FILE, OPTION_MULTIPLE_FILES</code>
	 * @param observer The observer to provide stats to
	 * @return The result status <code>COMPLETE, CANCELLED, FAILED</code>
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static int copy(InputStream in, OutputStream out, AsyncObserver observer) throws IOException, InterruptedException
	{
		return copy(in, out, observer, 0);
	}
	
	
	
	/**
	 * Copy the contents of the InputStream to the OutputStream
	 * 
	 * @param in The InputStream to read from
	 * @param out The OutputStream to write to
	 * @param flags Class flags <code>OVERWRITE, OPTION_SINGLE_FILE, OPTION_MULTIPLE_FILES</code>
	 * @param observer The observer to provide stats to
	 * @param throttle The max transfer speed
	 * @return The result status <code>COMPLETE, CANCELLED, FAILED</code>
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static int copy(InputStream in, OutputStream out, AsyncObserver observer, int throttle) throws IOException, InterruptedException
	{
		int n;
		int limit = 0, bps = 0, seconds = 0, aveSpeed = 1;
		long size = 0L;
		long speedLongNew 	= 0L, speedLongOld = System.currentTimeMillis();
		long cancelLongNew 	= 0L, cancelLongOld = System.currentTimeMillis();
		byte[] buf = new byte[BUFFER_SIZE];
		
		
		// Check to see if either the input or output stream(s) are null and throw an error if they are
		if( in == null || out == null )
			throw new NullPointerException("InputStream or OutputStream cannot be null");
		
		// If no error is thrown, we can then assert that the streams are non-null.
		assert in != null;
		assert out != null;
		
		
		while( (n = in.read(buf)) > 0 ) 
		{
			out.write(buf, 0, n);
			size += n;
			bps += n;
			speedLongNew = System.currentTimeMillis();
			cancelLongNew = System.currentTimeMillis();
			
			
			// If an observer has been supplied to this function we want to 
			// track the progress of this operation and provide feedback
			// to the observer
			if( observer != null ) {
				if( speedLongNew - speedLongOld > 1000 ) {
					observer.info.speed = bps;
					bps = 0;
					seconds++;
					speedLongOld = speedLongNew;
					aveSpeed = (int) (size / seconds); 
				}
				observer.info.cur += n;
				observer.info.percent = (int) (observer.info.cur * 100 / observer.info.max);
				observer.info.time = (int) ((observer.info.max - size) / aveSpeed);
				observer.onUpdate();
			}
			
			
			// Check every so often to see if the global cancel variable
			// has been changed, indicating that the transfer should be 
			// canceled.
			if( cancelLongNew - cancelLongOld > 200 ) {
				cancelLongOld = cancelLongNew;
				if( Settings.transferCancelled == true ) {
					in.close();
					out.close();
					return CANCELLED;
				}
			}
			
			
			// This is an optional argument that allows the transfer to be
			// throttled to a specific speed. 
			// NOTE: this should only be used for testing or to prevent UI bugs.
			if( throttle > 0 ) {
				limit += n;
				if( limit >= ( throttle / 10 )) {
					limit = 0;
					Thread.sleep(100);
				}
			}
		}
		out.flush();
		in.close();
		out.close();
		return COMPLETE;
	}
	

	/**
	 * Move or rename the source file to the destination file.
	 * Default operation is to not overwrite the destination file if it already exists.
	 * 
	 * @param source The source file to move
	 * @param destination The destination file of where the source will be moved to
	 * @return The result status <code>COMPLETE, CANCELLED, FAILED</code>
	 * 
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public static int move( String source, String destination ) throws IOException, InterruptedException
	{
		return move(source, destination, NO_FLAGS);
	}
	
	
	
	/**
	 * Move or rename the source file to the destination file.
	 * Default operation is to not overwrite the destination file if it already exists.
	 * 
	 * @param source The source file to move
	 * @param destination The destination file of where the source will be moved to
	 * @return The result status <code>COMPLETE, CANCELLED, FAILED</code>
	 * 
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public static int move( File source, File destination ) throws IOException, InterruptedException
	{
		return move(source, destination, NO_FLAGS);
	}
	
	
	
	/**
	 * Move or rename the source file to the destination file.
	 * 
	 * @param source The source file to move
	 * @param destination The destination file of where the source will be moved to
	 * @param flags Big flags to specify if the file should be overwritten.
	 * @return The result status <code>COMPLETE, CANCELLED, FAILED</code>
	 * 
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public static int move( String source, String destination, int flags ) throws IOException, InterruptedException
	{
		return move(new File(source), new File(destination), flags);
	}
	
	
	
	/**
	 * Move or rename the source file to the destination file.
	 * 
	 * @param source The source file to move
	 * @param destination The destination file
	 * @param flags Bit flags {@link TransferUtils#NO_FLAGS}, {@link TransferUtils#OVERWRITE}, {@link TransferUtils#PRESERVE}
	 * @return The result status <code>COMPLETE, CANCELLED, FAILED</code>
	 * 
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public static int move( File source, File destination, int flags ) throws FileNotFoundException, IOException, InterruptedException
	{
		return move(source, destination, flags, null);
	}
	
	
	/**
	 * Move or rename the source file to the destination file.
	 * 
	 * @param source The source file
	 * @param destination The destination file
	 * @param flags Bit flags {@link TransferUtils#NO_FLAGS}, {@link TransferUtils#OVERWRITE}, {@link TransferUtils#PRESERVE}
	 * @param observer The observer to provide stats to
	 * @return Returns the result code <code>COMPLETE, FAILED, CANCELLED</code>
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static int move( File source, File destination, int flags, AsyncObserver observer ) throws FileNotFoundException, IOException, InterruptedException
	{
		return move(source, destination, flags, observer, 0);
	}

	
	/**
	 * 
	 * @param source The source file
	 * @param destination The destination file
	 * @param flags Bit flags {@link TransferUtils#NO_FLAGS}, {@link TransferUtils#OVERWRITE}, {@link TransferUtils#PRESERVE}
	 * @param observer The observer to provide stats to
	 * @param throttle The max transfer speed
	 * @return The result code <code>COMPLETE, FAILED, CANCELLED</code>
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static int move( File source, File destination, int flags, AsyncObserver observer, int throttle ) throws FileNotFoundException, IOException, InterruptedException
	{
		int cp = copy(source, destination, flags, observer, throttle);
		recursiveDelete(source);
		
		return cp;
	}
	

	/**
	 * Get the extension of a file
	 * <br><br>
	 * Example Usage:
	 * <code>
	 * <pre>
	 * Windows:
	 * 	File f1 = new File("C:/Program Files/SomeFileName.txt");
	 * 
	 * Unix:
	 * 	File f2 = new File("/users/lib/SomeOtherName.xml");
	 * 	File f3 = new File("/var/lib/files.var/README");
	 * 	
	 * 	FileUtils.getExt( f1 )	=	"txt"
	 * 	FileUtils.getExt( f2 )	= 	"xml"
	 * 	FileUtils.getExt( f3 )	=	""
	 * </pre>
	 * </code>
	 * 
	 * @param f The file
	 * @return The extension of the file
	 */
	public static String getExt(File f)
	{
		return getExt(f.getAbsolutePath());
	}
	
	
	
	/**
	 * Get the extension of a file
	 * <br><br>
	 * Example Usage:
	 * <code>
	 * <pre>
	 * 	String str1 = "C:/Program Files/SomeFileName.txt";
	 * 	String str2 = "/users/lib/SomeOtherName.xml";
	 * 	String str3 = "/var/lib/files.var/README";
	 * 	
	 * 	FileUtils.getExt( str1 )	=	"txt"
	 * 	FileUtils.getExt( str2 )	= 	"xml"
	 * 	FileUtils.getExt( str3 )	=	""
	 * </pre>
	 * </code>
	 * 
	 * @param s The string path of the file
	 * @return The extension of the file
	 */
	public static String getExt(String s)
	{
		int idx1 = s.lastIndexOf("/");
		int idx2 = s.lastIndexOf(".");
		if( idx2 > idx1 )
			return s.substring( idx2 + 1 );
		
		// The file does not have an extension
		return "";
	}

	

	/**
	 * Get the entire file contents of a file as a string.
	 * 
	 * @param s The path to the file
	 * @return The file contents
	 * 
	 * @throws IOException
	 */
	public static String getFileContents(String s) throws IOException
	{
		return getFileContents(new File(s));
	}
	
	
	
	/**
	 * Get the entire file contents of a file as a string.
	 * 
	 * @param f The file
	 * @return The file contents
	 * 
	 * @throws IOException
	 */
	public static String getFileContents(File f) throws IOException
	{
		return getFileContents(new FileInputStream(f));
	}
	
	
	/**
	 * Get the entire file contents of a file as a string.
	 * 
	 * @param in The input stream to the file
	 * @return The file contents
	 * 
	 * @throws IOException
	 */
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
	

	
	/**
	 * Get a human readable size of a value.
	 * 
	 * @param size The value to convert
	 * @return The size as a human readable string
	 */
	public static String sizeify( long size )
	{
		return sizeify( (double)size );
	}
	

	
	/**
	 * Get a human readable size of a value.
	 * 
	 * @param size The value to convert
	 * @return The size as a human readable string
	 */
	public static String sizeify( int size )
	{
		return sizeify( (double)size );
	}
	
	

	/**
	 * Get a human readable size of a value.
	 * 
	 * @param size The value to convert
	 * @return The size as a human readable string
	 */
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
	
	@Reflectable
	public static boolean setReadable(String path, Boolean p, Boolean a)
	{
		return new File(path).setReadable(p, a);
	}
	@Reflectable
	public static boolean setWritable(String path, Boolean p, Boolean a)
	{
		return new File(path).setWritable(p, a);
	}
	@Reflectable
	public static boolean setExecutable(String path, Boolean p, Boolean a)
	{
		return new File(path).setExecutable(p, a);
	}
	
	@Reflectable
	public static boolean setPermissions(String path, Integer permissions) throws SecurityException
	{
		return setPermissions(new File(path), permissions);
	}
	@Reflectable
	public static boolean setPermissions(File file, Integer permissions) throws SecurityException
	{
		boolean perm = true;
		int u = permissions & 0xF00;
		int o = permissions & 0x00F;
		// Other
		if( o == 0x7 ) {
			perm &= file.setReadable(true, false);
			perm &= file.setWritable(true, false);
			perm &= file.setExecutable(true, false);
		} else if( o == 0x6 ) {
			perm &= file.setReadable(true, false);
			perm &= file.setWritable(true, false);
			perm &= file.setExecutable(false, false);
		} else if( o == 0x5 ) {
			perm &= file.setReadable(true, false);
			perm &= file.setWritable(false, false);
			perm &= file.setExecutable(true, false); 
		} else if( o == 0x4 ) {
			perm &= file.setReadable(true, false);
			perm &= file.setWritable(false, false);
			perm &= file.setExecutable(false, false); 
		} else if( o == 0x0 ) {
			perm &= file.setReadable(false, false);
			perm &= file.setWritable(false, false);
			perm &= file.setExecutable(false, false);
		}
		// User
		if( u == 0x700 ) {
			perm &= file.setReadable(true);
			perm &= file.setWritable(true);
			perm &= file.setExecutable(true);
		} else if( u == 0x600 ) {
			perm &= file.setReadable(true);
			perm &= file.setWritable(true);
			perm &= file.setExecutable(false);
		} else if( u == 0x500 ) {
			perm &= file.setReadable(true);
			perm &= file.setWritable(false);
			perm &= file.setExecutable(true);
		} else if( u == 0x400 ) {
			perm &= file.setReadable(true);
			perm &= file.setWritable(false);
			perm &= file.setExecutable(false);
		}
		
		return perm;
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

	public static File getPathRoot(String s)
	{
		return getPathRoot(new File(s));
	}
	public static File getPathRoot(File f)
	{
		File p = f;
		while( p.getParentFile() != null )
			p = p.getParentFile();
		return p;
	}
	
	public static long getSize(String file)
	{
		return getSize(new File(file));
	}
	public static long getSize(File file)
	{
		long length = 0L;
		
		if( file.isDirectory() )
			for( File f : file.listFiles() )
				length += getSize(f);
		else
			length += file.length();
		
		return length;
	}
}
