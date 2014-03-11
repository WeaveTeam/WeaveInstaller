/*   1:    */ package weave;
/*   2:    */ 
/*   3:    */ import java.awt.Desktop;
/*   4:    */ import java.io.BufferedInputStream;
/*   5:    */ import java.io.BufferedReader;
/*   6:    */ import java.io.DataInputStream;
/*   7:    */ import java.io.File;
/*   8:    */ import java.io.FileInputStream;
/*   9:    */ import java.io.FileNotFoundException;
/*  10:    */ import java.io.FileOutputStream;
/*  11:    */ import java.io.IOException;
/*  12:    */ import java.io.InputStream;
/*  13:    */ import java.io.InputStreamReader;
/*  14:    */ import java.io.ObjectInputStream;
/*  15:    */ import java.io.ObjectOutputStream;
/*  16:    */ import java.io.PrintStream;
/*  17:    */ import java.io.StringWriter;
/*  18:    */ import java.net.HttpURLConnection;
/*  19:    */ import java.net.Socket;
/*  20:    */ import java.net.URI;
/*  21:    */ import java.net.URL;
/*  22:    */ import java.net.URLConnection;
/*  23:    */ import java.util.HashMap;
/*  24:    */ import java.util.Map;
/*  25:    */ import java.util.Timer;
/*  26:    */ import java.util.TimerTask;
/*  27:    */ import javax.swing.JOptionPane;
/*  28:    */ 
/*  29:    */ public class Settings
/*  30:    */ {
/*  31: 50 */   public final String UPDATE_URL = "https://github.com/IVPR/Weave-Binaries/zipball/master";
/*  32: 51 */   public final String UPDATE_CONFIG = "http://oicweave.org/WeaveUpdater/config.txt";
/*  33: 52 */   public final String INSTALLER_URL = "http://info.oicweave.org/projects/weave/wiki/Installer";
/*  34: 53 */   public final String UPDATER_VER = "1.2";
/*  35: 54 */   public final String TITLE = "Weave Installer v".concat("1.2");
/*  36: 55 */   public File SETTINGS_DIRECTORY = null;
/*  37: 56 */   public File EXE_DIRECTORY = null;
/*  38: 57 */   public File ZIP_DIRECTORY = null;
/*  39: 58 */   public File UNZIP_DIRECTORY = null;
/*  40: 59 */   public File SETTINGS_FILE = null;
/*  41: 60 */   public File TOMCAT_FILE = null;
/*  42: 61 */   public File MySQL_FILE = null;
/*  43:    */   
/*  44:    */   public static enum OS_TYPE
/*  45:    */   {
/*  46: 63 */     WINDOWS,  LINUX,  MAC,  UNKNOWN;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public static enum SERVICE_PORTS
/*  50:    */   {
/*  51: 64 */     MySQL,  TOMCAT;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public static enum MSI_TYPE
/*  55:    */   {
/*  56: 65 */     TOMCAT_MSI,  MySQL_MSI;
/*  57:    */   }
/*  58:    */   
/*  59: 66 */   public String LAST_UPDATE_CHECK = "Never";
/*  60: 67 */   public String TOMCAT_DIR = "";
/*  61: 68 */   public String CURRENT_INSTALL_VER = "";
/*  62: 69 */   public String BEST_TOMCAT = null;
/*  63: 70 */   public String BEST_MYSQL = null;
/*  64: 71 */   public OS_TYPE OS = null;
/*  65: 73 */   public int MySQL_PORT = 3306;
/*  66: 74 */   public int TOMCAT_PORT = 8080;
/*  67: 75 */   public int runningProcesses = 0;
/*  68: 76 */   public int recommendPrune = 6;
/*  69: 78 */   private Map<String, Object> settings = null;
/*  70: 80 */   private static Settings _instance = null;
/*  71:    */   
/*  72:    */   public static Settings instance()
/*  73:    */   {
/*  74: 88 */     if (_instance == null) {
/*  75: 89 */       _instance = new Settings();
/*  76:    */     }
/*  77: 90 */     return _instance;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public Settings()
/*  81:    */   {
/*  82:100 */     findOS();
/*  83:101 */     createFS();
/*  84:102 */     if (!readSettings().booleanValue()) {
/*  85:103 */       findTomcatDir();
/*  86:    */     }
/*  87:107 */     new Timer().schedule(new TimerTask()
/*  88:    */     {
/*  89:    */       public void run()
/*  90:    */       {
/*  91:110 */         if (Settings.this.isConnectedToInternet().booleanValue()) {
/*  92:111 */           Settings.this.checkForUpdate();
/*  93:    */         }
/*  94:    */       }
/*  95:113 */     }, 1000L);
/*  96:    */     
/*  97:    */ 
/*  98:116 */     new Timer().schedule(new TimerTask()
/*  99:    */     {
/* 100:    */       public void run()
/* 101:    */       {
/* 102:119 */         if (Settings.this.isConnectedToInternet().booleanValue()) {
/* 103:120 */           Settings.this.checkForUpdate();
/* 104:    */         }
/* 105:    */       }
/* 106:122 */     }, 86400000L, 86400000L);
/* 107:    */   }
/* 108:    */   
/* 109:    */   public void checkForUpdate()
/* 110:    */   {
/* 111:127 */     String latestVersion = getLatestWeaveUpdaterVersion();
/* 112:129 */     if (!latestVersion.equals("1.2"))
/* 113:    */     {
/* 114:131 */       int n = JOptionPane.showConfirmDialog(null, "There is a newer version of this tool available for download. \n\nWould you like to download it now?", "Update Available", 0, 1);
/* 115:132 */       if (n == 0) {
/* 116:134 */         if (Desktop.isDesktopSupported()) {
/* 117:    */           try
/* 118:    */           {
/* 119:137 */             Desktop.getDesktop().browse(new URI(getLatestWeaveUpdaterURL()));
/* 120:    */           }
/* 121:    */           catch (Exception e)
/* 122:    */           {
/* 123:139 */             e.printStackTrace();
/* 124:    */           }
/* 125:    */         } else {
/* 126:142 */           JOptionPane.showMessageDialog(null, "Sorry, this feature is not supported by your version of Java.", "Error", 0);
/* 127:    */         }
/* 128:    */       }
/* 129:    */     }
/* 130:    */   }
/* 131:    */   
/* 132:    */   public Boolean settingsExists()
/* 133:    */   {
/* 134:153 */     return Boolean.valueOf(this.SETTINGS_FILE.exists());
/* 135:    */   }
/* 136:    */   
/* 137:    */   public Boolean writeSettings()
/* 138:    */   {
/* 139:    */     try
/* 140:    */     {
/* 141:164 */       if (!this.SETTINGS_DIRECTORY.exists())
/* 142:    */       {
/* 143:166 */         this.SETTINGS_DIRECTORY.mkdirs();
/* 144:167 */         if (!this.SETTINGS_FILE.exists()) {
/* 145:168 */           this.SETTINGS_FILE.createNewFile();
/* 146:    */         }
/* 147:    */       }
/* 148:171 */       this.settings = new HashMap();
/* 149:172 */       this.settings.put("OS", this.OS);
/* 150:173 */       this.settings.put("TOMCAT_DIR", this.TOMCAT_DIR);
/* 151:174 */       this.settings.put("MySQL_PORT", Integer.valueOf(this.MySQL_PORT));
/* 152:175 */       this.settings.put("TOMCAT_PORT", Integer.valueOf(this.TOMCAT_PORT));
/* 153:176 */       this.settings.put("LAST_UPDATE_CHECK", this.LAST_UPDATE_CHECK);
/* 154:177 */       this.settings.put("CURRENT_INSTALL_VER", this.CURRENT_INSTALL_VER);
/* 155:    */       
/* 156:179 */       FileOutputStream fout = new FileOutputStream(this.SETTINGS_FILE);
/* 157:180 */       ObjectOutputStream outstream = new ObjectOutputStream(fout);
/* 158:181 */       outstream.writeObject(this.settings);
/* 159:182 */       outstream.close();
/* 160:    */     }
/* 161:    */     catch (IOException e)
/* 162:    */     {
/* 163:184 */       e.printStackTrace();
/* 164:185 */       return Boolean.valueOf(false);
/* 165:    */     }
/* 166:187 */     return Boolean.valueOf(true);
/* 167:    */   }
/* 168:    */   
/* 169:    */   public Boolean readSettings()
/* 170:    */   {
/* 171:    */     try
/* 172:    */     {
/* 173:199 */       Thread.sleep(1000L);
/* 174:    */     }
/* 175:    */     catch (InterruptedException e1)
/* 176:    */     {
/* 177:199 */       e1.printStackTrace();
/* 178:    */     }
/* 179:201 */     if (!this.SETTINGS_FILE.exists()) {
/* 180:201 */       return Boolean.valueOf(false);
/* 181:    */     }
/* 182:    */     try
/* 183:    */     {
/* 184:207 */       FileInputStream fin = new FileInputStream(this.SETTINGS_FILE);
/* 185:208 */       ObjectInputStream instream = new ObjectInputStream(fin);
/* 186:209 */       this.settings = ((Map)instream.readObject());
/* 187:210 */       instream.close();
/* 188:    */       
/* 189:    */ 
/* 190:213 */       this.OS = ((OS_TYPE)this.settings.get("OS"));
/* 191:214 */       this.TOMCAT_DIR = ((String)this.settings.get("TOMCAT_DIR"));
/* 192:215 */       this.MySQL_PORT = ((Integer)this.settings.get("MySQL_PORT")).intValue();
/* 193:216 */       this.TOMCAT_PORT = ((Integer)this.settings.get("TOMCAT_PORT")).intValue();
/* 194:217 */       this.LAST_UPDATE_CHECK = ((String)this.settings.get("LAST_UPDATE_CHECK"));
/* 195:218 */       this.CURRENT_INSTALL_VER = ((String)this.settings.get("CURRENT_INSTALL_VER"));
/* 196:    */       
/* 197:220 */       System.out.println("Settings successfully read!");
/* 198:221 */       System.out.println("OS: " + this.OS);
/* 199:222 */       System.out.println("TOMCAT_DIR: " + this.TOMCAT_DIR);
/* 200:223 */       System.out.println("MySQL_PORT: " + this.MySQL_PORT);
/* 201:224 */       System.out.println("TOMCAT_PORT: " + this.TOMCAT_PORT);
/* 202:225 */       System.out.println("LAST_UPDATE_CHECK: " + this.LAST_UPDATE_CHECK);
/* 203:226 */       System.out.println("CURRENT_INSTALL_VER: " + this.CURRENT_INSTALL_VER);
/* 204:    */     }
/* 205:    */     catch (FileNotFoundException e)
/* 206:    */     {
/* 207:228 */       e.printStackTrace();
/* 208:229 */       return Boolean.valueOf(false);
/* 209:    */     }
/* 210:    */     catch (IOException e)
/* 211:    */     {
/* 212:231 */       e.printStackTrace();
/* 213:232 */       return Boolean.valueOf(false);
/* 214:    */     }
/* 215:    */     catch (ClassNotFoundException e)
/* 216:    */     {
/* 217:234 */       JOptionPane.showMessageDialog(null, "Error reading settings file.", "Error", 0);
/* 218:235 */       return Boolean.valueOf(false);
/* 219:    */     }
/* 220:237 */     return Boolean.valueOf(true);
/* 221:    */   }
/* 222:    */   
/* 223:    */   public void createFS()
/* 224:    */   {
/* 225:246 */     if (this.OS == OS_TYPE.WINDOWS)
/* 226:    */     {
/* 227:247 */       this.SETTINGS_DIRECTORY = new File(System.getenv("APPDATA") + "/WeaveUpdater/");
/* 228:    */     }
/* 229:248 */     else if (this.OS == OS_TYPE.LINUX)
/* 230:    */     {
/* 231:249 */       this.SETTINGS_DIRECTORY = new File(System.getenv("HOME") + "/.config/WeaveUpdater/");
/* 232:    */     }
/* 233:250 */     else if (this.OS == OS_TYPE.MAC)
/* 234:    */     {
/* 235:251 */       this.SETTINGS_DIRECTORY = new File("~/Library/Application Support/WeaveUpdater/");
/* 236:    */     }
/* 237:    */     else
/* 238:    */     {
/* 239:254 */       System.out.println("Error: Unknown OS!!");
/* 240:255 */       JOptionPane.showMessageDialog(null, "You have an unknown Operating System\nthat is not supported by this program.", 
/* 241:256 */         "Error", 0);
/* 242:257 */       System.exit(1);
/* 243:258 */       return;
/* 244:    */     }
/* 245:261 */     this.EXE_DIRECTORY = new File(this.SETTINGS_DIRECTORY, "/exe/");
/* 246:262 */     this.ZIP_DIRECTORY = new File(this.SETTINGS_DIRECTORY, "/revisions/");
/* 247:263 */     this.UNZIP_DIRECTORY = new File(this.SETTINGS_DIRECTORY, "/unzip/");
/* 248:264 */     this.SETTINGS_FILE = new File(this.SETTINGS_DIRECTORY, "/settings");
/* 249:266 */     if (isConnectedToInternet().booleanValue())
/* 250:    */     {
/* 251:268 */       this.TOMCAT_FILE = new File(this.EXE_DIRECTORY, "/tomcat_" + getLatestTomcatVersion() + ".exe");
/* 252:269 */       this.MySQL_FILE = new File(this.EXE_DIRECTORY, "/mysql_" + getLatestMySQLVersion() + ".msi");
/* 253:    */     }
/* 254:273 */     if (!this.SETTINGS_DIRECTORY.exists()) {
/* 255:273 */       this.SETTINGS_DIRECTORY.mkdirs();
/* 256:    */     }
/* 257:274 */     if (!this.ZIP_DIRECTORY.exists()) {
/* 258:274 */       this.ZIP_DIRECTORY.mkdirs();
/* 259:    */     }
/* 260:275 */     if (!this.EXE_DIRECTORY.exists()) {
/* 261:275 */       this.EXE_DIRECTORY.mkdirs();
/* 262:    */     }
/* 263:277 */     System.out.println("file structure created");
/* 264:    */   }
/* 265:    */   
/* 266:    */   public Boolean isServiceUp(int port)
/* 267:    */   {
/* 268:287 */     Boolean b = Boolean.valueOf(false);
/* 269:    */     try
/* 270:    */     {
/* 271:289 */       Socket sock = new Socket("localhost", port);
/* 272:290 */       b = Boolean.valueOf(true);
/* 273:291 */       sock.close();
/* 274:    */     }
/* 275:    */     catch (IOException localIOException) {}catch (IllegalArgumentException ex)
/* 276:    */     {
/* 277:295 */       JOptionPane.showMessageDialog(null, "Port out of range.");
/* 278:    */     }
/* 279:297 */     return b;
/* 280:    */   }
/* 281:    */   
/* 282:    */   public int getLatency(String addr)
/* 283:    */   {
/* 284:    */     try
/* 285:    */     {
/* 286:308 */       URL url = new URL(addr);
/* 287:309 */       long start = System.currentTimeMillis();
/* 288:310 */       HttpURLConnection conn = (HttpURLConnection)url.openConnection();
/* 289:311 */       conn.setConnectTimeout(1000);
/* 290:312 */       conn.connect();
/* 291:313 */       long end = System.currentTimeMillis();
/* 292:314 */       if (conn.getResponseCode() == 200) {
/* 293:315 */         return new Long(end - start).intValue();
/* 294:    */       }
/* 295:317 */       return -1;
/* 296:    */     }
/* 297:    */     catch (Exception e)
/* 298:    */     {
/* 299:319 */       e.printStackTrace();
/* 300:    */     }
/* 301:320 */     return -1;
/* 302:    */   }
/* 303:    */   
/* 304:    */   public Boolean isConnectedToInternet()
/* 305:    */   {
/* 306:    */     try
/* 307:    */     {
/* 308:332 */       URL url = new URL("http://www.google.com/");
/* 309:333 */       URLConnection conn = url.openConnection();
/* 310:334 */       conn.getContent();
/* 311:    */     }
/* 312:    */     catch (IOException e)
/* 313:    */     {
/* 314:336 */       return Boolean.valueOf(false);
/* 315:    */     }
/* 316:    */     try
/* 317:    */     {
/* 318:339 */       URL url = new URL("http://www.yahoo.com/");
/* 319:340 */       url.openConnection();
/* 320:341 */       URLConnection conn = url.openConnection();
/* 321:342 */       conn.getContent();
/* 322:    */     }
/* 323:    */     catch (IOException e)
/* 324:    */     {
/* 325:344 */       return Boolean.valueOf(false);
/* 326:    */     }
/* 327:346 */     return Boolean.valueOf(true);
/* 328:    */   }
/* 329:    */   
/* 330:    */   public void findOS()
/* 331:    */   {
/* 332:356 */     String os = System.getProperty("os.name");
/* 333:358 */     if (os.toLowerCase().contains("windows")) {
/* 334:359 */       this.OS = OS_TYPE.WINDOWS;
/* 335:361 */     } else if ((os.toLowerCase().contains("nix")) || (os.toLowerCase().contains("nux"))) {
/* 336:362 */       this.OS = OS_TYPE.LINUX;
/* 337:363 */     } else if (os.toLowerCase().contains("mac")) {
/* 338:364 */       this.OS = OS_TYPE.MAC;
/* 339:    */     } else {
/* 340:366 */       this.OS = OS_TYPE.UNKNOWN;
/* 341:    */     }
/* 342:368 */     System.out.println("Detected OS: " + os);
/* 343:    */   }
/* 344:    */   
/* 345:    */   public void findTomcatDir()
/* 346:    */   {
/* 347:377 */     if (this.OS == OS_TYPE.WINDOWS)
/* 348:    */     {
/* 349:379 */       String[] paths = {
/* 350:380 */         "\"HKLM\\SOFTWARE\\Apache Software Foundation\\Tomcat\\6.0\" /v InstallPath", 
/* 351:381 */         "\"HKLM\\SOFTWARE\\Apache Software Foundation\\Tomcat\\7.0\" /v InstallPath", 
/* 352:382 */         "\"HKLM\\SOFTWARE\\Apache Software Foundation\\Tomcat\\7.0\\Tomcat7\" /v InstallPath" };
/* 353:386 */       for (String path : paths)
/* 354:    */       {
/* 355:388 */         System.out.println(path);
/* 356:389 */         String installPath = queryRegistry(path);
/* 357:390 */         if (installPath != null)
/* 358:    */         {
/* 359:392 */           System.out.println("Install Path: \"" + installPath + "\"");
/* 360:393 */           this.TOMCAT_DIR = installPath;
/* 361:394 */           return;
/* 362:    */         }
/* 363:    */       }
/* 364:398 */       System.out.println("Ask user for install path");
/* 365:399 */       this.TOMCAT_DIR = "";
/* 366:    */     }
/* 367:    */   }
/* 368:    */   
/* 369:    */   public String getBestTomcatURL()
/* 370:    */   {
/* 371:409 */     String url = getLatestTomcatURL();
/* 372:410 */     if (getLatency(url) >= 0) {
/* 373:411 */       return url;
/* 374:    */     }
/* 375:413 */     url = getLatestTomcatBackupURL();
/* 376:414 */     if (getLatency(url) >= 0) {
/* 377:415 */       return url;
/* 378:    */     }
/* 379:417 */     return null;
/* 380:    */   }
/* 381:    */   
/* 382:    */   public String getBestMySQLURL()
/* 383:    */   {
/* 384:426 */     String url = getLatestMySQLURL();
/* 385:427 */     if (getLatency(url) >= 0) {
/* 386:428 */       return url;
/* 387:    */     }
/* 388:430 */     url = getLatestMySQLBackupURL();
/* 389:431 */     if (getLatency(url) >= 0) {
/* 390:432 */       return url;
/* 391:    */     }
/* 392:434 */     return null;
/* 393:    */   }
/* 394:    */   
/* 395:    */   public String[] getConfigFile()
/* 396:    */   {
/* 397:444 */     String content = "";
/* 398:    */     try
/* 399:    */     {
/* 400:447 */       URL url = new URL("http://oicweave.org/WeaveUpdater/config.txt");
/* 401:448 */       String line = "";
/* 402:449 */       InputStream is = url.openStream();
/* 403:450 */       DataInputStream dis = new DataInputStream(new BufferedInputStream(is));
/* 404:452 */       while ((line = dis.readLine()) != null) {
/* 405:453 */         content = content + line;
/* 406:    */       }
/* 407:    */     }
/* 408:    */     catch (Exception e)
/* 409:    */     {
/* 410:455 */       e.printStackTrace();
/* 411:    */     }
/* 412:457 */     return content.split(";");
/* 413:    */   }
/* 414:    */   
/* 415:    */   public String getLatestWeaveUpdaterURL()
/* 416:    */   {
/* 417:462 */     for (String s : getConfigFile()) {
/* 418:463 */       if (s.contains("WeaveUpdaterURL")) {
/* 419:464 */         return s.substring(s.indexOf(":") + 1).trim();
/* 420:    */       }
/* 421:    */     }
/* 422:465 */     return null;
/* 423:    */   }
/* 424:    */   
/* 425:    */   public String getLatestWeaveUpdaterVersion()
/* 426:    */   {
/* 427:469 */     for (String s : getConfigFile()) {
/* 428:470 */       if (s.contains("WeaveUpdaterVersion")) {
/* 429:471 */         return s.substring(s.indexOf(":") + 1).trim();
/* 430:    */       }
/* 431:    */     }
/* 432:472 */     return null;
/* 433:    */   }
/* 434:    */   
/* 435:    */   public String getLatestTomcatURL()
/* 436:    */   {
/* 437:476 */     for (String s : getConfigFile()) {
/* 438:477 */       if (s.contains("TomcatURL")) {
/* 439:478 */         return s.substring(s.indexOf(":") + 1).trim();
/* 440:    */       }
/* 441:    */     }
/* 442:479 */     return null;
/* 443:    */   }
/* 444:    */   
/* 445:    */   public String getLatestTomcatBackupURL()
/* 446:    */   {
/* 447:483 */     for (String s : getConfigFile()) {
/* 448:484 */       if (s.contains("TomcatBackupURL")) {
/* 449:485 */         return s.substring(s.indexOf(":") + 1).trim();
/* 450:    */       }
/* 451:    */     }
/* 452:486 */     return null;
/* 453:    */   }
/* 454:    */   
/* 455:    */   public String getLatestTomcatVersion()
/* 456:    */   {
/* 457:490 */     for (String s : getConfigFile()) {
/* 458:491 */       if (s.contains("TomcatVersion")) {
/* 459:492 */         return s.substring(s.indexOf(":") + 1).trim();
/* 460:    */       }
/* 461:    */     }
/* 462:493 */     return null;
/* 463:    */   }
/* 464:    */   
/* 465:    */   public String getLatestMySQLURL()
/* 466:    */   {
/* 467:497 */     for (String s : getConfigFile()) {
/* 468:498 */       if (s.contains("MySQLURL")) {
/* 469:499 */         return s.substring(s.indexOf(":") + 1).trim();
/* 470:    */       }
/* 471:    */     }
/* 472:500 */     return null;
/* 473:    */   }
/* 474:    */   
/* 475:    */   public String getLatestMySQLBackupURL()
/* 476:    */   {
/* 477:504 */     for (String s : getConfigFile()) {
/* 478:505 */       if (s.contains("MySQLBackupURL")) {
/* 479:506 */         return s.substring(s.indexOf(":") + 1).trim();
/* 480:    */       }
/* 481:    */     }
/* 482:507 */     return null;
/* 483:    */   }
/* 484:    */   
/* 485:    */   public String getLatestMySQLVersion()
/* 486:    */   {
/* 487:511 */     for (String s : getConfigFile()) {
/* 488:512 */       if (s.contains("MySQLVersion")) {
/* 489:513 */         return s.substring(s.indexOf(":") + 1).trim();
/* 490:    */       }
/* 491:    */     }
/* 492:514 */     return null;
/* 493:    */   }
/* 494:    */   
/* 495:    */   private String queryRegistry(String cmd)
/* 496:    */   {
/* 497:526 */     Process proc = null;
/* 498:    */     try
/* 499:    */     {
/* 500:529 */       proc = Runtime.getRuntime().exec("reg query " + cmd);
/* 501:    */       
/* 502:    */ 
/* 503:    */ 
/* 504:    */ 
/* 505:534 */       StreamReader reader = new StreamReader(proc.getInputStream());
/* 506:535 */       reader.start();
/* 507:    */       
/* 508:    */ 
/* 509:    */ 
/* 510:    */ 
/* 511:540 */       int result = proc.waitFor();
/* 512:541 */       if (result != 0)
/* 513:    */       {
/* 514:543 */         BufferedReader errorReader = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
/* 515:544 */         System.out.println(errorReader.readLine() + "\n");
/* 516:545 */         return null;
/* 517:    */       }
/* 518:548 */       reader.join();
/* 519:    */       
/* 520:550 */       String output = reader.getResult();
/* 521:553 */       if (!output.contains("\t")) {
/* 522:554 */         return null;
/* 523:    */       }
/* 524:557 */       String[] parsed = output.split("\t");
/* 525:    */       
/* 526:    */ 
/* 527:    */ 
/* 528:    */ 
/* 529:    */ 
/* 530:563 */       return parsed[(parsed.length - 1)].split("\r\n")[0];
/* 531:    */     }
/* 532:    */     catch (Exception e)
/* 533:    */     {
/* 534:566 */       e.printStackTrace();
/* 535:    */     }
/* 536:568 */     return null;
/* 537:    */   }
/* 538:    */   
/* 539:    */   private class StreamReader
/* 540:    */     extends Thread
/* 541:    */   {
/* 542:    */     private InputStream is;
/* 543:574 */     private StringWriter sw = new StringWriter();
/* 544:    */     
/* 545:    */     public StreamReader(InputStream is)
/* 546:    */     {
/* 547:583 */       this.is = is;
/* 548:    */     }
/* 549:    */     
/* 550:    */     public void run()
/* 551:    */     {
/* 552:    */       try
/* 553:    */       {
/* 554:    */         int c;
/* 555:592 */         while ((c = this.is.read()) != -1)
/* 556:    */         {
/* 558:593 */           this.sw.write(c);
/* 559:    */         }
/* 560:    */       }
/* 561:    */       catch (IOException localIOException) {}
/* 562:    */     }
/* 563:    */     
/* 564:    */     public String getResult()
/* 565:    */     {
/* 566:601 */       return this.sw.toString();
/* 567:    */     }
/* 568:    */   }
/* 569:    */ }


/* Location:           C:\Users\Andy\Desktop\WeaveUpdaterV1.2\Weave Installer.jar
 * Qualified Name:     weave.Settings
 * JD-Core Version:    0.7.0.1
 */