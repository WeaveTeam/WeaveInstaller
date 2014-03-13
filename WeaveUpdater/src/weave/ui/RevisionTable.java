package weave.ui;

import java.awt.GridLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import weave.Revisions;
import weave.Settings;

public class RevisionTable
		extends JPanel
{
	private static final long serialVersionUID = 1L;
	public JTable table;
	JScrollPane scrollPane;
	private int numColumns = 2;
	String[] columnNames = {
			"Revision", "Date Downloaded" };

	public RevisionTable()
	{
		setLayout(new GridLayout());
		this.table = new JTable(
				new DefaultTableModel(null, this.columnNames))
		{
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int rowIndex, int colIndex)
			{
				return false;
			}
		};
		this.scrollPane = new JScrollPane(this.table);

		add(this.scrollPane);
	}

	@SuppressWarnings("deprecation")
	public void updateTableData()
	{
		int numberOfRevisions = Revisions.getNumberOfRevisions();
		Object[][] data = new Object[numberOfRevisions][this.numColumns];
		ArrayList<File> sortedFiles = Revisions.getRevisionData();
		String fileName = "";
		String s = "";
		Date d = new Date();

		int k = 0;
		Iterator<File> it = sortedFiles.iterator();
		while (it.hasNext())
		{
			File f = (File) it.next();
			fileName = f.getName();
			s = Revisions.getRevisionName(fileName);
			if (s.equals(Settings.instance().CURRENT_INSTALL_VER))
			{
				s = s + "  (current)";
			}
			data[k][0] = s;

			d.setTime(f.lastModified());
			if (d.getHours() > 12)
			{
				data[k][1] = (d.getMonth() + 1 + "/" + d.getDate() + "/" + (d.getYear() + 1900) + " "
						+ (d.getHours() - 12) + ":" + ((d.getMinutes() >= 0) && (d.getMinutes() < 10)
								? "0" : "") + d.getMinutes() + " PM");
			}
			else
			{
				data[k][1] = (d.getMonth() + 1 + "/" + d.getDate() + "/" + (d.getYear() + 1900) + " " + d.getHours()
						+ ":" + ((d.getMinutes() >= 0) && (d.getMinutes() < 10)
								? "0" : "") + d.getMinutes() + " AM");
			}
			k++;
		}
		for (int i = 0; i < numberOfRevisions; i++)
		{
			for (int j = 0; j < this.numColumns; j++)
			{
				if (this.table.getModel().getRowCount() <= i)
				{
					((DefaultTableModel) this.table.getModel()).addRow(data[i]);
				}
				else
				{
					this.table.getModel().setValueAt(data[i][j], i, j);
				}
			}
		}
		if (numberOfRevisions < this.table.getModel().getRowCount())
		{
			for (int i = this.table.getModel().getRowCount() - 1; i >= numberOfRevisions; i--)
			{
				((DefaultTableModel) this.table.getModel()).removeRow(i);
			}
		}
	}
}
