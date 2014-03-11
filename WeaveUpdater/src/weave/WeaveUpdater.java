/*   1:    */ package weave;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.awt.Cursor;
/*   5:    */ import java.awt.Desktop;
/*   6:    */ import java.awt.Dimension;
/*   7:    */ import java.awt.Font;
/*   8:    */ import java.awt.Toolkit;
/*   9:    */ import java.awt.event.ActionEvent;
/*  10:    */ import java.awt.event.ActionListener;
/*  11:    */ import java.awt.event.MouseEvent;
/*  12:    */ import java.awt.event.MouseListener;
/*  13:    */ import java.awt.event.WindowEvent;
/*  14:    */ import java.awt.event.WindowListener;
/*  15:    */ import java.awt.image.BufferedImage;
/*  16:    */ import java.io.File;
/*  17:    */ import java.io.PrintStream;
/*  18:    */ import java.net.URI;
/*  19:    */ import java.util.ArrayList;
/*  20:    */ import java.util.HashMap;
/*  21:    */ import java.util.Timer;
/*  22:    */ import java.util.TimerTask;
/*  23:    */ import javax.imageio.ImageIO;
/*  24:    */ import javax.swing.BorderFactory;
/*  25:    */ import javax.swing.ImageIcon;
/*  26:    */ import javax.swing.JButton;
/*  27:    */ import javax.swing.JCheckBox;
/*  28:    */ import javax.swing.JFileChooser;
/*  29:    */ import javax.swing.JFrame;
/*  30:    */ import javax.swing.JLabel;
/*  31:    */ import javax.swing.JOptionPane;
/*  32:    */ import javax.swing.JPanel;
/*  33:    */ import javax.swing.JProgressBar;
/*  34:    */ import javax.swing.JTable;
/*  35:    */ import javax.swing.JTextField;
/*  36:    */ import javax.swing.SwingUtilities;
/*  37:    */ import javax.swing.UIManager;
/*  38:    */ import weave.inc.ISetupPanel;
/*  39:    */ import weave.ui.CurSetupPanel;
/*  40:    */ import weave.ui.DirectoryChooser;
/*  41:    */ import weave.ui.PostSetupPanel;
/*  42:    */ import weave.ui.PreSetupPanel;
/*  43:    */ import weave.ui.ProgressUpdate;
/*  44:    */ import weave.ui.RevisionTable;
/*  45:    */ import weave.ui.WeaveStats;
/*  46:    */ 
/*  47:    */ public class WeaveUpdater
/*  48:    */   extends JFrame
/*  49:    */ {
/*  50: 61 */   public static WeaveUpdater updater = null;
/*  51:    */   public static final String PRE_SETUP = "PRE_SETUP";
/*  52:    */   public static final String CUR_SETUP = "CUR_SETUP";
/*  53:    */   public static final String POST_SETUP = "POST_SETUP";
/*  54: 66 */   public PreSetupPanel preSP = null;
/*  55: 67 */   public CurSetupPanel curSP = null;
/*  56: 68 */   public PostSetupPanel postSP = null;
/*  57: 69 */   public HashMap<String, JPanel> setupPanels = new HashMap();
/*  58: 70 */   public Thread ping = null;
/*  59: 71 */   public JButton cancelButton = null;
/*  60: 72 */   public JButton backButton = null;
/*  61: 73 */   public JButton nextButton = null;
/*  62: 74 */   public Timer pingTimer = null;
/*  63: 76 */   Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
/*  64: 77 */   JPanel rightPanel = null;
/*  65: 78 */   JButton helpButton = null;
/*  66: 79 */   JButton configureButton = null;
/*  67:    */   
/*  68:    */   public static void main(String[] args)
/*  69:    */   {
/*  70:    */     try
/*  71:    */     {
/*  72: 84 */       UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
/*  73: 85 */       updater = new WeaveUpdater();
/*  74:    */     }
/*  75:    */     catch (Exception e)
/*  76:    */     {
/*  77: 87 */       e.printStackTrace();
/*  78: 88 */       System.exit(64);
/*  79:    */     }
/*  80: 91 */     updater.addWindowListener(new WindowListener()
/*  81:    */     {
/*  82:    */       public void windowClosing(WindowEvent e)
/*  83:    */       {
/*  84: 93 */         System.out.println("Closing...");
/*  85: 94 */         if (Settings.instance().UNZIP_DIRECTORY.exists())
/*  86:    */         {
/*  87: 95 */           System.out.println("Deleted");
/*  88: 96 */           Revisions.recursiveDelete(Settings.instance().UNZIP_DIRECTORY);
/*  89:    */           try
/*  90:    */           {
/*  91: 97 */             Thread.sleep(1000L);
/*  92:    */           }
/*  93:    */           catch (Exception e1)
/*  94:    */           {
/*  95: 97 */             e1.printStackTrace();
/*  96:    */           }
/*  97:    */         }
/*  98: 99 */         System.exit(0);
/*  99:    */       }
/* 100:    */       
/* 101:    */       public void windowDeactivated(WindowEvent e) {}
/* 102:    */       
/* 103:    */       public void windowClosed(WindowEvent e) {}
/* 104:    */       
/* 105:    */       public void windowActivated(WindowEvent e) {}
/* 106:    */       
/* 107:    */       public void windowDeiconified(WindowEvent e) {}
/* 108:    */       
/* 109:    */       public void windowIconified(WindowEvent e) {}
/* 110:    */       
/* 111:    */       public void windowOpened(WindowEvent e) {}
/* 112:    */     });
/* 113:    */   }
/* 114:    */   
/* 115:    */   public WeaveUpdater()
/* 116:    */     throws Exception
/* 117:    */   {
/* 118:116 */     setSize(500, 400);
/* 119:117 */     setResizable(false);
/* 120:118 */     setLayout(null);
/* 121:119 */     setTitle(Settings.instance().TITLE);
/* 122:120 */     setDefaultCloseOperation(0);
/* 123:121 */     setLocation(this.screen.width / 2 - getWidth() / 2, this.screen.height / 2 - getHeight() / 2);
/* 124:122 */     setIconImage(ImageIO.read(WeaveUpdater.class.getResource("/resources/update.png")));
/* 125:    */     
/* 126:    */ 
/* 127:125 */     JPanel leftPanel = new JPanel();
/* 128:126 */     leftPanel.setLayout(null);
/* 129:127 */     leftPanel.setBounds(0, 0, 150, 325);
/* 130:128 */     leftPanel.setBackground(new Color(15658734));
/* 131:129 */     leftPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.black));
/* 132:    */     
/* 133:131 */     BufferedImage oicLogo = ImageIO.read(WeaveUpdater.class.getResource("/resources/oic4.png"));
/* 134:132 */     JLabel oicLabel = new JLabel("", new ImageIcon(oicLogo), 0);
/* 135:133 */     oicLabel.setBounds(10, 10, 125, 57);
/* 136:134 */     leftPanel.add(oicLabel);
/* 137:    */     
/* 138:136 */     JLabel iweaveLink = new JLabel("oicweave.org");
/* 139:137 */     iweaveLink.setBounds(30, 300, 125, 20);
/* 140:138 */     iweaveLink.setCursor(new Cursor(12));
/* 141:139 */     iweaveLink.setFont(new Font("Corbel", 0, 15));
/* 142:140 */     iweaveLink.addMouseListener(new MouseListener()
/* 143:    */     {
/* 144:    */       public void mouseReleased(MouseEvent e) {}
/* 145:    */       
/* 146:    */       public void mousePressed(MouseEvent e) {}
/* 147:    */       
/* 148:    */       public void mouseExited(MouseEvent e) {}
/* 149:    */       
/* 150:    */       public void mouseEntered(MouseEvent e) {}
/* 151:    */       
/* 152:    */       public void mouseClicked(MouseEvent e)
/* 153:    */       {
/* 154:146 */         if (Desktop.isDesktopSupported()) {
/* 155:    */           try
/* 156:    */           {
/* 157:148 */             Desktop.getDesktop().browse(new URI("http://oicweave.org"));
/* 158:    */           }
/* 159:    */           catch (Exception e1)
/* 160:    */           {
/* 161:150 */             e1.printStackTrace();
/* 162:    */           }
/* 163:    */         }
/* 164:    */       }
/* 165:155 */     });
/* 166:156 */     leftPanel.add(iweaveLink);
/* 167:157 */     leftPanel.setVisible(false);
/* 168:158 */     add(leftPanel);
/* 169:    */     
/* 170:    */ 
/* 171:161 */     JPanel bottomPanel = new JPanel();
/* 172:162 */     bottomPanel.setLayout(null);
/* 173:163 */     bottomPanel.setBounds(0, 325, 500, 50);
/* 174:164 */     bottomPanel.setBackground(new Color(5274282));
/* 175:165 */     bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.black));
/* 176:    */     
/* 177:167 */     this.helpButton = new JButton("Help");
/* 178:168 */     this.helpButton.setBounds(10, 13, 80, 25);
/* 179:169 */     this.helpButton.setBackground(new Color(5274282));
/* 180:170 */     this.helpButton.setToolTipText("Open wiki page for help");
/* 181:171 */     this.helpButton.addActionListener(new ActionListener()
/* 182:    */     {
/* 183:    */       public void actionPerformed(ActionEvent arg0)
/* 184:    */       {
/* 185:174 */         if (Desktop.isDesktopSupported()) {
/* 186:    */           try
/* 187:    */           {
/* 188:176 */             Settings.instance().getClass();Desktop.getDesktop().browse(new URI("http://info.oicweave.org/projects/weave/wiki/Installer"));
/* 189:    */           }
/* 190:    */           catch (Exception e)
/* 191:    */           {
/* 192:178 */             e.printStackTrace();
/* 193:    */           }
/* 194:    */         } else {
/* 195:181 */           JOptionPane.showMessageDialog(null, "This feature is not supported by the \nversion of Java you are running.", "Error", 0);
/* 196:    */         }
/* 197:    */       }
/* 198:183 */     });
/* 199:184 */     this.helpButton.setVisible(true);
/* 200:    */     
/* 201:186 */     this.configureButton = new JButton("Configure");
/* 202:187 */     this.configureButton.setBounds(100, 13, 100, 25);
/* 203:188 */     this.configureButton.setBackground(new Color(5274282));
/* 204:189 */     this.configureButton.setToolTipText("Edit configuration settings or check for a new installation of Tomcat or MySQL.");
/* 205:190 */     this.configureButton.addActionListener(new ActionListener()
/* 206:    */     {
/* 207:    */       public void actionPerformed(ActionEvent arg0)
/* 208:    */       {
/* 209:    */         try
/* 210:    */         {
/* 211:194 */           WeaveUpdater.this.switchToCurSetupPanel();
/* 212:    */         }
/* 213:    */         catch (Exception e)
/* 214:    */         {
/* 215:196 */           e.printStackTrace();
/* 216:    */         }
/* 217:    */       }
/* 218:199 */     });
/* 219:200 */     this.configureButton.setVisible(false);
/* 220:    */     
/* 221:202 */     this.backButton = new JButton("< Back");
/* 222:203 */     this.backButton.setBounds(200, 13, 80, 25);
/* 223:204 */     this.backButton.setBackground(new Color(5274282));
/* 224:    */     
/* 225:206 */     this.nextButton = new JButton("Next >");
/* 226:207 */     this.nextButton.setBounds(280, 13, 80, 25);
/* 227:208 */     this.nextButton.setBackground(new Color(5274282));
/* 228:    */     
/* 229:210 */     this.cancelButton = new JButton("Cancel");
/* 230:211 */     this.cancelButton.setBounds(400, 13, 80, 25);
/* 231:212 */     this.cancelButton.setBackground(new Color(5274282));
/* 232:213 */     this.cancelButton.setToolTipText("Close the installer");
/* 233:214 */     this.cancelButton.addActionListener(new ActionListener()
/* 234:    */     {
/* 235:    */       public void actionPerformed(ActionEvent arg0)
/* 236:    */       {
/* 237:217 */         int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit?", "Confirm", 0);
/* 238:218 */         if (response == 0)
/* 239:    */         {
/* 240:219 */           System.gc();
/* 241:    */           try
/* 242:    */           {
/* 243:221 */             Thread.sleep(200L);
/* 244:    */           }
/* 245:    */           catch (Exception e)
/* 246:    */           {
/* 247:223 */             e.printStackTrace();
/* 248:    */           }
/* 249:225 */           System.exit(0);
/* 250:    */         }
/* 251:    */       }
/* 252:229 */     });
/* 253:230 */     this.backButton.setEnabled(false);
/* 254:    */     
/* 255:232 */     bottomPanel.add(this.helpButton);
/* 256:233 */     bottomPanel.add(this.configureButton);
/* 257:234 */     bottomPanel.add(this.backButton);
/* 258:235 */     bottomPanel.add(this.cancelButton);
/* 259:236 */     bottomPanel.add(this.nextButton);
/* 260:237 */     add(bottomPanel);
/* 261:    */     
/* 262:    */ 
/* 263:240 */     this.rightPanel = new JPanel();
/* 264:241 */     this.rightPanel.setLayout(null);
/* 265:242 */     this.rightPanel.setBounds(150, 0, 350, 325);
/* 266:243 */     this.rightPanel.setBackground(new Color(16777215));
/* 267:244 */     this.rightPanel.setVisible(false);
/* 268:245 */     add(this.rightPanel);
/* 269:    */     
/* 270:247 */     this.rightPanel.setVisible(true);
/* 271:248 */     bottomPanel.setVisible(true);
/* 272:249 */     leftPanel.setVisible(true);
/* 273:250 */     setVisible(true);
/* 274:    */     
/* 275:252 */     switchToPreSetupPanel(this.rightPanel);
/* 276:255 */     if (Settings.instance().isConnectedToInternet().booleanValue())
/* 277:    */     {
/* 278:257 */       Settings.instance().BEST_TOMCAT = Settings.instance().getBestTomcatURL();
/* 279:258 */       System.out.println("Tomcat: " + Settings.instance().BEST_TOMCAT);
/* 280:259 */       Settings.instance().BEST_MYSQL = Settings.instance().getBestMySQLURL();
/* 281:260 */       System.out.println("MySQL: " + Settings.instance().BEST_MYSQL);
/* 282:    */     }
/* 283:    */     else
/* 284:    */     {
/* 285:264 */       this.pingTimer = new Timer();
/* 286:265 */       this.pingTimer.schedule(new TimerTask()
/* 287:    */       {
/* 288:    */         public void run()
/* 289:    */         {
/* 290:268 */           if (Settings.instance().isConnectedToInternet().booleanValue())
/* 291:    */           {
/* 292:270 */             Settings.instance().TOMCAT_FILE = new File(Settings.instance().EXE_DIRECTORY, "/tomcat_" + Settings.instance().getLatestTomcatVersion() + ".exe");
/* 293:271 */             Settings.instance().BEST_TOMCAT = Settings.instance().getBestTomcatURL();
/* 294:272 */             System.out.println("Tomcat: " + Settings.instance().BEST_TOMCAT);
/* 295:273 */             Settings.instance().MySQL_FILE = new File(Settings.instance().EXE_DIRECTORY, "/mysql_" + Settings.instance().getLatestMySQLVersion() + ".msi");
/* 296:274 */             Settings.instance().BEST_MYSQL = Settings.instance().getBestMySQLURL();
/* 297:275 */             System.out.println("MySQL: " + Settings.instance().BEST_MYSQL);
/* 298:276 */             WeaveUpdater.this.pingTimer.cancel();
/* 299:277 */             WeaveUpdater.this.nextButton.setEnabled(true);
/* 300:    */           }
/* 301:    */         }
/* 302:280 */       }, 1000L, 5000L);
/* 303:    */       
/* 304:282 */       this.nextButton.setEnabled(false);
/* 305:283 */       JOptionPane.showMessageDialog(null, "Internet connection could not be established.\n\nPlease make sure you are connected to the\ninternet before you continue.", 
/* 306:    */       
/* 307:285 */         "Warning", 2);
/* 308:    */     }
/* 309:    */   }
/* 310:    */   
/* 311:    */   public void switchToPreSetupPanel()
/* 312:    */     throws Exception
/* 313:    */   {
/* 314:297 */     if (this.setupPanels.containsKey("PRE_SETUP"))
/* 315:    */     {
/* 316:299 */       for (String key : this.setupPanels.keySet())
/* 317:    */       {
/* 318:300 */         ((ISetupPanel)this.setupPanels.get(key)).hidePanels();
/* 319:301 */         ((JPanel)this.setupPanels.get(key)).setVisible(false);
/* 320:    */       }
/* 321:303 */       ((JPanel)this.setupPanels.get("PRE_SETUP")).setVisible(true);
/* 322:304 */       ((ISetupPanel)this.setupPanels.get("PRE_SETUP")).showPanels();
/* 323:    */       
/* 324:306 */       this.backButton.setEnabled(false);this.backButton.setVisible(true);
/* 325:307 */       this.nextButton.setEnabled(true);this.nextButton.setVisible(true);
/* 326:308 */       this.configureButton.setEnabled(true);this.configureButton.setVisible(false);
/* 327:    */       
/* 328:310 */       removeButtonActions();
/* 329:311 */       this.preSP.addActionToButton(this.nextButton, new ActionListener()
/* 330:    */       {
/* 331:    */         public void actionPerformed(ActionEvent e)
/* 332:    */         {
/* 333:    */           try
/* 334:    */           {
/* 335:315 */             if (Settings.instance().settingsExists().booleanValue()) {
/* 336:316 */               WeaveUpdater.this.switchToPostSetupPanel();
/* 337:    */             } else {
/* 338:318 */               WeaveUpdater.this.switchToCurSetupPanel();
/* 339:    */             }
/* 340:    */           }
/* 341:    */           catch (Exception ex)
/* 342:    */           {
/* 343:320 */             ex.printStackTrace();
/* 344:    */           }
/* 345:    */         }
/* 346:323 */       });
/* 347:324 */       this.backButton.setEnabled(false);
/* 348:325 */       this.nextButton.setEnabled(true);
/* 349:    */     }
/* 350:    */     else
/* 351:    */     {
/* 352:327 */       switchToPreSetupPanel(this.rightPanel);
/* 353:    */     }
/* 354:    */   }
/* 355:    */   
/* 356:    */   public void switchToPreSetupPanel(JPanel parent)
/* 357:    */     throws Exception
/* 358:    */   {
/* 359:340 */     if (this.setupPanels.containsKey("PRE_SETUP"))
/* 360:    */     {
/* 361:341 */       switchToPreSetupPanel();
/* 362:342 */       return;
/* 363:    */     }
/* 364:345 */     this.preSP = new PreSetupPanel();
/* 365:346 */     this.preSP.hidePanels();
/* 366:347 */     this.setupPanels.put("PRE_SETUP", this.preSP);
/* 367:348 */     parent.add(this.preSP);
/* 368:349 */     switchToPreSetupPanel();
/* 369:    */   }
/* 370:    */   
/* 371:    */   public void switchToCurSetupPanel()
/* 372:    */     throws Exception
/* 373:    */   {
/* 374:354 */     if (this.setupPanels.containsKey("CUR_SETUP"))
/* 375:    */     {
/* 376:356 */       for (String key : this.setupPanels.keySet())
/* 377:    */       {
/* 378:357 */         ((ISetupPanel)this.setupPanels.get(key)).hidePanels();
/* 379:358 */         ((JPanel)this.setupPanels.get(key)).setVisible(false);
/* 380:    */       }
/* 381:360 */       ((JPanel)this.setupPanels.get("CUR_SETUP")).setVisible(true);
/* 382:361 */       ((ISetupPanel)this.setupPanels.get("CUR_SETUP")).showPanels();
/* 383:    */       
/* 384:363 */       removeButtonActions();
/* 385:364 */       this.curSP.addActionToButton(this.backButton, new ActionListener()
/* 386:    */       {
/* 387:    */         public void actionPerformed(ActionEvent arg0)
/* 388:    */         {
/* 389:367 */           if (WeaveUpdater.this.curSP.getCurrentPanelIndex() == 0)
/* 390:    */           {
/* 391:    */             try
/* 392:    */             {
/* 393:369 */               WeaveUpdater.this.switchToPreSetupPanel();
/* 394:    */             }
/* 395:    */             catch (Exception e)
/* 396:    */             {
/* 397:371 */               e.printStackTrace();
/* 398:    */             }
/* 399:    */           }
/* 400:376 */           else if (WeaveUpdater.this.curSP.getCurrentPanelIndex() > 0)
/* 401:    */           {
/* 402:377 */             WeaveUpdater.this.curSP.previousPanel();
/* 403:378 */             if ((WeaveUpdater.this.curSP.getCurrentPanelIndex() == WeaveUpdater.this.curSP.getNumberOfPanels() - 2) && 
/* 404:379 */               (!WeaveUpdater.this.curSP.tomcatCheck.isSelected()) && (!WeaveUpdater.this.curSP.mysqlCheck.isSelected())) {
/* 405:380 */               WeaveUpdater.this.backButton.doClick();
/* 406:    */             }
/* 407:382 */             WeaveUpdater.this.nextButton.setEnabled(true);
/* 408:383 */             WeaveUpdater.this.backButton.setEnabled(true);
/* 409:384 */             WeaveUpdater.this.nextButton.setText("Next >");
/* 410:385 */             WeaveUpdater.this.nextButton.setBounds(WeaveUpdater.this.nextButton.getX(), WeaveUpdater.this.nextButton.getY(), 80, WeaveUpdater.this.nextButton.getHeight());
/* 411:    */           }
/* 412:    */         }
/* 413:388 */       });
/* 414:389 */       this.curSP.addActionToButton(this.nextButton, new ActionListener()
/* 415:    */       {
/* 416:    */         public void actionPerformed(ActionEvent e)
/* 417:    */         {
/* 418:392 */           if (WeaveUpdater.this.curSP.getCurrentPanelIndex() == WeaveUpdater.this.curSP.getNumberOfPanels() - 1)
/* 419:    */           {
/* 420:394 */             if ((Settings.instance().MySQL_PORT == 0) || (Settings.instance().TOMCAT_PORT == 0) || (Settings.instance().TOMCAT_DIR.equals("")))
/* 421:    */             {
/* 422:395 */               JOptionPane.showMessageDialog(null, "Error validating settings information.", "Error", 0);
/* 423:    */             }
/* 424:396 */             else if (Settings.instance().writeSettings().booleanValue())
/* 425:    */             {
/* 426:397 */               JOptionPane.showMessageDialog(null, "Settings saved successfully", "Settings", 1);
/* 427:398 */               WeaveUpdater.this.nextButton.setBounds(WeaveUpdater.this.nextButton.getX(), WeaveUpdater.this.nextButton.getY(), 80, WeaveUpdater.this.nextButton.getHeight());
/* 428:    */               try
/* 429:    */               {
/* 430:400 */                 WeaveUpdater.this.switchToPostSetupPanel(WeaveUpdater.this.rightPanel);
/* 431:    */               }
/* 432:    */               catch (Exception e1)
/* 433:    */               {
/* 434:402 */                 e1.printStackTrace();
/* 435:    */               }
/* 436:    */             }
/* 437:    */             else
/* 438:    */             {
/* 439:405 */               JOptionPane.showMessageDialog(null, "Error trying to save settings.", "Error", 0);
/* 440:    */             }
/* 441:    */           }
/* 442:407 */           else if (WeaveUpdater.this.curSP.getCurrentPanelIndex() < WeaveUpdater.this.curSP.getNumberOfPanels())
/* 443:    */           {
/* 444:409 */             WeaveUpdater.this.curSP.nextPanel();
/* 445:410 */             if ((WeaveUpdater.this.curSP.getCurrentPanelIndex() == 1) && 
/* 446:411 */               (!WeaveUpdater.this.curSP.tomcatCheck.isSelected()) && (!WeaveUpdater.this.curSP.mysqlCheck.isSelected())) {
/* 447:412 */               WeaveUpdater.this.nextButton.doClick();
/* 448:    */             }
/* 449:414 */             WeaveUpdater.this.backButton.setEnabled(true);
/* 450:415 */             WeaveUpdater.this.backButton.setVisible(true);
/* 451:416 */             if (WeaveUpdater.this.curSP.getCurrentPanelIndex() == WeaveUpdater.this.curSP.getNumberOfPanels() - 1)
/* 452:    */             {
/* 453:417 */               WeaveUpdater.this.nextButton.setText("Save & Finish");
/* 454:418 */               WeaveUpdater.this.nextButton.setEnabled(true);
/* 455:419 */               WeaveUpdater.this.nextButton.setBounds(WeaveUpdater.this.nextButton.getX(), WeaveUpdater.this.nextButton.getY(), 100, WeaveUpdater.this.nextButton.getHeight());
/* 456:    */             }
/* 457:    */           }
/* 458:    */         }
/* 459:424 */       });
/* 460:425 */       this.backButton.setEnabled(true);this.backButton.setVisible(true);
/* 461:426 */       this.nextButton.setEnabled(true);this.nextButton.setVisible(true);
/* 462:427 */       this.backButton.setText("< Back");this.nextButton.setText("Next >");
/* 463:428 */       this.configureButton.setEnabled(true);this.configureButton.setVisible(false);
/* 464:    */     }
/* 465:    */     else
/* 466:    */     {
/* 467:430 */       switchToCurSetupPanel(this.rightPanel);
/* 468:    */     }
/* 469:    */   }
/* 470:    */   
/* 471:    */   public void switchToCurSetupPanel(JPanel parent)
/* 472:    */     throws Exception
/* 473:    */   {
/* 474:435 */     if (this.setupPanels.containsKey("CUR_SETUP"))
/* 475:    */     {
/* 476:436 */       switchToCurSetupPanel();
/* 477:437 */       return;
/* 478:    */     }
/* 479:440 */     this.curSP = new CurSetupPanel();
/* 480:441 */     this.curSP.hidePanels();
/* 481:442 */     this.curSP.addActionToButton(this.curSP.tomcatDownloadButton, new ActionListener()
/* 482:    */     {
/* 483:    */       public void actionPerformed(ActionEvent arg0)
/* 484:    */       {
/* 485:445 */         WeaveUpdater.this.curSP.tomcatDownloadButton.setEnabled(false);
/* 486:446 */         WeaveUpdater.this.nextButton.setEnabled(false);
/* 487:447 */         WeaveUpdater.this.backButton.setEnabled(false);
/* 488:448 */         WeaveUpdater.this.cancelButton.setEnabled(false);
/* 489:    */         try
/* 490:    */         {
/* 491:450 */           if (Settings.instance().TOMCAT_FILE.exists())
/* 492:    */           {
/* 493:451 */             int response = JOptionPane.showConfirmDialog(null, "Weave Installer has detected that an executable installer already exists.\nWould you like to re-download and overwrite?", 
/* 494:    */             
/* 495:453 */               "Confirm", 0);
/* 496:454 */             if (response == 0)
/* 497:    */             {
/* 498:    */               try
/* 499:    */               {
/* 500:456 */                 WeaveUpdater.this.curSP.installTomcat.setVisible(false);
/* 501:457 */                 WeaveUpdater.this.curSP.installTomcat.setEnabled(false);
/* 502:458 */                 WeaveUpdater.this.curSP.progTomcat.downloadMSI(WeaveUpdater.this.curSP.tomcatPanel, WeaveUpdater.this.curSP.tomcatDownloadButton, Settings.MSI_TYPE.TOMCAT_MSI);
/* 503:    */               }
/* 504:    */               catch (Exception e)
/* 505:    */               {
/* 506:460 */                 e.printStackTrace();
/* 507:    */               }
/* 508:    */             }
/* 509:    */             else
/* 510:    */             {
/* 511:463 */               WeaveUpdater.this.curSP.tomcatDownloadButton.setEnabled(true);
/* 512:464 */               WeaveUpdater.this.nextButton.setEnabled(true);
/* 513:465 */               WeaveUpdater.this.backButton.setEnabled(true);
/* 514:466 */               WeaveUpdater.this.cancelButton.setEnabled(true);
/* 515:    */             }
/* 516:    */           }
/* 517:    */           else
/* 518:    */           {
/* 519:469 */             WeaveUpdater.this.curSP.installTomcat.setVisible(false);
/* 520:470 */             WeaveUpdater.this.curSP.installTomcat.setEnabled(false);
/* 521:471 */             WeaveUpdater.this.curSP.progTomcat.downloadMSI(WeaveUpdater.this.curSP.tomcatPanel, WeaveUpdater.this.curSP.tomcatDownloadButton, Settings.MSI_TYPE.TOMCAT_MSI);
/* 522:    */           }
/* 523:    */         }
/* 524:    */         catch (Exception e)
/* 525:    */         {
/* 526:474 */           e.printStackTrace();
/* 527:    */         }
/* 528:    */       }
/* 529:477 */     });
/* 530:478 */     this.curSP.addActionToButton(this.curSP.mySQLDownloadButton, new ActionListener()
/* 531:    */     {
/* 532:    */       public void actionPerformed(ActionEvent arg0)
/* 533:    */       {
/* 534:481 */         WeaveUpdater.this.curSP.mySQLDownloadButton.setEnabled(false);
/* 535:482 */         WeaveUpdater.this.nextButton.setEnabled(false);
/* 536:483 */         WeaveUpdater.this.backButton.setEnabled(false);
/* 537:484 */         WeaveUpdater.this.cancelButton.setEnabled(false);
/* 538:    */         try
/* 539:    */         {
/* 540:486 */           if (Settings.instance().MySQL_FILE.exists())
/* 541:    */           {
/* 542:487 */             int response = JOptionPane.showConfirmDialog(null, "Weave Installer has detected that an executable installer already exists.\nWould you like to re-download and overwrite?", 
/* 543:    */             
/* 544:489 */               "Confirm", 0);
/* 545:490 */             if (response == 0)
/* 546:    */             {
/* 547:    */               try
/* 548:    */               {
/* 549:492 */                 WeaveUpdater.this.curSP.installMySQL.setVisible(false);
/* 550:493 */                 WeaveUpdater.this.curSP.progMySQL.downloadMSI(WeaveUpdater.this.curSP.mysqlPanel, WeaveUpdater.this.curSP.mySQLDownloadButton, Settings.MSI_TYPE.MySQL_MSI);
/* 551:    */               }
/* 552:    */               catch (Exception e)
/* 553:    */               {
/* 554:495 */                 e.printStackTrace();
/* 555:    */               }
/* 556:    */             }
/* 557:    */             else
/* 558:    */             {
/* 559:498 */               WeaveUpdater.this.curSP.mySQLDownloadButton.setEnabled(true);
/* 560:499 */               WeaveUpdater.this.nextButton.setEnabled(true);
/* 561:500 */               WeaveUpdater.this.backButton.setEnabled(true);
/* 562:501 */               WeaveUpdater.this.cancelButton.setEnabled(true);
/* 563:    */             }
/* 564:    */           }
/* 565:    */           else
/* 566:    */           {
/* 567:504 */             WeaveUpdater.this.curSP.installMySQL.setVisible(false);
/* 568:505 */             WeaveUpdater.this.curSP.progMySQL.downloadMSI(WeaveUpdater.this.curSP.mysqlPanel, WeaveUpdater.this.curSP.mySQLDownloadButton, Settings.MSI_TYPE.MySQL_MSI);
/* 569:    */           }
/* 570:    */         }
/* 571:    */         catch (Exception e)
/* 572:    */         {
/* 573:508 */           e.printStackTrace();
/* 574:    */         }
/* 575:    */       }
/* 576:511 */     });
/* 577:512 */     this.curSP.addActionToButton(this.curSP.installTomcat, new ActionListener()
/* 578:    */     {
/* 579:    */       public void actionPerformed(ActionEvent arg0)
/* 580:    */       {
/* 581:514 */         WeaveUpdater.this.curSP.progMySQL.runExecutable(Settings.instance().TOMCAT_FILE);
/* 582:    */       }
/* 583:516 */     });
/* 584:517 */     this.curSP.addActionToButton(this.curSP.installMySQL, new ActionListener()
/* 585:    */     {
/* 586:    */       public void actionPerformed(ActionEvent arg0)
/* 587:    */       {
/* 588:519 */         WeaveUpdater.this.curSP.progMySQL.runExecutable(Settings.instance().MySQL_FILE);
/* 589:    */       }
/* 590:521 */     });
/* 591:522 */     this.curSP.addActionToButton(this.curSP.dirButton, new ActionListener()
/* 592:    */     {
/* 593:    */       public void actionPerformed(ActionEvent arg0)
/* 594:    */       {
/* 595:525 */         WeaveUpdater.this.curSP.dirChooser.fileChooser.setFileSelectionMode(1);
/* 596:526 */         int retVal = WeaveUpdater.this.curSP.dirChooser.fileChooser.showOpenDialog(null);
/* 597:527 */         if (retVal == 0)
/* 598:    */         {
/* 599:528 */           String dir = WeaveUpdater.this.curSP.dirChooser.fileChooser.getSelectedFile().getPath();
/* 600:529 */           File f = new File(dir + "/webapps/ROOT/");
/* 601:530 */           File g = new File(dir + "/Uninstall.exe");
/* 602:531 */           if ((f.exists()) && (g.exists()))
/* 603:    */           {
/* 604:532 */             Settings.instance().TOMCAT_DIR = dir;
/* 605:    */           }
/* 606:    */           else
/* 607:    */           {
/* 608:534 */             Settings.instance().TOMCAT_DIR = "";
/* 609:535 */             JOptionPane.showMessageDialog(null, "Invalid Tomcat Directory", "Error", 0);
/* 610:    */           }
/* 611:    */         }
/* 612:538 */         WeaveUpdater.this.curSP.dirChooser.textField.setText(Settings.instance().TOMCAT_DIR);
/* 613:    */       }
/* 614:540 */     });
/* 615:541 */     this.setupPanels.put("CUR_SETUP", this.curSP);
/* 616:542 */     parent.add(this.curSP);
/* 617:543 */     switchToCurSetupPanel();
/* 618:    */   }
/* 619:    */   
/* 620:    */   public void switchToPostSetupPanel()
/* 621:    */     throws Exception
/* 622:    */   {
/* 623:548 */     if (this.setupPanels.containsKey("POST_SETUP"))
/* 624:    */     {
/* 625:550 */       for (String key : this.setupPanels.keySet())
/* 626:    */       {
/* 627:551 */         ((ISetupPanel)this.setupPanels.get(key)).hidePanels();
/* 628:552 */         ((JPanel)this.setupPanels.get(key)).setVisible(false);
/* 629:    */       }
/* 630:554 */       ((JPanel)this.setupPanels.get("POST_SETUP")).setVisible(true);
/* 631:555 */       ((ISetupPanel)this.setupPanels.get("POST_SETUP")).showPanels();
/* 632:    */       
/* 633:557 */       this.backButton.setEnabled(false);this.backButton.setVisible(false);
/* 634:558 */       this.nextButton.setEnabled(false);this.nextButton.setVisible(false);
/* 635:559 */       this.configureButton.setEnabled(true);this.configureButton.setVisible(true);
/* 636:    */       
/* 637:561 */       removeButtonActions();
/* 638:    */     }
/* 639:    */     else
/* 640:    */     {
/* 641:563 */       switchToPostSetupPanel(this.rightPanel);
/* 642:    */     }
/* 643:    */   }
/* 644:    */   
/* 645:    */   public void switchToPostSetupPanel(JPanel parent)
/* 646:    */     throws Exception
/* 647:    */   {
/* 648:568 */     if (this.setupPanels.containsKey("POST_SETUP"))
/* 649:    */     {
/* 650:569 */       switchToPostSetupPanel();
/* 651:570 */       return;
/* 652:    */     }
/* 653:573 */     this.postSP = new PostSetupPanel();
/* 654:574 */     this.postSP.hidePanels();
/* 655:575 */     this.postSP.addActionToButton(this.postSP.installButton, new ActionListener()
/* 656:    */     {
/* 657:    */       public void actionPerformed(ActionEvent e)
/* 658:    */       {
/* 659:578 */         if (!Settings.instance().isConnectedToInternet().booleanValue())
/* 660:    */         {
/* 661:579 */           WeaveUpdater.this.postSP.progress.progBar.setStringPainted(true);
/* 662:580 */           WeaveUpdater.this.postSP.progress.progBar.setString("No Internet Connection");
/* 663:581 */           WeaveUpdater.this.postSP.progress.progBar.setValue(0);
/* 664:582 */           return;
/* 665:    */         }
/* 666:584 */         if ((Settings.instance().isServiceUp(Settings.instance().TOMCAT_PORT).booleanValue()) && 
/* 667:585 */           (!Settings.instance().TOMCAT_DIR.equals("")))
/* 668:    */         {
/* 669:586 */           WeaveUpdater.this.postSP.installButton.setEnabled(false);
/* 670:587 */           WeaveUpdater.this.postSP.deploy.setEnabled(false);
/* 671:588 */           WeaveUpdater.this.postSP.deleteButton.setEnabled(false);
/* 672:589 */           WeaveUpdater.this.postSP.checkButton.setEnabled(false);
/* 673:590 */           WeaveUpdater.this.postSP.pruneButton.setEnabled(false);
/* 674:591 */           WeaveUpdater.this.postSP.progress.downloadZip(WeaveUpdater.this.postSP.checkButton);
/* 675:    */         }
/* 676:    */         else
/* 677:    */         {
/* 678:595 */           JOptionPane.showMessageDialog(
/* 679:596 */             null, 
/* 680:597 */             "Tomcat must be properly configured and running to install Weave.", 
/* 681:598 */             "Error", 0);
/* 682:    */         }
/* 683:    */       }
/* 684:601 */     });
/* 685:602 */     this.postSP.addActionToButton(this.postSP.checkButton, new ActionListener()
/* 686:    */     {
/* 687:    */       public void actionPerformed(ActionEvent e)
/* 688:    */       {
/* 689:609 */         Thread t = new Thread(new Runnable()
/* 690:    */         {
/* 691:    */           public void run()
/* 692:    */           {
/* 693:619 */             WeaveUpdater.this.postSP.deploy.setEnabled(true);
/* 694:620 */             WeaveUpdater.this.postSP.deleteButton.setEnabled(true);
/* 695:621 */             WeaveUpdater.this.postSP.pruneButton.setEnabled(true);
/* 696:    */             
/* 697:    */ 
/* 698:    */ 
/* 699:    */ 
/* 700:    */ 
/* 701:627 */             final int ret = Revisions.checkForUpdates(true);
/* 702:628 */             WeaveUpdater.this.postSP.weaveStats.refresh(ret);
/* 703:636 */             if ((ret == 1) && (!Settings.instance().TOMCAT_DIR.equals("")))
/* 704:    */             {
/* 705:638 */               WeaveUpdater.this.postSP.installButton.setEnabled(true);
/* 706:639 */               WeaveUpdater.this.postSP.launchAdmin.setForeground(Color.BLACK);
/* 707:    */             }
/* 708:    */             else
/* 709:    */             {
/* 710:643 */               WeaveUpdater.this.postSP.installButton.setEnabled(false);
/* 711:    */             }
/* 712:646 */             WeaveUpdater.this.postSP.revisionTable.updateTableData();
/* 713:    */             
/* 714:648 */             SwingUtilities.invokeLater(new Runnable()
/* 715:    */             {
/* 716:    */               public void run()
/* 717:    */               {
/* 718:    */                 try
/* 719:    */                 {
/* 720:652 */                   Thread.sleep(3000L);
/* 721:    */                 }
/* 722:    */                 catch (InterruptedException e)
/* 723:    */                 {
/* 724:654 */                   e.printStackTrace();
/* 725:    */                 }
/* 726:656 */                 WeaveUpdater.this.postSP.progress.progBar.setStringPainted(true);
/* 727:657 */                 WeaveUpdater.this.postSP.progress.progBar.setValue(0);
/* 728:658 */                 if (ret == -2) {
/* 729:659 */                   WeaveUpdater.this.postSP.progress.progBar.setString("No Internet Connection");
/* 730:    */                 } else {
/* 731:661 */                   WeaveUpdater.this.postSP.progress.progBar.setString("");
/* 732:    */                 }
/* 733:    */               }
/* 734:    */             });
/* 735:    */           }
/* 736:665 */         });
/* 737:666 */         t.start();
/* 738:    */       }
/* 739:668 */     });
/* 740:669 */     this.postSP.addActionToButton(this.postSP.deploy, new ActionListener()
/* 741:    */     {
/* 742:    */       public void actionPerformed(ActionEvent e)
/* 743:    */       {
/* 744:674 */         int n = WeaveUpdater.this.postSP.revisionTable.table.getSelectedRow();
/* 745:675 */         if (n < 0) {
/* 746:675 */           return;
/* 747:    */         }
/* 748:681 */         File f = (File)Revisions.getRevisionData().get(n);
/* 749:686 */         if (!Settings.instance().TOMCAT_DIR.equals(""))
/* 750:    */         {
/* 751:687 */           WeaveUpdater.this.postSP.installButton.setEnabled(false);
/* 752:688 */           WeaveUpdater.this.postSP.deleteButton.setEnabled(false);
/* 753:689 */           WeaveUpdater.this.postSP.deploy.setEnabled(false);
/* 754:690 */           WeaveUpdater.this.postSP.pruneButton.setEnabled(false);
/* 755:691 */           Revisions.extractZip(f.getPath(), WeaveUpdater.this.postSP.progress.progBar, WeaveUpdater.this.postSP.checkButton);
/* 756:    */         }
/* 757:    */         else
/* 758:    */         {
/* 759:693 */           JOptionPane.showMessageDialog(null, "Tomcat must be properly configured and running to deploy.", "Error", 0);
/* 760:    */         }
/* 761:    */       }
/* 762:696 */     });
/* 763:697 */     this.postSP.addActionToButton(this.postSP.deleteButton, new ActionListener()
/* 764:    */     {
/* 765:    */       public void actionPerformed(ActionEvent e)
/* 766:    */       {
/* 767:701 */         int n = WeaveUpdater.this.postSP.revisionTable.table.getSelectedRow();
/* 768:702 */         if (n < 0) {
/* 769:702 */           return;
/* 770:    */         }
/* 771:704 */         File f = (File)Revisions.getRevisionData().get(n);
/* 772:705 */         if (Settings.instance().CURRENT_INSTALL_VER.equals(Revisions.getRevisionName(f.getPath())))
/* 773:    */         {
/* 774:706 */           JOptionPane.showMessageDialog(null, "Cannot delete current installation.", "Error", 0);
/* 775:707 */           return;
/* 776:    */         }
/* 777:710 */         int val = JOptionPane.showConfirmDialog(null, "Deleting revisions cannot be undone.\n\nAre you sure you want to continue?", "Warning", 0);
/* 778:711 */         if (val == 1) {
/* 779:712 */           return;
/* 780:    */         }
/* 781:714 */         Revisions.recursiveDelete(f);
/* 782:715 */         SwingUtilities.invokeLater(new Runnable()
/* 783:    */         {
/* 784:    */           public void run()
/* 785:    */           {
/* 786:718 */             WeaveUpdater.this.postSP.checkButton.doClick();
/* 787:    */           }
/* 788:    */         });
/* 789:    */       }
/* 790:722 */     });
/* 791:723 */     this.postSP.addActionToButton(this.postSP.pruneButton, new ActionListener()
/* 792:    */     {
/* 793:    */       public void actionPerformed(ActionEvent e)
/* 794:    */       {
/* 795:727 */         double sizeMB = Revisions.getSizeOfRevisions() / 1024L / 1024L;
/* 796:728 */         int numRevs = Revisions.getNumberOfRevisions();
/* 797:730 */         if (numRevs >= Settings.instance().recommendPrune)
/* 798:    */         {
/* 799:732 */           int val = JOptionPane.showConfirmDialog(null, "Auto-cleaned revisions will be deleted\nand cannot be undone.\n\nAre you sure you want to continue?", "Warning", 0, 2);
/* 800:733 */           if (val == 1) {
/* 801:734 */             return;
/* 802:    */           }
/* 803:736 */           if (Revisions.pruneRevisions())
/* 804:    */           {
/* 805:738 */             double newSize = Revisions.getSizeOfRevisions() / 1024L / 1024L;
/* 806:739 */             int newNumRevs = Revisions.getNumberOfRevisions();
/* 807:    */             
/* 808:741 */             SwingUtilities.invokeLater(new Runnable()
/* 809:    */             {
/* 810:    */               public void run()
/* 811:    */               {
/* 812:744 */                 WeaveUpdater.this.postSP.checkButton.doClick();
/* 813:    */               }
/* 814:747 */             });
/* 815:748 */             JOptionPane.showMessageDialog(null, "Auto-clean completed successfully!\n\nDeleted: " + (
/* 816:749 */               numRevs - newNumRevs) + " files\n" + 
/* 817:750 */               "Freed Up: " + (sizeMB - newSize) + "MB", "Finished", 1);
/* 818:    */           }
/* 819:    */           else
/* 820:    */           {
/* 821:754 */             JOptionPane.showMessageDialog(null, "Sorry, the auto-clean feature encoutered\nan error and did not complete successfully.", 
/* 822:755 */               "Error", 0);
/* 823:    */           }
/* 824:    */         }
/* 825:    */         else
/* 826:    */         {
/* 827:758 */           JOptionPane.showMessageDialog(null, "You need at least " + Settings.instance().recommendPrune + " revisions for\n" + 
/* 828:759 */             "the auto-clean feature to work.\n\n" + 
/* 829:760 */             "Please delete revisions manually.", "Warning", 2);
/* 830:    */         }
/* 831:    */       }
/* 832:763 */     });
/* 833:764 */     this.postSP.addActionToButton(this.postSP.launchAdmin, new ActionListener()
/* 834:    */     {
/* 835:    */       public void actionPerformed(ActionEvent e)
/* 836:    */       {
/* 837:767 */         if (!Settings.instance().isServiceUp(Settings.instance().TOMCAT_PORT).booleanValue())
/* 838:    */         {
/* 839:769 */           int n = JOptionPane.showConfirmDialog(null, "Tomcat service is not running.\n\nWould you like to launch AdminConsole anyway?\n", "Error", 0);
/* 840:770 */           if (n == 1) {
/* 841:771 */             return;
/* 842:    */           }
/* 843:    */         }
/* 844:773 */         if (Desktop.isDesktopSupported()) {
/* 845:    */           try
/* 846:    */           {
/* 847:776 */             Desktop.getDesktop().browse(new URI("http://localhost:" + Settings.instance().TOMCAT_PORT + "/AdminConsole.html"));
/* 848:    */           }
/* 849:    */           catch (Exception e1)
/* 850:    */           {
/* 851:778 */             e1.printStackTrace();
/* 852:    */           }
/* 853:    */         } else {
/* 854:781 */           JOptionPane.showMessageDialog(null, "Feature not supported.", "Error", 0);
/* 855:    */         }
/* 856:    */       }
/* 857:783 */     });
/* 858:784 */     this.setupPanels.put("POST_SETUP", this.postSP);
/* 859:785 */     parent.add(this.postSP);
/* 860:786 */     switchToPostSetupPanel();
/* 861:    */   }
/* 862:    */   
/* 863:    */   public void removeButtonActions()
/* 864:    */   {
/* 865:791 */     for (ActionListener a : this.backButton.getActionListeners()) {
/* 866:792 */       this.backButton.removeActionListener(a);
/* 867:    */     }
/* 868:793 */     for (ActionListener a : this.nextButton.getActionListeners()) {
/* 869:794 */       this.nextButton.removeActionListener(a);
/* 870:    */     }
/* 871:    */   }
/* 872:    */ }


/* Location:           C:\Users\Andy\Desktop\WeaveUpdaterV1.2\Weave Installer.jar
 * Qualified Name:     weave.WeaveUpdater
 * JD-Core Version:    0.7.0.1
 */