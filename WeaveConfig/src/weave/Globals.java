package weave;

import java.util.HashMap;
import java.util.Map;

import weave.reflect.Reflectable;
import weave.reflect.ReflectionUtils;
import weave.utils.TraceUtils;

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
	
	public static void setInstallerProgress(Integer bit)
	{
		try {
			ReflectionUtils.reflectMethod(get("Server"), "setProgress", new Class<?>[] {Integer.class}, new Object[] { bit });
		} catch (Exception e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
		}
	}
}
