/*
    Weave (Web-based Analysis and Visualization Environment)
    Copyright (C) 2008-2015 University of Massachusetts Lowell

    This file is a part of Weave.

    Weave is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License, Version 3,
    as published by the Free Software Foundation.

    Weave is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Weave.  If not, see <http://www.gnu.org/licenses/>.
*/

package weave.async;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import weave.core.Function;

public class AsyncFunction extends Function<Object, Function<Object, Object>>
{
	private String description = "";
	private List<AsyncCallback> callbacks = null;
	private Thread t = null;
	
	public AsyncFunction()
	{
		description = "";
		callbacks = Collections.synchronizedList(new ArrayList<AsyncCallback>());
	}
	public AsyncFunction(String desc)
	{
		description = desc;
		callbacks = Collections.synchronizedList(new ArrayList<AsyncCallback>());
	}
	
	protected Object doInBackground() 
	{
		throw new UnsupportedOperationException("Method Not Implemented Yet");
	}
	
	@Override
	public final Object call(Function<Object, Object> ...arguments) 
	{
		if( arguments.length > 1 )
			throw new IllegalArgumentException("Wrong number of arguments");
		
		final Function<Object, Object> backgroundTask;
		
		if( arguments.length == 1 )
			backgroundTask = arguments[0];
		else
			backgroundTask = new Function<Object, Object>() {
				@Override public Object call(Object... arguments) {
					return doInBackground();
				}
			};
			
		AsyncCallback c = new AsyncCallback() {
			@Override
			public void run(Object o) {
				AsyncTaskManager.removeTask(AsyncFunction.this);
			}
		};
		addCallback(c);
		
		AsyncTaskManager.addTask(this);
		
		t = new Thread(new Runnable() {
			@Override
			public void run() {
				Object o = backgroundTask.call();
				
				runCallbacks(o);
			}
		});
		t.start();
		return this;
	}
	
	public void cancel()
	{
		AsyncTaskManager.removeTask(this);
		removeAllCallbacks();
		t.interrupt();
		t = null;
	}
	
	public String toString()
	{
		return getClass().getName() + (description.length() > 0 ? " {" + description + "}" : "");
	}
	
	
	public AsyncFunction addCallback(AsyncCallback c) {
		callbacks.add(c);
		return this;
	}
	public AsyncFunction removeCallback(AsyncCallback c) {
		callbacks.remove(c);
		return this;
	}
	public AsyncFunction removeAllCallbacks() {
		Iterator<AsyncCallback> it = callbacks.iterator();
		while( it.hasNext() )
			removeCallback(it.next());
		return this;
	}
	
	
	private void runCallbacks(Object o)
	{
		if( callbacks != null )
		{
			Iterator<AsyncCallback> it = callbacks.iterator();
			while( it.hasNext() )
				it.next().run(o);
		}
	}
}
