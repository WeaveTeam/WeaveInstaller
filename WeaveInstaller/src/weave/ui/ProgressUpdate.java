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
import weave.Settings.MSI_TYPE;
import weave.plugins.MySQL;
import weave.plugins.Tomcat;
import weave.utils.DownloadUtils;
import weave.utils.FileUtils;
import weave.WeaveInstaller;

@SuppressWarnings("serial")
public class ProgressUpdate extends JPanel 
{
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
	
	/**
	 * Sets up the progress bar and adds it to the panel.
	 */
	public ProgressUpdate()
	{
		setLayout(null);
		setBackground(new Color(0xFFFFFF));
		
		progBar = new JProgressBar(0, 100);
		
		add(progBar);
	}
	
	public String setZipSpeed(String s)
	{
		firePropertyChange(ZIP_SPEED, zipInfo.strSpeed, s);
		zipInfo.strSpeed = s;
		return s;
	}
	public String setZipPercent(String s)
	{
		firePropertyChange(ZIP_PERCENT, zipInfo.strPercent, s);
		zipInfo.strPercent = s;
		return s;
	}
	public String setZipTimeleft(String s)
	{
		firePropertyChange(ZIP_TIMELEFT, zipInfo.strTimeleft, s);
		zipInfo.strTimeleft = s;
		return s;
	}
	public String setZipSizeDownloaded(String s)
	{
		firePropertyChange(ZIP_SIZEDOWNLOADED, zipInfo.strSizeDownloaded, s);
		zipInfo.strSizeDownloaded = s;
		return s;
	}
	public String setZipTotalSize(String s)
	{
		firePropertyChange(ZIP_TOTALSIZE, zipInfo.strTotalSize, s);
		zipInfo.strTotalSize = s;
		return s;
	}
	public String setMSISpeed(String s)
	{
		firePropertyChange(MSI_SPEED, msiInfo.strSpeed, s);
		msiInfo.strSpeed = s;
		return s;
	}
	public String setMSIPercent(String s)
	{
		firePropertyChange(MSI_PERCENT, msiInfo.strPercent, s);
		msiInfo.strPercent = s;
		return s;
	}
	public String setMSITimeleft(String s)
	{
		firePropertyChange(MSI_TIMELEFT, msiInfo.strTimeleft, s);
		msiInfo.strTimeleft = s;
		return s;
	}
	public String setMSISizeDownloaded(String s)
	{
		firePropertyChange(MSI_SIZEDOWNLOADED, msiInfo.strSizeDownloaded, s);
		msiInfo.strSizeDownloaded = s;
		return s;
	}
	public String setMSITotalSize(String s)
	{
		firePropertyChange(MSI_TOTALSIZE, msiInfo.strTotalSize, s);
		msiInfo.strTotalSize = s;
		return s;
	}
	@Override 
	public void setBounds(int x, int y, int width, int height)
	{
		super.setBounds(x, y, width, height);

		progBar.setBounds(0, 0, width, height);
	}
	
	/** downloadZip( final JButton button )
	 * 
	 * Opens a connection and downloads the zip file.
	 * 
	 * @param button	JButton passed by the WeaveUpdater as the checkButton.
	 */
	public void downloadZip(final JButton button) 
	{
		URL url;
		try {
			/* Initialization */
			progBar.setStringPainted(true);
			url = new URL(Settings.BINARIES_UPDATE_URL);
			final URLConnection conn = url.openConnection();
			final InputStream in = conn.getInputStream();
			zipInfo = new DownloadInfo();
			int updateAvailable = Revisions.checkForUpdates(false);
			String urlFileName = conn.getHeaderField("Content-Disposition");
			
			/* Check for 'No internet connection' ( code -2 ) returned by Revisions.checkForUpdates( false ) */
			if( updateAvailable == -2 ) {
				progBar.setValue(0);
				progBar.setIndeterminate(false);
				progBar.setString("No Internet Connection");
				
				button.setEnabled(true);
				return;
			/* Check for another error ( code -1 ) returned by Revisions.checkForUpdates( false ) */
			} else if( updateAvailable == -1 ) {
				progBar.setValue(100);
				progBar.setIndeterminate(false);
				progBar.setString("Error Downloading");
				
				button.setEnabled(true);
				return;
			/* Code 0 indicates successful return with no updates available */
			} else if( updateAvailable == 0 ) {
				progBar.setValue(100);
				progBar.setIndeterminate(false);
				progBar.setString("No Updates");
				
				button.setEnabled(true);
				return;
			}

			/* Else there is an available update and we have chosen to install it */
			int pos = urlFileName.indexOf("filename=");
			final String updateFileName = urlFileName.substring(pos+9);
			final File updateFile = new File(Settings.REVISIONS_DIRECTORY, updateFileName);
			System.out.println(Settings.REVISIONS_DIRECTORY.getAbsolutePath() + "/" + updateFileName);
			
			/* Create directories if they do not exist */
			if( !Settings.WEAVE_ROOT_DIRECTORY.exists() )	Settings.WEAVE_ROOT_DIRECTORY.mkdirs();
			if( !Settings.REVISIONS_DIRECTORY.exists() ) 		Settings.REVISIONS_DIRECTORY.mkdirs();
			
			updateFile.createNewFile();

			/* Update GUI info containing download information in a concurrent thread */
			Thread t = new Thread(new Runnable() {
				int size = conn.getContentLength();
				FileOutputStream out = new FileOutputStream(updateFileName);
				
				byte[] b = new byte[1024*4];
				int count, total = 0, kbps = 0, seconds = 0, aveDownSpeed = 0, timeleft = 0;
				long newLong = 0;
				long oldLong = System.currentTimeMillis();
				
				@Override
				public void run() {
					try {
						WeaveInstaller.installer.cancelButton.setEnabled( false ) ;
						WeaveInstaller.installer.postSP.zipLabelSpeed.setVisible(true);
						WeaveInstaller.installer.postSP.zipLabelTimeleft.setVisible(true);
						
						setZipTotalSize(FileUtils.sizeify(size));

						progBar.setIndeterminate(true);
						progBar.setString("Preparing Download...");
						Thread.sleep(2000);
						progBar.setIndeterminate(false);

						while ((count = in.read(b)) > 0)
						{
							out.write(b, 0, count);
							total += count;
							kbps += (count/1024);
							zipInfo.percent = ((total/(size+0.0))*100);
							newLong = System.currentTimeMillis();
							if( (newLong - oldLong) > 1000 )
							{
								zipInfo.speed = kbps;
								kbps = 0;
								seconds++;
								oldLong = newLong;
								aveDownSpeed = (total/1024)/seconds;

								setZipSizeDownloaded(FileUtils.sizeify(total));
							}
							timeleft = ((size-total)/aveDownSpeed)/1024;
							progBar.setValue(Integer.parseInt(String.format("%.0f", zipInfo.percent)));
							progBar.setString(setZipPercent(String.format("%.0f", zipInfo.percent))+"%");

							if( zipInfo.speed > 1024 )
								setZipSpeed(String.format("%.1f", zipInfo.speed/(1024+0.0))+" MB/s");
							else
								setZipSpeed(String.format("%d", zipInfo.speed)+" KB/s");
							
							if( timeleft > 60 ) {
								int t = timeleft / 60;
								if( t == 1 )
									setZipTimeleft(String.format("%d minute remaining", t));
								else
									setZipTimeleft(String.format("%d minutes remaining", t));
							}
							else
								setZipTimeleft(String.format("%d second(s) remaining", timeleft));
							
						}
						out.flush();
						progBar.setValue(100);
						progBar.setIndeterminate(true);
						progBar.setString("Download Finished");
						out.close();
						in.close();
						Thread.sleep(3000);
						WeaveInstaller.installer.postSP.zipLabelSpeedHolder.setText("");
						WeaveInstaller.installer.postSP.zipLabelTimeleftHolder.setText("");
						WeaveInstaller.installer.postSP.zipLabelSizeDownloadHolder.setText("");
						WeaveInstaller.installer.postSP.zipLabelSpeed.setVisible(false);
						WeaveInstaller.installer.postSP.zipLabelTimeleft.setVisible(false);
						/* Extract the ZIP */
						Revisions.extractZip(updateFileName, progBar, button);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			t.start();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Finds a mirror with the best ping, downloads an executes an MSI/Executable
	 * 
	 * @param currentPanel	The panel to modify with the 'cancel download' button
	 * @param button		Download button
	 * @param downloadMSI	ENUM that determines whether we are downloading Tomcat or MySQL
	 */
	public int downloadMSI( final JPanel currentPanel, final JButton button, final Settings.MSI_TYPE downloadMSI ) 
	{
		URL url;
		final JButton cancelInstall = new JButton( "Cancel" ) ;
		cancelInstall.setBounds(5, 115, 100, 20) ;
		cancelInstall.setVisible( false ) ;
		cancelInstall.setEnabled( false ) ;
		currentPanel.add(cancelInstall) ;
		try {
			/* Initialization */
			
			progBar.setStringPainted(true);
			
			//Check for internet conection
			if( !Settings.isConnectedToInternet() ) {
				progBar.setValue(0);
				progBar.setIndeterminate(false);
				progBar.setString("No Internet Connection");
				button.setEnabled(true);
				WeaveInstaller.installer.nextButton.setEnabled(true);
				WeaveInstaller.installer.backButton.setEnabled(true);
				WeaveInstaller.installer.cancelButton.setEnabled(true);
				if( downloadMSI == MSI_TYPE.TOMCAT_MSI ){
					WeaveInstaller.installer.curSP.installTomcat.setVisible( true ) ;
					if( Tomcat.getConfig().TOMCAT_INSTALL_FILE.exists() )
						WeaveInstaller.installer.curSP.installTomcat.setEnabled( true ) ;
				} else if( downloadMSI == MSI_TYPE.MySQL_MSI ){
					if( MySQL.getConfig().MYSQL_INSTALL_FILE.exists() )
						WeaveInstaller.installer.curSP.installMySQL.setEnabled( true ) ;
					WeaveInstaller.installer.curSP.installMySQL.setVisible( true ) ;
				}
				
				currentPanel.remove(cancelInstall) ;
				return -1 ;
			}
			
			//Determine download URL, checks the same condition like 4 times; probably could
			//merge them all into one and optimize but :/
			if( downloadMSI == Settings.MSI_TYPE.TOMCAT_MSI ) {
				while( Tomcat.getConfig().TOMCAT_URL == null ) ;
				url = new URL(Tomcat.getConfig().TOMCAT_URL);
			} else if( downloadMSI == Settings.MSI_TYPE.MySQL_MSI ) {
				while( MySQL.getConfig().MYSQL_URL == null ) ;
				url = new URL(MySQL.getConfig().MYSQL_URL);
			} else {
				return -1 ;
			}
			
			final URLConnection conn = url.openConnection();
			final InputStream in = conn.getInputStream();
			msiInfo = new DownloadInfo();
			final String updateFileName ;

			/* Check for TOMCAT vs. MySQL download */
			if( downloadMSI == Settings.MSI_TYPE.TOMCAT_MSI ) {
				updateFileName = Tomcat.getConfig().TOMCAT_INSTALL_FILE.getPath();
			} else if( downloadMSI == Settings.MSI_TYPE.MySQL_MSI ) {
				updateFileName = MySQL.getConfig().MYSQL_INSTALL_FILE.getPath();
			} else {
				return -1 ;
			}
			
			final File updateFile = new File(updateFileName);
			System.out.println(updateFileName);
			
			/* Create directories if they do not exist */
			if( !Settings.WEAVE_ROOT_DIRECTORY.exists() )	Settings.WEAVE_ROOT_DIRECTORY.mkdirs();
			
			cancelInstall.addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent arg0) 
				{
					cancelInstall.setEnabled( false ) ;
					cancelInstall.setVisible( false ) ;
					
					try {
						msiInfo.cancelFlag = 1 ;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}) ;
			
			updateFile.createNewFile();

			/* Update GUI info containing download information in a concurrent thread */
			final Thread t = new Thread(new Runnable() {
				int size = conn.getContentLength();
				FileOutputStream out = new FileOutputStream(updateFileName);
				
				byte[] b = new byte[1024*4];
				int count, total = 0, kbps = 0, seconds = 0, aveDownSpeed = 0, timeleft = 0;
				long newLong = 0;
				long oldLong = System.currentTimeMillis();
				
				@Override
				public void run() {
					try {
						progBar.setIndeterminate(true);
						progBar.setString("Preparing Download...");
						Thread.sleep(2000);
						progBar.setIndeterminate(false);
						cancelInstall.setVisible( true );
						cancelInstall.setEnabled( true );
						msiInfo.cancelFlag = 0 ;

						setMSITotalSize(FileUtils.sizeify(size));
						
						while ((count = in.read(b)) > 0)
						{
							if( msiInfo.cancelFlag == 1 )
								break ;
							
							out.write(b, 0, count);
							total += count;
							kbps += (count/1024);
							msiInfo.percent = ((total/(size+0.0))*100);
							newLong = System.currentTimeMillis();
							if( (newLong - oldLong) > 1000 )
							{
								msiInfo.speed = kbps;
//								chart.push( ((downloadMSI == MSI_TYPE.MySQL_MSI) ? "MYSQL" : "TOMCAT"), kbps);
								kbps = 0;
								seconds++;
								oldLong = newLong;
								aveDownSpeed = (total/1024)/seconds;
								
								setMSISizeDownloaded(FileUtils.sizeify(total));
							}
							timeleft = ((size-total)/aveDownSpeed)/1024;
							progBar.setValue(Integer.parseInt(String.format("%.0f", msiInfo.percent)));
							progBar.setString(setMSIPercent(String.format("%.0f", msiInfo.percent))+"%");
							
							setMSISpeed(DownloadUtils.speedify(msiInfo.speed));
							
							if( timeleft > 60 ) {
								int t = timeleft / 60;
								if( t == 1 )
									setMSITimeleft(String.format("%d minute", t));
								else
									setMSITimeleft(String.format("%d minutes", t));
							}
							else
								setMSITimeleft(String.format("%d second(s)", timeleft));
								
						}
						if( msiInfo.cancelFlag == 0 ){
							//download completed
							out.flush();
							progBar.setValue(100);
							progBar.setIndeterminate(true);
							cancelInstall.setEnabled( false ) ;
							cancelInstall.setVisible( false ) ;
							Thread.sleep(1500);
							out.close();
							in.close();
							progBar.setString("Download Finished");
							progBar.setValue(100);
							progBar.setIndeterminate(false);
							if( downloadMSI == Settings.MSI_TYPE.TOMCAT_MSI ) {
								WeaveInstaller.installer.curSP.installTomcat.setEnabled( true ) ;
								WeaveInstaller.installer.curSP.installTomcat.setVisible( true ) ;
							}else if( downloadMSI == Settings.MSI_TYPE.MySQL_MSI ){
								WeaveInstaller.installer.curSP.installMySQL.setEnabled( true ) ;
								WeaveInstaller.installer.curSP.installMySQL.setVisible( true ) ;
							}
						}else{
							//download was cancelled
							out.flush();
							progBar.setValue(0);
							progBar.setIndeterminate(true);
							progBar.setString("Removing Local Files...");
							Thread.sleep(1500);
							out.close();
							in.close();
							FileUtils.recursiveDelete( updateFile );
							Thread.sleep(1000) ;
							progBar.setIndeterminate(false);
							progBar.setString("Download Cancelled") ;
							
							if( downloadMSI == Settings.MSI_TYPE.TOMCAT_MSI ) {
								WeaveInstaller.installer.curSP.installTomcat.setEnabled( false ) ;
								WeaveInstaller.installer.curSP.installTomcat.setVisible( true ) ;
							}else if( downloadMSI == Settings.MSI_TYPE.MySQL_MSI ){
								WeaveInstaller.installer.curSP.installMySQL.setEnabled( false ) ;
								WeaveInstaller.installer.curSP.installMySQL.setVisible( true ) ;
							}
						}
						
						if( downloadMSI == MSI_TYPE.TOMCAT_MSI ) {
							WeaveInstaller.installer.curSP.tomcatLabelSizeDownloadHolder.setText("");
							WeaveInstaller.installer.curSP.tomcatLabelSpeedHolder.setText("");
							WeaveInstaller.installer.curSP.tomcatLabelTimeleftHolder.setText("");
						} else if( downloadMSI == MSI_TYPE.MySQL_MSI ) {
							WeaveInstaller.installer.curSP.mysqlLabelSizeDownloadHolder.setText("");
							WeaveInstaller.installer.curSP.mysqlLabelSpeedHolder.setText("");
							WeaveInstaller.installer.curSP.mysqlLabelTimeleftHolder.setText("");
						}
						
						button.setEnabled( true ) ;
						WeaveInstaller.installer.nextButton.setEnabled( true ) ;
						WeaveInstaller.installer.backButton.setEnabled( true ) ;
						WeaveInstaller.installer.cancelButton.setEnabled( true ) ;
						currentPanel.remove(cancelInstall) ;
					} catch (Exception e) {
						e.printStackTrace() ;
					}
				}
			});
			t.start();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return 0 ;
	}
	
	public int runExecutable( File file )
	{
		if( file.exists() ) 
		{
			try
			{
				Desktop.getDesktop().open(file);
			} catch( Exception e ) {
					e.printStackTrace();
			}
			return 0;
		}
		return -1 ;
	}
	
	public static class DownloadInfo
	{
		int speed = 0;
		double percent = 0;
		
		String strSpeed = "";
		String strPercent = "";
		String strTimeleft = "";
		String strSizeDownloaded = "";
		String strTotalSize = "";
		
		int cancelFlag = 0;
	}
}
