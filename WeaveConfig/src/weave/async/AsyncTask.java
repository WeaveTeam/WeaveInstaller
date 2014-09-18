package weave.async;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class AsyncTask
{
	private List<IAsyncCallback> callbacks = null;
	
	public AsyncTask()
	{
		callbacks = Collections.synchronizedList(new ArrayList<IAsyncCallback>());
	}
	
	public abstract Object doInBackground();
	
	public void execute()
	{
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				Object o = doInBackground();
				
				runCallbacks(o);
			}
		});
		t.start();
	}
	
	
	public boolean addCallback(IAsyncCallback c) {
		return callbacks.add(c);
	}
	public boolean removeCallback(IAsyncCallback c) {
		return callbacks.remove(c);
	}
	public void removeAllCallbacks() {
		Iterator<IAsyncCallback> it = callbacks.iterator();
		while( it.hasNext() )
			removeCallback(it.next());
	}
	
	
	private void runCallbacks(Object o)
	{
		if( callbacks != null )
		{
			Iterator<IAsyncCallback> it = callbacks.iterator();
			while( it.hasNext() )
				it.next().run(o);
		}
	}
}
