package weave.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;
import javax.swing.JPanel;

import weave.Settings;
import weave.utils.RemoteUtils;

@SuppressWarnings("serial")
public class SocketStatus extends JPanel
{
	private JLabel label;
	private JLabel status;
	private Timer timer;
	
	private String desc;
	private String host;
	private int port;
	private int timeout;
	
	public SocketStatus(String label, String host, int port)
	{
		this.desc = label;
		this.host = host;
		this.port = port;
		this.timeout = 10000;
		
		createUI();
	}
	public SocketStatus(String label, String host, int port, int timeout)
	{
		this.desc = label;
		this.host = host;
		this.port = port;
		this.timeout = timeout;
	
		createUI();
	}
	private void createUI()
	{
		setLayout(new GridLayout(1, 2));
		
		label = new JLabel(desc);
		label.setFont(new Font(Settings.FONT, Font.BOLD, 13));
		label.setForeground(Color.BLACK);
		label.setVisible(true);
		add(label);
		
		status = new JLabel("");
		status.setFont(new Font(Settings.FONT, Font.BOLD, 12));
		status.setVisible(true);
		add(status);
	}
	
	public void update(boolean useRemoteAPI)
	{
		if( !isShowing() )
			return;
		
		if( useRemoteAPI )
		{
			if( RemoteUtils.isServiceUp(host, port) )
			{
				status.setForeground(new Color(0x004F00));
				status.setText("Online");
			}
			else
			{
				status.setForeground(Color.RED);
				status.setText("Offline");
			}
		}
		else
		{
			if( Settings.isServiceUp(host, port) )
			{
				status.setForeground(new Color(0x004F00));
				status.setText("Online");
			}
			else
			{
				status.setForeground(Color.RED);
				status.setText("Offline");
			}
		}
	}
	public void startMonitor()
	{
		startMonitor(false);
	}
	public void startMonitor(final boolean useRemoteAPI)
	{
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				update(useRemoteAPI);
			}
		}, 2000, timeout);
	}
	public void stopMonitor()
	{
		timer.cancel();
	}
}
