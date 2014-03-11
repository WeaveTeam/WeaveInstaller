/*  1:   */ package weave.ui;
/*  2:   */ 
/*  3:   */ import java.awt.Color;
/*  4:   */ import java.awt.Font;
/*  5:   */ import javax.swing.JLabel;
/*  6:   */ import javax.swing.JPanel;
/*  7:   */ import weave.Settings;
/*  8:   */ 
/*  9:   */ public class WeaveStats
/* 10:   */   extends JPanel
/* 11:   */ {
/* 12:33 */   JLabel status = null;
/* 13:33 */   JLabel desc2 = null;
/* 14:33 */   JLabel desc = null;
/* 15:34 */   public JLabel lastUpdateTime = null;
/* 16:   */   
/* 17:   */   public WeaveStats()
/* 18:   */   {
/* 19:38 */     setLayout(null);
/* 20:39 */     setBackground(new Color(16777215));
/* 21:   */     
/* 22:41 */     this.desc = new JLabel("Weave Update:");
/* 23:42 */     this.desc.setFont(new Font("Serif", 1, 14));
/* 24:   */     
/* 25:44 */     this.status = new JLabel("Loading...");
/* 26:45 */     this.status.setFont(new Font("Serif", 1, 14));
/* 27:   */     
/* 28:47 */     this.desc2 = new JLabel("Last Check:");
/* 29:48 */     this.desc2.setFont(new Font("Serif", 1, 13));
/* 30:   */     
/* 31:50 */     this.lastUpdateTime = new JLabel(Settings.instance().LAST_UPDATE_CHECK);
/* 32:51 */     this.lastUpdateTime.setFont(new Font("Serif", 1, 13));
/* 33:   */     
/* 34:53 */     this.desc.setBounds(0, 0, 115, 25);
/* 35:54 */     this.status.setBounds(115, 0, 140, 25);
/* 36:55 */     this.desc2.setBounds(0, 25, 140, 20);
/* 37:56 */     this.lastUpdateTime.setBounds(115, 25, 140, 20);
/* 38:   */     
/* 39:58 */     add(this.desc);
/* 40:59 */     add(this.status);
/* 41:60 */     add(this.desc2);
/* 42:61 */     add(this.lastUpdateTime);
/* 43:   */   }
/* 44:   */   
/* 45:   */   public void refresh(int _status)
/* 46:   */   {
/* 47:71 */     if (_status == 1)
/* 48:   */     {
/* 49:73 */       this.status.setText("Update Available");
/* 50:74 */       this.status.setForeground(new Color(20224));
/* 51:   */     }
/* 52:75 */     else if (_status == 0)
/* 53:   */     {
/* 54:76 */       this.status.setText("Up to Date");
/* 55:77 */       this.status.setForeground(Color.BLACK);
/* 56:   */     }
/* 57:   */     else
/* 58:   */     {
/* 59:79 */       this.status.setText("Updating Error");
/* 60:80 */       this.status.setForeground(Color.RED);
/* 61:   */     }
/* 62:82 */     this.lastUpdateTime.setText(Settings.instance().LAST_UPDATE_CHECK);
/* 63:   */   }
/* 64:   */ }


/* Location:           C:\Users\Andy\Desktop\WeaveUpdaterV1.2\Weave Installer.jar
 * Qualified Name:     weave.ui.WeaveStats
 * JD-Core Version:    0.7.0.1
 */