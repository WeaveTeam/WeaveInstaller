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
import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import weave.utils.StringUtils;

@SuppressWarnings("serial")
public class Launcher extends JFrame
{
	public static Launcher launcher = null;
	
//	private Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	
	public static void main( final String[] args )
	{
		if( !Desktop.isDesktopSupported() ) {
			System.out.println("!! Desktop functionality not supported.");
			System.exit(NORMAL);
		}
		
		Settings.init();
		
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

		String path = "";
		int delay = 0;
		
		setState(JFrame.ICONIFIED);

		try {
			if( args.length == 0 ) System.exit(NORMAL);
			if( args.length > 0 ) path = args[0];
			if( args.length > 1 ) delay = Integer.parseInt(args[1]);
			
			
			// Handle special cases first
			if( StringUtils.beginsWith(path, Settings.PROJECT_PROTOCOL) )
			{
				String[] params = path.substring(Settings.PROJECT_PROTOCOL.length()).split("/");
				String cmd = params[0];
//				String arg = params[1];
				
				if( cmd.equals("open") ) {

				} 
				else if( cmd.equals("download") ) {
					
				}
				else if( cmd.equals("startApplication" ) ) {
					File file = new File(Settings.BIN_DIRECTORY, Settings.INSTALLER_JAR);
					start(file, delay);
				}
				else if( cmd.equals("stopApplication") ) {
					Socket s = new Socket(Settings.LOCALHOST, Settings.RPC_PORT);
					PrintWriter out = new PrintWriter(s.getOutputStream(), true);
					
					out.println("stopApplication");
					out.close();
					s.close();
				}
				else {
					throw new IllegalArgumentException("Invalid protocol command: " + cmd);
				}
			}
			else if( StringUtils.endsWith(path, Settings.PROJECT_EXTENSION) )
			{
				
			}
			else
			{
				start(path, delay);
			}

		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		} catch (Exception e3) {
			e3.printStackTrace();
		}
		
		System.exit(NORMAL);
	}
	
	private void start(String path, int delay) throws HeadlessException, IOException, InterruptedException 
	{
		start(new File(path), delay);
	}
	private void start(File file, int delay) throws IOException, InterruptedException
	{
		if( !file.exists() ) {
			System.out.println("!! Program not found: \"" + file.getCanonicalPath() + "\"");
			JOptionPane.showMessageDialog(null, 
					"Program not found: \n\"" + file.getCanonicalPath() + "\"", 
					"File Not Found", 
					JOptionPane.ERROR_MESSAGE);
			System.exit(NORMAL);
		}
		
		Thread.sleep(delay);
		Desktop.getDesktop().open(file.getCanonicalFile());
		Thread.sleep(delay);
	}
}
