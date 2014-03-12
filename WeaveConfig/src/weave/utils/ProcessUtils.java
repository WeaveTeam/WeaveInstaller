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

import weave.includes.IUtils;

public class ProcessUtils implements IUtils
{
	protected static Runtime runtime = Runtime.getRuntime();
	protected static Process proccess = null;
	
	protected static InputStream inStream;
	protected static InputStreamReader inStreamReader;
	protected static BufferedReader buffReader;
	
	private static Thread currentThread = null;
	
	public ProcessUtils(){
	}

	@Override
	public String getID() {
		return "ProcessUtils";
	}
	
	
	public static ArrayList<String> runAndWait( List<String> cmds ) throws IOException, InterruptedException
	{
		String[] strList = new String[cmds.size()];
		return runAndWait(strList);
	}
	public static ArrayList<String> runAndWait( final String cmds[] ) throws InterruptedException
	{
		ProcInternals proc = new ProcInternals();
		ProccessRunnable pr = new ProccessRunnable(cmds, proc); 
		currentThread = new Thread(pr);
		
		currentThread.start();
		currentThread.join();
		
		return proc.result;
	}
	public static void stopWaiting()
	{
		if( currentThread.isAlive() )
		{
			currentThread.interrupt();
			currentThread = null;
		}
	}
}

class ProcInternals
{
	public ArrayList<String> result = null;
}

class ProccessRunnable extends ProcessUtils implements Runnable
{
	private String cmds[] = null;
	ProcInternals proc = null;
	
	public ProccessRunnable(String cmds[], ProcInternals proc)
	{
		this.cmds = cmds;
		this.proc = proc;
		this.proc.result = new ArrayList<String>();
	}
	
	
	@Override
	public void run() {
		
		String line = "";
		
		try {
			proccess = runtime.exec(cmds);
			proccess.waitFor();

			inStream = proccess.getInputStream();
			inStreamReader = new InputStreamReader(inStream);
			buffReader = new BufferedReader(inStreamReader);
			
			while( (line = buffReader.readLine()) != null )
				proc.result.add(line);
			
			buffReader.close();
			
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