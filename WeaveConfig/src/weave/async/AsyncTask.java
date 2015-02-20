/*
    Weave (Web-based Analysis and Visualization Environment)
    Copyright (C) 2008-2014 University of Massachusetts Lowell

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

import weave.Globals;

public abstract class AsyncTask extends Globals
{
	private List<AsyncCallback> callbacks = null;
	
	public AsyncTask()
	{
		callbacks = Collections.synchronizedList(new ArrayList<AsyncCallback>());
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
	
	
	public AsyncTask addCallback(AsyncCallback c) {
		callbacks.add(c);
		return this;
	}
	public AsyncTask removeCallback(AsyncCallback c) {
		callbacks.remove(c);
		return this;
	}
	public void removeAllCallbacks() {
		Iterator<AsyncCallback> it = callbacks.iterator();
		while( it.hasNext() )
			removeCallback(it.next());
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
