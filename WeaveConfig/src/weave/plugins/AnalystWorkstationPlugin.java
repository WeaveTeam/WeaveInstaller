package weave.plugins;

import static weave.utils.TraceUtils.STDERR;
import static weave.utils.TraceUtils.trace;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import weave.Settings;
import weave.configs.IConfig;
import weave.managers.ConfigManager;
import weave.managers.DownloadManager;
import weave.misc.Function;
import weave.utils.BugReportUtils;
import weave.utils.EnvironmentUtils;
import weave.utils.LaunchUtils;
import weave.utils.RemoteUtils;

public class AnalystWorkstationPlugin extends Plugin 
{
	public static final String NAME = "WeaveAnalyst";
	public static final String HOMEPAGEURL = "http://info.oicweave.org/projects/weave/wiki/Weave_Analyst";
	public static final String DESCRIPTION = "WeaveAnalyst is a computational support engine that allows " +
											 "you to easily analyze any amount of data.<br>" +
											 "You can explore data for patterns and trends, build a data model, " +
											 "or present information in a visually represented format.";
	
	private static AnalystWorkstationPlugin _instance = null;
	public static AnalystWorkstationPlugin getPlugin()
	{
		if( _instance == null )
			_instance = new AnalystWorkstationPlugin();
		return _instance;
	}
	
	public AnalystWorkstationPlugin()
	{
		super(NAME);
		
		String filename = "";
		String url = RemoteUtils.getConfigEntry(RemoteUtils.AWS_URL);
		if( url != null ) 
			filename = url.substring(url.lastIndexOf("/") + 1);
		
		setPluginHomepageURL(HOMEPAGEURL);
		setPluginDescription(DESCRIPTION);
		setPluginDownloadURL(url);
		setPluginDownloadFile("${" + EnvironmentUtils.DOWNLOAD_DIR + "}/" + filename);
		setPluginBaseDirectory("${" + EnvironmentUtils.WEBAPPS + "}/");
	}

	private JPanel panel = null;
	private JLabel iconLabel = null;
	private JLabel nameLabel = null;
	private JEditorPane description = null;
	private JButton openButton = null;
	private JButton installButton = null;
	private JButton removeButton = null;
	private JProgressBar progressbar = null;
	private JLabel progressLabel = null;

	private Function onDownloadCompleteCallback = new Function() {
		@Override
		public void run() {
			setAllButtonsEnabled(true);
			progressbar.setValue(0);
			progressbar.setIndeterminate(true);
			progressbar.setVisible(false);
			progressLabel.setText("");
			progressLabel.setVisible(false);
			try {
				Settings.cleanUp();
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				trace(STDERR, e);
				BugReportUtils.showBugReportDialog(e);
			}
			getPluginPanel();
		}
	};
	
	@Override
	public JPanel getPluginPanel()
	{
		if( panel != null ) 
			return panel;
		
		panel = super.getPluginPanel();
		
		iconLabel = new JLabel();
		iconLabel.setBounds(174, 10, 106, 30);
		iconLabel.setHorizontalAlignment(JLabel.CENTER);
		iconLabel.setVerticalAlignment(JLabel.CENTER);
		panel.add(iconLabel);
		
		nameLabel = new JLabel(getPluginName());
		nameLabel.setBounds(10, 10, 150, 25);
		nameLabel.setFont(new Font(Settings.FONT, Font.BOLD, 14));
		panel.add(nameLabel);
		
		description = new JEditorPane();
		description.setBounds(10, 50, 270, 140);
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

					DownloadManager.init("plugin")
						.setLabel(progressLabel)
						.setProgressbar(progressbar)
						.downloadFrom(getPluginDownloadURL())
						.extractTo(getPluginDownloadFile())
						.installTo(getPluginBaseDirectory())
						.callback(onDownloadCompleteCallback)
						.start();
					
				} catch (MalformedURLException | InterruptedException ex) {
					trace(STDERR, ex);
					BugReportUtils.showBugReportDialog(ex);
				}
			}
		});
		panel.add(installButton);
		
		removeButton = new JButton("Remove");
		removeButton.setBounds(190, 235, 90, 25);
		removeButton.setVisible(false);
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("remove");
			}
		});
		panel.add(removeButton);

		openButton = new JButton("Open");
		openButton.setBounds(190, 270, 90, 25);
		openButton.setVisible(false);
		openButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				IConfig servlet = ConfigManager.getConfigManager().getActiveContainer();
				if( servlet == null )
					return;
				
				try {
					LaunchUtils.browse("http://" + 
							Settings.LOCALHOST + ":" + 
							servlet.getPort() + 
							"/aws/");
				} catch (IOException | URISyntaxException | InterruptedException ex) {
					trace(STDERR, ex);
					BugReportUtils.showBugReportDialog(ex);
				}
			}
		});
		panel.add(openButton);
		
		progressbar = new JProgressBar();
		progressbar.setBounds(20, 235, 260, 25);
		progressbar.setIndeterminate(true);
		progressbar.setStringPainted(false);
		progressbar.setValue(0);
		progressbar.setVisible(false);
		panel.add(progressbar);
		
		progressLabel = new JLabel();
		progressLabel.setBounds(20, 270, 260, 25);
		progressLabel.setFont(new Font(Settings.FONT, Font.PLAIN, 10));
		progressLabel.setText("");
		progressLabel.setVisible(false);
		panel.add(progressLabel);
		
		return getPluginPanel();
	}
	
	@Override public void pluginPanelRefresh()
	{
		super.pluginPanelRefresh();
		
		if( isPluginInstalled() )
		{
			installButton.setText("Reinstall");
			installButton.setEnabled(!Settings.isOfflineMode());
			removeButton.setVisible(true);
			removeButton.setEnabled(false);
			openButton.setVisible(true);
		}
		else
		{
			installButton.setText("Install");
			installButton.setEnabled(!Settings.isOfflineMode());
			removeButton.setVisible(false);
			openButton.setVisible(false);
		}
	}
	
	public void setAllButtonsEnabled(boolean b)
	{
		installButton.setEnabled(b);
		removeButton.setEnabled(false);
		openButton.setEnabled(b);
	}
}
