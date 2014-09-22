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

import javax.swing.JOptionPane;

import org.json.JSONArray;
import org.json.JSONObject;

import weave.utils.BugReportUtils;
import weave.utils.ObjectUtils;
import weave.utils.ReflectionUtils;
import weave.utils.TraceUtils;

public class ServerListener
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
		TraceUtils.trace(TraceUtils.STDOUT, "-> Starting RPC Server............");
		
		try {
			
			ssocket = new ServerSocket(port);
			connections = new ArrayList<ServerListener.ServerListenerThread>();
			
		} catch (BindException e) {
			TraceUtils.put(TraceUtils.STDOUT, "FAILED (Port already in use)");
			TraceUtils.trace(TraceUtils.STDERR, e);
			JOptionPane.showMessageDialog(null, 
					"Starting RPC server failed. Port already in use.\n\n" + 
					"Close any process running on port " + port + " and try again.",
					"Error", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			TraceUtils.put(TraceUtils.STDOUT, "FAILED");
			TraceUtils.trace(TraceUtils.STDERR, e);
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
					TraceUtils.trace(TraceUtils.STDERR, e);
				}
			}
		});
		loopThread.start();
		
		TraceUtils.put(TraceUtils.STDOUT, "DONE");
	}
	
	public void stop()
	{
		int i = 0;
		TraceUtils.trace(TraceUtils.STDOUT, "-> Stopping RPC Server............");
		
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
			TraceUtils.put(TraceUtils.STDOUT, "FAILED");
			TraceUtils.trace(TraceUtils.STDERR, e);
		}
		
		loopThread.interrupt();
		ssocket = null;
		loopThread = null;
		TraceUtils.put(TraceUtils.STDOUT, "DONE (" + i + " remaining connections killed)");
	}
	
	
	
	
	
	class ServerListenerThread extends Thread implements Runnable
	{
		private Socket clientSock = null;
		private BufferedReader in = null;
		private BufferedWriter out = null;
		
		public ServerListenerThread(Socket s)
		{
			clientSock = s;

			try {
				in = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
				out = new BufferedWriter(new OutputStreamWriter(clientSock.getOutputStream()));
			} catch (IOException e) {
				TraceUtils.trace(TraceUtils.STDERR, e);
				BugReportUtils.showBugReportDialog(e);
			}
		}
		
		public void close()
		{
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
				String func = queryObj.getString("function");
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
						if( jsonArgs.getString(i).contains("new") )
							args[i] = sigs[i].newInstance();
						else
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
				{
					// Run a function on an object
					o = ReflectionUtils.reflectMethod(pkg, clzz, func, sigs, args);
					if( o == null )
						out.write("NULL");
					else if( o instanceof String )
						out.write( (String)ObjectUtils.ternary(o, "NULL") );
					else if( o instanceof Integer )
						out.write( "" + ObjectUtils.ternary(o, 0) );
					else						
						out.write("No case for type: " + o.getClass().getSimpleName());
					out.newLine();
					out.flush();
				}
				else
				{
					// Get a variable from an object
					o = ReflectionUtils.reflectField(pkg, clzz, func);
					if( o == null )
						out.write("NULL");
					else if( o instanceof String )
						out.write( (String)ObjectUtils.ternary(o, "NULL") );
					else if( o instanceof Integer )
						out.write( "" + ObjectUtils.ternary(o, 0) );
					else
						out.write("No case for type: " + o.getClass().getSimpleName());
					out.newLine();
					out.flush();
				}
			} catch (Exception e) {
				TraceUtils.trace(TraceUtils.STDERR, e);
				try {
					out.write(TraceUtils.getStackTrace(e));
					out.newLine();
					out.flush();
				} catch (IOException e1) {
					TraceUtils.trace(TraceUtils.STDERR, e);
				}
			}
			close();
		}
	}
}
