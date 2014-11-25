package weave;

import java.util.HashMap;
import java.util.Map;

import weave.reflect.Reflectable;

@Reflectable
public class Globals 
{
	public static Map<String, Object> globalHashMap = new HashMap<String, Object>();
	
	public static Object set(String key, Object val)
	{
		return globalHashMap.put(key, val);
	}
	
	public static Object get(String key)
	{
		return globalHashMap.get(key);
	}
}
