package weave.ui;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import weave.Revisions;
import weave.Settings;
import weave.WeaveUpdater;
import weave.utils.Strings;

public class ProgressUpdate
		extends JPanel
{
	private static final long serialVersionUID = 1L;
	public static final String ZIP_SPEED = "ZIP_SPEED";
	public static final String ZIP_PERCENT = "ZIP_PERCENT";
	public static final String ZIP_TIMELEFT = "ZIP_TIMELEFT";
	public static final String ZIP_SIZEDOWNLOADED = "ZIP_SIZEDOWNLOADED";
	public static final String ZIP_TOTALSIZE = "ZIP_TOTALSIZE";
	public static final String MSI_SPEED = "MSI_SPEED";
	public static final String MSI_PERCENT = "MSI_PERCENT";
	public static final String MSI_TIMELEFT = "MSI_TIMELEFT";
	public static final String MSI_SIZEDOWNLOADED = "MSI_SIZEDOWNLOADED";
	public static final String MSI_TOTALSIZE = "MSI_TOTALSIZE";
	public JProgressBar progBar;
	public DownloadInfo zipInfo;
	public DownloadInfo msiInfo;

	public ProgressUpdate()
	{
		setLayout(null);
		setBackground(new Color(16777215));

		this.progBar = new JProgressBar(0, 100);

		add(this.progBar);
	}

	public String setZipSpeed(String s)
	{
		firePropertyChange("ZIP_SPEED", this.zipInfo.strSpeed, s);
		this.zipInfo.strSpeed = s;
		return s;
	}

	public String setZipPercent(String s)
	{
		firePropertyChange("ZIP_PERCENT", this.zipInfo.strPercent, s);
		this.zipInfo.strPercent = s;
		return s;
	}

	public String setZipTimeleft(String s)
	{
		firePropertyChange("ZIP_TIMELEFT", this.zipInfo.strTimeleft, s);
		this.zipInfo.strTimeleft = s;
		return s;
	}

	public String setZipSizeDownloaded(String s)
	{
		firePropertyChange("ZIP_SIZEDOWNLOADED", this.zipInfo.strSizeDownloaded, s);
		this.zipInfo.strSizeDownloaded = s;
		return s;
	}

	public String setZipTotalSize(String s)
	{
		firePropertyChange("ZIP_TOTALSIZE", this.zipInfo.strTotalSize, s);
		this.zipInfo.strTotalSize = s;
		return s;
	}

	public String setMSISpeed(String s)
	{
		firePropertyChange("MSI_SPEED", this.msiInfo.strSpeed, s);
		this.msiInfo.strSpeed = s;
		return s;
	}

	public String setMSIPercent(String s)
	{
		firePropertyChange("MSI_PERCENT", this.msiInfo.strPercent, s);
		this.msiInfo.strPercent = s;
		return s;
	}

	public String setMSITimeleft(String s)
	{
		firePropertyChange("MSI_TIMELEFT", this.msiInfo.strTimeleft, s);
		this.msiInfo.strTimeleft = s;
		return s;
	}

	public String setMSISizeDownloaded(String s)
	{
		firePropertyChange("MSI_SIZEDOWNLOADED", this.msiInfo.strSizeDownloaded, s);
		this.msiInfo.strSizeDownloaded = s;
		return s;
	}

	public String setMSITotalSize(String s)
	{
		firePropertyChange("MSI_TOTALSIZE", this.msiInfo.strTotalSize, s);
		this.msiInfo.strTotalSize = s;
		return s;
	}

	public void setBounds(int x, int y, int width, int height)
	{
		super.setBounds(x, y, width, height);

		this.progBar.setBounds(0, 0, width, height);
	}

	public void downloadZip(final JButton button)
	{
		try
		{
			this.progBar.setStringPainted(true);
			Settings.instance().getClass();
			URL url = new URL("https://github.com/IVPR/Weave-Binaries/zipball/master");
			URLConnection conn = url.openConnection();
			final InputStream in = conn.getInputStream();
			this.zipInfo = new DownloadInfo();
			int updateAvailable = Revisions.checkForUpdates(false);
			String urlFileName = conn.getHeaderField("Content-Disposition");
			if (updateAvailable == -2)
			{
				this.progBar.setValue(0);
				this.progBar.setIndeterminate(false);
				this.progBar.setString("No Internet Connection");

				button.setEnabled(true);
				return;
			}
			if (updateAvailable == -1)
			{
				this.progBar.setValue(100);
				this.progBar.setIndeterminate(false);
				this.progBar.setString("Error Downloading");

				button.setEnabled(true);
				return;
			}
			if (updateAvailable == 0)
			{
				this.progBar.setValue(100);
				this.progBar.setIndeterminate(false);
				this.progBar.setString("No Updates");

				button.setEnabled(true);
				return;
			}
			int pos = urlFileName.indexOf("filename=");
			final String updateFileName = Settings.instance().ZIP_DIRECTORY.getPath() + "/"
					+ urlFileName.substring(pos + 9);
			final File updateFile = new File(updateFileName);
			System.out.println(updateFileName);
			if (!Settings.instance().SETTINGS_DIRECTORY.exists())
			{
				Settings.instance().SETTINGS_DIRECTORY.mkdirs();
			}
			if (!Settings.instance().ZIP_DIRECTORY.exists())
			{
				Settings.instance().ZIP_DIRECTORY.mkdirs();
			}
			updateFile.createNewFile();

			Thread t = new Thread(new Runnable()
			{
				int size;
				FileOutputStream out = new FileOutputStream(updateFile);
				byte[] b = new byte[1024];
				int count;
				int total;
				int kbps;
				int seconds;
				int aveDownSpeed;
				int timeleft;
				long newLong;
				long oldLong;

				public void run()
				{
					try
					{
						WeaveUpdater.updater.cancelButton.setEnabled(false);
						WeaveUpdater.updater.postSP.zipLabelSpeed.setVisible(true);
						WeaveUpdater.updater.postSP.zipLabelTimeleft.setVisible(true);

						String strSize = "";
						if (this.size > 1024)
						{
							if (this.size / 1024 > 1024)
							{
								strSize = Strings.format(
										"%.2f MB",
										new Object[] { Double.valueOf(((this.size + 0.0D) / 1024.0D + 0.0D) / 1024.0D) });
							}
							else
							{
								strSize = Strings.format(
										"%.0f KB", new Object[] { Double.valueOf((this.size + 0.0D) / 1024.0D) });
							}
						}
						else
						{
							strSize = Strings.format(
									"%.0f B", new Object[] { Double.valueOf((this.size + 0.0D) / 1.0D) });
						}
						ProgressUpdate.this.setZipTotalSize(strSize);

						ProgressUpdate.this.progBar.setIndeterminate(true);
						ProgressUpdate.this.progBar.setString("Preparing Download...");
						Thread.sleep(2000L);
						ProgressUpdate.this.progBar.setIndeterminate(false);
						while ((this.count = in.read(this.b)) > 0)
						{
							this.out.write(this.b, 0, this.count);
							this.total += this.count;
							this.kbps += this.count / 1024;
							ProgressUpdate.this.zipInfo.percent = (this.total / (this.size + 0.0D) * 100.0D);
							this.newLong = System.currentTimeMillis();
							if (this.newLong - this.oldLong > 1000L)
							{
								ProgressUpdate.this.zipInfo.speed = this.kbps;
								this.kbps = 0;
								this.seconds += 1;
								this.oldLong = this.newLong;
								this.aveDownSpeed = (this.total / 1024 / this.seconds);
								if (this.total > 1024)
								{
									if (this.total / 1024 > 1024)
									{
										ProgressUpdate.this.setZipSizeDownloaded(Strings.format(
												"%.2f MB",
												new Object[] { Double.valueOf((this.total + 0.0D) / 1024.0D / 1024.0D) }));
									}
									else
									{
										ProgressUpdate.this.setZipSizeDownloaded(Strings.format(
												"%.0f KB",
												new Object[] { Double.valueOf((this.total + 0.0D) / 1024.0D) }));
									}
								}
								else
								{
									ProgressUpdate.this.setZipSizeDownloaded(Strings.format("%s B", this.total));
								}
							}
							try
							{
								this.timeleft = ((this.size - this.total) / this.aveDownSpeed / 1024);
							}
							catch (ArithmeticException e)
							{
								e.printStackTrace();
								this.timeleft = Integer.MAX_VALUE;
							}
							ProgressUpdate.this.progBar.setValue(Strings.parseInt(Strings.format(
									"%.0f", new Object[] { Double.valueOf(ProgressUpdate.this.zipInfo.percent) })));
							ProgressUpdate.this.progBar.setString(ProgressUpdate.this.setZipPercent(Strings.format(
									"%.0f", new Object[] { Double.valueOf(ProgressUpdate.this.zipInfo.percent) }))
									+ "%");
							if (ProgressUpdate.this.zipInfo.speed > 1024)
							{
								ProgressUpdate.this.setZipSpeed(Strings.format(
										"%.1f",
										new Object[] { Double.valueOf(ProgressUpdate.this.zipInfo.speed / 1024.0D) })
										+ " MB/s");
							}
							else
							{
								ProgressUpdate.this.setZipSpeed(Strings.format(
										"%d", new Object[] { Integer.valueOf(ProgressUpdate.this.zipInfo.speed) })
										+ " KB/s");
							}
							if (this.timeleft > 60)
							{
								int t = this.timeleft / 60;
								if (t == 1)
								{
									ProgressUpdate.this.setZipTimeleft(Strings.format(
											"%d minute remaining", new Object[] { Integer.valueOf(t) }));
								}
								else
								{
									ProgressUpdate.this.setZipTimeleft(Strings.format(
											"%d minutes remaining", new Object[] { Integer.valueOf(t) }));
								}
							}
							else
							{
								ProgressUpdate.this.setZipTimeleft(Strings.format(
										"%d second(s) remaining", new Object[] { Integer.valueOf(this.timeleft) }));
							}
						}
						this.out.flush();
						ProgressUpdate.this.progBar.setValue(100);
						ProgressUpdate.this.progBar.setIndeterminate(true);
						ProgressUpdate.this.progBar.setString("Download Finished");
						Thread.sleep(3000L);
						this.out.close();
						in.close();
						WeaveUpdater.updater.postSP.zipLabelSpeedHolder.setText("");
						WeaveUpdater.updater.postSP.zipLabelTimeleftHolder.setText("");
						WeaveUpdater.updater.postSP.zipLabelSizeDownloadHolder.setText("");
						WeaveUpdater.updater.postSP.zipLabelSpeed.setVisible(false);
						WeaveUpdater.updater.postSP.zipLabelTimeleft.setVisible(false);

						Revisions.extractZip(updateFileName, ProgressUpdate.this.progBar, button);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			});
			t.start();
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}

	public int downloadMSI(final JPanel currentPanel, final JButton button, final Settings.MSI_TYPE downloadMSI)
	{
		final JButton cancelInstall = new JButton("Cancel");
		cancelInstall.setBounds(5, 115, 100, 20);
		cancelInstall.setVisible(false);
		cancelInstall.setEnabled(false);
		currentPanel.add(cancelInstall);
		try
		{
			this.progBar.setStringPainted(true);
			if (!Settings.instance().isConnectedToInternet().booleanValue())
			{
				this.progBar.setValue(0);
				this.progBar.setIndeterminate(false);
				this.progBar.setString("No Internet Connection");
				button.setEnabled(true);
				WeaveUpdater.updater.nextButton.setEnabled(true);
				WeaveUpdater.updater.backButton.setEnabled(true);
				WeaveUpdater.updater.cancelButton.setEnabled(true);
				if (downloadMSI == Settings.MSI_TYPE.TOMCAT_MSI)
				{
					WeaveUpdater.updater.curSP.installTomcat.setVisible(true);
					if (Settings.instance().TOMCAT_FILE.exists())
					{
						WeaveUpdater.updater.curSP.installTomcat.setEnabled(true);
					}
				}
				else if (downloadMSI == Settings.MSI_TYPE.MySQL_MSI)
				{
					if (Settings.instance().MySQL_FILE.exists())
					{
						WeaveUpdater.updater.curSP.installMySQL.setEnabled(true);
					}
					WeaveUpdater.updater.curSP.installMySQL.setVisible(true);
				}
				currentPanel.remove(cancelInstall);
				return -1;
			}
			URL url;
			if (downloadMSI == Settings.MSI_TYPE.TOMCAT_MSI)
			{
				while (Settings.instance().BEST_TOMCAT == null)
				{
				}
				url = new URL(Settings.instance().BEST_TOMCAT);
			}
			else
			{
				if (downloadMSI == Settings.MSI_TYPE.MySQL_MSI)
				{
					while (Settings.instance().BEST_MYSQL == null)
					{
					}
					url = new URL(Settings.instance().BEST_MYSQL);
				}
				else
				{
					return -1;
				}
			}
			URLConnection conn = url.openConnection();
			final InputStream in = conn.getInputStream();
			this.msiInfo = new DownloadInfo();
			String updateFileName;
			if (downloadMSI == Settings.MSI_TYPE.TOMCAT_MSI)
			{
				updateFileName = Settings.instance().TOMCAT_FILE.getPath();
			}
			else
			{
				if (downloadMSI == Settings.MSI_TYPE.MySQL_MSI)
				{
					updateFileName = Settings.instance().MySQL_FILE.getPath();
				}
				else
				{
					return -1;
				}
			}
			final File updateFile = new File(updateFileName);
			System.out.println(updateFileName);
			if (!Settings.instance().SETTINGS_DIRECTORY.exists())
			{
				Settings.instance().SETTINGS_DIRECTORY.mkdirs();
			}
			if (!Settings.instance().EXE_DIRECTORY.exists())
			{
				Settings.instance().EXE_DIRECTORY.mkdirs();
			}
			cancelInstall.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0)
				{
					cancelInstall.setEnabled(false);
					cancelInstall.setVisible(false);
					try
					{
						ProgressUpdate.this.msiInfo.cancelFlag = 1;
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			});
			updateFile.createNewFile();

			Thread t = new Thread(new Runnable()
			{
				int size;
				FileOutputStream out = new FileOutputStream(updateFile);
				byte[] b = new byte[1024];
				int count;
				int total;
				int kbps;
				int seconds;
				int aveDownSpeed;
				int timeleft;
				long newLong;
				long oldLong;

				public void run()
				{
					try
					{
						ProgressUpdate.this.progBar.setIndeterminate(true);
						ProgressUpdate.this.progBar.setString("Preparing Download...");
						Thread.sleep(2000L);
						ProgressUpdate.this.progBar.setIndeterminate(false);
						cancelInstall.setVisible(true);
						cancelInstall.setEnabled(true);
						ProgressUpdate.this.msiInfo.cancelFlag = 0;

						String strSize = "";
						if (this.size > 1024)
						{
							if (this.size / 1024 > 1024)
							{
								strSize = Strings.format(
										"%.2f MB",
										new Object[] { Double.valueOf(((this.size + 0.0D) / 1024.0D + 0.0D) / 1024.0D) });
							}
							else
							{
								strSize = Strings.format(
										"%.0f KB", new Object[] { Double.valueOf((this.size + 0.0D) / 1024.0D) });
							}
						}
						else
						{
							strSize = Strings.format("%.0f B", new Object[] { Integer.valueOf(this.size) });
						}
						ProgressUpdate.this.setMSITotalSize(strSize);
						while ((this.count = in.read(this.b)) > 0)
						{
							if (ProgressUpdate.this.msiInfo.cancelFlag == 1)
							{
								break;
							}
							this.out.write(this.b, 0, this.count);
							this.total += this.count;
							this.kbps += this.count / 1024;
							ProgressUpdate.this.msiInfo.percent = (this.total / (this.size + 0.0D) * 100.0D);
							this.newLong = System.currentTimeMillis();
							if (this.newLong - this.oldLong > 1000L)
							{
								ProgressUpdate.this.msiInfo.speed = this.kbps;

								this.kbps = 0;
								this.seconds += 1;
								this.oldLong = this.newLong;
								this.aveDownSpeed = (this.total / 1024 / this.seconds);
								if (this.total > 1024)
								{
									if (this.total / 1024 > 1024)
									{
										ProgressUpdate.this.setMSISizeDownloaded(Strings.format(
												"%.2f MB",
												new Object[] { Double.valueOf((this.total + 0.0D) / 1024.0D / 1024.0D) }));
									}
									else
									{
										ProgressUpdate.this.setMSISizeDownloaded(Strings.format(
												"%.0f KB",
												new Object[] { Double.valueOf((this.total + 0.0D) / 1024.0D) }));
									}
								}
								else
								{
									ProgressUpdate.this.setMSISizeDownloaded(Strings.format(
											"%.0f B", new Object[] { Integer.valueOf(this.total) }));
								}
							}
							try
							{
								this.timeleft = ((this.size - this.total) / this.aveDownSpeed / 1024);
							}
							catch (ArithmeticException e)
							{
								e.printStackTrace();
								this.timeleft = Integer.MAX_VALUE;
							}
							ProgressUpdate.this.progBar.setValue(Strings.parseInt(Strings.format(
									"%.0f", new Object[] { Double.valueOf(ProgressUpdate.this.msiInfo.percent) })));
							ProgressUpdate.this.progBar.setString(ProgressUpdate.this.setMSIPercent(Strings.format(
									"%.0f", new Object[] { Double.valueOf(ProgressUpdate.this.msiInfo.percent) }))
									+ "%");
							if (ProgressUpdate.this.msiInfo.speed > 1024)
							{
								ProgressUpdate.this.setMSISpeed(Strings.format(
										"%.1f",
										new Object[] { Double.valueOf(ProgressUpdate.this.msiInfo.speed / 1024.0D) })
										+ " MB/s");
							}
							else
							{
								ProgressUpdate.this.setMSISpeed(Strings.format(
										"%d", new Object[] { Integer.valueOf(ProgressUpdate.this.msiInfo.speed) })
										+ " KB/s");
							}
							if (this.timeleft > 60)
							{
								int t = this.timeleft / 60;
								if (t == 1)
								{
									ProgressUpdate.this.setMSITimeleft(Strings.format(
											"%d minute", new Object[] { Integer.valueOf(t) }));
								}
								else
								{
									ProgressUpdate.this.setMSITimeleft(Strings.format(
											"%d minutes", new Object[] { Integer.valueOf(t) }));
								}
							}
							else
							{
								ProgressUpdate.this.setMSITimeleft(Strings.format(
										"%d second(s)", new Object[] { Integer.valueOf(this.timeleft) }));
							}
						}
						if (ProgressUpdate.this.msiInfo.cancelFlag == 0)
						{
							this.out.flush();
							ProgressUpdate.this.progBar.setValue(100);
							ProgressUpdate.this.progBar.setIndeterminate(true);
							cancelInstall.setEnabled(false);
							cancelInstall.setVisible(false);
							Thread.sleep(1500L);
							this.out.close();
							in.close();
							ProgressUpdate.this.progBar.setString("Download Finished");
							ProgressUpdate.this.progBar.setValue(100);
							ProgressUpdate.this.progBar.setIndeterminate(false);
							if (downloadMSI == Settings.MSI_TYPE.TOMCAT_MSI)
							{
								WeaveUpdater.updater.curSP.installTomcat.setEnabled(true);
								WeaveUpdater.updater.curSP.installTomcat.setVisible(true);
							}
							else if (downloadMSI == Settings.MSI_TYPE.MySQL_MSI)
							{
								WeaveUpdater.updater.curSP.installMySQL.setEnabled(true);
								WeaveUpdater.updater.curSP.installMySQL.setVisible(true);
							}
						}
						else
						{
							this.out.flush();
							ProgressUpdate.this.progBar.setValue(0);
							ProgressUpdate.this.progBar.setIndeterminate(true);
							ProgressUpdate.this.progBar.setString("Removing Local Files...");
							Thread.sleep(1500L);
							this.out.close();
							in.close();
							Revisions.recursiveDelete(updateFile);
							Thread.sleep(1000L);
							ProgressUpdate.this.progBar.setIndeterminate(false);
							ProgressUpdate.this.progBar.setString("Download Cancelled");
							if (downloadMSI == Settings.MSI_TYPE.TOMCAT_MSI)
							{
								WeaveUpdater.updater.curSP.installTomcat.setEnabled(false);
								WeaveUpdater.updater.curSP.installTomcat.setVisible(true);
							}
							else if (downloadMSI == Settings.MSI_TYPE.MySQL_MSI)
							{
								WeaveUpdater.updater.curSP.installMySQL.setEnabled(false);
								WeaveUpdater.updater.curSP.installMySQL.setVisible(true);
							}
						}
						if (downloadMSI == Settings.MSI_TYPE.TOMCAT_MSI)
						{
							WeaveUpdater.updater.curSP.tomcatLabelSizeDownloadHolder.setText("");
							WeaveUpdater.updater.curSP.tomcatLabelSpeedHolder.setText("");
							WeaveUpdater.updater.curSP.tomcatLabelTimeleftHolder.setText("");
						}
						else if (downloadMSI == Settings.MSI_TYPE.MySQL_MSI)
						{
							WeaveUpdater.updater.curSP.mysqlLabelSizeDownloadHolder.setText("");
							WeaveUpdater.updater.curSP.mysqlLabelSpeedHolder.setText("");
							WeaveUpdater.updater.curSP.mysqlLabelTimeleftHolder.setText("");
						}
						button.setEnabled(true);
						WeaveUpdater.updater.nextButton.setEnabled(true);
						WeaveUpdater.updater.backButton.setEnabled(true);
						WeaveUpdater.updater.cancelButton.setEnabled(true);
						currentPanel.remove(cancelInstall);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			});
			t.start();
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		return 0;
	}

	public int runExecutable(File file)
	{
		if (file.exists())
		{
			try
			{
				Desktop.getDesktop().open(file);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return 0;
		}
		return -1;
	}

	public static class DownloadInfo
	{
		int speed = 0;
		double percent = 0.0D;
		String strSpeed = "";
		String strPercent = "";
		String strTimeleft = "";
		String strSizeDownloaded = "";
		String strTotalSize = "";
		int cancelFlag = 0;
	}
}
