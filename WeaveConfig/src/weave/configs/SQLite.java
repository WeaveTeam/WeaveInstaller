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
	private static SQLite _instance = null;
	public static SQLite getConfig()
	{
		if( _instance == null )
			_instance = new SQLite();
		return _instance;
	}
	
	
	public SQLite()
	{
		super("SQLite");
		
		try {
			setTechLevel("Easy");
			setDescription(	"SQLite is a software library that implements a self-contained, " +
							"serverless, zero-configuration, transactional SQL database engine.");
			setImage(ImageIO.read(IconManager.IMAGE_SQLITE));
		} catch (IOException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		}
	}
	
	@Override public void loadConfig()
	{
		if( ConfigManager.getConfigManager().setDatabase(_instance) )
			super.loadConfig();
		else
			JOptionPane.showMessageDialog(null, 
					"There was an error loading the " + CONFIG_NAME + " plugin.\n" + 
					"Another plugin might already be loaded.", 
					"Error", JOptionPane.ERROR_MESSAGE);
	}

	@Override public void unloadConfig() 
	{
		ConfigManager.getConfigManager().setDatabase(null);
		super.unloadConfig();
	}
}