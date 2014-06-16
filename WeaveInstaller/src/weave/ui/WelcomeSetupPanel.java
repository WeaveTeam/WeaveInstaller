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

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import weave.inc.SetupPanel;

@SuppressWarnings("serial")
public class WelcomeSetupPanel extends SetupPanel
{
	public WelcomeSetupPanel()
	{
		maxPanels = 1;
		
		setLayout(null);
		setSize(350, 325);
		setBounds(0, 0, 350, 325);
		
		JPanel panel = null;
		for (int i = 0; i < maxPanels; i++) {
			switch (i) {
				case 0: panel = createWelcomePanel(); 	break;
			}
			panels.add(panel);
			add(panel);
		}
		hidePanels();
	}

	public JPanel createWelcomePanel() 
	{
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBounds(0, 0, 500 - 150, 325);
		panel.setBackground(new Color(0xFFFFFF));

		JLabel welcome = new JLabel("Welcome to Weave Setup Wizard");
		welcome.setFont(new Font("Corbel", Font.BOLD, 17));
		welcome.setBounds(30, 30, 290, 50);

		JTextArea info = new JTextArea(10, 8);
		info.setEditable(false);
		info.setLineWrap(true);
		info.setText("The Setup Wizard will help install Weave" + "\n" +
					"on your computer. " + "\n\n" +
					"Click Next to continue or Cancel to exit" + "\n" +
					"the Setup Wizard.");
		info.setFont(new Font("Corbel", Font.PLAIN, 14));
		info.setBounds(30, 100, 290, 150);

		panel.add(welcome);
		panel.add(info);
		return panel;
	}
}
