package weave.ui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import weave.Settings;

public class WeaveStats
		extends JPanel
{
	private static final long serialVersionUID = 1L;
	JLabel status = null;
	JLabel desc2 = null;
	JLabel desc = null;
	public JLabel lastUpdateTime = null;

	public WeaveStats()
	{
		setLayout(null);
		setBackground(new Color(16777215));

		this.desc = new JLabel("Weave Update:");
		this.desc.setFont(new Font("Serif", 1, 14));

		this.status = new JLabel("Loading...");
		this.status.setFont(new Font("Serif", 1, 14));

		this.desc2 = new JLabel("Last Check:");
		this.desc2.setFont(new Font("Serif", 1, 13));

		this.lastUpdateTime = new JLabel(Settings.instance().LAST_UPDATE_CHECK);
		this.lastUpdateTime.setFont(new Font("Serif", 1, 13));

		this.desc.setBounds(0, 0, 115, 25);
		this.status.setBounds(115, 0, 140, 25);
		this.desc2.setBounds(0, 25, 140, 20);
		this.lastUpdateTime.setBounds(115, 25, 140, 20);

		add(this.desc);
		add(this.status);
		add(this.desc2);
		add(this.lastUpdateTime);
	}

	public void refresh(int _status)
	{
		if (_status == 1)
		{
			this.status.setText("Update Available");
			this.status.setForeground(new Color(20224));
		}
		else if (_status == 0)
		{
			this.status.setText("Up to Date");
			this.status.setForeground(Color.BLACK);
		}
		else
		{
			this.status.setText("Updating Error");
			this.status.setForeground(Color.RED);
		}
		this.lastUpdateTime.setText(Settings.instance().LAST_UPDATE_CHECK);
	}
}
