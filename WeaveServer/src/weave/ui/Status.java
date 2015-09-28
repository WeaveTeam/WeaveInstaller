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
import java.awt.GridLayout;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Status extends JPanel
{
	int lastStatus = -1;
	JLabel label = null;
	JLabel status = null;
	BufferedImage status_OK = null;
	BufferedImage status_BAD = null;
	
	/**
	 * 
	 * Constructor for status elements.
	 * 
	 * @param _label	Label name for this status element.
	 * @param _status	Boolean value indicating whether the status is OK or BAD.
	 */
	public Status(String _label, Boolean _status)
	{
		/* Setup a layout and add image elements to corresponding status */
		setLayout(new GridLayout(1, 2));
		try {			
			status_OK = ImageIO.read(Status.class.getResource("/resources/check_19x18.png"));// ClassLoader.getSystemResource("resources\\check_19x18.png"));
			status_BAD = ImageIO.read(Status.class.getResource("/resources/warning_21x18.png"));// ClassLoader.getSystemResource("resources\\warning_21x18.png"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		/* Create a new label and add it to the GUI, then refresh the status */
		label = new JLabel(_label);
		label.setFont(new Font("Serif", Font.BOLD, 14));
		
		add(label);
		
		refresh(_status);
	}
	
	/**
	 * 
	 * Asynchronous update of status of TOMCAT and MySQL ( running or not running ).
	 * This function simply updates the GUI.
	 * 
	 * @param _status	Boolean value indicating whether status is OK or BAD.
	 */
	public void refresh(Boolean _status)
	{
		if( lastStatus == ((_status)?1:0))
			return;
		
		lastStatus = ((_status)?1:0);
		
		if( status != null )
		{
			remove(status);
			status = null;
		}

		if( _status ) {
			status = new JLabel("Running", new ImageIcon(status_OK), JLabel.LEFT);
			status.setForeground(new Color(0x004F00));
		} else {
			status = new JLabel("Not Running", new ImageIcon(status_BAD), JLabel.LEFT);
			status.setForeground(Color.RED);
		}
		status.setFont(new Font("Serif", Font.BOLD, 20));
		
		add(status);
		validate();
	}
	
}
