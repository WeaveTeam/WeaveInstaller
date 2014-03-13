package weave;

import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;

public class Settings
{
	public final String UPDATE_URL = "https://codeload.github.com/IVPR/Weave-Binaries/legacy.zip/master";
	public final String UPDATE_CONFIG = "http://oicweave.org/WeaveUpdater/config.txt";
	public final String INSTALLER_URL = "http://info.oicweave.org/projects/weave/wiki/Installer";
	public final String UPDATER_VER = "1.2R2";
	public final String TITLE = "Weave Installer v".concat(UPDATER_VER);
	public File SETTINGS_DIRECTORY = null;
	public File EXE_DIRECTORY = null;
	public File ZIP_DIRECTORY = null;
	public File UNZIP_DIRECTORY = null;
	public File SETTINGS_FILE = null;
	public File TOMCAT_FILE = null;
	public File MySQL_FILE = null;

	public static enum OS_TYPE
	{
		WINDOWS,
		LINUX,
		MAC,
		UNKNOWN;
	}

	public static enum SERVICE_PORTS
	{
		MySQL,
		TOMCAT;
	}

	public static enum MSI_TYPE
	{
		TOMCAT_MSI,
		MySQL_MSI;
	}

	public String LAST_UPDATE_CHECK = "Never";
	public String TOMCAT_DIR = "";
	public String CURRENT_INSTALL_VER = "";
	public String BEST_TOMCAT = null;
	public String BEST_MYSQL = null;
	public OS_TYPE OS = null;
	public int MySQL_PORT = 3306;
	public int TOMCAT_PORT = 8080;
	public int runningProcesses = 0;
	public int recommendPrune = 6;
	private Map<String, Object> settings = null;
	private static Settings _instance = null;

	public static Settings instance()
	{
		if (_instance == null)
		{
			_instance = new Settings();
		}
		return _instance;
	}

	public Settings()
	{
		findOS();
		createFS();
		if (!readSettings().booleanValue())
		{
			findTomcatDir();
		}
		new Timer().schedule(new TimerTask()
		{
			public void run()
			{
				if (Settings.this.isConnectedToInternet().booleanValue())
				{
					Settings.this.checkForUpdate();
				}
			}
		}, 1000L);

		new Timer().schedule(new TimerTask()
		{
			public void run()
			{
				if (Settings.this.isConnectedToInternet().booleanValue())
				{
					Settings.this.checkForUpdate();
				}
			}
		}, 86400000L, 86400000L);
	}

	public void checkForUpdate()
	{
		String latestVersion = getLatestWeaveUpdaterVersion();
		if (!latestVersion.equals(UPDATER_VER))
		{
			int n = JOptionPane.showConfirmDialog(
					null,
					"There is a newer version of this tool available for download. \n\nWould you like to download it now?",
					"Update Available", 0, 1);
			if (n == 0)
			{
				if (Desktop.isDesktopSupported())
				{
					try
					{
						Desktop.getDesktop().browse(new URI(getLatestWeaveUpdaterURL()));
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					JOptionPane.showMessageDialog(
							null, "Sorry, this feature is not supported by your version of Java.", "Error", 0);
				}
			}
		}
	}

	public Boolean settingsExists()
	{
		return Boolean.valueOf(this.SETTINGS_FILE.exists());
	}

	public Boolean writeSettings()
	{
		try
		{
			if (!this.SETTINGS_DIRECTORY.exists())
			{
				this.SETTINGS_DIRECTORY.mkdirs();
				if (!this.SETTINGS_FILE.exists())
				{
					this.SETTINGS_FILE.createNewFile();
				}
			}
			this.settings = new HashMap<String,Object>();
			this.settings.put("OS", this.OS);
			this.settings.put("TOMCAT_DIR", this.TOMCAT_DIR);
			this.settings.put("MySQL_PORT", Integer.valueOf(this.MySQL_PORT));
			this.settings.put("TOMCAT_PORT", Integer.valueOf(this.TOMCAT_PORT));
			this.settings.put("LAST_UPDATE_CHECK", this.LAST_UPDATE_CHECK);
			this.settings.put("CURRENT_INSTALL_VER", this.CURRENT_INSTALL_VER);

			FileOutputStream fout = new FileOutputStream(this.SETTINGS_FILE);
			ObjectOutputStream outstream = new ObjectOutputStream(fout);
			outstream.writeObject(this.settings);
			outstream.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return Boolean.valueOf(false);
		}
		return Boolean.valueOf(true);
	}

	@SuppressWarnings("unchecked")
	public Boolean readSettings()
	{
		try
		{
			Thread.sleep(1000L);
		}
		catch (InterruptedException e1)
		{
			e1.printStackTrace();
		}
		if (!this.SETTINGS_FILE.exists())
		{
			return Boolean.valueOf(false);
		}
		try
		{
			FileInputStream fin = new FileInputStream(this.SETTINGS_FILE);
			ObjectInputStream instream = new ObjectInputStream(fin);
			this.settings = ((Map<String,Object>) instream.readObject());
			instream.close();

			this.OS = ((OS_TYPE) this.settings.get("OS"));
			this.TOMCAT_DIR = ((String) this.settings.get("TOMCAT_DIR"));
			this.MySQL_PORT = ((Integer) this.settings.get("MySQL_PORT")).intValue();
			this.TOMCAT_PORT = ((Integer) this.settings.get("TOMCAT_PORT")).intValue();
			this.LAST_UPDATE_CHECK = ((String) this.settings.get("LAST_UPDATE_CHECK"));
			this.CURRENT_INSTALL_VER = ((String) this.settings.get("CURRENT_INSTALL_VER"));

			System.out.println("Settings successfully read!");
			System.out.println("OS: " + this.OS);
			System.out.println("TOMCAT_DIR: " + this.TOMCAT_DIR);
			System.out.println("MySQL_PORT: " + this.MySQL_PORT);
			System.out.println("TOMCAT_PORT: " + this.TOMCAT_PORT);
			System.out.println("LAST_UPDATE_CHECK: " + this.LAST_UPDATE_CHECK);
			System.out.println("CURRENT_INSTALL_VER: " + this.CURRENT_INSTALL_VER);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			return Boolean.valueOf(false);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return Boolean.valueOf(false);
		}
		catch (ClassNotFoundException e)
		{
			JOptionPane.showMessageDialog(null, "Error reading settings file.", "Error", 0);
			return Boolean.valueOf(false);
		}
		return Boolean.valueOf(true);
	}

	public void createFS()
	{
		if (this.OS == OS_TYPE.WINDOWS)
		{
			this.SETTINGS_DIRECTORY = new File(System.getenv("APPDATA") + "/WeaveUpdater/");
		}
		else if (this.OS == OS_TYPE.LINUX)
		{
			this.SETTINGS_DIRECTORY = new File(System.getenv("HOME") + "/.config/WeaveUpdater/");
		}
		else if (this.OS == OS_TYPE.MAC)
		{
			this.SETTINGS_DIRECTORY = new File("~/Library/Application Support/WeaveUpdater/");
		}
		else
		{
			System.out.println("Error: Unknown OS!!");
			JOptionPane.showMessageDialog(
					null, "You have an unknown Operating System\nthat is not supported by this program.", "Error", 0);
			System.exit(1);
			return;
		}
		this.EXE_DIRECTORY = new File(this.SETTINGS_DIRECTORY, "/exe/");
		this.ZIP_DIRECTORY = new File(this.SETTINGS_DIRECTORY, "/revisions/");
		this.UNZIP_DIRECTORY = new File(this.SETTINGS_DIRECTORY, "/unzip/");
		this.SETTINGS_FILE = new File(this.SETTINGS_DIRECTORY, "/settings");
		if (isConnectedToInternet().booleanValue())
		{
			this.TOMCAT_FILE = new File(this.EXE_DIRECTORY, "/tomcat_" + getLatestTomcatVersion() + ".exe");
			this.MySQL_FILE = new File(this.EXE_DIRECTORY, "/mysql_" + getLatestMySQLVersion() + ".msi");
		}
		if (!this.SETTINGS_DIRECTORY.exists())
		{
			this.SETTINGS_DIRECTORY.mkdirs();
		}
		if (!this.ZIP_DIRECTORY.exists())
		{
			this.ZIP_DIRECTORY.mkdirs();
		}
		if (!this.EXE_DIRECTORY.exists())
		{
			this.EXE_DIRECTORY.mkdirs();
		}
		System.out.println("file structure created");
	}

	public Boolean isServiceUp(int port)
	{
		Boolean b = Boolean.valueOf(false);
		try
		{
			Socket sock = new Socket("localhost", port);
			b = Boolean.valueOf(true);
			sock.close();
		}
		catch (IOException localIOException)
		{
		}
		catch (IllegalArgumentException ex)
		{
			JOptionPane.showMessageDialog(null, "Port out of range.");
		}
		return b;
	}

	public int getLatency(String addr)
	{
		try
		{
			URL url = new URL(addr);
			long start = System.currentTimeMillis();
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(1000);
			conn.connect();
			long end = System.currentTimeMillis();
			if (conn.getResponseCode() == 200)
			{
				return new Long(end - start).intValue();
			}
			return -1;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return -1;
	}

	public Boolean isConnectedToInternet()
	{
		try
		{
			URL url = new URL("http://www.google.com/");
			URLConnection conn = url.openConnection();
			conn.getContent();
		}
		catch (IOException e)
		{
			return Boolean.valueOf(false);
		}
		try
		{
			URL url = new URL("http://www.yahoo.com/");
			url.openConnection();
			URLConnection conn = url.openConnection();
			conn.getContent();
		}
		catch (IOException e)
		{
			return Boolean.valueOf(false);
		}
		return Boolean.valueOf(true);
	}

	public void findOS()
	{
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("windows"))
		{
			this.OS = OS_TYPE.WINDOWS;
		}
		else if ((os.toLowerCase().contains("nix")) || (os.toLowerCase().contains("nux")))
		{
			this.OS = OS_TYPE.LINUX;
		}
		else if (os.toLowerCase().contains("mac"))
		{
			this.OS = OS_TYPE.MAC;
		}
		else
		{
			this.OS = OS_TYPE.UNKNOWN;
		}
		System.out.println("Detected OS: " + os);
	}

	public void findTomcatDir()
	{
		if (this.OS == OS_TYPE.WINDOWS)
		{
			String[] paths = {
					"\"HKLM\\SOFTWARE\\Apache Software Foundation\\Tomcat\\6.0\" /v InstallPath",
					"\"HKLM\\SOFTWARE\\Apache Software Foundation\\Tomcat\\7.0\" /v InstallPath",
					"\"HKLM\\SOFTWARE\\Apache Software Foundation\\Tomcat\\7.0\\Tomcat7\" /v InstallPath" };
			for (String path : paths)
			{
				System.out.println(path);
				String installPath = queryRegistry(path);
				if (installPath != null)
				{
					System.out.println("Install Path: \"" + installPath + "\"");
					this.TOMCAT_DIR = installPath;
					return;
				}
			}
			System.out.println("Ask user for install path");
			this.TOMCAT_DIR = "";
		}
	}

	public String getBestTomcatURL()
	{
		String url = getLatestTomcatURL();
		if (getLatency(url) >= 0)
		{
			return url;
		}
		url = getLatestTomcatBackupURL();
		if (getLatency(url) >= 0)
		{
			return url;
		}
		return null;
	}

	public String getBestMySQLURL()
	{
		String url = getLatestMySQLURL();
		if (getLatency(url) >= 0)
		{
			return url;
		}
		url = getLatestMySQLBackupURL();
		if (getLatency(url) >= 0)
		{
			return url;
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	public String[] getConfigFile()
	{
		String content = "";
		try
		{
			URL url = new URL(UPDATE_CONFIG);
			String line = "";
			InputStream is = url.openStream();
			DataInputStream dis = new DataInputStream(new BufferedInputStream(is));
			while ((line = dis.readLine()) != null)
			{
				content = content + line;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return content.split(";");
	}

	public String getLatestWeaveUpdaterURL()
	{
		for (String s : getConfigFile())
		{
			if (s.contains("WeaveUpdaterURL"))
			{
				return s.substring(s.indexOf(":") + 1).trim();
			}
		}
		return null;
	}

	public String getLatestWeaveUpdaterVersion()
	{
		for (String s : getConfigFile())
		{
			if (s.contains("WeaveUpdaterVersion"))
			{
				return s.substring(s.indexOf(":") + 1).trim();
			}
		}
		return null;
	}

	public String getLatestTomcatURL()
	{
		for (String s : getConfigFile())
		{
			if (s.contains("TomcatURL"))
			{
				return s.substring(s.indexOf(":") + 1).trim();
			}
		}
		return null;
	}

	public String getLatestTomcatBackupURL()
	{
		for (String s : getConfigFile())
		{
			if (s.contains("TomcatBackupURL"))
			{
				return s.substring(s.indexOf(":") + 1).trim();
			}
		}
		return null;
	}

	public String getLatestTomcatVersion()
	{
		for (String s : getConfigFile())
		{
			if (s.contains("TomcatVersion"))
			{
				return s.substring(s.indexOf(":") + 1).trim();
			}
		}
		return null;
	}

	public String getLatestMySQLURL()
	{
		for (String s : getConfigFile())
		{
			if (s.contains("MySQLURL"))
			{
				return s.substring(s.indexOf(":") + 1).trim();
			}
		}
		return null;
	}

	public String getLatestMySQLBackupURL()
	{
		for (String s : getConfigFile())
		{
			if (s.contains("MySQLBackupURL"))
			{
				return s.substring(s.indexOf(":") + 1).trim();
			}
		}
		return null;
	}

	public String getLatestMySQLVersion()
	{
		for (String s : getConfigFile())
		{
			if (s.contains("MySQLVersion"))
			{
				return s.substring(s.indexOf(":") + 1).trim();
			}
		}
		return null;
	}

	private String queryRegistry(String cmd)
	{
		Process proc = null;
		try
		{
			proc = Runtime.getRuntime().exec("reg query " + cmd);

			StreamReader reader = new StreamReader(proc.getInputStream());
			reader.start();

			int result = proc.waitFor();
			if (result != 0)
			{
				BufferedReader errorReader = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
				System.out.println(errorReader.readLine() + "\n");
				return null;
			}
			reader.join();

			String output = reader.getResult();
			if (!output.contains("\t"))
			{
				return null;
			}
			String[] parsed = output.split("\t");

			return parsed[(parsed.length - 1)].split("\r\n")[0];
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	private class StreamReader
			extends Thread
	{
		private InputStream is;
		private StringWriter sw = new StringWriter();

		public StreamReader(InputStream is)
		{
			this.is = is;
		}

		public void run()
		{
			try
			{
				int c;
				while ((c = this.is.read()) != -1)
				{
					this.sw.write(c);
				}
			}
			catch (IOException localIOException)
			{
			}
		}

		public String getResult()
		{
			return this.sw.toString();
		}
	}
}
