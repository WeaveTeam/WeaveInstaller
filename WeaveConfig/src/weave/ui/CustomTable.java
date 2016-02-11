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

import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.DropMode;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import weave.Settings;
import weave.comparables.FileSize;
import weave.utils.FileUtils;

@SuppressWarnings("serial")
public class CustomTable extends JPanel
{
	private JTable table;
	private TableRowSorter<TableModel> sorter;
	private JScrollPane scrollPane;
	private String[] columnNames;
	private Class<?>[] columnClasses;
	private Boolean[] sortable;
	private Object[][] data;
	private int defaultSortColID;
	private SortOrder defaultSortOrder;

	public CustomTable(String[] columnNames, Class<?>[] classes, Boolean[] sortable, Object[][] data, int sortCol, SortOrder order)
	{
		setLayout(new GridLayout());

		setTableStructure(columnNames, classes, sortable, sortCol, order);
		setData(data).refreshTable(true);
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
	public void setSelectedIndex(int i)
	{
		table.setRowSelectionInterval(i, i);
	}
	public CustomTable setTableStructure(String[] names, Class<?>[] classes, Boolean[] sortable, int sortCol, SortOrder order)
	{
		this.columnNames = names;
		this.columnClasses = classes;
		this.sortable = sortable;
		this.defaultSortColID = sortCol;
		this.defaultSortOrder = order;
		generateTable();
		return this;
	}
	public CustomTable setColumnSizes(int[] sizes)
	{
		for( int i = 0; i < columnNames.length; i++ )
			table.getColumnModel().getColumn(i).setPreferredWidth(sizes[i]);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		return this;
	}
	public CustomTable setData(Object[][] data)
	{
		this.data = data;
		return this;
	}
	public void addTableMouseListener(MouseListener l)
	{
		table.addMouseListener(l);
	}
	public void addTableFocusListener(FocusListener l)
	{
		table.addFocusListener(l);
	}
	public void addTableSelectionListener(ListSelectionListener l)
	{
		table.getSelectionModel().addListSelectionListener(l);
	}
	private void generateTable()
	{
		table = new JTable(new CustomTableModel(columnNames, columnClasses));
		sorter = new TableRowSorter<TableModel>(table.getModel());
		table.setRowSorter(sorter);
		
		for( int i = 0; i < sortable.length; i++ ) {
			sorter.setSortable(i, sortable[i]);
			table.getColumnModel().getColumn(i).setCellRenderer(new CustomCellRenderer());
		}

		table.setFont(new Font(Settings.FONT, Font.PLAIN, 11));
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setDragEnabled(true);
		table.setDropMode(DropMode.INSERT_ROWS);
		scrollPane = new JScrollPane(table);
		
		add(scrollPane);
	}
	
	public void refreshTable()
	{
		refreshTable(false);
	}
	
	public void refreshTable(boolean sort)
	{
		// Add row(s) if needed
		for( int i = 0; i < data.length; i++ )
		{
			for( int j = 0; j < columnNames.length; j++ )
			{
				if( table.getModel().getRowCount() <= i )
					((DefaultTableModel)table.getModel()).addRow(data[i]);
				else
					((DefaultTableModel)table.getModel()).setValueAt(data[i][j], i, j);
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
		// Sort data based on provided sort
		if( sort && defaultSortColID != -1 )
		{
			List<SortKey> list = new ArrayList<SortKey>();
			list.add(new RowSorter.SortKey(defaultSortColID, defaultSortOrder));
			sorter.setSortKeys(list);
			sorter.sort();
		}
	}
	
	class CustomTableModel extends DefaultTableModel
	{
		private Class<?>[] classList = null;
		
		public CustomTableModel(String[] columns, Class<?>[] classes) {
			super(columns, 0);
			classList = classes;
		}
			
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
		
		@Override
		public Class<?> getColumnClass(int i) {
			return classList[i];
		}
	}
	
	class CustomCellRenderer extends DefaultTableCellRenderer
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy h:mm a");
		
		@Override
		public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			if( value instanceof Date )
			{
				value = dateFormat.format(value);
			}
			else if( value instanceof FileSize )
			{
				value = FileUtils.sizeify(((FileSize)value).getSize());
			}
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
	}
}
