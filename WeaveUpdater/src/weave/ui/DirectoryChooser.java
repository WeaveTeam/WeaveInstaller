/*  1:   */ package weave.ui;
/*  2:   */ 
/*  3:   */ import java.awt.Font;
/*  4:   */ import java.awt.GridLayout;
/*  5:   */ import javax.swing.JFileChooser;
/*  6:   */ import javax.swing.JLabel;
/*  7:   */ import javax.swing.JPanel;
/*  8:   */ import javax.swing.JTextField;
/*  9:   */ import weave.Settings;
/* 10:   */ 
/* 11:   */ public class DirectoryChooser
/* 12:   */   extends JPanel
/* 13:   */ {
/* 14:   */   public JTextField textField;
/* 15:   */   public JFileChooser fileChooser;
/* 16:   */   JLabel label;
/* 17:   */   
/* 18:   */   public DirectoryChooser(String _label)
/* 19:   */   {
/* 20:46 */     setLayout(new GridLayout(1, 2));
/* 21:   */     
/* 22:48 */     this.fileChooser = new JFileChooser("C:\\");
/* 23:49 */     this.label = new JLabel(_label);
/* 24:50 */     this.label.setFont(new Font("Serif", 1, 14));
/* 25:   */     
/* 26:52 */     this.textField = new JTextField(Settings.instance().TOMCAT_DIR, 19);
/* 27:53 */     this.textField.setEditable(false);
/* 28:   */     
/* 29:55 */     add(this.label);
/* 30:56 */     add(this.textField);
/* 31:   */   }
/* 32:   */ }


/* Location:           C:\Users\Andy\Desktop\WeaveUpdaterV1.2\Weave Installer.jar
 * Qualified Name:     weave.ui.DirectoryChooser
 * JD-Core Version:    0.7.0.1
 */