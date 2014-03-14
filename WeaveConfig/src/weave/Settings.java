/*
    Weave (Web-based Analysis and Visualization Environment)
    Copyright (C) 2008-2014 University of Massachusetts Lowell

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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.jimmc.jshortcut.JShellLink;

import weave.plugins.IPlugin;
import weave.utils.BugReportUtils;
import weave.utils.FileUtils;
import weave.utils.ProcessUtils;
import weave.utils.RegistryUtils;
import weave.utils.RemoteUtils;
import weave.utils.TraceUtils;

public class Settings 
{

	public static final String PROJECT_NAME				= "Weave";
	
	
	
	/*
	 * Remote API URLs for POST methods
	 */
	public static final String OICWEAVE_HOST			= "oicweave.org";
	public static final String OICWEAVE_URL				= "http://" + OICWEAVE_HOST + "/";
	public static final String UPDATE_CONFIG			= OICWEAVE_URL + ".weave/config.txt";
	public static final String UPDATE_FILES				= OICWEAVE_URL + ".weave/files.txt";
	
	public static final String API_GET_IP				= OICWEAVE_URL + "api/ip.php";
	public static final String API_STATS_LOG			= OICWEAVE_URL + "api/log.php";
	public static final String API_STATS_LIVE			= OICWEAVE_URL + "api/noop.php";
	public static final String API_BUG_REPORT			= OICWEAVE_URL + "api/bug_report.php";

	
	/*
	 * Weave Installer
	 */
	public static final String INSTALLER_NAME			= PROJECT_NAME + " Installer";
	public static final String INSTALLER_VER			= "1.1.0 R2";
	public static final String INSTALLER_TITLE 			= INSTALLER_NAME + " v" + INSTALLER_VER;
	public static final String WEAVEINSTALLER_JAR		= "Installer.jar";
	
	/*
	 * Weave Updater
	 */
	public static final String UPDATER_NAME				= PROJECT_NAME + " Updater";
	public static final String UPDATER_VER				= "1.0.1";
	public static final String UPDATER_TITLE			= UPDATER_NAME + " v" + UPDATER_VER;
	public static final String WEAVEUPDATER_JAR			= "Updater.jar";
	public static final String WEAVEUDPATER_NEW_JAR		= "Updater_new.jar";
	
	/*
	 * Weave Launcher
	 */
	public static final String LAUNCHER_NAME			= PROJECT_NAME + " Launcher";
	public static final String LAUNCHER_VER				= "1.0.0";
	public static final String LAUNCHER_JAR				= "Launcher.jar";
	
	/*
	 * OS specific settings
	 */
	public static final String USER_HOME				= System.getProperty("user.home");
	public static final String N_L						= System.getProperty("line.separator");
	public static final String F_S						= System.getProperty("file.separator");
	public static final String EXACT_OS					= System.getProperty("os.name");
	
	/*
	 * File directory structure
	 */
	public static File APPDATA_DIRECTORY				= null;
	public static File WEAVE_ROOT_DIRECTORY 			= null;
	public static File DOWNLOADS_DIRECTORY				= null;
	public static File DOWNLOADS_TMP_DIRECTORY			= null;
	public static File BIN_DIRECTORY					= null;
	public static File LOGS_DIRECTORY					= null;
	public static File EXE_DIRECTORY					= null;
	public static File REVISIONS_DIRECTORY 				= null;
	public static File UNZIP_DIRECTORY 					= null;
	public static File DOWNLOADS_PLUGINS_DIRECTORY		= null;
	public static File DEPLOYED_PLUGINS_DIRECTORY		= null;
	public static File SETTINGS_FILE 					= null;
	public static File PLUGINS_FILE						= null;
	public static File LOCK_FILE						= null;
	public static File ICON_FILE						= null;
	public static File DESKTOP_DIRECTORY				= null;
	
	public static IPlugin ACTIVE_CONTAINER_PLUGIN= null;
	public static IPlugin ACTIVE_DATABASE_PLUGIN	= null;
	
	/*
	 * Operating System
	 */
	public enum OS_TYPE 								{ WINDOWS, LINUX, MAC, UNKNOWN };
	public static OS_TYPE OS 							= OS_TYPE.UNKNOWN;
	
	/*
	 * Settings File
	 */
	private static Map<String, Object> settings 		= null;

	public static enum UPDATE_TYPE						{ START, DAY, WEEK, NEVER };
	public static UPDATE_TYPE UPDATE_FREQ				= UPDATE_TYPE.START;
	
	public static String UNIQUE_ID						= "";
	public static String LAST_UPDATE_CHECK 				= "Never";
	public static String CURRENT_INSTALL_VER 			= "";
	public static String SHORTCUT_VER					= "0";

	/*
	 * Networking
	 */
	public static enum MODE								{ ONLINE_MODE, OFFLINE_MODE };
	public static MODE LAUNCH_MODE						= MODE.ONLINE_MODE;
	public static String REMOTE_IP						= "";
	public static String LOCAL_IP						= "";
	public static String LOCALHOST						= "";
	
	/*
	 * Misc
	 */
	public static final String BINARIES_UPDATE_URL 		= "https://github.com/IVPR/Weave-Binaries/zipball/master";
	public static final String WIKI_HELP_PAGE			= "http://info.oicweave.org/projects/weave/wiki/Installer";
	
	public static boolean canQuit						= true;
	public static boolean downloadCanceled				= false;
	public static boolean downloadLocked				= false;
	public static boolean isConnectedToInternet			= true;

	public static 		String CURRENT_PROGRAM_NAME		= "";
	public static final String FONT						= "Arial";
	public static boolean INSTALLER_POPUP_SHOWN			= false;
	public static int recommendPrune					= 6;
	
	
	/**
	 * This function must be called at the beginning of an executable
	 * to ensure that all values are properly set
	 */
	public static void init()
	{
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		findOS();
		createFS();
		
		// Read all settings
		load();
		// Overwrite settings file with loaded settings,
		// and any new settings with the default values.
		save();		 

		getNetworkInfo(( isOfflineMode() || !isConnectedToInternet() ));
	}
	
	/**
	 * Check to see if the file that holds all the settings exists in the file structure.
	 * 
	 * @return <code>true</code> if settings file exists, <code>false</code> otherwise
	 */
	public static Boolean settingsFileExists()
	{
		return SETTINGS_FILE.exists();
	}
	
	/**
	 * Check to see if the file that holds all of the plugin settings exists in the file structure.
	 * 
	 * @return <code>true</code> if plugins file exists, <code>false</code> otherwise
	 */
	public static Boolean pluginsFileExists()
	{
		return PLUGINS_FILE.exists();
	}
	
	/**
	 * Check to see if a unique identifier has been assigned to the tool yet.
	 * 
	 * @return <code>true</code> if the tool has a unique identifier, <code>false</code> otherwise 
	 */
	public static Boolean hasUniqueID()
	{
		return !UNIQUE_ID.equals("");
	}
	
	/**
	 * Check if the tool is to launch in offline mode.
	 * This will provide limited functionality to the tool,
	 * any online required feature will be disabled.
	 * 
	 * @return <code>true</code> of launching in offline mode
	 */
	public static Boolean isOfflineMode()
	{
		return (LAUNCH_MODE == MODE.OFFLINE_MODE);
	}
	
	/**
	 * Write the current values of data members to the settings map.
	 *
	 * @return <code>true</code> if successful write, <code>false</code> otherwise
	 * @see load()
	 */
	public static Boolean save()
	{
		try {
			TraceUtils.trace(TraceUtils.STDOUT, "-> Saving settings file...........");
			
			if( !WEAVE_ROOT_DIRECTORY.exists() )
				WEAVE_ROOT_DIRECTORY.mkdirs();
			if( !SETTINGS_FILE.exists() )
				SETTINGS_FILE.createNewFile();
			
			settings = new HashMap<String, Object>();
			settings.put("UNIQUE_ID", UNIQUE_ID);
			settings.put("LAST_UPDATE_CHECK", LAST_UPDATE_CHECK);
			settings.put("CURRENT_INSTALL_VER", CURRENT_INSTALL_VER);
			settings.put("SHORTCUT_VER", SHORTCUT_VER);
			settings.put("UPDATE_FREQ", UPDATE_FREQ);
			settings.put("LAUNCH_MODE", LAUNCH_MODE);
			
			FileOutputStream fout = new FileOutputStream(SETTINGS_FILE);
			ObjectOutputStream outstream = new ObjectOutputStream(fout);
			outstream.writeObject(settings);
			outstream.close();
			TraceUtils.put(TraceUtils.STDOUT, "DONE");
		} catch (IOException e) {
			TraceUtils.put(TraceUtils.STDOUT, "FAILED");
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
			return false;
		}
		return true;
	}
	
	
	/**
	 * Set all the data members to the settings retrieved from the SETTINGS_FILE
	 * and print them to the console if a read was successful.
	 * 
	 * @return <code>true</code> if successful read, <code>false</code> otherwise
	 * @see save()
	 */
	@SuppressWarnings("unchecked")
	public static Boolean load()
	{
		try {Thread.sleep(1000);} catch (InterruptedException e1) {	TraceUtils.trace(TraceUtils.STDERR, e1); }
		
		if( !settingsFileExists() ) return false;
		
		try {
			TraceUtils.trace(TraceUtils.STDOUT, "-> Loading settings file..........");
			
			FileInputStream fin = new FileInputStream(SETTINGS_FILE);
			ObjectInputStream instream = new ObjectInputStream(fin);
			settings = (Map<String, Object>) instream.readObject();
			instream.close();
			
			/* Obtain the map values and assign them to data members */
			UNIQUE_ID = 			(String)		settings.get("UNIQUE_ID");
			LAST_UPDATE_CHECK = 	(String) 		settings.get("LAST_UPDATE_CHECK");
			CURRENT_INSTALL_VER = 	(String)  		settings.get("CURRENT_INSTALL_VER");
			SHORTCUT_VER = 			(String)		settings.get("SHORTCUT_VER");
			UPDATE_FREQ = 			(UPDATE_TYPE)	settings.get("UPDATE_FREQ");
			LAUNCH_MODE = 			(MODE)			settings.get("LAUNCH_MODE");
			

//			TraceUtils.trace(TraceUtils.STDOUT, "\tUNIQUE_ID: " + UNIQUE_ID);
//			TraceUtils.trace(TraceUtils.STDOUT, "\tLAST_UPDATE_CHECK: " + LAST_UPDATE_CHECK);
//			TraceUtils.trace(TraceUtils.STDOUT, "\tCURRENT_INSTALL_VER: " + CURRENT_INSTALL_VER);
//			TraceUtils.trace(TraceUtils.STDOUT, "\tSHORTCUT_VER: " + SHORTCUT_VER);
//			TraceUtils.trace(TraceUtils.STDOUT, "\tUPDATE_FREQ: " + UPDATE_FREQ);

			fin.close();
		} catch (FileNotFoundException e) {
			TraceUtils.put(TraceUtils.STDOUT, "FAILED");
			TraceUtils.trace(TraceUtils.STDERR, e);
			JOptionPane.showMessageDialog(null, "Error reading settings file: File not found", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		} catch (IOException e) {
			TraceUtils.put(TraceUtils.STDOUT, "FAILED");
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
			return false;
		} catch (ClassNotFoundException e) {
			TraceUtils.put(TraceUtils.STDOUT, "FAILED");
			TraceUtils.trace(TraceUtils.STDERR, e);
			BugReportUtils.showBugReportDialog(e);
			return false;
		}
		TraceUtils.put(TraceUtils.STDOUT, "DONE");
		return true;
	}
	
	
	/**
	 * Create appropriate file pointers to directories in the system 
	 * corresponding to the install paths depending on the OS.
	 */
	public static void createFS()
	{
		if( OS == OS_TYPE.WINDOWS )
			APPDATA_DIRECTORY 	= new File(System.getenv("APPDATA"));
		else if( OS == OS_TYPE.LINUX )
			APPDATA_DIRECTORY 	= new File(USER_HOME + F_S +".config" + F_S);
		else if( OS == OS_TYPE.MAC )
			APPDATA_DIRECTORY 	= new File(USER_HOME + F_S + "Library" + F_S + "Application Support" + F_S);
		else
		{
			JOptionPane.showConfirmDialog(null, "You have an unknown Operating System\n" +
												"that is not supported by this program.", 
												"Error",
												JOptionPane.OK_CANCEL_OPTION,
												JOptionPane.ERROR_MESSAGE);
			System.exit(1);
			return;
		}
		
		WEAVE_ROOT_DIRECTORY		= new File(APPDATA_DIRECTORY, 		F_S + ".weave"		 		+ F_S);
		BIN_DIRECTORY				= new File(WEAVE_ROOT_DIRECTORY, 	F_S + "bin" 				+ F_S);
		SETTINGS_FILE 				= new File(BIN_DIRECTORY, 			F_S + "configuration.settings"	 );
		PLUGINS_FILE				= new File(BIN_DIRECTORY, 			F_S + "plugins.settings"		 );
		LOCK_FILE					= new File(BIN_DIRECTORY,			F_S + ".lock"					 );
		ICON_FILE					= new File(BIN_DIRECTORY,			F_S + "icon.ico"				 );
		LOGS_DIRECTORY				= new File(WEAVE_ROOT_DIRECTORY,	F_S + "logs" 				+ F_S);
		DOWNLOADS_DIRECTORY 		= new File(WEAVE_ROOT_DIRECTORY, 	F_S + "downloads" 			+ F_S);
		DOWNLOADS_TMP_DIRECTORY		= new File(DOWNLOADS_DIRECTORY, 	F_S + "tmp" 				+ F_S);
		EXE_DIRECTORY				= new File(DOWNLOADS_DIRECTORY, 	F_S + "exe" 				+ F_S);
		DOWNLOADS_PLUGINS_DIRECTORY	= new File(DOWNLOADS_DIRECTORY, 	F_S + "plugins" 			+ F_S);
		DEPLOYED_PLUGINS_DIRECTORY	= new File(WEAVE_ROOT_DIRECTORY, 	F_S + "plugins" 			+ F_S);
		REVISIONS_DIRECTORY 		= new File(WEAVE_ROOT_DIRECTORY, 	F_S + "revisions" 			+ F_S);
		UNZIP_DIRECTORY 			= new File(WEAVE_ROOT_DIRECTORY, 	F_S + "unzip" 				+ F_S);
		DESKTOP_DIRECTORY 			= new File(USER_HOME, 				F_S + "Desktop" 			+ F_S);
		
		/* If the settings and zip directory do not already exist, create them. */
		if( !WEAVE_ROOT_DIRECTORY.exists() ) 		WEAVE_ROOT_DIRECTORY.mkdirs();
		if( !BIN_DIRECTORY.exists() )				BIN_DIRECTORY.mkdirs();
		if( !LOGS_DIRECTORY.exists() )				LOGS_DIRECTORY.mkdirs();
		if( !DOWNLOADS_DIRECTORY.exists() )			DOWNLOADS_DIRECTORY.mkdirs();
		if( !EXE_DIRECTORY.exists() )				EXE_DIRECTORY.mkdirs();
		if( !DOWNLOADS_PLUGINS_DIRECTORY.exists() )	DOWNLOADS_PLUGINS_DIRECTORY.mkdirs();
		if( !DEPLOYED_PLUGINS_DIRECTORY.exists() )	DEPLOYED_PLUGINS_DIRECTORY.mkdirs();
		if( !REVISIONS_DIRECTORY.exists() )			REVISIONS_DIRECTORY.mkdirs();

		TraceUtils.traceln(TraceUtils.STDOUT, "");
		TraceUtils.traceln(TraceUtils.STDOUT, "######################################");
		TraceUtils.traceln(TraceUtils.STDOUT, "=== Running Preconfiguration ===");
		TraceUtils.traceln(TraceUtils.STDOUT, "-> Creating File Structure........DONE");
	}
	
	/**
	 * Stores all IP values for the client computer.
	 */
	public static void getNetworkInfo(Boolean offline)
	{
		TraceUtils.trace(TraceUtils.STDOUT, "-> Getting network info...........");
		try {
			if( !offline )
				REMOTE_IP = RemoteUtils.getIP();
			
			LOCAL_IP = InetAddress.getLocalHost().getHostAddress();
			LOCALHOST = "127.0.0.1";
		} catch (UnknownHostException e) {
			TraceUtils.put(TraceUtils.STDOUT, "FAILED");
			TraceUtils.trace(TraceUtils.STDERR, e);
		}
		TraceUtils.put(TraceUtils.STDOUT, "DONE");
	}
	
	/**
	 * Create a shortcut on the user's desktop to the updater
	 * 
	 * @param overwrite Mark as true to overwrite the old shortcut, FALSE otherwise
	 * @throws IOException
	 */
	public static void createShortcut( boolean overwrite ) throws IOException
	{
		JShellLink link = new JShellLink();
//		File shortcut = null;
//		File target = null;
		
//		if( OS == OS_TYPE.WINDOWS ) 
//		{
//			shortcut = new File(DESKTOP_DIRECTORY, UPDATER_NAME+".lnk");
//			target = new File(SHORTCUT_DIRECTORY, "windows.lnk");
//		} 
//		else if( OS == OS_TYPE.MAC ) 
//		{
//			shortcut = new File(DESKTOP_DIRECTORY, UPDATER_NAME);
//			target = new File(SHORTCUT_DIRECTORY, "mac");
//		} 
//		else if( OS == OS_TYPE.LINUX ) 
//		{
//			shortcut = new File(DESKTOP_DIRECTORY, UPDATER_NAME);
//			target = new File(SHORTCUT_DIRECTORY, "linux");
//		}

//		if( target.exists() && ( !shortcut.exists() || overwrite ) ) {
//			if( !shortcut.exists() )
//				TraceUtils.traceln(TraceUtils.STDOUT, "-> Creating shortcut...");
//			else if( overwrite )
//				TraceUtils.traceln(TraceUtils.STDOUT, "-> Updating shortcut...");
				
//			try {
//				FileUtils.copy(target, shortcut, FileUtils.OVERWRITE | FileUtils.OPTION_SINGLE_FILE);
//			} catch (InterruptedException e) {
//				TraceUtils.trace(TraceUtils.STDERR, e);
//			}
//		}
		
		link.setFolder(DESKTOP_DIRECTORY.getCanonicalPath());
		link.setName(PROJECT_NAME);
		link.setPath(new File(BIN_DIRECTORY, WEAVEUPDATER_JAR).getCanonicalPath());
		link.setIconLocation(ICON_FILE.getCanonicalPath());
		link.save();
		
		String ver = RemoteUtils.getConfigEntry(RemoteUtils.SHORTCUT_VER);
		Settings.SHORTCUT_VER = ( ver == null ) ? "0" : ver;
		Settings.save();
	}
	
	
	/**
	 * Obtain a file lock to allow only 1 instance to be open.
	 * 
	 * @return <code>true</code> if lock is obtained successfully, <code>false</code> otherwise
	 * @see releaseLock()
	 */
	public static boolean getLock()
	{
		int myPID = getPID();

		if( LOCK_FILE.exists() )
		{
			try {
				int lockPID = Integer.parseInt(FileUtils.getFileContents(LOCK_FILE));
				
				if( isActivePID(lockPID) )
					return false;
				
				releaseLock();
				return getLock();
				
			} catch (FileNotFoundException e) {
				TraceUtils.trace(TraceUtils.STDERR, e);
				return false;
			}
		}
		else
		{
			// This will get the lock
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(LOCK_FILE));
				bw.write("" + myPID + "");
				bw.flush();
				bw.close();
			} catch (IOException e) {
				TraceUtils.trace(TraceUtils.STDERR, e);
				return false;
			}
		}
		return true;
	}
	
	
	/**
	 * Release the file lock so it may be used by another process.
	 * 
	 * @see getLock()
	 */
	public static void releaseLock()
	{
		if( LOCK_FILE.exists() )
			FileUtils.recursiveDelete(LOCK_FILE);
	}
	
	/**
	 * Check if a service is running on the specified port
	 * @param host the host to query
	 * @param port	the port to check
	 */
	public static Boolean isServiceUp(String host, int port)
	{
		Boolean b = false;
		try {
			Socket sock = new Socket(host, port);
			b = true;
			sock.close();
		} catch (IOException ex) {
			TraceUtils.trace(TraceUtils.STDERR, ex);
		} catch (IllegalArgumentException ex) {
			JOptionPane.showMessageDialog(null, "Port out of range.");
			TraceUtils.trace(TraceUtils.STDERR, ex);
		}
		return b;
	}
	
	
	/**
	 * Determine if an Internet connection can be established
	 * 
	 * @return TRUE if there is an Internet connection, FALSE otherwise
	 */
	public static Boolean isConnectedToInternet()
	{
		/*
		 * In order to use inter-thread communication, the thread required an unchangeable
		 * variable - itc - which we can have are changable variable inside.
		 */
		class ITC {
			boolean isConnected = false;
		}
		final ITC itc = new ITC();
		
		TraceUtils.trace(TraceUtils.STDOUT, "-> Checking Internet Connection...");
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				URL url 			= null;
				URLConnection conn 	= null;
				try {
					url = new URL(Settings.OICWEAVE_URL);
					conn = url.openConnection();
					conn.connect();
					conn.getContent();
				} catch (ConnectException e) {
					// Don't trace error here
					itc.isConnected = false;
				} catch (IOException e) {
					TraceUtils.trace(TraceUtils.STDERR, e);
					itc.isConnected = false;
				}
				itc.isConnected = true;
			}
		});
		try {
			t.start();
			t.join(700);
			t.interrupt();
			t = null;
		} catch (InterruptedException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
		}
		
		isConnectedToInternet = itc.isConnected;
		
		if( itc.isConnected )
			TraceUtils.put(TraceUtils.STDOUT, "CONNECTED");
		else
			TraceUtils.put(TraceUtils.STDOUT, "FAILED");
		
		return itc.isConnected;
	}
	
	public static void enableWeaveProtocol() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		RegistryUtils.createKey(
			RegistryUtils.HKEY_LOCAL_MACHINE,
			"SOFTWARE\\classes\\weave");
		RegistryUtils.createKey(
			RegistryUtils.HKEY_LOCAL_MACHINE,
			"SOFTWARE\\classes\\weave\\DefaultIcon");
		RegistryUtils.createKey(
			RegistryUtils.HKEY_LOCAL_MACHINE,
			"SOFTWARE\\classes\\weave\\Shell\\");
		RegistryUtils.createKey(
			RegistryUtils.HKEY_LOCAL_MACHINE,
			"SOFTWARE\\classes\\weave\\Shell\\Open");
		RegistryUtils.createKey(
			RegistryUtils.HKEY_LOCAL_MACHINE,
			"SOFTWARE\\classes\\weave\\Shell\\Open\\command");
		
		RegistryUtils.writeStringValue(
			RegistryUtils.HKEY_LOCAL_MACHINE, 
			"SOFTWARE\\classes\\weave", 
			"",
			"URL:weave protocol");
		RegistryUtils.writeStringValue(
			RegistryUtils.HKEY_LOCAL_MACHINE,
			"SOFTWARE\\classes\\weave",
			"URL Protocol",
			"");
		RegistryUtils.writeStringValue(
			RegistryUtils.HKEY_LOCAL_MACHINE, 
			"SOFTWARE\\classes\\weave\\Shell\\Open\\command", 
			"",
			"\"" + WEAVE_ROOT_DIRECTORY + "\\bin\\WeaveInstaller.jar\" \"%1\"");
//		WinRegistry.writeStringValue(
//			WinRegistry.HKEY_CURRENT_USER,
//			"Software\\Classes\\Local Settings\\Software\\Microsoft\\Windows\\Shell\\MuiChache",
//			System.getenv("APPDATA") + "\\WeaveInstaller\\bin\\WeaveInstaller.jar",
//			"Weave Installer");
	}
	
	public static void disableWeaveProtocol() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		RegistryUtils.deleteKey(RegistryUtils.HKEY_LOCAL_MACHINE, "SOFTWARE\\classes\\weave");
	}
	
	
	/**
	 * Finds the computer's OS.
	 * The function sets the value of the data member OS to the corresponding enum OS_TYPE value.
	 * 
	 * @see getOS()
	 * @see getExactOS()
	 */
	public static void findOS()
	{
		if( EXACT_OS.toLowerCase().contains("windows") )
			OS = OS_TYPE.WINDOWS;
		else if( EXACT_OS.toLowerCase().contains("nix") || EXACT_OS.toLowerCase().contains("nux") )
			OS = OS_TYPE.LINUX;
		else if( EXACT_OS.toLowerCase().contains("mac") )
			OS = OS_TYPE.MAC;
		else
			OS = OS_TYPE.UNKNOWN;
	}
	
	
	/**
	 * Get the string representation of the base Operating System
	 * 
	 * @return OS as a string
	 * @see findOS()
	 */
	public static String getOS()
	{
		if( OS == OS_TYPE.WINDOWS )
			return "Windows";
		else if( OS == OS_TYPE.MAC )
			return "Mac";
		else if( OS == OS_TYPE.LINUX )
			return "Linux";
		
		return "Unknown";
	}
	
	
	/**
	 * Get the exact string representation of the base Operating System
	 * 
	 * @return Exact OS as a String
	 * @see findOS()
	 * @see getOS()
	 */
	public static String getExactOS()
	{
		return EXACT_OS;
	}

	
	/**
	 * Get the currently running process's PID.
	 * 
	 * @return the process pid
	 */
	public static int getPID()
	{
		String runtimeName = ManagementFactory.getRuntimeMXBean().getName();
		return Integer.parseInt(runtimeName.substring(0, runtimeName.indexOf('@')));
	}
	
	
	/**
	 * Check to see if a process with a particular PID is running.
	 * 
	 * @param pid The PID you want to check
	 * @return <code>true</code> if the PID is running, <code>false</code> otherwise
	 */
	public static boolean isActivePID(int pid)
	{
		String cmds[] = {"cmd", "/c", "tasklist /FI \"IMAGENAME eq javaw.exe\" /FO CSV /V /NH"};
		
		try {
			ArrayList<String> result = ProcessUtils.runAndWait(cmds);
			
			for( int i = 0; i < result.size(); i++ )
				if( result.get(i).contains("\"" + pid + "\"") )
					return true;
		} catch (InterruptedException e) {
			TraceUtils.trace(TraceUtils.STDERR, e);
		}
		return false;
	}
	
	
	/**
	 * Removes any temporary directories created by the tool
	 * This should be run before each shutdown just as a precaution.
	 * 
	 * @throws InterruptedException
	 */
	public static void cleanUp() throws InterruptedException
	{
		boolean success = false;
		int loop = 0;
		
		TraceUtils.traceln(TraceUtils.STDOUT, "-> Cleaning...");
		
		if( DOWNLOADS_TMP_DIRECTORY.exists() )
		{
			do {
				success = FileUtils.recursiveDelete(Settings.DOWNLOADS_TMP_DIRECTORY);
				Thread.sleep(50);
			} while( success == false && loop < 3 );
		}
		
		loop = 0;
		success = false;

		if( UNZIP_DIRECTORY.exists() )
		{
			do {
				success = FileUtils.recursiveDelete(Settings.UNZIP_DIRECTORY);
				Thread.sleep(50);
			} while( success == false && loop < 3 );
		}
	}
	
	
	/**
	 * Stops the WeaveInstaller tool
	 */
	public static void shutdown()
	{
		shutdown(JFrame.NORMAL);
	}
	
	/**
	 * Stops the Weave Installer Tool
	 * @param errno An error code
	 */
	public static void shutdown( int errno )
	{
		if( !canQuit )
			return;
		
		// Do any shutdown procedures here
		if( Settings.CURRENT_PROGRAM_NAME.equals(Settings.UPDATER_NAME) )
		{
			try {
				cleanUp();
			} catch (InterruptedException e) {
				TraceUtils.trace(TraceUtils.STDERR, e);
			}
		}
		else if( Settings.CURRENT_PROGRAM_NAME.equals(Settings.INSTALLER_NAME) )
		{
			
		}
		
		if( errno != JFrame.ERROR && errno != JFrame.ABORT )
			releaseLock();
		
		TraceUtils.traceln(TraceUtils.STDOUT, "=== " + Settings.CURRENT_PROGRAM_NAME + " Shutting Down ===");
		System.exit(errno);
	}
}
