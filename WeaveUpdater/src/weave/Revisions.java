/*   1:    */ package weave;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.FileOutputStream;
/*   5:    */ import java.io.InputStream;
/*   6:    */ import java.net.URL;
/*   7:    */ import java.net.URLConnection;
/*   8:    */ import java.text.SimpleDateFormat;
/*   9:    */ import java.util.ArrayList;
/*  10:    */ import java.util.Calendar;
/*  11:    */ import java.util.Collections;
/*  12:    */ import java.util.Comparator;
/*  13:    */ import java.util.Enumeration;
/*  14:    */ import java.util.Iterator;
/*  15:    */ import java.util.zip.ZipEntry;
/*  16:    */ import java.util.zip.ZipFile;
/*  17:    */ import javax.swing.JButton;
/*  18:    */ import javax.swing.JOptionPane;
/*  19:    */ import javax.swing.JProgressBar;
/*  20:    */ import javax.swing.SwingUtilities;
/*  21:    */ import weave.ui.PostSetupPanel;
/*  22:    */ 
/*  23:    */ public class Revisions
/*  24:    */ {
/*  25:    */   public static int checkForUpdates(boolean save)
/*  26:    */   {
/*  27: 55 */     if (!Settings.instance().isConnectedToInternet().booleanValue()) {
/*  28: 56 */       return -2;
/*  29:    */     }
/*  30:    */     try
/*  31:    */     {
/*  32: 62 */       Settings.instance().getClass();URL url = new URL("https://github.com/IVPR/Weave-Binaries/zipball/master");
/*  33:    */       
/*  34:    */ 
/*  35: 65 */       URLConnection conn = url.openConnection();
/*  36:    */       
/*  37: 67 */       String urlFileName = conn.getHeaderField("Content-Disposition");
/*  38:    */       
/*  39: 69 */       int pos = urlFileName != null ? urlFileName.indexOf("filename=") : -1;
/*  40: 70 */       if (pos == -1) {
/*  41: 71 */         return -1;
/*  42:    */       }
/*  43: 76 */       String updateFileName = Settings.instance().ZIP_DIRECTORY.getPath() + "/" + urlFileName.substring(pos + 9);
/*  44: 77 */       File updateFile = new File(updateFileName);
/*  45: 81 */       if (save)
/*  46:    */       {
/*  47: 82 */         Settings.instance().LAST_UPDATE_CHECK = new SimpleDateFormat("M/dd/yy h:mm a").format(Calendar.getInstance().getTime());
/*  48: 83 */         Settings.instance().writeSettings();
/*  49:    */       }
/*  50: 87 */       return updateFile.exists() ? 0 : 1;
/*  51:    */     }
/*  52:    */     catch (Exception e)
/*  53:    */     {
/*  54: 89 */       e.printStackTrace();
/*  55:    */     }
/*  56: 94 */     return -1;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public static void extractZip(String fileName, final JProgressBar progBar, final JButton button)
/*  60:    */   {
/*  61:106 */     progBar.setStringPainted(true);
/*  62:107 */     progBar.setString("Extracting Files...");
/*  63:108 */     progBar.setIndeterminate(false);
/*  64:    */     final String finalFileName = fileName;
/*  65:110 */     Thread t = new Thread(new Runnable()
/*  66:    */     {
/*  67:    */       public void run()
/*  68:    */       {
/*  69:    */         try
/*  70:    */         {
/*  71:114 */           int i = 0;
/*  72:115 */           ZipFile zipFile = new ZipFile(finalFileName);
/*  73:116 */           Enumeration<?> enu = zipFile.entries();
/*  74:    */           
/*  75:118 */           progBar.setMaximum(zipFile.size());
/*  76:119 */           progBar.setValue(i);
/*  77:120 */           while (enu.hasMoreElements())
/*  78:    */           {
/*  79:121 */             ZipEntry zipEntry = (ZipEntry)enu.nextElement();
/*  80:    */             
/*  81:123 */             String name = zipEntry.getName();
/*  82:    */             
/*  83:    */ 
/*  84:    */ 
/*  85:    */ 
/*  86:128 */             File file = new File(Settings.instance().UNZIP_DIRECTORY.getPath() + "/" + name);
/*  87:129 */             if (name.endsWith("/"))
/*  88:    */             {
/*  89:130 */               file.mkdirs();
/*  90:    */             }
/*  91:    */             else
/*  92:    */             {
/*  93:134 */               InputStream is = zipFile.getInputStream(zipEntry);
/*  94:135 */               FileOutputStream fos = new FileOutputStream(file);
/*  95:136 */               byte[] bytes = new byte[1024];
/*  96:    */               int length;
/*  97:138 */               while ((length = is.read(bytes)) >= 0)
/*  98:    */               {
/* 100:139 */                 fos.write(bytes, 0, length);
/* 101:    */               }
/* 102:141 */               is.close();
/* 103:142 */               fos.close();
/* 104:143 */               progBar.setValue(++i);
/* 105:144 */               Thread.sleep(100L);
/* 106:    */             }
/* 107:    */           }
/* 108:146 */           zipFile.close();
/* 109:    */           
/* 110:148 */           progBar.setMaximum(100);
/* 111:149 */           progBar.setString("Files Extracted Successfully");
/* 112:150 */           progBar.setIndeterminate(false);
/* 113:151 */           progBar.setValue(100);
/* 114:152 */           Revisions.moveExtractedFiles(finalFileName, progBar, button);
/* 115:    */         }
/* 116:    */         catch (Exception e)
/* 117:    */         {
/* 118:154 */           e.printStackTrace();
/* 119:155 */           JOptionPane.showMessageDialog(null, "Error extracting build " + Revisions.getRevisionName(finalFileName) + ". The version is currupt.\n\nPlease delete this revision.", "Error", 0);
/* 120:    */           
/* 121:157 */           progBar.setStringPainted(true);
/* 122:158 */           progBar.setString("");
/* 123:159 */           progBar.setIndeterminate(false);
/* 124:160 */           progBar.setMaximum(100);
/* 125:161 */           progBar.setValue(0);
/* 126:162 */           WeaveUpdater.updater.postSP.deleteButton.setEnabled(true);
/* 127:163 */           WeaveUpdater.updater.postSP.deploy.setEnabled(true);
/* 128:    */         }
/* 129:    */       }
/* 130:166 */     });
/* 131:167 */     t.start();
/* 132:    */   }
/* 133:    */   
/* 134:    */   private static void moveExtractedFiles(String zipFileName, JProgressBar progBar, JButton button)
/* 135:    */     throws Exception
/* 136:    */   {
/* 137:179 */     String[] files = Settings.instance().UNZIP_DIRECTORY.list();
/* 138:180 */     File releaseDir = new File(Settings.instance().UNZIP_DIRECTORY.getPath() + "/" + files[0]);
/* 139:181 */     File webapps = new File(Settings.instance().TOMCAT_DIR + "/webapps/");
/* 140:182 */     File ROOT = new File(webapps, "ROOT");
/* 141:183 */     String[] releaseFiles = releaseDir.list();
/* 142:185 */     for (int i = 0; i < releaseFiles.length; i++)
/* 143:    */     {
/* 144:187 */       String fileName = releaseFiles[i];
/* 145:188 */       int extN = fileName.lastIndexOf('.');
/* 146:189 */       if (extN == -1)
/* 147:    */       {
/* 148:191 */         if (fileName.equals("ROOT"))
/* 149:    */         {
/* 150:193 */           File rootDir = new File(releaseDir, "ROOT");
/* 151:194 */           String[] rootFiles = rootDir.list();
/* 152:    */           
/* 153:196 */           progBar.setStringPainted(true);
/* 154:197 */           progBar.setString("Installing Files...");
/* 155:198 */           progBar.setMaximum(rootFiles.length);
/* 156:200 */           for (int j = 0; j < rootFiles.length; j++)
/* 157:    */           {
/* 158:202 */             String rootFileName = rootFiles[j];
/* 159:203 */             File unzipedFile = new File(rootDir, rootFileName);
/* 160:204 */             File movedFile = new File(ROOT, rootFileName);
/* 161:205 */             if (movedFile.exists()) {
/* 162:206 */               movedFile.delete();
/* 163:    */             }
/* 164:208 */             unzipedFile.renameTo(movedFile);
/* 165:209 */             progBar.setValue(j + 1);
/* 166:    */             
/* 167:211 */             Thread.sleep(300L);
/* 168:    */           }
/* 169:    */         }
/* 170:    */       }
/* 171:    */       else
/* 172:    */       {
/* 173:217 */         String ext = fileName.substring(extN + 1, fileName.length());
/* 174:218 */         if (ext.equals("war"))
/* 175:    */         {
/* 176:220 */           File unzipedWar = new File(releaseDir, fileName);
/* 177:221 */           File movedWar = new File(webapps, fileName);
/* 178:222 */           if (movedWar.exists()) {
/* 179:223 */             movedWar.delete();
/* 180:    */           }
/* 181:224 */           unzipedWar.renameTo(movedWar);
/* 182:    */         }
/* 183:    */       }
/* 184:    */     }
/* 185:228 */     progBar.setString("Installation Finished. Please wait...");
/* 186:229 */     progBar.setIndeterminate(false);
/* 187:230 */     progBar.setMaximum(100);
/* 188:231 */     progBar.setValue(100);
/* 189:    */     
/* 190:233 */     Settings.instance().CURRENT_INSTALL_VER = getRevisionName(zipFileName);
/* 191:    */     
/* 192:235 */     recursiveDelete(Settings.instance().UNZIP_DIRECTORY);
/* 193:236 */     Settings.instance().writeSettings();
/* 194:237 */     button.setEnabled(true);
/* 195:238 */     WeaveUpdater.updater.cancelButton.setEnabled(true);
/* 196:    */     
/* 197:240 */     SwingUtilities.invokeLater(new Runnable()
/* 198:    */     {
/* 199:    */       public void run()
/* 200:    */       {
					//TODO
/* 201:243 */         //Revisions.this.doClick();
/* 202:    */       }
/* 203:    */     });
/* 204:    */   }
/* 205:    */   
/* 206:    */   public static int getNumberOfRevisions()
/* 207:    */   {
/* 208:255 */     if (!Settings.instance().ZIP_DIRECTORY.exists()) {
/* 209:256 */       return 0;
/* 210:    */     }
/* 211:258 */     return Settings.instance().ZIP_DIRECTORY.list().length;
/* 212:    */   }
/* 213:    */   
/* 214:    */   public static long getSizeOfRevisions()
/* 215:    */   {
/* 216:268 */     if (!Settings.instance().ZIP_DIRECTORY.exists()) {
/* 217:269 */       return 0L;
/* 218:    */     }
/* 219:271 */     long size = 0L;
/* 220:272 */     File[] files = Settings.instance().ZIP_DIRECTORY.listFiles();
/* 221:274 */     for (int i = 0; i < files.length; i++) {
/* 222:275 */       size += files[i].length();
/* 223:    */     }
/* 224:277 */     return size;
/* 225:    */   }
/* 226:    */   
/* 227:    */   public static boolean pruneRevisions()
/* 228:    */   {
/* 229:287 */     ArrayList<File> files = getRevisionData();
/* 230:288 */     Iterator<File> it = files.iterator();
/* 231:289 */     File file = null;
/* 232:290 */     int i = 0;
/* 233:291 */     long[] mods = new long[files.size()];
/* 234:293 */     while (it.hasNext()) {
/* 235:293 */       mods[(i++)] = ((File)it.next()).lastModified();
/* 236:    */     }
/* 237:295 */     i = 0;
/* 238:296 */     it = files.iterator();
/* 239:298 */     while (it.hasNext())
/* 240:    */     {
/* 241:300 */       file = (File)it.next();
/* 242:303 */       if ((i == 0) || (i == 1))
/* 243:    */       {
/* 244:303 */         i++;
/* 245:    */       }
/* 246:304 */       else if (i == Math.ceil((mods.length - 2) / 2) + 2.0D)
/* 247:    */       {
/* 248:304 */         i++;
/* 249:    */       }
/* 250:305 */       else if (i == mods.length - 2)
/* 251:    */       {
/* 252:305 */         i++;
/* 253:    */       }
/* 254:    */       else
/* 255:    */       {
/* 256:308 */         file.delete();
/* 257:309 */         i++;
/* 258:    */       }
/* 259:    */     }
/* 260:311 */     return true;
/* 261:    */   }
/* 262:    */   
/* 263:    */   public static String getRevisionName(String n)
/* 264:    */   {
/* 265:322 */     return n.substring(n.lastIndexOf('-') + 1, n.lastIndexOf('.')).toUpperCase();
/* 266:    */   }
/* 267:    */   
/* 268:    */   public static ArrayList<File> getRevisionData()
/* 269:    */   {
/* 270:333 */     File[] files = Settings.instance().ZIP_DIRECTORY.listFiles();
/* 271:334 */     ArrayList<File> sortedFiles = new ArrayList();
/* 272:336 */     for (int i = 0; i < files.length; i++) {
/* 273:337 */       sortedFiles.add(files[i]);
/* 274:    */     }
/* 275:339 */     Collections.sort(sortedFiles, new Comparator<File>()
/* 276:    */     {
/* 277:    */       public int compare(File o1, File o2)
/* 278:    */       {
/* 279:342 */         if (o1.lastModified() < o2.lastModified()) {
/* 280:342 */           return 1;
/* 281:    */         }
/* 282:343 */         if (o1.lastModified() > o2.lastModified()) {
/* 283:343 */           return -1;
/* 284:    */         }
/* 285:344 */         return 0;
/* 286:    */       }
/* 287:346 */     });
/* 288:347 */     return sortedFiles;
/* 289:    */   }
/* 290:    */   
/* 291:    */   public static boolean recursiveDelete(File dir)
/* 292:    */   {
/* 293:358 */     if (dir.isDirectory())
/* 294:    */     {
/* 295:360 */       String[] children = dir.list();
/* 296:361 */       for (int i = 0; i < children.length; i++) {
/* 297:363 */         recursiveDelete(new File(dir, children[i]));
/* 298:    */       }
/* 299:    */     }
/* 300:366 */     return dir.delete();
/* 301:    */   }
/* 302:    */ }


/* Location:           C:\Users\Andy\Desktop\WeaveUpdaterV1.2\Weave Installer.jar
 * Qualified Name:     weave.Revisions
 * JD-Core Version:    0.7.0.1
 */