package weave.plugins;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;

import weave.Settings;
import weave.utils.EnvironmentUtils;
import weave.utils.RemoteUtils;

public class AnalystWorkstationPlugin extends Plugin 
{
	public static final String NAME = "Analyst Workstation";
	public static final String HOMEPAGEURL = "http://info.oicweave.org/projects/weave/wiki/Weave_Analyst";
	public static final String DESCRIPTION = "Analyst workstation for Weave";
	
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
		setPluginDownloadURL(url);
		setPluginDescription(DESCRIPTION);
		setPluginDownloadFile("${" + EnvironmentUtils.DOWNLOAD_DIR + "}/" + filename);
		setPluginBaseDirectory("${" + EnvironmentUtils.WEBAPPS + "}/");
	}

	private JPanel panel = null;
	private JLabel nameLabel = null;
	private JEditorPane description = null;
	private JButton installButton = null;
	private JButton removeButton = null;
	
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
				System.out.println("install");
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
}
