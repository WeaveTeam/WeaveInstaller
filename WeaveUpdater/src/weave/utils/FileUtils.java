package weave.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils
{
	public static void copy(String source, String destination)
		throws IOException
	{
		copy(new File(source), new File(destination));
	}

	public static void copy(File source, File destination)
		throws IOException
	{
		InputStream in = new FileInputStream(source);
		OutputStream out = new FileOutputStream(destination);
		copy(in, out);
	}

	public static void copy(InputStream in, OutputStream out)
		throws IOException
	{
		byte[] buffer = new byte[4096];
		int length;
		while ((length = in.read(buffer)) > 0)
		{
			out.write(buffer, 0, length);
		}
		in.close();
		out.close();
	}
}
