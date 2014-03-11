package weave.inc;

import java.awt.event.ActionListener;
import javax.swing.JButton;

public abstract interface ISetupPanel
{
	public abstract int getCurrentPanelIndex();

	public abstract int getNumberOfPanels();

	public abstract void nextPanel();

	public abstract void previousPanel();

	public abstract void showPanels();

	public abstract void hidePanels();

	public abstract void addActionToButton(JButton paramJButton, ActionListener paramActionListener);
}
