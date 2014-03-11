package weave.inc;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JPanel;

public class SetupPanel
		extends JPanel
		implements ISetupPanel
{
	private static final long serialVersionUID = 1L;
	protected int currentPanel;
	protected int maxPanels;
	public ArrayList<JPanel> panels = new ArrayList<JPanel>();

	public int getCurrentPanelIndex()
	{
		return this.currentPanel;
	}

	public int getNumberOfPanels()
	{
		return this.maxPanels;
	}

	public void nextPanel()
	{
		if (this.currentPanel < this.maxPanels)
		{
			hidePanels();
			((JPanel) this.panels.get(++this.currentPanel)).setVisible(true);
		}
	}

	public void previousPanel()
	{
		if (this.currentPanel > 0)
		{
			hidePanels();
			((JPanel) this.panels.get(--this.currentPanel)).setVisible(true);
		}
	}

	public void showPanels()
	{
		hidePanels();
		this.currentPanel = 0;
		((JPanel) this.panels.get(this.currentPanel)).setVisible(true);
	}

	public void hidePanels()
	{
		for (int i = 0; i < this.maxPanels; i++)
		{
			((JPanel) this.panels.get(i)).setVisible(false);
		}
	}

	public void addActionToButton(JButton button, ActionListener action)
	{
		button.addActionListener(action);
	}
}
