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

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.text.html.HTMLDocument;

import weave.Settings;
import weave.inc.SetupPanel;

@SuppressWarnings("serial")
public class WelcomeSetupPanel extends SetupPanel
{
	public WelcomeSetupPanel()
	{
		maxPanels = 1;
		
		setLayout(null);
		setSize(350, 325);
		setBounds(0, 0, SetupPanel.RIGHT_PANEL_WIDTH, SetupPanel.RIGHT_PANEL_HEIGHT);
		
		JPanel panel = null;
		for (int i = 0; i < maxPanels; i++) {
			switch (i) {
				case 0: panel = createWelcomePanel(); 	break;
			}
			panels.add(panel);
			add(panel);
		}
		hidePanels();
		
		globalHashMap.put("WelcomeSetupPanel", WelcomeSetupPanel.this);
	}

	public JPanel createWelcomePanel() 
	{
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBounds(0, 0, getWidth(), getHeight());
		panel.setBackground(new Color(0xFFFFFF));

		JEditorPane welcome = new JEditorPane();
		welcome.setBounds(30, 30, 390, 70);
		welcome.setBackground(Color.WHITE);
		welcome.setEditable(false);
		welcome.setContentType("text/html");
		welcome.setFont(new Font(Settings.FONT, Font.BOLD, 14));
		String welcomeStyle =	"body { font-family: " + welcome.getFont().getFamily() + "; " + 
								"font-size: " + welcome.getFont().getSize() + "px; }";
		((HTMLDocument)welcome.getDocument()).getStyleSheet().addRule(welcomeStyle);
		welcome.setText("<center><b>Welcome to the<br>" + Settings.SERVER_NAME + "</b></center>");

		JEditorPane info = new JEditorPane();
		info.setBounds(30, 120, 390, 200);
		info.setBackground(Color.WHITE);
		info.setEditable(false);
		info.setContentType("text/html");
		info.setFont(new Font(Settings.FONT, Font.PLAIN, 11));
		String infoStyle =	"body { font-family: " + info.getFont().getFamily() + "; " + 
							"font-size: " + info.getFont().getSize() + "px; }";
		((HTMLDocument)info.getDocument()).getStyleSheet().addRule(infoStyle);
		info.setText("This tool will assist you in quickly setting up Weave on your system.");

		panel.add(welcome);
		panel.add(info);
		return panel;
	}
}
