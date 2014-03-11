/*  1:   */ package weave.inc;
/*  2:   */ 
/*  3:   */ import java.awt.event.ActionListener;
/*  4:   */ import java.util.ArrayList;
/*  5:   */ import javax.swing.JButton;
/*  6:   */ import javax.swing.JPanel;
/*  7:   */ 
/*  8:   */ public class SetupPanel
/*  9:   */   extends JPanel
/* 10:   */   implements ISetupPanel
/* 11:   */ {
/* 12:   */   protected int currentPanel;
/* 13:   */   protected int maxPanels;
/* 14:33 */   public ArrayList<JPanel> panels = new ArrayList();
/* 15:   */   
/* 16:   */   public int getCurrentPanelIndex()
/* 17:   */   {
/* 18:37 */     return this.currentPanel;
/* 19:   */   }
/* 20:   */   
/* 21:   */   public int getNumberOfPanels()
/* 22:   */   {
/* 23:42 */     return this.maxPanels;
/* 24:   */   }
/* 25:   */   
/* 26:   */   public void nextPanel()
/* 27:   */   {
/* 28:47 */     if (this.currentPanel < this.maxPanels)
/* 29:   */     {
/* 30:49 */       hidePanels();
/* 31:50 */       ((JPanel)this.panels.get(++this.currentPanel)).setVisible(true);
/* 32:   */     }
/* 33:   */   }
/* 34:   */   
/* 35:   */   public void previousPanel()
/* 36:   */   {
/* 37:56 */     if (this.currentPanel > 0)
/* 38:   */     {
/* 39:58 */       hidePanels();
/* 40:59 */       ((JPanel)this.panels.get(--this.currentPanel)).setVisible(true);
/* 41:   */     }
/* 42:   */   }
/* 43:   */   
/* 44:   */   public void showPanels()
/* 45:   */   {
/* 46:65 */     hidePanels();
/* 47:66 */     this.currentPanel = 0;
/* 48:67 */     ((JPanel)this.panels.get(this.currentPanel)).setVisible(true);
/* 49:   */   }
/* 50:   */   
/* 51:   */   public void hidePanels()
/* 52:   */   {
/* 53:72 */     for (int i = 0; i < this.maxPanels; i++) {
/* 54:73 */       ((JPanel)this.panels.get(i)).setVisible(false);
/* 55:   */     }
/* 56:   */   }
/* 57:   */   
/* 58:   */   public void addActionToButton(JButton button, ActionListener action)
/* 59:   */   {
/* 60:78 */     button.addActionListener(action);
/* 61:   */   }
/* 62:   */ }


/* Location:           C:\Users\Andy\Desktop\WeaveUpdaterV1.2\Weave Installer.jar
 * Qualified Name:     weave.inc.SetupPanel
 * JD-Core Version:    0.7.0.1
 */