package weave.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Status
		extends JPanel
{
	private static final long serialVersionUID = 1L;
	int lastStatus = -1;
	JLabel label = null;
	JLabel status = null;
	BufferedImage status_OK = null;
	BufferedImage status_BAD = null;

	public Status(String _label, Boolean _status)
	{
		setLayout(new GridLayout(1, 2));
		try
		{
			this.status_OK = ImageIO.read(Status.class.getResource("/resources/check_19x18.png"));
			this.status_BAD = ImageIO.read(Status.class.getResource("/resources/warning_21x18.png"));
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		this.label = new JLabel(_label);
		this.label.setFont(new Font("Serif", 1, 14));

		add(this.label);

		refresh(_status);
	}

	public void refresh(Boolean _status)
	{
		if (this.lastStatus == (_status.booleanValue()
				? 1 : 0))
		{
			return;
		}
		this.lastStatus = (_status.booleanValue()
				? 1 : 0);
		if (this.status != null)
		{
			remove(this.status);
			this.status = null;
		}
		if (_status.booleanValue())
		{
			this.status = new JLabel("Running", new ImageIcon(this.status_OK), 2);
			this.status.setForeground(new Color(20224));
		}
		else
		{
			this.status = new JLabel("Not Running", new ImageIcon(this.status_BAD), 2);
			this.status.setForeground(Color.RED);
		}
		this.status.setFont(new Font("Serif", 1, 20));

		add(this.status);
		validate();
	}
}
