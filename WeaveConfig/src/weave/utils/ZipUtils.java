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

import static weave.utils.TraceUtils.STDERR;
import static weave.utils.TraceUtils.trace;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import weave.async.AsyncObserver;

public class ZipUtils extends TransferUtils
{
	public static final byte[] MAGIC_BYTES = { 'P', 'K', 0x3, 0x4 };
	
	/**
	 * Extract a zip file to the destination location.
	 * 
	 * @param zipFile The zip file to extract
	 * @param destination The location to extract the contents to
	 * @return The status code <code>COMPLETE, CANCELLED, FAILED</code>
	 * 
	 * @throws IOException 
	 * @throws ZipException 
	 * @throws InterruptedException 
	 */
	public static int extract( File zipFile, File destination ) throws ZipException, IOException, InterruptedException
	{
		return extract(zipFile, destination, NO_FLAGS);
	}
	
	/**
	 * Extract a zip file to the destination location.
	 * 
	 * @param zipFile The zip file to extract
	 * @param destination The location to extract the contents to
	 * @param flags Bits to specify how the observer status should be followed
	 * @return The status code <code>COMPLETE, CANCELLED, FAILED</code>
	 * 
	 * @throws IOException 
	 * @throws ZipException 
	 * @throws InterruptedException 
	 */
	public static int extract( File zipFile, File destination, int flags) throws ZipException, IOException, InterruptedException
	{
		return extract(zipFile, destination, flags, null);
	}

	/**
	 * Extract a zip file to the destination location.
	 * 
	 * @param zipFile The zip file to extract
	 * @param destination The location to extract the contents to
	 * @param flags Bits to specify how the observer status should be followed
	 * @param observer The async observer to watch the status
	 * @return The status code <code>COMPLETE, CANCELLED, FAILED</code>
	 * 
	 * @throws IOException 
	 * @throws ZipException 
	 * @throws InterruptedException 
	 */
	public static int extract( File zipFile, File destination, int flags, AsyncObserver observer) throws ZipException, IOException, InterruptedException
	{
		return extract(zipFile, destination, flags, observer, 0);
	}
	
	/**
	 * Extract a zip file to the destination location.
	 * 
	 * @param zipFile The zip file to extract
	 * @param destination The location to extract the contents to
	 * @param flags Bits to specify how the observer status should be followed
	 * @param observer The async observer to watch the status
	 * @param throttle The transfer limit of the operation
	 * @return The status code <code>COMPLETE, CANCELLED, FAILED</code>
	 * 
	 * @throws IOException 
	 * @throws ZipException 
	 * @throws InterruptedException 
	 */
	public static int extract( File zipFile, File destination, int flags, AsyncObserver observer, int throttle ) throws ZipException, IOException, InterruptedException
	{
		if( zipFile == null || destination == null )
			throw new NullPointerException("Zip File or Destination File is null");
		
		assert zipFile != null;
		assert destination != null;
		
		if( destination.exists() && (flags & OVERWRITE) == 0 )
			throw new FileAlreadyExistsException(destination.getAbsolutePath());
		
		int result = COMPLETE;
		ZipFile zip = new ZipFile(zipFile);
		Enumeration<? extends ZipEntry> enu = zip.entries();
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
			
			InputStream is = zip.getInputStream(zipEntry);
			FileOutputStream fos = new FileOutputStream(outputFile);
			result &= FileUtils.copy(is, fos, observer, throttle);
		}
		zip.close();
		
		return result;
	}

	public static List<String> getZipEntries(File zipFile) throws ZipException, IOException
	{
		if( zipFile == null )
			throw new NullPointerException("Zip File is null");
		
		assert zipFile != null;
		
		List<String> fileList = new ArrayList<String>();
		ZipFile zip = new ZipFile(zipFile);
		Enumeration<? extends ZipEntry> enu = zip.entries();
		ZipEntry entry = null;
		
		while(enu.hasMoreElements())
		{
			entry = (ZipEntry) enu.nextElement();
			fileList.add(entry.getName());
		}
		zip.close();
		System.out.println("fileList: " + fileList);
		
		return fileList;
	}

	/**
	 * Check how many entries are in a zip file.<br>
	 * If the zip file provided doesn't exist or is not a file,
	 * the return value is 0.
	 * 
	 * @param zip The zip file to check
	 * @return The number of entries in the zip
	 */
	public static int getNumberOfEntriesInZip( String zip )
	{
		return getNumberOfEntriesInZip(new File(zip));
	}
	
	/**
	 * Check how many entries are in a zip file.<br>
	 * If the zip file provided doesn't exist or is not a file,
	 * the return value is 0.
	 * 
	 * @param zip The zip file to check
	 * @return The number of entries in the zip
	 */
	public static int getNumberOfEntriesInZip( File zip )
	{
		if( zip == null )
			throw new NullPointerException("Zip File is null");
		
		assert zip != null;
		
		if( !zip.exists() )	return 0;
		if( !zip.isFile() )	return 0;
		
		try {
			ZipFile z = new ZipFile(zip);
			int size = z.size(); 
			z.close();
			return size;
		} catch (ZipException e) {
			trace(STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		} catch (IOException e) {
			trace(STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		}
		
		return 0;
	}
	
	/**
	 * Check to see if the file is a zip file.<br><br>
	 * The ZIP file format can be determined by detecting  
	 * {@link #MAGIC_BYTES} at the start of the zip file.
	 * 
	 * @param zip The file to check
	 * @return <code>true</code> if the file is a zip file, <code>false</code> otherwise
	 * @throws IOException
	 */
	public static boolean isZip(File zip) throws IOException
	{
		byte[] buffer = new byte[MAGIC_BYTES.length];
		
		if( zip == null )
			throw new NullPointerException("Zip file is null");
		
		assert zip != null;
		
		// If the file is a directory, it surely isn't a zip
		if( zip.isDirectory() )
			return false;
		
		InputStream in = new DataInputStream(new FileInputStream(zip));
		in.read(buffer);
		in.close();
		
		for( int i = 0; i < buffer.length; i++ )
			if( buffer[i] != MAGIC_BYTES[i] )
				return false;
		return true;
	}

	/**
	 * Get the uncompressed size of a zip file.
	 * 
	 * @param zip The zip file to get the size of
	 * @return The uncompressed size of the zip
	 * 
	 * @throws ZipException
	 * @throws IOException
	 */
	public static long getUncompressedSize(String zip) throws ZipException, IOException
	{
		return getUncompressedSize(new File(zip));
	}
	
	/**
	 * Get the uncompressed size of a zip file.
	 * 
	 * @param zip The zip file to get the size of
	 * @return The uncompressed size of the zip
	 * 
	 * @throws ZipException
	 * @throws IOException
	 */
	public static long getUncompressedSize(File zip) throws ZipException, IOException
	{
		if( zip == null )
			throw new NullPointerException("Zip file is null");
		
		assert zip != null;
		
		if( !zip.exists() ) 
			throw new FileNotFoundException();
		if( !zip.isFile() )
			return 0L;
		
		long length = 0L;
		ZipFile z = new ZipFile(zip);
		Enumeration<?> zipEnum = z.entries();
		ZipEntry entry = null;
		
		while(zipEnum.hasMoreElements())
		{
			entry = (ZipEntry)zipEnum.nextElement();
			length += entry.getSize();
		}
		z.close();
		
		return length;
	}
}
