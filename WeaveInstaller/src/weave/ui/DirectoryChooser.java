/*
    Weave (Web-based Analysis and Visualization Environment)
    Copyright (C) 2008-2011 University of Massachusetts Lowell

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

package weave.ui;

import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import weave.Settings;
import weave.plugins.Tomcat;

@SuppressWarnings("serial")
public class DirectoryChooser extends JPanel 
{
	public JTextField textField;
	public JFileChooser fileChooser;
	JLabel label;
	
	/**
	 * JTextField for choosing the directory of the Tomcat installation folder if one is not detected.
	 * 
	 * @param _label
	 */
	public DirectoryChooser(String _label)
	{
		setLayout(new GridLayout(1, 2));
		
		fileChooser = new JFileChooser(Settings.USER_HOME);
		label = new JLabel(_label);
		label.setFont(new Font(Settings.FONT, Font.BOLD, 14));
		
		if( Tomcat.getConfig().getHomeDirectory() == null )
			textField = new JTextField("", 19);
		else
			textField = new JTextField(Tomcat.getConfig().getHomeDirectory().getAbsolutePath(), 19);
		
		textField.setEditable(false);
		
		add(label);
		add(textField);
	}
}
