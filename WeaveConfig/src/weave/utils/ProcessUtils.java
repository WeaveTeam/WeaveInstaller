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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weave.Globals;

public class ProcessUtils extends Globals
{
	protected static Runtime runtime = Runtime.getRuntime();
	protected static Process proccess = null;
	
	public static Map<String, List<String>> run(List<String> cmds) throws IOException, InterruptedException
	{
		String[] strList = cmds.toArray(new String[cmds.size()]);
		return run(strList);
	}
	public static Map<String, List<String>> run(String cmds[]) throws IOException, InterruptedException
	{
		return run(cmds, (File)null, (File)null);
	}
	public static Map<String, List<String>> run(String cmds[], String stdout, String stderr) throws IOException, InterruptedException
	{
		return run(cmds, new File(stdout), new File(stderr));
	}
	public static Map<String, List<String>> run(String cmds[], File stdout, File stderr) throws IOException, InterruptedException
	{
		Map<String, List<String>> returnMap = new HashMap<String, List<String>>();
		ProcInternals internals = new ProcInternals();
		internals.output = new ArrayList<String>();
		internals.error = new ArrayList<String>();
		
		if( cmds == null )
			throw new NullPointerException("Runtime cannot run NULL commands.");
		
		proccess = runtime.exec(cmds);

		ProcessStream outputStream = new ProcessStream(proccess.getInputStream(), internals.output, stdout);
		ProcessStream errorStream = new ProcessStream(proccess.getErrorStream(), internals.error, stderr);
		
		outputStream.start();
		errorStream.start();
		
		proccess.waitFor();
		
		returnMap.put("output", internals.output);
		returnMap.put("error", internals.error);
		
		return returnMap;
	}
}

class ProcInternals
{
	public List<String> output = null;
	public List<String> error = null;
}

class ProcessStream extends Thread
{
	private InputStream is = null;
	private BufferedReader reader = null;
	private BufferedWriter writer = null;
	private List<String> list = null;
	private File output = null;
	
	public ProcessStream(InputStream is, List<String> list)
	{
		this(is, list, null);
	}
	
	public ProcessStream(InputStream is, List<String> list, File output)
	{
		this.is = is;
		this.list = list;
		this.output = output;
	}
	
	@Override
	public void run()
	{
		try {

			String line = "";
			reader = new BufferedReader(new InputStreamReader(is));
			if( output != null ) {
				writer = new BufferedWriter(new FileWriter(output, true));
				writer.newLine();
			}
		
			while( (line = reader.readLine()) != null ) {
//				System.out.println(line);
				list.add(line);
				if( writer != null ) {
					writer.write(line);
					writer.newLine();
					writer.flush();
				}
			}
		} catch (IOException e) {
			trace(STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		} finally {
			try {
				if( reader != null ) 	reader.close();
				if( writer != null )	writer.close();
			} catch (IOException e) {
			}
		}
	}
}
