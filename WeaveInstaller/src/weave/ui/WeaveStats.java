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

import weave.Settings;
import weave.utils.UpdateUtils;

@SuppressWarnings("serial")
public class WeaveStats extends JPanel 
{
	JLabel desc = null, status = null, desc2 = null;
	public JLabel lastUpdateTime = null;
	
	public WeaveStats()
	{
		setLayout(null);
		setBackground(new Color(0xFFFFFF));
		
		desc = new JLabel("Weave Update:");
		desc.setFont(new Font(Settings.FONT, Font.BOLD, 14));
		
		status = new JLabel("Loading...");
		status.setFont(new Font(Settings.FONT, Font.BOLD, 14));
		
		desc2 = new JLabel("Last Check:");
		desc2.setFont(new Font(Settings.FONT, Font.BOLD, 13));
		
		lastUpdateTime = new JLabel(Settings.LAST_UPDATE_CHECK);
		lastUpdateTime.setFont(new Font(Settings.FONT, Font.BOLD, 13));
		
		desc.setBounds(0, 0, 115, 25);
		status.setBounds(115, 0, 140, 25);
		desc2.setBounds(0, 30, 140, 20);
		lastUpdateTime.setBounds(115, 30, 140, 20);

		add(desc);
		add(status);
		add(desc2);
		add(lastUpdateTime);
	}
	
	/** 
	 * Refreshes the update status.
	 * 
	 * @param _status 1 for available update; 0 for up to date; else error.
	 */
	public void refresh(int _status)
	{
		if( _status == UpdateUtils.UPDATE_AVAILABLE ) {
			status.setText("Update Available");
			status.setForeground(new Color(0x004F00));
		} else if( _status == UpdateUtils.NO_UPDATE_AVAILABLE ) {
			status.setText("Up to Date");
			status.setForeground(Color.BLACK);
		} else if( _status == UpdateUtils.UPDATE_OFFLINE ) {
			status.setText("Offline...");
			status.setForeground(Color.BLACK);
		} else {
			status.setText("Updating Error");
			status.setForeground(Color.RED);
		}
		lastUpdateTime.setText(Settings.LAST_UPDATE_CHECK);
	}
}
