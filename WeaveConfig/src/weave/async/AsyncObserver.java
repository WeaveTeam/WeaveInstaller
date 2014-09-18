package weave.async;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipFile;

import weave.utils.FileUtils;
import weave.utils.TransferUtils;

public abstract class AsyncObserver
{
	public AsyncObserverObject info = new AsyncObserverObject();
	public abstract void onUpdate();
	
	public void init(File f, int flags)
	{
		info.min = 0;
		info.cur = 0;
		info.percent = 0;
		
		if( (flags & TransferUtils.MULTIPLE_FILES) != 0 )
			info.max = FileUtils.getNumberOfFilesInDirectory(f);
		else if( (flags & TransferUtils.SINGLE_FILE) != 0)
			info.max = f.length();
	}
	public void init(ZipFile z)
	{
		info.min = 0;
		info.cur = 0;
		info.percent = 0;
		info.max = z.size();
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
