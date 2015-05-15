package weave.ui;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import weave.Settings.INSTALL_ENUM;

@SuppressWarnings("serial")
public class DropDownButton extends JButton
{
	private JPopupMenu popupMenu = new JPopupMenu("");
	private ActionListener listener = null;
	private Timer pressedTimer = new Timer();
	private Boolean menuShown = false;
	
	public DropDownButton()
	{
		super();
		attachMouseHandler();
	}
	public DropDownButton(Icon icon)
	{
		super(icon);
		attachMouseHandler();
	}
	public DropDownButton(String str)
	{
		super(str);
		attachMouseHandler();
	}
	public DropDownButton(String str, Icon icon)
	{
		super(str, icon);
		attachMouseHandler();
	}
	
	private void attachMouseHandler()
	{
		addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if( !isEnabled() )
					return;
				
				if( !menuShown )
					listener.actionPerformed(null);
				
				pressedTimer.cancel();
				menuShown = false;
			}
			@Override public void mousePressed(MouseEvent e) {
				if( !isEnabled() )
					return;
				
				pressedTimer = new Timer();
				pressedTimer.schedule(new TimerTask() {
					@Override
					public void run() {
						popupMenu.show(getParent(), getLocation().x, getLocation().y + getHeight());
						menuShown = true;
					}
				}, 400);
			}
			@Override public void mouseExited(MouseEvent e) { }
			@Override public void mouseEntered(MouseEvent e) { }
			@Override public void mouseClicked(MouseEvent e) { }
		});
	}
	
	public void addDropDownItem(JMenuItem item, ActionListener l)
	{
		item.addActionListener(l);
		popupMenu.add(item);
	}
	
	public void updateSelectedItem(INSTALL_ENUM mode)
	{
		for( Component component : popupMenu.getComponents() )
		{
			if( component instanceof JMenuItem )
			{
				JMenuItem item = (JMenuItem) component;

				if( item.getText().contains("Nightly") )
					if( mode == INSTALL_ENUM.NIGHTLY )
						item.setText("✔ Nightly");
					else
						item.setText("Nightly");
				else if( item.getText().contains("Milestone") )
					if( mode == INSTALL_ENUM.MILESTONE )
						item.setText("✔ Milestone");
					else
						item.setText("Milestone");
			}
		}
	}
	
	@Override
	public void addActionListener(ActionListener l)
	{
		listener = l;
	}
}
