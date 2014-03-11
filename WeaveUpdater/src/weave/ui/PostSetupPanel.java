/*   1:    */ package weave.ui;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.awt.Font;
/*   5:    */ import java.beans.PropertyChangeEvent;
/*   6:    */ import java.beans.PropertyChangeListener;
/*   7:    */ import java.util.ArrayList;
/*   8:    */ import java.util.Timer;
/*   9:    */ import java.util.TimerTask;
/*  10:    */ import javax.swing.JButton;
/*  11:    */ import javax.swing.JLabel;
/*  12:    */ import javax.swing.JPanel;
/*  13:    */ import javax.swing.JProgressBar;
/*  14:    */ import weave.Revisions;
/*  15:    */ import weave.Settings;
/*  16:    */ import weave.inc.SetupPanel;
/*  17:    */ 
/*  18:    */ public class PostSetupPanel
/*  19:    */   extends SetupPanel
/*  20:    */ {
/*  21: 40 */   public JButton installButton = new JButton("Install");
/*  22: 41 */   public JButton checkButton = new JButton("Refresh");
/*  23: 42 */   public JButton deploy = new JButton("Deploy");
/*  24: 43 */   public JButton deleteButton = new JButton("Delete");
/*  25: 44 */   public JButton pruneButton = new JButton("<html><center>Auto <br> Clean</center></html>");
/*  26: 45 */   public JButton launchAdmin = new JButton("Launch Admin Console");
/*  27: 46 */   public ProgressUpdate progress = new ProgressUpdate();
/*  28: 47 */   public WeaveStats weaveStats = new WeaveStats();
/*  29: 48 */   public RevisionTable revisionTable = new RevisionTable();
/*  30: 49 */   private Timer timer = new Timer();
/*  31:    */   public JLabel zipLabelSpeed;
/*  32:    */   public JLabel zipLabelTimeleft;
/*  33:    */   public JLabel zipLabelSpeedHolder;
/*  34:    */   public JLabel zipLabelTimeleftHolder;
/*  35:    */   public JLabel zipLabelSizeDownloadHolder;
/*  36:    */   
/*  37:    */   public PostSetupPanel()
/*  38:    */   {
/*  39: 56 */     this.maxPanels = 1;
/*  40:    */     
/*  41: 58 */     setLayout(null);
/*  42: 59 */     setSize(350, 325);
/*  43: 60 */     setBounds(0, 0, 350, 325);
/*  44:    */     
/*  45: 62 */     JPanel panel = createPostSetupPanel();
/*  46:    */     
/*  47: 64 */     this.panels.add(panel);
/*  48: 65 */     add(panel);
/*  49:    */     
/*  50: 67 */     setVisible(true);
/*  51:    */     
/*  52: 69 */     this.timer.schedule(new TimerTask()
/*  53:    */     {
/*  54:    */       public void run()
/*  55:    */       {
/*  56: 72 */         int updateCheck = Revisions.checkForUpdates(false);
/*  57: 73 */         PostSetupPanel.this.weaveStats.refresh(updateCheck);
/*  58: 75 */         if (updateCheck == -2)
/*  59:    */         {
/*  60: 77 */           PostSetupPanel.this.progress.progBar.setStringPainted(true);
/*  61: 78 */           PostSetupPanel.this.progress.progBar.setString("No Internet Connection");
/*  62: 79 */           PostSetupPanel.this.progress.progBar.setValue(0);
/*  63:    */         }
/*  64: 82 */         PostSetupPanel.this.revisionTable.updateTableData();
/*  65: 83 */         PostSetupPanel.this.installButton.setEnabled((updateCheck == 1) && (!Settings.instance().TOMCAT_DIR.equals("")));
/*  66:    */       }
/*  67: 85 */     }, 1000L);
/*  68:    */   }
/*  69:    */   
/*  70:    */   public JPanel createPostSetupPanel()
/*  71:    */   {
/*  72: 90 */     JPanel panel = new JPanel();
/*  73: 91 */     panel.setLayout(null);
/*  74: 92 */     panel.setBounds(0, 0, 350, 325);
/*  75: 93 */     panel.setBackground(new Color(16777215));
/*  76:    */     
/*  77: 95 */     this.zipLabelSpeed = new JLabel("Download Rate:");
/*  78: 96 */     this.zipLabelTimeleft = new JLabel("Time left:");
/*  79: 97 */     this.zipLabelSpeedHolder = new JLabel();
/*  80: 98 */     this.zipLabelTimeleftHolder = new JLabel();
/*  81: 99 */     this.zipLabelSizeDownloadHolder = new JLabel();
/*  82:    */     
/*  83:    */ 
/*  84:102 */     this.zipLabelSpeed.setBounds(10, 60, 100, 20);
/*  85:103 */     this.zipLabelSpeed.setFont(new Font("Serif", 0, 13));
/*  86:104 */     this.zipLabelSpeed.setVisible(false);
/*  87:105 */     this.zipLabelTimeleft.setBounds(10, 80, 100, 20);
/*  88:106 */     this.zipLabelTimeleft.setFont(new Font("Serif", 0, 13));
/*  89:107 */     this.zipLabelTimeleft.setVisible(false);
/*  90:108 */     this.zipLabelSpeedHolder.setBounds(150, 60, 170, 20);
/*  91:109 */     this.zipLabelSpeedHolder.setHorizontalAlignment(4);
/*  92:110 */     this.zipLabelTimeleftHolder.setBounds(150, 80, 170, 20);
/*  93:111 */     this.zipLabelTimeleftHolder.setHorizontalAlignment(4);
/*  94:112 */     this.zipLabelSizeDownloadHolder.setBounds(150, 100, 170, 20);
/*  95:113 */     this.zipLabelSizeDownloadHolder.setHorizontalAlignment(4);
/*  96:114 */     this.zipLabelSizeDownloadHolder.setFont(new Font("Serif", 0, 13));
/*  97:    */     
/*  98:116 */     this.weaveStats.setBounds(10, 10, 230, 50);
/*  99:117 */     this.installButton.setBounds(250, 35, 80, 23);this.installButton.setToolTipText("Download the latest version of Weave and install it.");
/* 100:118 */     this.installButton.setEnabled(false);
/* 101:119 */     this.checkButton.setBounds(250, 10, 80, 23);this.checkButton.setToolTipText("Check for a new version of Weave");
/* 102:120 */     this.progress.setBounds(10, 120, 320, 20);
/* 103:121 */     this.progress.addPropertyChangeListener("ZIP_SPEED", new PropertyChangeListener()
/* 104:    */     {
/* 105:    */       public void propertyChange(PropertyChangeEvent evt)
/* 106:    */       {
/* 107:124 */         PostSetupPanel.this.zipLabelSpeedHolder.setText(PostSetupPanel.this.progress.zipInfo.strSpeed);
/* 108:    */       }
/* 109:126 */     });
/* 110:127 */     this.progress.addPropertyChangeListener("ZIP_TIMELEFT", new PropertyChangeListener()
/* 111:    */     {
/* 112:    */       public void propertyChange(PropertyChangeEvent evt)
/* 113:    */       {
/* 114:130 */         PostSetupPanel.this.zipLabelTimeleftHolder.setText(PostSetupPanel.this.progress.zipInfo.strTimeleft);
/* 115:    */       }
/* 116:132 */     });
/* 117:133 */     this.progress.addPropertyChangeListener("ZIP_SIZEDOWNLOADED", new PropertyChangeListener()
/* 118:    */     {
/* 119:    */       public void propertyChange(PropertyChangeEvent evt)
/* 120:    */       {
/* 121:136 */         PostSetupPanel.this.zipLabelSizeDownloadHolder.setText(PostSetupPanel.this.progress.zipInfo.strSizeDownloaded + " of " + PostSetupPanel.this.progress.zipInfo.strTotalSize);
/* 122:    */       }
/* 123:138 */     });
/* 124:139 */     this.revisionTable.setBounds(10, 150, 230, 130);
/* 125:140 */     this.deploy.setBounds(250, 150, 80, 25);this.deploy.setToolTipText("Install Weave from a backup revision, selected on the left in the table.");
/* 126:141 */     this.deleteButton.setBounds(250, 180, 80, 25);this.deleteButton.setToolTipText("Delete an individual revision, selected on the left in the table.");
/* 127:142 */     this.pruneButton.setBounds(250, 210, 80, 40);this.pruneButton.setToolTipText("Auto-delete older revisions to free up space on your hard drive.");
/* 128:143 */     this.launchAdmin.setBounds(10, 290, 230, 25);this.launchAdmin.setToolTipText("Open up the Admin Console");
/* 129:    */     
/* 130:    */ 
/* 131:146 */     panel.add(this.zipLabelSpeed);
/* 132:147 */     panel.add(this.zipLabelTimeleft);
/* 133:148 */     panel.add(this.zipLabelSpeedHolder);
/* 134:149 */     panel.add(this.zipLabelTimeleftHolder);
/* 135:150 */     panel.add(this.zipLabelSizeDownloadHolder);
/* 136:    */     
/* 137:152 */     panel.add(this.weaveStats);
/* 138:153 */     panel.add(this.installButton);
/* 139:154 */     panel.add(this.checkButton);
/* 140:155 */     panel.add(this.progress);
/* 141:156 */     panel.add(this.revisionTable);
/* 142:157 */     panel.add(this.deploy);
/* 143:158 */     panel.add(this.deleteButton);
/* 144:159 */     panel.add(this.pruneButton);
/* 145:160 */     panel.add(this.launchAdmin);
/* 146:    */     
/* 147:162 */     return panel;
/* 148:    */   }
/* 149:    */ }


/* Location:           C:\Users\Andy\Desktop\WeaveUpdaterV1.2\Weave Installer.jar
 * Qualified Name:     weave.ui.PostSetupPanel
 * JD-Core Version:    0.7.0.1
 */