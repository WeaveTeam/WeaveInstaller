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

package weave;

import static weave.utils.TraceUtils.STDERR;
import static weave.utils.TraceUtils.STDOUT;
import static weave.utils.TraceUtils.trace;
import static weave.utils.TraceUtils.traceln;

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
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import weave.configs.IConfig;
import weave.inc.SetupPanel;
import weave.managers.ConfigManager;
import weave.managers.IconManager;
import weave.managers.TrayManager;
import weave.reflect.Reflectable;
import weave.ui.ConfigSetupPanel;
import weave.ui.CustomCheckbox;
import weave.ui.HomeSetupPanel;
import weave.ui.WelcomeSetupPanel;
import weave.utils.BugReportUtils;
import weave.utils.FileUtils;
import weave.utils.IdentityUtils;
import weave.utils.ImageUtils;
import weave.utils.LaunchUtils;
import weave.utils.RemoteUtils;
import weave.utils.StatsUtils;
import weave.utils.StringUtils;
import weave.utils.TransferUtils;
import weave.utils.UpdateUtils;

import com.jtattoo.plaf.fast.FastLookAndFeel;

@SuppressWarnings("serial")
public class Installer extends JFrame
{
	public static Installer 			installer 		= null;
	public static final String 			PRE_SETUP		= "PRE_SETUP";
	public static final String			CFG_SETUP		= "CFG_SETUP";
	public static final String			HOME_SETUP		= "HOME_SETUP";
	private Dimension 					screen 			= Toolkit.getDefaultToolkit().getScreenSize();
	
	// === Left Panel === //
	public SetupPanel					leftPanel		= null;
	public CustomCheckbox				servletCheckbox = null;
	public CustomCheckbox				databaseCheckbox = null;
	public CustomCheckbox				installCheckbox = null;
	public CustomCheckbox				configCheckbox	= null;
	
	// === Right Panel === //
	public SetupPanel 					rightPanel		= null;
	public WelcomeSetupPanel 			SP_welcome 		= null;
	public ConfigSetupPanel 			SP_config		= null;
	public HomeSetupPanel 				SP_home 		= null;
	public Map<String, SetupPanel>		setupPanels		= new HashMap<String, SetupPanel>();

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
			Properties props = new Properties();
			props.put("logoString", "");
			FastLookAndFeel.setCurrentTheme(props);
			UIManager.setLookAndFeel(FastLookAndFeel.class.getCanonicalName());
			Thread.sleep(1000);

			Settings.CURRENT_PROGRAM_NAME = Settings.SERVER_NAME;
			Settings.init();

			Thread.sleep(1000);
			if( !Settings.getLock() )
			{
				JOptionPane.showMessageDialog(null, 
						Settings.CURRENT_PROGRAM_NAME + " is already running.\n\n" +
						"Please stop that one before starting another.", 
						"Error", JOptionPane.ERROR_MESSAGE);
				Settings.shutdown(JFrame.ERROR);
			}
			
			
			traceln(STDOUT, "");
			traceln(STDOUT, "=== " + Settings.CURRENT_PROGRAM_NAME + " Starting Up ===");

			if( !Desktop.isDesktopSupported() )
			{
				traceln(STDOUT, "");
				traceln(STDOUT, "!! Fault detected !!");
				traceln(STDOUT, "!! System does not support Java Desktop Features" );
				traceln(STDOUT, "");
				Settings.shutdown(ABORT);
				return;
			}
			
			while( true ) 
			{
				if( !Settings.isOfflineMode() && !RemoteUtils.isConnectedToInternet() )
				{
					int ops = JOptionPane.showOptionDialog(null, 
							"No internet connection could be established at this time.\n" + 
							"Would you like to launch in offline mode?", "No Internet", 
							JOptionPane.YES_NO_CANCEL_OPTION, 
							JOptionPane.WARNING_MESSAGE, 
							null, new String[] { "Retry", "Yes", "Cancel" },
							null);
					
					if( ops == JOptionPane.YES_OPTION ) 
					{
						continue;
					} 
					else if( ops == JOptionPane.CANCEL_OPTION || ops == JOptionPane.CLOSED_OPTION )
					{
						Settings.shutdown(ABORT);
					}
					else
					{
						Settings.LAUNCH_MODE = Settings.LAUNCH_ENUM.OFFLINE_MODE;
						Settings.save();
						LaunchUtils.launchWeaveUpdater();
					}
				} else
					break;
			}
			
			installer = new Installer();

		} catch (ClassNotFoundException e) {			trace(STDERR, e);	BugReportUtils.showBugReportDialog(e);
		} catch (InstantiationException e) {			trace(STDERR, e);	BugReportUtils.showBugReportDialog(e);
		} catch (IllegalAccessException e) {			trace(STDERR, e);	BugReportUtils.showBugReportDialog(e);			
		} catch (UnsupportedLookAndFeelException e) {	trace(STDERR, e);	BugReportUtils.showBugReportDialog(e);	
		} catch (IOException e) {						trace(STDERR, e);	BugReportUtils.showBugReportDialog(e);						
		} catch (Exception e) {							trace(STDERR, e);	BugReportUtils.showBugReportDialog(e);							
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
		
		Globals.globalHashMap.put("Installer", installer);
	}
	
	public Installer() throws Exception
	{
		// ======== INITIALIZATION ======== //
		try {
			if( Settings.OS == Settings.OS_ENUM.WINDOWS )
				Settings.loadLibrary("DLLInterface" + System.getProperty("sun.arch.data.model") + ".dll");
		} catch (UnsatisfiedLinkError e) {
			// If we can't find the dll then don't error
		} catch (Exception e) {
			trace(STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		}

		Settings.canQuit = false;
		if( !Settings.hasUniqueID() ) {
			Settings.UNIQUE_ID = IdentityUtils.createID();
			traceln(STDOUT, "-> Generated new UniqueID: " + Settings.UNIQUE_ID);
			Settings.save();
		}
		Settings.canQuit = true;
		
		TrayManager.initializeTray(this);
		ConfigManager.getConfigManager().initializeConfigs();
		

		// ======== STRUCTURING ========= //
		setSize(600, 450);
		setResizable(false);
		setLayout(null);
		setTitle(Settings.SERVER_TITLE);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLocation(screen.width/2 - getWidth()/2, screen.height/2 - getHeight()/2);
		setIconImage(ImageIO.read(IconManager.IMAGE_NULL));
		

		// ======== CREATE LEFT PANEL ======== //
		leftPanel = new SetupPanel();
		leftPanel.setLayout(null);
		leftPanel.setBounds(0, 0, SetupPanel.LEFT_PANEL_WIDTH, SetupPanel.LEFT_PANEL_HEIGHT);
		leftPanel.setBackground(new Color(0xEEEEEE));
		leftPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.black));

		BufferedImage wvaLogo = ImageUtils.fit(ImageIO.read(IconManager.IMAGE_W_LOGO), 125, 65);
		JLabel wvaLabel = new JLabel("", new ImageIcon(wvaLogo), JLabel.CENTER);
		wvaLabel.setBounds(10, 10, 125, 65);
		leftPanel.add(wvaLabel);
		
		servletCheckbox = new CustomCheckbox("Select Servlet");
		servletCheckbox.setBounds(10, 120, 125, 25);
		servletCheckbox.setBackground(leftPanel.getBackground());
		servletCheckbox.setSelected(false);
		servletCheckbox.setVisible(!Settings.SETUP_COMPLETE);
		servletCheckbox.setToolTipText("Select the application server");
		leftPanel.add(servletCheckbox);
		
		databaseCheckbox = new CustomCheckbox("Select Database");
		databaseCheckbox.setBounds(10, 150, 125, 25);
		databaseCheckbox.setBackground(new Color(0xEE, 0xEE, 0xEE, 125));
		databaseCheckbox.setOpaque(false);
		databaseCheckbox.setSelected(false);
		databaseCheckbox.setVisible(!Settings.SETUP_COMPLETE);
		databaseCheckbox.setToolTipText("Select the database server.");
		leftPanel.add(databaseCheckbox);

		installCheckbox = new CustomCheckbox("Install Weave");
		installCheckbox.setBounds(10, 180, 125, 25);
		installCheckbox.setBackground(leftPanel.getBackground());
		installCheckbox.setSelected(false);
		installCheckbox.setVisible(!Settings.SETUP_COMPLETE);
		installCheckbox.setToolTipText("Click Install on the Weave tab.");
		leftPanel.add(installCheckbox);
		
		configCheckbox = new CustomCheckbox("Configure");
		configCheckbox.setBounds(10, 210, 125, 25);
		configCheckbox.setBackground(leftPanel.getBackground());
		configCheckbox.setSelected(false);
		configCheckbox.setVisible(!Settings.SETUP_COMPLETE);
		configCheckbox.setToolTipText("<html>Open the Admin Console on the Sessions tab.<br>Configure Weave for first time use.</html>");
		leftPanel.add(configCheckbox);
		
		final JLabel iweaveLink = new JLabel(Settings.IWEAVE_HOST);
		iweaveLink.setBounds(30, SetupPanel.LEFT_PANEL_HEIGHT - 30, 125, 20);
		iweaveLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
		iweaveLink.setFont(new Font(Settings.FONT, Font.PLAIN, 15));
		iweaveLink.addMouseListener(new MouseListener() {
			@Override public void mouseReleased(MouseEvent e) {}
			@Override public void mousePressed(MouseEvent e) {}
			@Override public void mouseExited(MouseEvent e) {}
			@Override public void mouseEntered(MouseEvent e) {}
			@Override public void mouseClicked(MouseEvent e) {
				try {
					LaunchUtils.browse(Settings.IWEAVE_URL);
				} catch (IOException ex) {
					trace(STDERR, ex);
					BugReportUtils.showBugReportDialog(ex);
				} catch (URISyntaxException ex) {
					trace(STDERR, ex);
					BugReportUtils.showBugReportDialog(ex);
				} catch (InterruptedException ex) {
					trace(STDERR, ex);
					BugReportUtils.showBugReportDialog(ex);
				}
			}
		});

		leftPanel.add(iweaveLink);
		leftPanel.setVisible(false);
		add(leftPanel);
		
		
		
		// ======== CREATE BOTTOM PANEL ======== //
		bottomPanel = new SetupPanel();
		bottomPanel.setLayout(null);
		bottomPanel.setBounds(0, SetupPanel.LEFT_PANEL_HEIGHT, SetupPanel.BOTTOM_PANEL_WIDTH, SetupPanel.BOTTOM_PANEL_HEIGHT);
		bottomPanel.setBackground(new Color(0x507AAA));
		bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.black));
		//////////////////////////////////////////////////////////////////////////////////
		configureButton = new JButton("Configure");
		configureButton.setBounds(150, 10, 120, 30);
		configureButton.setOpaque(true);
		configureButton.setBackground(new Color(0x507AAA));
		configureButton.setToolTipText("Edit configuration settings");
		configureButton.setVisible(false);
		//////////////////////////////////////////////////////////////////////////////////
		backButton = new JButton("< Back");
		backButton.setBounds(260, 10, 100, 30);
		backButton.setOpaque(true);
		backButton.setBackground(new Color(0x507AAA));
		//////////////////////////////////////////////////////////////////////////////////
		nextButton = new JButton("Next >") ;
		nextButton.setBounds(360, 10, 100, 30);
		nextButton.setOpaque(true);
		nextButton.setBackground(new Color(0x507AAA));
		//////////////////////////////////////////////////////////////////////////////////
		helpButton = new JButton("Help");
		helpButton.setBounds(10, 10, 100, 30);				// 10, 13, 80, 25
		helpButton.setOpaque(true);
		helpButton.setBackground(new Color(0x507AAA));
		helpButton.setToolTipText("Open wiki page for help");
		helpButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					LaunchUtils.browse(Settings.WIKI_HELP_PAGE);
				} catch (IOException ex) {
					trace(STDERR, ex);
					BugReportUtils.showBugReportDialog(ex);
				} catch (InterruptedException ex) {
					trace(STDERR, ex);
					BugReportUtils.showBugReportDialog(ex);
				} catch (URISyntaxException ex) {
					trace(STDERR, ex);
					BugReportUtils.showBugReportDialog(ex);
				}
			}
		});
		helpButton.setVisible(true);
		//////////////////////////////////////////////////////////////////////////////////
		cancelButton = new JButton("Close");
		cancelButton.setBounds(480, 10, 100, 30);				// 400, 13, 80, 25
		cancelButton.setBackground(new Color(0x507AAA));
		cancelButton.setToolTipText("Close the installer");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit?", "Quit", JOptionPane.YES_NO_OPTION ) ;
				if( response == JOptionPane.YES_OPTION ){
					System.gc();
					Settings.shutdown();
				}
			}
		});
		//////////////////////////////////////////////////////////////////////////////////
		bottomPanel.add(helpButton);
		bottomPanel.add(configureButton);
		bottomPanel.add(backButton);
		bottomPanel.add(cancelButton);
		bottomPanel.add(nextButton);
		add(bottomPanel);
		
		
		
		// ======== CREATE RIGHT PANEL ======== //
		rightPanel = new SetupPanel();
		rightPanel.setLayout(null);
		rightPanel.setBounds(SetupPanel.LEFT_PANEL_WIDTH, 0, SetupPanel.RIGHT_PANEL_WIDTH, SetupPanel.RIGHT_PANEL_HEIGHT);
		rightPanel.setBackground(new Color(0xFFFFFF));
		rightPanel.setVisible(false);
		add(rightPanel);

		
		
		// ======== FINISHING TOUCHES ======== //
		rightPanel.requestFocus();
		rightPanel.setVisible( true );
		bottomPanel.setVisible( true );
		leftPanel.setVisible( true );

		setVisible(true);
		if( !Settings.isOfflineMode() )
			startTimers();
		else
			setTitle(getTitle() + " [OFFLINE MODE]");
		
		// Listen for network socket requests
		Settings.startListenerServer();
		
		// Delay renaming in case updater is still open.
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					if( updateToNewUpdater() == TransferUtils.COMPLETE ) {
						traceln(STDOUT, StringUtils.rpad("-> Updating WeaveUpdater", ".", Settings.LOG_PADDING_LENGTH));
						Settings.setDirectoryPermissions();
					}
				} catch (IOException e) {
					trace(STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				} catch (InterruptedException e) {
					trace(STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				}
			}
		}, 3000);
		
		switchToWelcomeSetupPanels(rightPanel);
	}

	
	
	
	
	
	

	//==========================================================================================================//
	//											WELCOME PANEL													//
	//==========================================================================================================//
	public void switchToWelcomeSetupPanels(final JPanel parent)
	{
		if( setupPanels.containsKey(PRE_SETUP) ) {
			switchToWelcomeSetupPanels();
			return;
		}
		
		SP_welcome = new WelcomeSetupPanel();
		SP_welcome.hidePanels();
		setupPanels.put(PRE_SETUP, SP_welcome);
		parent.add(SP_welcome);
		switchToWelcomeSetupPanels();
	}
	@Reflectable
	public void switchToWelcomeSetupPanels()
	{
		if( setupPanels.containsKey(PRE_SETUP) )
		{
			hideAllPanels();
			SP_welcome.setVisible(true);
			SP_welcome.showFirstPanel();

			backButton.setEnabled(false);	backButton.setVisible(true);
			nextButton.setEnabled(true);	nextButton.setVisible(true);
			backButton.setText("< Back");	nextButton.setText("Next >");
			configureButton.setEnabled(false);
			configureButton.setVisible(false);

			removeButtonActions();
			nextButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if( SP_welcome.isLastPanel() )
					{
						if( !Settings.CONFIGURED )
							switchToConfigSetupPanel();
						else
							switchToHomeSetupPanel();
					}
					else
					{
						SP_welcome.nextPanel();
					}
				}
			});
			backButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// Will be disabled
				}
			});
			
			IConfig servlet = ConfigManager.getConfigManager().getActiveContainer(),
					database = ConfigManager.getConfigManager().getActiveDatabase();
			
			if( Settings.SETUP_COMPLETE )
				setProgress(15);
			else if( FileUtils.getNumberOfFilesInDirectory(Settings.REVISIONS_DIRECTORY) > 0 )
				setProgress(7);
			else if( servlet != null && database != null )
				setProgress(3);
			else if( servlet != null )
				setProgress(1);
			else if( database != null )
				setProgress(2);
			
		} else
			switchToWelcomeSetupPanels(rightPanel);
	}
	//============================================================================================================
	
	
	
	
	
	
	
	
	//==========================================================================================================//
	//												CONFIG PANEL												//
	//==========================================================================================================//
	public void switchToConfigSetupPanel(JPanel parent)
	{
		if( setupPanels.containsKey(CFG_SETUP) ) {
			switchToConfigSetupPanel();
			return;
		}
		
		SP_config = new ConfigSetupPanel();
		SP_config.hidePanels();
		setupPanels.put(CFG_SETUP, SP_config);
		parent.add(SP_config);
		switchToConfigSetupPanel();
	}
	@Reflectable
	public void switchToConfigSetupPanel()
	{
		if( setupPanels.containsKey(CFG_SETUP) )
		{
			hideAllPanels();
			SP_config.setVisible(true);
			SP_config.showFirstPanel();

			backButton.setEnabled(true);	backButton.setVisible(true);
			nextButton.setEnabled(true);	nextButton.setVisible(true);
			backButton.setText("< Back");	nextButton.setText("Next >");
			configureButton.setEnabled(false);
			configureButton.setVisible(false);
			
			removeButtonActions();
			nextButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if( SP_config.isLastPanel() )
					{
						SP_config.savePanelInput();
						if( !Settings.CONFIGURED ) {
							Settings.CONFIGURED = true;
							Settings.save();
						}
						switchToHomeSetupPanel();
					}
					else
					{
						if( !SP_config.validatePanelInput(SP_config.getCurrentPanelIndex()) )
							return;
						if( !SP_config.savePanelInput(SP_config.getCurrentPanelIndex()) )
							return;
						
						switch (SP_config.getCurrentPanelIndex()) {
							case 0:	setProgress(getProgress() | 1);	break;
							case 1:	setProgress(getProgress() | 2);	break;
							default:				break;
						}
						
						SP_config.nextPanel();
						
						if( SP_config.isLastPanel() ) {
							nextButton.setText("Finish");
							SP_config.updateReviewPanel();
						}
					}
				}
			});
			backButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if( SP_config.isFirstPanel() )
					{
						switchToWelcomeSetupPanels();
					}
					else
					{
						SP_config.previousPanel();
						switch (SP_config.getCurrentPanelIndex()) {
							case 0:	setProgress(getProgress() & 12);	break;
							case 1:	setProgress(getProgress() & 13);	break;
							default:									break;
						}
						nextButton.setText("Next >");
					}
				}
			});
			setProgress(getProgress() & 12);

		} else
			switchToConfigSetupPanel(rightPanel);
	}
	//============================================================================================================
	

	
	
	
	
	
	
	//============================================================================================================
	//												HOME PANEL													//
	//============================================================================================================
	public void switchToHomeSetupPanel(JPanel parent)
	{
		if( setupPanels.containsKey(HOME_SETUP) ) {
			switchToConfigSetupPanel();
			return;
		}
		
		SP_home = new HomeSetupPanel();
		SP_home.hidePanels();
		setupPanels.put(HOME_SETUP, SP_home);
		parent.add(SP_home);
		switchToHomeSetupPanel();
	}
	@Reflectable
	public void switchToHomeSetupPanel()
	{
		if( setupPanels.containsKey(HOME_SETUP) )
		{
			hideAllPanels();
			SP_home.setVisible(true);
			SP_home.showFirstPanel();
			SP_home.switchToTab(SP_home.getCurrentTabIndex());

			backButton.setEnabled(false);	backButton.setVisible(false);
			nextButton.setEnabled(false);	nextButton.setVisible(false);
			backButton.setText("< Back");	nextButton.setText("Next >");
			configureButton.setEnabled(true);
			configureButton.setVisible(true);
			
			removeButtonActions();
			nextButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if( SP_home.isLastPanel() )
					{
						
					}
					else
					{
						SP_home.nextPanel();
					}
				}
			});
			backButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if( SP_home.isFirstPanel() )
					{
						switchToWelcomeSetupPanels();
					}
					else
					{
						SP_home.previousPanel();
					}
				}
			});
			configureButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					switchToConfigSetupPanel();
				}
			});
			
			// Refresh the tray tool tip
			TrayManager.refreshDefaultTrayToolTip();

		} else
			switchToHomeSetupPanel(rightPanel);
	}
	//============================================================================================================
	
	
	
	
	
	
	

	//============================================================================================================
	@Reflectable
	public void setProgress(Integer bit)
	{
		servletCheckbox.setSelected((bit & 1 << 0) > 0);
		databaseCheckbox.setSelected((bit & 1 << 1) > 0);
		installCheckbox.setSelected((bit & 1 << 2) > 0);
		configCheckbox.setSelected((bit & 1 << 3) > 0);
	}
	@Reflectable
	public Integer getProgress()
	{
		return  ((servletCheckbox.isSelected() ? 1 : 0) << 0) +
				((databaseCheckbox.isSelected() ? 1 : 0) << 1) +
				((installCheckbox.isSelected() ? 1 : 0) << 2) +
				((configCheckbox.isSelected() ? 1 : 0) << 3);
	}
	//============================================================================================================
	public void startTimers()
	{
		new Timer().schedule(new TimerTask() {
			@Override 
			public void run() {
				UpdateUtils.checkForServerUpdate(UpdateUtils.FROM_EVENT);
			}
		}, 60 * 60 * 1000, 60 * 60 * 1000);
		
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				StatsUtils.noop();
			}
		}, 5 * 1000, 60 * 1000);
	}
	//============================================================================================================
	private int updateToNewUpdater() throws IOException, InterruptedException
	{
		File oldUpdater = new File(Settings.BIN_DIRECTORY, Settings.UPDATER_JAR);
		File newUpdater = new File(Settings.BIN_DIRECTORY, Settings.UPDATER_NEW_JAR);
		
		return ( newUpdater.exists() ? FileUtils.move(newUpdater, oldUpdater, TransferUtils.OVERWRITE | TransferUtils.PRESERVE) : TransferUtils.COMPLETE );
	}
	//============================================================================================================
	public void hideAllPanels()
	{
		for( Entry<String, SetupPanel> entry : setupPanels.entrySet() ) {
			entry.getValue().hidePanels();
			entry.getValue().setVisible(false);
		}
	}
	//============================================================================================================
	public void removeButtonActions()
	{
		for( ActionListener a : backButton.getActionListeners() )
			backButton.removeActionListener(a);
		for( ActionListener a : nextButton.getActionListeners() )
			nextButton.removeActionListener(a);
		for( ActionListener a : configureButton.getActionListeners() )
			configureButton.removeActionListener(a);
	}
}
