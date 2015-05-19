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
import static weave.utils.TraceUtils.STDOUT;
import static weave.utils.TraceUtils.getLogFile;
import static weave.utils.TraceUtils.getStackTrace;
import static weave.utils.TraceUtils.put;
import static weave.utils.TraceUtils.trace;
import static weave.utils.TraceUtils.traceln;

import java.awt.HeadlessException;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import weave.Globals;
import weave.Settings;
import weave.ui.BugReportWindow;

public class BugReportUtils extends Globals
{
	public static void autoSubmitBugReport( final Throwable e )
	{
		traceln(STDOUT, "");
		traceln(STDOUT, "!! Bug detected !!");
		traceln(STDOUT, "!! Stack Trace in " + getLogFile(STDERR).getAbsolutePath() );
		traceln(STDOUT, "");

		submitReport(e, "");
	}
	
	public static void showBugReportDialog( final Throwable e ) throws HeadlessException
	{
		Settings.canQuit = false;
		
		final BugReportWindow brw = BugReportWindow.instance(e);
		traceln(STDOUT, "");
		traceln(STDOUT, "!! Bug detected !!");
		traceln(STDOUT, "!! Stack Trace in " + getLogFile(STDERR).getAbsolutePath() );
		traceln(STDOUT, "");
		
		for( WindowListener l : brw.getWindowListeners() )
			brw.removeWindowListener(l);
		
		brw.addWindowListener(new WindowListener() {
			@Override public void windowOpened(WindowEvent arg0) { }
			@Override public void windowIconified(WindowEvent arg0) { }
			@Override public void windowDeiconified(WindowEvent arg0) { }
			@Override public void windowDeactivated(WindowEvent arg0) {	}
			@Override public void windowClosing(WindowEvent arg0) {
				trace(STDOUT, StringUtils.rpad("-> Should send bug report?", ".", Settings.LOG_PADDING_LENGTH));
				if( brw.CLOSE_OPTION == BugReportWindow.YES_OPTION ) {
					put(STDOUT, "YES");
					
					String c = ( brw.data.comment.trim().equals(BugReportWindow.defaultComment) ? "" : brw.data.comment );
					submitReport( e, c );
				} else {
					put(STDOUT, "NO");
				}
				Settings.canQuit = true;
			}
			@Override public void windowClosed(WindowEvent arg0) { }
			@Override public void windowActivated(WindowEvent arg0) { }
		});
		brw.setVisible(true);
	}
	
	private static void submitReport(Throwable e, String comment)
	{
		trace(STDOUT, StringUtils.rpad("-> Sending Bug report", ".", Settings.LOG_PADDING_LENGTH));

		try {
			String stack = getStackTrace(e);
			
			URLRequestParams params = new URLRequestParams();
			params.add("os", Settings.getExactOS());
			params.add("updr_ver", Settings.UPDATER_VER);
			params.add("instll_ver", Settings.SERVER_VER);
			params.add("comment", comment);
			params.add("stack", stack);
			params.add("epoch", ""+(System.currentTimeMillis()/1000));
			
			URLRequestResult result = URLRequestUtils.request(URLRequestUtils.POST, Settings.API_BUG_REPORT, params);
			
			put(STDOUT, (result.getResponseContent().equals("1") ? "SUCCESSFUL" : "ERROR")
						+ " [ " + result.getResponseHeader(null) + " ]" 
						+ " : " + result.getResponseContent().replaceAll("\\<.*?>", ""));
			
		} catch (IOException e1) {
			trace(STDERR, e1);
			put(STDOUT, "FAILED");
		}
	}
}
