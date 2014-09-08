/*
    Weave (Web-based Analysis and Visualization Environment)
    Copyright (C) 2008-2014 University of Massachusetts Lowell

    This file is a part of Weave.

    Weave is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License, Version 3,
    as published by the Free Software Foundation.

    Weave is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Weave.  If not, see <http://www.gnu.org/licenses/>.
*/

package weave.configs;

import java.awt.image.BufferedImage;
import java.io.File;

public interface IConfig 
{
	public void initConfig();
	public void loadConfig();
	public void unloadConfig();
	public boolean isConfigLoaded();
	
	public String getConfigName();
	
	public String getURL();
	public void setURL(String s);
	
	public String getDescription();
	public void setDescription(String s);
	
	public String getWarning();
	public void setWarning(String s);
	
	public BufferedImage getImage();
	public void setImage(BufferedImage i);
	
	public File getWebappsDirectory();
	public void setWebappsDirectory(File f);
	public void setWebappsDirectory(String s);
	
	public File getInstallFile();
	public void setInstallFile(File f);
	
	public int getPort();
	public void setPort(int i);
	public void setPort(String s);
}
