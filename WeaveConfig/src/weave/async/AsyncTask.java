package weave.async;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class AsyncTask
{
	private List<AsyncObserver> observers = null;
	private List<IAsyncCallback> callbacks = null;
	
	public AsyncTask()
	{
		observers = Collections.synchronizedList(new ArrayList<AsyncObserver>());
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
	
	
	public boolean addObserver(AsyncObserver o) {
		return observers.add(o);
	}
	public boolean removeObserver(AsyncObserver o) {
		return observers.remove(o);
	}
	public void removeAllObservers() {
		Iterator<AsyncObserver> it = observers.iterator();
		while( it.hasNext() )
			removeObserver(it.next());
	}
	
	
	public void notifyObservers()
	{
		Iterator<AsyncObserver> it = observers.iterator();
		while( it.hasNext() )
			it.next().onUpdate();
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
