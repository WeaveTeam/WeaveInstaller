
package weave.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import weave.Globals;

public class MD5Utils extends Globals
{
	private static MessageDigest m = null;
	
	public static String hash(String str)
	{
		String ret = "";
		if( m == null )
		{
			try {
				m = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				TraceUtils.trace(TraceUtils.STDERR, e);
				return null;
			}
		}
		
		m.update(str.getBytes(), 0, str.length());
		ret = new BigInteger(1, m.digest()).toString(16);
		
		while( ret.length() < 32 )
			ret = "0" + ret;
		return ret;
	}
}
