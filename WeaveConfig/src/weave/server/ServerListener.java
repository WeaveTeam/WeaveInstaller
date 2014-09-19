package weave.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import weave.utils.BugReportUtils;
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
		TraceUtils.trace(TraceUtils.STDOUT, "-> Stopping RPC Server............");
		
		try {
			while( !connections.isEmpty() )
			{
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
		TraceUtils.put(TraceUtils.STDOUT, "DONE");
	}
	
	
	
	
	
	class ServerListenerThread extends Thread implements Runnable
	{
		private Socket clientSock = null;
		private BufferedReader in = null;
		
		public ServerListenerThread(Socket s)
		{
			clientSock = s;

			try {
				in = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
			} catch (IOException e) {
				TraceUtils.trace(TraceUtils.STDERR, e);
				BugReportUtils.showBugReportDialog(e);
			}
		}
		
		public void close()
		{
			try {
				
				if( in != null ) in.close();
				clientSock.close();
				connections.remove(this);
				
			} catch (IOException e) {
			}
		}
		
		@Override
		public void run() {
			try {
				
				String query = in.readLine();
				JSONObject queryObj = new JSONObject(query);

				

			} catch (UnsupportedEncodingException e) {
				TraceUtils.trace(TraceUtils.STDERR, e);
				BugReportUtils.showBugReportDialog(e);
			} catch (IOException e) {
				TraceUtils.trace(TraceUtils.STDERR, e);
				BugReportUtils.showBugReportDialog(e);
			}
			close();
		}
	}
	
	static class URLUtils
	{
		public static Map<String, String> parse(String url) throws UnsupportedEncodingException
		{
			Map<String, String> result = new HashMap<String, String>();
			
			String[] pairs = url.split("&");
			for( String pair : pairs ) 
			{
				String[] key_val = pair.split("=");
				result.put(URLDecoder.decode(key_val[0], "UTF-8"), URLDecoder.decode(key_val[1], "UTF-8"));
			}
			
			return result;
		}
	}
}
