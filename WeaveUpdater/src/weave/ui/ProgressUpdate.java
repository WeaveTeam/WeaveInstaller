/*   1:    */ package weave.ui;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.awt.Desktop;
/*   5:    */ import java.awt.event.ActionEvent;
/*   6:    */ import java.awt.event.ActionListener;
/*   7:    */ import java.io.File;
/*   8:    */ import java.io.FileNotFoundException;
/*   9:    */ import java.io.FileOutputStream;
/*  10:    */ import java.io.IOException;
/*  11:    */ import java.io.InputStream;
/*  12:    */ import java.io.PrintStream;
/*  13:    */ import java.net.MalformedURLException;
/*  14:    */ import java.net.URL;
/*  15:    */ import java.net.URLConnection;
/*  16:    */ import javax.swing.JButton;
/*  17:    */ import javax.swing.JLabel;
/*  18:    */ import javax.swing.JPanel;
/*  19:    */ import javax.swing.JProgressBar;
/*  20:    */ import weave.Revisions;
/*  21:    */ import weave.Settings;
/*  22:    */ import weave.Settings.MSI_TYPE;
/*  23:    */ import weave.WeaveUpdater;
/*  24:    */ 
/*  25:    */ public class ProgressUpdate
/*  26:    */   extends JPanel
/*  27:    */ {
/*  28:    */   public static final String ZIP_SPEED = "ZIP_SPEED";
/*  29:    */   public static final String ZIP_PERCENT = "ZIP_PERCENT";
/*  30:    */   public static final String ZIP_TIMELEFT = "ZIP_TIMELEFT";
/*  31:    */   public static final String ZIP_SIZEDOWNLOADED = "ZIP_SIZEDOWNLOADED";
/*  32:    */   public static final String ZIP_TOTALSIZE = "ZIP_TOTALSIZE";
/*  33:    */   public static final String MSI_SPEED = "MSI_SPEED";
/*  34:    */   public static final String MSI_PERCENT = "MSI_PERCENT";
/*  35:    */   public static final String MSI_TIMELEFT = "MSI_TIMELEFT";
/*  36:    */   public static final String MSI_SIZEDOWNLOADED = "MSI_SIZEDOWNLOADED";
/*  37:    */   public static final String MSI_TOTALSIZE = "MSI_TOTALSIZE";
/*  38:    */   public JProgressBar progBar;
/*  39:    */   public DownloadInfo zipInfo;
/*  40:    */   public DownloadInfo msiInfo;
/*  41:    */   
/*  42:    */   public ProgressUpdate()
/*  43:    */   {
/*  44: 67 */     setLayout(null);
/*  45: 68 */     setBackground(new Color(16777215));
/*  46:    */     
/*  47: 70 */     this.progBar = new JProgressBar(0, 100);
/*  48:    */     
/*  49: 72 */     add(this.progBar);
/*  50:    */   }
/*  51:    */   
/*  52:    */   public String setZipSpeed(String s)
/*  53:    */   {
/*  54: 77 */     firePropertyChange("ZIP_SPEED", this.zipInfo.strSpeed, s);
/*  55: 78 */     this.zipInfo.strSpeed = s;
/*  56: 79 */     return s;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public String setZipPercent(String s)
/*  60:    */   {
/*  61: 83 */     firePropertyChange("ZIP_PERCENT", this.zipInfo.strPercent, s);
/*  62: 84 */     this.zipInfo.strPercent = s;
/*  63: 85 */     return s;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public String setZipTimeleft(String s)
/*  67:    */   {
/*  68: 89 */     firePropertyChange("ZIP_TIMELEFT", this.zipInfo.strTimeleft, s);
/*  69: 90 */     this.zipInfo.strTimeleft = s;
/*  70: 91 */     return s;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public String setZipSizeDownloaded(String s)
/*  74:    */   {
/*  75: 95 */     firePropertyChange("ZIP_SIZEDOWNLOADED", this.zipInfo.strSizeDownloaded, s);
/*  76: 96 */     this.zipInfo.strSizeDownloaded = s;
/*  77: 97 */     return s;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public String setZipTotalSize(String s)
/*  81:    */   {
/*  82:101 */     firePropertyChange("ZIP_TOTALSIZE", this.zipInfo.strTotalSize, s);
/*  83:102 */     this.zipInfo.strTotalSize = s;
/*  84:103 */     return s;
/*  85:    */   }
/*  86:    */   
/*  87:    */   public String setMSISpeed(String s)
/*  88:    */   {
/*  89:107 */     firePropertyChange("MSI_SPEED", this.msiInfo.strSpeed, s);
/*  90:108 */     this.msiInfo.strSpeed = s;
/*  91:109 */     return s;
/*  92:    */   }
/*  93:    */   
/*  94:    */   public String setMSIPercent(String s)
/*  95:    */   {
/*  96:113 */     firePropertyChange("MSI_PERCENT", this.msiInfo.strPercent, s);
/*  97:114 */     this.msiInfo.strPercent = s;
/*  98:115 */     return s;
/*  99:    */   }
/* 100:    */   
/* 101:    */   public String setMSITimeleft(String s)
/* 102:    */   {
/* 103:119 */     firePropertyChange("MSI_TIMELEFT", this.msiInfo.strTimeleft, s);
/* 104:120 */     this.msiInfo.strTimeleft = s;
/* 105:121 */     return s;
/* 106:    */   }
/* 107:    */   
/* 108:    */   public String setMSISizeDownloaded(String s)
/* 109:    */   {
/* 110:125 */     firePropertyChange("MSI_SIZEDOWNLOADED", this.msiInfo.strSizeDownloaded, s);
/* 111:126 */     this.msiInfo.strSizeDownloaded = s;
/* 112:127 */     return s;
/* 113:    */   }
/* 114:    */   
/* 115:    */   public String setMSITotalSize(String s)
/* 116:    */   {
/* 117:131 */     firePropertyChange("MSI_TOTALSIZE", this.msiInfo.strTotalSize, s);
/* 118:132 */     this.msiInfo.strTotalSize = s;
/* 119:133 */     return s;
/* 120:    */   }
/* 121:    */   
/* 122:    */   public void setBounds(int x, int y, int width, int height)
/* 123:    */   {
/* 124:138 */     super.setBounds(x, y, width, height);
/* 125:    */     
/* 126:140 */     this.progBar.setBounds(0, 0, width, height);
/* 127:    */   }
/* 128:    */   
/* 129:    */   public void downloadZip(final JButton button)
/* 130:    */   {
/* 131:    */     try
/* 132:    */     {
/* 133:154 */       this.progBar.setStringPainted(true);
/* 134:155 */       Settings.instance().getClass();URL url = new URL("https://github.com/IVPR/Weave-Binaries/zipball/master");
/* 135:156 */       URLConnection conn = url.openConnection();
/* 136:157 */       final InputStream in = conn.getInputStream();
/* 137:158 */       this.zipInfo = new DownloadInfo();
/* 138:159 */       int updateAvailable = Revisions.checkForUpdates(false);
/* 139:160 */       String urlFileName = conn.getHeaderField("Content-Disposition");
/* 140:163 */       if (updateAvailable == -2)
/* 141:    */       {
/* 142:164 */         this.progBar.setValue(0);
/* 143:165 */         this.progBar.setIndeterminate(false);
/* 144:166 */         this.progBar.setString("No Internet Connection");
/* 145:    */         
/* 146:168 */         button.setEnabled(true);
/* 147:169 */         return;
/* 148:    */       }
/* 149:171 */       if (updateAvailable == -1)
/* 150:    */       {
/* 151:172 */         this.progBar.setValue(100);
/* 152:173 */         this.progBar.setIndeterminate(false);
/* 153:174 */         this.progBar.setString("Error Downloading");
/* 154:    */         
/* 155:176 */         button.setEnabled(true);
/* 156:177 */         return;
/* 157:    */       }
/* 158:179 */       if (updateAvailable == 0)
/* 159:    */       {
/* 160:180 */         this.progBar.setValue(100);
/* 161:181 */         this.progBar.setIndeterminate(false);
/* 162:182 */         this.progBar.setString("No Updates");
/* 163:    */         
/* 164:184 */         button.setEnabled(true);
/* 165:185 */         return;
/* 166:    */       }
/* 167:189 */       int pos = urlFileName.indexOf("filename=");
/* 168:190 */       final String updateFileName = Settings.instance().ZIP_DIRECTORY.getPath() + "/" + urlFileName.substring(pos + 9);
/* 169:191 */       File updateFile = new File(updateFileName);
/* 170:192 */       System.out.println(updateFileName);
/* 171:195 */       if (!Settings.instance().SETTINGS_DIRECTORY.exists()) {
/* 172:195 */         Settings.instance().SETTINGS_DIRECTORY.mkdirs();
/* 173:    */       }
/* 174:196 */       if (!Settings.instance().ZIP_DIRECTORY.exists()) {
/* 175:196 */         Settings.instance().ZIP_DIRECTORY.mkdirs();
/* 176:    */       }
/* 177:198 */       updateFile.createNewFile();
/* 178:    */       
/* 179:    */ 
/* 180:201 */       Thread t = new Thread(new Runnable()
/* 181:    */       {
/* 182:    */         int size;
/* 183:    */         FileOutputStream out;
/* 184:    */         byte[] b;
/* 185:    */         int count;
/* 186:    */         int total;
/* 187:    */         int kbps;
/* 188:    */         int seconds;
/* 189:    */         int aveDownSpeed;
/* 190:    */         int timeleft;
/* 191:    */         long newLong;
/* 192:    */         long oldLong;
/* 193:    */         
/* 194:    */         public void run()
/* 195:    */         {
/* 196:    */           try
/* 197:    */           {
/* 198:213 */             WeaveUpdater.updater.cancelButton.setEnabled(false);
/* 199:214 */             WeaveUpdater.updater.postSP.zipLabelSpeed.setVisible(true);
/* 200:215 */             WeaveUpdater.updater.postSP.zipLabelTimeleft.setVisible(true);
/* 201:    */             
/* 202:217 */             String strSize = "";
/* 203:218 */             if (this.size > 1024)
/* 204:    */             {
/* 205:220 */               if (this.size / 1024 > 1024) {
/* 206:221 */                 strSize = String.format("%.2f MB", new Object[] { Double.valueOf(((this.size + 0.0D) / 1024.0D + 0.0D) / 1024.0D) });
/* 207:    */               } else {
/* 208:223 */                 strSize = String.format("%.0f KB", new Object[] { Double.valueOf((this.size + 0.0D) / 1024.0D) });
/* 209:    */               }
/* 210:    */             }
/* 211:    */             else {
/* 212:225 */               strSize = String.format("%.0f B", new Object[] { Double.valueOf((this.size + 0.0D) / 1.0D) });
/* 213:    */             }
/* 214:227 */             ProgressUpdate.this.setZipTotalSize(strSize);
/* 215:    */             
/* 216:229 */             ProgressUpdate.this.progBar.setIndeterminate(true);
/* 217:230 */             ProgressUpdate.this.progBar.setString("Preparing Download...");
/* 218:231 */             Thread.sleep(2000L);
/* 219:232 */             ProgressUpdate.this.progBar.setIndeterminate(false);
/* 220:234 */             while ((this.count = in.read(this.b)) > 0)
/* 221:    */             {
/* 222:236 */               this.out.write(this.b, 0, this.count);
/* 223:237 */               this.total += this.count;
/* 224:238 */               this.kbps += this.count / 1024;
/* 225:239 */               ProgressUpdate.this.zipInfo.percent = (this.total / (this.size + 0.0D) * 100.0D);
/* 226:240 */               this.newLong = System.currentTimeMillis();
/* 227:241 */               if (this.newLong - this.oldLong > 1000L)
/* 228:    */               {
/* 229:243 */                 ProgressUpdate.this.zipInfo.speed = this.kbps;
/* 230:244 */                 this.kbps = 0;
/* 231:245 */                 this.seconds += 1;
/* 232:246 */                 this.oldLong = this.newLong;
/* 233:247 */                 this.aveDownSpeed = (this.total / 1024 / this.seconds);
/* 234:249 */                 if (this.total > 1024)
/* 235:    */                 {
/* 236:250 */                   if (this.total / 1024 > 1024) {
/* 237:251 */                     ProgressUpdate.this.setZipSizeDownloaded(String.format("%.2f MB", new Object[] { Double.valueOf((this.total + 0.0D) / 1024.0D / 1024.0D) }));
/* 238:    */                   } else {
/* 239:253 */                     ProgressUpdate.this.setZipSizeDownloaded(String.format("%.0f KB", new Object[] { Double.valueOf((this.total + 0.0D) / 1024.0D) }));
/* 240:    */                   }
/* 241:    */                 }
/* 242:    */                 else {
/* 243:255 */                   ProgressUpdate.this.setZipSizeDownloaded(String.format("%.0f B", new Object[] { Integer.valueOf(this.total) }));
/* 244:    */                 }
/* 245:    */               }
/* 246:257 */               this.timeleft = ((this.size - this.total) / this.aveDownSpeed / 1024);
/* 247:258 */               ProgressUpdate.this.progBar.setValue(Integer.parseInt(String.format("%.0f", new Object[] { Double.valueOf(ProgressUpdate.this.zipInfo.percent) })));
/* 248:259 */               ProgressUpdate.this.progBar.setString(ProgressUpdate.this.setZipPercent(String.format("%.0f", new Object[] { Double.valueOf(ProgressUpdate.this.zipInfo.percent) })) + "%");
/* 249:261 */               if (ProgressUpdate.this.zipInfo.speed > 1024) {
/* 250:262 */                 ProgressUpdate.this.setZipSpeed(String.format("%.1f", new Object[] { Double.valueOf(ProgressUpdate.this.zipInfo.speed / 1024.0D) }) + " MB/s");
/* 251:    */               } else {
/* 252:264 */                 ProgressUpdate.this.setZipSpeed(String.format("%d", new Object[] { Integer.valueOf(ProgressUpdate.this.zipInfo.speed) }) + " KB/s");
/* 253:    */               }
/* 254:266 */               if (this.timeleft > 60)
/* 255:    */               {
/* 256:267 */                 int t = this.timeleft / 60;
/* 257:268 */                 if (t == 1) {
/* 258:269 */                   ProgressUpdate.this.setZipTimeleft(String.format("%d minute remaining", new Object[] { Integer.valueOf(t) }));
/* 259:    */                 } else {
/* 260:271 */                   ProgressUpdate.this.setZipTimeleft(String.format("%d minutes remaining", new Object[] { Integer.valueOf(t) }));
/* 261:    */                 }
/* 262:    */               }
/* 263:    */               else
/* 264:    */               {
/* 265:274 */                 ProgressUpdate.this.setZipTimeleft(String.format("%d second(s) remaining", new Object[] { Integer.valueOf(this.timeleft) }));
/* 266:    */               }
/* 267:    */             }
/* 268:277 */             this.out.flush();
/* 269:278 */             ProgressUpdate.this.progBar.setValue(100);
/* 270:279 */             ProgressUpdate.this.progBar.setIndeterminate(true);
/* 271:280 */             ProgressUpdate.this.progBar.setString("Download Finished");
/* 272:281 */             Thread.sleep(3000L);
/* 273:282 */             this.out.close();
/* 274:283 */             in.close();
/* 275:284 */             WeaveUpdater.updater.postSP.zipLabelSpeedHolder.setText("");
/* 276:285 */             WeaveUpdater.updater.postSP.zipLabelTimeleftHolder.setText("");
/* 277:286 */             WeaveUpdater.updater.postSP.zipLabelSizeDownloadHolder.setText("");
/* 278:287 */             WeaveUpdater.updater.postSP.zipLabelSpeed.setVisible(false);
/* 279:288 */             WeaveUpdater.updater.postSP.zipLabelTimeleft.setVisible(false);
/* 280:    */             
/* 281:290 */             Revisions.extractZip(updateFileName, ProgressUpdate.this.progBar, button);
/* 282:    */           }
/* 283:    */           catch (Exception e)
/* 284:    */           {
/* 285:292 */             e.printStackTrace();
/* 286:    */           }
/* 287:    */         }
/* 288:295 */       });
/* 289:296 */       t.start();
/* 290:    */     }
/* 291:    */     catch (MalformedURLException e)
/* 292:    */     {
/* 293:298 */       e.printStackTrace();
/* 294:    */     }
/* 295:    */     catch (IOException ex)
/* 296:    */     {
/* 297:300 */       ex.printStackTrace();
/* 298:    */     }
/* 299:    */   }
/* 300:    */   
/* 301:    */   public int downloadMSI(final JPanel currentPanel, final JButton button, final Settings.MSI_TYPE downloadMSI)
/* 302:    */   {
/* 303:314 */     final JButton cancelInstall = new JButton("Cancel");
/* 304:315 */     cancelInstall.setBounds(5, 115, 100, 20);
/* 305:316 */     cancelInstall.setVisible(false);
/* 306:317 */     cancelInstall.setEnabled(false);
/* 307:318 */     currentPanel.add(cancelInstall);
/* 308:    */     try
/* 309:    */     {
/* 310:322 */       this.progBar.setStringPainted(true);
/* 311:325 */       if (!Settings.instance().isConnectedToInternet().booleanValue())
/* 312:    */       {
/* 313:326 */         this.progBar.setValue(0);
/* 314:327 */         this.progBar.setIndeterminate(false);
/* 315:328 */         this.progBar.setString("No Internet Connection");
/* 316:329 */         button.setEnabled(true);
/* 317:330 */         WeaveUpdater.updater.nextButton.setEnabled(true);
/* 318:331 */         WeaveUpdater.updater.backButton.setEnabled(true);
/* 319:332 */         WeaveUpdater.updater.cancelButton.setEnabled(true);
/* 320:333 */         if (downloadMSI == Settings.MSI_TYPE.TOMCAT_MSI)
/* 321:    */         {
/* 322:334 */           WeaveUpdater.updater.curSP.installTomcat.setVisible(true);
/* 323:335 */           if (Settings.instance().TOMCAT_FILE.exists()) {
/* 324:336 */             WeaveUpdater.updater.curSP.installTomcat.setEnabled(true);
/* 325:    */           }
/* 326:    */         }
/* 327:337 */         else if (downloadMSI == Settings.MSI_TYPE.MySQL_MSI)
/* 328:    */         {
/* 329:338 */           if (Settings.instance().MySQL_FILE.exists()) {
/* 330:339 */             WeaveUpdater.updater.curSP.installMySQL.setEnabled(true);
/* 331:    */           }
/* 332:340 */           WeaveUpdater.updater.curSP.installMySQL.setVisible(true);
/* 333:    */         }
/* 334:343 */         currentPanel.remove(cancelInstall);
/* 335:344 */         return -1;
/* 336:    */       }
/* 337:    */       URL url;
/* 338:349 */       if (downloadMSI == Settings.MSI_TYPE.TOMCAT_MSI)
/* 339:    */       {
/* 340:350 */         while (Settings.instance().BEST_TOMCAT == null) {}
/* 341:351 */         url = new URL(Settings.instance().BEST_TOMCAT);
/* 342:    */       }
/* 343:    */       else
/* 344:    */       {
/* 346:352 */         if (downloadMSI == Settings.MSI_TYPE.MySQL_MSI)
/* 347:    */         {
/* 348:353 */           while (Settings.instance().BEST_MYSQL == null) {}
/* 349:354 */           url = new URL(Settings.instance().BEST_MYSQL);
/* 350:    */         }
/* 351:    */         else
/* 352:    */         {
/* 353:356 */           return -1;
/* 354:    */         }
/* 355:    */       }
/* 357:359 */       URLConnection conn = url.openConnection();
/* 358:360 */       final InputStream in = conn.getInputStream();
/* 359:361 */       this.msiInfo = new DownloadInfo();
/* 360:    */       String updateFileName;
/* 361:365 */       if (downloadMSI == Settings.MSI_TYPE.TOMCAT_MSI)
/* 362:    */       {
/* 363:366 */         updateFileName = Settings.instance().TOMCAT_FILE.getPath();
/* 364:    */       }
/* 365:    */       else
/* 366:    */       {
/* 368:367 */         if (downloadMSI == Settings.MSI_TYPE.MySQL_MSI) {
/* 369:368 */           updateFileName = Settings.instance().MySQL_FILE.getPath();
/* 370:    */         } else {
/* 371:370 */           return -1;
/* 372:    */         }
/* 373:    */       }
/* 375:373 */       final File updateFile = new File(updateFileName);
/* 376:374 */       System.out.println(updateFileName);
/* 377:377 */       if (!Settings.instance().SETTINGS_DIRECTORY.exists()) {
/* 378:377 */         Settings.instance().SETTINGS_DIRECTORY.mkdirs();
/* 379:    */       }
/* 380:378 */       if (!Settings.instance().EXE_DIRECTORY.exists()) {
/* 381:378 */         Settings.instance().EXE_DIRECTORY.mkdirs();
/* 382:    */       }
/* 383:380 */       cancelInstall.addActionListener(new ActionListener()
/* 384:    */       {
/* 385:    */         public void actionPerformed(ActionEvent arg0)
/* 386:    */         {
/* 387:383 */           cancelInstall.setEnabled(false);
/* 388:384 */           cancelInstall.setVisible(false);
/* 389:    */           try
/* 390:    */           {
/* 391:387 */             ProgressUpdate.this.msiInfo.cancelFlag = 1;
/* 392:    */           }
/* 393:    */           catch (Exception e)
/* 394:    */           {
/* 395:389 */             e.printStackTrace();
/* 396:    */           }
/* 397:    */         }
/* 398:393 */       });
/* 399:394 */       updateFile.createNewFile();
/* 400:    */       
/* 401:    */ 
/* 402:397 */       Thread t = new Thread(new Runnable()
/* 403:    */       {
/* 404:    */         int size;
/* 405:    */         FileOutputStream out;
/* 406:    */         byte[] b;
/* 407:    */         int count;
/* 408:    */         int total;
/* 409:    */         int kbps;
/* 410:    */         int seconds;
/* 411:    */         int aveDownSpeed;
/* 412:    */         int timeleft;
/* 413:    */         long newLong;
/* 414:    */         long oldLong;
/* 415:    */         
/* 416:    */         public void run()
/* 417:    */         {
/* 418:    */           try
/* 419:    */           {
/* 420:409 */             ProgressUpdate.this.progBar.setIndeterminate(true);
/* 421:410 */             ProgressUpdate.this.progBar.setString("Preparing Download...");
/* 422:411 */             Thread.sleep(2000L);
/* 423:412 */             ProgressUpdate.this.progBar.setIndeterminate(false);
/* 424:413 */             cancelInstall.setVisible(true);
/* 425:414 */             cancelInstall.setEnabled(true);
/* 426:415 */             ProgressUpdate.this.msiInfo.cancelFlag = 0;
/* 427:    */             
/* 428:417 */             String strSize = "";
/* 429:418 */             if (this.size > 1024)
/* 430:    */             {
/* 431:420 */               if (this.size / 1024 > 1024) {
/* 432:421 */                 strSize = String.format("%.2f MB", new Object[] { Double.valueOf(((this.size + 0.0D) / 1024.0D + 0.0D) / 1024.0D) });
/* 433:    */               } else {
/* 434:423 */                 strSize = String.format("%.0f KB", new Object[] { Double.valueOf((this.size + 0.0D) / 1024.0D) });
/* 435:    */               }
/* 436:    */             }
/* 437:    */             else {
/* 438:425 */               strSize = String.format("%.0f B", new Object[] { Integer.valueOf(this.size) });
/* 439:    */             }
/* 440:427 */             ProgressUpdate.this.setMSITotalSize(strSize);
/* 441:429 */             while ((this.count = in.read(this.b)) > 0)
/* 442:    */             {
/* 443:431 */               if (ProgressUpdate.this.msiInfo.cancelFlag == 1) {
/* 444:    */                 break;
/* 445:    */               }
/* 446:434 */               this.out.write(this.b, 0, this.count);
/* 447:435 */               this.total += this.count;
/* 448:436 */               this.kbps += this.count / 1024;
/* 449:437 */               ProgressUpdate.this.msiInfo.percent = (this.total / (this.size + 0.0D) * 100.0D);
/* 450:438 */               this.newLong = System.currentTimeMillis();
/* 451:439 */               if (this.newLong - this.oldLong > 1000L)
/* 452:    */               {
/* 453:441 */                 ProgressUpdate.this.msiInfo.speed = this.kbps;
/* 454:    */                 
/* 455:443 */                 this.kbps = 0;
/* 456:444 */                 this.seconds += 1;
/* 457:445 */                 this.oldLong = this.newLong;
/* 458:446 */                 this.aveDownSpeed = (this.total / 1024 / this.seconds);
/* 459:448 */                 if (this.total > 1024)
/* 460:    */                 {
/* 461:449 */                   if (this.total / 1024 > 1024) {
/* 462:450 */                     ProgressUpdate.this.setMSISizeDownloaded(String.format("%.2f MB", new Object[] { Double.valueOf((this.total + 0.0D) / 1024.0D / 1024.0D) }));
/* 463:    */                   } else {
/* 464:452 */                     ProgressUpdate.this.setMSISizeDownloaded(String.format("%.0f KB", new Object[] { Double.valueOf((this.total + 0.0D) / 1024.0D) }));
/* 465:    */                   }
/* 466:    */                 }
/* 467:    */                 else {
/* 468:454 */                   ProgressUpdate.this.setMSISizeDownloaded(String.format("%.0f B", new Object[] { Integer.valueOf(this.total) }));
/* 469:    */                 }
/* 470:    */               }
/* 471:456 */               this.timeleft = ((this.size - this.total) / this.aveDownSpeed / 1024);
/* 472:457 */               ProgressUpdate.this.progBar.setValue(Integer.parseInt(String.format("%.0f", new Object[] { Double.valueOf(ProgressUpdate.this.msiInfo.percent) })));
/* 473:458 */               ProgressUpdate.this.progBar.setString(ProgressUpdate.this.setMSIPercent(String.format("%.0f", new Object[] { Double.valueOf(ProgressUpdate.this.msiInfo.percent) })) + "%");
/* 474:460 */               if (ProgressUpdate.this.msiInfo.speed > 1024) {
/* 475:461 */                 ProgressUpdate.this.setMSISpeed(String.format("%.1f", new Object[] { Double.valueOf(ProgressUpdate.this.msiInfo.speed / 1024.0D) }) + " MB/s");
/* 476:    */               } else {
/* 477:463 */                 ProgressUpdate.this.setMSISpeed(String.format("%d", new Object[] { Integer.valueOf(ProgressUpdate.this.msiInfo.speed) }) + " KB/s");
/* 478:    */               }
/* 479:465 */               if (this.timeleft > 60)
/* 480:    */               {
/* 481:466 */                 int t = this.timeleft / 60;
/* 482:467 */                 if (t == 1) {
/* 483:468 */                   ProgressUpdate.this.setMSITimeleft(String.format("%d minute", new Object[] { Integer.valueOf(t) }));
/* 484:    */                 } else {
/* 485:470 */                   ProgressUpdate.this.setMSITimeleft(String.format("%d minutes", new Object[] { Integer.valueOf(t) }));
/* 486:    */                 }
/* 487:    */               }
/* 488:    */               else
/* 489:    */               {
/* 490:473 */                 ProgressUpdate.this.setMSITimeleft(String.format("%d second(s)", new Object[] { Integer.valueOf(this.timeleft) }));
/* 491:    */               }
/* 492:    */             }
/* 493:476 */             if (ProgressUpdate.this.msiInfo.cancelFlag == 0)
/* 494:    */             {
/* 495:478 */               this.out.flush();
/* 496:479 */               ProgressUpdate.this.progBar.setValue(100);
/* 497:480 */               ProgressUpdate.this.progBar.setIndeterminate(true);
/* 498:481 */               cancelInstall.setEnabled(false);
/* 499:482 */               cancelInstall.setVisible(false);
/* 500:483 */               Thread.sleep(1500L);
/* 501:484 */               this.out.close();
/* 502:485 */               in.close();
/* 503:486 */               ProgressUpdate.this.progBar.setString("Download Finished");
/* 504:487 */               ProgressUpdate.this.progBar.setValue(100);
/* 505:488 */               ProgressUpdate.this.progBar.setIndeterminate(false);
/* 506:489 */               if (downloadMSI == Settings.MSI_TYPE.TOMCAT_MSI)
/* 507:    */               {
/* 508:490 */                 WeaveUpdater.updater.curSP.installTomcat.setEnabled(true);
/* 509:491 */                 WeaveUpdater.updater.curSP.installTomcat.setVisible(true);
/* 510:    */               }
/* 511:492 */               else if (downloadMSI == Settings.MSI_TYPE.MySQL_MSI)
/* 512:    */               {
/* 513:493 */                 WeaveUpdater.updater.curSP.installMySQL.setEnabled(true);
/* 514:494 */                 WeaveUpdater.updater.curSP.installMySQL.setVisible(true);
/* 515:    */               }
/* 516:    */             }
/* 517:    */             else
/* 518:    */             {
/* 519:498 */               this.out.flush();
/* 520:499 */               ProgressUpdate.this.progBar.setValue(0);
/* 521:500 */               ProgressUpdate.this.progBar.setIndeterminate(true);
/* 522:501 */               ProgressUpdate.this.progBar.setString("Removing Local Files...");
/* 523:502 */               Thread.sleep(1500L);
/* 524:503 */               this.out.close();
/* 525:504 */               in.close();
/* 526:505 */               Revisions.recursiveDelete(updateFile);
/* 527:506 */               Thread.sleep(1000L);
/* 528:507 */               ProgressUpdate.this.progBar.setIndeterminate(false);
/* 529:508 */               ProgressUpdate.this.progBar.setString("Download Cancelled");
/* 530:510 */               if (downloadMSI == Settings.MSI_TYPE.TOMCAT_MSI)
/* 531:    */               {
/* 532:511 */                 WeaveUpdater.updater.curSP.installTomcat.setEnabled(false);
/* 533:512 */                 WeaveUpdater.updater.curSP.installTomcat.setVisible(true);
/* 534:    */               }
/* 535:513 */               else if (downloadMSI == Settings.MSI_TYPE.MySQL_MSI)
/* 536:    */               {
/* 537:514 */                 WeaveUpdater.updater.curSP.installMySQL.setEnabled(false);
/* 538:515 */                 WeaveUpdater.updater.curSP.installMySQL.setVisible(true);
/* 539:    */               }
/* 540:    */             }
/* 541:519 */             if (downloadMSI == Settings.MSI_TYPE.TOMCAT_MSI)
/* 542:    */             {
/* 543:520 */               WeaveUpdater.updater.curSP.tomcatLabelSizeDownloadHolder.setText("");
/* 544:521 */               WeaveUpdater.updater.curSP.tomcatLabelSpeedHolder.setText("");
/* 545:522 */               WeaveUpdater.updater.curSP.tomcatLabelTimeleftHolder.setText("");
/* 546:    */             }
/* 547:523 */             else if (downloadMSI == Settings.MSI_TYPE.MySQL_MSI)
/* 548:    */             {
/* 549:524 */               WeaveUpdater.updater.curSP.mysqlLabelSizeDownloadHolder.setText("");
/* 550:525 */               WeaveUpdater.updater.curSP.mysqlLabelSpeedHolder.setText("");
/* 551:526 */               WeaveUpdater.updater.curSP.mysqlLabelTimeleftHolder.setText("");
/* 552:    */             }
/* 553:529 */             button.setEnabled(true);
/* 554:530 */             WeaveUpdater.updater.nextButton.setEnabled(true);
/* 555:531 */             WeaveUpdater.updater.backButton.setEnabled(true);
/* 556:532 */             WeaveUpdater.updater.cancelButton.setEnabled(true);
/* 557:533 */             currentPanel.remove(cancelInstall);
/* 558:    */           }
/* 559:    */           catch (Exception e)
/* 560:    */           {
/* 561:535 */             e.printStackTrace();
/* 562:    */           }
/* 563:    */         }
/* 564:538 */       });
/* 565:539 */       t.start();
/* 566:    */     }
/* 567:    */     catch (MalformedURLException e)
/* 568:    */     {
/* 569:541 */       e.printStackTrace();
/* 570:    */     }
/* 571:    */     catch (IOException ex)
/* 572:    */     {
/* 573:543 */       ex.printStackTrace();
/* 574:    */     }
/* 575:545 */     return 0;
/* 576:    */   }
/* 577:    */   
/* 578:    */   public int runExecutable(File file)
/* 579:    */   {
/* 580:550 */     if (file.exists())
/* 581:    */     {
/* 582:    */       try
/* 583:    */       {
/* 584:554 */         Desktop.getDesktop().open(file);
/* 585:    */       }
/* 586:    */       catch (Exception e)
/* 587:    */       {
/* 588:556 */         e.printStackTrace();
/* 589:    */       }
/* 590:558 */       return 0;
/* 591:    */     }
/* 592:560 */     return -1;
/* 593:    */   }
/* 594:    */   
/* 595:    */   public static class DownloadInfo
/* 596:    */   {
/* 597:565 */     int speed = 0;
/* 598:566 */     double percent = 0.0D;
/* 599:568 */     String strSpeed = "";
/* 600:569 */     String strPercent = "";
/* 601:570 */     String strTimeleft = "";
/* 602:571 */     String strSizeDownloaded = "";
/* 603:572 */     String strTotalSize = "";
/* 604:574 */     int cancelFlag = 0;
/* 605:    */   }
/* 606:    */ }


/* Location:           C:\Users\Andy\Desktop\WeaveUpdaterV1.2\Weave Installer.jar
 * Qualified Name:     weave.ui.ProgressUpdate
 * JD-Core Version:    0.7.0.1
 */