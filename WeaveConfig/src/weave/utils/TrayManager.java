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

package weave.utils;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import weave.Settings;

public class TrayManager 
{
	private static JFrame		_parent			= null;
	
	public static SystemTray	systemTray		= null;
	public static TrayIcon		trayIcon		= null;
	public static PopupMenu		popupMenu		= null;
	
	public static Image			trayIconOnline	= null;
	public static Image			trayIconOffline	= null;
	public static Image			trayIconError	= null;

	private static MenuItem restoreItem 		= null;
	private static MenuItem onOffItem			= null;
	private static MenuItem aboutItem 			= null;
	private static Menu 	quickLinksMenu 		= null;
	private static MenuItem adminConsoleItem 	= null;
	private static MenuItem installerWikiItem 	= null;
	private static MenuItem exitItem 			= null;
	
//	private static enum states					{ ONLINE, OFFLINE, ERROR };
//	private static states state					= states.OFFLINE;
	
	public static void initializeTray( JFrame p ) throws IOException
	{
		TraceUtils.trace(TraceUtils.STDOUT, "-> Initializing System Tray.......");
		
//		trayIconOnline = ImageIO.read(TrayManager.class.getResource("/resources/logo_green.png"));
//		trayIconOffline = ImageIO.read(TrayManager.class.getResource("/resources/logo_orange.png"));
//		trayIconError = ImageIO.read(TrayManager.class.getResource("/resources/logo_red.png"));
		
		trayIconOnline = ImageIO.read(TrayManager.class.getResource("/resources/logo2.png"));
		trayIconOffline = ImageIO.read(TrayManager.class.getResource("/resources/logo2.png"));
		trayIconError = ImageIO.read(TrayManager.class.getResource("/resources/logo2.png"));

		if( !SystemTray.isSupported() ) {
			TraceUtils.put(TraceUtils.STDOUT, "FAILED");
			return;
		}
		
		_parent = p;
		
		systemTray = SystemTray.getSystemTray();
		popupMenu = new PopupMenu();
		
		restoreItem = new MenuItem("Restore " + Settings.INSTALLER_NAME);
		onOffItem = new MenuItem("Go into " + ((Settings.isOfflineMode()) ? "Online" : "Offline") + " Mode");
		aboutItem = new MenuItem("About");
		quickLinksMenu = new Menu("Quick Links");
		adminConsoleItem = new MenuItem("Admin Console");
		installerWikiItem = new MenuItem("Help");
		exitItem = new MenuItem("Exit");
		
		popupMenu.add(restoreItem);
		popupMenu.add(onOffItem);
		popupMenu.addSeparator();
		popupMenu.add(quickLinksMenu);
		quickLinksMenu.add(adminConsoleItem);
		quickLinksMenu.add(installerWikiItem);
		popupMenu.add(aboutItem);
		popupMenu.addSeparator();
		popupMenu.add(exitItem);
		
		trayIcon = new TrayIcon(trayIconOffline, Settings.INSTALLER_NAME);
		trayIcon.setImageAutoSize(true);
		trayIcon.setPopupMenu(popupMenu);
		
		setupActionListeners();
		setupWindowListeners();
		
        try {
			systemTray.add(trayIcon);
		} catch (AWTException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
		}

		TraceUtils.put(TraceUtils.STDOUT, "DONE");
	}
	
	public static void setTooltip( String text )
	{
		if( !SystemTray.isSupported() ) return;
		trayIcon.setToolTip(text);
	}
	public static void setImage( Image img )
	{
		if( !SystemTray.isSupported() ) return;
		trayIcon.setImage(img);
	}
	public static void setImageAutoSize( boolean b )
	{
		if( !SystemTray.isSupported() ) return;
		trayIcon.setImageAutoSize(b);
	}
	
	public static void displayTrayMessage( String caption, String text, MessageType type )
	{
		if( !SystemTray.isSupported() ) return;
		trayIcon.displayMessage(caption, text, type);
	}

	private static void setupActionListeners()
	{
		if( !SystemTray.isSupported() ) return;
		
		// Double click on the tray icon
        trayIcon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	_parent.setVisible( !_parent.isVisible() );
            	_parent.setExtendedState( _parent.isVisible() ? JFrame.NORMAL : JFrame.ICONIFIED );
            }
        });
        
        // Restore Menu Item
        restoreItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				_parent.setVisible(true);
				_parent.setExtendedState(JFrame.NORMAL);
			}
		});
        
        // Online / Offline Mode
        onOffItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if( Settings.isOfflineMode() )
					Settings.LAUNCH_MODE = Settings.MODE.ONLINE_MODE;
				else
					Settings.LAUNCH_MODE = Settings.MODE.OFFLINE_MODE;
				
				Settings.save();
				
				try {
					LaunchUtils.launchWeaveUpdater();
					Settings.shutdown();
				} catch (IOException e1) {
					TraceUtils.trace(TraceUtils.STDERR, e1);
				} catch (InterruptedException e1) {
					TraceUtils.trace(TraceUtils.STDERR, e1);
				}
			}
		});
        
        // Quick Links - Admin Console
        adminConsoleItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if( Desktop.isDesktopSupported() ) {
					try {
						Desktop.getDesktop().browse(new URI("http://" + Settings.LOCALHOST + ":"	+ Settings.ACTIVE_CONTAINER_PLUGIN.getPort() + "/AdminConsole.html"));
					} catch (IOException e1) {			TraceUtils.trace(TraceUtils.STDERR, e1);
					} catch (URISyntaxException e1) {	TraceUtils.trace(TraceUtils.STDERR, e1);
					} catch (Exception e1) {			TraceUtils.trace(TraceUtils.STDERR, e1);
					}
				} else
					JOptionPane.showMessageDialog(null, "Feature not supported.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
        
        // Quick Links - Wiki Help
        installerWikiItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if( Desktop.isDesktopSupported() ) {
					try {
						Desktop.getDesktop().browse(new URI(Settings.WIKI_HELP_PAGE));
					} catch (IOException e1) {			TraceUtils.trace(TraceUtils.STDERR, e1);
					} catch (URISyntaxException e1) {	TraceUtils.trace(TraceUtils.STDERR, e1);
					}
				} else
					JOptionPane.showMessageDialog(null, "Feature not supported.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
        
        // About Menu Item
        aboutItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(null, "This is the about menu popup.");
			}
		});
        
        // Exit Menu Item
        exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				systemTray.remove(trayIcon);
				Settings.shutdown();
			}
		});
	}
	
	private static void setupWindowListeners()
	{
		if( !SystemTray.isSupported() ) return;
		
		_parent.addWindowStateListener(new WindowStateListener() {
			@Override
			public void windowStateChanged(WindowEvent e) {
				if( e.getNewState() == JFrame.ICONIFIED || e.getNewState() == 7) {
					_parent.setVisible(false);
					if( !Settings.INSTALLER_POPUP_SHOWN ) {
						displayTrayMessage(Settings.INSTALLER_NAME, Settings.INSTALLER_NAME + " is still running!\nDouble click on this icon to restore", TrayIcon.MessageType.INFO);
						Settings.INSTALLER_POPUP_SHOWN = true;
					}
				} else if( e.getNewState() == JFrame.MAXIMIZED_BOTH || e.getNewState() == JFrame.NORMAL ) {
					_parent.setVisible(true);
				}
			}
		});
	}
}
