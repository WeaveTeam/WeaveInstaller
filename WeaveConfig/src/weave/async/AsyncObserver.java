package weave.async;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipException;

import weave.Globals;
import weave.utils.FileUtils;
import weave.utils.ZipUtils;

public abstract class AsyncObserver extends Globals
{
	public AsyncObserverObject info = new AsyncObserverObject();
	
	public abstract void onUpdate();
	
	public void init(File f) throws ZipException, IOException
	{
		info.min = 0;
		info.cur = 0;
		info.percent = 0;
		
		if( ZipUtils.isZip(f) )
			info.max = ZipUtils.getUncompressedSize(f);
		else
			info.max = FileUtils.getSize(f);
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
		
		@Override
		public String toString()
		{
			return "AsyncObserver$AsyncObserverObject {" +
					"min:" + min + ", " +
					"max:" + max + ", " +
					"cur:" + cur + ", " +
					"percent:" + percent + ", " + 
					"time:" + time + ", " +
					"speed:" + speed + "}";
		}
	}
}
