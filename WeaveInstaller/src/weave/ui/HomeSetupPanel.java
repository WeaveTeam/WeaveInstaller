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
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
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
import weave.configs.IConfig;
import weave.inc.SetupPanel;
import weave.includes.IUtilsInfo;
import weave.managers.ConfigManager;
import weave.utils.BugReportUtils;
import weave.utils.DownloadUtils;
import weave.utils.FileUtils;
import weave.utils.RemoteUtils;
import weave.utils.TimeUtils;
import weave.utils.TraceUtils;
import weave.utils.UpdateUtils;
import weave.utils.ZipUtils;

@SuppressWarnings("serial")
public class HomeSetupPanel extends SetupPanel
{
	private static final String _TMP_FOLDERNAME_ = "weave";
	private static final String _TMP_FILENAME_   = _TMP_FOLDERNAME_ + ".zip";
	
	private boolean refreshProgramatically = false;
	public JTabbedPane tabbedPane;
	public JPanel tab1, tab2, tab3, tab4;

	
	// ============== Tab 1 ============== //
	public JButton  installButton, refreshButton, 
					revertButton, deleteButton, 
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
										Desktop.getDesktop().browse(e.getURL().toURI());
									} catch (IOException ex) {
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
					TraceUtils.traceln(TraceUtils.STDOUT, "-> Checking for new Weave Binaries....");

					Settings.canQuit = false;
					setButtonsEnabled(false);
					
					int updateAvailable = UpdateUtils.isWeaveUpdateAvailable(!refreshProgramatically);
					weaveStats.refresh(updateAvailable);
					refreshProgramatically = false;

					downloadLabel.setText("");
					progressbar.setIndeterminate(true);
					progressbar.setString("");
					progressbar.setValue(0);
					setButtonsEnabled(true);
					installButton.setEnabled(updateAvailable == UpdateUtils.UPDATE_AVAILABLE);
					pruneButton.setEnabled(Revisions.getNumberOfRevisions() > Settings.recommendPrune);
					revisionTable.updateTableData();
					
					Settings.canQuit = true;
					
				} catch (InterruptedException e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
				}
			}
		});
		
		
		installButton = new JButton("Install");
		installButton.setBounds(250, 40, 80, 25);
		installButton.setToolTipText("Download the latest version of "+ Settings.PROJECT_NAME +" and install it.");
		installButton.setEnabled(false);
		installButton.addActionListener(new ActionListener() {
			class Internal
			{
				public int status;
			}
			
			@Override
			public void actionPerformed(ActionEvent a)
			{
				try {
					final Internal internal = new Internal();
					internal.status = DownloadUtils.FAILED;
					
					Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							internal.status = downloadBinaries();
						}
					});
					
					setButtonsEnabled(false);
					progressbar.setIndeterminate(true);
					downloadLabel.setText("Preparing Download....");
					Thread.sleep(1000);
					t.start();
					t.join();
					
					switch (internal.status) {
						case DownloadUtils.COMPLETE:
//							installBinaries(new File(Settings.DOWNLOADS_TMP_DIRECTORY, _TMP_FILENAME_));
							break;
						
						case DownloadUtils.CANCELLED:
							break;
							
						case DownloadUtils.FAILED:
							break;
	
						case DownloadUtils.OFFLINE:
							JOptionPane.showConfirmDialog(null, 
									"A connection to the internet could not be established.\n\n" +
									"Please connect to the internet and try again.", 
									"No Connection", 
									JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
							break;
					}
					
				} catch (InterruptedException e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
				}
			}
		});
		
		revertButton = new JButton("Revert");
		revertButton.setBounds(250, 125, 80, 25);
		revertButton.setToolTipText("Install Weave from a backup revision, selected on the left in the table.");
		revertButton.setVisible(true);
		revertButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent a) 
			{
				int index = revisionTable.getTable().getSelectedRow();
				if( index < 0 )
					return;
				
				installBinaries(Revisions.getRevisionsList().get(index));
				revisionTable.updateTableData();
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
				revisionTable.updateTableData();
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
				pruneButton.setEnabled(Revisions.getNumberOfRevisions() > Settings.recommendPrune);
			}
		});
		
		
		adminButton = new JButton("Launch Admin Console");
		adminButton.setBounds(10, 235, 230, 25);
		adminButton.setToolTipText("Open up the Admin Console");
		adminButton.setVisible(true);
		adminButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent a) 
			{
				IConfig containerConfig = ConfigManager.getConfigManager().getContainer();
				
				if( containerConfig != null )
				{
					try {
						Desktop.getDesktop().browse(new URI(
								"http://" + Settings.LOCALHOST + ":" + 
								containerConfig.getPort() + "/" + 
								"AdminConsole.html"));
					} catch (IOException e) {
						TraceUtils.trace(TraceUtils.STDERR, e);
					} catch (URISyntaxException e) {
						TraceUtils.trace(TraceUtils.STDERR, e);
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
		panel.add(revertButton);
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
	
	private int downloadBinaries()
	{
		int status = DownloadUtils.FAILED;
		String url = RemoteUtils.getConfigEntry(RemoteUtils.WEAVE_BINARIES_URL);
		File destination = new File(Settings.DOWNLOADS_TMP_DIRECTORY, _TMP_FILENAME_);
		
		if( url == null )
			return DownloadUtils.OFFLINE;
		
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
										TimeUtils.format("%m:%s remaining", info.timeleft),
										FileUtils.sizeify(info.cur),
										DownloadUtils.speedify(info.speed)) );
						}
					}
				});
			}
		};
		
		try {
			// Error checking
			if( !Settings.DOWNLOADS_TMP_DIRECTORY.exists() )
				Settings.DOWNLOADS_TMP_DIRECTORY.mkdirs();
			if( destination.exists() )
				destination.delete();
			destination.createNewFile();

			TraceUtils.trace(TraceUtils.STDOUT, "-> Downloading update.............");
			
			downloadLabel.setVisible(true);
			progressbar.setVisible(true);
			
			downloadLabel.setText("Downloading update.....");
			progressbar.setIndeterminate(true);

			Settings.downloadLocked = true;
			Settings.downloadCanceled = false;
			
			DownloadUtils du = new DownloadUtils();
			du.addStatusListener(null, downloadInfo);
			status = du.downloadWithInfo(url, destination, 100 * DownloadUtils.KB);

			Settings.downloadCanceled = false;
			Settings.downloadLocked = false;
			
			switch (status) {
				case DownloadUtils.FAILED:
					TraceUtils.put(TraceUtils.STDOUT, "FAILED");
					downloadLabel.setText("Download Failed....");
					downloadLabel.setForeground(Color.RED);
					break;
					
				case DownloadUtils.CANCELLED:
					TraceUtils.put(TraceUtils.STDOUT, "CANCELLED");
					downloadLabel.setText("Cancelling Download....");
					downloadLabel.setForeground(Color.BLACK);
					break;

				case DownloadUtils.COMPLETE:
					TraceUtils.put(TraceUtils.STDOUT, "DONE");
					downloadLabel.setText("Donwload Complete....");
					downloadLabel.setForeground(Color.BLACK);
					break;
			}
			
			du.removeStatusListener();
			System.gc();
			Thread.sleep(2000);
			
		} catch (IOException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
		} catch (InterruptedException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
		}
		return status;
	}
	private void installBinaries(File zipFile)
	{
		File unzippedFile = new File(Settings.UNZIP_DIRECTORY, _TMP_FOLDERNAME_);
		File configWEBAPPS = ConfigManager.getConfigManager().getContainer().getWebappsDirectory();
		
		TraceUtils.trace(TraceUtils.STDOUT, "-> Installing update..............");
		
		try {
			
			IUtilsInfo zipListener = new IUtilsInfo() {
				@Override
				public void onProgressUpdate() {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							progressbar.setValue( info.progress / 2 );
							downloadLabel.setText( String.format("Extracting update....%d%%", info.progress / 2 ) );
						}
					});
				}
			};
			IUtilsInfo fileListener = new IUtilsInfo() {
				@Override
				public void onProgressUpdate() {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							progressbar.setValue(50 + info.progress / 2 );
							downloadLabel.setText( String.format("Installing update....%d%%", 50 + info.progress / 2 ));
						}
					});
				}
			};

			Settings.canQuit = false;
			
			downloadLabel.setText("Extracting update....");
			progressbar.setIndeterminate(false);
			Thread.sleep(500);
			
			progressbar.setValue(0);
			downloadLabel.setText("Extracting update....0%" );
			Thread.sleep(800);
			
			ZipUtils zu = new ZipUtils();
			zu.addStatusListener(null, zipListener, zipFile);
			zu.extractZipWithInfo(zipFile, unzippedFile);
			
			progressbar.setValue( 50 );
			downloadLabel.setText( "Installing update....50%" );
			Thread.sleep(800);
			
			FileUtils fu = new FileUtils();
			fu.addStatusListener(null, fileListener, unzippedFile, FileUtils.OVERWRITE | FileUtils.OPTION_MULTIPLE_FILES);
			fu.copyWithInfo(unzippedFile, configWEBAPPS, FileUtils.OVERWRITE | FileUtils.OPTION_MULTIPLE_FILES);
			
			progressbar.setValue( 100 );
			downloadLabel.setText( "Installing update....100%" );
			Thread.sleep(800);
			
			System.gc();
			downloadLabel.setText("Install complete....");
			TraceUtils.put(TraceUtils.STDOUT, "DONE");
			
			Settings.canQuit = true;
			
			zu.removeStatusListener();
			fu.removeStatusListener();
			
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			TraceUtils.put(TraceUtils.STDOUT, "FAILED");
			TraceUtils.trace(TraceUtils.STDERR, e);
		} catch (IOException e) {
			TraceUtils.put(TraceUtils.STDOUT, "FAILED");
			TraceUtils.trace(TraceUtils.STDERR, e);
		}
	}

	private void setButtonsEnabled(boolean enabled)
	{
		refreshButton.setEnabled(enabled);
		installButton.setEnabled(enabled);
		revertButton.setEnabled(enabled);
		deleteButton.setEnabled(enabled);
		pruneButton.setEnabled(enabled);
	}
}
