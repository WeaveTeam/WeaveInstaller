package weave.plugins;

public interface IPlugin
{
	public void initPlugin();
	
	public String getPluginName();
	public String getPluginHomepageURL();
	public String getPluginDownloadURL();
	public String getPluginVersion();
	public String getPluginDescription();
	public String getPluginDownloadFile();
	public String getPluginBaseDirectory();
	
	public void setPluginName(String name);
	public void setPluginHomepageURL(String url);
	public void setPluginDownloadURL(String url);
	public void setPluginVersion(String version);
	public void setPluginDescription(String desc);
	public void setPluginDownloadFile(String file);
	public void setPluginBaseDirectory(String dir);

	public Boolean isPluginInstalled();
}
