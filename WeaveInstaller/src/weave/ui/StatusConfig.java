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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Timer;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import weave.Settings.SERVICE_PORTS;
import weave.plugins.MySQL;
import weave.plugins.Tomcat;

@SuppressWarnings("serial")
public class StatusConfig extends JPanel 
{
	JLabel labelPort = null;
	public JTextField textPort = null;
	Timer inputTimer = null;
	
	public StatusConfig(String _label, final SERVICE_PORTS _portType)
	{
		setLayout(new GridLayout(1, 2));
		
		inputTimer = new Timer();
		labelPort = new JLabel(_label);
		if( _portType == SERVICE_PORTS.MySQL )
			textPort = new JTextField(Integer.toString(MySQL.getConfig().MYSQL_PORT));
		else
			textPort = new JTextField(Integer.toString(Tomcat.getConfig().TOMCAT_PORT));
		
		textPort.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if( textPort.getText().equals("") ) {
					JOptionPane.showMessageDialog(null, "Port value cannot be empty");
					if( _portType == SERVICE_PORTS.MySQL )
						MySQL.getConfig().MYSQL_PORT = 0;
					else
						Tomcat.getConfig().TOMCAT_PORT = 0;
					return;
				}
				
				int port;
				try {
					port = Integer.parseInt(textPort.getText());
					if( port > 65535 || port < 1 ) {
						JOptionPane.showMessageDialog(null, "Input values: 0 < port < 65535");
						if( _portType == SERVICE_PORTS.MySQL )
							MySQL.getConfig().MYSQL_PORT = 0;
						else
							Tomcat.getConfig().TOMCAT_PORT = 0;
						return;
					}
					if( _portType == SERVICE_PORTS.MySQL )
						MySQL.getConfig().MYSQL_PORT = port;
					else
						Tomcat.getConfig().TOMCAT_PORT = port;
				} catch (NumberFormatException fe) {
					JOptionPane.showMessageDialog(null, "Only numbers are allowed as inputs.");
					return;
				}
			}
		});
		labelPort.setFont(new Font("Serif", Font.BOLD, 14));

		add(labelPort);
		add(textPort);
	}
}
