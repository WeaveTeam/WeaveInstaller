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

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;

import javax.swing.DropMode;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import weave.Settings;

@SuppressWarnings("serial")
public class CustomTable extends JPanel
{
	private JTable table;
	private JScrollPane scrollPane;
	private String[] columnNames;
	private Object[][] data;

	public CustomTable()
	{
		setLayout(new GridLayout());
		
		columnNames = new String[0];
		data = new Object[0][0];
		
		generateTable();
		refreshTable();
	}
	
	public CustomTable(String[] columnNames, Object[][] data)
	{
		setLayout(new GridLayout());
		
		setColumnNames(columnNames);
		setData(data);
		
		generateTable();
		refreshTable();
	}
	public int getSelectedIndex()
	{
		return table.getSelectedRow();
	}
	public Object[] getSelectedRow()
	{
		int row = getSelectedIndex();
		Object[] ret = new Object[columnNames.length];
		
		if( row < 0 )
			return ret;
		
		for( int i = 0; i < ret.length; i++ )
			ret[i] = table.getValueAt(row, i);
		
		return ret;
	}
	public void setColumnNames(String[] names)
	{
		this.columnNames = names;
	}
	public void setColumnSizes(int[] sizes)
	{
		for( int i = 0; i < columnNames.length; i++ )
			table.getColumnModel().getColumn(i).setPreferredWidth(sizes[i]);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
	}
	public void setData(Object[][] data)
	{
		this.data = data;
	}
	public void addTableMouseListener(MouseListener l)
	{
		table.addMouseListener(l);
	}
	public void addTableFocusListener(FocusListener l)
	{
		table.addFocusListener(l);
	}
	private void generateTable()
	{
		table = new JTable(new DefaultTableModel(null, columnNames)) {
			public boolean isCellEditable(int rowIndex, int colIndex) {
				return false;
			}
		};
		table.setFont(new Font(Settings.FONT, Font.PLAIN, 11));
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setDragEnabled(true);
		table.setDropMode(DropMode.INSERT_ROWS);
//		table.setRowSorter(new TableRowSorter<TableModel>(table.getModel()));
		scrollPane = new JScrollPane(table);
		
		add(scrollPane);
	}
	public void refreshTable()
	{
		// Add row(s) if needed
		for( int i = 0; i < data.length; i++ )
		{
			for( int j = 0; j < columnNames.length; j++ )
			{
				if( table.getModel().getRowCount() <= i )
					((DefaultTableModel)table.getModel()).addRow(data[i]);
				else
					table.getModel().setValueAt(data[i][j], i, j);
			}
		}
		// Remove row(s) if needed
		if( data.length < table.getModel().getRowCount() )
		{
			for( int i = table.getModel().getRowCount()-1; i >= data.length; i-- )
			{
				((DefaultTableModel)table.getModel()).removeRow(i);
			}
		}
	}
}
