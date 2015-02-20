package weave.plugins;

public class AnalystWorkstationPlugin extends Plugin 
{
	public static final String NAME = "Analyst Workstation";
	public static final String URL = "";
	public static final String VERSION = "0.8";
	public static final String DESCRIPTION = "Analyst workstation for Weave";
	
	private static AnalystWorkstationPlugin _instance = null;
	public static AnalystWorkstationPlugin getPlugin()
	{
		if( _instance == null )
			_instance = new AnalystWorkstationPlugin();
		return _instance;
	}
	
	public AnalystWorkstationPlugin()
	{
		super(NAME);
	}
}
