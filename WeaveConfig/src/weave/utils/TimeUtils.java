package weave.utils;

import java.util.concurrent.TimeUnit;

import weave.includes.IUtils;

public class TimeUtils implements IUtils
{
	@Override public String getID() {
		return "TimeUtils";
	}
	
	private static TimeInternals parse(long seconds)
	{
		TimeInternals ti = new TimeInternals();
		
		ti.hours = TimeUnit.SECONDS.toHours(seconds);
		ti.minutes = TimeUnit.SECONDS.toMinutes(seconds) - (ti.hours * 60);
		ti.seconds = TimeUnit.SECONDS.toSeconds(seconds) - (ti.hours * 60 * 60) - (ti.minutes * 60);
		
		return ti;
	}
	
	public static String format(String format, long seconds)
	{
		String ret = new String(format);
		String formats[] = {"h", "m", "s"};
		TimeInternals ti = parse(seconds);
		
		for( int i = 4; i > 0; i-- )
		{
			for( int j = 0; j < formats.length; j++ )
			{
				String replace = StringUtils.repeat(formats[j], i);
				ret = ret.replace("%"+replace, String.format("%0" + i + "d", ti.getTime(formats[j])));
			}
		}
		
		return ret;
	}
}

class TimeInternals
{
	public long hours;
	public long minutes;
	public long seconds;
	
	public long getTime(String f)
	{
		if( f.equals("h"))
			return hours;
		else if( f.equals("m"))
			return minutes;
		else if( f.equals("s"))
			return seconds;
		return 0;
	}
}