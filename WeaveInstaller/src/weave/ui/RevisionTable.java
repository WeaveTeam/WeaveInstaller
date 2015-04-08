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

import static weave.utils.TraceUtils.STDERR;
import static weave.utils.TraceUtils.trace;

import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import weave.Revisions;
import weave.Settings;
import weave.managers.ConfigManager;
import weave.utils.ObjectUtils;

@SuppressWarnings("serial")
public class RevisionTable extends JPanel
{
	private JTable table;
	private JScrollPane scrollPane;
	
	String[] columnNames =
	{
			"Revision",
			"Date Downloaded"
	};

	public RevisionTable()
	{
		setLayout(new GridLayout());
		table = new JTable(new DefaultTableModel(null, columnNames)) {
			public boolean isCellEditable(int rowIndex, int colIndex) {
				return false;
			}
		};
		table.setFont(new Font(Settings.FONT, Font.PLAIN, 11));
		scrollPane = new JScrollPane(table);
		
		add(scrollPane);
	}

	public JTable getTable()
	{
		return table;
	}
	
	public void updateTableData()
	{
		ArrayList<File> sortedFiles = Revisions.getRevisionsList();
		Object[][] data = new Object[sortedFiles.size()][columnNames.length];
		File file = null;
		Date date = new Date();
		String revisionName = "";
		
		try {
			for( int i = 0; i < sortedFiles.size(); i++ )
			{
				file = sortedFiles.get(i);
				revisionName = Revisions.getRevisionVersion(file.getName());
				date.setTime(file.lastModified());

				String configVer = (String)ObjectUtils.ternary(
										ConfigManager.getConfigManager().getActiveContainer(), "getInstallVersion", "");
				data[i][0] = revisionName + ((revisionName.equals(configVer)) ? "  (current)" : "" );
				data[i][1] = new SimpleDateFormat("MM/dd/yyyy h:mm a").format(date);
			}
		} catch (NoSuchMethodException e) {
			trace(STDERR, e);
		} catch (SecurityException e) {
			trace(STDERR, e);
		} catch (IllegalAccessException e) {
			trace(STDERR, e);
		} catch (IllegalArgumentException e) {
			trace(STDERR, e);
		} catch (InvocationTargetException e) {
			trace(STDERR, e);
		}

		// Add row(s) if needed
		for( int i = 0; i < sortedFiles.size(); i++ )
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
		if( sortedFiles.size() < table.getModel().getRowCount() )
		{
			for( int i = table.getModel().getRowCount()-1; i >= sortedFiles.size(); i-- )
			{
				((DefaultTableModel)table.getModel()).removeRow(i);
			}
		}
	}
}
