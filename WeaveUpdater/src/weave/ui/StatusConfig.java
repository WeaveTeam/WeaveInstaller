package weave.ui;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Timer;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import weave.Settings;

public class StatusConfig
		extends JPanel
{
	private static final long serialVersionUID = 1L;
	JLabel labelPort = null;
	public JTextField textPort = null;
	Timer inputTimer = null;

	public StatusConfig(String _label, final Settings.SERVICE_PORTS _portType)
	{
		setLayout(new GridLayout(1, 2));

		this.inputTimer = new Timer();
		this.labelPort = new JLabel(_label);
		if (_portType == Settings.SERVICE_PORTS.MySQL)
		{
			this.textPort = new JTextField(Integer.toString(Settings.instance().MySQL_PORT));
		}
		else
		{
			this.textPort = new JTextField(Integer.toString(Settings.instance().TOMCAT_PORT));
		}
		this.textPort.addKeyListener(new KeyAdapter()
		{
			public void keyReleased(KeyEvent e)
			{
				if (StatusConfig.this.textPort.getText().equals(""))
				{
					JOptionPane.showMessageDialog(null, "Port value cannot be empty");
					if (_portType == Settings.SERVICE_PORTS.MySQL)
					{
						Settings.instance().MySQL_PORT = 0;
					}
					else
					{
						Settings.instance().TOMCAT_PORT = 0;
					}
					return;
				}
				try
				{
					int port = Integer.parseInt(StatusConfig.this.textPort.getText());
					if ((port > 65535) || (port < 1))
					{
						JOptionPane.showMessageDialog(null, "Input values: 0 < port < 65535");
						if (_portType == Settings.SERVICE_PORTS.MySQL)
						{
							Settings.instance().MySQL_PORT = 0;
						}
						else
						{
							Settings.instance().TOMCAT_PORT = 0;
						}
						return;
					}
					if (_portType == Settings.SERVICE_PORTS.MySQL)
					{
						Settings.instance().MySQL_PORT = port;
					}
					else
					{
						Settings.instance().TOMCAT_PORT = port;
					}
				}
				catch (NumberFormatException fe)
				{
					JOptionPane.showMessageDialog(null, "Only numbers are allowed as inputs.");
					return;
				}
			}
		});
		this.labelPort.setFont(new Font("Serif", 1, 14));

		add(this.labelPort);
		add(this.textPort);
	}
}
