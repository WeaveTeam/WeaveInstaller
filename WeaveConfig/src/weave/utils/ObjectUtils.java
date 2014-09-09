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
import java.lang.reflect.Method;

public class ObjectUtils
{
	/**
	 * Shorthand ternary operation to simplify testing
	 * 
	 * @param test The test to see if it is null
	 * @param failDefault The fail-safe default value
	 * @return This will return the test value if it is non-null, otherwise it will return the default
	 */
	public static Object ternary(Object testNotNull, Object failDefault)
	{
		return testNotNull != null ? testNotNull : failDefault;
	}
	
	
	/**
	 * Shorthand ternary operation to simplify testing null cases.
	 * 
	 * You can specify a function to apply to the null object if the test
	 * of the object is not null. By default, no arguments can be supplied 
	 * to the function.
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
	 * Shorthand ternary operation to simplify testing null cases.
	 * 
	 * You can specify a function to apply to the null object if the test
	 * of the object is not null.
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
		
		Method m = testNotNull.getClass().getDeclaredMethod(functionName, argClassList);
		return m.invoke(testNotNull, args);
	}
	
	
	/**
	 * Coalesce all of the arguments passed to this function
	 * and return the first non-null argument passed.
	 * 
	 * @param args Variable length list of arguments
	 * @return The first non-null argument
	 */
	public static Object coalesce( Object ... args )
	{
		for( int i = 0; i < args.length; i++ )
			if( args[i] != null )
				return args[i];
		return null;
	}
}
