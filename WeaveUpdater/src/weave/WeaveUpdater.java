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
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import weave.inc.ISetupPanel;
import weave.ui.CurSetupPanel;
import weave.ui.PostSetupPanel;
import weave.ui.PreSetupPanel;

public class WeaveUpdater
		extends JFrame
{
	private static final long serialVersionUID = 1L;
	public static WeaveUpdater updater = null;
	public static final String PRE_SETUP = "PRE_SETUP";
	public static final String CUR_SETUP = "CUR_SETUP";
	public static final String POST_SETUP = "POST_SETUP";
	public PreSetupPanel preSP = null;
	public CurSetupPanel curSP = null;
	public PostSetupPanel postSP = null;
	public HashMap<String, JPanel> setupPanels = new HashMap<String,JPanel>();
	public Thread ping = null;
	public JButton cancelButton = null;
	public JButton backButton = null;
	public JButton nextButton = null;
	public Timer pingTimer = null;
	Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	JPanel rightPanel = null;
	JButton helpButton = null;
	JButton configureButton = null;

	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			updater = new WeaveUpdater();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(64);
		}
		updater.addWindowListener(new WindowListener()
		{
			public void windowClosing(WindowEvent e)
			{
				System.out.println("Closing...");
				if (Settings.instance().UNZIP_DIRECTORY.exists())
				{
					System.out.println("Deleted");
					Revisions.recursiveDelete(Settings.instance().UNZIP_DIRECTORY);
					try
					{
						Thread.sleep(1000L);
					}
					catch (Exception e1)
					{
						e1.printStackTrace();
					}
				}
				System.exit(0);
			}

			public void windowDeactivated(WindowEvent e)
			{
			}

			public void windowClosed(WindowEvent e)
			{
			}

			public void windowActivated(WindowEvent e)
			{
			}

			public void windowDeiconified(WindowEvent e)
			{
			}

			public void windowIconified(WindowEvent e)
			{
			}

			public void windowOpened(WindowEvent e)
			{
			}
		});
	}

	public WeaveUpdater()
			throws Exception
	{
		setSize(500, 400);
		setResizable(false);
		setLayout(null);
		setTitle(Settings.instance().TITLE);
		setDefaultCloseOperation(0);
		setLocation(this.screen.width / 2 - getWidth() / 2, this.screen.height / 2 - getHeight() / 2);
		setIconImage(ImageIO.read(WeaveUpdater.class.getResource("/resources/update.png")));

		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(null);
		leftPanel.setBounds(0, 0, 150, 325);
		leftPanel.setBackground(new Color(15658734));
		leftPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.black));

		BufferedImage oicLogo = ImageIO.read(WeaveUpdater.class.getResource("/resources/oic4.png"));
		JLabel oicLabel = new JLabel("", new ImageIcon(oicLogo), 0);
		oicLabel.setBounds(10, 10, 125, 57);
		leftPanel.add(oicLabel);

		JLabel iweaveLink = new JLabel("oicweave.org");
		iweaveLink.setBounds(30, 300, 125, 20);
		iweaveLink.setCursor(new Cursor(12));
		iweaveLink.setFont(new Font("Corbel", 0, 15));
		iweaveLink.addMouseListener(new MouseListener()
		{
			public void mouseReleased(MouseEvent e)
			{
			}

			public void mousePressed(MouseEvent e)
			{
			}

			public void mouseExited(MouseEvent e)
			{
			}

			public void mouseEntered(MouseEvent e)
			{
			}

			public void mouseClicked(MouseEvent e)
			{
				if (Desktop.isDesktopSupported())
				{
					try
					{
						Desktop.getDesktop().browse(new URI("http://oicweave.org"));
					}
					catch (Exception e1)
					{
						e1.printStackTrace();
					}
				}
			}
		});
		leftPanel.add(iweaveLink);
		leftPanel.setVisible(false);
		add(leftPanel);

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(null);
		bottomPanel.setBounds(0, 325, 500, 50);
		bottomPanel.setBackground(new Color(5274282));
		bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.black));

		this.helpButton = new JButton("Help");
		this.helpButton.setBounds(10, 13, 80, 25);
		this.helpButton.setBackground(new Color(5274282));
		this.helpButton.setToolTipText("Open wiki page for help");
		this.helpButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				if (Desktop.isDesktopSupported())
				{
					try
					{
						Settings.instance().getClass();
						Desktop.getDesktop().browse(new URI("http://info.oicweave.org/projects/weave/wiki/Installer"));
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					JOptionPane.showMessageDialog(
							null, "This feature is not supported by the \nversion of Java you are running.", "Error", 0);
				}
			}
		});
		this.helpButton.setVisible(true);

		this.configureButton = new JButton("Configure");
		this.configureButton.setBounds(100, 13, 100, 25);
		this.configureButton.setBackground(new Color(5274282));
		this.configureButton.setToolTipText("Edit configuration settings or check for a new installation of Tomcat or MySQL.");
		this.configureButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				try
				{
					WeaveUpdater.this.switchToCurSetupPanel();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		this.configureButton.setVisible(false);

		this.backButton = new JButton("< Back");
		this.backButton.setBounds(200, 13, 80, 25);
		this.backButton.setBackground(new Color(5274282));

		this.nextButton = new JButton("Next >");
		this.nextButton.setBounds(280, 13, 80, 25);
		this.nextButton.setBackground(new Color(5274282));

		this.cancelButton = new JButton("Cancel");
		this.cancelButton.setBounds(400, 13, 80, 25);
		this.cancelButton.setBackground(new Color(5274282));
		this.cancelButton.setToolTipText("Close the installer");
		this.cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit?", "Confirm", 0);
				if (response == 0)
				{
					System.gc();
					try
					{
						Thread.sleep(200L);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					System.exit(0);
				}
			}
		});
		this.backButton.setEnabled(false);

		bottomPanel.add(this.helpButton);
		bottomPanel.add(this.configureButton);
		bottomPanel.add(this.backButton);
		bottomPanel.add(this.cancelButton);
		bottomPanel.add(this.nextButton);
		add(bottomPanel);

		this.rightPanel = new JPanel();
		this.rightPanel.setLayout(null);
		this.rightPanel.setBounds(150, 0, 350, 325);
		this.rightPanel.setBackground(new Color(16777215));
		this.rightPanel.setVisible(false);
		add(this.rightPanel);

		this.rightPanel.setVisible(true);
		bottomPanel.setVisible(true);
		leftPanel.setVisible(true);
		setVisible(true);

		switchToPreSetupPanel(this.rightPanel);
		if (Settings.instance().isConnectedToInternet().booleanValue())
		{
			Settings.instance().BEST_TOMCAT = Settings.instance().getBestTomcatURL();
			System.out.println("Tomcat: " + Settings.instance().BEST_TOMCAT);
			Settings.instance().BEST_MYSQL = Settings.instance().getBestMySQLURL();
			System.out.println("MySQL: " + Settings.instance().BEST_MYSQL);
		}
		else
		{
			this.pingTimer = new Timer();
			this.pingTimer.schedule(new TimerTask()
			{
				public void run()
				{
					if (Settings.instance().isConnectedToInternet().booleanValue())
					{
						Settings.instance().TOMCAT_FILE = new File(Settings.instance().EXE_DIRECTORY, "/tomcat_"
								+ Settings.instance().getLatestTomcatVersion() + ".exe");
						Settings.instance().BEST_TOMCAT = Settings.instance().getBestTomcatURL();
						System.out.println("Tomcat: " + Settings.instance().BEST_TOMCAT);
						Settings.instance().MySQL_FILE = new File(Settings.instance().EXE_DIRECTORY, "/mysql_"
								+ Settings.instance().getLatestMySQLVersion() + ".msi");
						Settings.instance().BEST_MYSQL = Settings.instance().getBestMySQLURL();
						System.out.println("MySQL: " + Settings.instance().BEST_MYSQL);
						WeaveUpdater.this.pingTimer.cancel();
						WeaveUpdater.this.nextButton.setEnabled(true);
					}
				}
			}, 1000L, 5000L);

			this.nextButton.setEnabled(false);
			JOptionPane.showMessageDialog(
					null,
					"Internet connection could not be established.\n\nPlease make sure you are connected to the\ninternet before you continue.",

					"Warning", 2);
		}
	}

	public void switchToPreSetupPanel()
		throws Exception
	{
		if (this.setupPanels.containsKey("PRE_SETUP"))
		{
			for (String key : this.setupPanels.keySet())
			{
				((ISetupPanel) this.setupPanels.get(key)).hidePanels();
				((JPanel) this.setupPanels.get(key)).setVisible(false);
			}
			((JPanel) this.setupPanels.get("PRE_SETUP")).setVisible(true);
			((ISetupPanel) this.setupPanels.get("PRE_SETUP")).showPanels();

			this.backButton.setEnabled(false);
			this.backButton.setVisible(true);
			this.nextButton.setEnabled(true);
			this.nextButton.setVisible(true);
			this.configureButton.setEnabled(true);
			this.configureButton.setVisible(false);

			removeButtonActions();
			this.preSP.addActionToButton(this.nextButton, new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					try
					{
						if (Settings.instance().settingsExists().booleanValue())
						{
							WeaveUpdater.this.switchToPostSetupPanel();
						}
						else
						{
							WeaveUpdater.this.switchToCurSetupPanel();
						}
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}
			});
			this.backButton.setEnabled(false);
			this.nextButton.setEnabled(true);
		}
		else
		{
			switchToPreSetupPanel(this.rightPanel);
		}
	}

	public void switchToPreSetupPanel(JPanel parent)
		throws Exception
	{
		if (this.setupPanels.containsKey("PRE_SETUP"))
		{
			switchToPreSetupPanel();
			return;
		}
		this.preSP = new PreSetupPanel();
		this.preSP.hidePanels();
		this.setupPanels.put("PRE_SETUP", this.preSP);
		parent.add(this.preSP);
		switchToPreSetupPanel();
	}

	public void switchToCurSetupPanel()
		throws Exception
	{
		if (this.setupPanels.containsKey("CUR_SETUP"))
		{
			for (String key : this.setupPanels.keySet())
			{
				((ISetupPanel) this.setupPanels.get(key)).hidePanels();
				((JPanel) this.setupPanels.get(key)).setVisible(false);
			}
			((JPanel) this.setupPanels.get("CUR_SETUP")).setVisible(true);
			((ISetupPanel) this.setupPanels.get("CUR_SETUP")).showPanels();

			removeButtonActions();
			this.curSP.addActionToButton(this.backButton, new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0)
				{
					if (WeaveUpdater.this.curSP.getCurrentPanelIndex() == 0)
					{
						try
						{
							WeaveUpdater.this.switchToPreSetupPanel();
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
					else if (WeaveUpdater.this.curSP.getCurrentPanelIndex() > 0)
					{
						WeaveUpdater.this.curSP.previousPanel();
						if ((WeaveUpdater.this.curSP.getCurrentPanelIndex() == WeaveUpdater.this.curSP.getNumberOfPanels() - 2)
								&& (!WeaveUpdater.this.curSP.tomcatCheck.isSelected())
								&& (!WeaveUpdater.this.curSP.mysqlCheck.isSelected()))
						{
							WeaveUpdater.this.backButton.doClick();
						}
						WeaveUpdater.this.nextButton.setEnabled(true);
						WeaveUpdater.this.backButton.setEnabled(true);
						WeaveUpdater.this.nextButton.setText("Next >");
						WeaveUpdater.this.nextButton.setBounds(
								WeaveUpdater.this.nextButton.getX(), WeaveUpdater.this.nextButton.getY(), 80,
								WeaveUpdater.this.nextButton.getHeight());
					}
				}
			});
			this.curSP.addActionToButton(this.nextButton, new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					if (WeaveUpdater.this.curSP.getCurrentPanelIndex() == WeaveUpdater.this.curSP.getNumberOfPanels() - 1)
					{
						if ((Settings.instance().MySQL_PORT == 0) || (Settings.instance().TOMCAT_PORT == 0)
								|| (Settings.instance().TOMCAT_DIR.equals("")))
						{
							JOptionPane.showMessageDialog(null, "Error validating settings information.", "Error", 0);
						}
						else if (Settings.instance().writeSettings().booleanValue())
						{
							JOptionPane.showMessageDialog(null, "Settings saved successfully", "Settings", 1);
							WeaveUpdater.this.nextButton.setBounds(
									WeaveUpdater.this.nextButton.getX(), WeaveUpdater.this.nextButton.getY(), 80,
									WeaveUpdater.this.nextButton.getHeight());
							try
							{
								WeaveUpdater.this.switchToPostSetupPanel(WeaveUpdater.this.rightPanel);
							}
							catch (Exception e1)
							{
								e1.printStackTrace();
							}
						}
						else
						{
							JOptionPane.showMessageDialog(null, "Error trying to save settings.", "Error", 0);
						}
					}
					else if (WeaveUpdater.this.curSP.getCurrentPanelIndex() < WeaveUpdater.this.curSP.getNumberOfPanels())
					{
						WeaveUpdater.this.curSP.nextPanel();
						if ((WeaveUpdater.this.curSP.getCurrentPanelIndex() == 1)
								&& (!WeaveUpdater.this.curSP.tomcatCheck.isSelected())
								&& (!WeaveUpdater.this.curSP.mysqlCheck.isSelected()))
						{
							WeaveUpdater.this.nextButton.doClick();
						}
						WeaveUpdater.this.backButton.setEnabled(true);
						WeaveUpdater.this.backButton.setVisible(true);
						if (WeaveUpdater.this.curSP.getCurrentPanelIndex() == WeaveUpdater.this.curSP.getNumberOfPanels() - 1)
						{
							WeaveUpdater.this.nextButton.setText("Save & Finish");
							WeaveUpdater.this.nextButton.setEnabled(true);
							WeaveUpdater.this.nextButton.setBounds(
									WeaveUpdater.this.nextButton.getX(), WeaveUpdater.this.nextButton.getY(), 100,
									WeaveUpdater.this.nextButton.getHeight());
						}
					}
				}
			});
			this.backButton.setEnabled(true);
			this.backButton.setVisible(true);
			this.nextButton.setEnabled(true);
			this.nextButton.setVisible(true);
			this.backButton.setText("< Back");
			this.nextButton.setText("Next >");
			this.configureButton.setEnabled(true);
			this.configureButton.setVisible(false);
		}
		else
		{
			switchToCurSetupPanel(this.rightPanel);
		}
	}

	public void switchToCurSetupPanel(JPanel parent)
		throws Exception
	{
		if (this.setupPanels.containsKey("CUR_SETUP"))
		{
			switchToCurSetupPanel();
			return;
		}
		this.curSP = new CurSetupPanel();
		this.curSP.hidePanels();
		this.curSP.addActionToButton(this.curSP.tomcatDownloadButton, new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				WeaveUpdater.this.curSP.tomcatDownloadButton.setEnabled(false);
				WeaveUpdater.this.nextButton.setEnabled(false);
				WeaveUpdater.this.backButton.setEnabled(false);
				WeaveUpdater.this.cancelButton.setEnabled(false);
				try
				{
					if (Settings.instance().TOMCAT_FILE.exists())
					{
						int response = JOptionPane.showConfirmDialog(
								null,
								"Weave Installer has detected that an executable installer already exists.\nWould you like to re-download and overwrite?",

								"Confirm", 0);
						if (response == 0)
						{
							try
							{
								WeaveUpdater.this.curSP.installTomcat.setVisible(false);
								WeaveUpdater.this.curSP.installTomcat.setEnabled(false);
								WeaveUpdater.this.curSP.progTomcat.downloadMSI(
										WeaveUpdater.this.curSP.tomcatPanel,
										WeaveUpdater.this.curSP.tomcatDownloadButton, Settings.MSI_TYPE.TOMCAT_MSI);
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
						}
						else
						{
							WeaveUpdater.this.curSP.tomcatDownloadButton.setEnabled(true);
							WeaveUpdater.this.nextButton.setEnabled(true);
							WeaveUpdater.this.backButton.setEnabled(true);
							WeaveUpdater.this.cancelButton.setEnabled(true);
						}
					}
					else
					{
						WeaveUpdater.this.curSP.installTomcat.setVisible(false);
						WeaveUpdater.this.curSP.installTomcat.setEnabled(false);
						WeaveUpdater.this.curSP.progTomcat.downloadMSI(
								WeaveUpdater.this.curSP.tomcatPanel, WeaveUpdater.this.curSP.tomcatDownloadButton,
								Settings.MSI_TYPE.TOMCAT_MSI);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		this.curSP.addActionToButton(this.curSP.mySQLDownloadButton, new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				WeaveUpdater.this.curSP.mySQLDownloadButton.setEnabled(false);
				WeaveUpdater.this.nextButton.setEnabled(false);
				WeaveUpdater.this.backButton.setEnabled(false);
				WeaveUpdater.this.cancelButton.setEnabled(false);
				try
				{
					if (Settings.instance().MySQL_FILE.exists())
					{
						int response = JOptionPane.showConfirmDialog(
								null,
								"Weave Installer has detected that an executable installer already exists.\nWould you like to re-download and overwrite?",

								"Confirm", 0);
						if (response == 0)
						{
							try
							{
								WeaveUpdater.this.curSP.installMySQL.setVisible(false);
								WeaveUpdater.this.curSP.progMySQL.downloadMSI(
										WeaveUpdater.this.curSP.mysqlPanel,
										WeaveUpdater.this.curSP.mySQLDownloadButton, Settings.MSI_TYPE.MySQL_MSI);
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
						}
						else
						{
							WeaveUpdater.this.curSP.mySQLDownloadButton.setEnabled(true);
							WeaveUpdater.this.nextButton.setEnabled(true);
							WeaveUpdater.this.backButton.setEnabled(true);
							WeaveUpdater.this.cancelButton.setEnabled(true);
						}
					}
					else
					{
						WeaveUpdater.this.curSP.installMySQL.setVisible(false);
						WeaveUpdater.this.curSP.progMySQL.downloadMSI(
								WeaveUpdater.this.curSP.mysqlPanel, WeaveUpdater.this.curSP.mySQLDownloadButton,
								Settings.MSI_TYPE.MySQL_MSI);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		this.curSP.addActionToButton(this.curSP.installTomcat, new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				WeaveUpdater.this.curSP.progMySQL.runExecutable(Settings.instance().TOMCAT_FILE);
			}
		});
		this.curSP.addActionToButton(this.curSP.installMySQL, new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				WeaveUpdater.this.curSP.progMySQL.runExecutable(Settings.instance().MySQL_FILE);
			}
		});
		this.curSP.addActionToButton(this.curSP.dirButton, new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				WeaveUpdater.this.curSP.dirChooser.fileChooser.setFileSelectionMode(1);
				int retVal = WeaveUpdater.this.curSP.dirChooser.fileChooser.showOpenDialog(null);
				if (retVal == 0)
				{
					String dir = WeaveUpdater.this.curSP.dirChooser.fileChooser.getSelectedFile().getPath();
					File f = new File(dir + "/webapps/ROOT/");
					File g = new File(dir + "/Uninstall.exe");
					if ((f.exists()) && (g.exists()))
					{
						Settings.instance().TOMCAT_DIR = dir;
					}
					else
					{
						Settings.instance().TOMCAT_DIR = "";
						JOptionPane.showMessageDialog(null, "Invalid Tomcat Directory", "Error", 0);
					}
				}
				WeaveUpdater.this.curSP.dirChooser.textField.setText(Settings.instance().TOMCAT_DIR);
			}
		});
		this.setupPanels.put("CUR_SETUP", this.curSP);
		parent.add(this.curSP);
		switchToCurSetupPanel();
	}

	public void switchToPostSetupPanel()
		throws Exception
	{
		if (this.setupPanels.containsKey("POST_SETUP"))
		{
			for (String key : this.setupPanels.keySet())
			{
				((ISetupPanel) this.setupPanels.get(key)).hidePanels();
				((JPanel) this.setupPanels.get(key)).setVisible(false);
			}
			((JPanel) this.setupPanels.get("POST_SETUP")).setVisible(true);
			((ISetupPanel) this.setupPanels.get("POST_SETUP")).showPanels();

			this.backButton.setEnabled(false);
			this.backButton.setVisible(false);
			this.nextButton.setEnabled(false);
			this.nextButton.setVisible(false);
			this.configureButton.setEnabled(true);
			this.configureButton.setVisible(true);

			removeButtonActions();
		}
		else
		{
			switchToPostSetupPanel(this.rightPanel);
		}
	}

	public void switchToPostSetupPanel(JPanel parent)
		throws Exception
	{
		if (this.setupPanels.containsKey("POST_SETUP"))
		{
			switchToPostSetupPanel();
			return;
		}
		this.postSP = new PostSetupPanel();
		this.postSP.hidePanels();
		this.postSP.addActionToButton(this.postSP.installButton, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (!Settings.instance().isConnectedToInternet().booleanValue())
				{
					WeaveUpdater.this.postSP.progress.progBar.setStringPainted(true);
					WeaveUpdater.this.postSP.progress.progBar.setString("No Internet Connection");
					WeaveUpdater.this.postSP.progress.progBar.setValue(0);
					return;
				}
				if (// (Settings.instance().isServiceUp(Settings.instance().TOMCAT_PORT).booleanValue()) &&
				(!Settings.instance().TOMCAT_DIR.equals("")))
				{
					WeaveUpdater.this.postSP.installButton.setEnabled(false);
					WeaveUpdater.this.postSP.deploy.setEnabled(false);
					WeaveUpdater.this.postSP.deleteButton.setEnabled(false);
					WeaveUpdater.this.postSP.checkButton.setEnabled(false);
					WeaveUpdater.this.postSP.pruneButton.setEnabled(false);
					WeaveUpdater.this.postSP.progress.downloadZip(WeaveUpdater.this.postSP.checkButton);
				}
				else
				{
					JOptionPane.showMessageDialog(null,
					// "Tomcat must be properly configured and running to install Weave.",
					"Tomcat must be properly configured to install Weave.", "Error", 0);
				}
			}
		});
		this.postSP.addActionToButton(this.postSP.checkButton, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Thread t = new Thread(new Runnable()
				{
					public void run()
					{
						WeaveUpdater.this.postSP.deploy.setEnabled(true);
						WeaveUpdater.this.postSP.deleteButton.setEnabled(true);
						WeaveUpdater.this.postSP.pruneButton.setEnabled(true);

						final int ret = Revisions.checkForUpdates(true);
						WeaveUpdater.this.postSP.weaveStats.refresh(ret);
						if ((ret == 1) && (!Settings.instance().TOMCAT_DIR.equals("")))
						{
							WeaveUpdater.this.postSP.installButton.setEnabled(true);
							WeaveUpdater.this.postSP.launchAdmin.setForeground(Color.BLACK);
						}
						else
						{
							WeaveUpdater.this.postSP.installButton.setEnabled(false);
						}
						WeaveUpdater.this.postSP.revisionTable.updateTableData();

						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								try
								{
									Thread.sleep(3000L);
								}
								catch (InterruptedException e)
								{
									e.printStackTrace();
								}
								WeaveUpdater.this.postSP.progress.progBar.setStringPainted(true);
								WeaveUpdater.this.postSP.progress.progBar.setValue(0);
								if (ret == -2)
								{
									WeaveUpdater.this.postSP.progress.progBar.setString("No Internet Connection");
								}
								else
								{
									WeaveUpdater.this.postSP.progress.progBar.setString("");
								}
							}
						});
					}
				});
				t.start();
			}
		});
		this.postSP.addActionToButton(this.postSP.deploy, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int n = WeaveUpdater.this.postSP.revisionTable.table.getSelectedRow();
				if (n < 0)
				{
					return;
				}
				File f = (File) Revisions.getRevisionData().get(n);
				if (!Settings.instance().TOMCAT_DIR.equals(""))
				{
					WeaveUpdater.this.postSP.installButton.setEnabled(false);
					WeaveUpdater.this.postSP.deleteButton.setEnabled(false);
					WeaveUpdater.this.postSP.deploy.setEnabled(false);
					WeaveUpdater.this.postSP.pruneButton.setEnabled(false);
					Revisions.extractZip(
							f.getPath(), WeaveUpdater.this.postSP.progress.progBar,
							WeaveUpdater.this.postSP.checkButton);
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Tomcat must be properly configured to deploy.", "Error", 0);
				}
			}
		});
		this.postSP.addActionToButton(this.postSP.deleteButton, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int n = WeaveUpdater.this.postSP.revisionTable.table.getSelectedRow();
				if (n < 0)
				{
					return;
				}
				File f = (File) Revisions.getRevisionData().get(n);
				if (Settings.instance().CURRENT_INSTALL_VER != null
					&& Settings.instance().CURRENT_INSTALL_VER.equals(Revisions.getRevisionName(f.getPath())))
				{
					JOptionPane.showMessageDialog(null, "Cannot delete current installation.", "Error", 0);
					return;
				}
				int val = JOptionPane.showConfirmDialog(
						null, "Deleting revisions cannot be undone.\n\nAre you sure you want to continue?", "Warning",
						0);
				if (val == 1)
				{
					return;
				}
				Revisions.recursiveDelete(f);
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						WeaveUpdater.this.postSP.checkButton.doClick();
					}
				});
			}
		});
		this.postSP.addActionToButton(this.postSP.pruneButton, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				double sizeMB = Revisions.getSizeOfRevisions() / 1024L / 1024L;
				int numRevs = Revisions.getNumberOfRevisions();
				if (numRevs >= Settings.instance().recommendPrune)
				{
					int val = JOptionPane.showConfirmDialog(
							null,
							"Auto-cleaned revisions will be deleted\nand cannot be undone.\n\nAre you sure you want to continue?",
							"Warning", 0, 2);
					if (val == 1)
					{
						return;
					}
					if (Revisions.pruneRevisions())
					{
						double newSize = Revisions.getSizeOfRevisions() / 1024L / 1024L;
						int newNumRevs = Revisions.getNumberOfRevisions();

						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								WeaveUpdater.this.postSP.checkButton.doClick();
							}
						});
						JOptionPane.showMessageDialog(
								null, "Auto-clean completed successfully!\n\nDeleted: " + (numRevs - newNumRevs)
										+ " files\n" + "Freed Up: " + (sizeMB - newSize) + "MB", "Finished", 1);
					}
					else
					{
						JOptionPane.showMessageDialog(
								null,
								"Sorry, the auto-clean feature encoutered\nan error and did not complete successfully.",
								"Error", 0);
					}
				}
				else
				{
					JOptionPane.showMessageDialog(null, "You need at least " + Settings.instance().recommendPrune
							+ " revisions for\n" + "the auto-clean feature to work.\n\n"
							+ "Please delete revisions manually.", "Warning", 2);
				}
			}
		});
		this.postSP.addActionToButton(this.postSP.launchAdmin, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (!Settings.instance().isServiceUp(Settings.instance().TOMCAT_PORT).booleanValue())
				{
					int n = JOptionPane.showConfirmDialog(
							null, "Tomcat service is not running.\n\nWould you like to launch AdminConsole anyway?\n",
							"Error", 0);
					if (n == 1)
					{
						return;
					}
				}
				if (Desktop.isDesktopSupported())
				{
					try
					{
						Desktop.getDesktop().browse(
								new URI("http://localhost:" + Settings.instance().TOMCAT_PORT + "/AdminConsole.html"));
					}
					catch (Exception e1)
					{
						e1.printStackTrace();
					}
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Feature not supported.", "Error", 0);
				}
			}
		});
		this.setupPanels.put("POST_SETUP", this.postSP);
		parent.add(this.postSP);
		switchToPostSetupPanel();
	}

	public void removeButtonActions()
	{
		for (ActionListener a : this.backButton.getActionListeners())
		{
			this.backButton.removeActionListener(a);
		}
		for (ActionListener a : this.nextButton.getActionListeners())
		{
			this.nextButton.removeActionListener(a);
		}
	}
}
