package weave.utils;

import static weave.utils.TraceUtils.STDERR;
import static weave.utils.TraceUtils.trace;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import weave.Globals;

public class URLRequestParams extends Globals
{
	Map<String, String> params = new HashMap<String, String>();
	
	public URLRequestParams()
	{
		
	}
	
	public String add(String key, String val)
	{
		return params.put(key, val);
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for( Map.Entry<String, String> entry : params.entrySet() ) {
			if( sb.length() > 0 )
				sb.append("&");
			
			try {
				sb.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
					.append("=")
					.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				trace(STDERR, e);
				BugReportUtils.showBugReportDialog(e);
			}
		}
		
		return sb.toString(); 
	}
}