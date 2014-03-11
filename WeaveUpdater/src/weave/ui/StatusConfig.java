/*  1:   */ package weave.ui;
/*  2:   */ 
/*  3:   */ import java.awt.Font;
/*  4:   */ import java.awt.GridLayout;
/*  5:   */ import java.awt.event.KeyAdapter;
/*  6:   */ import java.awt.event.KeyEvent;
/*  7:   */ import java.util.Timer;
/*  8:   */ import javax.swing.JLabel;
/*  9:   */ import javax.swing.JOptionPane;
/* 10:   */ import javax.swing.JPanel;
/* 11:   */ import javax.swing.JTextField;
/* 12:   */ import weave.Settings;
/* 13:   */ import weave.Settings.SERVICE_PORTS;
/* 14:   */ 
/* 15:   */ public class StatusConfig
/* 16:   */   extends JPanel
/* 17:   */ {
/* 18:39 */   JLabel labelPort = null;
/* 19:40 */   public JTextField textPort = null;
/* 20:41 */   Timer inputTimer = null;
/* 21:   */   
/* 22:   */   public StatusConfig(String _label, final Settings.SERVICE_PORTS _portType)
/* 23:   */   {
/* 24:45 */     setLayout(new GridLayout(1, 2));
/* 25:   */     
/* 26:47 */     this.inputTimer = new Timer();
/* 27:48 */     this.labelPort = new JLabel(_label);
/* 28:49 */     if (_portType == Settings.SERVICE_PORTS.MySQL) {
/* 29:50 */       this.textPort = new JTextField(Integer.toString(Settings.instance().MySQL_PORT));
/* 30:   */     } else {
/* 31:52 */       this.textPort = new JTextField(Integer.toString(Settings.instance().TOMCAT_PORT));
/* 32:   */     }
/* 33:54 */     this.textPort.addKeyListener(new KeyAdapter()
/* 34:   */     {
/* 35:   */       public void keyReleased(KeyEvent e)
/* 36:   */       {
/* 37:57 */         if (StatusConfig.this.textPort.getText().equals(""))
/* 38:   */         {
/* 39:58 */           JOptionPane.showMessageDialog(null, "Port value cannot be empty");
/* 40:59 */           if (_portType == Settings.SERVICE_PORTS.MySQL) {
/* 41:60 */             Settings.instance().MySQL_PORT = 0;
/* 42:   */           } else {
/* 43:62 */             Settings.instance().TOMCAT_PORT = 0;
/* 44:   */           }
/* 45:63 */           return;
/* 46:   */         }
/* 47:   */         try
/* 48:   */         {
/* 49:68 */           int port = Integer.parseInt(StatusConfig.this.textPort.getText());
/* 50:69 */           if ((port > 65535) || (port < 1))
/* 51:   */           {
/* 52:70 */             JOptionPane.showMessageDialog(null, "Input values: 0 < port < 65535");
/* 53:71 */             if (_portType == Settings.SERVICE_PORTS.MySQL) {
/* 54:72 */               Settings.instance().MySQL_PORT = 0;
/* 55:   */             } else {
/* 56:74 */               Settings.instance().TOMCAT_PORT = 0;
/* 57:   */             }
/* 58:75 */             return;
/* 59:   */           }
/* 60:77 */           if (_portType == Settings.SERVICE_PORTS.MySQL) {
/* 61:78 */             Settings.instance().MySQL_PORT = port;
/* 62:   */           } else {
/* 63:80 */             Settings.instance().TOMCAT_PORT = port;
/* 64:   */           }
/* 65:   */         }
/* 66:   */         catch (NumberFormatException fe)
/* 67:   */         {
/* 68:82 */           JOptionPane.showMessageDialog(null, "Only numbers are allowed as inputs."); return;
/* 69:   */         }
/* 70:   */         int port;
/* 71:   */       }
/* 72:86 */     });
/* 73:87 */     this.labelPort.setFont(new Font("Serif", 1, 14));
/* 74:   */     
/* 75:89 */     add(this.labelPort);
/* 76:90 */     add(this.textPort);
/* 77:   */   }
/* 78:   */ }


/* Location:           C:\Users\Andy\Desktop\WeaveUpdaterV1.2\Weave Installer.jar
 * Qualified Name:     weave.ui.StatusConfig
 * JD-Core Version:    0.7.0.1
 */