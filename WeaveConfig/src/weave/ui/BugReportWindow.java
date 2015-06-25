/*
    Weave (Web-based Analysis and Visualization Environment)
    Copyright (C) 2008-2015 University of Massachusetts Lowell

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

import static weave.utils.TraceUtils.STDERR;
import static weave.utils.TraceUtils.getLogFile;
import static weave.utils.TraceUtils.getSimpleClassAndMsg;
import static weave.utils.TraceUtils.trace;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.html.HTMLDocument;

import weave.Settings;
import weave.utils.LaunchUtils;

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
	private 		int				MARGIN				= 20;
	
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

		_instance.setSize(450, 350); 	// 394 x 297 (inner)
		_instance.setResizable(false);
		_instance.setLayout(null);
		_instance.setBackground(new Color(0xF0F0F0));
		_instance.setTitle("Bug Reporter");
		_instance.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		_instance.setLocation(screen.width/2 - getWidth()/2, screen.height/2 - getHeight()/2);

		JEditorPane titleContainer 		= new JEditorPane();
		JEditorPane exceptionContainer	= new JEditorPane();
		JEditorPane messageContainer 	= new JEditorPane();
		final JCheckBox checkbox 		= new JCheckBox("Tell developers about the bug", true);
		final JButton detailsButton 	= new JButton("Details...");
		final JTextArea commentPanel 	= new JTextArea(defaultComment);
		final JScrollPane commentScroller = new JScrollPane(commentPanel);
		JButton close					= new JButton("Close");
		Font titleFont 					= new Font(Settings.FONT, Font.BOLD, 11);
		Font textFont					= new Font(Settings.FONT, Font.PLAIN, 10);
		String titleCSSRule				= "body { font-family: " + titleFont.getFamily() + "; font-size: " + titleFont.getSize() + "px; }";
		String textCSSRule				= "body { font-family: " + textFont.getFamily() + ";  font-size: " + textFont.getSize() + "px; }";
		
		String title = "<b>" + Settings.CURRENT_PROGRAM_NAME + " has encountered a bug</b>";
		titleContainer.setBounds(MARGIN, 10, getWidth() - 2 * MARGIN, 35);
		titleContainer.setContentType("text/html");
		titleContainer.setText(title);
		titleContainer.setOpaque(true);
		titleContainer.setBackground(new Color(240,240,240,0));
		titleContainer.setEditable(false);
		titleContainer.setVisible(true);
		((HTMLDocument)titleContainer.getDocument()).getStyleSheet().addRule(titleCSSRule);
		titleContainer.addMouseListener(new MouseListener() {
			@Override public void mouseReleased(MouseEvent e) {
				_instance.invalidate();
				_instance.repaint();
			}
			@Override public void mousePressed(MouseEvent e) { }
			@Override public void mouseExited(MouseEvent e) { }
			@Override public void mouseEntered(MouseEvent e) { }
			@Override public void mouseClicked(MouseEvent e) { }
		});

		String exception = "<center><i>" + getSimpleClassAndMsg(e) + "</i></center>";
		exceptionContainer.setBounds(MARGIN, 45, getWidth() - 2 * MARGIN, 50);
		exceptionContainer.setContentType("text/html");
		exceptionContainer.setText(exception);
		exceptionContainer.setFont(new Font(Settings.FONT, Font.PLAIN, 12));
		exceptionContainer.setOpaque(true);
		exceptionContainer.setBackground(new Color(240,240,240,0));
		exceptionContainer.setEditable(false);
		exceptionContainer.setVisible(true);
		((HTMLDocument)exceptionContainer.getDocument()).getStyleSheet().addRule(textCSSRule);
		exceptionContainer.addMouseListener(new MouseListener() {
			@Override public void mouseReleased(MouseEvent e) {
				_instance.invalidate();
				_instance.repaint();
			}
			@Override public void mousePressed(MouseEvent e) { }
			@Override public void mouseExited(MouseEvent e) { }
			@Override public void mouseEntered(MouseEvent e) { }
			@Override public void mouseClicked(MouseEvent e) { }
		});
		
		String  message  = "Continued use of the tool may cause stability issues.<br />";
				message += "It is recommended that you close the tool and try again.";
		messageContainer.setBounds(MARGIN, 95, getWidth() - 2 * MARGIN, 55);
		messageContainer.setContentType("text/html");
		messageContainer.setText(message);
		messageContainer.setOpaque(true);
		messageContainer.setBackground(new Color(240,240,240,0));
		messageContainer.setEditable(false);
		messageContainer.setVisible(true);
		((HTMLDocument)messageContainer.getDocument()).getStyleSheet().addRule(textCSSRule);
		messageContainer.addMouseListener(new MouseListener() {
			@Override public void mouseReleased(MouseEvent e) {
				_instance.invalidate();
				_instance.repaint();
			}
			@Override public void mousePressed(MouseEvent e) { }
			@Override public void mouseExited(MouseEvent e) { }
			@Override public void mouseEntered(MouseEvent e) { }
			@Override public void mouseClicked(MouseEvent e) { }
		});

		checkbox.setBounds(MARGIN, 150, getWidth() - 2 * MARGIN - 100, 25);
		checkbox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if( checkbox.isSelected() ) {
					CLOSE_OPTION = YES_OPTION;
					detailsButton.setEnabled(true);
					commentPanel.setEnabled(true);
					commentScroller.setEnabled(true);
				} else {
					CLOSE_OPTION = NO_OPTION;
					detailsButton.setEnabled(false);
					commentPanel.setEnabled(false);
					commentScroller.setEnabled(false);
				}
			}
		});
		checkbox.setVisible(true);
		checkbox.setSelected(true);
		
		detailsButton.setBounds(getWidth() - 100 - MARGIN, 150, 100, 25);
		detailsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if( getLogFile(STDERR).exists() )
						LaunchUtils.open(getLogFile(STDERR).getAbsolutePath());
				} catch (IOException | InterruptedException e) {
					trace(STDERR, e);
				}
			}
		});
		detailsButton.setEnabled(true);
		detailsButton.setVisible(true);
		
		commentPanel.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent arg0) {
				if( commentPanel.getText().trim().length() == 0 )
					commentPanel.setText(defaultComment);
			}
			@Override
			public void focusGained(FocusEvent arg0) {
				if( commentPanel.getText().trim().equals(defaultComment) )
					commentPanel.setText("");
			}
		});
		
		commentScroller.setBounds(MARGIN, 180, getWidth() - 2 * MARGIN, 75);
		commentScroller.setVisible(true);
		
		close.setBounds(getWidth() / 2 - 50, 260, 100, 25);
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
		
		_instance.add(titleContainer);
		_instance.add(exceptionContainer);
		_instance.add(messageContainer);
		_instance.add(detailsButton);
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
	
	public class _internal
	{
		public String comment = "";
	}
}
