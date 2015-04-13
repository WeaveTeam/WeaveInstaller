package weave.plugins;

import static weave.utils.TraceUtils.STDERR;
import static weave.utils.TraceUtils.STDOUT;
import static weave.utils.TraceUtils.put;
import static weave.utils.TraceUtils.trace;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipException;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import weave.Function;
import weave.Settings;
import weave.async.AsyncCallback;
import weave.async.AsyncObserver;
import weave.async.AsyncTask;
import weave.configs.JettyConfig;
import weave.managers.DownloadManager;
import weave.utils.BugReportUtils;
import weave.utils.DownloadUtils;
import weave.utils.EnvironmentUtils;
import weave.utils.FileUtils;
import weave.utils.TimeUtils;
import weave.utils.TransferUtils;
import weave.utils.ZipUtils;

public class JettyPlugin extends Plugin
{
	private static JettyPlugin _instance = null;
	public static JettyPlugin getPlugin()
	{
		if( _instance == null )
			_instance = new JettyPlugin();
		return _instance;
	}
	
	public JettyPlugin()
	{
		super(JettyConfig.NAME);
		
		String filename = "";
		String url = JettyConfig.getConfig().getDownloadURL();
		if( url != null )
			filename = url.substring(url.lastIndexOf("/") + 1);
		
		setPluginVersion(JettyConfig.getConfig().getInstallVersion());
		setPluginHomepageURL(JettyConfig.getConfig().getHomepageURL());
		setPluginDownloadURL(url);
		setPluginDescription(JettyConfig.getConfig().getDescription());
		setPluginDownloadFile("${" + EnvironmentUtils.DOWNLOAD_DIR + "}/" + filename);
		setPluginBaseDirectory("${" + EnvironmentUtils.PLUGINS_DIR + "}/" + getPluginName());
	}

	private JPanel panel = null;
	private JLabel nameLabel = null;
	private JEditorPane description = null;
	private JButton installButton = null;
	private JButton removeButton = null;
	private JProgressBar progressbar = null;
	private JLabel progressLabel = null;
	
	@Override
	public JPanel getPluginPanel()
	{
		if( panel != null ) 
		{
			if( isPluginInstalled() )
			{
				installButton.setText("Reinstall");
				installButton.setBounds(90, 200, 90, 25);
				removeButton.setVisible(true);
			}
			else
			{
				installButton.setText("Install");
				installButton.setBounds(190, 200, 90, 25);
				removeButton.setVisible(false);
			}
			return panel;
		}
		
		panel = super.getPluginPanel();
		
		nameLabel = new JLabel(getPluginName());
		nameLabel.setBounds(20, 10, 150, 25);
		nameLabel.setFont(new Font(Settings.FONT, Font.BOLD, 14));
		panel.add(nameLabel);
		
		description = new JEditorPane();
		description.setBounds(20, 40, 260, 140);
		description.setFont(new Font(Settings.FONT, Font.PLAIN, 11));
		description.setBackground(Color.WHITE);
		description.setContentType("text/html");
		description.setText(getPluginDescription());
		description.setEditable(false);
		panel.add(description);
		
		installButton = new JButton("Install");
		installButton.setBounds(190, 200, 90, 25);
		installButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					setAllButtonsEnabled(false);
					DownloadManager.download(getPluginDownloadURL(), getPluginDownloadFile(), getPluginBaseDirectory(),
											 progressbar, progressLabel,
											 new Function() {
												@Override
												public void run() {
													setAllButtonsEnabled(true);
													progressbar.setValue(0);
													progressbar.setIndeterminate(true);
													progressbar.setVisible(false);
													progressLabel.setText("");
													progressLabel.setVisible(false);
													getPluginPanel();
												}
											});
				} catch (MalformedURLException ex) {
					trace(STDERR, ex);
					BugReportUtils.showBugReportDialog(ex);
				} catch (InterruptedException ex) {
					trace(STDERR, ex);
					BugReportUtils.showBugReportDialog(ex);
				}
			}
		});
		panel.add(installButton);
		
		removeButton = new JButton("Remove");
		removeButton.setBounds(190, 200, 90, 25);
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("remove");
			}
		});
		panel.add(removeButton);
		
		return getPluginPanel();
	}

	private void extractPlugin(String zipFile, final String destination)
	{
		final File zip = new File(zipFile);
		
		final AsyncObserver observer = new AsyncObserver() {
			@Override
			public void onUpdate() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						progressbar.setValue( info.percent / 2 );
						progressLabel.setText( 
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
						put(STDOUT, "DONE");
						
						movePlugin(Settings.UNZIP_DIRECTORY.getAbsolutePath(), destination);
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
					observer.init(zip);
					o = ZipUtils.extract(zip, Settings.UNZIP_DIRECTORY, TransferUtils.OVERWRITE | TransferUtils.MULTIPLE_FILES, observer, 8 * TransferUtils.MB);
				} catch (ArithmeticException e) {
					trace(STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				}catch (NullPointerException e) {
					trace(STDERR, e);
					// No bug report
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

		try {
			if( !Settings.UNZIP_DIRECTORY.exists() )
				Settings.UNZIP_DIRECTORY.mkdirs();
			
			progressbar.setVisible(true);
			progressLabel.setVisible(true);
			
			progressbar.setIndeterminate(true);
			progressLabel.setText("Preparing Extraction....");
			Thread.sleep(1000);
			
			trace(STDOUT, "-> Extracting plugin..............");
			
			Settings.canQuit = false;
			
			progressLabel.setText("Extracting plugin....");
			progressbar.setIndeterminate(false);
		} catch (InterruptedException e) {
			trace(STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		}
		
		task.addCallback(callback).execute();
	}
	private void movePlugin(String source, final String destination)
	{
		final File unzippedFile = new File(source);
		
		final AsyncObserver observer = new AsyncObserver() {
			@Override
			public void onUpdate() {
				progressbar.setValue( 50 + info.percent / 2 );
				progressLabel.setText( 
						String.format(
								"Installing plugin.... %d%%", 
								50 + info.percent / 2 ) );
			}
		};
		AsyncCallback callback = new AsyncCallback() {
			@Override
			public void run(Object o) {
				int returnCode = (Integer) o;
				
				switch( returnCode ) 
				{
					case TransferUtils.COMPLETE:
						put(STDOUT, "DONE");
						progressLabel.setText("Install complete....");
						
						Settings.canQuit = true;
						System.gc();
	
						try {
							Settings.cleanUp();
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							trace(STDERR, e);
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
						File s = new File(unzippedFile, file);
						File d = new File(destination, file);
						status &= FileUtils.copy(s, d, TransferUtils.MULTIPLE_FILES | TransferUtils.OVERWRITE, observer, 8 * TransferUtils.MB);
					}
				} catch (ArithmeticException e) {
					trace(STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				} catch (FileNotFoundException e) {
					trace(STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				} catch (IOException e) {
					trace(STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				} catch (InterruptedException e) {
					trace(STDERR, e);
					BugReportUtils.showBugReportDialog(e);
				}
				return status;
			}
		};

		trace(STDOUT, "-> Installing plugin..............");

		progressLabel.setText("Installing Plugin....");
		progressbar.setIndeterminate(false);
		
		task.addCallback(callback).execute();
	}
	
	public void setAllButtonsEnabled(boolean b)
	{
		installButton.setEnabled(b);
		removeButton.setEnabled(b);
	}
}
