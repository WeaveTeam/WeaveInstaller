/*
    Weave (Web-based Analysis and Visualization Environment)
    Copyright (C) 2008-2011 University of Massachusetts Lowell

    This file is a part of Weave.

    Weave is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License, Version 3,
    as published by the Free Software Foundation.

    Weave is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Weave.  If not, see <http://www.gnu.org/licenses/>.
*/

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

@SuppressWarnings("serial")
public class RevisionTable extends JPanel
{
	public JTable table;
	JScrollPane scrollPane;
	private int numColumns = 2;
	
	String[] columnNames =
	{
			"Revision",
			"Date Downloaded"
	};
	/** ReivisonTable()
	 * 
	 * Constructor for a revision table, the GUI element that keeps track of current/old installations.
	 * 
	 */
	public RevisionTable()
	{
		setLayout(new GridLayout());
		table = new JTable(new DefaultTableModel(null, columnNames)) {
			public boolean isCellEditable(int rowIndex, int colIndex) {
				return false;
			}
		};
		scrollPane = new JScrollPane(table);
		
		add(scrollPane);
	}
	/**
	 * Updates the table.
	 */
	@SuppressWarnings("deprecation")
	public void updateTableData()
	{
		/* Initialization */
		int numberOfRevisions = Revisions.getNumberOfRevisions();
		Object[][] data = new Object[numberOfRevisions][numColumns];
		ArrayList<File> sortedFiles = Revisions.getRevisionData();
		String fileName = "";
		String s = "";
		Date d = new Date();
		
		/* Iterate through the sorted files. */
		int k = 0;
		Iterator<File> it = sortedFiles.iterator();
		while( it.hasNext() )
		{
			File f = it.next();
			fileName = f.getName();
			s = Revisions.getRevisionName(fileName);
			/* If we find the current installation version, append ( current ) to the end of the string */
			if( s.equals(Settings.CURRENT_INSTALL_VER) )
				s += "  (current)";
			
			/* Add each revision to a different column */
			data[k][0] = s;
			
			/* Set the date of the revision to the second column of each revision entry row */
			d.setTime(f.lastModified());
			if( d.getHours() > 12 )
				data[k][1] = (d.getMonth()+1)+"/"+d.getDate()+"/"+(d.getYear()+1900)+" "+(d.getHours()-12)+":"+((d.getMinutes()>= 0 && d.getMinutes() < 10) ? "0":"")+d.getMinutes()+" PM";
			else
				data[k][1] = (d.getMonth()+1)+"/"+d.getDate()+"/"+(d.getYear()+1900)+" "+d.getHours()+":"+((d.getMinutes()>= 0 && d.getMinutes() < 10) ? "0":"")+d.getMinutes()+" AM";
			k++;
		}

		/* Finally, if the number of revisions exceeds the current row count, add a new row and set the value */
		for( int i = 0; i < numberOfRevisions; i++ )
			for( int j = 0; j < numColumns; j++ )
			{
				if( table.getModel().getRowCount() <= i )
					((DefaultTableModel)table.getModel()).addRow(data[i]);
				else
					table.getModel().setValueAt(data[i][j], i, j);
			}
		/* If the number of revisions is less than the rowCount(), remove a row */
		if( numberOfRevisions < table.getModel().getRowCount() )
		{
			for( int i = table.getModel().getRowCount()-1; i >= numberOfRevisions; i-- )
			{
				((DefaultTableModel)table.getModel()).removeRow(i);
			}
		}
	}
}
