package weave.ui;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;

@SuppressWarnings("serial")
public class CustomCheckbox extends JCheckBox 
{
	public CustomCheckbox(String text)
	{
		super(text);
	}
	
	@Override
	protected void processKeyEvent(KeyEvent e) {

	}
	
	@Override
	protected void processMouseEvent(MouseEvent e) {

	}
}
