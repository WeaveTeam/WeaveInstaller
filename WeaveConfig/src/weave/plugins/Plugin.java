package weave.plugins;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipException;

import weave.Globals;
import weave.Settings;
import weave.utils.BugReportUtils;
import weave.utils.EnvironmentUtils;
import weave.utils.TraceUtils;
import weave.utils.ZipUtils;

public class Plugin extends Globals implements IPlugin
{
	protected String _name = "";
	protected String _homepage = "";
	protected String _downloadURL = "";
	protected String _version = "";
	protected String _desc = "";
	protected String _downloadFile = "";
	protected String _dir = "";
	
	public Plugin()
	{
		
	}
	
	public Plugin(String name)
	{
		_name = name;
	}
	
	@Override public void initPlugin() {
		
	}

	@Override public String getPluginName() {			return _name; 	}
	@Override public String getPluginHomepageURL() { 	return _homepage;	}
	@Override public String getPluginDownloadURL() { 	return _downloadURL;	}
	@Override public String getPluginVersion() { 		return _version;}
	@Override public String getPluginDescription() {	return _desc;	}
	@Override public String getPluginDownloadFile()	{	return EnvironmentUtils.replace(_downloadFile); }
	@Override public String getPluginBaseDirectory() {	return EnvironmentUtils.replace(_dir);	}

	@Override public void setPluginName(String name) { 			_name = name;	}
	@Override public void setPluginHomepageURL(String url) {	_homepage = url;		}
	@Override public void setPluginDownloadURL(String url) {	_downloadURL = url;		}
	@Override public void setPluginVersion(String version) {	_version = version;	}
	@Override public void setPluginDescription(String desc) {	_desc = desc; 	}
	@Override public void setPluginDownloadFile(String file) {	_downloadFile = file; }
	@Override public void setPluginBaseDirectory(String dir) {	_dir = dir;		}

	@Override public Boolean isPluginInstalled() 
	{
		boolean installed = true;
		File zipfile = new File(Settings.DOWNLOADS_DIRECTORY, getPluginName() + ".zip");
		if( !zipfile.exists() )
			return false;
		
		try {
			if( getPluginBaseDirectory() == null )
				return false; 
			
			List<String> list = ZipUtils.getZipEntries(zipfile);
			File destination = new File(getPluginBaseDirectory());
			File tmp = null;
			
			for( int i = 0; i < list.size(); i++ ) 
			{
				tmp = new File(destination, list.get(i));
				installed &= tmp.exists();
			}
			
		} catch (ZipException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
			return false;
		} catch (IOException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
			return false;
		}
		return installed;
	}
}
