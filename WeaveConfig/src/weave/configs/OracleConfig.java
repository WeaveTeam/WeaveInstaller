package weave.configs;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import weave.managers.ConfigManager;
import weave.managers.IconManager;
import weave.utils.BugReportUtils;
import weave.utils.TraceUtils;

public class OracleConfig extends Config 
{
	public static final String NAME = "Oracle";
	public static final String HOMEPAGE = "http://www.oracle.com/technetwork/indexes/downloads/index.html";
	public static final String URL = HOMEPAGE;
	public static final String HOST = "localhost";
	public static final int PORT = 1521;
	
	public static OracleConfig _instance = null;
	public static OracleConfig getConfig()
	{
		if( _instance == null )
			_instance = new OracleConfig();
		return _instance;
	}
	
	public OracleConfig()
	{
		super(NAME, HOMEPAGE, URL, HOST, PORT);
	}

	@Override public void initConfig()
	{
		super.initConfig(_HOST | _PORT);
		
		try {
			
			setDescription(getConfigName() + " Database delivers industry leading performance, scalability, security and reliability on a choice of clustered or single-servers running Windows, Linux, and UNIX.");
			setWarning("<center><b>" + getConfigName() + " requires the use of its external application found " + 
						"<a href='" + getDownloadURL() + "'>here.</a></b></center>");
			setImage(ImageIO.read(IconManager.IMAGE_ORACLE));

		} catch (IOException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		} catch (NumberFormatException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		}
	}
	@Override public boolean loadConfig() 
	{
		boolean result = ConfigManager.getConfigManager().setDatabase(_instance); 
		if( result )
			super.loadConfig();
		else
			JOptionPane.showMessageDialog(null, 
					"There was an error loading the " + getConfigName() + " config.\n" + 
					"Another config might already be loaded.", 
					"Error", JOptionPane.ERROR_MESSAGE);
		return result;
	}

	@Override public boolean unloadConfig()
	{
		boolean result = ConfigManager.getConfigManager().setDatabase(null);
		super.unloadConfig();
		return result;
	}
}