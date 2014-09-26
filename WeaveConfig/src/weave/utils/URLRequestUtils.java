package weave.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import weave.Settings;

public class URLRequestUtils 
{
	public static final String GET = "GET";
	public static final String POST = "POST";
	
	public static String request(String method, String urlStr) throws IOException, InterruptedException
	{
		return request(method, urlStr, null);
	}
	
	public static String request(final String method, final String urlStr, final URLRequestParams params) throws IOException, InterruptedException
	{
		if( Settings.isOfflineMode() )
			return null;

		URL url 					= null;
		HttpURLConnection conn 		= null;
		DataOutputStream outStream 	= null;
		BufferedReader reader 		= null;
		String line 				= null;
		StringBuilder response 		= null;

		try {
			if( method.equals(GET) )
			{
				if( params == null )
					url = new URL(urlStr);
				else
					url = new URL(urlStr + "?" + params.toString());
				
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod(GET);
				conn.setDoOutput(false);
				conn.setUseCaches(false);
				conn.setRequestProperty("Content-Type", "multipart/form-data");
				conn.setRequestProperty("charset", "utf-8");
				conn.connect();
				
				response = new StringBuilder();
				reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				while( (line = reader.readLine()) != null )
					response.append(line);
				
				reader.close();
			}
			else if( method.equals(POST) )
			{
				url = new URL(urlStr);
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod(POST);
				conn.setDoOutput(params != null);
				conn.setUseCaches(false);
				conn.setRequestProperty("Content-Type", "multipart/form-data");
				conn.setRequestProperty("charset", "UTF-8");
				conn.connect();
				
				if( params != null )
				{
					outStream = new DataOutputStream(conn.getOutputStream());
					outStream.writeBytes(params.toString());
					outStream.flush();
					outStream.close();
				}
				
				response = new StringBuilder();
				reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				while( (line = reader.readLine()) != null )
					response.append(line);
				
				reader.close();
			}
			return response.toString();
			
		} catch (MalformedURLException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		} catch (ProtocolException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		} catch (IOException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		}
		
		return null;
	}
	
	public static String getContentHeader(final String url, final String field) throws InterruptedException, MalformedURLException
	{
		return getContentHeader(new URL(url), field);
	}
	public static String getContentHeader(final URL url, final String field) throws InterruptedException
	{
		if( Settings.isOfflineMode() )
			return null;
		
		try {
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setInstanceFollowRedirects(true);
			conn.setRequestMethod("GET");
			conn.connect();
			
			return conn.getHeaderField(field);

		} catch (IOException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
		}
		return null;
	}
}

