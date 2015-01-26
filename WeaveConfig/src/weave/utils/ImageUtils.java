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
	
	public static BufferedImage fit(BufferedImage image, int maxWidth, int maxHeight)
	{
		int imgWidth = image.getWidth();
		int imgHeight = image.getHeight();
		int widthDiff = maxWidth - imgWidth;
		int heightDiff = maxHeight - imgHeight;
		
//		System.out.printf("\nImage: %d x %d\nFit: %d x %d\nDiff: %d x %d\n", imgWidth, imgHeight, maxWidth, maxHeight, widthDiff, heightDiff);
		
		// width is on the bound
		// need to check height to see if it needs scale
		if( widthDiff == 0 )
		{
			return heightDiff >= 0 ?
					image :
					scale(image, maxHeight, SCALE_HEIGHT);
		}
		// height is on the bound
		// need to check width to see if it needs scale
		else if( heightDiff == 0 )
		{
			return widthDiff >= 0 ?
					image :
					scale(image, maxWidth, SCALE_WIDTH);
		}
		// width outside bounds, height inside bounds
		// need to scale to width
		else if( widthDiff < 0 && heightDiff > 0 )
		{
			return scale(image, maxWidth, SCALE_WIDTH);
		}
		// width inside bounds, height outside bounds
		// need to scale to height
		else if( widthDiff > 0 && heightDiff < 0 )
		{
			return scale(image, maxHeight, SCALE_HEIGHT);
		}
		// both dimensions inside bounds
		// need to scale to the smaller diff
		else if( widthDiff > 0 && heightDiff > 0 )
		{
			return widthDiff > heightDiff ? 
					scale(image, maxHeight, SCALE_HEIGHT) :
					scale(image, maxWidth, SCALE_WIDTH);
		}
		// both dimensions outisde of bounds
		// need to scale to the larger diff
		else if( widthDiff < 0 && heightDiff < 0 )
		{
			return widthDiff > heightDiff ?
					scale(image, maxHeight, SCALE_HEIGHT) :
					scale(image, maxWidth, SCALE_WIDTH);
		}
		
		// should never get here but let's default to height
		return scale(image, maxHeight, SCALE_HEIGHT);
	}
}
