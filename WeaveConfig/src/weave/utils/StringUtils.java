package weave.utils;

public class StringUtils
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
}
