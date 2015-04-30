package weave.plugins;

import static weave.utils.TraceUtils.STDERR;
import static weave.utils.TraceUtils.trace;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import weave.Function;
import weave.Settings;
import weave.configs.JettyConfig;
import weave.managers.DownloadManager;
import weave.utils.BugReportUtils;
import weave.utils.EnvironmentUtils;

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
	
	private Function onDownloadCompleteCallback = new Function() {
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
	};
	
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

					DownloadManager.init("plugin")
						.setLabel(progressLabel)
						.setProgressbar(progressbar)
						.downloadFrom(getPluginDownloadURL())
						.extractTo(getPluginDownloadFile())
						.installTo(getPluginBaseDirectory())
						.callback(onDownloadCompleteCallback)
						.start();
					
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
				
			}
		});
		panel.add(removeButton);
		
		return getPluginPanel();
	}
	
	public void setAllButtonsEnabled(boolean b)
	{
		installButton.setEnabled(b);
		removeButton.setEnabled(b);
	}
}
