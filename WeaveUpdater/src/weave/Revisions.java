package weave;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public class Revisions
{
	public static int checkForUpdates(boolean save)
	{
		if (!Settings.instance().isConnectedToInternet().booleanValue())
		{
			return -2;
		}
		try
		{
			Settings.instance().getClass();
			URL url = new URL(Settings.instance().UPDATE_URL);

			URLConnection conn = url.openConnection();

			String urlFileName = conn.getHeaderField("Content-Disposition");

			int pos = urlFileName != null
					? urlFileName.indexOf("filename=") : -1;
			if (pos == -1)
			{
				return -1;
			}
			String updateFileName = Settings.instance().ZIP_DIRECTORY.getPath() + "/" + urlFileName.substring(pos + 9);
			File updateFile = new File(updateFileName);
			if (save)
			{
				Settings.instance().LAST_UPDATE_CHECK = new SimpleDateFormat("M/dd/yy h:mm a").format(Calendar.getInstance().getTime());
				Settings.instance().writeSettings();
			}
			return updateFile.exists()
					? 0 : 1;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return -1;
	}

	public static void extractZip(String fileName, final JProgressBar progBar, final JButton button)
	{
		progBar.setStringPainted(true);
		progBar.setString("Extracting Files...");
		progBar.setIndeterminate(false);
		final String finalFileName = fileName;
		Thread t = new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					int i = 0;
					ZipFile zipFile = new ZipFile(finalFileName);
					Enumeration<?> enu = zipFile.entries();

					progBar.setMaximum(zipFile.size());
					progBar.setValue(i);
					while (enu.hasMoreElements())
					{
						ZipEntry zipEntry = (ZipEntry) enu.nextElement();

						String name = zipEntry.getName();

						File file = new File(Settings.instance().UNZIP_DIRECTORY.getPath() + "/" + name);
						if (name.endsWith("/"))
						{
							file.mkdirs();
						}
						else
						{
							InputStream is = zipFile.getInputStream(zipEntry);
							FileOutputStream fos = new FileOutputStream(file);
							byte[] bytes = new byte[1024];
							int length;
							while ((length = is.read(bytes)) >= 0)
							{
								fos.write(bytes, 0, length);
							}
							is.close();
							fos.close();
							progBar.setValue(++i);
							Thread.sleep(100L);
						}
					}
					zipFile.close();

					progBar.setMaximum(100);
					progBar.setString("Files Extracted Successfully");
					progBar.setIndeterminate(false);
					progBar.setValue(100);
					Revisions.moveExtractedFiles(finalFileName, progBar, button);
				}
				catch (Exception e)
				{
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Error extracting build "
							+ Revisions.getRevisionName(finalFileName)
							+ ". The version is currupt.\n\nPlease delete this revision.", "Error", 0);

					progBar.setStringPainted(true);
					progBar.setString("");
					progBar.setIndeterminate(false);
					progBar.setMaximum(100);
					progBar.setValue(0);
					WeaveUpdater.updater.postSP.deleteButton.setEnabled(true);
					WeaveUpdater.updater.postSP.deploy.setEnabled(true);
				}
			}
		});
		t.start();
	}

	private static void moveExtractedFiles(String zipFileName, JProgressBar progBar, JButton button)
		throws Exception
	{
		String[] files = Settings.instance().UNZIP_DIRECTORY.list();
		File releaseDir = new File(Settings.instance().UNZIP_DIRECTORY.getPath() + "/" + files[0]);
		File webapps = new File(Settings.instance().TOMCAT_DIR + "/webapps/");
		File ROOT = new File(webapps, "ROOT");
		String[] releaseFiles = releaseDir.list();
		for (int i = 0; i < releaseFiles.length; i++)
		{
			String fileName = releaseFiles[i];
			int extN = fileName.lastIndexOf('.');
			if (extN == -1)
			{
				if (fileName.equals("ROOT"))
				{
					File rootDir = new File(releaseDir, "ROOT");
					String[] rootFiles = rootDir.list();

					progBar.setStringPainted(true);
					progBar.setString("Installing Files...");
					progBar.setMaximum(rootFiles.length);
					for (int j = 0; j < rootFiles.length; j++)
					{
						String rootFileName = rootFiles[j];
						File unzipedFile = new File(rootDir, rootFileName);
						File movedFile = new File(ROOT, rootFileName);
						if (movedFile.exists())
						{
							movedFile.delete();
						}
						unzipedFile.renameTo(movedFile);
						progBar.setValue(j + 1);

						Thread.sleep(300L);
					}
				}
			}
			else
			{
				String ext = fileName.substring(extN + 1, fileName.length());
				if (ext.equals("war"))
				{
					File unzipedWar = new File(releaseDir, fileName);
					File movedWar = new File(webapps, fileName);
					if (movedWar.exists())
					{
						movedWar.delete();
					}
					unzipedWar.renameTo(movedWar);
				}
			}
		}
		progBar.setString("Installation Finished. Please wait...");
		progBar.setIndeterminate(false);
		progBar.setMaximum(100);
		progBar.setValue(100);

		Settings.instance().CURRENT_INSTALL_VER = getRevisionName(zipFileName);

		recursiveDelete(Settings.instance().UNZIP_DIRECTORY);
		Settings.instance().writeSettings();
		button.setEnabled(true);
		WeaveUpdater.updater.cancelButton.setEnabled(true);

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				WeaveUpdater.updater.postSP.checkButton.doClick();
			}
		});
	}

	public static int getNumberOfRevisions()
	{
		if (!Settings.instance().ZIP_DIRECTORY.exists())
		{
			return 0;
		}
		return Settings.instance().ZIP_DIRECTORY.list().length;
	}

	public static long getSizeOfRevisions()
	{
		if (!Settings.instance().ZIP_DIRECTORY.exists())
		{
			return 0L;
		}
		long size = 0L;
		File[] files = Settings.instance().ZIP_DIRECTORY.listFiles();
		for (int i = 0; i < files.length; i++)
		{
			size += files[i].length();
		}
		return size;
	}

	public static boolean pruneRevisions()
	{
		ArrayList<File> files = getRevisionData();
		Iterator<File> it = files.iterator();
		File file = null;
		int i = 0;
		long[] mods = new long[files.size()];
		while (it.hasNext())
		{
			mods[(i++)] = ((File) it.next()).lastModified();
		}
		i = 0;
		it = files.iterator();
		while (it.hasNext())
		{
			file = (File) it.next();
			if ((i == 0) || (i == 1))
			{
				i++;
			}
			else if (i == Math.ceil((mods.length - 2) / 2) + 2.0D)
			{
				i++;
			}
			else if (i == mods.length - 2)
			{
				i++;
			}
			else
			{
				file.delete();
				i++;
			}
		}
		return true;
	}

	public static String getRevisionName(String n)
	{
		return n.substring(n.lastIndexOf('-') + 1, n.lastIndexOf('.')).toUpperCase();
	}

	public static ArrayList<File> getRevisionData()
	{
		File[] files = Settings.instance().ZIP_DIRECTORY.listFiles();
		ArrayList<File> sortedFiles = new ArrayList<File>();
		for (int i = 0; i < files.length; i++)
		{
			sortedFiles.add(files[i]);
		}
		Collections.sort(sortedFiles, new Comparator<File>()
		{
			public int compare(File o1, File o2)
			{
				if (o1.lastModified() < o2.lastModified())
				{
					return 1;
				}
				if (o1.lastModified() > o2.lastModified())
				{
					return -1;
				}
				return 0;
			}
		});
		return sortedFiles;
	}

	public static boolean recursiveDelete(File dir)
	{
		if (dir.isDirectory())
		{
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++)
			{
				recursiveDelete(new File(dir, children[i]));
			}
		}
		return dir.delete();
	}
}
