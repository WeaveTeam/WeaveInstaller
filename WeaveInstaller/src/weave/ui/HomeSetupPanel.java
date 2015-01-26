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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
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
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;

import weave.Revisions;
import weave.Settings;
import weave.async.AsyncCallback;
import weave.async.AsyncObserver;
import weave.async.AsyncTask;
import weave.configs.IConfig;
import weave.inc.SetupPanel;
import weave.managers.ConfigManager;
import weave.managers.IconManager;
import weave.reflect.Reflectable;
import weave.utils.BugReportUtils;
import weave.utils.DownloadUtils;
import weave.utils.FileUtils;
import weave.utils.ImageUtils;
import weave.utils.LaunchUtils;
import weave.utils.ObjectUtils;
import weave.utils.RemoteUtils;
import weave.utils.TimeUtils;
import weave.utils.TraceUtils;
import weave.utils.TransferUtils;
import weave.utils.UpdateUtils;
import weave.utils.ZipUtils;

@SuppressWarnings("serial")
public class HomeSetupPanel extends SetupPanel 
{
	private boolean refreshProgramatically = false;
	public JTabbedPane tabbedPane;
	public JPanel tab1, tab2, tab3, tab4, tab5;

	
	// ============== Tab 1 ============== //
	public JButton  installButton, refreshButton, 
					deployButton, deleteButton, 
					cleanButton;
	public JLabel	downloadLabel;
	public JProgressBar progressbar;
	public WeaveStats weaveStats;
	public CustomTable revisionTable;
	public SocketStatus localhostStatus, lanStatus, internetStatus;
	
	
	// ============== Tab 2 ============== //
	public CustomTable sessionStateTable;
	public JLabel sessionLabel;
	public JTextArea dndHelp;
	public JButton launchSessionState, launchAdminConsoleButton;
	
	// ============== Tab 3 ============== //
	public JScrollPane settingsScrollPane;
	public TitledBorder settingsServerUpdatesTitle, settingsWeaveUpdatesTitle, settingsMaintenanceTitle, settingsProtoExtTitle;
	public JCheckBox settingsUpdatesAutoInstallCheckbox, settingsUpdatesCheckNewCheckbox;
	public JComboBox<String> settingsUpdatesCheckNewCombobox;
	public JCheckBox settingsMaintenanceDeleteLogsCheckbox, settingsMaintenanceDebugCheckbox;
	public JTextField settingsMaintenanceDeleteLogsTextfield;
	public JCheckBox settingsExtCheckbox, settingsProtocolCheckbox;
	
	
	// ============== Tab 4 ============== //
	public String faqURL = "http://ivpr." + Settings.IWEAVE_HOST + "/faq.php?" + Calendar.getInstance().getTimeInMillis();
	public JEditorPane troubleshootHTML;
	public JScrollPane troubleshootScrollPane;

	// ============== Tab 5 ============== //
	public JLabel aboutImage, aboutTitle, aboutVersion;
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
				refreshButton.doClick();
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

		tabbedPane.addTab("Weave", (tab1 = createTab1(tabbedPane)));
		tabbedPane.addTab("Sessions", (tab2 = createTab2(tabbedPane)));
//		tabbedPane.addTab("Settings", (tab3 = createTab3(tabbedPane)));
		tabbedPane.addTab("Troubleshoot", (tab4 = createTab4(tabbedPane)));
		tabbedPane.addTab("About", (tab5 = createTab5(tabbedPane)));
		tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent event)
			{
				JPanel selectedPanel = (JPanel) tabbedPane.getSelectedComponent();
				if( selectedPanel == tab2 )
				{
					File WEBAPPS, ROOT;
					
					try {
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
								
								sessionStateTable.setData(new Object[1][3]);
								sessionStateTable.refreshTable();
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
								
								sessionStateTable.setData(data);
								sessionStateTable.refreshTable();
							}
						}
					} catch (NoSuchMethodException e) {
						TraceUtils.trace(TraceUtils.STDERR, e);
						BugReportUtils.showBugReportDialog(e);
					} catch (SecurityException e) {
						TraceUtils.trace(TraceUtils.STDERR, e);
						BugReportUtils.showBugReportDialog(e);
					} catch (IllegalAccessException e) {
						TraceUtils.trace(TraceUtils.STDERR, e);
						BugReportUtils.showBugReportDialog(e);
					} catch (IllegalArgumentException e) {
						TraceUtils.trace(TraceUtils.STDERR, e);
						BugReportUtils.showBugReportDialog(e);
					} catch (InvocationTargetException e) {
						TraceUtils.trace(TraceUtils.STDERR, e);
						BugReportUtils.showBugReportDialog(e);
					}
				}
				else if( selectedPanel == tab4 )
				{
					try {
						faqURL = "http://ivpr." + Settings.IWEAVE_HOST + "/faq.php?" + Calendar.getInstance().getTimeInMillis();
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

//		tabbedPane.setEnabledAt(1, false);
		switchToTab(tab1);
		
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
	public Boolean switchToTab(int index)
	{
		try {
			tabbedPane.setSelectedIndex(0);
			tabbedPane.setSelectedIndex(index);
		} catch (IndexOutOfBoundsException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
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
	
	public JPanel createTab1(JComponent parent)
	{
		JPanel panel = createTab(parent);

		refreshButton = new JButton("Refresh");
		refreshButton.setBounds(330, 10, 100, 30);
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
		panel.add(refreshButton);
		
		installButton = new JButton("Install");
		installButton.setBounds(330, 50, 100, 30);
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
				} catch (IOException e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
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
			public void actionPerformed(ActionEvent a) 
			{
				int index = revisionTable.getSelectedIndex();
				if( index < 0 )
					return;
				
				extractBinaries(Revisions.getRevisionsList().get(index));
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
							TraceUtils.trace(TraceUtils.STDERR, e);
						} catch (MalformedURLException e) {
							TraceUtils.trace(TraceUtils.STDERR, e);
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
							TraceUtils.trace(TraceUtils.STDERR, e);
						} catch (MalformedURLException e) {
							TraceUtils.trace(TraceUtils.STDERR, e);
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
		panel.add(revisionTable);
		
		/*
		localhostStatus = new SocketStatus("Visible on your computer:", Settings.LOCALHOST, ConfigManager.getConfigManager().getActiveContainer().getPort());
		localhostStatus.setBounds(10, 270, 400, 20);
		localhostStatus.setOpaque(true);
		localhostStatus.setBackground(Color.WHITE);
		localhostStatus.setVisible(true);
		localhostStatus.startMonitor();
		panel.add(localhostStatus);
		
		lanStatus = new SocketStatus("Visible on your network:", Settings.LOCAL_IP, ConfigManager.getConfigManager().getActiveContainer().getPort());
		lanStatus.setBounds(10, 290, 400, 20);
		lanStatus.setOpaque(true);
		lanStatus.setBackground(Color.WHITE);
		lanStatus.setVisible(true);
		lanStatus.startMonitor();
		panel.add(lanStatus);
		
		internetStatus = new SocketStatus("Visible from the internet:", Settings.REMOTE_IP, ConfigManager.getConfigManager().getActiveContainer().getPort());
		internetStatus.setBounds(10, 310, 400, 20);
		internetStatus.setOpaque(true);
		internetStatus.setBackground(Color.WHITE);
		internetStatus.setVisible(true);
		internetStatus.startMonitor(true);
		panel.add(internetStatus);
		*/
		
		return panel;
	}
	public JPanel createTab2(JComponent parent)
	{
		JPanel panel = createTab(parent);
		
		sessionStateTable = new CustomTable(new String[] {"Name", "Date", "Size"}, new Object[0][3]);
		sessionStateTable.setBounds(0, 0, panel.getWidth() - 10, panel.getHeight() / 2);
		sessionStateTable.setColumnSizes(new int[] { 200, 75, 50 });
		sessionStateTable.addTableMouseListener(new MouseListener() {
			@Override public void mouseReleased(MouseEvent e) { }
			@Override public void mousePressed(MouseEvent e) { }
			@Override public void mouseExited(MouseEvent e) { }
			@Override public void mouseEntered(MouseEvent e) { }
			@Override public void mouseClicked(MouseEvent e) 
			{
				String selectedFile = (String) sessionStateTable.getSelectedRow()[0];
				if( selectedFile == null )
					return;
				
				File WEBAPPS, ROOT, sessionState;
				
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
					
					
					if( e.getClickCount() == 1 )
					{
						ZipFile zip = new ZipFile(sessionState);
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
						zip.close();
						launchSessionState.setEnabled(sessionStateTable.getSelectedIndex() >= 0);
					}
					else if( e.getClickCount() == 2 )
					{
						LaunchUtils.browse("http://" + 
								Settings.LOCALHOST + ":" + 
								ConfigManager.getConfigManager().getActiveContainer().getPort() +
								"/weave.html?file=" + 
								sessionState.getName());
					}
				} catch (NoSuchMethodException e1) {
					TraceUtils.trace(TraceUtils.STDERR, e1);
				} catch (SecurityException e1) {
					TraceUtils.trace(TraceUtils.STDERR, e1);
				} catch (IllegalAccessException e1) {
					TraceUtils.trace(TraceUtils.STDERR, e1);
				} catch (IllegalArgumentException e1) {
					TraceUtils.trace(TraceUtils.STDERR, e1);
				} catch (InvocationTargetException e1) {
					TraceUtils.trace(TraceUtils.STDERR, e1);
				} catch (ZipException e1) {
					sessionLabel.setIcon(null);
				} catch (IOException e1) {
					TraceUtils.trace(TraceUtils.STDERR, e1);
				} catch (URISyntaxException e1) {
					TraceUtils.trace(TraceUtils.STDERR, e1);
				} catch (InterruptedException e1) {
					TraceUtils.trace(TraceUtils.STDERR, e1);
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
							TraceUtils.trace(TraceUtils.STDERR, e);
						} catch (IOException e) {
							TraceUtils.trace(TraceUtils.STDERR, e);
						} catch (InterruptedException e) {
							TraceUtils.trace(TraceUtils.STDERR, e);
						} catch (NoSuchMethodException e) {
							TraceUtils.trace(TraceUtils.STDERR, e);
						} catch (SecurityException e) {
							TraceUtils.trace(TraceUtils.STDERR, e);
						} catch (IllegalAccessException e) {
							TraceUtils.trace(TraceUtils.STDERR, e);
						} catch (IllegalArgumentException e) {
							TraceUtils.trace(TraceUtils.STDERR, e);
						} catch (InvocationTargetException e) {
							TraceUtils.trace(TraceUtils.STDERR, e);
						}
					}
				}
				switchToTab(tab2);
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
			public void actionPerformed(ActionEvent e) 
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
							sessionState.getName());
					
				} catch (NoSuchMethodException e1) {
				} catch (SecurityException e1) {
				} catch (IllegalAccessException e1) {
				} catch (IllegalArgumentException e1) {
				} catch (InvocationTargetException e1) {
				} catch (IOException e1) {
				} catch (URISyntaxException e1) {
				} catch (InterruptedException e1) {
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
					LaunchUtils.openAdminConsole();
				} catch (IOException e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
				} catch (URISyntaxException e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
				} catch (InterruptedException e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
				}
			}
		});
		panel.add(launchAdminConsoleButton);
		
		return panel;
	}
	public JPanel createTab3(JComponent parent)
	{
		JPanel panel = createTab(parent);
		JPanel innerPanel = new JPanel();
		JPanel serverUpdateBox = new JPanel();
		JPanel weaveUpdateBox = new JPanel();
		JPanel maintenanceBox = new JPanel();
		JPanel protoextBox = new JPanel();
		
		innerPanel.setLayout(null);
		innerPanel.setSize(panel.getWidth() - 40, 800);
		innerPanel.setPreferredSize(new Dimension(parent.getWidth() - 40, 800));
		innerPanel.setBackground(Color.WHITE);
		

		////////////////////////////////////////////////////////////////////////////////////////////////
		// Weave Server Assistant Updates
		////////////////////////////////////////////////////////////////////////////////////////////////
		settingsServerUpdatesTitle = BorderFactory.createTitledBorder(null, "Server Assistant Updates", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font(Settings.FONT, Font.BOLD, 14), Color.BLUE);
		serverUpdateBox.setBounds(10, 10, innerPanel.getWidth() - 40, 150);
		serverUpdateBox.setLayout(null);
		serverUpdateBox.setBackground(Color.WHITE);
		serverUpdateBox.setBorder(settingsServerUpdatesTitle);
		
		settingsUpdatesAutoInstallCheckbox = new JCheckBox("Automatically install updates on startup");
		settingsUpdatesAutoInstallCheckbox.setBounds(10, 20, 300, 30);
		settingsUpdatesAutoInstallCheckbox.setBackground(Color.WHITE);
		settingsUpdatesAutoInstallCheckbox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		serverUpdateBox.add(settingsUpdatesAutoInstallCheckbox);
		
		settingsUpdatesCheckNewCheckbox = new JCheckBox("Check for new updates");
		settingsUpdatesCheckNewCheckbox.setBounds(10, 50, 170, 30);
		settingsUpdatesCheckNewCheckbox.setBackground(Color.WHITE);
		settingsUpdatesCheckNewCheckbox.setEnabled(true);
		settingsUpdatesCheckNewCheckbox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				settingsUpdatesCheckNewCombobox.setEnabled(settingsUpdatesCheckNewCheckbox.isEnabled());
			}
		});
		serverUpdateBox.add(settingsUpdatesCheckNewCheckbox);
		
		settingsUpdatesCheckNewCombobox = new JComboBox<String>();
		settingsUpdatesCheckNewCombobox.setBounds(180, 50, 150, 30);
		settingsUpdatesCheckNewCombobox.setBackground(Color.WHITE);
		settingsUpdatesCheckNewCombobox.setEnabled(settingsUpdatesCheckNewCheckbox.isEnabled());
		settingsUpdatesCheckNewCombobox.addItem("Every hour");
		settingsUpdatesCheckNewCombobox.addItem("Every day");
		settingsUpdatesCheckNewCombobox.addItem("Every week");
		settingsUpdatesCheckNewCombobox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		serverUpdateBox.add(settingsUpdatesCheckNewCombobox);
		

		////////////////////////////////////////////////////////////////////////////////////////////////
		// Weave Updates
		////////////////////////////////////////////////////////////////////////////////////////////////
		settingsWeaveUpdatesTitle = BorderFactory.createTitledBorder(null, "Weave Updates", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font(Settings.FONT, Font.BOLD, 14), Color.BLUE);
		weaveUpdateBox.setBounds(10, 170, innerPanel.getWidth() - 40, 150);
		weaveUpdateBox.setLayout(null);
		weaveUpdateBox.setBackground(Color.WHITE);
		weaveUpdateBox.setBorder(settingsWeaveUpdatesTitle);
		
		

		////////////////////////////////////////////////////////////////////////////////////////////////
		// Maintenance
		////////////////////////////////////////////////////////////////////////////////////////////////
		settingsMaintenanceTitle = BorderFactory.createTitledBorder(null, "Maintenance", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font(Settings.FONT, Font.BOLD, 14), Color.BLUE);
		maintenanceBox.setBounds(10, 330, innerPanel.getWidth() - 40, 150);
		maintenanceBox.setLayout(null);
		maintenanceBox.setBackground(Color.WHITE);
		maintenanceBox.setBorder(settingsMaintenanceTitle);
		
		settingsMaintenanceDeleteLogsCheckbox = new JCheckBox("Delete log files older than ");
		settingsMaintenanceDeleteLogsCheckbox.setBounds(10, 22, 180, 25);
		settingsMaintenanceDeleteLogsCheckbox.setBackground(Color.WHITE);
		settingsMaintenanceDeleteLogsCheckbox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		maintenanceBox.add(settingsMaintenanceDeleteLogsCheckbox);
		
		settingsMaintenanceDeleteLogsTextfield = new JTextField();
		settingsMaintenanceDeleteLogsTextfield.setBounds(190, 21, 25, 26);
		settingsMaintenanceDeleteLogsTextfield.setBackground(Color.WHITE);
		settingsMaintenanceDeleteLogsTextfield.setBorder(new LineBorder(Color.BLACK, 1));
		maintenanceBox.add(settingsMaintenanceDeleteLogsTextfield);
		
		maintenanceBox.setComponentZOrder(settingsMaintenanceDeleteLogsTextfield, 0);
		maintenanceBox.setComponentZOrder(settingsMaintenanceDeleteLogsCheckbox, 1);

		

		////////////////////////////////////////////////////////////////////////////////////////////////
		// Protocols & Extensions
		////////////////////////////////////////////////////////////////////////////////////////////////
		
		settingsProtoExtTitle = BorderFactory.createTitledBorder(null, "Protocol & Extension", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font(Settings.FONT, Font.BOLD, 14), Color.GRAY);
		protoextBox.setBounds(10, 490, innerPanel.getWidth() - 40, 150);
		protoextBox.setLayout(null);
		protoextBox.setBackground(Color.WHITE);
		protoextBox.setBorder(settingsProtoExtTitle);
		
		settingsExtCheckbox = new JCheckBox("Enable Weave Extesion");
		settingsExtCheckbox.setBounds(10, 20, 300, 30);
		settingsExtCheckbox.setBackground(Color.WHITE);
		settingsExtCheckbox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Settings.enableWeaveExtension(settingsExtCheckbox.isSelected());
				} catch (IllegalArgumentException e1) {
					TraceUtils.trace(TraceUtils.STDERR, e1);
				} catch (IllegalAccessException e1) {
					TraceUtils.trace(TraceUtils.STDERR, e1);
				} catch (InvocationTargetException e1) {
					TraceUtils.trace(TraceUtils.STDERR, e1);
				}
			}
		});
		protoextBox.add(settingsExtCheckbox);
		
		settingsProtocolCheckbox = new JCheckBox("Enable Weave Protocol");
		settingsProtocolCheckbox.setBounds(10, 50, 300, 30);
		settingsProtocolCheckbox.setBackground(Color.WHITE);
		settingsProtocolCheckbox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Settings.enableWeaveProtocol(settingsProtocolCheckbox.isSelected());
				} catch (IllegalArgumentException e1) {
					TraceUtils.trace(TraceUtils.STDERR, e1);
				} catch (IllegalAccessException e1) {
					TraceUtils.trace(TraceUtils.STDERR, e1);
				} catch (InvocationTargetException e1) {
					TraceUtils.trace(TraceUtils.STDERR, e1);
				}
			}
		});
		protoextBox.add(settingsProtocolCheckbox);
		
		
		innerPanel.add(serverUpdateBox);
		innerPanel.add(weaveUpdateBox);
		innerPanel.add(maintenanceBox);
		innerPanel.add(protoextBox);
		
		
		settingsScrollPane = new JScrollPane();
		settingsScrollPane.setBounds(0, 0, parent.getWidth() - 10, parent.getHeight() - 30);
		settingsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		settingsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		settingsScrollPane.setViewportView(innerPanel);
		settingsScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		settingsScrollPane.setVisible(true);
		
		panel.add(settingsScrollPane);
		
		return panel;
	}
	public JPanel createTab4(JComponent parent)
	{
		JPanel panel = createTab(parent);

		try {
			troubleshootHTML = new JEditorPane();
			troubleshootHTML.setPage(faqURL);
			troubleshootHTML.setBounds(0, 0, panel.getWidth() - 20, panel.getHeight() - 20);
			troubleshootHTML.setBackground(Color.WHITE);
			troubleshootHTML.setEditable(false);
			troubleshootHTML.setVisible(true);
			
			troubleshootScrollPane = new JScrollPane(troubleshootHTML, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			troubleshootScrollPane.setBounds(0, 0, parent.getWidth() - 10, parent.getHeight() - 30);
			troubleshootScrollPane.getVerticalScrollBar().setUnitIncrement(16);
			troubleshootScrollPane.setVisible(true);
		} catch (IOException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		}

		panel.add(troubleshootScrollPane);
		
		return panel;
	}
	public JPanel createTab5(JComponent parent)
	{
		JPanel panel = createTab(parent);
		
		try {
			aboutImage = new JLabel("");
			aboutImage.setBounds(20, 20, 100, 100);
			aboutImage.setIcon(new ImageIcon(ImageUtils.scale(ImageIO.read(IconManager.ICON_TRAY_LOGO_LARGE), aboutImage.getWidth(), ImageUtils.SCALE_WIDTH)));
			
			aboutTitle = new JLabel(Settings.SERVER_NAME);
			aboutTitle.setBounds(150, 30, 300, 30);
			aboutTitle.setFont(new Font(Settings.FONT, Font.BOLD, 18));
			
			aboutVersion = new JLabel(Settings.SERVER_VER);
			aboutVersion.setBounds(150, 60, 300, 30);
			aboutVersion.setFont(new Font(Settings.FONT, Font.PLAIN, 13));
			
			aboutHTML = new JEditorPane();
			aboutHTML.setBounds(20, 130, 400, 200);
			aboutHTML.setBackground(Color.WHITE);
			aboutHTML.setEditable(false);
			aboutHTML.setContentType("text/html");
			aboutHTML.setFont(new Font(Settings.FONT, Font.PLAIN, 10));
			aboutHTML.setText(	"Weave is a <b>We</b>b-based <b>A</b>nalysis and <b>V</b>isualization <b>E</b>nvironment designed to " +
								"enable visualization of any available  data by anyone for any purpose.<br><br><br><br>" +
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
		}
		
		panel.add(aboutImage);
		panel.add(aboutTitle);
		panel.add(aboutVersion);
		panel.add(aboutHTML);
		
		return panel;
	}
	
	private void downloadBinaries() throws InterruptedException, IOException
	{
		// Get the install URL to the zip file
		final URL url;
		final String urlStr = RemoteUtils.getConfigEntry(RemoteUtils.WEAVE_BINARIES_URL);
		if( urlStr == null ) {
			JOptionPane.showConfirmDialog(null, 
					"A connection to the internet could not be established.\n\n" +
					"Please connect to the internet and try again.", 
					"No Connection", 
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
			setButtonsEnabled(true);
			cleanButton.setEnabled(Revisions.getNumberOfRevisions() > Settings.recommendPrune);
			return;
		}
		url = new URL(urlStr);

		// Get the zip file's file name
		String fileName = UpdateUtils.getWeaveUpdateFileName();
		if( fileName == null ) {
			JOptionPane.showConfirmDialog(null,
					"There was an error generating the update package filename.\n\n" +
					"Please try again later or if the problem persists,\n" +
					"report this issue as a bug for the developers.", 
					"Error", 
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
			setButtonsEnabled(true);
			cleanButton.setEnabled(Revisions.getNumberOfRevisions() > Settings.recommendPrune);
			return;
		}
		
		// Get the active servlet container
		IConfig actvContainer = ConfigManager.getConfigManager().getActiveContainer();
		if( actvContainer == null ) {
			JOptionPane.showMessageDialog(null, 
					"There is no active servlet selected.\n\n" + 
					"Please configure a servlet to use, then try again.", "Error", JOptionPane.ERROR_MESSAGE);
			setButtonsEnabled(true);
			cleanButton.setEnabled(Revisions.getNumberOfRevisions() > Settings.recommendPrune);
			return;
		}

		// Get the active servlet container's webapps directory
		File cfgWebapps = actvContainer.getWebappsDirectory();
		if( cfgWebapps == null || !cfgWebapps.exists() ) {
			JOptionPane.showMessageDialog(null, 
					"Webapps folder for " + actvContainer.getConfigName() + " is not set.", "Error", JOptionPane.ERROR_MESSAGE);
			setButtonsEnabled(true);
			cleanButton.setEnabled(Revisions.getNumberOfRevisions() > Settings.recommendPrune);
			return;
		}
		
		final File zipFile = new File(Settings.REVISIONS_DIRECTORY, fileName);
		
		final AsyncObserver observer = new AsyncObserver() {
			@Override
			public void onUpdate() {
				if( info.max == -1 ) {
					// Unknown max size - progress unavailable
					progressbar.setIndeterminate(true);
					downloadLabel.setText( 
							String.format("Downloading update.... %s @ %s",
								FileUtils.sizeify(info.cur), 
								DownloadUtils.speedify(info.speed)) );
				} else {
					// Known max size
					progressbar.setIndeterminate(false);
					progressbar.setValue( info.percent );
					if( info.time > 3600 )
						downloadLabel.setText(
								String.format("Downloading - %d%% - %s - %s (%s)", 
									info.percent, 
									"Calculating ETA...",
									FileUtils.sizeify(info.cur),
									DownloadUtils.speedify(info.speed)) );
					else if( info.time < 60 )
						downloadLabel.setText(
								String.format("Downloading - %d%% - %s - %s (%s)", 
									info.percent, 
									TimeUtils.format("%s s remaining", info.time),
									FileUtils.sizeify(info.cur),
									DownloadUtils.speedify(info.speed)) );
					else
						downloadLabel.setText(
								String.format("Downloading - %d%% - %s - %s (%s)",
									info.percent, 
									TimeUtils.format("%m:%ss remaining", info.time),
									FileUtils.sizeify(info.cur),
									DownloadUtils.speedify(info.speed)) );
				}
			}
		};
		AsyncCallback callback = new AsyncCallback() {
			@Override
			public void run(Object o) {
				int returnCode = (Integer) o;

				Settings.transferCancelled = false;
				Settings.downloadLocked = false;

				switch( returnCode )
				{
					case TransferUtils.COMPLETE:
						TraceUtils.put(TraceUtils.STDOUT, "DONE");
						downloadLabel.setText("Download Complete....");
						downloadLabel.setForeground(Color.BLACK);
	
						extractBinaries(zipFile);
						break;
					case TransferUtils.CANCELLED:
						TraceUtils.put(TraceUtils.STDOUT, "CANCELLED");
						downloadLabel.setText("Cancelling Download....");
						downloadLabel.setForeground(Color.BLACK);
						break;
					case TransferUtils.FAILED:
						TraceUtils.put(TraceUtils.STDOUT, "FAILED");
						downloadLabel.setText("Download Failed....");
						downloadLabel.setForeground(Color.RED);

						try {
							Thread.sleep(2000);
							refreshProgramatically = true;
							refreshInterface();
						} catch (InterruptedException e) {
							TraceUtils.trace(TraceUtils.STDERR, e);
							BugReportUtils.showBugReportDialog(e);
						} catch (MalformedURLException e) {
							TraceUtils.trace(TraceUtils.STDERR, e);
							BugReportUtils.showBugReportDialog(e);
						}
						break;
					case TransferUtils.OFFLINE:
						break;
				}
			}
		};
		AsyncTask task = new AsyncTask() {
			@Override
			public Object doInBackground() {
				Object o = TransferUtils.FAILED;
				try {
					observer.init(url);
					o = DownloadUtils.download(urlStr, zipFile, observer, 4 * TransferUtils.MB);
				} catch (ArithmeticException e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				} catch (IOException e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				} catch (InterruptedException e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				}
				return o;
			}
		};

		if( !Settings.DOWNLOADS_TMP_DIRECTORY.exists() )
			Settings.DOWNLOADS_TMP_DIRECTORY.mkdirs();

		TraceUtils.trace(TraceUtils.STDOUT, "-> Downloading update.............");
		
		downloadLabel.setVisible(true);
		progressbar.setVisible(true);
		
		downloadLabel.setText("Downloading update.....");
		progressbar.setIndeterminate(true);
		
		Thread.sleep(1000);
		
		progressbar.setValue(0);
		progressbar.setIndeterminate(false);

		Settings.downloadLocked = true;
		Settings.transferCancelled = false;
		
		task.addCallback(callback);
		task.execute();
	}
	
	private void extractBinaries(final File zipFile)
	{
		final AsyncObserver observer = new AsyncObserver() {
			@Override
			public void onUpdate() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						progressbar.setValue( info.percent / 2 );
						downloadLabel.setText( 
								String.format(
										"Extracting update.... %d%%", 
										info.percent / 2 ) );
					}
				});
			}
		};
		AsyncCallback callback = new AsyncCallback() {
			@Override
			public void run(Object o) {
				int returnCode = (Integer) o;
				
				switch( returnCode )
				{
					case TransferUtils.COMPLETE:
						TraceUtils.put(TraceUtils.STDOUT, "DONE");
						
						String folderName = Revisions.getRevisionName(zipFile.getAbsolutePath());
						moveBinaries(new File(Settings.UNZIP_DIRECTORY, folderName));
						break;
					case TransferUtils.FAILED:
						break;
					case TransferUtils.CANCELLED:
						break;
					case TransferUtils.OFFLINE:
						break;
				}
			}
		};
		AsyncTask task = new AsyncTask() {
			@Override
			public Object doInBackground() {
				Object o = TransferUtils.FAILED;
				try {
					observer.init(zipFile);
					o = ZipUtils.extract(zipFile, Settings.UNZIP_DIRECTORY, TransferUtils.OVERWRITE | TransferUtils.MULTIPLE_FILES, observer, 8 * TransferUtils.MB);
				} catch (ArithmeticException e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				}catch (NullPointerException e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
					// No bug report
				} catch (ZipException e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				} catch (IOException e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				} catch (InterruptedException e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				}
				return o;
			}
		};

		try {
			if( !Settings.UNZIP_DIRECTORY.exists() )
				Settings.UNZIP_DIRECTORY.mkdirs();
			
			progressbar.setVisible(true);
			downloadLabel.setVisible(true);
			
			progressbar.setIndeterminate(true);
			downloadLabel.setText("Preparing Extraction....");
			Thread.sleep(1000);
			
			TraceUtils.trace(TraceUtils.STDOUT, "-> Extracting update..............");
			
			Settings.canQuit = false;
			
			downloadLabel.setText("Extracting update....");
			progressbar.setIndeterminate(false);
		} catch (InterruptedException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		}
		
		task.addCallback(callback);
		task.execute();
	}
	
	private void moveBinaries(final File unzippedFile)
	{
		final File configWebapps = ConfigManager.getConfigManager().getActiveContainer().getWebappsDirectory();
		
		final AsyncObserver observer = new AsyncObserver() {
			@Override
			public void onUpdate() {
				progressbar.setValue( 50 + info.percent / 2 );
				downloadLabel.setText( 
						String.format(
								"Installing update.... %d%%", 
								50 + info.percent / 2 ) );
			}
		};
		AsyncCallback callback = new AsyncCallback() {
			@Override
			public void run(Object o) {
				int returnCode = (Integer) o;
				
				switch( returnCode ) {
				case TransferUtils.COMPLETE:
					TraceUtils.put(TraceUtils.STDOUT, "DONE");
					downloadLabel.setText("Install complete....");
					
					Settings.canQuit = true;
					System.gc();

					ConfigManager
						.getConfigManager()
						.getActiveContainer()
						.setInstallVersion(Revisions.getRevisionVersion(unzippedFile.getAbsolutePath()));
					ConfigManager.getConfigManager().save();
					
					try {
						Settings.cleanUp();
						Thread.sleep(1000);
						refreshProgramatically = true;
						refreshInterface();
					} catch (InterruptedException e) {
						TraceUtils.trace(TraceUtils.STDERR, e);
					} catch (MalformedURLException e) {
						TraceUtils.trace(TraceUtils.STDERR, e);
					}
					break;
				case TransferUtils.CANCELLED:
					break;
				case TransferUtils.FAILED:
					break;
				case TransferUtils.OFFLINE:
					break;
				}
			}
		};
		AsyncTask task = new AsyncTask() {
			@Override
			public Object doInBackground() {
				int status = TransferUtils.COMPLETE;
				String[] files = unzippedFile.list();
				
				try {
					observer.init(unzippedFile);

					for( String file : files )
					{
						File source = new File(unzippedFile, file);
						File destination = new File(configWebapps, file);
						status &= FileUtils.copy(source, destination, TransferUtils.MULTIPLE_FILES | TransferUtils.OVERWRITE, observer, 8 * TransferUtils.MB);
					}
				} catch (ArithmeticException e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				} catch (FileNotFoundException e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				} catch (IOException e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				} catch (InterruptedException e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				}
				return status;
			}
		};

		TraceUtils.trace(TraceUtils.STDOUT, "-> Installing update..............");

		downloadLabel.setText("Installing Update....");
		progressbar.setIndeterminate(false);
		
		task.addCallback(callback);
		task.execute();
	}
	
	private void refreshInterface() throws InterruptedException, MalformedURLException
	{
		TraceUtils.traceln(TraceUtils.STDOUT, "-> Refreshing User Interface......");

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
		cleanButton.setEnabled(Revisions.getNumberOfRevisions() > Settings.recommendPrune);
		
		// Update revision table data
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
			TraceUtils.trace(TraceUtils.STDERR, e);
		} catch (SecurityException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
		} catch (IllegalAccessException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
		} catch (IllegalArgumentException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
		} catch (InvocationTargetException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
		}
		revisionTable.setData(revisionData);
		revisionTable.refreshTable();
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
