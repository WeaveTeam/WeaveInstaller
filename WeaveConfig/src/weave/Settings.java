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

import static weave.utils.ObjectUtils.ternary;
import static weave.utils.TraceUtils.STDERR;
import static weave.utils.TraceUtils.STDOUT;
import static weave.utils.TraceUtils.getSimpleClassAndMsg;
import static weave.utils.TraceUtils.put;
import static weave.utils.TraceUtils.trace;
import static weave.utils.TraceUtils.traceln;
import static weave.utils.TraceUtils.LEVEL.DEBUG;
import static weave.utils.TraceUtils.LEVEL.INFO;
import static weave.utils.TraceUtils.LEVEL.WARN;

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import weave.core.Function;
import weave.managers.ConfigManager;
import weave.managers.TrayManager;
import weave.reflect.Reflectable;
import weave.server.ServerListener;
import weave.utils.BugReportUtils;
import weave.utils.FileUtils;
import weave.utils.ProcessUtils;
import weave.utils.RegistryUtils;
import weave.utils.RemoteUtils;
import weave.utils.StringUtils;
import weave.utils.SyscallCreatorUtils;

public class Settings extends Globals
{
	@Reflectable public static final String PROJECT_NAME		= "Weave";
	@Reflectable public static final String PROJECT_PROTOCOL	= "weave://";
	@Reflectable public static final String PROJECT_EXTENSION	= ".weave";
	

	/*
	 * Remote API URLs for POST methods
	 */
	@Reflectable public static final String IWEAVE_HOST			= "iweave.com";
	@Reflectable public static final String INSTALL_IWEAVE_HOST	= "install." + IWEAVE_HOST;
	@Reflectable public static final String INFO_IWEAVE_HOST	= "info." + IWEAVE_HOST;
	@Reflectable public static final String IVPR_IWEAVE_HOST	= "ivpr." + IWEAVE_HOST;
	
	@Reflectable public static final String IWEAVE_URL			= "http://" + IWEAVE_HOST + "/";
	@Reflectable public static final String INSTALL_IWEAVE_URL	= "http://" + INSTALL_IWEAVE_HOST + "/";
	@Reflectable public static final String INFO_IWEAVE_URL		= "http://" + INFO_IWEAVE_HOST + "/";
	@Reflectable public static final String IVPR_IWEAVE_URL		= "http://" + IVPR_IWEAVE_HOST + "/";
	
	
	 public static final String UPDATE_CONFIG		= INSTALL_IWEAVE_URL + ".weave/config.txt";
	 public static final String UPDATE_FILES		= INSTALL_IWEAVE_URL + ".weave/files.txt";
	
	 public static final String API_GET_IP			= INSTALL_IWEAVE_URL + "api/ip.php";
	 public static final String API_SOCKET			= INSTALL_IWEAVE_URL + "api/socket.php";
	 public static final String API_FAQ				= INSTALL_IWEAVE_URL + "api/faq.php";
	 public static final String API_STATS_LOG		= INSTALL_IWEAVE_URL + "api/log.php";
	 public static final String API_STATS_LIVE		= INSTALL_IWEAVE_URL + "api/noop.php";
	 public static final String API_BUG_REPORT		= INSTALL_IWEAVE_URL + "api/bug_report.php";

	
	/*
	 * Weave Server
	 */
	@Reflectable public static final String SERVER_NAME			= PROJECT_NAME + " Server Assistant";
	@Reflectable public static final String SERVER_VER			= "2.0.9 Beta";
	@Reflectable public static final String SERVER_TITLE 		= SERVER_NAME + " v" + SERVER_VER;
	@Reflectable public static final String SERVER_JAR			= "Server.jar";
	
	/*
	 * Weave Updater
	 */
	@Reflectable public static final String UPDATER_NAME		= PROJECT_NAME + " Updater";
	@Reflectable public static final String UPDATER_VER			= "1.1.4 Beta";
	@Reflectable public static final String UPDATER_TITLE		= UPDATER_NAME + " v" + UPDATER_VER;
	@Reflectable public static final String UPDATER_JAR			= "Updater.jar";
	@Reflectable public static final String UPDATER_NEW_JAR		= "Updater_new.jar";
	
	/*
	 * Weave Launcher
	 */
	@Reflectable public static final String LAUNCHER_NAME		= PROJECT_NAME + " Launcher";
	@Reflectable public static final String LAUNCHER_VER		= "1.0.0";
	@Reflectable public static final String LAUNCHER_TITLE		= LAUNCHER_NAME + " v" + LAUNCHER_VER;
	@Reflectable public static final String LAUNCHER_JAR		= "Launcher.jar";
	
	/*
	 * OS specific settings
	 */
	public static final String USER_HOME				= System.getProperty("user.home");
	public static final String N_L						= System.getProperty("line.separator");
	public static final String F_S						= System.getProperty("file.separator");
	public static final String EXACT_OS					= System.getProperty("os.name");
	public static final String JAVA_VERSION				= System.getProperty("java.specification.version");
	
	/*
	 * File directory structure
	 */
	public static final String WEAVE_ROOT_DIRECTORY_NAME		= ".weave";
	public static final String LOGS_DIRECTORY_NAME				= "logs";
	public static final String BIN_DIRECTORY_NAME 				= "bin";
	public static final String SETTINGS_FILE_NAME 				= "settings.save";
	public static final String CONFIG_FILE_NAME 				= "configs.save";
	public static final String SLOCK_FILE_NAME					= "s.lock";
	public static final String ULOCK_FILE_NAME					= "u.lock";
	public static final String ICON_FILE_NAME					= "icon.ico";
	public static final String LIBS_DIRECTORY_NAME				= "libs";
	public static final String DOWNLOADS_DIRECTORY_NAME 		= "downloads";
	public static final String DOWNLOADS_TMP_DIRECTORY_NAME		= "tmp";
	public static final String WEAVE_BINARIES_DIRECTORY_NAME 	= "weave-binaries";
	public static final String ANALYST_BINARIES_DIRECTORY_NAME	= "analyst-binaries";
	public static final String DEPLOYED_PLUGINS_DIRECTORY_NAME	= "plugins";
	public static final String UNZIP_DIRECTORY_NAME 			= "unzip";
	public static final String DESKTOP_DIRECTORY_NAME 			= "Desktop";
	
	public static File APPDATA_DIRECTORY				= null;
	public static File WEAVE_ROOT_DIRECTORY 			= null;
	public static File DOWNLOADS_DIRECTORY				= null;
	public static File DOWNLOADS_TMP_DIRECTORY			= null;
	public static File BIN_DIRECTORY					= null;
	public static File LIBS_DIRECTORY					= null;
	public static File LOGS_DIRECTORY					= null;
	public static File WEAVE_BINARIES_DIRECTORY 		= null;
	public static File ANALYST_BINARIES_DIRECTORY		= null;
	public static File UNZIP_DIRECTORY 					= null;
	public static File DEPLOYED_PLUGINS_DIRECTORY		= null;
	public static File SETTINGS_FILE 					= null;
	public static File CONFIG_FILE						= null;
	public static File SLOCK_FILE						= null;
	public static File ULOCK_FILE						= null;
	public static File ICON_FILE						= null;
	public static File DESKTOP_DIRECTORY				= null;
	
	/*
	 * Operating System
	 */
	public static enum OS_ENUM 							{ WINDOWS, LINUX, MAC, UNKNOWN };
	public static OS_ENUM OS 							= OS_ENUM.UNKNOWN;
	
	/*
	 * Settings File
	 */
	private static Map<String, Object> SETTINGS_MAP 	= null;

	public static boolean UPDATE_OVERRIDE				= false;
	public static boolean CONFIGURED					= false;
	public static boolean SETUP_COMPLETE				= false;
	public static String UNIQUE_ID						= "";
	public static String LAST_UPDATE_CHECK 				= "Never";
	public static int 	 RPC_PORT						= 3579;

	public static enum INSTALL_ENUM						{ NIGHTLY, MILESTONE };
	public static INSTALL_ENUM INSTALL_MODE				= INSTALL_ENUM.MILESTONE;
	
	/*
	 * Networking
	 */
	public static enum LAUNCH_ENUM						{ ONLINE_MODE, OFFLINE_MODE };
	public static LAUNCH_ENUM LAUNCH_MODE				= LAUNCH_ENUM.ONLINE_MODE;
	public static String REMOTE_IP						= "";
	public static String LOCAL_IP						= "";
	public static String LOCALHOST						= "";
	
	/*
	 * Socket Server for RPC
	 */
	public static Thread rpcThread						= null;
	public static ServerListener rpcServer				= null;
	
	/*
	 * Misc
	 */
	public static final String WIKI_HELP_PAGE			= "http://" + INFO_IWEAVE_HOST + "/projects/weave/wiki/Weave_Server_Assistant";
	public static final String ELEVATE_UTIL				= "elevate.exe";
	
	public static boolean canQuit						= true;
	public static boolean transferCancelled				= false;
	public static boolean transferLocked				= false;
	public static boolean isConnectedToInternet			= true;

	public static 		String CURRENT_PROGRAM_NAME		= PROJECT_NAME;
	public static final String FONT						= Font.SANS_SERIF;
	public static boolean INSTALLER_POPUP_SHOWN			= false;
	public static double REQUIRED_JAVA_VERSION			= 1.7;
	public static int LOG_PADDING_LENGTH				= 34;
	public static int recommendPrune					= 6;
	
	
	/**
	 * This function must be called at the beginning of an executable
	 * to ensure that all values are properly set
	 */
	public static void init()
	{
		// Check for Java version
		double version = Double.parseDouble(JAVA_VERSION);
		if( version < REQUIRED_JAVA_VERSION )
		{
			JOptionPane.showMessageDialog(null, "Your version of Java is not supported by this application.\n\n" +
					"Your version: " + JAVA_VERSION + "\n" +
					"Required version: " + REQUIRED_JAVA_VERSION + "\n\n" + 
					"Please update Java and try again.\n" +
					"If the problem persists, try installing the JDK.", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(JPanel.ERROR);
		}
		
		findOS();
		createFS();
		
		// Read all settings
		load();
		// Overwrite settings file with loaded settings,
		// and any new settings with the default values.
		save();		 

		RemoteUtils.isConnectedToInternet(
			new Function<Object, Object>() {
				@Override
				public Object call(Object... args) {
					getNetworkInfo( isOfflineMode() );
					return null;
				}
			}, new Function<Object, Object>() {
				@Override
				public Object call(Object... args) {
					getNetworkInfo(true);
					return null;
				}
			}
		);
	}
	

	/**
	 * Check to see if any arbitrary file in the /bin/ directory exists.
	 * 
	 * @param name The name of the file
	 * @return <code>true</code> if the file exists, <code>false</code> otherwise
	 */
	public static boolean binFileExists(String name)
	{
		return new File(BIN_DIRECTORY, name).exists();
	}
	
	/**
	 * Check to see if the file that holds all the settings exists in the file structure.
	 * 
	 * @return <code>true</code> if settings file exists, <code>false</code> otherwise
	 */
	public static boolean settingsFileExists()
	{
		return binFileExists(SETTINGS_FILE_NAME);
	}
	
	
	/**
	 * Check to see if the file that holds all of the plugin settings exists in the file structure.
	 * 
	 * @return <code>true</code> if plugins file exists, <code>false</code> otherwise
	 */
	public static boolean configsFileExists()
	{
		return binFileExists(CONFIG_FILE_NAME);
	}
	
	/**
	 * Check to see if a unique identifier has been assigned to the tool yet.
	 * 
	 * @return <code>true</code> if the tool has a unique identifier, <code>false</code> otherwise 
	 */
	public static boolean hasUniqueID()
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
	public static boolean isOfflineMode()
	{
		return (LAUNCH_MODE == LAUNCH_ENUM.OFFLINE_MODE);
	}
	
	
	/**
	 * Write the current values of data members to the settings map.
	 *
	 * @return <code>true</code> if successful write, <code>false</code> otherwise
	 * @see load()
	 */
	public static boolean save()
	{
		try {
			trace(STDOUT, WARN, StringUtils.rpad("Saving settings file", ".", LOG_PADDING_LENGTH));
			
			if( !WEAVE_ROOT_DIRECTORY.exists() )
				WEAVE_ROOT_DIRECTORY.mkdirs();
			if( !SETTINGS_FILE.exists() )
				SETTINGS_FILE.createNewFile();
			
			SETTINGS_MAP = new HashMap<String, Object>();
			SETTINGS_MAP.put("CONFIGURED", CONFIGURED);
			SETTINGS_MAP.put("SETUP_COMPLETE", SETUP_COMPLETE);
			SETTINGS_MAP.put("UNIQUE_ID", UNIQUE_ID);
			SETTINGS_MAP.put("LAST_UPDATE_CHECK", LAST_UPDATE_CHECK);
			SETTINGS_MAP.put("UPDATE_OVERRIDE", UPDATE_OVERRIDE);
			SETTINGS_MAP.put("LAUNCH_MODE", LAUNCH_MODE);
			SETTINGS_MAP.put("INSTALL_MODE", INSTALL_MODE);
			SETTINGS_MAP.put("RPC_PORT", RPC_PORT);
			
			ObjectOutputStream outstream = new ObjectOutputStream(new FileOutputStream(SETTINGS_FILE));
			outstream.writeObject(SETTINGS_MAP);
			outstream.close();
			put(STDOUT, "DONE");
		} catch (IOException e) {
			put(STDOUT, "FAILED (" + getSimpleClassAndMsg(e) + ")");
			trace(STDERR, e);
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
	public static boolean load()
	{
		if( !settingsFileExists() ) return false;
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			trace(STDERR, e); 
		}
		
		try {
			trace(STDOUT, WARN, StringUtils.rpad("Loading settings file", ".", LOG_PADDING_LENGTH));
			
			ObjectInputStream instream = new ObjectInputStream(new FileInputStream(SETTINGS_FILE));
			SETTINGS_MAP = (Map<String, Object>) instream.readObject();
			instream.close();
			
			/* Obtain the map values and assign them to data members */
			CONFIGURED = 			(Boolean)		ternary(SETTINGS_MAP.get("CONFIGURED"), 			CONFIGURED);
			SETUP_COMPLETE = 		(Boolean)		ternary(SETTINGS_MAP.get("SETUP_COMPLETE"), 		SETUP_COMPLETE);
			UNIQUE_ID = 			(String)		ternary(SETTINGS_MAP.get("UNIQUE_ID"), 				UNIQUE_ID);
			LAST_UPDATE_CHECK = 	(String) 		ternary(SETTINGS_MAP.get("LAST_UPDATE_CHECK"), 		LAST_UPDATE_CHECK);
			UPDATE_OVERRIDE	=		(Boolean)		ternary(SETTINGS_MAP.get("UPDATE_OVERRIDE"), 		UPDATE_OVERRIDE);
			LAUNCH_MODE = 			(LAUNCH_ENUM)	ternary(SETTINGS_MAP.get("LAUNCH_MODE"), 			LAUNCH_MODE);
			INSTALL_MODE = 			(INSTALL_ENUM)	ternary(SETTINGS_MAP.get("INSTALL_MODE"), 			INSTALL_MODE);
			RPC_PORT = 				(Integer)		ternary(SETTINGS_MAP.get("RPC_PORT"), 				RPC_PORT);

		} catch (FileNotFoundException e) {
			put(STDOUT, "FAILED (" + getSimpleClassAndMsg(e) + ")");
			trace(STDERR, e);
			JOptionPane.showMessageDialog(null, "Error reading settings file: File not found", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		} catch (ClassNotFoundException e) {
			put(STDOUT, "FAILED (" + getSimpleClassAndMsg(e) + ")");
			trace(STDERR, e);
			return false;
		} catch (IOException e) {
			put(STDOUT, "FAILED (" + getSimpleClassAndMsg(e) + ")");
			trace(STDERR, e);
			BugReportUtils.showBugReportDialog(e);
			return false;
		}
		put(STDOUT, "DONE");
		return true;
	}
	
	
	/**
	 * Create appropriate file pointers to directories in the system 
	 * corresponding to the install paths depending on the OS.
	 */
	public static void createFS()
	{
		String wsp = System.getenv("WEAVE_HOME");
		File wsp_file;
		
		if( wsp != null )
		{
			wsp_file = new File(wsp);
			if( wsp_file.exists() && wsp_file.isDirectory() )
				createFS(wsp);
		}
		
		else if( OS == OS_ENUM.WINDOWS )
			createFS(System.getenv("APPDATA"));
		else if( OS == OS_ENUM.LINUX )
			createFS(USER_HOME);
		else if( OS == OS_ENUM.MAC )
			createFS(USER_HOME + F_S + "Library" + F_S + "Application Support");
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
	}
	
	public static void createFS(String directory)
	{
		APPDATA_DIRECTORY			= new File(directory);
		WEAVE_ROOT_DIRECTORY		= new File(APPDATA_DIRECTORY, 		F_S + WEAVE_ROOT_DIRECTORY_NAME + F_S);
		LOGS_DIRECTORY				= new File(WEAVE_ROOT_DIRECTORY,	F_S + LOGS_DIRECTORY_NAME + F_S);

		traceln(STDOUT, INFO, "");
		traceln(STDOUT, INFO, "######################################");
		traceln(STDOUT, INFO, "=== Running " + CURRENT_PROGRAM_NAME + " Preconfiguration ===");
		traceln(STDOUT, INFO, StringUtils.rpad("Creating File Structure", ".", LOG_PADDING_LENGTH));

		
		BIN_DIRECTORY				= new File(WEAVE_ROOT_DIRECTORY, 	F_S + BIN_DIRECTORY_NAME + F_S);
		SETTINGS_FILE 				= new File(BIN_DIRECTORY, 			F_S + SETTINGS_FILE_NAME);
		CONFIG_FILE					= new File(BIN_DIRECTORY, 			F_S + CONFIG_FILE_NAME);
		SLOCK_FILE					= new File(BIN_DIRECTORY,			F_S + SLOCK_FILE_NAME);
		ULOCK_FILE					= new File(BIN_DIRECTORY, 			F_S + ULOCK_FILE_NAME);
		ICON_FILE					= new File(BIN_DIRECTORY,			F_S + ICON_FILE_NAME);
		LIBS_DIRECTORY				= new File(WEAVE_ROOT_DIRECTORY,	F_S + LIBS_DIRECTORY_NAME + F_S);
		DOWNLOADS_DIRECTORY 		= new File(WEAVE_ROOT_DIRECTORY, 	F_S + DOWNLOADS_DIRECTORY_NAME + F_S);
		DOWNLOADS_TMP_DIRECTORY		= new File(DOWNLOADS_DIRECTORY, 	F_S + DOWNLOADS_TMP_DIRECTORY_NAME + F_S);
		WEAVE_BINARIES_DIRECTORY 	= new File(DOWNLOADS_DIRECTORY, 	F_S + WEAVE_BINARIES_DIRECTORY_NAME + F_S);
		ANALYST_BINARIES_DIRECTORY	= new File(DOWNLOADS_DIRECTORY,		F_S + ANALYST_BINARIES_DIRECTORY_NAME + F_S);
		DEPLOYED_PLUGINS_DIRECTORY	= new File(WEAVE_ROOT_DIRECTORY, 	F_S + DEPLOYED_PLUGINS_DIRECTORY_NAME + F_S);
		UNZIP_DIRECTORY 			= new File(WEAVE_ROOT_DIRECTORY, 	F_S + UNZIP_DIRECTORY_NAME + F_S);
		DESKTOP_DIRECTORY 			= new File(USER_HOME, 				F_S + DESKTOP_DIRECTORY_NAME + F_S);
		
		/* If the folders do not already exist, create them. */
		if( !WEAVE_ROOT_DIRECTORY.exists() ) 		WEAVE_ROOT_DIRECTORY.mkdirs();
		if( !BIN_DIRECTORY.exists() )				BIN_DIRECTORY.mkdirs();
		if( !LIBS_DIRECTORY.exists() )				LIBS_DIRECTORY.mkdirs();
		if( !LOGS_DIRECTORY.exists() )				LOGS_DIRECTORY.mkdirs();
		if( !DOWNLOADS_DIRECTORY.exists() )			DOWNLOADS_DIRECTORY.mkdirs();
		if( !DEPLOYED_PLUGINS_DIRECTORY.exists() )	DEPLOYED_PLUGINS_DIRECTORY.mkdirs();
		if( !WEAVE_BINARIES_DIRECTORY.exists() )	WEAVE_BINARIES_DIRECTORY.mkdirs();
		if( !ANALYST_BINARIES_DIRECTORY.exists() )	ANALYST_BINARIES_DIRECTORY.mkdirs();

		put(STDOUT, "DONE");
	}
	
	
	/**
	 * Stores all IP values for the client computer.
	 */
	public static void getNetworkInfo(Boolean offline)
	{
		trace(STDOUT, WARN, StringUtils.rpad("Getting network info", ".", LOG_PADDING_LENGTH));
		try {
			LOCAL_IP = InetAddress.getLocalHost().getHostAddress();
			LOCALHOST = "127.0.0.1";
			
			if( !offline )
				REMOTE_IP = RemoteUtils.getIP();
			
		} catch (UnknownHostException e) {
			put(STDOUT, "FAILED (" + getSimpleClassAndMsg(e) + ")");
			trace(STDERR, e);
		}
		put(STDOUT, "DONE");
	}
	
	
	public static void loadLibrary(String path) throws FileNotFoundException
	{
		File lib = new File(LIBS_DIRECTORY, path);
		
		if( !lib.exists() )
			return;
		
		System.load(lib.getAbsolutePath());
	}
	
	/**
	 * Obtain a file lock to allow only 1 instance to be open.
	 * 
	 * @return <code>true</code> if lock is obtained successfully, <code>false</code> otherwise
	 * 
	 * @throws InterruptedException 
	 * @see releaseLock()
	 */
	public static boolean getLock()
	{
		int myPID = getPID();
		
		Function<Object, Object> getSLock = new Function<Object, Object>() {
			@Override
			public Object call(Object... args) {
				Integer pid = (Integer) args[0];
				
				traceln(STDOUT, WARN, StringUtils.rpad("Getting slock file", ".", LOG_PADDING_LENGTH));
				if( SLOCK_FILE.exists() )
				{
					try {
						int lockPID = Integer.parseInt(FileUtils.getFileContents(SLOCK_FILE));

						if( isActivePID(lockPID) ) {
							put(STDOUT, "FAILED (ALREADY OPEN)");
							return false;
						}
						
						releaseLock(SLOCK_FILE);
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							trace(STDERR, e);
						}
						put(STDOUT, "CLEANING");
						
						return null;
						
					} catch (NumberFormatException e) {
						put(STDOUT, "FAILED (" + getSimpleClassAndMsg(e) + ")");
						trace(STDERR, e); 
						BugReportUtils.showBugReportDialog(e);
						return false;
					} catch (IOException e) {
						put(STDOUT, "FAILED (" + getSimpleClassAndMsg(e) + ")");
						trace(STDERR, e);
						BugReportUtils.showBugReportDialog(e);
						return false;
					}
				}
				else
				{
					// This will get the lock
					try {
						SLOCK_FILE.createNewFile();
						FileOutputStream out = new FileOutputStream(SLOCK_FILE);
						out.write(("" + pid).getBytes(Charset.forName("UTF-8")));
						out.flush();
						out.close();
					} catch (IOException e) {
						put(STDOUT, "FAILED (" + getSimpleClassAndMsg(e) + ")");
						trace(STDERR, e);
						return false;
					}
				}
				put(STDOUT, "DONE");
				return true;
			}
		};
		Function<Object, Object> getULock = new Function<Object, Object>() {
			@Override
			public Object call(Object... args) {
				Integer pid = (Integer) args[0];
				
				traceln(STDOUT, WARN, StringUtils.rpad("Getting ulock file", ".", LOG_PADDING_LENGTH));
				if( ULOCK_FILE.exists() )
				{
					try {
						int lockPID = Integer.parseInt(FileUtils.getFileContents(ULOCK_FILE));
						
						if( isActivePID(lockPID) ) {
							put(STDOUT, "FAILED (ALREADY OPEN)");
							return false;
						}
						
						releaseLock(ULOCK_FILE);
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							trace(STDERR, e);
						}
						put(STDOUT, "CLEANING");
						
						return null;
						
					} catch (NumberFormatException e) {
						put(STDOUT, "FAILED (" + getSimpleClassAndMsg(e) + ")");
						trace(STDERR, e); 
						BugReportUtils.showBugReportDialog(e);
						return false;
					} catch (IOException e) {
						put(STDOUT, "FAILED (" + getSimpleClassAndMsg(e) + ")");
						trace(STDERR, e);
						BugReportUtils.showBugReportDialog(e);
						return false;
					}
				}
				else
				{
					// This will get the lock
					try {
						ULOCK_FILE.createNewFile();
						FileOutputStream out = new FileOutputStream(ULOCK_FILE);
						out.write(("" + pid).getBytes(Charset.forName("UTF-8")));
						out.flush();
						out.close();
					} catch (IOException e) {
						put(STDOUT, "FAILED (" + getSimpleClassAndMsg(e) + ")");
						trace(STDERR, e);
						return false;
					}
				}
				put(STDOUT, "DONE");
				return true;
			}
		};
		
		if( CURRENT_PROGRAM_NAME.equals(UPDATER_NAME) )
		{
			Boolean b = null,
					result = true;
			
			do {
				b = (Boolean)getSLock.call(myPID); 
			} while( b == null );
			result &= b;
			do {
				b = (Boolean)getULock.call(myPID);
			} while( b == null );
			result &= b;

			if( result && SLOCK_FILE.exists() )
				FileUtils.recursiveDelete(SLOCK_FILE);
				
			return result;
		}
		else if( CURRENT_PROGRAM_NAME.equals(SERVER_NAME) )
		{
			Boolean b = null;
			do {
				b = (Boolean)getSLock.call(myPID);
			} while( b == null );
			return b;
		}
		return false;
	}
	
	
	/**
	 * Release the file lock so it may be used by another process.
	 * 
	 * @see getLock()
	 */
	public static boolean releaseLock()
	{
		if( CURRENT_PROGRAM_NAME.equals(SERVER_NAME) )
			return releaseLock(SLOCK_FILE);
		
		if( CURRENT_PROGRAM_NAME.equals(UPDATER_NAME) )
			return releaseLock(ULOCK_FILE);
		
		return false;
	}
	
	/**
	 * Override current program lock and release a specific file lock 
	 * so it may be used by another process.
	 * 
	 * @param file The file lock to release
	 * @return <code>true</code> if the file was deleted, <code>false</code> otherwise
	 */
	public static boolean releaseLock(File file)
	{
		if( file.exists() )
			return FileUtils.recursiveDelete(file);
		
		return false;
	}

	/**
	 * Check to see if a service is running on the host at the specified port
	 * 
	 * @param host The host address to query
	 * @param port The port to check
	 * @return <code>true</code> if the service is up, <code>false</code> otherwise
	 */
	@Reflectable 
	public static Boolean isServiceUp(String host, Integer port)
	{
		Boolean b = false;
		try {
			Socket sock = new Socket(host, port);
			sock.close();
			b = true;
		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(null, "Port out of range.");
//			trace(STDERR, e);
		} catch (IOException e) {
//			trace(STDERR, e);
		}
		return b;
	}
	
	public static void startListenerServer()
	{
		rpcServer = new ServerListener(RPC_PORT);
		rpcServer.start();
	}
	
	public static void stopListenerServer()
	{
		if( rpcServer != null )
			rpcServer.stop();
	}
	
	@Reflectable 
	public static void enableWeaveProtocol(Boolean enable) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		if( OS != OS_ENUM.WINDOWS )
			return;

		// 	weave://
		if( enable )
		{
			RegistryUtils.writeString(
					RegistryUtils.HKEY_CURRENT_USER,
					"Software\\Classes\\weave", 
					RegistryUtils.REG_SZ, 
					"", "URL:weave Protocol");
			RegistryUtils.writeString(
					RegistryUtils.HKEY_CURRENT_USER,
					"Software\\Classes\\weave", 
					RegistryUtils.REG_SZ,
					"\"URL Protocol\"", "");
			RegistryUtils.writeString(
					RegistryUtils.HKEY_CURRENT_USER, 
					"Software\\Classes\\weave\\shell\\open\\command", 
					RegistryUtils.REG_EXPAND_SZ, 
					"", "cmd /C start /MIN java -jar \"^%APPDATA^%\\.weave\\bin\\Launcher.jar\" \"%1\"");
		}
		else
		{
			RegistryUtils.deleteKey(RegistryUtils.HKEY_CURRENT_USER, "Software\\Classes\\weave", null);
		}
	}
	
	@Reflectable 
	public static void enableWeaveExtension(Boolean enable) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		if( OS != OS_ENUM.WINDOWS )
			return;
		
		// .weave extension
		if( enable )
		{
			RegistryUtils.writeString(
					RegistryUtils.HKEY_CURRENT_USER, 
					"Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\FileExts\\.weave\\UserChoice",
					RegistryUtils.REG_SZ,
					"Progid", "weavefile");
			RegistryUtils.writeString(
					RegistryUtils.HKEY_CURRENT_USER,
					"Software\\Classes\\weavefile\\DefaultIcon", 
					RegistryUtils.REG_EXPAND_SZ, 
					"", "\"^%APPDATA^%\\.weave\\bin\\file.ico\"");
//			RegEdit.writeString(
//					RegEdit.HKEY_CURRENT_USER,
//					"Software\\Classes\\weavefile\\shell\\open\\command",
//					RegEdit.REG_EXPAND_SZ, 
//					"", "cmd /C start /MIN java -jar \"^%APPDATA^%\\.weave\\bin\\Launcher.jar\" \"%1\"");
		}
		else
		{
			RegistryUtils.deleteKey(
					RegistryUtils.HKEY_CURRENT_USER,
					"Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\FileExts\\.weave", 
					null);
			RegistryUtils.deleteKey(
					RegistryUtils.HKEY_CURRENT_USER, 
					"Software\\Classes\\weavefile",
					null);
		}
	}

	
	public static void setDirectoryPermissions()
	{
		int i = 0;
		File[] fileList = BIN_DIRECTORY.listFiles();
		for( i = 0; i < fileList.length; i++ )
		{
			if( FileUtils.getExt(fileList[i]).equals("jar") )
				FileUtils.setPermissions(fileList[i], 0x755);
			else if( FileUtils.getExt(fileList[i]).equals("save") )
				FileUtils.setPermissions(fileList[i], 0x766);
			else
				FileUtils.setPermissions(fileList[i], 0x744);
		}
		
		fileList = LOGS_DIRECTORY.listFiles();
		for( i = 0; i < fileList.length; i++ )
		{
			FileUtils.setPermissions(fileList[i], 0x766);
		}
	}	
	
	/**
	 * Finds the computer's OS.
	 * The function sets the value of the data member OS to the corresponding enum OS_TYPE value.
	 * 
	 * @see {@link Settings#getOS()}
	 * @see {@link Settings#getExactOS()}
	 */
	public static void findOS()
	{
		if( EXACT_OS.toLowerCase().contains("windows") )
			OS = OS_ENUM.WINDOWS;
		else if( EXACT_OS.toLowerCase().contains("nix") || EXACT_OS.toLowerCase().contains("nux") )
			OS = OS_ENUM.LINUX;
		else if( EXACT_OS.toLowerCase().contains("mac") )
			OS = OS_ENUM.MAC;
		else
			OS = OS_ENUM.UNKNOWN;
	}
	
	
	/**
	 * Get the string representation of the base Operating System
	 * 
	 * @return OS as a string
	 * 
	 * @see {@link Settings#findOS()}
	 */
	@Reflectable 
	public static String getOS()
	{
		if( OS == OS_ENUM.WINDOWS )
			return "Windows";
		else if( OS == OS_ENUM.MAC )
			return "Mac";
		else if( OS == OS_ENUM.LINUX )
			return "Linux";
		
		return "Unknown";
	}
	
	
	/**
	 * Get the exact string representation of the base Operating System
	 * 
	 * @return Exact OS as a String
	 * 
	 * @see {@link Settings#findOS()}
	 * @see {@link Settings#getOS()}
	 */
	@Reflectable 
	public static String getExactOS()
	{
		return EXACT_OS;
	}

	
	/**
	 * Get the currently running process's PID.
	 * 
	 * @return the process pid
	 * 
	 * @see {@link Settings#isActivePID(int)}
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
	 * 
	 * @see {@link Settings#getPID()}
	 */
	@Reflectable
	public static Boolean isActivePID(Integer pid)
	{
		Map<String, List<String>> result = null;
		String windows_cmds[] = SyscallCreatorUtils.generate("tasklist /FO CSV | findstr \"java\"");
		String unix_cmds[] = SyscallCreatorUtils.generate("ps -A -o pid,command | grep -i \"java\"" );
		
		try {
			
			if( OS == OS_ENUM.WINDOWS )		
				result = ProcessUtils.run(windows_cmds);
			else if( OS == OS_ENUM.MAC || OS == OS_ENUM.LINUX )
				result = ProcessUtils.run(unix_cmds);
			else
				result = new HashMap<String, List<String>>();
			
			for( int i = 0; i < result.get("output").size(); i++ )
				if( result.get("output").get(i).contains("" + pid) )
					return true;

		} catch (InterruptedException e) {
			trace(STDERR, e);
			return false;
		} catch (IOException e) {
			trace(STDERR, e);
			return false;
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
		
		traceln(STDOUT, DEBUG, "Cleaning...");
		
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
		
		System.gc();
	}
	
	
	/**
	 * Stops the WeaveServer tool
	 * @throws InterruptedException 
	 */
	@Reflectable
	public static void shutdown()
	{
		shutdown(JFrame.NORMAL);
	}
	
	/**
	 * Stops the Weave Server Tool
	 * @param errno An error code
	 * @throws InterruptedException 
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
				trace(STDERR, e);
			}
		}
		else if( Settings.CURRENT_PROGRAM_NAME.equals(Settings.SERVER_NAME) )
		{
			ConfigManager.getConfigManager().unloadAllConfigs();
			TrayManager.removeTrayIcon();
			stopListenerServer();
		}
		
		if( errno != JFrame.ERROR && errno != JFrame.ABORT )
			releaseLock();
		
		traceln(STDOUT, INFO, "=== " + Settings.CURRENT_PROGRAM_NAME + " Shutting Down ===");
		System.exit(errno);
	}
}
