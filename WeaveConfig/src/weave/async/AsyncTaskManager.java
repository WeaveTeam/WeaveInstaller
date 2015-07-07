package weave.async;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import weave.Settings;
import weave.reflect.Reflectable;

@Reflectable
public class AsyncTaskManager 
{
	private static List<AsyncTask> tasks = new ArrayList<AsyncTask>();
	
	public static List<AsyncTask> getTaskList()
	{
		return tasks;
	}
	
	public static Boolean addTask(AsyncTask t)
	{
		return tasks.add(t);
	}
	public static Boolean stopTask(Integer i)
	{
		tasks.get(i).cancel();
		return true;
	}
	public static Boolean stopTask(AsyncTask t)
	{
		Iterator<AsyncTask> it = tasks.iterator();
		while( it.hasNext() ) 
		{
			AsyncTask itt = it.next();
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
	public static Boolean removeTask(AsyncTask t)
	{
		return tasks.remove(t);
	}
	
	public static String _toString()
	{
		String ret = "";
		
		Iterator<AsyncTask> it = tasks.iterator();
		while( it.hasNext() )
			ret += it.next().toString() + Settings.N_L;
		
		return ret;
	}
}
