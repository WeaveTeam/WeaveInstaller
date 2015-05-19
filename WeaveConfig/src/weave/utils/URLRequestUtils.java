package weave.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import weave.Globals;
import weave.Settings;
public class URLRequestUtils extends Globals
{
	public static final String GET = "GET";
	public static final String POST = "POST";
	public static final int TIMEOUT = 3000;
	
	public static URLRequestResult request(String method, String urlStr) throws IOException
	{
		return request(method, urlStr, null);
	}
	
	public static URLRequestResult request(final String method, final String urlStr, final URLRequestParams params) throws IOException
	{
		if( Settings.isOfflineMode() )
			return null;

		URL url 					= null;
		HttpURLConnection conn 		= null;
		DataOutputStream outStream 	= null;
		BufferedReader reader 		= null;
		String line 				= null;
		StringBuilder content 		= new StringBuilder();
		URLRequestResult result		= null;

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
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("charset", "utf-8");
			conn.setConnectTimeout(TIMEOUT);
			conn.connect();
			
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while( (line = reader.readLine()) != null )
				content.append(line);
			reader.close();
			
			result = new URLRequestResult(conn, content.toString());
		}
		else if( method.equals(POST) )
		{
			url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(POST);
			conn.setDoOutput(params != null);
			conn.setUseCaches(false);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("charset", "UTF-8");
			conn.setConnectTimeout(TIMEOUT);
			conn.connect();
			
			if( params != null )
			{
				outStream = new DataOutputStream(conn.getOutputStream());
				outStream.writeBytes(params.toString());
				outStream.flush();
				outStream.close();
			}
			
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while( (line = reader.readLine()) != null )
				content.append(line);
			reader.close();
			
			result = new URLRequestResult(conn, content.toString());
		}
		return result;
	}
	
	public static String getContentHeader(final String url, final String field) throws InterruptedException, IOException
	{
		return getContentHeader(new URL(url), field);
	}
	public static String getContentHeader(final URL url, final String field) throws InterruptedException, IOException
	{
		if( Settings.isOfflineMode() )
			return null;
		
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setInstanceFollowRedirects(true);
		conn.setRequestMethod(GET);
		conn.setConnectTimeout(TIMEOUT);
		conn.connect();

		return new URLRequestResult(conn, "").getResponseHeader(field);
	}
}

