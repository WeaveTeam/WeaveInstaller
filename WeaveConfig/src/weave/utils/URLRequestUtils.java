package weave.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class URLRequestUtils 
{
	public static final String GET = "GET";
	public static final String POST = "POST";
	
	public static String request(String method, String urlStr) throws IOException
	{
		return request(method, urlStr, null);
	}
	
	public static String request(String method, String urlStr, URLRequestParams params) throws IOException
	{
		URL url 					= null;
		HttpURLConnection conn 		= null;
		DataOutputStream outStream 	= null;
		BufferedReader reader 		= null;
		String line 				= null;
		StringBuilder response 		= null;
		
		if( method.equals(GET) )
		{
			if( params == null )
				url = new URL(urlStr);
			else
				url = new URL(urlStr + params.toString());
			
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
	}
}

