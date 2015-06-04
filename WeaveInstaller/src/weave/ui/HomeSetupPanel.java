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

package weave.ui;

import static weave.utils.TraceUtils.STDERR;
import static weave.utils.TraceUtils.STDOUT;
import static weave.utils.TraceUtils.trace;
import static weave.utils.TraceUtils.traceln;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.html.HTMLDocument;

import weave.Function;
import weave.Globals;
import weave.Settings;
import weave.Settings.INSTALL_ENUM;
import weave.async.AsyncTask;
import weave.configs.IConfig;
import weave.inc.SetupPanel;
import weave.managers.ConfigManager;
import weave.managers.DownloadManager;
import weave.managers.PluginManager;
import weave.plugins.IPlugin;
import weave.reflect.Reflectable;
import weave.utils.BugReportUtils;
import weave.utils.FileUtils;
import weave.utils.ImageUtils;
import weave.utils.LaunchUtils;
import weave.utils.ObjectUtils;
import weave.utils.ReflectionUtils;
import weave.utils.RemoteUtils;
import weave.utils.Revisions;
import weave.utils.StringUtils;
import weave.utils.TransferUtils;
import weave.utils.URLRequestUtils;
import weave.utils.UpdateUtils;

@SuppressWarnings("serial")
public class HomeSetupPanel extends SetupPanel 
{
	private boolean refreshProgramatically = false;
	public JTabbedPane tabbedPane;
	public JPanel weaveTab, pluginsTab, sessionsTab, tab4, troubleshootTab, aboutTab;

	// ============== Weave Tab ============== //
	public DropDownButton refreshButton;
	public JButton installButton, deployButton, deleteButton, cleanButton;
	public JLabel downloadLabel;
	public JProgressBar progressbar;
	public WeaveStats weaveStats;
	public CustomTable revisionTable;
	public SocketStatus localhostStatus, lanStatus, internetStatus;
	
	// ============== Sessions Tab ============== //
	public CustomTable sessionStateTable;
	public JLabel sessionLabel;
	public JTextArea dndHelp;
	public JButton launchSessionState, launchAdminConsoleButton;
	
	// ============== Plugins Tab ============== //
	public CustomTable pluginsTable;
	public JPanel pluginsPanel;
//	public JEditorPane pluginsPane;
//	public JScrollPane pluginsScrollPane;
//	public JButton pluginsInstallButton;
//	public JProgressBar pluginsProgressBar;
//	public JLabel pluginsProgressLabel;
	
	// ============== Settings Tab ============== //
	public JScrollPane settingsScrollPane;
	public TitledBorder settingsServerUpdatesTitle, settingsWeaveUpdatesTitle, settingsMaintenanceTitle, settingsProtoExtTitle;
	public JCheckBox settingsUpdatesAutoInstallCheckbox, settingsUpdatesCheckNewCheckbox;
	public JComboBox<String> settingsUpdatesCheckNewCombobox;
	public JCheckBox settingsMaintenanceDeleteLogsCheckbox, settingsMaintenanceDebugCheckbox;
	public JTextField settingsMaintenanceDeleteLogsTextfield;
	public JCheckBox settingsExtCheckbox, settingsProtocolCheckbox;
	
	
	// ============== Troubleshoot Tab ============== //
	public String faqURL = "http://ivpr." + Settings.IWEAVE_HOST + "/faq.php?" + Calendar.getInstance().getTimeInMillis();
	public JEditorPane troubleshootHTML;
	public JScrollPane troubleshootScrollPane;

	// ============== About Tab ============== //
	public JLabel aboutTitle, aboutVersion;
	public JEditorPane aboutHTML;
	
	public HomeSetupPanel()
	{
		maxPanels = 1;
		
		setLayout(null);
		setBounds(0, 0, SetupPanel.RIGHT_PANEL_WIDTH, SetupPanel.RIGHT_PANEL_HEIGHT);

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
				refreshButton.dispatchEvent(new MouseEvent(refreshButton, MouseEvent.MOUSE_RELEASED,
															System.currentTimeMillis(), 0,
															refreshButton.getX(), refreshButton.getY(),
															1, false));
			}
		}, 1000);
		
		globalHashMap.put("HomeSetupPanel", HomeSetupPanel.this);
	}

	public JPanel createHomeSetupPanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBounds(0, 0, this.getWidth(), this.getHeight());
		panel.setBackground(new Color(0xFFFFFF));
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPane.setBounds(0, 0, panel.getWidth(), panel.getHeight());

		tabbedPane.addTab("Weave", (weaveTab = createWeaveTab(tabbedPane)));
		tabbedPane.addTab("Sessions", (sessionsTab = createSessionsTab(tabbedPane)));
		tabbedPane.addTab("Plugins", (pluginsTab = createPluginsTab(tabbedPane)));
//		tabbedPane.addTab("Settings", (tab4 = createTab4(tabbedPane)));
		tabbedPane.addTab("Troubleshoot", (troubleshootTab = createTroubleshootTab(tabbedPane)));
		tabbedPane.addTab("About", (aboutTab = createAboutTab(tabbedPane)));
		tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent event)
			{
				JPanel selectedTab = (JPanel) tabbedPane.getSelectedComponent();
				
				if( selectedTab == weaveTab )
				{
					refreshRevisionTable();
				}
				else if( selectedTab == sessionsTab )
				{
					try {
						refreshSessionTable();
						
						if( sessionStateTable.getSelectedIndex() == -1 )
							sessionStateTable.setSelectedIndex(0);
					} catch (IllegalArgumentException e) {
						// We will get here if we try to set the table index
						// but there is no items in the table
					} catch (NoSuchMethodException e) {
						trace(STDERR, e);
						BugReportUtils.showBugReportDialog(e);
					} catch (SecurityException e) {
						trace(STDERR, e);
						BugReportUtils.showBugReportDialog(e);
					} catch (IllegalAccessException e) {
						trace(STDERR, e);
						BugReportUtils.showBugReportDialog(e);
					} catch (InvocationTargetException e) {
						trace(STDERR, e);
						BugReportUtils.showBugReportDialog(e);
					}
				}
				else if( selectedTab == pluginsTab )
				{
					try {
						if( pluginsTable.getSelectedIndex() == -1 )
							pluginsTable.setSelectedIndex(0);
					} catch (IllegalArgumentException e) {
						// We will get here if we try to set the table index
						// but there is no items in the table
					}
				}
				else if( selectedTab == troubleshootTab )
				{
					AsyncTask task = new AsyncTask() {
						@Override
						public Object doInBackground() {
							try {
								if( Settings.isOfflineMode() )
								{
									troubleshootHTML.setText("<br><center>FAQ is currently offline.</center>");
									return null;
								}
								
								troubleshootHTML.setPage(Settings.API_FAQ + "?" + System.currentTimeMillis());
								
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
									}
								});
							} catch (IOException e) {
								trace(STDERR, e);
								troubleshootHTML.setText("<br><center>FAQ is currently offline.</center>");
							}
							return null;
						}
					};
					task.execute();
				}
			}
		});
		panel.add(tabbedPane);

		switchToTab(weaveTab);
		
		return panel;
	}


	@Reflectable
	public int getCurrentTabIndex()
	{
		return tabbedPane.getSelectedIndex();
	}
	@Reflectable
	public String getCurrentTabName()
	{
		return tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
	}
	@Reflectable
	public Boolean switchToTab(String name)
	{
		return switchToTab(tabbedPane.indexOfTab(name));
	}
	@Reflectable
	public Boolean switchToTab(Component c)
	{
		return switchToTab(tabbedPane.indexOfComponent(c));
	}
	@Reflectable
	public Boolean switchToTab(Integer index)
	{
		try {
			tabbedPane.setSelectedIndex(index == 0 ? 1 : 0);
			tabbedPane.setSelectedIndex(index);
		} catch (IndexOutOfBoundsException e) {
			trace(STDERR, e);
			return false;
		}
		return true;
	}
	
	
	public JPanel createTab(JComponent parent)
	{
		JPanel panel = new JPanel(null);
		panel.setBounds(0, 0, parent.getWidth(), parent.getHeight());
		panel.setBackground(Color.WHITE);
		
		return panel;
	}
	
	public JPanel createWeaveTab(JComponent parent)
	{
		JPanel panel = createTab(parent);

		refreshButton = new DropDownButton("Refresh");
		refreshButton.setBounds(330, 50, 100, 30);
		refreshButton.setToolTipText("Check for a new version of " + Settings.PROJECT_NAME);
		refreshButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent a) 
			{
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						try {
							refreshInterface();
						} catch (InterruptedException e) {
							trace(STDERR, e);
						} catch (MalformedURLException e) {
							trace(STDERR, e);
						} catch (IOException e) {
							trace(STDERR, e);
						}
					}
				}, 400);
			}
		});
		refreshButton.addDropDownItem(new JMenuItem("Milestone"), new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent a) {
				trace(STDOUT, StringUtils.rpad("-> Switching to Milestone", ".", Settings.LOG_PADDING_LENGTH));
				Settings.INSTALL_MODE = INSTALL_ENUM.MILESTONE;
				Settings.save();
				refreshButton.updateSelectedItem(Settings.INSTALL_MODE);
				refreshProgramatically = true;
				try {
					refreshInterface();
				} catch (InterruptedException e) {
					trace(STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				} catch (IOException e) {
					trace(STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				}
			}
		});
		refreshButton.addDropDownItem(new JMenuItem("Nightly"), new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent a) {
				trace(STDOUT, StringUtils.rpad("-> Switching to Nightly", ".", Settings.LOG_PADDING_LENGTH));
				Settings.INSTALL_MODE = INSTALL_ENUM.NIGHTLY;
				Settings.save();
				refreshButton.updateSelectedItem(Settings.INSTALL_MODE);
				refreshProgramatically = true;
				try {
					refreshInterface();
				} catch (InterruptedException e) {
					trace(STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				} catch (IOException e) {
					trace(STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				}
			}
		});
		refreshButton.updateSelectedItem(Settings.INSTALL_MODE);
		panel.add(refreshButton);
		
		installButton = new DropDownButton("Install");
		installButton.setBounds(330, 10, 100, 30);
		installButton.setToolTipText("Download the latest version of " + Settings.PROJECT_NAME + " and install it.");
		installButton.setEnabled(false);
		installButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent a)
			{
				setButtonsEnabled(false);
			
				try {
					// Get the install URL to the zip file
					String urlStr = null;
					if( Settings.INSTALL_MODE == INSTALL_ENUM.NIGHTLY )
						urlStr = RemoteUtils.getConfigEntry(RemoteUtils.WEAVE_BINARIES_URL);
					else if( Settings.INSTALL_MODE == INSTALL_ENUM.MILESTONE )
						urlStr = UpdateUtils.getLatestMilestoneURL();
					
					if( urlStr == null ) {
						JOptionPane.showConfirmDialog(null, 
								"A connection to the internet could not be established.\n\n" +
								"Please connect to the internet and try again.", 
								"No Connection", 
								JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
						refreshInterface();
						return;
					}

					// Get the zip file's file name
					String fileName = UpdateUtils.getWeaveUpdateFileName(urlStr);
					if( fileName == null ) {
						JOptionPane.showConfirmDialog(null,
								"There was an error generating the update package filename.\n\n" +
								"Please try again later or if the problem persists,\n" +
								"report this issue as a bug for the developers.", 
								"Error", 
								JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
						refreshInterface();
						return;
					}
					
					// Get the active servlet container
					IConfig actvContainer = ConfigManager.getConfigManager().getActiveContainer();
					if( actvContainer == null ) {
						JOptionPane.showMessageDialog(null, 
								"There is no active servlet selected.\n\n" + 
								"Please configure a servlet to use, then try again.", 
								"Error", JOptionPane.ERROR_MESSAGE);
						refreshInterface();
						return;
					}

					// Get the active servlet container's webapps directory
					File cfgWebapps = actvContainer.getWebappsDirectory();
					if( cfgWebapps == null || !cfgWebapps.exists() ) {
						JOptionPane.showMessageDialog(null, 
								"Webapps folder for " + actvContainer.getConfigName() + " is not set.", 
								"Error", JOptionPane.ERROR_MESSAGE);
						refreshInterface();
						return;
					}
					
					File zip = new File(Settings.REVISIONS_DIRECTORY, fileName);
					
					DownloadManager.init("update")
						.setLabel(downloadLabel)
						.setProgressbar(progressbar)
						.downloadFrom(urlStr)
						.extractTo(zip.getAbsolutePath())
						.installTo(cfgWebapps.getAbsolutePath())
						.callback(onDownloadCompleteCallback)
						.start();
					
				} catch (InterruptedException e) {
					trace(STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				} catch (MalformedURLException e) {
					trace(STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				} catch (IOException e) {
					trace(STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				}
			}
		});
		panel.add(installButton);
		
		deployButton = new JButton("Deploy");
		deployButton.setBounds(330, 150, 100, 30);
		deployButton.setToolTipText("Install Weave from a backup revision, selected on the left in the table.");
		deployButton.setVisible(true);
		deployButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent a) {
				try {
					int index = revisionTable.getSelectedIndex();
					if( index < 0 )
						return;
					
					String file = Revisions.getRevisionsList().get(index).getAbsolutePath();
	
					// Get the active servlet container
					IConfig actvContainer = ConfigManager.getConfigManager().getActiveContainer();
					if( actvContainer == null ) {
						JOptionPane.showMessageDialog(null, 
								"There is no active servlet selected.\n\n" + 
								"Please configure a servlet to use, then try again.", 
								"Error", JOptionPane.ERROR_MESSAGE);
						refreshInterface();
						return;
					}
	
					// Get the active servlet container's webapps directory
					File cfgWebapps = actvContainer.getWebappsDirectory();
					if( cfgWebapps == null || !cfgWebapps.exists() ) {
						JOptionPane.showMessageDialog(null, 
								"Webapps folder for " + actvContainer.getConfigName() + " is not set.", 
								"Error", JOptionPane.ERROR_MESSAGE);
						refreshInterface();
						return;
					}
					
					DownloadManager.init("update")
						.setLabel(downloadLabel)
						.setProgressbar(progressbar)
						.extractTo(file)
						.installTo(cfgWebapps.getAbsolutePath())
						.callback(onDownloadCompleteCallback)
						.start();
						
				} catch (MalformedURLException e) {
					trace(STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				} catch (InterruptedException e) {
					trace(STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				} catch (IOException e) {
					trace(STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				}
			}
		});
		panel.add(deployButton);
		
		deleteButton = new JButton("Delete");
		deleteButton.setBounds(330, 190, 100, 30);
		deleteButton.setToolTipText("Delete an individual revision, selected on the left in the table.");
		deleteButton.setVisible(true);
		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent a) 
			{
				int index = revisionTable.getSelectedIndex();
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
							trace(STDERR, e);
							BugReportUtils.showBugReportDialog(e);
						} catch (MalformedURLException e) {
							trace(STDERR, e);
							BugReportUtils.showBugReportDialog(e);
						} catch (IOException e) {
							trace(STDERR, e);
							BugReportUtils.showBugReportDialog(e);
						}
					}
				}, 1000);
			}
		});
		panel.add(deleteButton);
		
		cleanButton = new JButton("Clean");
		cleanButton.setBounds(330, 230, 100, 30);
		cleanButton.setToolTipText("Auto-delete older revisions to free up space on your hard drive.");
		cleanButton.setVisible(true);
		cleanButton.addActionListener(new ActionListener() {
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
							trace(STDERR, e);
							BugReportUtils.showBugReportDialog(e);
						} catch (MalformedURLException e) {
							trace(STDERR, e);
							BugReportUtils.showBugReportDialog(e);
						} catch (IOException e) {
							trace(STDERR, e);
							BugReportUtils.showBugReportDialog(e);
						}
					}
				}, 1000);
			}
		});
		panel.add(cleanButton);

		weaveStats = new WeaveStats();
		weaveStats.setBounds(10, 10, 300, 75);
		weaveStats.setVisible(true);
		panel.add(weaveStats);
		
		progressbar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
		progressbar.setBounds(10, 90, 420, 15);
		progressbar.setIndeterminate(true);
		progressbar.setVisible(false);
		panel.add(progressbar);
		
		downloadLabel = new JLabel();
		downloadLabel.setBounds(10, 110, 420, 25);
		downloadLabel.setFont(new Font(Settings.FONT, Font.PLAIN, 12));
		downloadLabel.setText("");
		downloadLabel.setVisible(false);
		panel.add(downloadLabel);
		
		revisionTable = new CustomTable(new String[] {"Revision", "Date Downloaded"}, new Object[0][2]);
		revisionTable.setBounds(10, 150, 300, 150);
		revisionTable.setVisible(true);
		new DropTarget(revisionTable, new DropTargetListener() {
			@SuppressWarnings("unchecked")
			@Override public void drop(DropTargetDropEvent dtde) {
				dtde.acceptDrop(DnDConstants.ACTION_COPY);
				
				Transferable transferable = dtde.getTransferable();
				DataFlavor[] flavors = transferable.getTransferDataFlavors();
				File REV = null, destination = null;
				
				for( DataFlavor flavor : flavors )
				{
					if( flavor.isFlavorJavaFileListType() )
					{
						try {
							List<File> files = (List<File>) transferable.getTransferData(flavor);
							REV = Settings.REVISIONS_DIRECTORY;
							if( REV == null || !REV.exists() )
								return;
							
							for( File file : files )
							{
								destination = new File(REV, file.getName());
								if( file.equals(destination) )
									continue;
								FileUtils.copy(file, destination, TransferUtils.SINGLE_FILE | TransferUtils.OVERWRITE);
							}
						} catch (InterruptedException e) {
							trace(STDERR, e);
							BugReportUtils.showBugReportDialog(e);
						} catch (UnsupportedFlavorException e) {
							trace(STDERR, e);
							BugReportUtils.showBugReportDialog(e);
						} catch (IOException e) {
							trace(STDERR, e);
							BugReportUtils.showBugReportDialog(e);
						}
					}
				}
				switchToTab(weaveTab);
			}
			@Override public void dropActionChanged(DropTargetDragEvent dtde) { }
			@Override public void dragOver(DropTargetDragEvent dtde) { }
			@Override public void dragExit(DropTargetEvent dte) { }
			@Override public void dragEnter(DropTargetDragEvent dtde) { }
		});
		panel.add(revisionTable);
		
		return panel;
	}
	
	public JPanel createSessionsTab(JComponent parent)
	{
		JPanel panel = createTab(parent);
		
		sessionStateTable = new CustomTable(new String[] {"Name", "Date", "Size"}, new Object[0][3]);
		sessionStateTable.setBounds(0, 0, panel.getWidth() - 10, panel.getHeight() / 2);
		sessionStateTable.setColumnSizes(new int[] { 200, 75, 50 });
		sessionStateTable.addTableSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) 
			{
				String selectedFile = (String) sessionStateTable.getSelectedRow()[0];
				if( selectedFile == null )
					return;
				
				File WEBAPPS, ROOT, sessionState;
				ZipFile zip = null;
				
				try {
					WEBAPPS = (File) ObjectUtils.ternary(ConfigManager.getConfigManager().getActiveContainer(), "getWebappsDirectory", null);
					if( WEBAPPS == null || !WEBAPPS.exists() )
						return;
					
					ROOT = new File(WEBAPPS, "ROOT");
					if( !ROOT.exists() )
						return;
					
					sessionState = new File(ROOT, selectedFile);
					if( !sessionState.exists() )
						return;
					
				
					zip = new ZipFile(sessionState);
					Enumeration<? extends ZipEntry> entries = zip.entries();
					BufferedImage img;
					while( entries.hasMoreElements() )
					{
						ZipEntry entry = entries.nextElement();
						if( entry.getName().contains("thumbnail") )
						{
							img = ImageUtils.fit(ImageIO.read(zip.getInputStream(entry)), sessionLabel.getWidth(), sessionLabel.getHeight());
							sessionLabel.setIcon(new ImageIcon(img));
							break;
						}
					}
					launchSessionState.setEnabled(sessionStateTable.getSelectedIndex() >= 0);
					
				} catch (NoSuchMethodException ex) {
					trace(STDERR, ex);
					BugReportUtils.showBugReportDialog(ex);
				} catch (SecurityException ex) {
					trace(STDERR, ex);
					BugReportUtils.showBugReportDialog(ex);
				} catch (IllegalAccessException ex) {
					trace(STDERR, ex);
					BugReportUtils.showBugReportDialog(ex);
				} catch (IllegalArgumentException ex) {
					trace(STDERR, ex);
					BugReportUtils.showBugReportDialog(ex);
				} catch (InvocationTargetException ex) {
					trace(STDERR, ex);
					BugReportUtils.showBugReportDialog(ex);
				} catch (ZipException ex) {
					sessionLabel.setIcon(null);
				} catch (IOException ex) {
					trace(STDERR, ex);
					BugReportUtils.showBugReportDialog(ex);
				} finally {
						try {
							if( zip != null )
								zip.close();
						} catch (IOException ex) {
							trace(STDERR, ex);
							BugReportUtils.showBugReportDialog(ex);
						}
				}
			}
		});
		sessionStateTable.addTableFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				launchSessionState.setEnabled(sessionStateTable.getSelectedIndex() >= 0);
				if( sessionStateTable.getSelectedIndex() == -1 )
					sessionLabel.setIcon(null);
			}
			@Override
			public void focusGained(FocusEvent e) {
				launchSessionState.setEnabled(sessionStateTable.getSelectedIndex() >= 0);
				if( sessionStateTable.getSelectedIndex() == -1 )
					sessionLabel.setIcon(null);
			}
		});
		new DropTarget(sessionStateTable, new DropTargetListener() {
			@SuppressWarnings("unchecked")
			@Override public void drop(DropTargetDropEvent dtde) {
				dtde.acceptDrop(DnDConstants.ACTION_COPY);
				
				Transferable transferable = dtde.getTransferable();
				DataFlavor[] flavors = transferable.getTransferDataFlavors();
				File WEBAPPS, ROOT, destination;
				
				for( DataFlavor flavor : flavors )
				{
					if( flavor.isFlavorJavaFileListType() )
					{
						try {
							List<File> files = (List<File>) transferable.getTransferData(flavor);
							WEBAPPS = (File) ObjectUtils.ternary(ConfigManager.getConfigManager().getActiveContainer(), "getWebappsDirectory", null);
							if( WEBAPPS == null || !WEBAPPS.exists() )
								return;
							
							ROOT = new File(WEBAPPS, "ROOT");
							if( !ROOT.exists() )
								return;
							
							for( File file : files )
							{
								destination = new File(ROOT, file.getName());
								if( file.equals(destination) )
									continue;
								FileUtils.copy(file, destination, TransferUtils.SINGLE_FILE | TransferUtils.OVERWRITE);
							}
						} catch (UnsupportedFlavorException e) {
							trace(STDERR, e);
							BugReportUtils.showBugReportDialog(e);
						} catch (IOException e) {
							trace(STDERR, e);
							BugReportUtils.showBugReportDialog(e);
						} catch (InterruptedException e) {
							trace(STDERR, e);
							BugReportUtils.showBugReportDialog(e);
						} catch (NoSuchMethodException e) {
							trace(STDERR, e);
							BugReportUtils.showBugReportDialog(e);
						} catch (SecurityException e) {
							trace(STDERR, e);
							BugReportUtils.showBugReportDialog(e);
						} catch (IllegalAccessException e) {
							trace(STDERR, e);
							BugReportUtils.showBugReportDialog(e);
						} catch (IllegalArgumentException e) {
							trace(STDERR, e);
							BugReportUtils.showBugReportDialog(e);
						} catch (InvocationTargetException e) {
							trace(STDERR, e);
							BugReportUtils.showBugReportDialog(e);
						}
					}
				}
				switchToTab(sessionsTab);
			}
			@Override public void dropActionChanged(DropTargetDragEvent dtde) { }
			@Override public void dragOver(DropTargetDragEvent dtde) { }
			@Override public void dragExit(DropTargetEvent dte) { }
			@Override public void dragEnter(DropTargetDragEvent dtde) { }
		});
		panel.add(sessionStateTable);

		sessionLabel = new JLabel();
		sessionLabel.setBounds(20, panel.getHeight() / 2 + 10, 200, 125);
		sessionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		sessionLabel.setVisible(true);
		panel.add(sessionLabel);
		
		dndHelp = new JTextArea();
		dndHelp.setBounds(240, panel.getHeight() / 2 + 100, 180, 50);
		dndHelp.setText("You may drag and drop Weave files into the table above.");
		dndHelp.setLineWrap(true);
		dndHelp.setWrapStyleWord(true);
		dndHelp.setVisible(true);
		panel.add(dndHelp);
		
		launchSessionState = new JButton("Open Session");
		launchSessionState.setBounds(240, panel.getHeight() / 2 + 10, 180, 30);
		launchSessionState.setToolTipText("Open the selected session state");
		launchSessionState.setEnabled(false);
		launchSessionState.setVisible(true);
		launchSessionState.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent a) 
			{
				String selectedFile = (String) sessionStateTable.getSelectedRow()[0];
				if( selectedFile == null )
					return;
				
				File WEBAPPS, ROOT, sessionState;
				
				try {
					WEBAPPS = (File) ObjectUtils.ternary(ConfigManager.getConfigManager().getActiveContainer(), "getWebappsDirectory", null);
					if( WEBAPPS == null )
						return;
					ROOT = new File(WEBAPPS, "ROOT");
					if( !ROOT.exists() )
						return;
					sessionState = new File(ROOT, selectedFile);
					if( !sessionState.exists() )
						return;
					
					LaunchUtils.browse("http://" + 
							Settings.LOCALHOST + ":" + 
							ConfigManager.getConfigManager().getActiveContainer().getPort() +
							"/weave.html?file=" + 
							URLRequestUtils.encodeURL(sessionState.getName(), StandardCharsets.UTF_8));
					
				} catch (NoSuchMethodException e) {
					trace(STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				} catch (SecurityException e) {
					trace(STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				} catch (IllegalAccessException e) {
					trace(STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				} catch (IllegalArgumentException e) {
					trace(STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				} catch (InvocationTargetException e) {
					trace(STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				} catch (IOException e) {
					trace(STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				} catch (URISyntaxException e) {
					trace(STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				} catch (InterruptedException e) {
					trace(STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				}
			}
		});
		panel.add(launchSessionState);

		launchAdminConsoleButton = new JButton("Open Admin Console");
		launchAdminConsoleButton.setBounds(240, panel.getHeight() / 2 + 50, 180, 30);
		launchAdminConsoleButton.setToolTipText("Launch the Admin Console in your browser");
		launchAdminConsoleButton.setVisible(true);
		launchAdminConsoleButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent a) 
			{
				try {
					try {
						ReflectionUtils.reflectMethod(Globals.get("Installer"), "setProgress",
													new Class<?>[] { Integer.class },
													new Object[] { 15 });
						Settings.SETUP_COMPLETE = true;
						Settings.save();
					} catch (NoSuchMethodException e) {
						trace(STDERR, e);
						BugReportUtils.showBugReportDialog(e);
					} catch (SecurityException e) {
						trace(STDERR, e);
						BugReportUtils.showBugReportDialog(e);
					} catch (IllegalAccessException e) {
						trace(STDERR, e);
						BugReportUtils.showBugReportDialog(e);
					} catch (IllegalArgumentException e) {
						trace(STDERR, e);
						BugReportUtils.showBugReportDialog(e);
					} catch (InvocationTargetException e) {
						trace(STDERR, e);
						BugReportUtils.showBugReportDialog(e);
					}
					
					LaunchUtils.openAdminConsole();
				} catch (IOException e) {
					trace(STDERR, e);
				} catch (URISyntaxException e) {
					trace(STDERR, e);
				} catch (InterruptedException e) {
					trace(STDERR, e);
				}
			}
		});
		panel.add(launchAdminConsoleButton);
		
		return panel;
	}
	
	public JPanel createPluginsTab(JComponent parent)
	{
		final JPanel panel = createTab(parent);

		pluginsPanel = new JPanel();
		pluginsPanel.setBounds(panel.getWidth() / 3, 0, 2 * panel.getWidth() / 3 - 12, panel.getHeight() - 30);
		pluginsPanel.setLayout(null);
		pluginsPanel.setBackground(Color.WHITE);
		panel.add(pluginsPanel);
		
		pluginsTable = new CustomTable(new String[] { "Name" }, new Object[0][1]);
		pluginsTable.setBounds(0, 0, panel.getWidth() / 3, panel.getHeight() - 30);
		pluginsTable.addTableSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {

				String pluginName = (String) pluginsTable.getSelectedRow()[0];
				if( pluginName == null )
					return;
				
				IPlugin selectedPlugin = PluginManager.getPluginManager().getPluginByName(pluginName);
				if( selectedPlugin == null )
					return;

				JPanel p = selectedPlugin.getPluginPanel();
				p.setBounds(0, 0, pluginsPanel.getWidth(), pluginsPanel.getHeight());
				p.setLayout(null);
				p.setBackground(Color.WHITE);
				pluginsPanel.removeAll();
				pluginsPanel.add(p);
				
				pluginsPanel.revalidate();
				pluginsPanel.repaint();
			}
		});
		panel.add(pluginsTable);

		refreshPluginTable();
		
		return panel;
	}
	
	public JPanel createTab4(JComponent parent)
	{
		JPanel panel = createTab(parent);
//		JPanel innerPanel = new JPanel();
//		JPanel serverUpdateBox = new JPanel();
//		JPanel weaveUpdateBox = new JPanel();
//		JPanel maintenanceBox = new JPanel();
//		JPanel protoextBox = new JPanel();
//		
//		innerPanel.setLayout(null);
//		innerPanel.setSize(panel.getWidth() - 40, 800);
//		innerPanel.setPreferredSize(new Dimension(parent.getWidth() - 40, 800));
//		innerPanel.setBackground(Color.WHITE);
//		
//
//		////////////////////////////////////////////////////////////////////////////////////////////////
//		// Weave Server Assistant Updates
//		////////////////////////////////////////////////////////////////////////////////////////////////
//		settingsServerUpdatesTitle = BorderFactory.createTitledBorder(null, "Server Assistant Updates", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font(Settings.FONT, Font.BOLD, 14), Color.BLUE);
//		serverUpdateBox.setBounds(10, 10, innerPanel.getWidth() - 40, 150);
//		serverUpdateBox.setLayout(null);
//		serverUpdateBox.setBackground(Color.WHITE);
//		serverUpdateBox.setBorder(settingsServerUpdatesTitle);
//		
//		settingsUpdatesAutoInstallCheckbox = new JCheckBox("Automatically install updates on startup");
//		settingsUpdatesAutoInstallCheckbox.setBounds(10, 20, 300, 30);
//		settingsUpdatesAutoInstallCheckbox.setBackground(Color.WHITE);
//		settingsUpdatesAutoInstallCheckbox.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				
//			}
//		});
//		serverUpdateBox.add(settingsUpdatesAutoInstallCheckbox);
//		
//		settingsUpdatesCheckNewCheckbox = new JCheckBox("Check for new updates");
//		settingsUpdatesCheckNewCheckbox.setBounds(10, 50, 170, 30);
//		settingsUpdatesCheckNewCheckbox.setBackground(Color.WHITE);
//		settingsUpdatesCheckNewCheckbox.setEnabled(true);
//		settingsUpdatesCheckNewCheckbox.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				settingsUpdatesCheckNewCombobox.setEnabled(settingsUpdatesCheckNewCheckbox.isEnabled());
//			}
//		});
//		serverUpdateBox.add(settingsUpdatesCheckNewCheckbox);
//		
//		settingsUpdatesCheckNewCombobox = new JComboBox<String>();
//		settingsUpdatesCheckNewCombobox.setBounds(180, 50, 150, 30);
//		settingsUpdatesCheckNewCombobox.setBackground(Color.WHITE);
//		settingsUpdatesCheckNewCombobox.setEnabled(settingsUpdatesCheckNewCheckbox.isEnabled());
//		settingsUpdatesCheckNewCombobox.addItem("Every hour");
//		settingsUpdatesCheckNewCombobox.addItem("Every day");
//		settingsUpdatesCheckNewCombobox.addItem("Every week");
//		settingsUpdatesCheckNewCombobox.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				
//			}
//		});
//		serverUpdateBox.add(settingsUpdatesCheckNewCombobox);
//		
//
//		////////////////////////////////////////////////////////////////////////////////////////////////
//		// Weave Updates
//		////////////////////////////////////////////////////////////////////////////////////////////////
//		settingsWeaveUpdatesTitle = BorderFactory.createTitledBorder(null, "Weave Updates", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font(Settings.FONT, Font.BOLD, 14), Color.BLUE);
//		weaveUpdateBox.setBounds(10, 170, innerPanel.getWidth() - 40, 150);
//		weaveUpdateBox.setLayout(null);
//		weaveUpdateBox.setBackground(Color.WHITE);
//		weaveUpdateBox.setBorder(settingsWeaveUpdatesTitle);
//		
//		
//
//		////////////////////////////////////////////////////////////////////////////////////////////////
//		// Maintenance
//		////////////////////////////////////////////////////////////////////////////////////////////////
//		settingsMaintenanceTitle = BorderFactory.createTitledBorder(null, "Maintenance", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font(Settings.FONT, Font.BOLD, 14), Color.BLUE);
//		maintenanceBox.setBounds(10, 330, innerPanel.getWidth() - 40, 150);
//		maintenanceBox.setLayout(null);
//		maintenanceBox.setBackground(Color.WHITE);
//		maintenanceBox.setBorder(settingsMaintenanceTitle);
//		
//		settingsMaintenanceDeleteLogsCheckbox = new JCheckBox("Delete log files older than ");
//		settingsMaintenanceDeleteLogsCheckbox.setBounds(10, 22, 180, 25);
//		settingsMaintenanceDeleteLogsCheckbox.setBackground(Color.WHITE);
//		settingsMaintenanceDeleteLogsCheckbox.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				
//			}
//		});
//		maintenanceBox.add(settingsMaintenanceDeleteLogsCheckbox);
//		
//		settingsMaintenanceDeleteLogsTextfield = new JTextField();
//		settingsMaintenanceDeleteLogsTextfield.setBounds(190, 21, 25, 26);
//		settingsMaintenanceDeleteLogsTextfield.setBackground(Color.WHITE);
//		settingsMaintenanceDeleteLogsTextfield.setBorder(new LineBorder(Color.BLACK, 1));
//		maintenanceBox.add(settingsMaintenanceDeleteLogsTextfield);
//		
//		maintenanceBox.setComponentZOrder(settingsMaintenanceDeleteLogsTextfield, 0);
//		maintenanceBox.setComponentZOrder(settingsMaintenanceDeleteLogsCheckbox, 1);
//
//		
//
//		////////////////////////////////////////////////////////////////////////////////////////////////
//		// Protocols & Extensions
//		////////////////////////////////////////////////////////////////////////////////////////////////
//		
//		settingsProtoExtTitle = BorderFactory.createTitledBorder(null, "Protocol & Extension", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font(Settings.FONT, Font.BOLD, 14), Color.GRAY);
//		protoextBox.setBounds(10, 490, innerPanel.getWidth() - 40, 150);
//		protoextBox.setLayout(null);
//		protoextBox.setBackground(Color.WHITE);
//		protoextBox.setBorder(settingsProtoExtTitle);
//		
//		settingsExtCheckbox = new JCheckBox("Enable Weave Extesion");
//		settingsExtCheckbox.setBounds(10, 20, 300, 30);
//		settingsExtCheckbox.setBackground(Color.WHITE);
//		settingsExtCheckbox.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				try {
//					Settings.enableWeaveExtension(settingsExtCheckbox.isSelected());
//				} catch (IllegalArgumentException e1) {
//					trace(STDERR, e1);
//				} catch (IllegalAccessException e1) {
//					trace(STDERR, e1);
//				} catch (InvocationTargetException e1) {
//					trace(STDERR, e1);
//				}
//			}
//		});
//		protoextBox.add(settingsExtCheckbox);
//		
//		settingsProtocolCheckbox = new JCheckBox("Enable Weave Protocol");
//		settingsProtocolCheckbox.setBounds(10, 50, 300, 30);
//		settingsProtocolCheckbox.setBackground(Color.WHITE);
//		settingsProtocolCheckbox.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				try {
//					Settings.enableWeaveProtocol(settingsProtocolCheckbox.isSelected());
//				} catch (IllegalArgumentException e1) {
//					trace(STDERR, e1);
//				} catch (IllegalAccessException e1) {
//					trace(STDERR, e1);
//				} catch (InvocationTargetException e1) {
//					trace(STDERR, e1);
//				}
//			}
//		});
//		protoextBox.add(settingsProtocolCheckbox);
//		
//		
//		innerPanel.add(serverUpdateBox);
//		innerPanel.add(weaveUpdateBox);
//		innerPanel.add(maintenanceBox);
//		innerPanel.add(protoextBox);
//		
//		
//		settingsScrollPane = new JScrollPane();
//		settingsScrollPane.setBounds(0, 0, parent.getWidth() - 10, parent.getHeight() - 30);
//		settingsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//		settingsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//		settingsScrollPane.setViewportView(innerPanel);
//		settingsScrollPane.getVerticalScrollBar().setUnitIncrement(16);
//		settingsScrollPane.setVisible(true);
//		
//		panel.add(settingsScrollPane);
//		
		return panel;
	}
	public JPanel createTroubleshootTab(JComponent parent)
	{
		JPanel panel = createTab(parent);

		troubleshootHTML = new JEditorPane();
		troubleshootHTML.setBounds(0, 0, panel.getWidth() - 20, panel.getHeight() - 20);
		troubleshootHTML.setBackground(Color.WHITE);
		troubleshootHTML.setEditable(false);
		troubleshootHTML.setContentType("text/html");
		troubleshootHTML.setText("<br><center>Loading....</center>");
		troubleshootHTML.setVisible(true);

		troubleshootScrollPane = new JScrollPane(troubleshootHTML, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		troubleshootScrollPane.setBounds(0, 0, parent.getWidth() - 10, parent.getHeight() - 30);
		troubleshootScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		troubleshootScrollPane.setVisible(true);

		panel.add(troubleshootScrollPane);
		
		return panel;
	}
	public JPanel createAboutTab(JComponent parent)
	{
		JPanel panel = createTab(parent);
	
		aboutTitle = new JLabel(Settings.SERVER_NAME);
		aboutTitle.setBounds(20, 20, 300, 30);
		aboutTitle.setFont(new Font(Settings.FONT, Font.BOLD, 18));
		
		aboutVersion = new JLabel(Settings.SERVER_VER);
		aboutVersion.setBounds(20, 50, 300, 30);
		aboutVersion.setFont(new Font(Settings.FONT, Font.PLAIN, 13));
		
		aboutHTML = new JEditorPane();
		aboutHTML.setBounds(20, 120, 400, 200);
		aboutHTML.setBackground(Color.WHITE);
		aboutHTML.setEditable(false);
		aboutHTML.setContentType("text/html");
		aboutHTML.setFont(new Font(Settings.FONT, Font.PLAIN, 10));
		aboutHTML.setText(	"The Weave Server Assistant is a cross-platform utiliy designed to help " +
							"users to install Weave and its components to your system with minimal effort. " +
							"<br><br><br><br><br><br>" +
							"(c) Institute for Visualization and Perception Research<br>" +
							"Visit: <a href='" + Settings.IWEAVE_URL + "'>" + Settings.IWEAVE_URL + "</a><br>");
		String htmlStyle = "body { 	font-family: " + aboutHTML.getFont().getFamily() + "; " +
									"font-size: " + aboutHTML.getFont().getSize() + "px; }" +
							"b { font-size: " + (aboutHTML.getFont().getSize() + 2) + "px; }";
		((HTMLDocument)aboutHTML.getDocument()).getStyleSheet().addRule(htmlStyle);
		aboutHTML.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if( e.getEventType() == HyperlinkEvent.EventType.ACTIVATED )
				{
					try {
						LaunchUtils.browse(e.getURL().toURI());
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
			}
		});
		
		panel.add(aboutTitle);
		panel.add(aboutVersion);
		panel.add(aboutHTML);
		
		return panel;
	}
	
	private Function onDownloadCompleteCallback = new Function() {
		@Override
		public void run() {
			System.gc();
			
			Integer returnCode = (Integer) arguments[0];
			String filename = (String) arguments[1];
			
			if( returnCode == TransferUtils.COMPLETE )
			{
				ConfigManager
					.getConfigManager()
					.getActiveContainer()
					.setInstallVersion(Revisions.getRevisionVersion(filename));
				ConfigManager.getConfigManager().save();
				
				try {
					ReflectionUtils.reflectMethod(Globals.get("Installer"), "setProgress", 
												new Class<?>[] { Integer.class },
												new Object[] { 7 });
				} catch (NoSuchMethodException e) {
					trace(STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				} catch (SecurityException e) {
					trace(STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				} catch (IllegalAccessException e) {
					trace(STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				} catch (IllegalArgumentException e) {
					trace(STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				} catch (InvocationTargetException e) {
					trace(STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				}
			}
			try {
				Settings.cleanUp();
				Thread.sleep(1000);
				refreshProgramatically = true;
				refreshInterface();
			} catch (InterruptedException e) {
				trace(STDERR, e);
			} catch (MalformedURLException e) {
				trace(STDERR, e);
			} catch (IOException e) {
				trace(STDERR, e);
			}			
		}
	};
	
	private void refreshRevisionTable()
	{
		ArrayList<File> revisionList = Revisions.getRevisionsList();
		Object[][] revisionData = new Object[revisionList.size()][2];
		File file = null;
		Date date = new Date();
		String revisionName = "";
		
		try {
			for( int i = 0; i < revisionList.size(); i++ )
			{
				file = revisionList.get(i);
				revisionName = Revisions.getRevisionVersion(file.getName());
				date.setTime(file.lastModified());
	
				String configVer = (String)ObjectUtils.ternary(
									ConfigManager.getConfigManager().getActiveContainer(), "getInstallVersion", "");
				revisionData[i][0] = revisionName + ((revisionName.equals(configVer)) ? "  (current)" : "" );
				revisionData[i][1] = new SimpleDateFormat("MM/dd/yyyy h:mm a").format(date);
			}
		} catch (NoSuchMethodException e) {
			trace(STDERR, e);
		} catch (SecurityException e) {
			trace(STDERR, e);
		} catch (IllegalAccessException e) {
			trace(STDERR, e);
		} catch (IllegalArgumentException e) {
			trace(STDERR, e);
		} catch (InvocationTargetException e) {
			trace(STDERR, e);
		}
		revisionTable.setData(revisionData).refreshTable();
	}
	private void refreshSessionTable() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		File WEBAPPS, ROOT;
	
		WEBAPPS = (File) ObjectUtils.ternary(ConfigManager.getConfigManager().getActiveContainer(), "getWebappsDirectory", null);
		if( WEBAPPS != null && WEBAPPS.exists() )
		{
			ROOT = new File(WEBAPPS, "ROOT");
			if( ROOT.exists() )
			{
				int fileCount = 0;
				String[] files = ROOT.list();
				
				for( int i = 0; i < files.length; i++ )
					if( FileUtils.getExt(files[i]).equals("weave") || FileUtils.getExt(files[i]).equals("xml") )
						fileCount++;
				
				File sessionFile = null;
				Date modifiedDate = null;
				Object[][] data = new Object[fileCount][3];
				
				sessionStateTable.setData(new Object[1][3]).refreshTable();
				fileCount = 0;
				
				for( int i = 0; i < files.length; i++ )
				{
					if( FileUtils.getExt(files[i]).equals("weave") || FileUtils.getExt(files[i]).equals("xml") ) {
						sessionFile = new File(ROOT, files[i]);
						modifiedDate = new Date(sessionFile.lastModified());
						data[fileCount][0] = sessionFile.getName();
						data[fileCount][1] = new SimpleDateFormat("MM/dd/yy h:mm a").format(modifiedDate);
						data[fileCount][2] = FileUtils.sizeify(sessionFile.length());
						fileCount++;
					}
				}
				
				sessionStateTable.setData(data).refreshTable();
			}
		}
	}
	private void refreshPluginTable()
	{
		ArrayList<IPlugin> plugins = PluginManager.getPluginManager().getPlugins();
		Object[][] data = new Object[plugins.size()][1];
		for( int i = 0; i < plugins.size(); i++ )
			data[i][0] = plugins.get(i).getPluginName();
		pluginsTable.setData(data).refreshTable();
	}
	private void refreshInterface() throws InterruptedException, IOException
	{
		traceln(STDOUT, StringUtils.rpad("-> Refreshing User Interface", ".", Settings.LOG_PADDING_LENGTH));

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
		
//		pluginsProgressLabel.setVisible(false);
//		pluginsProgressLabel.setText("");
//		pluginsProgressBar.setVisible(false);
//		pluginsProgressBar.setIndeterminate(true); 
//		pluginsProgressBar.setString(""); 
//		pluginsProgressBar.setValue(0); 

		setButtonsEnabled(true);
		installButton.setEnabled(updateAvailable == UpdateUtils.UPDATE_AVAILABLE);
		cleanButton.setEnabled(Revisions.getNumberOfRevisions() > Settings.recommendPrune);
		
		refreshRevisionTable();
	}
	
	private void setButtonsEnabled(boolean enabled)
	{
		refreshButton.setEnabled(enabled);
		installButton.setEnabled(enabled);
		deployButton.setEnabled(enabled);
		deleteButton.setEnabled(enabled);
		cleanButton.setEnabled(enabled);
	}
}
