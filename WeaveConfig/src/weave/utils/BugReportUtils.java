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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

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
		
		brw.addWindowListener(new WindowListener() {
			@Override public void windowOpened(WindowEvent arg0) { }
			@Override public void windowIconified(WindowEvent arg0) { }
			@Override public void windowDeiconified(WindowEvent arg0) { }
			@Override public void windowDeactivated(WindowEvent arg0) {	}
			@Override public void windowClosing(WindowEvent arg0) {
				trace(STDOUT, "-> Should send bug report?........");
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
		trace(STDOUT, "-> Sending Bug report.............");

		try {
			String stack = getStackTrace(e);
			Map<String, String> map = new HashMap<String, String>();
			map.put("os", Settings.getExactOS());
			map.put("updr_ver", Settings.UPDATER_VER);
			map.put("instll_ver", Settings.SERVER_VER);
			map.put("comment", comment);
			map.put("stack", stack);
			
			JSONObject json = new JSONObject(map);
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(Settings.API_BUG_REPORT);
			System.out.println(json.toString());
	
			StringEntity params = new StringEntity("json="+json.toString());
			post.setHeader("Content-Type", "application/x-www-form-urlencoded");
			post.setEntity(params);
			
			HttpResponse response = client.execute(post);
			InputStream is = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			
			String line = "";
			while( (line = br.readLine()) != null )
				sb.append(line + Settings.N_L);
			
//			System.out.println("");
//			System.out.println(sb.toString());
//			System.out.println("");
			
		} catch (ClientProtocolException e1) {	
			trace(STDERR, e1);
			put(STDOUT, "FAILED");
		} catch (IOException e1) {
			trace(STDERR, e1);
			put(STDOUT, "FAILED");
		}
		
		put(STDOUT, "SENT");
	}
}
