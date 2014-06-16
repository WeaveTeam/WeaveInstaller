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
	protected int 					currentPanel ;
	protected int 					maxPanels 	 ;
	public 	  ArrayList<JPanel> 	panels 		 = new ArrayList<JPanel>();
	
	@Override
	public int getCurrentPanelIndex() {
		return currentPanel;
	}

	@Override
	public int getNumberOfPanels() {
		return maxPanels;
	}

	@Override
	public void nextPanel() {
		if( currentPanel < maxPanels)
		{
			hidePanels();
			panels.get(++currentPanel).setVisible(true);
		}
	}

	@Override
	public void previousPanel() {
		if( currentPanel > 0 )
		{
			hidePanels();
			panels.get(--currentPanel).setVisible(true);
		}
	}

	@Override
	public boolean isFirstPanel() {
		return getCurrentPanelIndex() == 0;
	}

	@Override
	public boolean isLastPanel() {
		return getCurrentPanelIndex() == getNumberOfPanels() - 1;
	}
	
	@Override
	public void showPanel() {
		hidePanels();
		currentPanel = 0;
		panels.get(currentPanel).setVisible(true);
	}

	@Override
	public void hidePanels() {
		for( int i = 0; i < maxPanels; i++ )
			panels.get(i).setVisible(false);
	}

	@Override
	public void addActionToButton(JButton button, ActionListener action) {
		button.addActionListener(action);
	}
}
