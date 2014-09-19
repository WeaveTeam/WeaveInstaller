package weave.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionUtils 
{
	public static Object reflect(String pkg, String clzz, String function, Class<?>[] argClassList, Object[] args) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException
	{
		Class<?> c = Class.forName(pkg + "." + clzz);
		return reflect(c, function, argClassList, args);
	}
	public static Object reflect(Object instance, String function, Class<?>[] argClassList, Object[] args) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Method m = instance.getClass().getDeclaredMethod(function, argClassList);
		return m.invoke(instance, args);
	}
}
