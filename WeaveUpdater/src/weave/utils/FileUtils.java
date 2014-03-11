/*  1:   */ package weave.utils;
/*  2:   */ 
/*  3:   */ import java.io.File;
/*  4:   */ import java.io.FileInputStream;
/*  5:   */ import java.io.FileOutputStream;
/*  6:   */ import java.io.IOException;
/*  7:   */ import java.io.InputStream;
/*  8:   */ import java.io.OutputStream;
/*  9:   */ 
/* 10:   */ public class FileUtils
/* 11:   */ {
/* 12:   */   public static void copy(String source, String destination)
/* 13:   */     throws IOException
/* 14:   */   {
/* 15:36 */     copy(new File(source), new File(destination));
/* 16:   */   }
/* 17:   */   
/* 18:   */   public static void copy(File source, File destination)
/* 19:   */     throws IOException
/* 20:   */   {
/* 21:40 */     InputStream in = new FileInputStream(source);
/* 22:41 */     OutputStream out = new FileOutputStream(destination);
/* 23:42 */     copy(in, out);
/* 24:   */   }
/* 25:   */   
/* 26:   */   public static void copy(InputStream in, OutputStream out)
/* 27:   */     throws IOException
/* 28:   */   {
/* 29:46 */     byte[] buffer = new byte[4096];
/* 30:   */     int length;
/* 31:48 */     while ((length = in.read(buffer)) > 0)
/* 32:   */     {
/* 34:49 */       out.write(buffer, 0, length);
/* 35:   */     }
/* 36:50 */     in.close();
/* 37:51 */     out.close();
/* 38:   */   }
/* 39:   */ }


/* Location:           C:\Users\Andy\Desktop\WeaveUpdaterV1.2\Weave Installer.jar
 * Qualified Name:     weave.utils.FileUtils
 * JD-Core Version:    0.7.0.1
 */