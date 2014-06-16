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

import javax.swing.JButton;

public interface ISetupPanel
{
	/**
	 * Get the current panel index of the setup panel
	 * 
	 * @return the index of the current panel
	 */
	public int getCurrentPanelIndex();
	
	/**
	 * Get the total number of panels within the setup
	 * 
	 * @return the total number of panels
	 */
	public int getNumberOfPanels();
	
	/**
	 * Increase the index of the current panel and show the panel
	 */
	public void nextPanel();
	
	/**
	 * Decrement the index of the current panel and show the panel
	 */
	public void previousPanel();
	
	/**
	 * Check if we are on the first panel in the list
	 * 
	 * @return <code>true</code> if we are on the first panel, <code>false</code> otherwise
	 */
	public boolean isFirstPanel();
	
	/**
	 * Check if we are on the last panel in the list.
	 * 
	 * @return <code>true</code> if we are on the last panel, <code>false</code> otherwise
	 */
	public boolean isLastPanel();
	
	/**
	 * Shows only the first panel of a SetupPanel group
	 */
	public void showPanel();
	
	/**
	 * Hides all panels within the SetupPanel group
	 */
	public void hidePanels();
	
	/**
	 * Add an action to the specified button
	 * 
	 * @param button The button to add the action to
	 * @param action The action to add to the button
	 */
	public void addActionToButton(JButton button, ActionListener action);
}
