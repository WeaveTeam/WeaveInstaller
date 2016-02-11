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
import static weave.utils.TraceUtils.put;
import static weave.utils.TraceUtils.trace;
import static weave.utils.TraceUtils.traceln;
import static weave.utils.TraceUtils.LEVEL.INFO;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.zip.ZipException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import weave.Settings.LAUNCH_ENUM;
import weave.async.AsyncCallback;
import weave.async.AsyncFunction;
import weave.async.AsyncObserver;
import weave.managers.ResourceManager;
import weave.utils.BugReportUtils;
import weave.utils.DownloadUtils;
import weave.utils.FileUtils;
import weave.utils.IdentityUtils;
import weave.utils.LaunchUtils;
import weave.utils.LegacyUtils;
import weave.utils.RemoteUtils;
import weave.utils.StatsUtils;
import weave.utils.StringUtils;
import weave.utils.TraceUtils.LEVEL;
import weave.utils.TransferUtils;
import weave.utils.UpdateUtils;
import weave.utils.ZipUtils;

import com.jtattoo.plaf.fast.FastLookAndFeel;

@SuppressWarnings("serial")
public class Updater extends JFrame
{
	public static Updater updater 			= null;
	
	private Dimension 		screen 			= Toolkit.getDefaultToolkit().getScreenSize();
	public 	JLabel			staticLabel		= null;
	public 	JLabel			statusLabel 	= null;
	public	JProgressBar 	statusProgress	= null;
	public 	JButton			cancelButton	= null;
	
	private boolean			isUpdate		= false;
	
	private final String _TMP_UPDATE_ZIPFILE_NAME = "updates.zip";
	
	public static void main( String[] args ) 
	{
		try {
			UIManager.setLookAndFeel(FastLookAndFeel.class.getCanonicalName());
		} catch (ClassNotFoundException e) {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException e1) {			trace(STDERR, e1);	BugReportUtils.showBugReportDialog(e);
			} catch (InstantiationException e1) {			trace(STDERR, e1);	BugReportUtils.showBugReportDialog(e);
			} catch (IllegalAccessException e1) {			trace(STDERR, e1);	BugReportUtils.showBugReportDialog(e);
			} catch (UnsupportedLookAndFeelException e1) {	trace(STDERR, e1);	BugReportUtils.showBugReportDialog(e);
			}
		} catch (InstantiationException e) {				trace(STDERR, e);	BugReportUtils.showBugReportDialog(e);
		} catch (IllegalAccessException e) {				trace(STDERR, e);	BugReportUtils.showBugReportDialog(e);
		} catch (UnsupportedLookAndFeelException e) {		trace(STDERR, e);	BugReportUtils.showBugReportDialog(e);
		}

		if( !Desktop.isDesktopSupported() )
		{
			traceln(STDOUT, LEVEL.ERROR, "!! Fault detected !!");
			traceln(STDOUT, LEVEL.ERROR, "!! System does not support Java Desktop Features" );
			Settings.shutdown(ABORT);
			return;
		}
		
		Settings.CURRENT_PROGRAM_NAME = Settings.UPDATER_NAME;
		Settings.init();
		
		if( !Settings.getLock() )
		{
			JOptionPane.showMessageDialog(null, 
					Settings.CURRENT_PROGRAM_NAME + " is already running.\n\n" +
					"Please stop that one before starting another.", 
					"Error", JOptionPane.ERROR_MESSAGE);
			Settings.shutdown(JFrame.ERROR);
		}
		
		// If the new launcher jar file exists in the proper directory
		// just use this as a shortcut and launch that version instead
		if( !(new File(".").getAbsolutePath().equals(Settings.BIN_DIRECTORY.getAbsolutePath())) &&
			 (Settings.binFileExists(Settings.UPDATER_JAR) || Settings.binFileExists(Settings.UPDATER_NEW_JAR)) )
		{
			try {
				LaunchUtils.launchWeaveUpdater();
			} catch (Exception e) {
				trace(STDERR, e);
			}
			Settings.shutdown();
		}
		
		traceln(STDOUT, INFO, "");
		traceln(STDOUT, INFO, "=== " + Settings.CURRENT_PROGRAM_NAME + " Starting Up ===");

		if( !Settings.isOfflineMode() && !RemoteUtils.isConnectedToInternet() )
		{
			if( JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, 
					"It appears you have no connection to the internet.\n" +
					"Would you like to launch in offline mode?", 
					"No Internet Access", 
					JOptionPane.YES_NO_OPTION, 
					JOptionPane.WARNING_MESSAGE ))
			{
				Settings.LAUNCH_MODE = LAUNCH_ENUM.OFFLINE_MODE;
				Settings.save();
				try {
					LaunchUtils.launchWeaveUpdater(1000);
				} catch (Exception e) {
					trace(STDERR, e);
				}
				Settings.shutdown();
			}
			else
				Settings.shutdown(ABORT);
		}
		try {
			updater = new Updater();
		} catch (Exception e) {
			trace(STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		}
	}
	
	public Updater() throws IOException, InterruptedException
	{
		// ======== STRUCTURING ========= //
		setSize(500, 125);
		setResizable(false);
		setLayout(null);
		setTitle(Settings.UPDATER_TITLE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocation(screen.width/2 - getWidth()/2, screen.height/2 - getHeight()/2);
		setIconImage(ImageIO.read(ResourceManager.IMAGE_NULL));
		
		staticLabel = new JLabel("Updating " + Settings.SERVER_NAME + "...");
		staticLabel.setBounds(20, 10, 400, 20);
		staticLabel.setFont(new Font(Settings.FONT, Font.PLAIN, 15));
		staticLabel.setVisible(true);
		add(staticLabel);
		
		statusLabel = new JLabel("");
		statusLabel.setBounds(20, 65, 400, 20);
		statusLabel.setFont(new Font(Settings.FONT, Font.PLAIN, 13));
		statusLabel.setVisible(true);
		statusLabel.setText("Checking for update...");
		statusLabel.setForeground(Color.BLACK);
		add(statusLabel);
		
		statusProgress = new JProgressBar();
		statusProgress.setBounds(20, 35, 450, 20);
		statusProgress.setIndeterminate(true);
		statusProgress.setVisible(true);
		statusProgress.setStringPainted(false);
		statusProgress.setMinimum(0);
		statusProgress.setMaximum(100);
		statusProgress.setString("");
		add(statusProgress);
		
		cancelButton = new JButton("Cancel");
		cancelButton.setBounds(390, 61, 80, 25);
		cancelButton.setToolTipText("Close the installer");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if( Settings.transferLocked )
					Settings.transferCancelled = true;
				else
					Settings.shutdown();
			}
		});
		cancelButton.setVisible(true);
		add(cancelButton);

		addWindowListener(new WindowListener() {
			@Override public void windowClosing(WindowEvent e) 		{ System.out.println("Closing...");	}
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
		
		setVisible(true);
		
		
		//=================================================
		// This is strictly for the offline mode
		//=================================================
		if( Settings.isOfflineMode() )
		{
			setTitle(getTitle() + " [OFFLINE MODE]");
			traceln(STDOUT, INFO, "Offline Mode Enabled...");
			traceln(STDOUT, INFO, "Launching " + Settings.SERVER_NAME + "...");

			staticLabel.setText(Settings.UPDATER_NAME);
			statusLabel.setText("Launching " + Settings.SERVER_NAME + "...");

			Settings.setDirectoryPermissions();
			LaunchUtils.launchWeaveServer(1000);
			
			Settings.shutdown();
		}

		
		//=================================================
		// Everything below this is for online launching
		//=================================================
		traceln(STDOUT, INFO, StringUtils.rpad("Online Mode Enabled", ".", Settings.LOG_PADDING_LENGTH));
		
		
		// Need to check if this tool 
		// has a unique ID assigned to it
		Settings.canQuit = false;
		if( !Settings.hasUniqueID() ) {
			Settings.UNIQUE_ID = IdentityUtils.createID();
			Settings.save();
			traceln(STDOUT, INFO, "Generated new UniqueID: " + Settings.UNIQUE_ID);
		}
		Settings.canQuit = true;

		
		// Check to see if there is an update available and if 
		// we should update at this time
		traceln(STDOUT, INFO, StringUtils.rpad("Checking for updates", ".", Settings.LOG_PADDING_LENGTH));
		isUpdate = UpdateUtils.isServerUpdateAvailable();
		
		if( isUpdate || Settings.UPDATE_OVERRIDE )
		{
			put(STDOUT, "AVAILABLE");
			
			Settings.UPDATE_OVERRIDE = false;
			Settings.save();

			downloadUpdate();
		}
		else
		{
			put(STDOUT, "NONE");
			finish();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void downloadUpdate() throws IOException
	{
		// Get update URL
		final URL url;
		String urlStr = RemoteUtils.getConfigEntry(RemoteUtils.WEAVE_SERVER_UPDATES_URL);
		if( urlStr == null ) {
			JOptionPane.showConfirmDialog(null, 
				"A connection to the internet could not be established.\n\n" +
				"Please connect to the internet and try again.", 
				"No Connection", 
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
			Settings.shutdown(JFrame.NORMAL);
		}
		url = new URL(urlStr);
		
		final File destination = new File(Settings.DOWNLOADS_TMP_DIRECTORY, _TMP_UPDATE_ZIPFILE_NAME);
		if( !Settings.DOWNLOADS_TMP_DIRECTORY.exists() ) 
			Settings.DOWNLOADS_TMP_DIRECTORY.mkdirs();
		
		if( destination.exists() ) 
			destination.delete();
		destination.createNewFile();
		
		final AsyncObserver observer = new AsyncObserver() {
			@Override
			public void onUpdate() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						if( info.max == -1 ) {
							// Unknown max size
							statusProgress.setIndeterminate(true);
							statusLabel.setText( String.format("Downloading update....  %s", FileUtils.sizeify(info.cur)));
						} else {
							// Known max size
							statusProgress.setValue( info.percent );
							statusLabel.setText( String.format("Downloading update....  %d%%", info.percent));
						}
					}
				});
			}
		};
		AsyncCallback callback = new AsyncCallback() {
			@Override
			public void run(Object o) {
				int returnCode = (Integer) o;
				
				Settings.transferCancelled = false;
				Settings.transferLocked = false;
				
				try {
					switch( returnCode )
					{
						case TransferUtils.COMPLETE:
							put(STDOUT, "DONE");
							statusLabel.setText("Download complete....");
							
							StatsUtils.logUpdate();
							Thread.sleep(2000);
							installUpdate(destination);
							break;
						case TransferUtils.CANCELLED:
							put(STDOUT, "CANCELLED");
							statusLabel.setText("Cancelling download...");
							
							if( JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, 
									"Would you like to launch anyway?", "Download Cancelled", 
									JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE))
							{
								Settings.setDirectoryPermissions();
								LaunchUtils.launchWeaveServer();
							}
							Settings.shutdown();
							break;
						case TransferUtils.FAILED:
							put(STDOUT, "FAILED");
							statusLabel.setForeground(Color.RED);
							statusLabel.setText("Download Failed....");
							break;
						case TransferUtils.OFFLINE:
							break;
					}
				} catch (InterruptedException e) {
					trace(STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				} catch (IOException e) {
					trace(STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				}
			}
		};
		AsyncFunction task = new AsyncFunction() {
			@Override
			public Object doInBackground() {
				Object o = TransferUtils.FAILED;
				try {
					observer.init(url);
					o = DownloadUtils.download(url, destination, observer, 500 * DownloadUtils.KB);
				} catch (IOException e) {
					trace(STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				} catch (InterruptedException e) {
					trace(STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				}
				return o;
			}
		};

		trace(STDOUT, INFO, StringUtils.rpad("Downloading update", ".", Settings.LOG_PADDING_LENGTH));
		statusLabel.setText("Downloading update....");
		statusProgress.setIndeterminate(false);
		statusProgress.setValue(0);

		Settings.transferLocked = true;
		Settings.transferCancelled = false;
		
		task.addCallback(callback).call();
	}
	
	@SuppressWarnings("unchecked")
	private void installUpdate(final File zipFile)
	{
		final AsyncObserver observer = new AsyncObserver() {
			@Override
			public void onUpdate() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						statusProgress.setValue( info.percent );
						statusLabel.setText( String.format("Installing update....%d%%", info.percent ) );
					}
				});
			}
		};
		AsyncCallback callback = new AsyncCallback() {
			@Override
			public void run(Object o) {
				statusLabel.setText("Install complete....");
				put(STDOUT, "DONE");
				finish();
			}
		};
		AsyncFunction task = new AsyncFunction() {
			@Override
			public Object doInBackground() {
				Object o = TransferUtils.FAILED;
				try {
					observer.init(zipFile);
					o = ZipUtils.extract(zipFile, Settings.WEAVE_ROOT_DIRECTORY, ZipUtils.OVERWRITE, observer);
				} catch (ZipException e) {
					trace(STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				} catch (IOException e) {
					trace(STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				} catch (InterruptedException e) {
					trace(STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				}
				return o;
			}
		};

		trace(STDOUT, INFO, StringUtils.rpad("Installing update", ".", Settings.LOG_PADDING_LENGTH));
		
		Settings.canQuit = false;
		statusProgress.setIndeterminate(false);
		
		task.addCallback(callback).call();
	}
	
	private void finish()
	{
		try {
			System.gc();
			Settings.canQuit = true;
			Thread.sleep(2000);
			
			StatsUtils.noop();
			Settings.setDirectoryPermissions();
			LegacyUtils.moveV1toV2();

			Thread.sleep(500);
			
			traceln(STDOUT, INFO, "Launching " + Settings.SERVER_NAME);
			statusLabel.setText("Launching " + Settings.SERVER_NAME + "...");
	
			while( !Settings.canQuit ) Thread.sleep(1000);
	
			LaunchUtils.launchWeaveServer(1000);
		} catch (Exception e) {
			trace(STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		}

		Settings.shutdown();
	}
}
