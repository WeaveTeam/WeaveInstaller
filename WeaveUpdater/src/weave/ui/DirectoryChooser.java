package weave.ui;

import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import weave.Settings;

public class DirectoryChooser
		extends JPanel
{
	private static final long serialVersionUID = 1L;
	public JTextField textField;
	public JFileChooser fileChooser;
	JLabel label;

	public DirectoryChooser(String _label)
	{
		setLayout(new GridLayout(1, 2));

		this.fileChooser = new JFileChooser("C:\\");
		this.label = new JLabel(_label);
		this.label.setFont(new Font("Serif", 1, 14));

		this.textField = new JTextField(Settings.instance().TOMCAT_DIR, 19);
		this.textField.setEditable(false);

		add(this.label);
		add(this.textField);
	}
}
