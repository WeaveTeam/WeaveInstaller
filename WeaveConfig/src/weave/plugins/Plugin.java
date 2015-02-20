package weave.plugins;

import weave.Globals;

public class Plugin extends Globals implements IPlugin
{
	protected String _name = "";
	protected String _url = "";
	protected String _version = "";
	protected String _desc = "";
	protected String _dir = "";
	
	public Plugin()
	{
		
	}
	
	public Plugin(String name)
	{
		_name = name;
	}
	
	public Plugin(String name, String url, String version, String desc, String dir)
	{
		_name = name;
		_url = url;
		_version = version;
		_desc = desc;
		_dir = dir;
	}
	
	@Override public void initPlugin() {
		
	}

	@Override public String getPluginName() {			return _name; 	}
	@Override public String getPluginURL() { 			return _url;	}
	@Override public String getPluginVersion() { 		return _version;}
	@Override public String getPluginDescription() {	return _desc;	}
	@Override public String getPluginDirectory() {		return _dir;	}

	@Override public void setPluginName(String name) { 			_name = name;	}
	@Override public void setPluginURL(String url) {			_url = url;		}
	@Override public void setPluginVersion(String version) {	_version = version;	}
	@Override public void setPluginDescription(String desc) {	_desc = desc; 	}
	@Override public void setPluginDirectory(String dir) {		_dir = dir;		}
}
