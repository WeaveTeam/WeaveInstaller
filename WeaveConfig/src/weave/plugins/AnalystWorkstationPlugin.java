package weave.plugins;

import weave.utils.RemoteUtils;

public class AnalystWorkstationPlugin extends Plugin 
{
	public static final String NAME = "Analyst Workstation";
	public static final String HOMEPAGEURL = "http://info.oicweave.org/projects/weave/wiki/Weave_Analyst";
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
		
		setPluginHomepageURL(HOMEPAGEURL);
		setPluginDownloadURL(RemoteUtils.getConfigEntry(RemoteUtils.AWS_URL));
		setPluginDescription(DESCRIPTION);
		setPluginBaseDirectory("${WEBAPPS}/");
	}
}
