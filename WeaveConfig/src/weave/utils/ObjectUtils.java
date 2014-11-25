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

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Map.Entry;

import weave.Globals;

public class ObjectUtils extends Globals
{
	/**
	 * Shorthand ternary operation to simplify testing
	 * 
	 * @param testNotNull The test to see if it is null
	 * @param failDefault The fail-safe default value
	 * @return This will return the test value if it is non-null, otherwise it will return the default
	 */
	public static Object ternary(Object testNotNull, Object failDefault)
	{
		return testNotNull != null ? testNotNull : failDefault;
	}
	
	
	/**
	 * Shorthand ternary operation to simplify testing null cases.<br><br>
	 * 
	 * You can specify a function to apply to the null object if the test
	 * of the object is not null. By default, no arguments are supplied 
	 * to the function.
	 * 
	 * <code><pre>
	 * class Dog {
	 * 	public String bark() {
	 * 		return "woof";
	 * 	}
	 * }
	 * 
	 * Dog d = new Dog();
	 * String says = ObjectUtils.ternary( d, "bark", "null-default" );
	 * System.out.println( says );	// outputs: "woof"
	 *
	 * 
	 * 
	 * Dog d_Null = null;
	 * String says = ObjectUtils.ternary( d_Null, "bark", "null-default" );
	 * System.out.println( says );	// outputs: "null-default"
	 * 
	 * </pre></code>
	 * 
	 * @param testNotNull The test to see if it is <code>null</code>
	 * @param functionName The name of the function to apply to the non-null test case
	 * @param failDefault The fail-safe default value
	 * @return The test value and applied function if it is non-null, otherwise it will return the fail-safe value
	 * 
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public static Object ternary(Object testNotNull, String functionName, Object failDefault) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return ternary(testNotNull, functionName, failDefault, new Class<?>[]{}, new Object[]{});
	}
	
	
	/**
	 * Shorthand ternary operation to simplify testing null cases.<br><br>
	 * 
	 * You can specify a function to apply to the null object if the test
	 * of the object is not null.<br>
	 * 
	 * <code><pre>
	 * class Dog {
	 * 	public String bark(int breed) {
	 * 		switch( breed ) {
	 * 			case 1: return "woof";
	 * 			case 2: return "yipp";
	 * 			case 3: return "yelp";
	 * 			default: return "meow";
	 * 		}
	 * 		return null; // unreachable
	 * 	}
	 * }
	 * 
	 * Dog d = new Dog();
	 * String says = ObjectUtils.ternary( d, "bark", "null dog", new Class&lt;?>[] { Integer.class }, new Object[] { 2 } );
	 * System.out.println( says );	// outputs: "yipp"
	 *
	 * 
	 * 
	 * Dog d_Null = null;
	 * String says = ObjectUtils.ternary( d_Null, "bark", "null dog", new Class&lt;?>[] { Integer.class }, new Object[] { 1 } );
	 * System.out.println( says );	// outputs: "null dog"
	 * 
	 * </pre></code>
	 * 
	 * @param testNotNull The test to see if it is <code>null</code>
	 * @param functionName The name of the function to apply to the non-null test case
	 * @param failDefault The fail-safe default value
	 * @param argClassList The function argument signature
	 * @param args The function arguments
	 * @return The test value and applied function if it is non-null, otherwise it will return the fail-safe value
	 * 
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public static Object ternary(Object testNotNull, String functionName, Object failDefault, Class<?>[] argClassList, Object[] args) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		if( testNotNull == null )
			return failDefault;
		
		return ReflectionUtils.reflectMethod(testNotNull, functionName, argClassList, args);
	}
	
	
	/**
	 * Coalesce all of the arguments passed to this function
	 * and return the first non-null argument passed.
	 * 
	 * @param args Variable length list of arguments
	 * @return The first non-null argument, or <code>null</code> if all arguments are null
	 */
	public static Object coalesce( Object ... args )
	{
		for( int i = 0; i < args.length; i++ )
			if( args[i] != null )
				return args[i];
		return null;
	}
	
	public static String toString(Object o)
	{
		return toString(o, ", ");
	}
	
	public static String toString(Object o, String delim)
	{
		int i = 0;
		StringBuilder sb = new StringBuilder();
		
		if( o instanceof Map<?,?> )
		{
			Map<?,?> m = (Map<?, ?>)o;
			sb.append("{\n");
			for( Entry<?, ?> e : m.entrySet() )
				sb.append("\t\"" + e.getKey() + "\" : \"" + e.getValue() + "\"" + (i++ != m.size()-1 ? ",":"") + "\n");
			sb.append("}");
		}
		else if( o instanceof Object[] )
		{
			Object[] arr = (Object[])o;
			sb.append("[" + (delim.contains("\n") ? "\n":" "));
			for( int j = 0; j < arr.length; j++ )
				if( j == 0 && delim.contains("\t") )
					sb.append("\t\"" + arr[j].toString() + "\"" + (i++ != arr.length-1 ? delim: " "));
				else
					sb.append("\"" + arr[j].toString() + "\"" + (i++ != arr.length-1 ? delim:" "));
			sb.append("]");
		}
		else
		{
			sb.append(o.toString());
		}
		return sb.toString();
	}
}
