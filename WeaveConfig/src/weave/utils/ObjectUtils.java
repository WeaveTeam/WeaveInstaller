package weave.utils;

public class ObjectUtils
{
	public static Object coalesce( Object ... args )
	{
		for( int i = 0; i < args.length; i++ )
			if( args[i] != null )
				return args[i];
		return null;
	}
}
