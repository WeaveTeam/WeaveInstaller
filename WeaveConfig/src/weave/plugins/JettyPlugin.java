package weave.plugins;

import static weave.utils.TraceUtils.STDERR;
import static weave.utils.TraceUtils.trace;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import weave.Settings;
import weave.configs.JettyConfig;
import weave.managers.DownloadManager;
import weave.managers.ResourceManager;
import weave.misc.Function;
import weave.utils.BugReportUtils;
import weave.utils.EnvironmentUtils;
import weave.utils.ImageUtils;

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
	private JLabel iconLabel = null;
	private JLabel nameLabel = null;
	private JEditorPane description = null;
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
			}
			pluginPanelRefresh();
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
		try {
			iconLabel.setIcon(new ImageIcon(ImageUtils.fit(ImageIO.read(ResourceManager.IMAGE_JETTY), 106, 30)));
		} catch (IOException e) {
			trace(STDERR, e);
			BugReportUtils.showBugReportDialog(e);
		}
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
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		panel.add(removeButton);

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

		return panel;
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
		}
		else
		{
			installButton.setText("Install");
			installButton.setEnabled(!Settings.isOfflineMode());
			removeButton.setVisible(false);
		}
	}
	
	public void setAllButtonsEnabled(boolean b)
	{
		installButton.setEnabled(b);
		removeButton.setEnabled(false);
	}
}
