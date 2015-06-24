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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import weave.Globals;
import weave.Settings;

public class TraceUtils extends Globals
{
	public static int STDOUT = 0;
	public static int STDERR = 1;
	
	private static Date d = new Date();
	private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	private static DateFormat tf = new SimpleDateFormat("[hh:mm:ss.SSS a]");
	
	private static ArrayList<String> pipes = new ArrayList<String>( Arrays.asList("stdout", "stderr") );
	
	public static String getSimpleClassAndMsg( Throwable e )
	{
		return e.getClass().getSimpleName() + ": " + e.getLocalizedMessage();
	}
	
	public static String getStackTrace( Throwable e )
	{
		String dump = "";
		StackTraceElement[] stack = e.getStackTrace();
		
		dump += ( e.toString() + Settings.N_L );
		
		for( int i = 0; i < stack.length; i++ )
			dump += ("\tat " + stack[i].toString() + Settings.N_L );
		
		return dump;
	}

	synchronized public static boolean trace( int pipe, Throwable e )
	{
		String dump = getStackTrace(e);
		return traceln(pipe, dump);
	}

	synchronized public static boolean trace( int pipe, String dump )
	{
		try {
			d = new Date();
			File logFile = getLogFile(pipe);
			FileWriter fw = new FileWriter(logFile, true);
			BufferedWriter bw = new BufferedWriter(fw);
		
			System.out.printf("\n" + dump);
			
			if( !logFile.getParentFile().exists() )		logFile.getParentFile().mkdirs();
			if( !logFile.exists() )						logFile.createNewFile();
			
			bw.write(Settings.N_L);
			bw.write(tf.format(d) + " " + dump);
			bw.flush();
			bw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	synchronized public static boolean traceln( int pipe, List<String> dump)
	{
		try {
			d = new Date();
			File logFile = getLogFile(pipe);
			FileWriter fw = new FileWriter(logFile, true);
			BufferedWriter bw = new BufferedWriter(fw);
	
			if( !logFile.getParentFile().exists() )	logFile.getParentFile().mkdirs();
			if( !logFile.exists() )					logFile.createNewFile();
			
			for( String line : dump )
			{
				bw.write(Settings.N_L);
				bw.write(tf.format(d) + " " + line);
			}
			bw.flush();
			bw.close();
		
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	synchronized public static boolean traceln( int pipe, String dump )
	{
		try {
			d = new Date();
			File logFile = getLogFile(pipe);
			FileWriter fw = new FileWriter(logFile, true);
			BufferedWriter bw = new BufferedWriter(fw);
		
			System.out.printf("\n" + dump);

			if( !logFile.getParentFile().exists() )		logFile.getParentFile().mkdirs();
			if( !logFile.exists() )						logFile.createNewFile();
			
			bw.write(Settings.N_L);
			bw.write(tf.format(d) + " " + dump);
			bw.flush();
			bw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	synchronized public static boolean put( int pipe, String dump )
	{
		try {
			d = new Date();
			File logFile = getLogFile(pipe);
			FileWriter fw = new FileWriter(logFile, true);
			BufferedWriter bw = new BufferedWriter(fw);
		
			System.out.printf(dump);

			if( !logFile.getParentFile().exists() )		logFile.getParentFile().mkdirs();
			if( !logFile.exists() )						logFile.createNewFile();
			
			bw.write(dump);
			bw.flush();
			bw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static File getLogFile( int pipe )
	{
		return new File(Settings.LOGS_DIRECTORY, pipes.get(pipe) + "." + df.format(d) + ".log" );
	}
}
