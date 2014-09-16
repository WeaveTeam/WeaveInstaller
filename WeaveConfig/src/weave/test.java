package weave;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import weave.async.AsyncObserver;
import weave.async.AsyncTask;
import weave.async.IAsyncCallback;
import weave.utils.DownloadUtils;
import weave.utils.ObjectUtils;

public class test 
{
	public static void main(String args[])
	{
		final AsyncObserver observer = new AsyncObserver() {
			@Override
			public void onUpdate() {
				System.out.println("Update Percent: " + info.percent);
			}
		};
		IAsyncCallback callback = new IAsyncCallback() {
			@Override
			public void run(Object o) {
				System.out.println("Inside of callback: " + ObjectUtils.ternary(o, "NULL"));
			}
		};
		AsyncTask task = new AsyncTask() {
			@Override
			public Object doInBackground()
			{
				System.out.println("Inside of doInBackground");
				try {
					
					URL url = new URL("http://oicweave.org/.weave/updates.zip");
					File tmpTxt = new File(Settings.USER_HOME, "asdfasdf.zip");
				
					observer.init(url);
					DownloadUtils.download1(url, tmpTxt, observer);
					System.out.println("After download");
					
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				return "DONE";
			}
		};
		System.out.println("Adding callback");
		task.addCallback(callback);
		System.out.println("Executing");
		task.execute();
		System.out.println("After execute");
	}
}
