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
	/**
	 * Standard output is the stream where a program writes its output data.<br>
	 * In the case of this program, it will write the data to {@link System#out} as well as <br>
	 * the log file defined by {@link #getLogFile(STDOUT)}.
	 */
	public static int STDOUT = 1;
	
	/**
	 * Standard error is the stream typically used to output error messages or diagnostics.<br>
	 * It is independent of standard output and can be redirected separately.<br>
	 * In the case of this program, it will write the data to {@link System#err} as well as the log file <br>
	 * defined by {@link #getLogFile(STDERR)}.
	 */
	public static int STDERR = 2;
	
	/**
	 * Severity level of the log trace to indicate what kind of trace it is.<br>
	 * Possibilities:<br> 
	 * FATAL, ERROR, WARN, INFO, DEBUG
	 */
	public static enum LEVEL {
		FATAL("!!!"), ERROR(" !!"), WARN("/!\\"), INFO("-->"), DEBUG("/?\\");
		
		private final String value;
		private LEVEL(String s)
		{
			value = s;
		}
		@Override
		public String toString() {
			return value;
		}
	};
	
	private static String s = null;
	private static Date d = new Date();
	private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	private static DateFormat tf = new SimpleDateFormat("[hh:mm:ss.SSS a]");
	
	private static ArrayList<String> pipes = new ArrayList<String>( Arrays.asList("stdin", "stdout", "stderr") );
	
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

	/**
	 * Prints out the {@link Throwable} to the log file specified by {@code pipe}
	 * and also prints it out to the screen. 
	 * 
	 * @param pipe The log file pipe to print to. {@link #STDOUT} or {@link #STDERR}
	 * @param e The throwable exception
	 * @return <code>true</code> if the stack trace was written successfully, <code>false</code> otherwise
	 */
	synchronized public static boolean trace( int pipe, Throwable e )
	{
		String dump = getStackTrace(e);
		return traceln(pipe, LEVEL.ERROR, dump);
	}

	synchronized public static boolean trace( int pipe, LEVEL lvl, String dump )
	{
		try {
			d = new Date();
			File logFile = getLogFile(pipe);
			FileWriter fw = new FileWriter(logFile, true);
			BufferedWriter bw = new BufferedWriter(fw);
			
			if( !logFile.getParentFile().exists() )		logFile.getParentFile().mkdirs();
			if( !logFile.exists() )						logFile.createNewFile();
			
			s = Settings.N_L + tf.format(d) + " " + lvl + " " + dump;
			
			bw.write(s);
			bw.flush();
			bw.close();
			
			if( pipe == STDOUT )		System.out.printf(s);
			else if(pipe == STDERR )	System.err.printf(s);
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	synchronized public static boolean traceln( int pipe, LEVEL lvl, List<String> dump)
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
				s = Settings.N_L + tf.format(d) + " " + lvl + " " + line;
				bw.write(s);
				
				if( pipe == STDOUT )		System.out.println(s);
				else if( pipe == STDERR )	System.err.println(s);
			}
			bw.flush();
			bw.close();
		
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Append a string dump to the end of the log file specified by {@code pipe}.<br>
	 * This will prepend a date and timestamp before your string dump.
	 * 
	 * @param pipe The {@link #STDOUT} or {@link #STDERR} pipe
	 * @param lvl The severity level of dump defined by {@link #LEVEL}
	 * @param dump The string to append to the file
	 * @return {@code true} if succeeded, {@code false} otherwise
	 */
	synchronized public static boolean traceln( int pipe, LEVEL lvl, String dump )
	{
		try {
			d = new Date();
			File logFile = getLogFile(pipe);
			FileWriter fw = new FileWriter(logFile, true);
			BufferedWriter bw = new BufferedWriter(fw);

			if( !logFile.getParentFile().exists() )		logFile.getParentFile().mkdirs();
			if( !logFile.exists() )						logFile.createNewFile();
			
			s = Settings.N_L + tf.format(d) + " " + lvl + " " + dump; 
			
			bw.write(s);
			bw.flush();
			bw.close();
			
			if( pipe == STDOUT )		System.out.printf(s);
			else if( pipe == STDERR )	System.err.printf(s);
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Append a string to the end of the log file without any newlines.
	 * 
	 * @param pipe The {@link #STDOUT} or {@link #STDERR} pipe
	 * @param dump The string to append to the file
	 * @return {@code true} if succeeded, {@code false} otherwise
	 */
	synchronized public static boolean put( int pipe, String dump )
	{
		try {
			d = new Date();
			File logFile = getLogFile(pipe);
			FileWriter fw = new FileWriter(logFile, true);
			BufferedWriter bw = new BufferedWriter(fw);

			if( !logFile.getParentFile().exists() )		logFile.getParentFile().mkdirs();
			if( !logFile.exists() )						logFile.createNewFile();
			
			bw.write(dump);
			bw.flush();
			bw.close();
		
			if( pipe == STDOUT )		System.out.printf(dump);
			else if( pipe == STDERR )	System.err.printf(dump);
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Get the log file as a file that will be used for writing log information to.
	 * 
	 * @param pipe The {@link #STDOUT} or {@link #STDERR} pipe
	 * @return The log file associated with the {@code pipe}
	 */
	public static File getLogFile( int pipe )
	{
		return new File(Settings.LOGS_DIRECTORY, pipes.get(pipe) + "." + df.format(d) + ".log" );
	}
}
