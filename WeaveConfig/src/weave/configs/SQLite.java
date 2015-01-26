package weave.configs;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import weave.managers.ConfigManager;
import weave.managers.IconManager;
import weave.utils.BugReportUtils;
import weave.utils.TraceUtils;

public class SQLite extends Config
{
	public static final String NAME = "SQLite";
	private static SQLite _instance = null;
	
	public static SQLite getConfig()
	{
		if( _instance == null )
			_instance = new SQLite();
		return _instance;
	}
	
	
	public SQLite()
	{
		super(NAME);
	}
	
	@Override public void initConfig() 
	{
		super.initConfig(0);
		
		try {
			
			setDescription(	getConfigName() + " is a software library that implements a self-contained, " +
							"serverless, zero-configuration, transactional SQL database engine.");
			setWarning(	"<center><b>" + getConfigName() + " will run inside the tool and does not require an external application.<br>" +
						"This is the appropriate choice for new users.</b></center>");
			setImage(ImageIO.read(IconManager.IMAGE_SQLITE));

		} catch (IOException e) {
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
					"There was an error loading the " + getConfigName() + " plugin.\n" + 
					"Another plugin might already be loaded.", 
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
