package weave.plugins;

public interface IPlugin
{
	public void initPlugin();
	
	public String getPluginName();
	public String getPluginURL();
	public String getPluginVersion();
	public String getPluginDescription();
	public String getPluginDirectory();
	
	public void setPluginName(String name);
	public void setPluginURL(String url);
	public void setPluginVersion(String version);
	public void setPluginDescription(String desc);
	public void setPluginDirectory(String dir);
}
