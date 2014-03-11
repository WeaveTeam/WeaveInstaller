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


/* Location:           C:\Users\Andy\Desktop\WeaveUpdaterV1.2\Weave Installer.jar
 * Qualified Name:     weave.inc.ISetupPanel
 * JD-Core Version:    0.7.0.1
 */