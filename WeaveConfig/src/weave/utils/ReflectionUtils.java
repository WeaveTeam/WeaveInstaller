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

package weave.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import weave.Globals;
import weave.reflect.Reflectable;

public class ReflectionUtils extends Globals
{
	/**
	 * This will create and return a new instance of the class.<br>
	 * The nullary constructor will be called to initialize this class.
	 * 
	 * @param pkg The package of the class
	 * @param clazz The class to create
	 * @return An instance of the newly created object
	 * 
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public static Object reflectConstructor(String pkg, String clazz) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
	{
		return reflectConstructor(pkg, clazz, new Class<?>[]{}, new Object[]{});
	}
	
	/**
	 * This will create and return a new instance of the specified class.<br>
	 * The constructor that matches the same signature defined in the argClassList will be called to initialize this class.
	 * 
	 * @param pkg The package of the class
	 * @param clazz The class to create
	 * @param argClassList The signature of the constructor to initialize the instance with
	 * @param args The arg list to the constructor
	 * @return An instance of the newly created object
	 * 
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public static Object reflectConstructor(String pkg, String clazz, Class<?>[] argClassList, Object[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
	{
		Class<?> c = Class.forName(pkg + "." + clazz);
		return c.getConstructor(argClassList).newInstance(args);
	}
	
	
	/**
	 * Get a static field of a class.
	 * 
	 * @param pkg The fully qualified package name of a class
	 * @param clazz The class name
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
	public static Object reflectField(String pkg, String clazz, String field) throws ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		Field f = null;
		Class<?> c = Class.forName(pkg + "." + clazz);
		
		while( c != null )
		{
			try {
				f = c.getDeclaredField(field);

				if( !f.isAnnotationPresent(Reflectable.class) && !c.isAnnotationPresent(Reflectable.class) )
					throw new IllegalAccessException(clazz + "." + field);
				
				return f.get(null);
			} catch (NoSuchFieldException e) {
				c = c.getSuperclass();
			}
		}
		throw new NoSuchFieldException(clazz + "." + field);
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
		Field f = null;
		Class<?> c = instance.getClass();
		
		while( c != null )
		{
			try {
				f = c.getDeclaredField(field);

				if( (!f.isAnnotationPresent(Reflectable.class) && !c.isAnnotationPresent(Reflectable.class)) &&
					StringUtils.beginsWith(c.getCanonicalName(), "weave") )
					throw new IllegalAccessException(instance.getClass().getName() + "." + field);
				
				return f.get(instance);
			} catch (NoSuchFieldException e) {
				c = c.getSuperclass();
			}
		}
		throw new NoSuchFieldException(instance.getClass().getName() + "." + field);
	}
	

	/**
	 * Run a static function of a class by using reflection.<br>
	 * This is done by getting the declared method of the class with the given name.
	 * By default, this static function cannot take any arguments. This method is then 
	 * statically run outside of any instance of the class.
	 * 
	 * @param pkg The fully qualified package name of the class
	 * @param clazz The class name
	 * @param function The function you want to call on the class
	 * @return The result of calling the function
	 * @throws Exception 
	 */
	public static Object reflectMethod(String pkg, String clazz, String function) throws Exception
	{
		return reflectMethod(pkg, clazz, function, new Class<?>[] {}, new Object[] {});
	}
	
	
	/**
	 * Run a static function of a class by using reflection.<br>
	 * This is done by getting the declared method of the class with the given
	 * name and function signature. This method is then statically run outside
	 * of any instance of the class.
	 *  
	 * @param pkg The fully qualified package name of a class
	 * @param clazz The class name
	 * @param function The function you want to call on the class
	 * @param argClassList The function signature
	 * @param args The arguments to supply to the function
	 * @return The result of calling the function
	 * @throws Exception 
	 */
	public static Object reflectMethod(String pkg, String clazz, String function, Class<?>[] argClassList, Object[] args) throws Exception
	{
		Method m = null;
		Class<?> c = Class.forName(pkg + "." + clazz);
		
		while( c != null )
		{
			try {
				m = c.getDeclaredMethod(function, argClassList);

				if( !m.isAnnotationPresent(Reflectable.class) && !c.isAnnotationPresent(Reflectable.class) )
					throw new IllegalAccessException(clazz + "." + function + "()");
				
				return m.invoke(null, args);
				
			} catch (InvocationTargetException e) {
				if( e.getCause() instanceof Exception )
					throw (Exception) e.getCause();
			} catch (NoSuchMethodException e) {
				c = c.getSuperclass();
			}
		}
		throw new NoSuchMethodException(clazz + "." + function + "()");
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
	 * @throws Exception 
	 */
	public static Object reflectMethod(Object instance, String function) throws Exception
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
	 * @throws Exception 
	 */
	public static Object reflectMethod(Object instance, String function, Class<?>[] argClassList, Object[] args) throws Exception
	{
		Method m = null;
		Class<?> c = instance.getClass();
		
		while( c != null )
		{
			try {
				m = c.getDeclaredMethod(function, argClassList);
				
				if( (!m.isAnnotationPresent(Reflectable.class) && !c.isAnnotationPresent(Reflectable.class)) &&
					StringUtils.beginsWith(c.getCanonicalName(), "weave") )
					throw new IllegalAccessException(instance.getClass().getName() + "." + function + "()");
				
				return m.invoke(instance, args);
				
			} catch (InvocationTargetException e) {
				if( e.getCause() instanceof Exception )
					throw (Exception) e.getCause();
			} catch (NoSuchMethodException e) {
				c = c.getSuperclass();
			}
		}
		throw new NoSuchMethodException(instance.getClass().getName() + "." + function + "()");
	}
}
