package weave.utils;

import weave.Globals;

public class TransferUtils extends Globals
{
	public static final int B				= 1;
	public static final int KB				= B * 1024;
	public static final int MB				= KB * 1024;
	public static final int GB				= MB * 1024;
	public static final int TB				= GB * 1024;
	
	public static final int NO_FLAGS		= ( 1 << 0 );
	public static final int OVERWRITE		= ( 1 << 1 );
	public static final int SINGLE_FILE 	= ( 1 << 2 );
	public static final int MULTIPLE_FILES 	= ( 1 << 3 );

	public static final int FAILED			= 0;
	public static final int COMPLETE		= 1;
	public static final int CANCELLED		= 2;
	public static final int OFFLINE			= 3;
}
