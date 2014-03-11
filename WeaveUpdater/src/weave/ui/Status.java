/*  1:   */ package weave.ui;
/*  2:   */ 
/*  3:   */ import java.awt.Color;
/*  4:   */ import java.awt.Font;
/*  5:   */ import java.awt.GridLayout;
/*  6:   */ import java.awt.image.BufferedImage;
/*  7:   */ import javax.imageio.ImageIO;
/*  8:   */ import javax.swing.ImageIcon;
/*  9:   */ import javax.swing.JLabel;
/* 10:   */ import javax.swing.JPanel;
/* 11:   */ 
/* 12:   */ public class Status
/* 13:   */   extends JPanel
/* 14:   */ {
/* 15:35 */   int lastStatus = -1;
/* 16:36 */   JLabel label = null;
/* 17:37 */   JLabel status = null;
/* 18:38 */   BufferedImage status_OK = null;
/* 19:39 */   BufferedImage status_BAD = null;
/* 20:   */   
/* 21:   */   public Status(String _label, Boolean _status)
/* 22:   */   {
/* 23:51 */     setLayout(new GridLayout(1, 2));
/* 24:   */     try
/* 25:   */     {
/* 26:53 */       this.status_OK = ImageIO.read(Status.class.getResource("/resources/check_19x18.png"));
/* 27:54 */       this.status_BAD = ImageIO.read(Status.class.getResource("/resources/warning_21x18.png"));
/* 28:   */     }
/* 29:   */     catch (Exception ex)
/* 30:   */     {
/* 31:56 */       ex.printStackTrace();
/* 32:   */     }
/* 33:59 */     this.label = new JLabel(_label);
/* 34:60 */     this.label.setFont(new Font("Serif", 1, 14));
/* 35:   */     
/* 36:62 */     add(this.label);
/* 37:   */     
/* 38:64 */     refresh(_status);
/* 39:   */   }
/* 40:   */   
/* 41:   */   public void refresh(Boolean _status)
/* 42:   */   {
/* 43:76 */     if (this.lastStatus == (_status.booleanValue() ? 1 : 0)) {
/* 44:77 */       return;
/* 45:   */     }
/* 46:79 */     this.lastStatus = (_status.booleanValue() ? 1 : 0);
/* 47:81 */     if (this.status != null)
/* 48:   */     {
/* 49:83 */       remove(this.status);
/* 50:84 */       this.status = null;
/* 51:   */     }
/* 52:87 */     if (_status.booleanValue())
/* 53:   */     {
/* 54:88 */       this.status = new JLabel("Running", new ImageIcon(this.status_OK), 2);
/* 55:89 */       this.status.setForeground(new Color(20224));
/* 56:   */     }
/* 57:   */     else
/* 58:   */     {
/* 59:91 */       this.status = new JLabel("Not Running", new ImageIcon(this.status_BAD), 2);
/* 60:92 */       this.status.setForeground(Color.RED);
/* 61:   */     }
/* 62:94 */     this.status.setFont(new Font("Serif", 1, 20));
/* 63:   */     
/* 64:96 */     add(this.status);
/* 65:97 */     validate();
/* 66:   */   }
/* 67:   */ }


/* Location:           C:\Users\Andy\Desktop\WeaveUpdaterV1.2\Weave Installer.jar
 * Qualified Name:     weave.ui.Status
 * JD-Core Version:    0.7.0.1
 */