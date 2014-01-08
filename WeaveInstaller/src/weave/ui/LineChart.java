package weave.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class LineChart extends JPanel
{
   private static int MAX_SCORE = 20;
   private static final int BORDER_GAP = 1;
   private static final Stroke GRAPH_STROKE = new BasicStroke(3f);
   private static final Color TOMCAT_COLOR = Color.ORANGE;
   private static final Color MySQL_COLOR = Color.BLUE;
   private HashMap<String, ArrayList<Integer>> map = new HashMap<String, ArrayList<Integer>>();
   
	int maxDataPoints = 16;
	int maxScore = 20;
	Random random = new Random();

	public LineChart()
	{		
		setBackground(new Color(0xEEEEEE));
		setVisible(true);
	}

	public void push(String key, int kbps)
	{
		if( !map.containsKey(key) )
			map.put(key, new ArrayList<Integer>());
		
		((ArrayList<Integer>) map.get(key)).add(kbps);
		
		if( ((ArrayList<Integer>) map.get(key)).size() >= maxDataPoints )
			((ArrayList<Integer>) map.get(key)).remove(0);
		
		int max = 0;
		for( int i : ((ArrayList<Integer>)map.get(key)) )
			if( i > max ) max = i;
		MAX_SCORE = max;
		
		paintComponent(getGraphics());
	}
	
	public void clear()
	{
		MAX_SCORE = 20;
		for( String key : map.keySet() ) {
			while( map.get(key).size() > 0 )
				((ArrayList<Integer>) map.get(key)).remove(0);
			map.remove(key);
		}
		paintComponent(getGraphics());
	}
	
	@Override 
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		double xScale = ((double) getWidth() - 2 * BORDER_GAP) / (maxDataPoints - 1);
		double yScale = ((double) getHeight() - 2 * BORDER_GAP) / (MAX_SCORE - 1);
		
		for( String keys : map.keySet() )
		{
			ArrayList<Integer> scores = map.get(keys);
			
			List<Point> graphPoints = new ArrayList<Point>();
			for (int i = 0; i < scores.size(); i++) {
				int x1 = (int) (i * xScale + BORDER_GAP);
				int y1 = (int) (scores.get(i) * yScale + BORDER_GAP);
				graphPoints.add(new Point(x1, y1));
			}
			
			if( keys.equals("TOMCAT") )
				g2.setColor(TOMCAT_COLOR);
			else
				g2.setColor(MySQL_COLOR);
			
			g2.setStroke(GRAPH_STROKE);
			for (int i = 0; i < graphPoints.size() - 1; i++) {
				int x1 = graphPoints.get(i).x;
				int y1 = graphPoints.get(i).y;
				int x2 = graphPoints.get(i + 1).x;
				int y2 = graphPoints.get(i + 1).y;
				g2.drawLine(x1, y1, x2, y2);         
			}
		}
	}
}
