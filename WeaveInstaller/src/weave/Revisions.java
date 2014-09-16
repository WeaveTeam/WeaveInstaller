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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class Revisions 
{
	public static int getNumberOfRevisions()
	{
		if( !Settings.REVISIONS_DIRECTORY.exists() )
			return 0;

		return Settings.REVISIONS_DIRECTORY.list().length;
	}
	
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

	public static ArrayList<File> getRevisionsList()
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
	
	public static boolean pruneRevisions()
	{
		int i = 0;
		boolean ret = true;
		File file = null;
		ArrayList<File> files = getRevisionsList();
		Iterator<File> it = files.iterator();
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
			ret |= file.delete();
			i++;
		}
		return ret;
	}
	
	public static String getRevisionName(String s)
	{
		return s.substring(s.lastIndexOf('-')+1, s.lastIndexOf('.')).toUpperCase();
	}
}
