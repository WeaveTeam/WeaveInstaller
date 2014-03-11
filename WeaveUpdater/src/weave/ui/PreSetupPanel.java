package weave.ui;

import java.awt.Color;
import java.awt.Font;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import weave.Settings;
import weave.inc.SetupPanel;

public class PreSetupPanel
		extends SetupPanel
{
	private static final long serialVersionUID = 1L;
	public JButton updateButton = null;
	public JButton settingsButton = null;

	public PreSetupPanel()
			throws Exception
	{
		this.maxPanels = 1;

		setLayout(null);
		setSize(350, 325);
		setBounds(0, 0, 350, 325);

		JPanel panel = null;
		for (int i = 0; i < this.maxPanels; i++)
		{
			switch (i)
			{
				case 0:
					panel = createWelcomeMenu();
			}
			this.panels.add(panel);
			add(panel);
		}
		hidePanels();
	}

	public JPanel createWelcomeMenu()
	{
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBounds(0, 0, 350, 325);
		panel.setBackground(new Color(16777215));

		JLabel welcome = new JLabel("Welcome to Weave Setup Wizard");
		welcome.setFont(new Font("Corbel", 1, 17));
		welcome.setBounds(30, 30, 290, 50);

		JTextArea info = new JTextArea(3, 8);
		info.setEditable(false);
		info.setLineWrap(true);
		info.setText("The Setup Wizard will install Weave on your\ncomputer. Click Next to continue or Cancel to\nexit the Setup Wizard.");

		info.setFont(new Font("Corbel", 0, 14));
		info.setBounds(30, 100, 290, 60);

		panel.add(welcome);
		panel.add(info);
		return panel;
	}

	public JPanel createOptionsMenu()
		throws Exception
	{
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBounds(0, 0, 350, 325);
		panel.setBackground(new Color(16777215));

		ImageIcon settingsIcon = new ImageIcon(ImageIO.read(PreSetupPanel.class.getResource("/resources/settings.png")));
		this.settingsButton = new JButton(settingsIcon);
		this.settingsButton.setBounds(50, 70, 40, 40);
		panel.add(this.settingsButton);

		JLabel settingsLabel = new JLabel("Configure Settings");
		settingsLabel.setFont(new Font("Corbel", 0, 22));
		settingsLabel.setBounds(100, 70, 250, 40);
		panel.add(settingsLabel);

		ImageIcon downloadIcon = new ImageIcon(ImageIO.read(PreSetupPanel.class.getResource("/resources/download.png")));
		this.updateButton = new JButton(downloadIcon);
		this.updateButton.setBounds(50, 210, 40, 40);
		this.updateButton.setEnabled(Settings.instance().settingsExists().booleanValue());
		panel.add(this.updateButton);

		JLabel downloadLabel = new JLabel("Update Weave");
		downloadLabel.setFont(new Font("Corbel", 0, 22));
		downloadLabel.setBounds(100, 210, 250, 40);
		panel.add(downloadLabel);

		return panel;
	}
}
