
package weave.utils;

import static weave.utils.TraceUtils.STDERR;
import static weave.utils.TraceUtils.trace;

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
				trace(STDERR, e);
				return null;
			}
		}
		
		m.update(str.getBytes(), 0, str.length());
		ret = new BigInteger(1, m.digest()).toString(16);
		ret = StringUtils.lpad(ret, "0", 32);
		return ret;
	}
}
