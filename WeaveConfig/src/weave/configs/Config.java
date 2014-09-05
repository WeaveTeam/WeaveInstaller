package weave.configs;

import java.awt.image.BufferedImage;
import java.io.File;

import weave.utils.TraceUtils;

public class Config implements IConfig
{
	protected String 	CONFIG_NAME 	= "";

	protected String 	URL 			= null;
	protected File 		WEBAPPS 		= null;
	protected File 		INSTALL_FILE 	= null;
	protected int 		PORT 			= 0;
	protected boolean 	LOADED			= false;

	protected BufferedImage	_icon			= null;
	protected String 		_description	= "";
	protected String		_warning		= "";
	protected String		_techLevel		= "";
	
	public Config() {
		
	}
	
	public Config(String name) {
		CONFIG_NAME = name;
	}
	
	public Config(String name, String url) {
		CONFIG_NAME = name;
		URL = url;
	}
	
	@Override
	public void initConfig() {

	}

	@Override
	public void loadConfig() {
		LOADED = true;
	}

	@Override
	public void unloadConfig() {
		LOADED = false;
	}

	@Override public String getConfigName() 			{ return 	CONFIG_NAME; }
	@Override public String getURL() 					{ return 	URL; }
	@Override public File getWebappsDirectory() 		{ return 	WEBAPPS; }
	@Override public File getInstallFile() 				{ return 	INSTALL_FILE; }
	@Override public int getPort() 						{ return 	PORT; }
	@Override public boolean isConfigLoaded() 			{ return 	LOADED; }
	@Override public void setURL(String s) 				{ 			URL = s; }
	@Override public void setWebappsDirectory(File f) 	{ 			WEBAPPS = f; }
	@Override public void setInstallFile(File f) 		{ 			INSTALL_FILE = f; }
	@Override public void setPort(int i) 				{ 			PORT = i; }

	@Override public String getTechLevel() 				{ return 	_techLevel; }
	@Override public String getDescription() 			{ return 	_description; }
	@Override public String getWarning()				{ return	_warning; }
	@Override public BufferedImage getImage() 			{ return 	_icon; }
	@Override public void setTechLevel(String s) 		{ 			_techLevel = s; }
	@Override public void setDescription(String s) 		{			_description = s; }
	@Override public void setWarning(String s)			{			_warning = s; }
	@Override public void setImage(BufferedImage i) 	{ 			_icon = i; }

	@Override public void setWebappsDirectory(String s) {
		if( s != null )
			setWebappsDirectory(new File(s));
		else
			setWebappsDirectory((File)null);
	}

	@Override public void setPort(String s) {
		int i = 0;
		
		try {
			i = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
		}
		setPort(i);
	}
}
