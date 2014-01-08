/*
    Weave (Web-based Analysis and Visualization Environment)
    Copyright (C) 2008-2011 University of Massachusetts Lowell

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
import weave.inc.SetupPanel;
import weave.plugins.Tomcat;

@SuppressWarnings("serial")
public class PostSetupPanel extends SetupPanel
{
	public JButton 				installButton 	= new JButton("Install");
	public JButton 				checkButton 	= new JButton("Refresh");
	public JButton 				revertButton 	= new JButton("Revert");
	public JButton 				deleteButton 	= new JButton("Delete");
	public JButton				pruneButton		= new JButton("<html><center>Auto <br> Clean</center></html>");
	public JButton 				launchAdmin 	= new JButton("Launch Admin Console");
	public ProgressUpdate 		progress 		= new ProgressUpdate();
	public WeaveStats 			weaveStats 		= new WeaveStats();
	public RevisionTable 		revisionTable 	= new RevisionTable();
	private Timer				timer 			= new Timer();
	
	public JLabel				zipLabelSpeed, zipLabelTimeleft; 
	public JLabel				zipLabelSpeedHolder, zipLabelTimeleftHolder, zipLabelSizeDownloadHolder;
	
	public PostSetupPanel()
	{
		maxPanels = 1;
		
		setLayout(null);
		setSize(350, 325);
		setBounds(0, 0, 350, 325);
		
		JPanel panel = createPostSetupPanel();
		
		panels.add(panel);
		add(panel);

		setVisible(true);

		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				int updateCheck = Revisions.checkForUpdates(false);
				weaveStats.refresh(updateCheck);

				if( updateCheck == -2 )
				{
					progress.progBar.setStringPainted(true);
					progress.progBar.setString("No Internet Connection");
					progress.progBar.setValue(0);
				}
				
				revisionTable.updateTableData();
				installButton.setEnabled((updateCheck == 1) && !Tomcat.instance().TOMCAT_HOME.getPath().equals(""));
			}
		}, 1000);
	}

	public JPanel createPostSetupPanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBounds(0, 0, 350, 325);
		panel.setBackground(new Color(0xFFFFFF));

		zipLabelSpeed = new JLabel("Download Rate:");
		zipLabelTimeleft = new JLabel("Time left:");
		zipLabelSpeedHolder = new JLabel();
		zipLabelTimeleftHolder = new JLabel();
		zipLabelSizeDownloadHolder = new JLabel();
		
		// ======== SET COORDINATES ======== //
		zipLabelSpeed.setBounds(10, 60, 100, 20);
		zipLabelSpeed.setFont(new Font("Serif", Font.PLAIN, 13));
		zipLabelSpeed.setVisible(false);
		zipLabelTimeleft.setBounds(10, 80, 100, 20);
		zipLabelTimeleft.setFont(new Font("Serif", Font.PLAIN, 13));
		zipLabelTimeleft.setVisible(false);
		zipLabelSpeedHolder.setBounds(150, 60, 170, 20);
		zipLabelSpeedHolder.setHorizontalAlignment(JLabel.RIGHT);
		zipLabelTimeleftHolder.setBounds(150, 80, 170, 20);
		zipLabelTimeleftHolder.setHorizontalAlignment(JLabel.RIGHT);
		zipLabelSizeDownloadHolder.setBounds(150, 100, 170, 20);
		zipLabelSizeDownloadHolder.setHorizontalAlignment(JLabel.RIGHT);
		zipLabelSizeDownloadHolder.setFont(new Font("Serif", Font.PLAIN, 13));
		
		weaveStats.setBounds(10, 10, 230, 50);
		installButton.setBounds(250, 35, 80, 23);	installButton.setToolTipText("Download the latest version of Weave and install it.");
		installButton.setEnabled(false);
		checkButton.setBounds(250, 10, 80, 23);		checkButton.setToolTipText("Check for a new version of Weave");
		progress.setBounds(10, 120, 320, 20);
		progress.addPropertyChangeListener(ProgressUpdate.ZIP_SPEED, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				zipLabelSpeedHolder.setText(progress.zipInfo.strSpeed);
			}
		});
		progress.addPropertyChangeListener(ProgressUpdate.ZIP_TIMELEFT, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				zipLabelTimeleftHolder.setText(progress.zipInfo.strTimeleft);
			}
		});
		progress.addPropertyChangeListener(ProgressUpdate.ZIP_SIZEDOWNLOADED, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				zipLabelSizeDownloadHolder.setText(progress.zipInfo.strSizeDownloaded + " of " + progress.zipInfo.strTotalSize);
			}
		});
		revisionTable.setBounds(10, 150, 230, 130);	
		revertButton.setBounds(250, 150, 80, 25);	revertButton.setToolTipText("Install Weave from a backup revision, selected on the left in the table.");
		deleteButton.setBounds(250, 180, 80, 25);	deleteButton.setToolTipText("Delete an individual revision, selected on the left in the table.");
		pruneButton.setBounds(250, 210, 80, 40);	pruneButton.setToolTipText("Auto-delete older revisions to free up space on your hard drive.");
		launchAdmin.setBounds(10, 290, 230, 25);	launchAdmin.setToolTipText("Open up the Admin Console");

		// ======== ADD TO PANEL ======== //
		panel.add(zipLabelSpeed);
		panel.add(zipLabelTimeleft);
		panel.add(zipLabelSpeedHolder);
		panel.add(zipLabelTimeleftHolder);
		panel.add(zipLabelSizeDownloadHolder);
		
		panel.add(weaveStats);
		panel.add(installButton);
		panel.add(checkButton);
		panel.add(progress);
		panel.add(revisionTable);
		panel.add(revertButton);
		panel.add(deleteButton);
		panel.add(pruneButton);
		panel.add(launchAdmin);
		
		return panel;
	}
}
