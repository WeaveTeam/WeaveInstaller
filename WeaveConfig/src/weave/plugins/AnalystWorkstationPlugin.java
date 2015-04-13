package weave.plugins;

import javax.swing.JLabel;
import javax.swing.JPanel;

import weave.utils.EnvironmentUtils;
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
		
		String filename = "";
		String url = RemoteUtils.getConfigEntry(RemoteUtils.AWS_URL);
		if( url != null ) 
			filename = url.substring(url.lastIndexOf("/") + 1);
		
		setPluginHomepageURL(HOMEPAGEURL);
		setPluginDownloadURL(url);
		setPluginDescription(DESCRIPTION);
		setPluginDownloadFile("${" + EnvironmentUtils.DOWNLOAD_DIR + "}/" + filename);
		setPluginBaseDirectory("${" + EnvironmentUtils.WEBAPPS + "}/");
	}
	
	@Override
	public JPanel getPluginPanel()
	{
		JPanel panel = super.getPluginPanel();
		
		JLabel l = new JLabel(NAME);
		l.setBounds(20, 20, 150, 25);
		panel.add(l);
		
		return panel;
	}
}
