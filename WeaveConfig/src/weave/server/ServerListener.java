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

package weave.server;

import static weave.utils.TraceUtils.STDERR;
import static weave.utils.TraceUtils.STDOUT;
import static weave.utils.TraceUtils.getSimpleClassAndMsg;
import static weave.utils.TraceUtils.put;
import static weave.utils.TraceUtils.trace;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JOptionPane;

import org.json.JSONArray;
import org.json.JSONObject;

import weave.Globals;
import weave.Settings;
import weave.reflect.ReflectionUtils;
import weave.utils.BugReportUtils;
import weave.utils.ObjectUtils;
import weave.utils.StringUtils;
import weave.utils.TraceUtils;

public class ServerListener extends Globals
{
	private int port = 0;
	
	private Socket csocket = null;
	private ServerSocket ssocket = null;
	
	private Thread loopThread = null;
	private ArrayList<ServerListenerThread> connections = null;
	
	public ServerListener(int port)
	{
		this.port = port;
	}
	
	public void start()
	{
		trace(STDOUT, StringUtils.rpad("-> Starting RPC Server", ".", Settings.LOG_PADDING_LENGTH));
		
		try {
			
			ssocket = new ServerSocket(port);
			connections = new ArrayList<ServerListener.ServerListenerThread>();
			
		} catch (BindException e) {
			put(STDOUT, "FAILED (" + getSimpleClassAndMsg(e) + ")");
			trace(STDERR, e);
			JOptionPane.showMessageDialog(null, 
					"Starting RPC server failed. Port already in use.\n\n" + 
					"Close any process running on port " + port + " and try again.",
					"Error", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			put(STDOUT, "FAILED (" + getSimpleClassAndMsg(e) + ")");
			trace(STDERR, e);
		}
		
		loopThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
				
					while( true ) {
						csocket = ssocket.accept();
						
						ServerListenerThread slt = new ServerListenerThread(csocket);
						connections.add(slt);
						slt.start();
					}

				} catch (SocketException e) {
					// DO NOTHING
				} catch (IOException e) {
					trace(STDERR, e);
				}
			}
		});
		loopThread.start();
		
		put(STDOUT, "DONE");
	}
	
	public void stop()
	{
		int i = 0;
		trace(STDOUT, StringUtils.rpad("-> Stopping RPC Server", ".", Settings.LOG_PADDING_LENGTH));
		
		try {
			while( !connections.isEmpty() )
			{
				i++;
				ServerListenerThread slt = connections.get(0);
				slt.interrupt();
				slt.close();
			}
			
			if( ssocket != null ) ssocket.close();

		} catch (IOException e) {
			put(STDOUT, "FAILED (" + getSimpleClassAndMsg(e) + ")");
			trace(STDERR, e);
		}
		
		loopThread.interrupt();
		ssocket = null;
		loopThread = null;
		put(STDOUT, "DONE (" + i + " remaining connections killed)");
	}
	
	
	
	
	
	class ServerListenerThread extends Thread implements Runnable
	{
		private Socket clientSock = null;
		private BufferedReader in = null;
		private BufferedWriter out = null;
		
		public ServerListenerThread(Socket s)
		{
			clientSock = s;

			trace(STDOUT, "-> Incomming socket connection from " + clientSock.getRemoteSocketAddress().toString().substring(1));
			
			try {
				in = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
				out = new BufferedWriter(new OutputStreamWriter(clientSock.getOutputStream()));
			} catch (IOException e) {
				trace(STDERR, e);
				BugReportUtils.showBugReportDialog(e);
			}
		}
		
		public void close()
		{
			trace(STDOUT, "-> Closing socket connection from " + clientSock.getRemoteSocketAddress());

			try {
				if( in != null ) in.close();
				if( out != null ) out.close();
				clientSock.close();
				connections.remove(this);
			} catch (IOException e) {
			}
		}
		
		@Override
		public void run() 
		{
			try {
				int i = 0;
				String line = in.readLine();
				JSONObject queryObj = new JSONObject(line);
				JSONArray jsonSigs = null;
				JSONArray jsonArgs = null;
				
				String pkg = queryObj.getString("package");
				String clzz = queryObj.getString("class");
				String call = queryObj.getString("call");
				if( queryObj.has("signature") )
					jsonSigs = queryObj.getJSONArray("signature");
				if( queryObj.has("args") )
					jsonArgs = queryObj.getJSONArray("args");

				Class<?>[] sigs = null;
				Object[] args = null;
				
				if( jsonSigs != null ) {
					sigs = new Class<?>[jsonSigs.length()];
					for( i = 0; i < jsonSigs.length(); i++ )
						sigs[i] = Class.forName((String) jsonSigs.get(i));
				}
				if( jsonArgs != null ) {
					args = new Object[jsonArgs.length()];
					for( i = 0; i < jsonArgs.length(); i++ ) {
						if( jsonArgs.get(i) instanceof String ) {
							if( jsonArgs.getString(i).contains("new") )
								args[i] = sigs[i].newInstance();
							else
								args[i] = jsonArgs.get(i);
						} else
							args[i] = jsonArgs.get(i);
					}
				}
				
//				System.out.println("pkg: " + pkg);
//				System.out.println("clzz: " + clzz);
//				System.out.println("func: " + func);
//				System.out.println("sigs: " + Arrays.toString(sigs));
//				System.out.println("args: " + Arrays.toString(args));
				
				Object o = null;
				
				if( sigs != null && args != null )
					o = ReflectionUtils.reflectMethod(pkg, clzz, call, sigs, args);
				else
					o = ReflectionUtils.reflectField(pkg, clzz, call);

				if( o == null )						out.write( "NULL" );
				else if( o instanceof String )		out.write( (String)ObjectUtils.ternary(o, "NULL") );
				else if( o instanceof Integer || 
						 o instanceof Double || 
						 o instanceof Float )		out.write( "" + ObjectUtils.ternary(o, 0) );
				else if( o instanceof Boolean )		out.write( "" + ObjectUtils.ternary(o, "FALSE") );
				else if( o instanceof Map<?, ?> )	out.write( ObjectUtils.toString(o) );
				else								out.write( "No case for type: " + o.getClass().getSimpleName() );
				out.newLine();
				out.flush();

			} catch (Exception e) {
				trace(STDERR, e);
				try {
					out.write(TraceUtils.getStackTrace(e));
					out.newLine();
					out.flush();
				} catch (IOException e1) {
					trace(STDERR, e1);
				}
			}
			close();
		}
	}
}
