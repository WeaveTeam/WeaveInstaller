/*
    Weave (Web-based Analysis and Visualization Environment)
    Copyright (C) 2008-2014 University of Massachusetts Lowell

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

package weave;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import sun.java2d.HeadlessGraphicsEnvironment;
import weave.inc.ISetupPanel;
import weave.inc.SetupPanel;
import weave.plugins.MySQL;
import weave.plugins.PluginManager;
import weave.plugins.Tomcat;
import weave.ui.CurSetupPanel;
import weave.ui.PostSetupPanel;
import weave.ui.PreSetupPanel;
import weave.utils.BugReportUtils;
import weave.utils.FileUtils;
import weave.utils.LaunchUtils;
import weave.utils.TraceUtils;
import weave.utils.TrayManager;
import weave.utils.UpdateUtils;

@SuppressWarnings("serial")
public class Installer extends JFrame
{
	public static Installer 			installer 		= null;
	public static final String 			PRE_SETUP		= "PRE_SETUP";
	public static final String			CUR_SETUP		= "CUR_SETUP";
	public static final String			POST_SETUP		= "POST_SETUP";
	private Dimension 					screen 			= Toolkit.getDefaultToolkit().getScreenSize();
	
	// === Left Panel === //
	public SetupPanel					leftPanel		= null;
	
	// === Right Panel === //
	public SetupPanel 					rightPanel		= null;
	public PreSetupPanel 				preSP 			= null;
	public CurSetupPanel 				curSP			= null;
	public PostSetupPanel 				postSP 			= null;
	public HashMap<String, SetupPanel>	setupPanels		= new HashMap<String, SetupPanel>();

	// === Bottom Panel === //
	public SetupPanel					bottomPanel 	= null;
	public JButton       				cancelButton    = null;
	public JButton						backButton      = null;
	public JButton       				nextButton      = null;
	public JButton						helpButton 		= null;
	public JButton						configureButton = null;
	
	public static void main( String[] args )
	{
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			Thread.sleep(1000);

			Settings.init();

			Thread.sleep(1000);
			if( !Settings.getLock() )
			{
				JOptionPane.showMessageDialog(null, 
						"Another instance is already running.\n\nPlease stop that one before starting another.", 
						"Error", JOptionPane.ERROR_MESSAGE);
				Settings.shutdown(JFrame.ERROR);
			}
			
			Settings.CURRENT_PROGRAM_NAME = Settings.INSTALLER_NAME;
			
			TraceUtils.traceln(TraceUtils.STDOUT, "");
			TraceUtils.traceln(TraceUtils.STDOUT, "=== " + Settings.CURRENT_PROGRAM_NAME + " Starting Up ===");

			if( !Desktop.isDesktopSupported() || HeadlessGraphicsEnvironment.isHeadless() )
			{
				TraceUtils.traceln(TraceUtils.STDOUT, "");
				TraceUtils.traceln(TraceUtils.STDOUT, "!! Fault detected !!");
				TraceUtils.traceln(TraceUtils.STDOUT, "!! System does not support Java Desktop Features" );
				TraceUtils.traceln(TraceUtils.STDOUT, "");
				Settings.shutdown(ABORT);
				return;
			}
			
			if( !Settings.isOfflineMode() && !Settings.isConnectedToInternet() )
			{
				if( JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, 
						"It appears you have no connection to the internet.\n" +
						"Would you like to launch in offline mode?", 
						"No Internet", 
						JOptionPane.YES_NO_OPTION, 
						JOptionPane.WARNING_MESSAGE ))
				{
					Settings.LAUNCH_MODE = Settings.MODE.OFFLINE_MODE;
					Settings.save();
					LaunchUtils.launchWeaveUpdater();
				} 
				else
					Settings.shutdown(ABORT);
			}
			
			installer = new Installer();
			
		} catch (ClassNotFoundException e) {			TraceUtils.trace(TraceUtils.STDERR, e);	BugReportUtils.showBugReportDialog(e);
		} catch (InstantiationException e) {			TraceUtils.trace(TraceUtils.STDERR, e);	BugReportUtils.showBugReportDialog(e);
		} catch (IllegalAccessException e) {			TraceUtils.trace(TraceUtils.STDERR, e);	BugReportUtils.showBugReportDialog(e);			
		} catch (UnsupportedLookAndFeelException e) {	TraceUtils.trace(TraceUtils.STDERR, e);	BugReportUtils.showBugReportDialog(e);	
		} catch (IOException e) {						TraceUtils.trace(TraceUtils.STDERR, e);	BugReportUtils.showBugReportDialog(e);						
		} catch (Exception e) {							TraceUtils.trace(TraceUtils.STDERR, e);	BugReportUtils.showBugReportDialog(e);							
		}

		installer.addWindowListener(new WindowListener() {
			@Override public void windowClosing(WindowEvent e) {
//				System.out.println("Closing...");
				installer.setExtendedState(JFrame.ICONIFIED);
			}
			@Override public void windowDeactivated(WindowEvent e) {
				/* System.out.println("Deactivated..."); */
				System.gc();
			}
			@Override public void windowClosed(WindowEvent e) 		{/*	System.out.println("Closed...");		*/}
			@Override public void windowActivated(WindowEvent e) 	{/*	System.out.println("Activated...");		*/}
			@Override public void windowDeiconified(WindowEvent e) 	{/*	System.out.println("Deiconified...");	*/}
			@Override public void windowIconified(WindowEvent e) 	{/*	System.out.println("Iconified...");		*/}
			@Override public void windowOpened(WindowEvent e) 		{/*	System.out.println("Opened...");		*/}
		});
	}
	
	public Installer() throws Exception
	{
		TrayManager.initializeTray(this);
		PluginManager.instance().initializePlugins();

		// ======== STRUCTURING ========= //
		setSize(500, 400);
		setResizable(false);
		setLayout(null);
		setTitle(Settings.INSTALLER_TITLE);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLocation(screen.width/2 - getWidth()/2, screen.height/2 - getHeight()/2);
		setIconImage(TrayManager.trayIconOffline);
		
		

		// ======== CREATE LEFT PANEL ======== //
		leftPanel = new SetupPanel();
		leftPanel.setLayout(null);
		leftPanel.setBounds(0, 0, 150, 325);
		leftPanel.setBackground(new Color(0xEEEEEE));
		leftPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.black));

		BufferedImage oicLogo = ImageIO.read(WeaveInstaller.class.getResource("/resources/oic4.png"));
		JLabel oicLabel = new JLabel("", new ImageIcon(oicLogo), JLabel.CENTER);
		oicLabel.setBounds(10, 10, 125, 57);
		leftPanel.add(oicLabel);

		final JLabel iweaveLink = new JLabel(Settings.OICWEAVE_HOST);
		iweaveLink.setBounds(30, 300, 125, 20);
		iweaveLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
		iweaveLink.setFont(new Font(Settings.FONT, Font.PLAIN, 15));
		iweaveLink.addMouseListener(new MouseListener() {
			@Override public void mouseReleased(MouseEvent e) {}
			@Override public void mousePressed(MouseEvent e) {}
			@Override public void mouseExited(MouseEvent e) {}
			@Override public void mouseEntered(MouseEvent e) {}
			@Override public void mouseClicked(MouseEvent e) {
				if (Desktop.isDesktopSupported()) {
					try {
						Desktop.getDesktop().browse(new URI(Settings.OICWEAVE_URL));
					} catch (Exception e1) {
						TraceUtils.trace(TraceUtils.STDERR, e1);
					}
				}
			}
		});

		leftPanel.add(iweaveLink);
		leftPanel.setVisible(false);
		add(leftPanel);
		
		
		
		// ======== CREATE BOTTOM PANEL ======== //
		bottomPanel = new SetupPanel();
		bottomPanel.setLayout(null);
		bottomPanel.setBounds(0, 325, 500, 50);
		bottomPanel.setBackground(new Color(0x507AAA));
		bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.black));

		helpButton = new JButton("Help");
		helpButton.setBounds(10, 13, 80, 25);
		helpButton.setBackground(new Color(0x507AAA));
		helpButton.setToolTipText("Open wiki page for help");
		helpButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if( Desktop.isDesktopSupported() ) {
					try {
						Desktop.getDesktop().browse(new URI(Settings.WIKI_HELP_PAGE));
					} catch (Exception e) {
						TraceUtils.trace(TraceUtils.STDERR, e);
					}
				} else
					JOptionPane.showMessageDialog(null, "This feature is not supported by the \nversion of Java you are running.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
		helpButton.setVisible(true);
		
		configureButton = new JButton("Configure");
		configureButton.setBounds(100, 13, 100, 25);
		configureButton.setBackground(new Color(0x507AAA));
		configureButton.setToolTipText("Edit configuration settings");
		configureButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
//					switchToCurSetupPanel();
				} catch (Exception e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
				}
			}
		});
		configureButton.setVisible(false);
		
		backButton = new JButton("< Back") ;
		backButton.setBounds(200, 13, 80, 25);
		backButton.setBackground(new Color(0x507AAA));
		
		nextButton = new JButton("Next >") ;
		nextButton.setBounds(280, 13, 80, 25);
		nextButton.setBackground(new Color(0x507AAA));
		
		cancelButton = new JButton("Cancel");
		cancelButton.setBounds(400, 13, 80, 25);
		cancelButton.setBackground(new Color(0x507AAA));
		cancelButton.setToolTipText("Close the installer");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit?", "Confirm", JOptionPane.YES_NO_OPTION ) ;
				if( response == JOptionPane.YES_OPTION ){
					System.gc();
					try {
						Thread.sleep(200);
					} catch (Exception e) {
						TraceUtils.trace(TraceUtils.STDERR, e);
					}
					Settings.shutdown();
				}
			}
		});
		
		backButton.setEnabled(false);

		bottomPanel.add(helpButton);
		bottomPanel.add(configureButton);
		bottomPanel.add(backButton);
		bottomPanel.add(cancelButton);
		bottomPanel.add(nextButton);
		add(bottomPanel);
		
		
		
		// ======== CREATE RIGHT PANEL ======== //
		rightPanel = new SetupPanel();
		rightPanel.setLayout(null);
		rightPanel.setBounds(150, 0, 500 - 150, 325);
		rightPanel.setBackground(new Color(0xFFFFFF));
		rightPanel.setVisible( false ) ;
		add(rightPanel);

		rightPanel.setVisible( true );
		bottomPanel.setVisible( true );
		leftPanel.setVisible( true );

		setVisible(true);
		if( !Settings.isOfflineMode() )
			startTimers();
		else {
			setTitle(getTitle() + " [OFFLINE MODE]");
		}
		
		// Delay renaming in case updater is still open.
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				if( updateToNewUpdater() )
					TraceUtils.traceln(TraceUtils.STDOUT, "-> Updating WeaveUpdater..........DONE");
			}
		}, 2000);
		
		//switchToPreSetupPanel(rightPanel);
	}

	/**
	 * Creates the preSetupPanel ( if it has not already been created ) and adds it to the Hash Map.
	 * If it has already been created, the function simply calls the overloaded version with no arguments.
	 * Also adds listeners for all buttons associated with the pre-setup panel.
	 * 
	 * @param parent
	 * @throws Exception
	 */
	public void switchToPreSetupPanel(final JPanel parent) throws Exception
	{
		if( setupPanels.containsKey(PRE_SETUP) ) {
			switchToPreSetupPanel();
			return;
		}
		
		preSP = new PreSetupPanel();
		preSP.hidePanels();
		setupPanels.put(PRE_SETUP, preSP);
		parent.add(preSP);
		switchToPreSetupPanel();
	}
	
	/**
	 * Switch to the pre-setup panel after it has already been created.
	 * Used, for example, to switch from currentSetupPanel back to preSetupPanel
	 * 
	 * @throws Exception
	 */
	public void switchToPreSetupPanel() throws Exception
	{
		if( setupPanels.containsKey(PRE_SETUP) )
		{
			hideAllPanels();
			setupPanels.get(PRE_SETUP).setVisible(true);
			((ISetupPanel) setupPanels.get(PRE_SETUP)).showPanels();
			
			backButton.setEnabled(false);	backButton.setVisible(true);
			nextButton.setEnabled(true);	nextButton.setVisible(true);
			configureButton.setEnabled(true); configureButton.setVisible(false);

			removeButtonActions();
			preSP.addActionToButton(nextButton, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						if( Settings.settingsFileExists() )
//							switchToPostSetupPanel();
//						else
							switchToCurSetupPanel();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			});
			backButton.setEnabled(false);
			nextButton.setEnabled(true);
		} else
			switchToPreSetupPanel(rightPanel);
	}

	public void switchToCurSetupPanel() throws Exception
	{
		if( setupPanels.containsKey(CUR_SETUP) )
		{
			hideAllPanels();
			setupPanels.get(CUR_SETUP).setVisible(true);
			((ISetupPanel) setupPanels.get(CUR_SETUP)).showPanels();
			
			removeButtonActions();
			curSP.addActionToButton(backButton, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if( curSP.getCurrentPanelIndex() == 0 ) {
						try {
							switchToPreSetupPanel();
						} catch (Exception e) {
							e.printStackTrace();
						}
//						preSP.nextPanel();
//						nextButton.setEnabled(false);
//						backButton.setEnabled(true);
					} else if( curSP.getCurrentPanelIndex() > 0 ) {
						curSP.previousPanel();
						if( curSP.getCurrentPanelIndex() == (curSP.getNumberOfPanels() - 2) )
							if( !curSP.tomcatCheck.isSelected() && !curSP.mysqlCheck.isSelected() )
								backButton.doClick();
						
						nextButton.setEnabled(true);
						backButton.setEnabled(true);
						nextButton.setText("Next >");
						nextButton.setBounds(nextButton.getX(), nextButton.getY(), 80, nextButton.getHeight());
					}
				}
			});
			curSP.addActionToButton(nextButton, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if( curSP.getCurrentPanelIndex() == ( curSP.getNumberOfPanels() - 1) )
					{
						if (MySQL.instance().MYSQL_PORT == 0 || Tomcat.instance().TOMCAT_PORT == 0 || Tomcat.instance().TOMCAT_HOME.equals(""))
							JOptionPane.showMessageDialog(null,	"Error validating settings information.", "Error", JOptionPane.ERROR_MESSAGE);
						else if (Settings.save()) {
							JOptionPane.showMessageDialog(null, "Settings saved successfully", "Settings", JOptionPane.INFORMATION_MESSAGE);
							nextButton.setBounds(nextButton.getX(), nextButton.getY(), 80, nextButton.getHeight());
							try {
//								switchToPostSetupPanel( rightPanel );
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						} else
							JOptionPane.showMessageDialog(null, "Error trying to save settings.", "Error", JOptionPane.ERROR_MESSAGE);
					}
					else if( curSP.getCurrentPanelIndex() < ( curSP.getNumberOfPanels() ) ) 
					{
						curSP.nextPanel();
						if( curSP.getCurrentPanelIndex() == 1 )
							if( !curSP.tomcatCheck.isSelected() && !curSP.mysqlCheck.isSelected() )
								nextButton.doClick();
						
						backButton.setEnabled(true);
						backButton.setVisible(true);
						if( curSP.getCurrentPanelIndex() == ( curSP.getNumberOfPanels() - 1 ) ) {
							nextButton.setText("Save & Finish") ;
							nextButton.setEnabled(true);
							nextButton.setBounds(nextButton.getX(), nextButton.getY(), 100, nextButton.getHeight());
						}
					}
				}
			});
			
			backButton.setEnabled(true);	backButton.setVisible(true);
			nextButton.setEnabled(true);	nextButton.setVisible(true);
			backButton.setText("< Back");	nextButton.setText("Next >");
			configureButton.setEnabled(true); configureButton.setVisible(false);
		} else
			switchToCurSetupPanel(rightPanel);
	}
	
	public void switchToCurSetupPanel(JPanel parent) throws Exception
	{
		if( setupPanels.containsKey(CUR_SETUP) ) {
			switchToCurSetupPanel();
			return;
		}
		
		curSP = new CurSetupPanel();
		curSP.hidePanels();
		curSP.addActionToButton(curSP.tomcatDownloadButton, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				curSP.tomcatDownloadButton.setEnabled( false ) ;
				nextButton.setEnabled( false ) ;
				backButton.setEnabled( false ) ;
				cancelButton.setEnabled( false ) ;
				try {
					if( Tomcat.instance().TOMCAT_INSTALL_FILE.exists() ){
						int response = JOptionPane.showConfirmDialog(null, "Weave Installer has detected that an executable" +
								" installer already exists.\nWould you like to re-download and overwrite?",
								"Confirm", JOptionPane.YES_NO_OPTION ) ;
						if( response == JOptionPane.YES_OPTION ){
							try {
								curSP.installTomcat.setVisible( false ) ;
								curSP.installTomcat.setEnabled(false);
//								curSP.progTomcat.downloadMSI(curSP.tomcatPanel, curSP.tomcatDownloadButton, Settings.MSI_TYPE.TOMCAT_MSI ) ;
							} catch (Exception e) {
								e.printStackTrace();
							}
						}else{
							curSP.tomcatDownloadButton.setEnabled( true ) ;
							nextButton.setEnabled( true ) ;
							backButton.setEnabled( true ) ;
							cancelButton.setEnabled( true ) ;
						}
					}else{
						curSP.installTomcat.setVisible( false ) ;
						curSP.installTomcat.setEnabled(false);
//						curSP.progTomcat.downloadMSI(curSP.tomcatPanel, curSP.tomcatDownloadButton, Settings.MSI_TYPE.TOMCAT_MSI ) ;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		curSP.addActionToButton(curSP.mySQLDownloadButton, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				curSP.mySQLDownloadButton.setEnabled( false ) ;
				nextButton.setEnabled( false ) ;
				backButton.setEnabled( false ) ;
				cancelButton.setEnabled( false ) ;
				try {
					if( MySQL.instance().MYSQL_INSTALL_FILE.exists() ){
						int response = JOptionPane.showConfirmDialog(null, "Weave Installer has detected that an executable" +
								" installer already exists.\nWould you like to re-download and overwrite?",
								"Confirm", JOptionPane.YES_NO_OPTION ) ;
						if( response == JOptionPane.YES_OPTION ){
							try {
								curSP.installMySQL.setVisible( false ) ;
//								curSP.progMySQL.downloadMSI(curSP.mysqlPanel, curSP.mySQLDownloadButton, Settings.MSI_TYPE.MySQL_MSI ) ;
							} catch (Exception e) {
								e.printStackTrace();
							}
						}else{
							curSP.mySQLDownloadButton.setEnabled( true ) ;
							nextButton.setEnabled( true ) ;
							backButton.setEnabled( true ) ;
							cancelButton.setEnabled( true ) ;
						}
					}else{
						curSP.installMySQL.setVisible( false ) ;
//						curSP.progMySQL.downloadMSI(curSP.mysqlPanel, curSP.mySQLDownloadButton, Settings.MSI_TYPE.MySQL_MSI ) ;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		curSP.addActionToButton(curSP.installTomcat, new ActionListener(){
			@Override public void actionPerformed( ActionEvent arg0 ){
				curSP.progMySQL.runExecutable( Tomcat.instance().TOMCAT_INSTALL_FILE ) ;
			}
		}) ;
		curSP.addActionToButton(curSP.installMySQL, new ActionListener(){
			@Override public void actionPerformed( ActionEvent arg0 ){
				curSP.progMySQL.runExecutable( MySQL.instance().MYSQL_INSTALL_FILE ) ;
			}
		}) ;
		curSP.addActionToButton(curSP.dirButton, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				curSP.dirChooser.fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int retVal = curSP.dirChooser.fileChooser.showOpenDialog(null);
				if (retVal == JFileChooser.APPROVE_OPTION) {
					String dir = curSP.dirChooser.fileChooser.getSelectedFile().getPath();
					File f = new File(dir + "/webapps/ROOT/");
					File g = new File(dir + "/Uninstall.exe");
					if (f.exists() && g.exists()) {
						Tomcat.instance().TOMCAT_HOME = new File(dir);
					} else {
						Tomcat.instance().TOMCAT_HOME = null;
						JOptionPane.showMessageDialog(null, "Invalid Tomcat Directory", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
				curSP.dirChooser.textField.setText(Settings.ACTIVE_CONTAINER_PLUGIN.getHomeDirectory().getAbsolutePath());
			}
		});
		setupPanels.put(CUR_SETUP, curSP);
		parent.add(curSP);
		switchToCurSetupPanel();
	}
	
	/**
	 * Start update timers.
	 * 
	 * If the tool is open for extended periods of time (1+ days)
	 * we should periodically check for updates.
	 */
	public void startTimers()
	{
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				if( Settings.isConnectedToInternet() )
					checkForUpdate();
			}
		}, 1000);
		
		// check for updates once a day if they keep the tool open
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				if( Settings.isConnectedToInternet() )
					checkForUpdate();
			}
		}, 86400000, 86400000);
	}
	
	/**
	 * Check for update
	 */
	public void checkForUpdate()
	{
		boolean isUpdate = UpdateUtils.isUpdateAvailable();
		
		if( isUpdate )
		{
			int n = JOptionPane.showConfirmDialog(null, "There is a newer version of this tool available for download.\n\n" +
														"Would you like to restart the tool to apply the update?", "Update Available", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
			
			if( n == JOptionPane.YES_OPTION )
			{
				try {
					LaunchUtils.launchWeaveUpdater(1000);
					Thread.sleep(50);
					Settings.shutdown();
				} catch (IOException e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				} catch (InterruptedException e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				}
			}
		}
	}
	
	private boolean updateToNewUpdater()
	{
		File oldUpdater = new File(Settings.BIN_DIRECTORY, Settings.WEAVEUPDATER_JAR);
		File newUpdater = new File(Settings.BIN_DIRECTORY, Settings.WEAVEUDPATER_NEW_JAR);
		
		return ( newUpdater.exists() ? FileUtils.renameTo(newUpdater, oldUpdater, FileUtils.OVERWRITE) : false );
	}

	public void hideAllPanels()
	{
		for( String key : setupPanels.keySet() ) {
			((ISetupPanel) setupPanels.get(key)).hidePanels();
			setupPanels.get(key).setVisible(false);
		}
	}
	
	public void removeButtonActions()
	{
		for( ActionListener a : backButton.getActionListeners() )
			backButton.removeActionListener(a);
		for( ActionListener a : nextButton.getActionListeners() )
			nextButton.removeActionListener(a);
	}
}
