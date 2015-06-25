package weave.utils;

import java.util.concurrent.TimeUnit;

import weave.Globals;

public class TimerUtils extends Globals
{
	private static final String[] FORMATS = {"h", "m", "s"};
	private static final int MAX_FORMAT_COUNT = 4;
	
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
		TimeInternals ti = parse(seconds);
		
		for( int i = MAX_FORMAT_COUNT; i > 0; i-- )
		{
			for( int j = 0; j < FORMATS.length; j++ )
			{
				String replace = StringUtils.repeat(FORMATS[j], i);
				ret = ret.replace("%"+replace, String.format("%0" + i + "d", ti.getTime(FORMATS[j])));
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