package weave;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import weave.async.AsyncObserver;
import weave.async.AsyncTask;
import weave.async.IAsyncCallback;
import weave.utils.DownloadUtils;
import weave.utils.FileUtils;
import weave.utils.ObjectUtils;

public class test 
{
	public static void main(String args[])
	{
		/*
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
					
					File a = new File(Settings.USER_HOME, "asdfasdf.zip");
					File b = new File(Settings.USER_HOME, "Joss.zip");
					
//					URL url = new URL("http://oicweave.org/.weave/updates.zip");
//					File tmpTxt = new File(Settings.USER_HOME, "asdfasdf.zip");
				
					observer.init(a);
					FileUtils.copy(a, b, FileUtils.SINGLE_FILE, observer);
//					DownloadUtils.download(url, tmpTxt, observer);
					System.out.println("After download");
					
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
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
		*/
		System.out.println(DownloadUtils.speedify( 23523523 ));
		System.out.println(DownloadUtils.speedify( 34637 ));
	}
}
