
package weave.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import weave.callbacks.ICallback;
import weave.includes.IUtils;

public class MD5Utils implements IUtils
{
	private static MessageDigest m = null;
	
	public MD5Utils()
	{
		try {
			m = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
		}
	}
	
	@Override
	public String getID()
	{
		return "MD5Utils";
	}
	
	public static String hash(String str)
	{
		String ret = "";
		if( m == null )
			try {
				m = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				TraceUtils.trace(TraceUtils.STDERR, e);
				return null;
			}
		
		m.update(str.getBytes(), 0, str.length());
		ret = new BigInteger(1, m.digest()).toString(16);
		while( ret.length() < 32 )
			ret = "0" + ret;
		return ret;
	}
	
	@Override
	public boolean addCallback(ICallback c) {
		return false;
	}
	@Override
	public boolean removeCallback(ICallback c) {
		return false;
	}
	@Override
	public void removeAllCallbacks() {
	}
}
