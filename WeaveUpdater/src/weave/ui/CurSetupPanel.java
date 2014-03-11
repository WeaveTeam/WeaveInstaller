package weave.ui;

import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import weave.Settings;
import weave.WeaveUpdater;
import weave.inc.SetupPanel;

public class CurSetupPanel
		extends SetupPanel
{
	private static final long serialVersionUID = 1L;
	public JButton dirButton = null;
	public DirectoryChooser dirChooser = null;
	public ProgressUpdate progTomcat;
	public ProgressUpdate progMySQL;
	public JPanel tomcatPanel;
	public JPanel mysqlPanel;
	public JCheckBox tomcatCheck;
	public JCheckBox mysqlCheck;
	public JLabel tomcatL;
	public JLabel mysqlL;
	public Timer tomcatT1;
	public Timer mysqlT1;
	public Status tomcatStatus1;
	public Status mysqlStatus1;
	public StatusConfig tomcatConfig;
	public StatusConfig mysqlConfig;
	public JButton installTomcat;
	public JButton installMySQL;
	public JButton tomcatDownloadButton;
	public JButton mySQLDownloadButton;
	public JLabel tomcatLabelSpeedHolder;
	public JLabel tomcatLabelTimeleftHolder;
	public JLabel tomcatLabelSizeDownloadHolder;
	public JLabel mysqlLabelSpeedHolder;
	public JLabel mysqlLabelTimeleftHolder;
	public JLabel mysqlLabelSizeDownloadHolder;

	public CurSetupPanel()
			throws Exception
	{
		this.maxPanels = 3;

		setLayout(null);
		setSize(350, 325);
		setBounds(0, 0, 350, 325);

		JPanel panel = null;
		for (int i = 0; i < this.maxPanels; i++)
		{
			switch (i)
			{
				case 0:
					panel = createAddonsMenu();
					break;
				case 1:
					panel = createDownloadPanel();
					break;
				case 2:
					panel = createFinishMenu();
			}
			this.panels.add(panel);
			add(panel);
		}
		hidePanels();
	}

	public JPanel createAddonsMenu()
		throws Exception
	{
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBounds(0, 0, 350, 325);
		panel.setBackground(new Color(16777215));

		JLabel welcome = new JLabel("Requirements");
		welcome.setFont(new Font("Corbel", 1, 19));
		welcome.setBounds(20, 20, 290, 25);

		JTextArea info = new JTextArea();
		info.setEditable(false);
		info.setLineWrap(true);
		info.setBounds(25, 60, 290, 150);
		info.setFont(new Font("Corbel", 0, 14));
		info.setText("Weave requires Tomcat and MySQL to run. \n\nIf you already have an active installation of \nTomcat and MySQL feel free to skip this step.\n\nOtherwise, check off which programs to \ninstall with this installer.");

		this.tomcatCheck = new JCheckBox("Include Tomcat");
		this.tomcatCheck.setBackground(Color.WHITE);
		this.tomcatCheck.setBounds(25, 210, 290, 25);
		this.tomcatCheck.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent arg0)
			{
				CurSetupPanel.this.tomcatPanel.setVisible(CurSetupPanel.this.tomcatCheck.isSelected());
				if ((!CurSetupPanel.this.tomcatCheck.isSelected()) && (CurSetupPanel.this.mysqlCheck.isSelected()))
				{
					CurSetupPanel.this.mysqlPanel.setBounds(0, 0, 350, 140);
				}
				else
				{
					CurSetupPanel.this.mysqlPanel.setBounds(0, 150, 350, 140);
				}
			}
		});
		this.mysqlCheck = new JCheckBox("Include MySQL");
		this.mysqlCheck.setBackground(Color.WHITE);
		this.mysqlCheck.setBounds(25, 235, 290, 25);
		this.mysqlCheck.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				CurSetupPanel.this.mysqlPanel.setVisible(CurSetupPanel.this.mysqlCheck.isSelected());
				if ((!CurSetupPanel.this.tomcatCheck.isSelected()) && (CurSetupPanel.this.mysqlCheck.isSelected()))
				{
					CurSetupPanel.this.mysqlPanel.setBounds(0, 0, 350, 140);
				}
				else
				{
					CurSetupPanel.this.mysqlPanel.setBounds(0, 150, 350, 140);
				}
			}
		});
		panel.add(welcome);
		panel.add(info);
		panel.add(this.tomcatCheck);
		panel.add(this.mysqlCheck);

		return panel;
	}

	public JPanel createDownloadPanel()
		throws Exception
	{
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBounds(0, 0, 350, 325);
		panel.setBackground(new Color(16777215));

		this.tomcatPanel = new JPanel();
		this.tomcatPanel.setLayout(null);
		this.tomcatPanel.setBounds(0, 0, 350, 140);
		this.tomcatPanel.setBackground(new Color(16777215));

		this.mysqlPanel = new JPanel();
		this.mysqlPanel.setLayout(null);
		this.mysqlPanel.setBounds(0, 150, 350, 140);
		this.mysqlPanel.setBackground(new Color(16777215));

		String tomcatS = Settings.instance().getLatestTomcatVersion();
		this.tomcatL = new JLabel("Tomcat " + (tomcatS == null
				? "" : tomcatS));
		this.tomcatL.setBounds(10, 5, 150, 25);
		this.tomcatL.setFont(new Font("Corbel", 1, 16));
		this.tomcatPanel.add(this.tomcatL);

		ImageIcon tomcatIcon = new ImageIcon(ImageIO.read(WeaveUpdater.class.getResource("/resources/tomcatlogo.png")));
		JLabel tomcatIconLabel = new JLabel(tomcatIcon);
		tomcatIconLabel.setBounds(30, 35, 69, 50);
		this.tomcatPanel.add(tomcatIconLabel);

		JLabel tomcatLabelSpeed = new JLabel("Download Rate:");
		tomcatLabelSpeed.setBounds(140, 10, 100, 20);
		this.tomcatPanel.add(tomcatLabelSpeed);

		this.tomcatLabelSpeedHolder = new JLabel();
		this.tomcatLabelSpeedHolder.setBounds(220, 10, 100, 20);
		this.tomcatLabelSpeedHolder.setHorizontalAlignment(4);
		this.tomcatPanel.add(this.tomcatLabelSpeedHolder);

		JLabel tomcatLabelTimeleft = new JLabel("Time left:");
		tomcatLabelTimeleft.setBounds(140, 30, 50, 20);
		this.tomcatPanel.add(tomcatLabelTimeleft);

		this.tomcatLabelTimeleftHolder = new JLabel();
		this.tomcatLabelTimeleftHolder.setBounds(220, 30, 100, 20);
		this.tomcatLabelTimeleftHolder.setHorizontalAlignment(4);
		this.tomcatPanel.add(this.tomcatLabelTimeleftHolder);

		this.tomcatLabelSizeDownloadHolder = new JLabel();
		this.tomcatLabelSizeDownloadHolder.setBounds(175, 90, 155, 20);
		this.tomcatLabelSizeDownloadHolder.setHorizontalAlignment(4);
		this.tomcatPanel.add(this.tomcatLabelSizeDownloadHolder);

		this.progTomcat = new ProgressUpdate();
		this.progTomcat.setBounds(140, 115, 190, 20);
		this.progTomcat.addPropertyChangeListener("MSI_SPEED", new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				CurSetupPanel.this.tomcatLabelSpeedHolder.setText(CurSetupPanel.this.progTomcat.msiInfo.strSpeed);
			}
		});
		this.progTomcat.addPropertyChangeListener("MSI_TIMELEFT", new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				CurSetupPanel.this.tomcatLabelTimeleftHolder.setText(CurSetupPanel.this.progTomcat.msiInfo.strTimeleft);
			}
		});
		this.progTomcat.addPropertyChangeListener("MSI_SIZEDOWNLOADED", new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				CurSetupPanel.this.tomcatLabelSizeDownloadHolder.setText(CurSetupPanel.this.progTomcat.msiInfo.strSizeDownloaded
						+ " of " + CurSetupPanel.this.progTomcat.msiInfo.strTotalSize);
			}
		});
		this.tomcatPanel.add(this.progTomcat);

		this.tomcatDownloadButton = new JButton("Download");
		this.tomcatDownloadButton.setBounds(5, 90, 100, 20);
		this.tomcatPanel.add(this.tomcatDownloadButton);

		this.installTomcat = new JButton("Install");
		this.installTomcat.setBounds(5, 115, 100, 20);
		if (Settings.instance().TOMCAT_FILE.exists())
		{
			this.installTomcat.setEnabled(true);
		}
		else
		{
			this.installTomcat.setEnabled(false);
		}
		this.tomcatPanel.add(this.installTomcat);

		String mysqlS = Settings.instance().getLatestMySQLVersion();
		this.mysqlL = new JLabel("MySQL " + (mysqlS == null
				? "" : mysqlS));
		this.mysqlL.setBounds(10, 0, 150, 25);
		this.mysqlL.setFont(new Font("Corbel", 1, 16));
		this.mysqlPanel.add(this.mysqlL);

		JLabel mysqlLabelSpeed = new JLabel("Download Rate:");
		mysqlLabelSpeed.setBounds(140, 10, 100, 20);
		this.mysqlPanel.add(mysqlLabelSpeed);

		this.mysqlLabelSpeedHolder = new JLabel();
		this.mysqlLabelSpeedHolder.setBounds(220, 10, 100, 20);
		this.mysqlLabelSpeedHolder.setHorizontalAlignment(4);
		this.mysqlPanel.add(this.mysqlLabelSpeedHolder);

		JLabel mysqlLabelTimeleft = new JLabel("Time left:");
		mysqlLabelTimeleft.setBounds(140, 30, 50, 20);
		this.mysqlPanel.add(mysqlLabelTimeleft);

		this.mysqlLabelTimeleftHolder = new JLabel();
		this.mysqlLabelTimeleftHolder.setBounds(220, 30, 100, 20);
		this.mysqlLabelTimeleftHolder.setHorizontalAlignment(4);
		this.mysqlPanel.add(this.mysqlLabelTimeleftHolder);

		this.mysqlLabelSizeDownloadHolder = new JLabel();
		this.mysqlLabelSizeDownloadHolder.setBounds(175, 90, 155, 20);
		this.mysqlLabelSizeDownloadHolder.setHorizontalAlignment(4);
		this.mysqlPanel.add(this.mysqlLabelSizeDownloadHolder);

		ImageIcon mySQLIcon = new ImageIcon(
				ImageIO.read(WeaveUpdater.class.getResource("/resources/mysql-dolphin.png")));
		JLabel mySQLIconLabel = new JLabel(mySQLIcon);
		mySQLIconLabel.setBounds(25, 35, 88, 47);
		this.mysqlPanel.add(mySQLIconLabel);

		this.progMySQL = new ProgressUpdate();
		this.progMySQL.setBounds(140, 115, 190, 20);
		this.progMySQL.addPropertyChangeListener("MSI_SPEED", new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				CurSetupPanel.this.mysqlLabelSpeedHolder.setText(CurSetupPanel.this.progMySQL.msiInfo.strSpeed);
			}
		});
		this.progMySQL.addPropertyChangeListener("MSI_TIMELEFT", new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				CurSetupPanel.this.mysqlLabelTimeleftHolder.setText(CurSetupPanel.this.progMySQL.msiInfo.strTimeleft);
			}
		});
		this.progMySQL.addPropertyChangeListener("MSI_SIZEDOWNLOADED", new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				CurSetupPanel.this.mysqlLabelSizeDownloadHolder.setText(CurSetupPanel.this.progMySQL.msiInfo.strSizeDownloaded
						+ " of " + CurSetupPanel.this.progMySQL.msiInfo.strTotalSize);
			}
		});
		this.mysqlPanel.add(this.progMySQL);

		this.mySQLDownloadButton = new JButton("Download");
		this.mySQLDownloadButton.setBounds(5, 90, 100, 20);
		this.mysqlPanel.add(this.mySQLDownloadButton);

		this.installMySQL = new JButton("Install");
		this.installMySQL.setBounds(5, 115, 100, 20);
		this.mysqlPanel.add(this.installMySQL);
		if (Settings.instance().MySQL_FILE.exists())
		{
			this.installMySQL.setEnabled(true);
		}
		else
		{
			this.installMySQL.setEnabled(false);
		}
		this.mysqlPanel.setVisible(false);
		this.tomcatPanel.setVisible(false);

		panel.add(this.mysqlPanel);
		panel.add(this.tomcatPanel);

		return panel;
	}

	public JPanel createFinishMenu()
	{
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBounds(0, 0, 350, 325);
		panel.setBackground(new Color(16777215));

		JLabel placeholder = new JLabel("Review Settings");
		placeholder.setFont(new Font("Corbel", 1, 19));
		placeholder.setBounds(10, 20, 290, 25);

		JTextArea info = new JTextArea(3, 8);
		info.setEditable(false);
		info.setLineWrap(true);
		info.setText("Take this time to ensure that Tomcat and\nMySQL are both running as services on the\nspecified ports and specify the Tomcat directory.");

		info.setFont(new Font("Corbel", 0, 14));
		info.setBounds(10, 50, 310, 80);
		panel.add(info);

		this.mysqlT1 = new Timer();
		this.tomcatT1 = new Timer();
		this.mysqlStatus1 = new Status("MySQL", Settings.instance().isServiceUp(Settings.instance().MySQL_PORT));
		this.tomcatStatus1 = new Status("Tomcat", Settings.instance().isServiceUp(Settings.instance().TOMCAT_PORT));

		this.mysqlT1.schedule(new TimerTask()
		{
			public void run()
			{
				CurSetupPanel.this.mysqlStatus1.refresh(Settings.instance().isServiceUp(Settings.instance().MySQL_PORT));
			}
		}, 7000L, 5000L);
		this.tomcatT1.schedule(new TimerTask()
		{
			public void run()
			{
				CurSetupPanel.this.tomcatStatus1.refresh(Settings.instance().isServiceUp(
						Settings.instance().TOMCAT_PORT));
			}
		}, 5000L, 5000L);

		this.mysqlConfig = new StatusConfig("MySQL Port:", Settings.SERVICE_PORTS.MySQL);
		this.tomcatConfig = new StatusConfig("Tomcat Port:", Settings.SERVICE_PORTS.TOMCAT);
		this.dirChooser = new DirectoryChooser("Tomcat Directory:");
		this.dirButton = new JButton("...");

		this.mysqlStatus1.setBounds(20, 140, 260, 25);
		this.tomcatStatus1.setBounds(20, 170, 260, 25);
		this.mysqlConfig.setBounds(20, 205, 260, 20);
		this.tomcatConfig.setBounds(20, 235, 260, 20);
		this.dirChooser.setBounds(20, 265, 260, 25);
		this.dirButton.setBounds(285, 265, 36, 25);

		this.mysqlStatus1.setBackground(new Color(16777215));
		this.tomcatStatus1.setBackground(new Color(16777215));
		this.mysqlConfig.setBackground(new Color(16777215));
		this.tomcatConfig.setBackground(new Color(16777215));
		this.dirChooser.setBackground(new Color(16777215));
		this.dirButton.setBackground(new Color(16777215));

		panel.add(this.mysqlStatus1);
		panel.add(this.tomcatStatus1);
		panel.add(this.mysqlConfig);
		panel.add(this.tomcatConfig);
		panel.add(this.dirChooser);
		panel.add(this.dirButton);

		this.dirChooser.textField.setText(Settings.instance().TOMCAT_DIR);
		this.mysqlConfig.textPort.setText("" + Settings.instance().MySQL_PORT);
		this.tomcatConfig.textPort.setText("" + Settings.instance().TOMCAT_PORT);

		panel.add(placeholder);
		return panel;
	}
}
