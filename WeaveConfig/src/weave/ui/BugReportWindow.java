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
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import weave.Settings;
import weave.utils.TraceUtils;
import weave.utils.TrayManager;

@SuppressWarnings("serial")
public class BugReportWindow extends JFrame 
{
	public static int 		YES_OPTION 		= 1 << 0;
	public static int 		NO_OPTION 		= 1 << 1;
	public static int 		YES_NO_OPTIONS 	= YES_OPTION | NO_OPTION;
	public 		  int 		CLOSE_OPTION	= YES_OPTION;
	public static String 	defaultComment	= "Additional Comments...";
	
	private static 	BugReportWindow _instance 			= null;
	private 		Dimension 		screen 				= Toolkit.getDefaultToolkit().getScreenSize();
	private			boolean			closedWithButton	= false;
	
	public _internal data = new _internal();
	
	public static BugReportWindow instance(Throwable e)
	{
		if( _instance == null )
			_instance = new BugReportWindow(e);
		
		return _instance;
	}
	
	public BugReportWindow(Throwable e)
	{
		_instance = this;
		
		_instance.setSize(400, 300); 	// 394 x 272 (inner)
		_instance.setResizable(false);
		_instance.setLayout(null);
		_instance.setTitle("Bug Reporter");
		_instance.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		_instance.setLocation(screen.width/2 - getWidth()/2, screen.height/2 - getHeight()/2);

		Icon icon 						= new ImageIcon( resizeImage(30, 30, TrayManager.trayIconOffline) );
		JLabel iconLabel 				= new JLabel(icon);
		JEditorPane titleContainer 		= new JEditorPane();
		JEditorPane messageContainer 	= new JEditorPane();
		final JCheckBox checkbox 		= new JCheckBox("Tell developers about the bug", true);
		final JButton details 			= new JButton("Details...");
		final JTextArea commentPanel 	= new JTextArea(defaultComment);
		final JScrollPane commentScroller = new JScrollPane(commentPanel);
		JButton close					= new JButton("Close");
		
		iconLabel.setBounds(20, 10, 30, 30);
		iconLabel.setVisible(true);
		
		String title = "<b>" + Settings.CURRENT_PROGRAM_NAME + " has encountered a bug</b>";
		
		titleContainer.setBounds(65, 15, 275, 30);
		titleContainer.setContentType("text/html");
		titleContainer.setText(title);
		titleContainer.setBackground(new Color(0xF0F0F0));
		titleContainer.setEditable(false);
		titleContainer.setVisible(true);
		
		String message  = "Continued use of the tool may cause stability issues.<br />";
			   message += "It is recommended that you close the tool and try again.";
		
		messageContainer.setBounds(20, 50, 354, 55);
		messageContainer.setContentType("text/html");
		messageContainer.setText(message);
		messageContainer.setBackground(new Color(0xF0F0F0));
		messageContainer.setEditable(false);
		messageContainer.setVisible(true);
		
		details.setBounds(284, 110, 90, 25);
		details.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if( Desktop.isDesktopSupported() ) {
					if( TraceUtils.getLogFile(TraceUtils.STDERR).exists() ) {
						try {
							Desktop.getDesktop().open(TraceUtils.getLogFile(TraceUtils.STDERR));
						} catch (IOException e) {
							TraceUtils.trace(TraceUtils.STDERR, e);
						}
					}
				}
			}
		});
		details.setEnabled(true);
		details.setVisible(true);
		
		checkbox.setBounds(20, 110, 190, 25);
		checkbox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if( checkbox.isSelected() ) {
					CLOSE_OPTION = YES_OPTION;
					details.setEnabled(true);
					commentPanel.setEnabled(true);
					commentScroller.setEnabled(true);
				} else {
					CLOSE_OPTION = NO_OPTION;
					details.setEnabled(false);
					commentPanel.setEnabled(false);
					commentScroller.setEnabled(false);
				}
			}
		});
		checkbox.setVisible(true);
		checkbox.setSelected(true);
		
		commentPanel.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent arg0) {
				if( commentPanel.getText().trim().length() == 0 )
					commentPanel.setText("Additional Comments...");
			}
			@Override
			public void focusGained(FocusEvent arg0) {
				if( commentPanel.getText().trim().equals("Additional Comments...") )
					commentPanel.setText("");
			}
		});
		
		commentScroller.setBounds(40, 140, 334, 75);
		commentScroller.setVisible(true);
		
		close.setBounds(157, 225, 80, 25);
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				closedWithButton = true;
				if( checkbox.isSelected() )
					data.comment = commentPanel.getText();
				
				dispatchEvent(new WindowEvent(_instance, WindowEvent.WINDOW_CLOSING));
			}
		});
		close.setEnabled(true);
		close.setVisible(true);
		
		_instance.add(iconLabel);
		_instance.add(titleContainer);
		_instance.add(messageContainer);
		_instance.add(details);
		_instance.add(checkbox);
		_instance.add(commentScroller);
		_instance.add(close);
		
		_instance.addWindowListener(new WindowListener() {
			@Override public void windowOpened(WindowEvent arg0) { }
			@Override public void windowIconified(WindowEvent arg0) { }
			@Override public void windowDeiconified(WindowEvent arg0) { }
			@Override public void windowDeactivated(WindowEvent arg0) {	}
			@Override public void windowClosing(WindowEvent arg0) {
				if( closedWithButton && !checkbox.isSelected() ) {
					CLOSE_OPTION = NO_OPTION;
				}
			}
			@Override public void windowClosed(WindowEvent arg0) { }
			@Override public void windowActivated(WindowEvent arg0) { }
		});
	}
	
	private static BufferedImage resizeImage(int width, int height, Image original)
	{
		BufferedImage resized = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
		Graphics2D g = resized.createGraphics();
		g.drawImage(original, 0, 0, width, height, null);
		g.dispose();
		
		return resized;
	}
	
	public class _internal
	{
		public String comment = "";
	}
}
