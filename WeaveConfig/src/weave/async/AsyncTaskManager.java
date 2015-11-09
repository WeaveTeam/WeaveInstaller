package weave.async;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import weave.Settings;
import weave.reflect.Reflectable;

@Reflectable
public class AsyncTaskManager 
{
	private static List<AsyncFunction> tasks = new ArrayList<AsyncFunction>();
	
	public static List<AsyncFunction> getTaskList()
	{
		return tasks;
	}
	
	public static Boolean addTask(AsyncFunction t)
	{
		return tasks.add(t);
	}
	public static Boolean stopTask(Integer i)
	{
		tasks.get(i).cancel();
		return true;
	}
	public static Boolean stopTask(AsyncFunction t)
	{
		Iterator<AsyncFunction> it = tasks.iterator();
		while( it.hasNext() ) 
		{
			AsyncFunction itt = it.next();
			if( itt.equals(t) ) {
				itt.cancel();
				return true;
			}
		}
		return false;
	}
	public static Boolean removeTask(Integer i)
	{
		return tasks.remove(i);
	}
	public static Boolean removeTask(AsyncFunction t)
	{
		return tasks.remove(t);
	}
	
	public static void serialize(AsyncFunction ...functions)
	{
		_serialize(0, functions);
	}
	private static void _serialize(final int i, final AsyncFunction ...functions)
	{
		AsyncFunction f = functions[i];
		
		AsyncCallback c = new AsyncCallback() {
			@Override public void run(Object o) {
				if( i < functions.length - 1 )
					_serialize(i + 1, functions);
			}
		};
		
		f.addCallback(c).call();
	}
	
	public static String _toString()
	{
		String ret = "";
		
		Iterator<AsyncFunction> it = tasks.iterator();
		while( it.hasNext() )
			ret += it.next().toString() + Settings.N_L;
		
		return ret;
	}
}
