package weave.utils;

import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import weave.Globals;

public class URLRequestResult extends Globals
{
	private static String content = null;
	private static Map<String, List<String>> map = null;
	
	public URLRequestResult(URLConnection con, String c)
	{
		content = c;
		map = con.getHeaderFields();
	}
	
	public String getResponseHeader(String key)
	{
		List<String> list = map.get(key);
		String result = list.get(list.size() - 1);
		return result; 
	}
	public String getResponseContent()
	{
		return content;
	}
}
