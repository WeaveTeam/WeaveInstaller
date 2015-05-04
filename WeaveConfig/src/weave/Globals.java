package weave;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import weave.reflect.Reflectable;
import weave.utils.ReflectionUtils;
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
			ReflectionUtils.reflectMethod(get("Installer"), "setProgress", new Class<?>[] {Integer.class}, new Object[] { bit });
		} catch (NoSuchMethodException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
		} catch (SecurityException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
		} catch (IllegalAccessException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
		} catch (IllegalArgumentException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
		} catch (InvocationTargetException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
		}
	}
}
