package weave.async;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class AsyncObserver
{
	public AsyncObserverObject info = new AsyncObserverObject();
	public abstract void onUpdate();
	
	public void init(File f)
	{
		info.min = 0;
		info.cur = 0;
		info.percent = 0;
		info.max = f.length();
	}
	public void init(URL url) throws IOException
	{
		info.min = 0;
		info.cur = 0;
		info.percent = 0;
		info.max = ((HttpURLConnection)url.openConnection()).getContentLengthLong();
	}
	
	public class AsyncObserverObject
	{
		public long min;
		public long max;
		public long cur;
		public int percent;
		
		public int time;
		public int speed;
	}
}
