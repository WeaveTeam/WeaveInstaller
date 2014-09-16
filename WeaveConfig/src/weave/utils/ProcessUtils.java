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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import weave.async.IAsyncCallback;
import weave.includes.IUtils;

public class ProcessUtils implements IUtils
{
	protected static Runtime runtime = Runtime.getRuntime();
	protected static Process proccess = null;
	
	private static Thread currentThread = null;
	
	public ProcessUtils(){
	}

	@Override
	public String getID() {
		return "ProcessUtils";
	}
	
	
	public static List<String> runAndWait( List<String> cmds ) throws IOException, InterruptedException
	{
		String[] strList = cmds.toArray(new String[cmds.size()]);
		return runAndWait(strList);
	}
	public static List<String> runAndWait( final String cmds[] ) throws InterruptedException
	{
		ProcInternals proc = new ProcInternals();
		ProcessRunnable pr = new ProcessRunnable(cmds, proc); 
		currentThread = new Thread(pr);
		
//		System.out.println("Running query: " + Arrays.toString(cmds));
		
		currentThread.start();
		currentThread.join();

//		System.out.println("\tOutput: " + proc.output);
//		System.out.println("\tError: " + proc.error + "\n");
		
		return proc.output;
	}
	public static void stopWaiting()
	{
		if( currentThread.isAlive() )
		{
			currentThread.interrupt();
			currentThread = null;
		}
	}

	@Override
	public boolean addCallback(IAsyncCallback c) {
		return false;
	}
	@Override
	public boolean removeCallback(IAsyncCallback c) {
		return false;
	}
	@Override
	public void removeAllCallbacks() {
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
	private List<String> list = null;
	
	public ProcessStream(InputStream is, List<String> list)
	{
		this.is = is;
		this.list = list;
	}
	
	@Override
	public void run()
	{
		try {

			String line = "";
			reader = new BufferedReader(new InputStreamReader(is));
		
			while( (line = reader.readLine()) != null )
				list.add(line);

			reader.close();
			
		} catch (IOException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		}
	}
}

class ProcessRunnable extends ProcessUtils implements Runnable
{
	private String cmds[] = null;
	ProcInternals proc = null;
	
	public ProcessRunnable(String cmds[], ProcInternals proc)
	{
		this.cmds = cmds;
		this.proc = proc;
		this.proc.output = new ArrayList<String>();
		this.proc.error = new ArrayList<String>();
	}
	
	
	@Override
	public void run() {
		
		try {
			proccess = runtime.exec(cmds);

			ProcessStream outputStream = new ProcessStream(proccess.getInputStream(), proc.output);
			ProcessStream errorStream = new ProcessStream(proccess.getErrorStream(), proc.error);
			
			outputStream.start();
			errorStream.start();
			
			proccess.waitFor();
			
			proccess.getOutputStream().close();
			proccess.getInputStream().close();
			proccess.getErrorStream().close();
			proccess.destroy();
			
		} catch (InterruptedException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		} catch (IOException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		}
	}
}