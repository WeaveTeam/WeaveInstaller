package weave.ui;

import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import weave.Revisions;
import weave.Settings;
import weave.inc.SetupPanel;

public class PostSetupPanel
		extends SetupPanel
{
	private static final long serialVersionUID = 1L;
	public JButton installButton = new JButton("Install");
	public JButton checkButton = new JButton("Refresh");
	public JButton deploy = new JButton("Deploy");
	public JButton deleteButton = new JButton("Delete");
	public JButton pruneButton = new JButton("<html><center>Auto <br> Clean</center></html>");
	public JButton launchAdmin = new JButton("Launch Admin Console");
	public ProgressUpdate progress = new ProgressUpdate();
	public WeaveStats weaveStats = new WeaveStats();
	public RevisionTable revisionTable = new RevisionTable();
	private Timer timer = new Timer();
	public JLabel zipLabelSpeed;
	public JLabel zipLabelTimeleft;
	public JLabel zipLabelSpeedHolder;
	public JLabel zipLabelTimeleftHolder;
	public JLabel zipLabelSizeDownloadHolder;

	public PostSetupPanel()
	{
		this.maxPanels = 1;

		setLayout(null);
		setSize(350, 325);
		setBounds(0, 0, 350, 325);

		JPanel panel = createPostSetupPanel();

		this.panels.add(panel);
		add(panel);

		setVisible(true);

		this.timer.schedule(new TimerTask()
		{
			public void run()
			{
				int updateCheck = Revisions.checkForUpdates(false);
				PostSetupPanel.this.weaveStats.refresh(updateCheck);
				if (updateCheck == -2)
				{
					PostSetupPanel.this.progress.progBar.setStringPainted(true);
					PostSetupPanel.this.progress.progBar.setString("No Internet Connection");
					PostSetupPanel.this.progress.progBar.setValue(0);
				}
				PostSetupPanel.this.revisionTable.updateTableData();
				PostSetupPanel.this.installButton.setEnabled((updateCheck == 1)
						&& (!Settings.instance().TOMCAT_DIR.equals("")));
			}
		}, 1000L);
	}

	public JPanel createPostSetupPanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBounds(0, 0, 350, 325);
		panel.setBackground(new Color(16777215));

		this.zipLabelSpeed = new JLabel("Download Rate:");
		this.zipLabelTimeleft = new JLabel("Time left:");
		this.zipLabelSpeedHolder = new JLabel();
		this.zipLabelTimeleftHolder = new JLabel();
		this.zipLabelSizeDownloadHolder = new JLabel();

		this.zipLabelSpeed.setBounds(10, 60, 100, 20);
		this.zipLabelSpeed.setFont(new Font("Serif", 0, 13));
		this.zipLabelSpeed.setVisible(false);
		this.zipLabelTimeleft.setBounds(10, 80, 100, 20);
		this.zipLabelTimeleft.setFont(new Font("Serif", 0, 13));
		this.zipLabelTimeleft.setVisible(false);
		this.zipLabelSpeedHolder.setBounds(150, 60, 170, 20);
		this.zipLabelSpeedHolder.setHorizontalAlignment(4);
		this.zipLabelTimeleftHolder.setBounds(150, 80, 170, 20);
		this.zipLabelTimeleftHolder.setHorizontalAlignment(4);
		this.zipLabelSizeDownloadHolder.setBounds(150, 100, 170, 20);
		this.zipLabelSizeDownloadHolder.setHorizontalAlignment(4);
		this.zipLabelSizeDownloadHolder.setFont(new Font("Serif", 0, 13));

		this.weaveStats.setBounds(10, 10, 230, 50);
		this.installButton.setBounds(250, 35, 80, 23);
		this.installButton.setToolTipText("Download the latest version of Weave and install it.");
		this.installButton.setEnabled(false);
		this.checkButton.setBounds(250, 10, 80, 23);
		this.checkButton.setToolTipText("Check for a new version of Weave");
		this.progress.setBounds(10, 120, 320, 20);
		this.progress.addPropertyChangeListener("ZIP_SPEED", new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				PostSetupPanel.this.zipLabelSpeedHolder.setText(PostSetupPanel.this.progress.zipInfo.strSpeed);
			}
		});
		this.progress.addPropertyChangeListener("ZIP_TIMELEFT", new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				PostSetupPanel.this.zipLabelTimeleftHolder.setText(PostSetupPanel.this.progress.zipInfo.strTimeleft);
			}
		});
		this.progress.addPropertyChangeListener("ZIP_SIZEDOWNLOADED", new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				PostSetupPanel.this.zipLabelSizeDownloadHolder.setText(PostSetupPanel.this.progress.zipInfo.strSizeDownloaded
						+ " of " + PostSetupPanel.this.progress.zipInfo.strTotalSize);
			}
		});
		this.revisionTable.setBounds(10, 150, 230, 130);
		this.deploy.setBounds(250, 150, 80, 25);
		this.deploy.setToolTipText("Install Weave from a backup revision, selected on the left in the table.");
		this.deleteButton.setBounds(250, 180, 80, 25);
		this.deleteButton.setToolTipText("Delete an individual revision, selected on the left in the table.");
		this.pruneButton.setBounds(250, 210, 80, 40);
		this.pruneButton.setToolTipText("Auto-delete older revisions to free up space on your hard drive.");
		this.launchAdmin.setBounds(10, 290, 230, 25);
		this.launchAdmin.setToolTipText("Open up the Admin Console");

		panel.add(this.zipLabelSpeed);
		panel.add(this.zipLabelTimeleft);
		panel.add(this.zipLabelSpeedHolder);
		panel.add(this.zipLabelTimeleftHolder);
		panel.add(this.zipLabelSizeDownloadHolder);

		panel.add(this.weaveStats);
		panel.add(this.installButton);
		panel.add(this.checkButton);
		panel.add(this.progress);
		panel.add(this.revisionTable);
		panel.add(this.deploy);
		panel.add(this.deleteButton);
		panel.add(this.pruneButton);
		panel.add(this.launchAdmin);

		return panel;
	}
}
