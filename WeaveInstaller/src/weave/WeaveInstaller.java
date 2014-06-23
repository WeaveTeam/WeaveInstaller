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
import java.net.URI;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import weave.inc.ISetupPanel;
import weave.managers.ConfigManager;
import weave.plugins.MySQL;
import weave.plugins.Tomcat;
import weave.ui.ConfigSetupPanel;
import weave.ui.HomeSetupPanel;
import weave.ui.WelcomeSetupPanel;
import weave.utils.FileUtils;
import weave.utils.IdentityUtils;

@SuppressWarnings("serial")
public class WeaveInstaller extends JFrame 
{
	public static WeaveInstaller 	installer 		= null;
	public static final String 		PRE_SETUP		= "PRE_SETUP";
	public static final String		CUR_SETUP		= "CUR_SETUP";
	public static final String		POST_SETUP		= "POST_SETUP";
	Dimension 						screen 			= Toolkit.getDefaultToolkit().getScreenSize();
	
	// === Left Panel === //
	public JPanel					leftPanel		= null;
	
	
	// === Right Panel === //
	public JPanel 					rightPanel		= null;
	public WelcomeSetupPanel 			preSP 			= null;
	public ConfigSetupPanel 			curSP			= null;
	public HomeSetupPanel 			postSP 			= null;
	public HashMap<String, JPanel>	setupPanels		= new HashMap<String, JPanel>();

	
	// === Bottom Panel === //
	public JPanel					bottomPanel 	= null;
	public JButton       			cancelButton    = null;
	public JButton					backButton      = null;
	public JButton       			nextButton      = null;
	public JButton					helpButton 		= null;
	public JButton					configureButton = null;
	
	
	public static void main(String[] args) 
	{
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			installer = new WeaveInstaller();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(ERROR);
		}

		installer.addWindowListener(new WindowListener() {
			@Override public void windowClosing(WindowEvent e) {
				System.out.println("Closing...");
				if (Settings.UNZIP_DIRECTORY.exists()) {
					System.out.println("Deleted");
					FileUtils.recursiveDelete(Settings.UNZIP_DIRECTORY);
					try {Thread.sleep(1000);} catch (Exception e1) {e1.printStackTrace();}
				}
				System.exit(NORMAL);
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

	public WeaveInstaller() throws Exception 
	{	
		ConfigManager.getConfigManager().initializeConfigs();
		
		// ======== STRUCTURING ========= //
		setSize(500, 400);
		setResizable(false);
		setLayout(null);
		setTitle(Settings.INSTALLER_TITLE);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLocation(screen.width/2 - getWidth()/2, screen.height/2 - getHeight()/2);
		setIconImage(ImageIO.read(WeaveInstaller.class.getResource("/resources/update.png")));
		
		
		
		// ======== CREATE LEFT PANEL ======== //
		leftPanel = new JPanel();
		leftPanel.setLayout(null);
		leftPanel.setBounds(0, 0, 150, 325);
		leftPanel.setBackground(new Color(0xEEEEEE));
		leftPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.black));

		BufferedImage oicLogo = ImageIO.read(WeaveInstaller.class.getResource("/resources/oic4.png"));
		JLabel oicLabel = new JLabel("", new ImageIcon(oicLogo), JLabel.CENTER);
		oicLabel.setBounds(10, 10, 125, 57);
		leftPanel.add(oicLabel);

		final JLabel iweaveLink = new JLabel("oicweave.org");
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
						Desktop.getDesktop().browse(new URI("http://oicweave.org"));
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		leftPanel.add(iweaveLink);
		leftPanel.setVisible(false);
		add(leftPanel);
		
		
		
		// ======== CREATE BOTTOM PANEL ======== //
		bottomPanel = new JPanel();
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
						e.printStackTrace();
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
					switchToCurSetupPanel();
				} catch (Exception e) {
					e.printStackTrace();
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
						e.printStackTrace();
					}
					System.exit(NORMAL);
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
		rightPanel = new JPanel();
		rightPanel.setLayout(null);
		rightPanel.setBounds(150, 0, 500 - 150, 325);
		rightPanel.setBackground(new Color(0xFFFFFF));
		rightPanel.setVisible( false ) ;
		add(rightPanel);

		rightPanel.setVisible( true ) ;
		bottomPanel.setVisible( true ) ;
		leftPanel.setVisible( true ) ;
		setVisible(true);

		// This is required for further use of Settings
		Settings.init();
		
		// Need to check if this WeaveInstaller tool 
		// has a unique ID assigned to it
		if( !Settings.hasUniqueID() ) {
			Settings.UNIQUE_ID = IdentityUtils.createID();
			Settings.save();
		}
		
		moveNewerUpdater();

		switchToPreSetupPanel(rightPanel);
		
		if( !Settings.isConnectedToInternet() ) 
		{
			nextButton.setEnabled(false);
			JOptionPane.showMessageDialog(null, "Internet connection could not be established.\n\n" +
												"Please make sure you are connected to the\n" +
												"internet before you continue.", "Warning", JOptionPane.WARNING_MESSAGE);
		}
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
			((ISetupPanel) setupPanels.get(PRE_SETUP)).showPanel();
			
			backButton.setEnabled(false);	backButton.setVisible(true);
			nextButton.setEnabled(true);	nextButton.setVisible(true);
			configureButton.setEnabled(true); configureButton.setVisible(false);

			removeButtonActions();
			preSP.addActionToButton(nextButton, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						if( Settings.settingsFileExists() )
							switchToPostSetupPanel();
						else
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
		
		preSP = new WelcomeSetupPanel();
		preSP.hidePanels();
		setupPanels.put(PRE_SETUP, preSP);
		parent.add(preSP);
		switchToPreSetupPanel();
	}
	
	public void switchToCurSetupPanel() throws Exception
	{
		if( setupPanels.containsKey(CUR_SETUP) )
		{
			hideAllPanels();
			setupPanels.get(CUR_SETUP).setVisible(true);
			((ISetupPanel) setupPanels.get(CUR_SETUP)).showPanel();
			
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
						if (MySQL.getConfig().MYSQL_PORT == 0 || Tomcat.getConfig().TOMCAT_PORT == 0 || Tomcat.getConfig().TOMCAT_HOME.equals(""))
							JOptionPane.showMessageDialog(null,	"Error validating settings information.", "Error", JOptionPane.ERROR_MESSAGE);
						else if (Settings.save()) {
							JOptionPane.showMessageDialog(null, "Settings saved successfully", "Settings", JOptionPane.INFORMATION_MESSAGE);
							nextButton.setBounds(nextButton.getX(), nextButton.getY(), 80, nextButton.getHeight());
							try {
								switchToPostSetupPanel( rightPanel );
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
		
		curSP = new ConfigSetupPanel();
		curSP.hidePanels();
		curSP.addActionToButton(curSP.tomcatDownloadButton, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				curSP.tomcatDownloadButton.setEnabled( false ) ;
				nextButton.setEnabled( false ) ;
				backButton.setEnabled( false ) ;
				cancelButton.setEnabled( false ) ;
				try {
					if( Tomcat.getConfig().TOMCAT_INSTALL_FILE.exists() ){
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
					if( MySQL.getConfig().MYSQL_INSTALL_FILE.exists() ){
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
				curSP.progMySQL.runExecutable( Tomcat.getConfig().TOMCAT_INSTALL_FILE ) ;
			}
		}) ;
		curSP.addActionToButton(curSP.installMySQL, new ActionListener(){
			@Override public void actionPerformed( ActionEvent arg0 ){
				curSP.progMySQL.runExecutable( MySQL.getConfig().MYSQL_INSTALL_FILE ) ;
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
						Tomcat.getConfig().TOMCAT_HOME = new File(dir);
					} else {
						Tomcat.getConfig().TOMCAT_HOME = null;
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
	
	public void switchToPostSetupPanel() throws Exception
	{
		if( setupPanels.containsKey(POST_SETUP) )
		{
			hideAllPanels();
			setupPanels.get(POST_SETUP).setVisible(true);
			((ISetupPanel) setupPanels.get(POST_SETUP)).showPanel();
			
			backButton.setEnabled(false);	backButton.setVisible(false);
			nextButton.setEnabled(false);	nextButton.setVisible(false);
			configureButton.setEnabled(true); configureButton.setVisible(true);
			
			removeButtonActions();
		} else
			switchToPostSetupPanel(rightPanel);
	}
	
	public void switchToPostSetupPanel(JPanel parent) throws Exception
	{
		if( setupPanels.containsKey(POST_SETUP) ) {
			switchToPostSetupPanel();
			return;
		}
		
		postSP = new HomeSetupPanel();
		postSP.hidePanels();
		postSP.addActionToButton(postSP.installButton, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!Settings.isConnectedToInternet()) {
					postSP.progress.progBar.setStringPainted(true);
					postSP.progress.progBar.setString("No Internet Connection");
					postSP.progress.progBar.setValue(0);
					return;
				}
				if (Settings.isServiceUp("sdf", Tomcat.getConfig().TOMCAT_PORT)
				&& !Tomcat.getConfig().TOMCAT_HOME.equals("")) {
					postSP.installButton.setEnabled(false);
					postSP.revertButton.setEnabled(false);
					postSP.deleteButton.setEnabled(false);
					postSP.checkButton.setEnabled(false);
					postSP.progress.downloadZip(postSP.checkButton);
				}
				/* Else notify the user that they don't have TOMCAT */
				else {
					JOptionPane.showMessageDialog(
							null,
							"Tomcat must be properly configured and running to install Weave.",
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		postSP.addActionToButton(postSP.checkButton, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				/*
				 * Create a new thread that is runnable and can run concurrently
				 * with the main program
				 */
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						/*
						 * Enable the revert and delete actions. When enabled,
						 * any component associated with an action is able to
						 * fire this object's actionPerformed method. If the
						 * value is changed, a PropertyChangeEvent is sent to
						 * listeners.
						 */
						postSP.revertButton.setEnabled(true);
						postSP.deleteButton.setEnabled(true);
						/*
						 * Obtain the return value from
						 * Revisions.checkForUpdates( bool save ) with the save
						 * flag enabled.
						 */
						final int ret = Revisions.checkForUpdates(true);
						postSP.weaveStats.refresh(ret);

						/*
						 * If ret == 1, then there is a new update available.
						 * Check if TOMCAT is up and if the TOMCAT directory is
						 * not equal to "" If these conditions are met, enable
						 * the install button; else disable it.
						 */
						if (ret == 1 && !Tomcat.getConfig().TOMCAT_HOME.equals(""))
						{
							postSP.installButton.setEnabled(true);
							postSP.adminButton.setForeground(Color.BLACK);
						}
						else
						{
							postSP.installButton.setEnabled(false);
						}

						postSP.revisionTable.updateTableData();

						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								try {
									Thread.sleep(3000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								postSP.progress.progBar.setStringPainted(true);
								postSP.progress.progBar.setValue(0);
								if (ret == -2)
									postSP.progress.progBar.setString("No Internet Connection");
								else
									postSP.progress.progBar.setString("");
							}
						});
					}
				});
				t.start();
			}
		});
		postSP.addActionToButton(postSP.revertButton, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				/* Get the index of the selected row in the revision table */
				int n = postSP.revisionTable.table.getSelectedRow();
				if (n < 0) return;

				/*
				 * Get the nth element in the revision table list, which
				 * corresponds to a File. ArrayList<File> getRevisionData()
				 */
				File f = Revisions.getRevisionData().get(n);
				if (Settings.CURRENT_INSTALL_VER.equals(Revisions.getRevisionName(f.getPath()))) {
					JOptionPane.showMessageDialog(null,	"Cannot revert to current installation.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (!Tomcat.getConfig().TOMCAT_HOME.equals("")) {
					postSP.installButton.setEnabled(false);
					postSP.deleteButton.setEnabled(false);
					postSP.revertButton.setEnabled(false);
					Revisions.extractZip(f.getPath(), postSP.progress.progBar,	postSP.checkButton);
				} else {
					JOptionPane.showMessageDialog(null, "Tomcat must be properly configured and running to revert.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		postSP.addActionToButton(postSP.deleteButton, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				int n = postSP.revisionTable.table.getSelectedRow();
				if (n < 0) return;
				
				int val = JOptionPane.showConfirmDialog(null, "Deleting revisions cannot be undone.\n\nAre you sure you want to continue?", "Warning", JOptionPane.YES_NO_OPTION);
				if (val == JOptionPane.NO_OPTION)
					return;

				File f = Revisions.getRevisionData().get(n);
				if (Settings.CURRENT_INSTALL_VER.equals(Revisions.getRevisionName(f.getPath()))) {
					JOptionPane.showMessageDialog(null, "Cannot delete current installation.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				FileUtils.recursiveDelete(f);
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						postSP.checkButton.doClick();
					}
				});
			}
		});
		postSP.addActionToButton(postSP.pruneButton, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				double sizeMB = (Revisions.getSizeOfRevisions()/1024/1024);
				int numRevs = Revisions.getNumberOfRevisions();
				
				if( numRevs >= Settings.recommendPrune )
				{
					int val = JOptionPane.showConfirmDialog(null, "Auto-cleaned revisions will be deleted\nand cannot be undone.\n\nAre you sure you want to continue?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
					if( val == JOptionPane.NO_OPTION )
						return;
					
					if( Revisions.pruneRevisions() )
					{
						double newSize = (Revisions.getSizeOfRevisions()/1024/1024);
						int newNumRevs = Revisions.getNumberOfRevisions();

						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								postSP.checkButton.doClick();
							}
						});
						
						JOptionPane.showMessageDialog(null, "Auto-clean completed successfully!\n\n" +
															"Deleted: " + (numRevs - newNumRevs) + " files\n" +
															"Freed Up: " + (sizeMB - newSize ) + "MB", "Finished", JOptionPane.INFORMATION_MESSAGE);
					}
					else
					{
						JOptionPane.showMessageDialog(null, "Sorry, the auto-clean feature encoutered\n" +
															"an error and did not complete successfully.", "Error", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(null, "You need at least " + Settings.recommendPrune + " revisions for\n" +
														"the auto-clean feature to work.\n\n" +
														"Please delete revisions manually.", "Warning", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		postSP.addActionToButton(postSP.adminButton, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!Settings.isServiceUp("sdf", Settings.ACTIVE_CONTAINER_PLUGIN.getPort()))
				{
					int n = JOptionPane.showConfirmDialog(null, "The active webapps service is not running.\n\nWould you like to launch AdminConsole anyway?\n", "Error", JOptionPane.YES_NO_OPTION);
					if( n == JOptionPane.NO_OPTION ) 
						return;
				}
				if( Desktop.isDesktopSupported() )
				{
					try {
						Desktop.getDesktop().browse(new URI("http://localhost:"	+ Settings.ACTIVE_CONTAINER_PLUGIN.getPort() + "/AdminConsole.html"));
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				} else
					JOptionPane.showMessageDialog(null, "Feature not supported.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
		setupPanels.put(POST_SETUP, postSP);
		parent.add(postSP);
		switchToPostSetupPanel();
	}
	
	public boolean moveNewerUpdater()
	{
		File source = new File(Settings.BIN_DIRECTORY, Settings.UDPATER_NEW_JAR);
		File destination = new File(Settings.BIN_DIRECTORY, Settings.UPDATER_JAR);
		
		if( source.exists() )
			return FileUtils.renameTo(source, destination, FileUtils.OVERWRITE);

		return false;
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
