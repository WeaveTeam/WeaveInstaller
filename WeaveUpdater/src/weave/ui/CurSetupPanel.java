/*   1:    */ package weave.ui;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.awt.Font;
/*   5:    */ import java.beans.PropertyChangeEvent;
/*   6:    */ import java.beans.PropertyChangeListener;
/*   7:    */ import java.io.File;
/*   8:    */ import java.util.ArrayList;
/*   9:    */ import java.util.Timer;
/*  10:    */ import java.util.TimerTask;
/*  11:    */ import javax.imageio.ImageIO;
/*  12:    */ import javax.swing.ImageIcon;
/*  13:    */ import javax.swing.JButton;
/*  14:    */ import javax.swing.JCheckBox;
/*  15:    */ import javax.swing.JLabel;
/*  16:    */ import javax.swing.JPanel;
/*  17:    */ import javax.swing.JTextArea;
/*  18:    */ import javax.swing.JTextField;
/*  19:    */ import javax.swing.event.ChangeEvent;
/*  20:    */ import javax.swing.event.ChangeListener;
/*  21:    */ import weave.Settings;
/*  22:    */ import weave.Settings.SERVICE_PORTS;
/*  23:    */ import weave.WeaveUpdater;
/*  24:    */ import weave.inc.SetupPanel;
/*  25:    */ 
/*  26:    */ public class CurSetupPanel
/*  27:    */   extends SetupPanel
/*  28:    */ {
/*  29: 47 */   public JButton dirButton = null;
/*  30: 48 */   public DirectoryChooser dirChooser = null;
/*  31:    */   public ProgressUpdate progTomcat;
/*  32:    */   public ProgressUpdate progMySQL;
/*  33:    */   public JPanel tomcatPanel;
/*  34:    */   public JPanel mysqlPanel;
/*  35:    */   public JCheckBox tomcatCheck;
/*  36:    */   public JCheckBox mysqlCheck;
/*  37:    */   public JLabel tomcatL;
/*  38:    */   public JLabel mysqlL;
/*  39:    */   public Timer tomcatT1;
/*  40:    */   public Timer mysqlT1;
/*  41:    */   public Status tomcatStatus1;
/*  42:    */   public Status mysqlStatus1;
/*  43:    */   public StatusConfig tomcatConfig;
/*  44:    */   public StatusConfig mysqlConfig;
/*  45:    */   public JButton installTomcat;
/*  46:    */   public JButton installMySQL;
/*  47:    */   public JButton tomcatDownloadButton;
/*  48:    */   public JButton mySQLDownloadButton;
/*  49:    */   public JLabel tomcatLabelSpeedHolder;
/*  50:    */   public JLabel tomcatLabelTimeleftHolder;
/*  51:    */   public JLabel tomcatLabelSizeDownloadHolder;
/*  52:    */   public JLabel mysqlLabelSpeedHolder;
/*  53:    */   public JLabel mysqlLabelTimeleftHolder;
/*  54:    */   public JLabel mysqlLabelSizeDownloadHolder;
/*  55:    */   
/*  56:    */   public CurSetupPanel()
/*  57:    */     throws Exception
/*  58:    */   {
/*  59: 63 */     this.maxPanels = 3;
/*  60:    */     
/*  61: 65 */     setLayout(null);
/*  62: 66 */     setSize(350, 325);
/*  63: 67 */     setBounds(0, 0, 350, 325);
/*  64:    */     
/*  65: 69 */     JPanel panel = null;
/*  66: 70 */     for (int i = 0; i < this.maxPanels; i++)
/*  67:    */     {
/*  68: 71 */       switch (i)
/*  69:    */       {
/*  70:    */       case 0: 
/*  71: 72 */         panel = createAddonsMenu(); break;
/*  72:    */       case 1: 
/*  73: 73 */         panel = createDownloadPanel(); break;
/*  74:    */       case 2: 
/*  75: 74 */         panel = createFinishMenu();
/*  76:    */       }
/*  77: 76 */       this.panels.add(panel);
/*  78: 77 */       add(panel);
/*  79:    */     }
/*  80: 79 */     hidePanels();
/*  81:    */   }
/*  82:    */   
/*  83:    */   public JPanel createAddonsMenu()
/*  84:    */     throws Exception
/*  85:    */   {
/*  86: 92 */     JPanel panel = new JPanel();
/*  87: 93 */     panel.setLayout(null);
/*  88: 94 */     panel.setBounds(0, 0, 350, 325);
/*  89: 95 */     panel.setBackground(new Color(16777215));
/*  90:    */     
/*  91: 97 */     JLabel welcome = new JLabel("Requirements");
/*  92: 98 */     welcome.setFont(new Font("Corbel", 1, 19));
/*  93: 99 */     welcome.setBounds(20, 20, 290, 25);
/*  94:    */     
/*  95:101 */     JTextArea info = new JTextArea();
/*  96:102 */     info.setEditable(false);
/*  97:103 */     info.setLineWrap(true);
/*  98:104 */     info.setBounds(25, 60, 290, 150);
/*  99:105 */     info.setFont(new Font("Corbel", 0, 14));
/* 100:106 */     info.setText("Weave requires Tomcat and MySQL to run. \n\nIf you already have an active installation of \nTomcat and MySQL feel free to skip this step.\n\nOtherwise, check off which programs to \ninstall with this installer.");
/* 101:    */     
/* 102:    */ 
/* 103:    */ 
/* 104:    */ 
/* 105:    */ 
/* 106:112 */     this.tomcatCheck = new JCheckBox("Include Tomcat");
/* 107:113 */     this.tomcatCheck.setBackground(Color.WHITE);
/* 108:114 */     this.tomcatCheck.setBounds(25, 210, 290, 25);
/* 109:115 */     this.tomcatCheck.addChangeListener(new ChangeListener()
/* 110:    */     {
/* 111:    */       public void stateChanged(ChangeEvent arg0)
/* 112:    */       {
/* 113:118 */         CurSetupPanel.this.tomcatPanel.setVisible(CurSetupPanel.this.tomcatCheck.isSelected());
/* 114:120 */         if ((!CurSetupPanel.this.tomcatCheck.isSelected()) && (CurSetupPanel.this.mysqlCheck.isSelected())) {
/* 115:121 */           CurSetupPanel.this.mysqlPanel.setBounds(0, 0, 350, 140);
/* 116:    */         } else {
/* 117:123 */           CurSetupPanel.this.mysqlPanel.setBounds(0, 150, 350, 140);
/* 118:    */         }
/* 119:    */       }
/* 120:126 */     });
/* 121:127 */     this.mysqlCheck = new JCheckBox("Include MySQL");
/* 122:128 */     this.mysqlCheck.setBackground(Color.WHITE);
/* 123:129 */     this.mysqlCheck.setBounds(25, 235, 290, 25);
/* 124:130 */     this.mysqlCheck.addChangeListener(new ChangeListener()
/* 125:    */     {
/* 126:    */       public void stateChanged(ChangeEvent e)
/* 127:    */       {
/* 128:133 */         CurSetupPanel.this.mysqlPanel.setVisible(CurSetupPanel.this.mysqlCheck.isSelected());
/* 129:135 */         if ((!CurSetupPanel.this.tomcatCheck.isSelected()) && (CurSetupPanel.this.mysqlCheck.isSelected())) {
/* 130:136 */           CurSetupPanel.this.mysqlPanel.setBounds(0, 0, 350, 140);
/* 131:    */         } else {
/* 132:138 */           CurSetupPanel.this.mysqlPanel.setBounds(0, 150, 350, 140);
/* 133:    */         }
/* 134:    */       }
/* 135:141 */     });
/* 136:142 */     panel.add(welcome);
/* 137:143 */     panel.add(info);
/* 138:144 */     panel.add(this.tomcatCheck);
/* 139:145 */     panel.add(this.mysqlCheck);
/* 140:    */     
/* 141:147 */     return panel;
/* 142:    */   }
/* 143:    */   
/* 144:    */   public JPanel createDownloadPanel()
/* 145:    */     throws Exception
/* 146:    */   {
/* 147:152 */     JPanel panel = new JPanel();
/* 148:153 */     panel.setLayout(null);
/* 149:154 */     panel.setBounds(0, 0, 350, 325);
/* 150:155 */     panel.setBackground(new Color(16777215));
/* 151:    */     
/* 152:157 */     this.tomcatPanel = new JPanel();
/* 153:158 */     this.tomcatPanel.setLayout(null);
/* 154:159 */     this.tomcatPanel.setBounds(0, 0, 350, 140);
/* 155:160 */     this.tomcatPanel.setBackground(new Color(16777215));
/* 156:    */     
/* 157:162 */     this.mysqlPanel = new JPanel();
/* 158:163 */     this.mysqlPanel.setLayout(null);
/* 159:164 */     this.mysqlPanel.setBounds(0, 150, 350, 140);
/* 160:165 */     this.mysqlPanel.setBackground(new Color(16777215));
/* 161:    */     
/* 162:167 */     String tomcatS = Settings.instance().getLatestTomcatVersion();
/* 163:168 */     this.tomcatL = new JLabel("Tomcat " + (tomcatS == null ? "" : tomcatS));
/* 164:169 */     this.tomcatL.setBounds(10, 5, 150, 25);
/* 165:170 */     this.tomcatL.setFont(new Font("Corbel", 1, 16));
/* 166:171 */     this.tomcatPanel.add(this.tomcatL);
/* 167:    */     
/* 168:173 */     ImageIcon tomcatIcon = new ImageIcon(ImageIO.read(WeaveUpdater.class.getResource("/resources/tomcatlogo.png")));
/* 169:174 */     JLabel tomcatIconLabel = new JLabel(tomcatIcon);
/* 170:175 */     tomcatIconLabel.setBounds(30, 35, 69, 50);
/* 171:176 */     this.tomcatPanel.add(tomcatIconLabel);
/* 172:    */     
/* 173:178 */     JLabel tomcatLabelSpeed = new JLabel("Download Rate:");
/* 174:179 */     tomcatLabelSpeed.setBounds(140, 10, 100, 20);
/* 175:180 */     this.tomcatPanel.add(tomcatLabelSpeed);
/* 176:    */     
/* 177:182 */     this.tomcatLabelSpeedHolder = new JLabel();
/* 178:183 */     this.tomcatLabelSpeedHolder.setBounds(220, 10, 100, 20);
/* 179:184 */     this.tomcatLabelSpeedHolder.setHorizontalAlignment(4);
/* 180:185 */     this.tomcatPanel.add(this.tomcatLabelSpeedHolder);
/* 181:    */     
/* 182:187 */     JLabel tomcatLabelTimeleft = new JLabel("Time left:");
/* 183:188 */     tomcatLabelTimeleft.setBounds(140, 30, 50, 20);
/* 184:189 */     this.tomcatPanel.add(tomcatLabelTimeleft);
/* 185:    */     
/* 186:191 */     this.tomcatLabelTimeleftHolder = new JLabel();
/* 187:192 */     this.tomcatLabelTimeleftHolder.setBounds(220, 30, 100, 20);
/* 188:193 */     this.tomcatLabelTimeleftHolder.setHorizontalAlignment(4);
/* 189:194 */     this.tomcatPanel.add(this.tomcatLabelTimeleftHolder);
/* 190:    */     
/* 191:196 */     this.tomcatLabelSizeDownloadHolder = new JLabel();
/* 192:197 */     this.tomcatLabelSizeDownloadHolder.setBounds(175, 90, 155, 20);
/* 193:198 */     this.tomcatLabelSizeDownloadHolder.setHorizontalAlignment(4);
/* 194:199 */     this.tomcatPanel.add(this.tomcatLabelSizeDownloadHolder);
/* 195:    */     
/* 196:201 */     this.progTomcat = new ProgressUpdate();
/* 197:202 */     this.progTomcat.setBounds(140, 115, 190, 20);
/* 198:203 */     this.progTomcat.addPropertyChangeListener("MSI_SPEED", new PropertyChangeListener()
/* 199:    */     {
/* 200:    */       public void propertyChange(PropertyChangeEvent evt)
/* 201:    */       {
/* 202:205 */         CurSetupPanel.this.tomcatLabelSpeedHolder.setText(CurSetupPanel.this.progTomcat.msiInfo.strSpeed);
/* 203:    */       }
/* 204:207 */     });
/* 205:208 */     this.progTomcat.addPropertyChangeListener("MSI_TIMELEFT", new PropertyChangeListener()
/* 206:    */     {
/* 207:    */       public void propertyChange(PropertyChangeEvent evt)
/* 208:    */       {
/* 209:210 */         CurSetupPanel.this.tomcatLabelTimeleftHolder.setText(CurSetupPanel.this.progTomcat.msiInfo.strTimeleft);
/* 210:    */       }
/* 211:212 */     });
/* 212:213 */     this.progTomcat.addPropertyChangeListener("MSI_SIZEDOWNLOADED", new PropertyChangeListener()
/* 213:    */     {
/* 214:    */       public void propertyChange(PropertyChangeEvent evt)
/* 215:    */       {
/* 216:215 */         CurSetupPanel.this.tomcatLabelSizeDownloadHolder.setText(CurSetupPanel.this.progTomcat.msiInfo.strSizeDownloaded + " of " + CurSetupPanel.this.progTomcat.msiInfo.strTotalSize);
/* 217:    */       }
/* 218:217 */     });
/* 219:218 */     this.tomcatPanel.add(this.progTomcat);
/* 220:    */     
/* 221:220 */     this.tomcatDownloadButton = new JButton("Download");
/* 222:221 */     this.tomcatDownloadButton.setBounds(5, 90, 100, 20);
/* 223:222 */     this.tomcatPanel.add(this.tomcatDownloadButton);
/* 224:    */     
/* 225:224 */     this.installTomcat = new JButton("Install");
/* 226:225 */     this.installTomcat.setBounds(5, 115, 100, 20);
/* 227:227 */     if (Settings.instance().TOMCAT_FILE.exists()) {
/* 228:228 */       this.installTomcat.setEnabled(true);
/* 229:    */     } else {
/* 230:230 */       this.installTomcat.setEnabled(false);
/* 231:    */     }
/* 232:231 */     this.tomcatPanel.add(this.installTomcat);
/* 233:    */     
/* 234:233 */     String mysqlS = Settings.instance().getLatestMySQLVersion();
/* 235:234 */     this.mysqlL = new JLabel("MySQL " + (mysqlS == null ? "" : mysqlS));
/* 236:235 */     this.mysqlL.setBounds(10, 0, 150, 25);
/* 237:236 */     this.mysqlL.setFont(new Font("Corbel", 1, 16));
/* 238:237 */     this.mysqlPanel.add(this.mysqlL);
/* 239:    */     
/* 240:239 */     JLabel mysqlLabelSpeed = new JLabel("Download Rate:");
/* 241:240 */     mysqlLabelSpeed.setBounds(140, 10, 100, 20);
/* 242:241 */     this.mysqlPanel.add(mysqlLabelSpeed);
/* 243:    */     
/* 244:243 */     this.mysqlLabelSpeedHolder = new JLabel();
/* 245:244 */     this.mysqlLabelSpeedHolder.setBounds(220, 10, 100, 20);
/* 246:245 */     this.mysqlLabelSpeedHolder.setHorizontalAlignment(4);
/* 247:246 */     this.mysqlPanel.add(this.mysqlLabelSpeedHolder);
/* 248:    */     
/* 249:248 */     JLabel mysqlLabelTimeleft = new JLabel("Time left:");
/* 250:249 */     mysqlLabelTimeleft.setBounds(140, 30, 50, 20);
/* 251:250 */     this.mysqlPanel.add(mysqlLabelTimeleft);
/* 252:    */     
/* 253:252 */     this.mysqlLabelTimeleftHolder = new JLabel();
/* 254:253 */     this.mysqlLabelTimeleftHolder.setBounds(220, 30, 100, 20);
/* 255:254 */     this.mysqlLabelTimeleftHolder.setHorizontalAlignment(4);
/* 256:255 */     this.mysqlPanel.add(this.mysqlLabelTimeleftHolder);
/* 257:    */     
/* 258:257 */     this.mysqlLabelSizeDownloadHolder = new JLabel();
/* 259:258 */     this.mysqlLabelSizeDownloadHolder.setBounds(175, 90, 155, 20);
/* 260:259 */     this.mysqlLabelSizeDownloadHolder.setHorizontalAlignment(4);
/* 261:260 */     this.mysqlPanel.add(this.mysqlLabelSizeDownloadHolder);
/* 262:    */     
/* 263:262 */     ImageIcon mySQLIcon = new ImageIcon(ImageIO.read(WeaveUpdater.class.getResource("/resources/mysql-dolphin.png")));
/* 264:263 */     JLabel mySQLIconLabel = new JLabel(mySQLIcon);
/* 265:264 */     mySQLIconLabel.setBounds(25, 35, 88, 47);
/* 266:265 */     this.mysqlPanel.add(mySQLIconLabel);
/* 267:    */     
/* 268:267 */     this.progMySQL = new ProgressUpdate();
/* 269:268 */     this.progMySQL.setBounds(140, 115, 190, 20);
/* 270:269 */     this.progMySQL.addPropertyChangeListener("MSI_SPEED", new PropertyChangeListener()
/* 271:    */     {
/* 272:    */       public void propertyChange(PropertyChangeEvent evt)
/* 273:    */       {
/* 274:271 */         CurSetupPanel.this.mysqlLabelSpeedHolder.setText(CurSetupPanel.this.progMySQL.msiInfo.strSpeed);
/* 275:    */       }
/* 276:273 */     });
/* 277:274 */     this.progMySQL.addPropertyChangeListener("MSI_TIMELEFT", new PropertyChangeListener()
/* 278:    */     {
/* 279:    */       public void propertyChange(PropertyChangeEvent evt)
/* 280:    */       {
/* 281:276 */         CurSetupPanel.this.mysqlLabelTimeleftHolder.setText(CurSetupPanel.this.progMySQL.msiInfo.strTimeleft);
/* 282:    */       }
/* 283:278 */     });
/* 284:279 */     this.progMySQL.addPropertyChangeListener("MSI_SIZEDOWNLOADED", new PropertyChangeListener()
/* 285:    */     {
/* 286:    */       public void propertyChange(PropertyChangeEvent evt)
/* 287:    */       {
/* 288:281 */         CurSetupPanel.this.mysqlLabelSizeDownloadHolder.setText(CurSetupPanel.this.progMySQL.msiInfo.strSizeDownloaded + " of " + CurSetupPanel.this.progMySQL.msiInfo.strTotalSize);
/* 289:    */       }
/* 290:283 */     });
/* 291:284 */     this.mysqlPanel.add(this.progMySQL);
/* 292:    */     
/* 293:286 */     this.mySQLDownloadButton = new JButton("Download");
/* 294:287 */     this.mySQLDownloadButton.setBounds(5, 90, 100, 20);
/* 295:288 */     this.mysqlPanel.add(this.mySQLDownloadButton);
/* 296:    */     
/* 297:290 */     this.installMySQL = new JButton("Install");
/* 298:291 */     this.installMySQL.setBounds(5, 115, 100, 20);
/* 299:292 */     this.mysqlPanel.add(this.installMySQL);
/* 300:294 */     if (Settings.instance().MySQL_FILE.exists()) {
/* 301:295 */       this.installMySQL.setEnabled(true);
/* 302:    */     } else {
/* 303:297 */       this.installMySQL.setEnabled(false);
/* 304:    */     }
/* 305:299 */     this.mysqlPanel.setVisible(false);
/* 306:300 */     this.tomcatPanel.setVisible(false);
/* 307:    */     
/* 308:302 */     panel.add(this.mysqlPanel);
/* 309:303 */     panel.add(this.tomcatPanel);
/* 310:    */     
/* 311:305 */     return panel;
/* 312:    */   }
/* 313:    */   
/* 314:    */   public JPanel createFinishMenu()
/* 315:    */   {
/* 316:314 */     JPanel panel = new JPanel();
/* 317:315 */     panel.setLayout(null);
/* 318:316 */     panel.setBounds(0, 0, 350, 325);
/* 319:317 */     panel.setBackground(new Color(16777215));
/* 320:    */     
/* 321:319 */     JLabel placeholder = new JLabel("Review Settings");
/* 322:320 */     placeholder.setFont(new Font("Corbel", 1, 19));
/* 323:321 */     placeholder.setBounds(10, 20, 290, 25);
/* 324:    */     
/* 325:323 */     JTextArea info = new JTextArea(3, 8);
/* 326:324 */     info.setEditable(false);
/* 327:325 */     info.setLineWrap(true);
/* 328:326 */     info.setText("Take this time to ensure that Tomcat and\nMySQL are both running as services on the\nspecified ports and specify the Tomcat directory.");
/* 329:    */     
/* 330:    */ 
/* 331:329 */     info.setFont(new Font("Corbel", 0, 14));
/* 332:330 */     info.setBounds(10, 50, 310, 80);
/* 333:331 */     panel.add(info);
/* 334:    */     
/* 335:333 */     this.mysqlT1 = new Timer();
/* 336:334 */     this.tomcatT1 = new Timer();
/* 337:335 */     this.mysqlStatus1 = new Status("MySQL", Settings.instance().isServiceUp(Settings.instance().MySQL_PORT));
/* 338:336 */     this.tomcatStatus1 = new Status("Tomcat", Settings.instance().isServiceUp(Settings.instance().TOMCAT_PORT));
/* 339:    */     
/* 340:338 */     this.mysqlT1.schedule(new TimerTask()
/* 341:    */     {
/* 342:    */       public void run()
/* 343:    */       {
/* 344:341 */         CurSetupPanel.this.mysqlStatus1.refresh(Settings.instance().isServiceUp(Settings.instance().MySQL_PORT));
/* 345:    */       }
/* 346:343 */     }, 7000L, 5000L);
/* 347:344 */     this.tomcatT1.schedule(new TimerTask()
/* 348:    */     {
/* 349:    */       public void run()
/* 350:    */       {
/* 351:347 */         CurSetupPanel.this.tomcatStatus1.refresh(Settings.instance().isServiceUp(Settings.instance().TOMCAT_PORT));
/* 352:    */       }
/* 353:349 */     }, 5000L, 5000L);
/* 354:    */     
/* 355:351 */     this.mysqlConfig = new StatusConfig("MySQL Port:", Settings.SERVICE_PORTS.MySQL);
/* 356:352 */     this.tomcatConfig = new StatusConfig("Tomcat Port:", Settings.SERVICE_PORTS.TOMCAT);
/* 357:353 */     this.dirChooser = new DirectoryChooser("Tomcat Directory:");
/* 358:354 */     this.dirButton = new JButton("...");
/* 359:    */     
/* 360:    */ 
/* 361:357 */     this.mysqlStatus1.setBounds(20, 140, 260, 25);
/* 362:358 */     this.tomcatStatus1.setBounds(20, 170, 260, 25);
/* 363:359 */     this.mysqlConfig.setBounds(20, 205, 260, 20);
/* 364:360 */     this.tomcatConfig.setBounds(20, 235, 260, 20);
/* 365:361 */     this.dirChooser.setBounds(20, 265, 260, 25);
/* 366:362 */     this.dirButton.setBounds(285, 265, 36, 25);
/* 367:    */     
/* 368:364 */     this.mysqlStatus1.setBackground(new Color(16777215));
/* 369:365 */     this.tomcatStatus1.setBackground(new Color(16777215));
/* 370:366 */     this.mysqlConfig.setBackground(new Color(16777215));
/* 371:367 */     this.tomcatConfig.setBackground(new Color(16777215));
/* 372:368 */     this.dirChooser.setBackground(new Color(16777215));
/* 373:369 */     this.dirButton.setBackground(new Color(16777215));
/* 374:    */     
/* 375:371 */     panel.add(this.mysqlStatus1);
/* 376:372 */     panel.add(this.tomcatStatus1);
/* 377:373 */     panel.add(this.mysqlConfig);
/* 378:374 */     panel.add(this.tomcatConfig);
/* 379:375 */     panel.add(this.dirChooser);
/* 380:376 */     panel.add(this.dirButton);
/* 381:    */     
/* 382:378 */     this.dirChooser.textField.setText(Settings.instance().TOMCAT_DIR);
/* 383:379 */     this.mysqlConfig.textPort.setText(""+Settings.instance().MySQL_PORT);
/* 384:380 */     this.tomcatConfig.textPort.setText(""+Settings.instance().TOMCAT_PORT);
/* 385:    */     
/* 386:382 */     panel.add(placeholder);
/* 387:383 */     return panel;
/* 388:    */   }
/* 389:    */ }


/* Location:           C:\Users\Andy\Desktop\WeaveUpdaterV1.2\Weave Installer.jar
 * Qualified Name:     weave.ui.CurSetupPanel
 * JD-Core Version:    0.7.0.1
 */