/*   1:    */ package weave.ui;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.awt.Font;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import javax.imageio.ImageIO;
/*   7:    */ import javax.swing.ImageIcon;
/*   8:    */ import javax.swing.JButton;
/*   9:    */ import javax.swing.JLabel;
/*  10:    */ import javax.swing.JPanel;
/*  11:    */ import javax.swing.JTextArea;
/*  12:    */ import weave.Settings;
/*  13:    */ import weave.inc.SetupPanel;
/*  14:    */ 
/*  15:    */ public class PreSetupPanel
/*  16:    */   extends SetupPanel
/*  17:    */ {
/*  18: 38 */   public JButton updateButton = null;
/*  19: 39 */   public JButton settingsButton = null;
/*  20:    */   
/*  21:    */   public PreSetupPanel()
/*  22:    */     throws Exception
/*  23:    */   {
/*  24: 43 */     this.maxPanels = 1;
/*  25:    */     
/*  26: 45 */     setLayout(null);
/*  27: 46 */     setSize(350, 325);
/*  28: 47 */     setBounds(0, 0, 350, 325);
/*  29:    */     
/*  30: 49 */     JPanel panel = null;
/*  31: 50 */     for (int i = 0; i < this.maxPanels; i++)
/*  32:    */     {
/*  33: 51 */       switch (i)
/*  34:    */       {
/*  35:    */       case 0: 
/*  36: 52 */         panel = createWelcomeMenu();
/*  37:    */       }
/*  38: 54 */       this.panels.add(panel);
/*  39: 55 */       add(panel);
/*  40:    */     }
/*  41: 57 */     hidePanels();
/*  42:    */   }
/*  43:    */   
/*  44:    */   public JPanel createWelcomeMenu()
/*  45:    */   {
/*  46: 62 */     JPanel panel = new JPanel();
/*  47: 63 */     panel.setLayout(null);
/*  48: 64 */     panel.setBounds(0, 0, 350, 325);
/*  49: 65 */     panel.setBackground(new Color(16777215));
/*  50:    */     
/*  51: 67 */     JLabel welcome = new JLabel("Welcome to Weave Setup Wizard");
/*  52: 68 */     welcome.setFont(new Font("Corbel", 1, 17));
/*  53: 69 */     welcome.setBounds(30, 30, 290, 50);
/*  54:    */     
/*  55: 71 */     JTextArea info = new JTextArea(3, 8);
/*  56: 72 */     info.setEditable(false);
/*  57: 73 */     info.setLineWrap(true);
/*  58: 74 */     info.setText("The Setup Wizard will install Weave on your\ncomputer. Click Next to continue or Cancel to\nexit the Setup Wizard.");
/*  59:    */     
/*  60:    */ 
/*  61: 77 */     info.setFont(new Font("Corbel", 0, 14));
/*  62: 78 */     info.setBounds(30, 100, 290, 60);
/*  63:    */     
/*  64: 80 */     panel.add(welcome);
/*  65: 81 */     panel.add(info);
/*  66: 82 */     return panel;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public JPanel createOptionsMenu()
/*  70:    */     throws Exception
/*  71:    */   {
/*  72: 87 */     JPanel panel = new JPanel();
/*  73: 88 */     panel.setLayout(null);
/*  74: 89 */     panel.setBounds(0, 0, 350, 325);
/*  75: 90 */     panel.setBackground(new Color(16777215));
/*  76:    */     
/*  77: 92 */     ImageIcon settingsIcon = new ImageIcon(ImageIO.read(PreSetupPanel.class.getResource("/resources/settings.png")));
/*  78: 93 */     this.settingsButton = new JButton(settingsIcon);
/*  79: 94 */     this.settingsButton.setBounds(50, 70, 40, 40);
/*  80: 95 */     panel.add(this.settingsButton);
/*  81:    */     
/*  82: 97 */     JLabel settingsLabel = new JLabel("Configure Settings");
/*  83: 98 */     settingsLabel.setFont(new Font("Corbel", 0, 22));
/*  84: 99 */     settingsLabel.setBounds(100, 70, 250, 40);
/*  85:100 */     panel.add(settingsLabel);
/*  86:    */     
/*  87:102 */     ImageIcon downloadIcon = new ImageIcon(ImageIO.read(PreSetupPanel.class.getResource("/resources/download.png")));
/*  88:103 */     this.updateButton = new JButton(downloadIcon);
/*  89:104 */     this.updateButton.setBounds(50, 210, 40, 40);
/*  90:105 */     this.updateButton.setEnabled(Settings.instance().settingsExists().booleanValue());
/*  91:106 */     panel.add(this.updateButton);
/*  92:    */     
/*  93:108 */     JLabel downloadLabel = new JLabel("Update Weave");
/*  94:109 */     downloadLabel.setFont(new Font("Corbel", 0, 22));
/*  95:110 */     downloadLabel.setBounds(100, 210, 250, 40);
/*  96:111 */     panel.add(downloadLabel);
/*  97:    */     
/*  98:113 */     return panel;
/*  99:    */   }
/* 100:    */ }


/* Location:           C:\Users\Andy\Desktop\WeaveUpdaterV1.2\Weave Installer.jar
 * Qualified Name:     weave.ui.PreSetupPanel
 * JD-Core Version:    0.7.0.1
 */