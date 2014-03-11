package weave.utils;

import java.util.Arrays;

public class Strings
{
	/**
	 * Integer.parseInt() wrapped in try/catch
	 * @param str
	 * @return
	 */
	public static int parseInt(String str)
	{
		if ("Infinity".equals(str))
			return Integer.MAX_VALUE;
		if ("-Infinity".equals(str))
			return -Integer.MAX_VALUE;
		try
		{
			return Integer.parseInt(str);
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
			return Integer.MAX_VALUE;
		}
	}
	
	/**
	 * String.format() wrapped in try/catch
	 * @param format
	 * @param args
	 * @return
	 */
	public static String format(String format, Object ...args)
	{
		try
		{
			return String.format(format, args);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return String.format("String.format(%s)", Arrays.deepToString(new Object[]{format, args}));
		}
	}
}
