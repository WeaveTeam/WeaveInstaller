/*   1:    */ package weave.ui;
/*   2:    */ 
/*   3:    */ import java.awt.GridLayout;
/*   4:    */ import java.io.File;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.Date;
/*   7:    */ import java.util.Iterator;
/*   8:    */ import javax.swing.JPanel;
/*   9:    */ import javax.swing.JScrollPane;
/*  10:    */ import javax.swing.JTable;
/*  11:    */ import javax.swing.table.DefaultTableModel;
/*  12:    */ import javax.swing.table.TableModel;
/*  13:    */ import weave.Revisions;
/*  14:    */ import weave.Settings;
/*  15:    */ 
/*  16:    */ public class RevisionTable
/*  17:    */   extends JPanel
/*  18:    */ {
/*  19:    */   public JTable table;
/*  20:    */   JScrollPane scrollPane;
/*  21: 41 */   private int numColumns = 2;
/*  22: 44 */   String[] columnNames = {
/*  23: 45 */     "Revision", 
/*  24: 46 */     "Date Downloaded" };
/*  25:    */   
/*  26:    */   public RevisionTable()
/*  27:    */   {
/*  28: 55 */     setLayout(new GridLayout());
/*  29: 56 */     this.table = new JTable(new DefaultTableModel(null, this.columnNames))
/*  30:    */     {
/*  31:    */       public boolean isCellEditable(int rowIndex, int colIndex)
/*  32:    */       {
/*  33: 58 */         return false;
/*  34:    */       }
/*  35: 60 */     };
/*  36: 61 */     this.scrollPane = new JScrollPane(this.table);
/*  37:    */     
/*  38: 63 */     add(this.scrollPane);
/*  39:    */   }
/*  40:    */   
/*  41:    */   public void updateTableData()
/*  42:    */   {
/*  43: 72 */     int numberOfRevisions = Revisions.getNumberOfRevisions();
/*  44: 73 */     Object[][] data = new Object[numberOfRevisions][this.numColumns];
/*  45: 74 */     ArrayList<File> sortedFiles = Revisions.getRevisionData();
/*  46: 75 */     String fileName = "";
/*  47: 76 */     String s = "";
/*  48: 77 */     Date d = new Date();
/*  49:    */     
/*  50:    */ 
/*  51: 80 */     int k = 0;
/*  52: 81 */     Iterator<File> it = sortedFiles.iterator();
/*  53: 82 */     while (it.hasNext())
/*  54:    */     {
/*  55: 84 */       File f = (File)it.next();
/*  56: 85 */       fileName = f.getName();
/*  57: 86 */       s = Revisions.getRevisionName(fileName);
/*  58: 88 */       if (s.equals(Settings.instance().CURRENT_INSTALL_VER)) {
/*  59: 89 */         s = s + "  (current)";
/*  60:    */       }
/*  61: 92 */       data[k][0] = s;
/*  62:    */       
/*  63:    */ 
/*  64: 95 */       d.setTime(f.lastModified());
/*  65: 96 */       if (d.getHours() > 12) {
/*  66: 97 */         data[k][1] = (d.getMonth() + 1 + "/" + d.getDate() + "/" + (d.getYear() + 1900) + " " + (d.getHours() - 12) + ":" + ((d.getMinutes() >= 0) && (d.getMinutes() < 10) ? "0" : "") + d.getMinutes() + " PM");
/*  67:    */       } else {
/*  68: 99 */         data[k][1] = (d.getMonth() + 1 + "/" + d.getDate() + "/" + (d.getYear() + 1900) + " " + d.getHours() + ":" + ((d.getMinutes() >= 0) && (d.getMinutes() < 10) ? "0" : "") + d.getMinutes() + " AM");
/*  69:    */       }
/*  70:100 */       k++;
/*  71:    */     }
/*  72:104 */     for (int i = 0; i < numberOfRevisions; i++) {
/*  73:105 */       for (int j = 0; j < this.numColumns; j++) {
/*  74:107 */         if (this.table.getModel().getRowCount() <= i) {
/*  75:108 */           ((DefaultTableModel)this.table.getModel()).addRow(data[i]);
/*  76:    */         } else {
/*  77:110 */           this.table.getModel().setValueAt(data[i][j], i, j);
/*  78:    */         }
/*  79:    */       }
/*  80:    */     }
/*  81:113 */     if (numberOfRevisions < this.table.getModel().getRowCount()) {
/*  82:115 */       for (int i = this.table.getModel().getRowCount() - 1; i >= numberOfRevisions; i--) {
/*  83:117 */         ((DefaultTableModel)this.table.getModel()).removeRow(i);
/*  84:    */       }
/*  85:    */     }
/*  86:    */   }
/*  87:    */ }


/* Location:           C:\Users\Andy\Desktop\WeaveUpdaterV1.2\Weave Installer.jar
 * Qualified Name:     weave.ui.RevisionTable
 * JD-Core Version:    0.7.0.1
 */