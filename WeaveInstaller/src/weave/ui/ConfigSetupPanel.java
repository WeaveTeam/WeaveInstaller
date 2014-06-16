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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Timer;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import weave.Settings;
import weave.configs.IConfig;
import weave.inc.SetupPanel;
import weave.managers.ConfigManager;
import weave.utils.ImageUtils;

@SuppressWarnings("serial")
public class ConfigSetupPanel extends SetupPanel
{
	public JComboBox			servletCombo,	databaseCombo;
	public JLabel				servletImage, 	databaseImage;
	public JLabel				servletLevel,	databaseLevel;
	public JTextArea			servletDesc, 	databaseDesc;
	public JFileChooser			servletFileChooser;
	public JLabel				servletWebappsLabel;
	
	public JButton 				dirButton 		= null;
	public DirectoryChooser 	dirChooser 		= null;
	public ProgressUpdate 		progTomcat, 	progMySQL;
	public JPanel				tomcatPanel, 	mysqlPanel;
	public JCheckBox			tomcatCheck, 	mysqlCheck;
	public JLabel				tomcatL, 		mysqlL;
	public Timer 				tomcatT1, 		mysqlT1;
	public Status 				tomcatStatus1, 	mysqlStatus1;
	public StatusConfig 		tomcatConfig, 	mysqlConfig;
	public JButton				installTomcat,	installMySQL;
	public JButton 				tomcatDownloadButton, mySQLDownloadButton ;
	public JLabel				tomcatLabelSpeedHolder, tomcatLabelTimeleftHolder, tomcatLabelSizeDownloadHolder;
	public JLabel				mysqlLabelSpeedHolder, mysqlLabelTimeleftHolder, mysqlLabelSizeDownloadHolder;
	
	public ConfigSetupPanel()
	{
		maxPanels = 3;
		
		setLayout(null);
		setBounds(0, 0, 350, 325);
		
		JPanel panel = null;
		for (int i = 0; i < maxPanels; i++) {
			switch (i) {
				case 0: panel = createServletPanel(); 	break;
				case 1: panel = createDatabasePanel();	break;
				case 2: panel = createReviewPanel();	break;
			}
			panels.add(panel);
			add(panel);
		}
		hidePanels();
	}
	
	public JPanel createServletPanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBounds(0, 0, 350, 325);
		panel.setBackground(new Color(0xFFFFFF));
		
		
		// Title
		JLabel title = new JLabel("Select Servlet");
		title.setFont(new Font(Settings.FONT, Font.BOLD, 19));
		title.setBounds(20, 20, 140, 30);
		panel.add(title);
		
		// Experience label
		servletLevel = new JLabel("Experience Level: ");
		servletLevel.setFont(new Font(Settings.FONT, Font.BOLD, 16));
		servletLevel.setBounds(20, 60, 300, 25);
		panel.add(servletLevel);
		
		// Servlet Combobox
		List<IConfig> servlets = ConfigManager.getConfigManager().getServletConfigs();
		servletCombo = new JComboBox();
		servletCombo.setBounds(160, 22, 170, 22);
		servletCombo.setVisible(true);
		servletCombo.setEnabled(true);
		servletCombo.setFont(new Font(Settings.FONT, Font.PLAIN, 16));
		
		for( int i = 0; i < servlets.size(); i++ ) 
			servletCombo.addItem(servlets.get(i).getConfigName());
		
		servletCombo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateServletInfo(
						ConfigManager
						.getConfigManager()
						.getConfigByName(servletCombo.getSelectedItem()));
			}
		});
		panel.add(servletCombo);
		
		
		// Image
		servletImage = new JLabel((ImageIcon)null, JLabel.CENTER);
		servletImage.setBounds(20, 100, 120, 120);
		servletImage.setVerticalAlignment(JLabel.TOP);
		panel.add(servletImage);

		
		// Description
		servletDesc = new JTextArea();
		servletDesc.setBounds(160, 90, 170, 140);
		servletDesc.setEditable(false);
		servletDesc.setLineWrap(true);
		servletDesc.setFont(new Font(Settings.FONT, Font.PLAIN, 14));
		panel.add(servletDesc);
		
		
		// Servlet directory label
		JLabel webappsLabel = new JLabel("Webapps:");
		webappsLabel.setBounds(20, 240, 70, 25);
		webappsLabel.setFont(new Font(Settings.FONT, Font.BOLD, 14));
		panel.add(webappsLabel);
		
		
		// Servlet directory chooser
		servletFileChooser = new JFileChooser();
		servletFileChooser.setMultiSelectionEnabled(false);
		servletFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		servletFileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		
		JTextArea fileChooseText = new JTextArea();
		fileChooseText.setBounds(100, 240, 100, 25);
		fileChooseText.setEditable(false);
		fileChooseText.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(0, 0, 0)));
		fileChooseText.setVisible(true);
		panel.add(fileChooseText);
		
		JButton fileChooseButton = new JButton("Browse");
		fileChooseButton.setBounds(260, 240, 70, 25);
		fileChooseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int retVal = servletFileChooser.showOpenDialog(ConfigSetupPanel.this);
				if( retVal == JFileChooser.APPROVE_OPTION )
				{
					IConfig config = ConfigManager
										.getConfigManager()
										.getConfigByName(servletCombo.getSelectedItem());
					File selectedFolder = servletFileChooser.getSelectedFile();
					if( (new File(selectedFolder, "ROOT")).exists() )
					{
						config.setWebappsDirectory(selectedFolder);
					}
				}
			}
		});
		fileChooseButton.setVisible(true);
		panel.add(fileChooseButton);
		
		
		// Apply default values
		servletCombo.setSelectedIndex(0);
		
		
		return panel;
	}
	
	public JPanel createDatabasePanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBounds(0, 0, 350, 325);
		panel.setBackground(new Color(0xFFFFFF));
		
		List<IConfig> databases = ConfigManager.getConfigManager().getDatabaseConfigs();
		databaseCombo = new JComboBox();
		databaseCombo.setBounds(20, 50, 200, 50);
		for( int i = 0; i < databases.size(); i++ )
			databaseCombo.addItem(databases.get(i));
		databaseCombo.setVisible(true);
		databaseCombo.setEnabled(true);
		panel.add(databaseCombo);
		
		
		return panel;
	}

	public JPanel createReviewPanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBounds(0, 0, 350, 325);
		panel.setBackground(new Color(0xFFFFFF));
		
		return panel;
	}
	
	
	
	private void updateServletInfo(IConfig servlet)
	{
		if( servlet == null )
			return;
		
		if( servletLevel != null )
			servletLevel.setText("Experience Level: " + servlet.getTechLevel());
		if( servletImage != null )
			servletImage.setIcon(
					new ImageIcon(
							ImageUtils.scale(
									servlet.getImage(), 
									servletImage.getWidth(), 
									ImageUtils.SCALE_WIDTH)));
		if( servletDesc != null )
			servletDesc.setText(servlet.getDescription());
		
		boolean visible = !servlet.getConfigName().equals("Jetty");
		
	}
	private void updateDatabaseInfo(IConfig database)
	{
		if( database == null )
			return;
		
		if( databaseLevel != null )
			databaseLevel.setText("Experience Level: " + database.getTechLevel());
		if( databaseImage != null )
			databaseImage.setIcon(
					new ImageIcon(
							ImageUtils.scale(
									database.getImage(), 
									databaseImage.getWidth(), 
									ImageUtils.SCALE_WIDTH)));
		if( databaseDesc != null )
			databaseDesc.setText(database.getDescription());
	}
	

	/*
	public JPanel createAddonsMenu() throws Exception
	{
		final JPanel panel = new JPanel() ;
		panel.setLayout(null) ;
		panel.setBounds( 0, 0, 500-150, 325) ;
		panel.setBackground( new Color(0xFFFFFF) );

		JLabel welcome = new JLabel("Requirements");
		welcome.setFont(new Font("Corbel", Font.BOLD, 19));
		welcome.setBounds(20, 20, 290, 25);
		
		JTextArea info = new JTextArea();
		info.setEditable(false);
		info.setLineWrap(true);
		info.setBounds(25, 60, 290, 150);
		info.setFont(new Font("Corbel", Font.PLAIN, 14));
		info.setText("Weave requires Tomcat and MySQL to run. " +
					 "\n\nIf you already have an active installation of "+
					 "\nTomcat and MySQL feel free to skip this step." + 
					 "\n\nOtherwise, check off which programs to " +
					 "\ninstall with this installer.");
		
		tomcatCheck = new JCheckBox("Include Tomcat");
		tomcatCheck.setBackground(Color.WHITE);
		tomcatCheck.setBounds(25, 210, 290, 25);
		tomcatCheck.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				tomcatPanel.setVisible(tomcatCheck.isSelected());
				
				if( !tomcatCheck.isSelected() && mysqlCheck.isSelected() )
					mysqlPanel.setBounds(0, 0, 350, 140);
				else
					mysqlPanel.setBounds(0, 150, 350, 140);
			}
		});

		mysqlCheck = new JCheckBox("Include MySQL");
		mysqlCheck.setBackground(Color.WHITE);
		mysqlCheck.setBounds(25, 235, 290, 25);
		mysqlCheck.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				mysqlPanel.setVisible(mysqlCheck.isSelected());
				
				if( !tomcatCheck.isSelected() && mysqlCheck.isSelected() )
					mysqlPanel.setBounds(0, 0, 350, 140);
				else
					mysqlPanel.setBounds(0, 150, 350, 140);
			}
		});
		
		panel.add(welcome);
		panel.add(info);
		panel.add(tomcatCheck);
		panel.add(mysqlCheck);
		
		return panel ;
	}

	public JPanel createDownloadPanel() throws Exception
	{
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBounds(0, 0, 500 - 150, 325);
		panel.setBackground(new Color(0xFFFFFF));
		
		tomcatPanel = new JPanel();
		tomcatPanel.setLayout(null);
		tomcatPanel.setBounds(0, 0, 350, 140);
		tomcatPanel.setBackground(new Color(0xFFFFFF));

		mysqlPanel = new JPanel();
		mysqlPanel.setLayout(null);
		mysqlPanel.setBounds(0, 150, 350, 140);
		mysqlPanel.setBackground(new Color(0xFFFFFF));
		
		String tomcatS = RemoteUtils.getConfigEntry(RemoteUtils.TOMCAT_VERSION);
		tomcatL = new JLabel("Tomcat " + ( tomcatS == null ? "" : tomcatS ));
		tomcatL.setBounds(10, 5, 150, 25);
		tomcatL.setFont(new Font("Corbel", Font.BOLD, 16));
		tomcatPanel.add(tomcatL);
		
		ImageIcon tomcatIcon = new ImageIcon(ImageIO.read(WeaveInstaller.class.getResource("/resources/tomcatlogo.png")));
		final JLabel tomcatIconLabel = new JLabel(tomcatIcon) ;
		tomcatIconLabel.setBounds(30, 35, 69, 50) ;
		tomcatPanel.add(tomcatIconLabel) ;

		JLabel tomcatLabelSpeed = new JLabel("Download Rate:");
		tomcatLabelSpeed.setBounds(140, 10, 100, 20);
		tomcatPanel.add(tomcatLabelSpeed);

		tomcatLabelSpeedHolder = new JLabel();
		tomcatLabelSpeedHolder.setBounds(220, 10, 100, 20);
		tomcatLabelSpeedHolder.setHorizontalAlignment(JLabel.RIGHT);
		tomcatPanel.add(tomcatLabelSpeedHolder);
		
		JLabel tomcatLabelTimeleft = new JLabel("Time left:");
		tomcatLabelTimeleft.setBounds(140, 30, 50, 20);
		tomcatPanel.add(tomcatLabelTimeleft);

		tomcatLabelTimeleftHolder = new JLabel();
		tomcatLabelTimeleftHolder.setBounds(220, 30, 100, 20);
		tomcatLabelTimeleftHolder.setHorizontalAlignment(JLabel.RIGHT);
		tomcatPanel.add(tomcatLabelTimeleftHolder);

		tomcatLabelSizeDownloadHolder = new JLabel();
		tomcatLabelSizeDownloadHolder.setBounds(175, 90, 155, 20);
		tomcatLabelSizeDownloadHolder.setHorizontalAlignment(JLabel.RIGHT);
		tomcatPanel.add(tomcatLabelSizeDownloadHolder);
		
		progTomcat = new ProgressUpdate() ;
		progTomcat.setBounds( 140, 115, 190, 20 ) ;
		progTomcat.addPropertyChangeListener(ProgressUpdate.MSI_SPEED, new PropertyChangeListener() {
			@Override public void propertyChange(PropertyChangeEvent evt) {
				tomcatLabelSpeedHolder.setText(progTomcat.msiInfo.strSpeed);
			}
		});
		progTomcat.addPropertyChangeListener(ProgressUpdate.MSI_TIMELEFT, new PropertyChangeListener() {
			@Override public void propertyChange(PropertyChangeEvent evt) {
				tomcatLabelTimeleftHolder.setText(progTomcat.msiInfo.strTimeleft);
			}
		});
		progTomcat.addPropertyChangeListener(ProgressUpdate.MSI_SIZEDOWNLOADED, new PropertyChangeListener() {
			@Override public void propertyChange(PropertyChangeEvent evt) {
				tomcatLabelSizeDownloadHolder.setText(progTomcat.msiInfo.strSizeDownloaded+" of "+progTomcat.msiInfo.strTotalSize);
			}
		});
		tomcatPanel.add( progTomcat ) ;

		tomcatDownloadButton = new JButton("Download");
		tomcatDownloadButton.setBounds(5, 90, 100, 20);
		tomcatPanel.add(tomcatDownloadButton);
		
		installTomcat = new JButton( "Install" ) ;
		installTomcat.setBounds( 5, 115, 100, 20 ) ;

		if( Tomcat.getConfig().TOMCAT_INSTALL_FILE.exists() )
			installTomcat.setEnabled( true ) ;
		else
			installTomcat.setEnabled( false ) ;
		tomcatPanel.add( installTomcat ) ;
		
		String mysqlS = RemoteUtils.getConfigEntry(RemoteUtils.MYSQL_VERSION);
		mysqlL = new JLabel("MySQL " + ( mysqlS == null ? "" : mysqlS ));
		mysqlL.setBounds(10, 0, 150, 25);
		mysqlL.setFont(new Font("Corbel", Font.BOLD, 16));
		mysqlPanel.add(mysqlL);

		JLabel mysqlLabelSpeed = new JLabel("Download Rate:");
		mysqlLabelSpeed.setBounds(140, 10, 100, 20);
		mysqlPanel.add(mysqlLabelSpeed);
		
		mysqlLabelSpeedHolder = new JLabel();
		mysqlLabelSpeedHolder.setBounds(220, 10, 100, 20);
		mysqlLabelSpeedHolder.setHorizontalAlignment(JLabel.RIGHT);
		mysqlPanel.add(mysqlLabelSpeedHolder);
		
		JLabel mysqlLabelTimeleft = new JLabel("Time left:");
		mysqlLabelTimeleft.setBounds(140, 30, 50, 20);
		mysqlPanel.add(mysqlLabelTimeleft);
		
		mysqlLabelTimeleftHolder = new JLabel();
		mysqlLabelTimeleftHolder.setBounds(220, 30, 100, 20);
		mysqlLabelTimeleftHolder.setHorizontalAlignment(JLabel.RIGHT);
		mysqlPanel.add(mysqlLabelTimeleftHolder);

		mysqlLabelSizeDownloadHolder = new JLabel();
		mysqlLabelSizeDownloadHolder.setBounds(175, 90, 155, 20);
		mysqlLabelSizeDownloadHolder.setHorizontalAlignment(JLabel.RIGHT);
		mysqlPanel.add(mysqlLabelSizeDownloadHolder);
		
		ImageIcon mySQLIcon = new ImageIcon(ImageIO.read(WeaveInstaller.class.getResource("/resources/mysql-dolphin.png")));
		JLabel mySQLIconLabel = new JLabel(mySQLIcon);
		mySQLIconLabel.setBounds(25, 35, 88, 47);
		mysqlPanel.add(mySQLIconLabel);

		progMySQL = new ProgressUpdate();
		progMySQL.setBounds( 140, 115, 190, 20 );
		progMySQL.addPropertyChangeListener(ProgressUpdate.MSI_SPEED, new PropertyChangeListener() {
			@Override public void propertyChange(PropertyChangeEvent evt) {
				mysqlLabelSpeedHolder.setText(progMySQL.msiInfo.strSpeed);
			}
		});
		progMySQL.addPropertyChangeListener(ProgressUpdate.MSI_TIMELEFT, new PropertyChangeListener() {
			@Override public void propertyChange(PropertyChangeEvent evt) {
				mysqlLabelTimeleftHolder.setText(progMySQL.msiInfo.strTimeleft);
			}
		});
		progMySQL.addPropertyChangeListener(ProgressUpdate.MSI_SIZEDOWNLOADED, new PropertyChangeListener() {
			@Override public void propertyChange(PropertyChangeEvent evt) {
				mysqlLabelSizeDownloadHolder.setText(progMySQL.msiInfo.strSizeDownloaded+" of "+progMySQL.msiInfo.strTotalSize);
			}
		});
		mysqlPanel.add( progMySQL );
		
		mySQLDownloadButton = new JButton("Download");
		mySQLDownloadButton.setBounds(5, 90, 100, 20);
		mysqlPanel.add(mySQLDownloadButton);
		
		installMySQL = new JButton( "Install" ) ;
		installMySQL.setBounds(5, 115, 100, 20) ;
		mysqlPanel.add( installMySQL ) ;

		if( MySQL.getConfig().MYSQL_INSTALL_FILE.exists() )
			installMySQL.setEnabled( true ) ;
		else
			installMySQL.setEnabled( false ) ;
		
		mysqlPanel.setVisible(false);
		tomcatPanel.setVisible(false);
		
		panel.add(mysqlPanel);
		panel.add(tomcatPanel);

		return panel;
	}

	public JPanel createFinishMenu()
	{
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBounds(0, 0, 500 - 150, 325);
		panel.setBackground(new Color(0xFFFFFF));

		JLabel placeholder = new JLabel("Review Settings");
		placeholder.setFont(new Font("Corbel", Font.BOLD, 19));
		placeholder.setBounds(10, 20, 290, 25);
		
		JTextArea info = new JTextArea(3, 8);
		info.setEditable(false);
		info.setLineWrap(true);
		info.setText("Take this time to ensure that Tomcat and" +
					 "\nMySQL are both running as services on the" +
					 "\nspecified ports and specify the Tomcat directory.");
		info.setFont(new Font("Corbel", Font.PLAIN, 14));
		info.setBounds(10, 50, 310, 80);
		panel.add( info ) ;
		
		mysqlT1 = new Timer();
     	tomcatT1 = new Timer();
		mysqlStatus1 = new Status("MySQL", Settings.isServiceUp("sdf", MySQL.getConfig().MYSQL_PORT));
		tomcatStatus1 = new Status("Tomcat", Settings.isServiceUp("sdf", Tomcat.getConfig().TOMCAT_PORT));

		mysqlT1.schedule(new TimerTask() {
			@Override
			public void run() {
				mysqlStatus1.refresh(Settings.isServiceUp("sdf", MySQL.getConfig().MYSQL_PORT));
			}
		}, 7000, 5000);
		tomcatT1.schedule(new TimerTask() {
			@Override
			public void run() {
				tomcatStatus1.refresh(Settings.isServiceUp("sdf", Tomcat.getConfig().TOMCAT_PORT));
			}
		}, 5000, 5000);

		mysqlConfig = new StatusConfig("MySQL Port:", SERVICE_PORTS.MySQL);
		tomcatConfig = new StatusConfig("Tomcat Port:", SERVICE_PORTS.TOMCAT);
		dirChooser = new DirectoryChooser("Tomcat Directory:");
		dirButton = new JButton("...");
	
		// ======== SET COORDINATES ======== //
		mysqlStatus1.setBounds(20, 140, 260, 25);
		tomcatStatus1.setBounds(20, 170, 260, 25);
		mysqlConfig.setBounds(20, 205, 260, 20);
		tomcatConfig.setBounds(20, 235, 260, 20);
		dirChooser.setBounds(20, 265, 260, 25); 
		dirButton.setBounds(285, 265, 36, 25); 

		mysqlStatus1.setBackground(  new Color(0xFFFFFF) ) ;
		tomcatStatus1.setBackground( new Color(0xFFFFFF) ) ;
		mysqlConfig.setBackground(   new Color(0xFFFFFF) ) ;
		tomcatConfig.setBackground(  new Color(0xFFFFFF) ) ;
		dirChooser.setBackground(    new Color(0xFFFFFF) ) ;
		dirButton.setBackground(     new Color(0xFFFFFF) ) ;
		
		panel.add( mysqlStatus1 ) ;
		panel.add( tomcatStatus1 ) ;
		panel.add( mysqlConfig ) ;
		panel.add( tomcatConfig ) ;
		panel.add( dirChooser ) ;
		panel.add( dirButton ) ;
		
		if( Tomcat.getConfig().getHomeDirectory() == null )
			dirChooser.textField.setText("");
		else
			dirChooser.textField.setText(Tomcat.getConfig().getHomeDirectory().getAbsolutePath());
		mysqlConfig.textPort.setText(MySQL.getConfig().MYSQL_PORT + "");
		tomcatConfig.textPort.setText(Tomcat.getConfig().TOMCAT_PORT + "");

		panel.add(placeholder);
		return panel;
	}
	*/
}
