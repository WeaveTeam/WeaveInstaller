package weave.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import weave.Globals;
import weave.Settings;

public class URLRequestUtils extends Globals
{
	public static final String GET = "GET";
	public static final String POST = "POST";
	public static final int TIMEOUT = 3000;
	
	/**
	 * Encode a given string into <i>application/x-www-form-urlencoded</i> format.
	 * 
	 * @param url The string to encode
	 * @param charset The charset encoding scheme
	 * @return The encoded string
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public static String encode(String url, Charset charset) throws UnsupportedEncodingException
	{
		return URLEncoder.encode(url, charset.displayName())
					.replaceAll("\\+", "%20")
					.replaceAll("\\%21", "!")
					.replaceAll("\\%27", "'")
					.replaceAll("\\%28", "(")
					.replaceAll("\\%29", ")")
					.replaceAll("\\%7E", "~");
	}
	
	/**
	 * Creates a new HTTP request with the given request method and URL string.<br>
	 * The URL parameters for this request will be null.
	 * 
	 * @param method {@link #GET} or {@link #POST}
	 * @param urlStr The string URL to make a connection to
	 * @return A {@link URLRequestResult} object that holds the response headers and result body
	 * 
	 * @throws IOException
	 */
	public static URLRequestResult request(String method, String urlStr) throws IOException
	{
		return request(method, urlStr, null);
	}
	
	/**
	 * Creates a new HTTP request with the given request method, URL string, and parameters.
	 * 
	 * @param method {@link #GET} or {@link #POST}
	 * @param urlStr The string URL to make a connection to
	 * @param params The URL parameters to pass to the server
	 * @return A {@link URLRequestResult} object that holds the response headers and result body
	 * 
	 * @throws IOException
	 */
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
			conn.setRequestProperty("User-Agent", "Mozilla/5.0");
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
			conn.setRequestProperty("User-Agent", "Mozilla/5.0");
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
	
	public static String getContentHeader(String url, String field) throws IOException
	{
		if( Settings.isOfflineMode() )
			return null;
		
		URLRequestResult result = request(GET, url);
		return result.getResponseHeader(field);
	}
	
	public static String getContentBody(String url) throws IOException
	{
		if( Settings.isOfflineMode() )
			return null;
		
		URLRequestResult result = request(GET, url);
		return result.getResponseContent();
	}
}

