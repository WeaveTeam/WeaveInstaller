package weave.plugins;

import java.awt.Color;
import java.io.File;

import javax.swing.JPanel;

import weave.Globals;
import weave.utils.EnvironmentUtils;

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

	@Override public JPanel getPluginPanel()
	{
		JPanel panel = new JPanel();
		
		panel.setLayout(null);
		panel.setBackground(Color.WHITE);
		
		return panel;
	}

	@Override public void pluginPanelRefresh() {
		
	}
	
	@Override public Boolean isPluginInstalled() 
	{
//		boolean installed = true;
		File zipFile = new File(getPluginDownloadFile());
		return zipFile.exists();
//		if( !zipFile.exists() )
//			return false;
		
//		try {
//			if( getPluginBaseDirectory() == null )
//				return false; 
//			
//			List<String> list = ZipUtils.getZipEntries(zipFile);
//			File destination = new File(getPluginBaseDirectory());
//			File tmp = null;
//			
//			for( int i = 0; i < list.size(); i++ ) 
//			{
//				tmp = new File(destination, list.get(i));
//				installed &= tmp.exists();
//			}
//			
//		} catch (ZipException e) {
//			TraceUtils.trace(TraceUtils.STDERR, e);
//			return false;
//		} catch (IOException e) {
//			TraceUtils.trace(TraceUtils.STDERR, e);
//			BugReportUtils.showBugReportDialog(e);
//			return false;
//		}
//		return installed;
	}
}
