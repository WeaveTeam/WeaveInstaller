package weave.utils;

import weave.Globals;

public class StringUtils extends Globals
{
	/**
	 * Check to see if the <code>test</code> word is at the 
	 * beginning of the <code>word</code>.
	 * <p>
	 * Examples:
	 * <pre>
	 * StringUtils.beginsWith("Elephant", "Ele") 	returns true
	 * StringUtils.beginsWith("Racecar", "Car") 	returns false
	 * </pre>
	 * </p>
	 * 
	 * @see #endsWith(String, String)
	 * 
	 * @param word The base word you want to test
	 * @param test The word that you are testing with
	 * @return <code>true</code> if the test is at the beginning, false otherwise
	 */
	public static boolean beginsWith(String word, String test)
	{
		return word.substring(0, test.length()).equals(test);
	}
	
	/**
	 * Check to see if the <code>test</code> word is at the
	 * end of the <code>word</code>.
	 * <p>
	 * Examples:
	 * <pre>
	 * StringUtils.endsWith("Racecar", "car")	returns true
	 * StringUtils.endWith("Computer", "com")	returns false
	 * </pre>
	 * </p>
	 * 
	 * @see #beginsWith(String, String)
	 * 
	 * @param word The base word you want to test
	 * @param test The word that you are testing with
	 * @return <code>true</code> if the test is at the end, false otherwise
	 */
	public static boolean endsWith(String word, String test)
	{
		return word.substring(word.length() - test.length(), word.length()).equals(test);
	}
	
	/**
	 * Repeat a String <code>repeat</code> times to form a new String.
	 * <p>
	 * Examples:
	 * <pre>
	 * StringUtils.repeat(null, 2)  = null
	 * StringUtils.repeat("", 2)    = ""
	 * StringUtils.repeat("a", 3)   = "aaa"
	 * </pre>
	 * </p>
	 * 
	 * @param str The String to repeat, may be null
	 * @param repeat The number of times to repeat <code>str</code>, negative treated as zero
	 * @return A new String consisting of the original String repeated, <code>null</code> if null String input
	 */
	public static String repeat(String str, int repeat)
	{
		String s = "";
		if( str == null ) return null;
		if( repeat < 0 ) return "";
		for( int i = 0; i < repeat; ++i ) s += str;
		
		return s;
	}
}
