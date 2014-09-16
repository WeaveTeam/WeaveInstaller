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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import weave.Revisions;
import weave.Settings;
import weave.async.IAsyncCallback;
import weave.async.IAsyncCallbackResult;
import weave.configs.IConfig;
import weave.inc.SetupPanel;
import weave.includes.IUtilsInfo;
import weave.managers.ConfigManager;
import weave.utils.BugReportUtils;
import weave.utils.DownloadUtils;
import weave.utils.FileUtils;
import weave.utils.LaunchUtils;
import weave.utils.RemoteUtils;
import weave.utils.TimeUtils;
import weave.utils.TraceUtils;
import weave.utils.UpdateUtils;
import weave.utils.ZipUtils;

@SuppressWarnings("serial")
public class HomeSetupPanel extends SetupPanel
{
	private boolean refreshProgramatically = false;
	public JTabbedPane tabbedPane;
	public JPanel tab1, tab2, tab3, tab4;

	
	// ============== Tab 1 ============== //
	public JButton  installButton, refreshButton, 
					deployButton, deleteButton, 
					pruneButton, adminButton;
	public JLabel	downloadLabel;
	public JProgressBar progressbar;
	public WeaveStats weaveStats;
	public RevisionTable revisionTable;
	
	
	// ============== Tab 2 ============== //
	
	
	// ============== Tab 3 ============== //
	
	
	// ============== Tab 4 ============== //
	public String faqURL = "http://ivpr.oicweave.org/faq.php?" + Calendar.getInstance().getTimeInMillis();
	public JEditorPane troubleshootHTML;
	public JScrollPane troubleshootScrollPane;
	
	
	public HomeSetupPanel()
	{
		maxPanels = 1;
		
		setLayout(null);
		setBounds(0, 0, 350, 325);

		JPanel panel = null;
		for (int i = 0; i < maxPanels; i++) {
			switch (i) {
				case 0: panel = createHomeSetupPanel(); 	break;
			}
			panels.add(panel);
			add(panel);
		}
		hidePanels();
		
		setVisible(true);

		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				refreshProgramatically = true;
				refreshButton.doClick();
			}
		}, 1000);
	}

	public JPanel createHomeSetupPanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBounds(0, 0, 350, 325);
		panel.setBackground(new Color(0xFFFFFF));
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPane.setBounds(0, 0, 350, 325);

		tabbedPane.addTab("Weave", (tab1 = createTab1(tabbedPane)));
		tabbedPane.addTab("Plugins", (tab2 = createTab2(tabbedPane)));
		tabbedPane.addTab("Settings", (tab3 = createTab3(tabbedPane)));
		tabbedPane.addTab("Troubleshoot", (tab4 = createTab4(tabbedPane)));
		tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent event)
			{
				JPanel selectedPanel = (JPanel) tabbedPane.getSelectedComponent();
				if( selectedPanel == tab4 )
				{
					try {
						faqURL = "http://ivpr.oicweave.org/faq.php?" + Calendar.getInstance().getTimeInMillis();
//						System.out.println("page updated to " + faqURL);
						troubleshootHTML.setPage(faqURL);
						
						// Remove all link listeners
						for( HyperlinkListener h : troubleshootHTML.getHyperlinkListeners() )
							troubleshootHTML.removeHyperlinkListener(h);
						// Add new link listener
						troubleshootHTML.addHyperlinkListener(new HyperlinkListener() {
							@Override
							public void hyperlinkUpdate(HyperlinkEvent e) {
								if( e.getEventType() == HyperlinkEvent.EventType.ACTIVATED )
								{
									try {
										LaunchUtils.browse(e.getURL().toURI());
									} catch (IOException ex) {
										TraceUtils.trace(TraceUtils.STDERR, ex);
										BugReportUtils.showBugReportDialog(ex);
									} catch (InterruptedException ex) {
										TraceUtils.trace(TraceUtils.STDERR, ex);
										BugReportUtils.showBugReportDialog(ex);
									} catch (URISyntaxException ex) {
										TraceUtils.trace(TraceUtils.STDERR, ex);
										BugReportUtils.showBugReportDialog(ex);
									}
								}
							}
						});
					} catch (IOException e) {
						TraceUtils.trace(TraceUtils.STDERR, e);
						BugReportUtils.showBugReportDialog(e);
					}
				}
			}
		});
		panel.add(tabbedPane);
		
		tabbedPane.setSelectedComponent(tab1);
		
		return panel;
	}
	
	public JPanel createTab(JComponent parent)
	{
		JPanel panel = new JPanel(null);
		panel.setBounds(0, 0, parent.getWidth(), parent.getHeight());
		panel.setBackground(Color.WHITE);
		
		return panel;
	}
	
	public JPanel createTab1(JComponent parent)
	{
		JPanel panel = createTab(parent);

		refreshButton = new JButton("Refresh");
		refreshButton.setBounds(250, 10, 80, 25);
		refreshButton.setToolTipText("Check for a new version of " + Settings.PROJECT_NAME);
		refreshButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent a) 
			{
				try {
					refreshInterface();
				} catch (InterruptedException e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
				} catch (MalformedURLException e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
				}
			}
		});
		
		
		installButton = new JButton("Install");
		installButton.setBounds(250, 40, 80, 25);
		installButton.setToolTipText("Download the latest version of "+ Settings.PROJECT_NAME +" and install it.");
		installButton.setEnabled(false);
		installButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent a)
			{
				try {
					setButtonsEnabled(false);
					downloadBinaries();
				} catch (InterruptedException e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
				} catch (MalformedURLException e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
				}
			}
		});
		
		deployButton = new JButton("Deploy");
		deployButton.setBounds(250, 125, 80, 25);
		deployButton.setToolTipText("Install Weave from a backup revision, selected on the left in the table.");
		deployButton.setVisible(true);
		deployButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent a) 
			{
				int index = revisionTable.getTable().getSelectedRow();
				if( index < 0 )
					return;
				
				installBinaries(Revisions.getRevisionsList().get(index));
			}
		});
		
		
		deleteButton = new JButton("Delete");
		deleteButton.setBounds(250, 155, 80, 25);
		deleteButton.setToolTipText("Delete an individual revision, selected on the left in the table.");
		deleteButton.setVisible(true);
		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent a) 
			{
				int index = revisionTable.getTable().getSelectedRow();
				if( index < 0 )
					return;
				
				if( JOptionPane.showConfirmDialog(
						null, 
						"Deleting revisions cannot be undone.\n\nAre you sure you want to continue?", 
						"Warning", 
						JOptionPane.YES_NO_OPTION, 
						JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION )
					return;
				
				File selectedFile = Revisions.getRevisionsList().get(index);
				FileUtils.recursiveDelete(selectedFile);

				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						refreshProgramatically = true;
						try {
							refreshInterface();
						} catch (InterruptedException e) {
							TraceUtils.trace(TraceUtils.STDERR, e);
						} catch (MalformedURLException e) {
							TraceUtils.trace(TraceUtils.STDERR, e);
						}
					}
				}, 1000);
			}
		});
		
		
		pruneButton = new JButton("Clean");
		pruneButton.setBounds(250, 185, 80, 25);
		pruneButton.setToolTipText("Auto-delete older revisions to free up space on your hard drive.");
		pruneButton.setVisible(true);
		pruneButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if( JOptionPane.showConfirmDialog(
						null, 
						"Auto-cleaned revisions will be deleted\nand cannot be undone.\n\nAre you sure you want to continue?",
						"Warning",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE) == JOptionPane.NO_OPTION )
					return;
				
				Revisions.pruneRevisions();
				
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						refreshProgramatically = true;
						try {
							refreshInterface();
						} catch (InterruptedException e) {
							TraceUtils.trace(TraceUtils.STDERR, e);
						} catch (MalformedURLException e) {
							TraceUtils.trace(TraceUtils.STDERR, e);
						}
					}
				}, 1000);
			}
		});
		
		
		adminButton = new JButton("Launch Admin Console");
		adminButton.setBounds(10, 265, 230, 25);
		adminButton.setToolTipText("Open up the Admin Console");
		adminButton.setVisible(true);
		adminButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent a) 
			{
				IConfig activeContainer = ConfigManager.getConfigManager().getActiveContainer();
				
				if( activeContainer != null )
				{
					try {
						LaunchUtils.browse("http://" + Settings.LOCALHOST + ":" + activeContainer.getPort() + "/AdminConsole.html");
					} catch (IOException ex) {
						TraceUtils.trace(TraceUtils.STDERR, ex);
					} catch (URISyntaxException ex) {
						TraceUtils.trace(TraceUtils.STDERR, ex);
					} catch (InterruptedException ex) {
						TraceUtils.trace(TraceUtils.STDERR, ex);
					}
				} else
					JOptionPane.showMessageDialog(null, "No servlet container loaded.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
		
		
		progressbar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
		progressbar.setBounds(10, 75, 320, 15);
		progressbar.setIndeterminate(true);
		progressbar.setVisible(false);
		
		downloadLabel = new JLabel();
		downloadLabel.setBounds(10, 90, 320, 25);
		downloadLabel.setText("");
		downloadLabel.setVisible(false);
		
		weaveStats = new WeaveStats();
		weaveStats.setBounds(10, 10, 230, 55);
		weaveStats.setVisible(true);
		
		
		revisionTable = new RevisionTable();
		revisionTable.setBounds(10, 125, 230, 130);
		revisionTable.setVisible(true);
		
		
		panel.add(weaveStats);
		panel.add(revisionTable);
		panel.add(progressbar);
		panel.add(downloadLabel);
		panel.add(refreshButton);
		panel.add(installButton);
		panel.add(deployButton);
		panel.add(deleteButton);
		panel.add(pruneButton);
		panel.add(adminButton);
		
		return panel;
	}
	public JPanel createTab2(JComponent parent)
	{
		JPanel panel = createTab(parent);
		return panel;
	}
	public JPanel createTab3(JComponent parent)
	{
		JPanel panel = createTab(parent);
		return panel;
	}
	public JPanel createTab4(JComponent parent)
	{
		JPanel panel = createTab(parent);

		try {
			troubleshootHTML = new JEditorPane();
			troubleshootHTML.setPage(faqURL);
			troubleshootHTML.setEditable(false);
			troubleshootHTML.setVisible(true);
			
			troubleshootScrollPane = new JScrollPane(troubleshootHTML, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			troubleshootScrollPane.setBounds(0, 0, parent.getWidth() - 10, parent.getHeight() - 30);
			troubleshootScrollPane.setVisible(true);
		} catch (IOException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		}

		panel.add(troubleshootScrollPane);
		
		return panel;
	}
	
	private void downloadBinaries() throws InterruptedException, MalformedURLException
	{
		String url = RemoteUtils.getConfigEntry(RemoteUtils.WEAVE_BINARIES_URL);
		if( url == null ) {
			JOptionPane.showConfirmDialog(null, 
					"A connection to the internet could not be established.\n\n" +
					"Please connect to the internet and try again.", 
					"No Connection", 
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
			setButtonsEnabled(true);
			pruneButton.setEnabled(Revisions.getNumberOfRevisions() > Settings.recommendPrune);
			return;
		}

		IConfig actvContainer = ConfigManager.getConfigManager().getActiveContainer();
		if( actvContainer == null )
		{
			JOptionPane.showMessageDialog(null, 
					"There is no active servlet selected.\n\n" + 
					"Please configure a servlet to use, then try again.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		File cfgWebapps = actvContainer.getWebappsDirectory();
		if( cfgWebapps == null || !cfgWebapps.exists() )
		{
			JOptionPane.showMessageDialog(null, 
					"Webapps folder is not set.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		String fileName = UpdateUtils.getWeaveUpdateFileName();
		final File zipFile = new File(Settings.REVISIONS_DIRECTORY, fileName);
		
		final DownloadUtils du = new DownloadUtils();
		IUtilsInfo downloadInfo = new IUtilsInfo() {
			@Override
			public void onProgressUpdate() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						if( info.max == -1 ) {
							// Unknown max size - progress unavailable
							progressbar.setIndeterminate(true);
							downloadLabel.setText( 
									String.format("Downloading update....%s @ %s",
										FileUtils.sizeify(info.cur), 
										DownloadUtils.speedify(info.speed)) );
						} else {
							// Known max size
							progressbar.setIndeterminate(false);
							progressbar.setValue( info.progress );
							if( info.timeleft > 3600 )
								downloadLabel.setText(
										String.format("Downloading - %d%% - %s - %s (%s)", 
											info.progress, 
											"Calculating ETA...",
											FileUtils.sizeify(info.cur),
											DownloadUtils.speedify(info.speed)) );
							else if( info.timeleft < 60 )
								downloadLabel.setText(
										String.format("Downloading - %d%% - %s - %s (%s)", 
											info.progress, 
											TimeUtils.format("%s s remaining", info.timeleft),
											FileUtils.sizeify(info.cur),
											DownloadUtils.speedify(info.speed)) );
							else
								downloadLabel.setText(
										String.format("Downloading - %d%% - %s - %s (%s)",
											info.progress, 
											TimeUtils.format("%m:%ss remaining", info.timeleft),
											FileUtils.sizeify(info.cur),
											DownloadUtils.speedify(info.speed)) );
						}
					}
				});
			}
		};
		IAsyncCallback downloadCallback = new IAsyncCallback() {
			@Override
			public void run(Object o) {
				int status = ((IAsyncCallbackResult)o).getCode();
				
				Settings.downloadCanceled = false;
				Settings.downloadLocked = false;

				du.removeAllCallbacks();
				du.removeStatusListener();
				System.gc();
				
				if( status == DownloadUtils.COMPLETE )
				{
					TraceUtils.put(TraceUtils.STDOUT, "DONE");
					downloadLabel.setText("Download Complete....");
					downloadLabel.setForeground(Color.BLACK);

					installBinaries(zipFile);
				}
				else if( status == DownloadUtils.FAILED )
				{
					TraceUtils.put(TraceUtils.STDOUT, "FAILED");
					downloadLabel.setText("Download Failed....");
					downloadLabel.setForeground(Color.RED);
				}
				else if( status == DownloadUtils.CANCELLED )
				{
					TraceUtils.put(TraceUtils.STDOUT, "CANCELLED");
					downloadLabel.setText("Cancelling Download....");
					downloadLabel.setForeground(Color.BLACK);
				}
			}
		};
		
		try {
			// Error checking
			if( !Settings.DOWNLOADS_TMP_DIRECTORY.exists() )
				Settings.DOWNLOADS_TMP_DIRECTORY.mkdirs();
			if( zipFile.exists() )
				zipFile.delete();
			zipFile.createNewFile();

			TraceUtils.trace(TraceUtils.STDOUT, "-> Downloading update.............");
			
			downloadLabel.setVisible(true);
			progressbar.setVisible(true);
			
			downloadLabel.setText("Downloading update.....");
			progressbar.setIndeterminate(true);

			Settings.downloadLocked = true;
			Settings.downloadCanceled = false;
			
			du.addCallback(downloadCallback);
			du.addStatusListener(null, downloadInfo);
			du.downloadWithInfo(url, zipFile, 2 * DownloadUtils.MB);

		} catch (IOException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
		} catch (InterruptedException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
		}
	}
	
	private void installBinaries(final File zipFile)
	{
		if( !Settings.UNZIP_DIRECTORY.exists() )
			Settings.UNZIP_DIRECTORY.mkdirs();
		
		final File unzippedFile = new File(Settings.UNZIP_DIRECTORY, zipFile.getName());
		
		final ZipUtils zu = new ZipUtils();
		IUtilsInfo zipInfo = new IUtilsInfo() {
			@Override
			public void onProgressUpdate() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						progressbar.setValue( info.progress / 2 );
						downloadLabel.setText( 
								String.format(
										"Extracting update....%d%%", 
										info.progress / 2 ) );
					}
				});
			}
		};
		IAsyncCallback zipCallback = new IAsyncCallback() {
			@Override
			public void run(Object o) {

				zu.removeAllCallbacks();
				zu.removeStatusListener();

				moveBinaries(unzippedFile);
			}
		};
		
		try {
			setButtonsEnabled(false);
			progressbar.setIndeterminate(true);
			downloadLabel.setText("Preparing Install....");
			Thread.sleep(1000);
			
			TraceUtils.trace(TraceUtils.STDOUT, "-> Installing update..............");
			
			Settings.canQuit = false;
			
			downloadLabel.setText("Extracting update....");
			progressbar.setIndeterminate(false);
			
			zu.addCallback(zipCallback);
			zu.addStatusListener(null, zipInfo, zipFile);
			zu.extractZipWithInfo( zipFile, unzippedFile );
		} catch (InterruptedException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
		}
	}
	
	private void moveBinaries(final File unzippedFile)
	{
		File configWebapps = ConfigManager.getConfigManager().getActiveContainer().getWebappsDirectory();
		
		FileUtils fu = new FileUtils();
		IUtilsInfo fileInfo = new IUtilsInfo() {
			@Override
			public void onProgressUpdate() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						progressbar.setValue( 50 + info.progress / 2 );
						downloadLabel.setText( 
								String.format(
										"Installing update....%d%%", 
										50 + info.progress / 2 ) );
					}
				});
			}
		};
		IAsyncCallback fileCallback = new IAsyncCallback() {
			@Override
			public void run(Object o) {

				Settings.canQuit = true;
				System.gc();
				
				TraceUtils.put(TraceUtils.STDOUT, "DONE");
				downloadLabel.setText("Install complete....");
				
				Settings.CURRENT_INSTALL_VER = Revisions.getRevisionName(unzippedFile.getAbsolutePath());
				Settings.save();

				setButtonsEnabled(true);
				
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						downloadLabel.setVisible(false);
						progressbar.setVisible(false);
						
						refreshProgramatically = true;
						refreshButton.doClick();
					}
				}, 1000);
			}
		};
		
		try {
			fu.addCallback(fileCallback);
			fu.addStatusListener(null, fileInfo, unzippedFile, FileUtils.OPTION_MULTIPLE_FILES);
			fu.copyWithInfo(unzippedFile, configWebapps, FileUtils.OPTION_MULTIPLE_FILES);
		} catch (IOException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
		} catch (InterruptedException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
		}
	}
	private void refreshInterface() throws InterruptedException, MalformedURLException
	{
		TraceUtils.traceln(TraceUtils.STDOUT, "-> Checking for new Weave Binaries....");

		Settings.canQuit = false;
		setButtonsEnabled(false);
		
		int updateAvailable = UpdateUtils.isWeaveUpdateAvailable(!refreshProgramatically);
		weaveStats.refresh(updateAvailable);
		refreshProgramatically = false;

		Settings.canQuit = true;
		
		downloadLabel.setVisible(false);
		downloadLabel.setText("");
		progressbar.setVisible(false);
		progressbar.setIndeterminate(true);
		progressbar.setString("");
		progressbar.setValue(0);
		setButtonsEnabled(true);
		installButton.setEnabled(updateAvailable == UpdateUtils.UPDATE_AVAILABLE);
		pruneButton.setEnabled(Revisions.getNumberOfRevisions() > Settings.recommendPrune);
		revisionTable.updateTableData();
	}
	private void setButtonsEnabled(boolean enabled)
	{
		refreshButton.setEnabled(enabled);
		installButton.setEnabled(enabled);
		deployButton.setEnabled(enabled);
		deleteButton.setEnabled(enabled);
		pruneButton.setEnabled(enabled);
	}
}
