package weave;

import java.util.HashMap;
import java.util.Map;

import weave.utils.Reflectable;

public class Globals 
{
	@Reflectable 
	public static Map<String, Object> globalHashMap = new HashMap<String, Object>();
	
	
	@Reflectable 
	public static Object set(String key, Object val)
	{
		return globalHashMap.put(key, val);
	}
	
	
	@Reflectable 
	public static Object get(String key)
	{
		return globalHashMap.get(key);
	}
}
