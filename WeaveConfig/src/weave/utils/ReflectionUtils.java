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

package weave.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionUtils 
{
	/**
	 * Get a static field of a class.
	 * 
	 * @param pkg The fully qualified package name of a class
	 * @param clzz The class name
	 * @param field The variable field you want to access
	 * @return The value of the field
	 * 
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws ClassNotFoundException
	 * @throws NoSuchFieldException 
	 */
	public static Object reflectField(String pkg, String clzz, String field) throws ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		Class<?> c = Class.forName(pkg + "." + clzz);
		Field f = c.getDeclaredField(field);
		return f.get(null);
	}

	
	/**
	 * Get a field of a class instance.
	 * 
	 * @param instance The instance of the class
	 * @param field The variable field you want to access
	 * @return The value of the field
	 * 
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws NoSuchFieldException 
	 * @throws InvocationTargetException
	 */
	public static Object reflectField(Object instance, String field) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException
	{
		Field f = instance.getClass().getDeclaredField(field);
		return f.get(instance);
	}
	

	/**
	 * Run a static function of a class by using reflection.<br>
	 * This is done by getting the declared method of the class with the given name.
	 * By default, this static function cannot take any arguments. This method is then 
	 * statically run outside of any instance of the class.
	 * 
	 * @param pkg The fully qualified package name of the class
	 * @param clzz The class name
	 * @param function The function you want to call on the class
	 * @return The result of calling the function
	 * 
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws ClassNotFoundException
	 */
	public static Object reflectMethod(String pkg, String clzz, String function) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException
	{
		return reflectMethod(pkg, clzz, function, new Class<?>[] {}, new Object[] {});
	}
	
	
	/**
	 * Run a static function of a class by using reflection.<br>
	 * This is done by getting the declared method of the class with the given
	 * name and function signature. This method is then statically run outside
	 * of any instance of the class.
	 *  
	 * @param pkg The fully qualified package name of a class
	 * @param clzz The class name
	 * @param function The function you want to call on the class
	 * @param argClassList The function signature
	 * @param args The arguments to supply to the function
	 * @return The result of calling the function
	 * 
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws ClassNotFoundException
	 */
	public static Object reflectMethod(String pkg, String clzz, String function, Class<?>[] argClassList, Object[] args) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException
	{
		Class<?> c = Class.forName(pkg + "." + clzz);
		Method m = c.getDeclaredMethod(function, argClassList);
		return m.invoke(null, args);
	}
	
	
	/**
	 * Run a class function on an instance of the class by using reflection.<br>
	 * This is done by getting the declared method of the class with the given name.
	 * By default, this function cannot take any arguments. The method is then invoked
	 * on the instance of the class.
	 * 
	 * @param instance The instance of the class
	 * @param function The function you want to call on the class
	 * @return The result of calling the function
	 * 
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public static Object reflectMethod(Object instance, String function) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return reflectMethod(instance, function, new Class<?>[] {}, new Object[] {});
	}
	
	
	/**
	 * Run a class function on an instance of the class by using reflection.<br>
	 * This is done by getting the declared method of the class with the given name
	 * and function signature. The method is then invoked on the instance of the class.
	 * 
	 * @param instance The instance of the class
	 * @param function The function you want to call on the class
	 * @param argClassList The function signature
	 * @param args The arguments to supply to the function
	 * @return The result of calling the function
	 * 
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public static Object reflectMethod(Object instance, String function, Class<?>[] argClassList, Object[] args) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Method m = null;
		Class<?> clazz = instance.getClass();
		while( clazz != null )
		{
			try {
				m = clazz.getDeclaredMethod(function, argClassList);
				return m.invoke(instance, args);
			} catch (NoSuchMethodException e) {
				clazz = clazz.getSuperclass();
			}
		}
		throw new NoSuchMethodException(instance.getClass().getName() + "." + function + "()");
	}
}
