package weave;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import weave.async.AsyncCallback;
import weave.async.AsyncObserver;
import weave.async.AsyncTask;
import weave.utils.DownloadUtils;
import weave.utils.FileUtils;
import weave.utils.ObjectUtils;
import weave.utils.TransferUtils;

public class test 
{
	public static void main(String args[])
	{
		final AsyncObserver copyObserver = new AsyncObserver() {
			@Override
			public void onUpdate() {
				System.out.println("Copy: " + info.percent + "%");
			}
		};
		final AsyncCallback copyCallback = new AsyncCallback() {
			@Override
			public void run(Object o) {
				System.out.println("Done Copying");
			}
		};
		final AsyncTask copyTask = new AsyncTask() {
			@Override
			public Object doInBackground() {
				System.out.println("Inside copy background task.");

				try {
					File a = new File(Settings.USER_HOME, "asdfasdf.zip");
					File b = new File(Settings.USER_HOME, "Joss.zip");
					copyObserver.init(a);
					FileUtils.copy(a, b, TransferUtils.SINGLE_FILE, copyObserver);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return null;
			}
		};

		final AsyncObserver downloadObserver = new AsyncObserver() {
			@Override
			public void onUpdate() {
				System.out.println("Download: " + info.percent + "%");
			}
		};
		AsyncCallback downloadCallback = new AsyncCallback() {
			@Override
			public void run(Object o) {
				System.out.println("Download Callback: " + ObjectUtils.ternary(o, "NULL"));
				
				System.out.println("Adding copy callback");
				copyTask.addCallback(copyCallback);
				System.out.println("Executing copy");
				copyTask.execute();
				System.out.println("After copy execute");
			}
		};
		AsyncTask downloadTask = new AsyncTask() {
			@Override
			public Object doInBackground()
			{
				System.out.println("Inside of download background task");
				
				try {
					URL url = new URL("http://iweave.com/.weave/updates.zip");
					File tmpTxt = new File(Settings.USER_HOME, "asdfasdf.zip");
					downloadObserver.init(url);
					DownloadUtils.download(url, tmpTxt, downloadObserver);
					
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
		
		System.out.println("Adding download callback");
		downloadTask.addCallback(downloadCallback);
		System.out.println("Executing download");
		downloadTask.execute();
		System.out.println("After download execute");
	}
}
