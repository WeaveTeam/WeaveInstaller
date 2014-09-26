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

package weave.inc;

import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class SetupPanel extends JPanel implements ISetupPanel 
{
	public static final int LEFT_PANEL_WIDTH 	= 150;
	public static final int LEFT_PANEL_HEIGHT 	= 375;
	
	public static final int BOTTOM_PANEL_WIDTH 	= 600;
	public static final int BOTTOM_PANEL_HEIGHT = 50;
	
	public static final int RIGHT_PANEL_WIDTH 	= 450;
	public static final int RIGHT_PANEL_HEIGHT 	= 375;
	
	
	protected int 					currentPanel ;
	protected int 					maxPanels 	 ;
	public 	  ArrayList<JPanel> 	panels 		 = new ArrayList<JPanel>();
	
	public int getCurrentPanelIndex() {
		return currentPanel;
	}

	public int getNumberOfPanels() {
		return maxPanels;
	}

	public void nextPanel() {
		if( currentPanel < maxPanels)
		{
			hidePanels();
			panels.get(++currentPanel).setVisible(true);
		}
	}

	public void previousPanel() {
		if( currentPanel > 0 )
		{
			hidePanels();
			panels.get(--currentPanel).setVisible(true);
		}
	}

	public boolean isFirstPanel() {
		return getCurrentPanelIndex() == 0;
	}

	public boolean isLastPanel() {
		return getCurrentPanelIndex() == getNumberOfPanels() - 1;
	}
	
	public void showFirstPanel() {
		hidePanels();
		currentPanel = 0;
		panels.get(currentPanel).setVisible(true);
	}

	public void hidePanels() {
		for( int i = 0; i < maxPanels; i++ )
			panels.get(i).setVisible(false);
	}

	public void addActionToButton(JButton button, ActionListener action) {
		button.addActionListener(action);
	}
}
