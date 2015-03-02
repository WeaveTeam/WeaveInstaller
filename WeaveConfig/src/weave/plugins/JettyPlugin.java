package weave.plugins;

import weave.configs.JettyConfig;

public class JettyPlugin extends Plugin
{
	private static JettyPlugin _instance = null;
	public static JettyPlugin getPlugin()
	{
		if( _instance == null )
			_instance = new JettyPlugin();
		return _instance;
	}
	
	public JettyPlugin()
	{
		super(JettyConfig.NAME);
		
		setPluginVersion(JettyConfig.getConfig().getInstallVersion());
		setPluginHomepageURL(JettyConfig.getConfig().getHomepageURL());
		setPluginDownloadURL(JettyConfig.getConfig().getDownloadURL());
		setPluginDescription(JettyConfig.getConfig().getDescription());
		setPluginBaseDirectory("${PLUGINS_DIR}/" + getPluginName());
	}
}
