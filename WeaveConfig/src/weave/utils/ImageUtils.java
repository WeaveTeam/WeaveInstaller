package weave.utils;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import weave.Globals;

public class ImageUtils extends Globals
{
	public static final int SCALE_WIDTH = 1;
	public static final int SCALE_HEIGHT = 2;
	
	public static BufferedImage resize(BufferedImage image, int width, int height)
	{
		BufferedImage buff = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
		Graphics2D g = (Graphics2D) buff.createGraphics();
		g.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
		g.drawImage(image, 0, 0, width, height, null);
		g.dispose();
		return buff;
	}
	
	public static BufferedImage scale(BufferedImage image, int scaleTo, int dimension)
	{
		int oldWidth = image.getWidth();
		int oldHeight = image.getHeight();
		double aspectRatio = (oldWidth + 0.0) / (oldHeight + 0.0);
		int newWidth = 0;
		int newHeight = 0;
		
		if( dimension == SCALE_WIDTH )
		{
			newWidth = scaleTo;
			newHeight = (int) Math.round(newWidth / aspectRatio);
		}
		else if( dimension == SCALE_HEIGHT )
		{
			newHeight = scaleTo;
			newWidth = (int) Math.round(newHeight * aspectRatio);
		}
		return resize(image, newWidth, newHeight);
	}
}
