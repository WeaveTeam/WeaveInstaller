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

package weave;

import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class Launcher extends JFrame
{
	public static Launcher launcher = null;
	
//	private Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	
	public static void main( final String[] args )
	{
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				launcher = new Launcher(args);
			}
		});
	}
	
	public Launcher(String[] args)
	{
		setSize(100, 50);
		setResizable(false);
		setLayout(null);
		setTitle(Settings.LAUNCHER_NAME);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocation(0, 0);
//		setLocation(screen.width/2 - getWidth()/2, screen.height/2 - getHeight()/2);
		setVisible(true);
		
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, getWidth(), getHeight());
		panel.setBackground(Color.BLACK);
		add(panel);

		String program = "";
		int delay = 0;
		
		setState(JFrame.ICONIFIED);

		try {
			if( args.length == 0 ) System.exit(NORMAL);
			if( args.length > 0 ) program = args[0];
			if( args.length > 1 ) delay = Integer.parseInt(args[1]);
			
			File prog = new File(program);
			
			if( !Desktop.isDesktopSupported() ) {
				System.out.println("!! Desktop functionality not supported.");
				System.exit(NORMAL);
			}
			if( !prog.exists() ) {
				System.out.println("!! Program not found: \"" + prog.getCanonicalPath() + "\"");
				JOptionPane.showMessageDialog(null, 
						"Program not found: \n\"" + prog.getCanonicalPath() + "\"", 
						"File Not Found", 
						JOptionPane.ERROR_MESSAGE);
				System.exit(NORMAL);
			}
			
			Desktop.getDesktop().open(prog.getCanonicalFile());
			Thread.sleep(delay);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		} catch (Exception e3) {
			e3.printStackTrace();
		}
		
		System.exit(NORMAL);
	}
}
