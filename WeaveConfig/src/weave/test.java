package weave;

import weave.utils.DownloadUtils;
import weave.utils.FileUtils;
import weave.utils.TimeUtils;

public class test 
{
	public static void main(String args[])
	{
		System.out.println("Time: " + TimeUtils.format("%m:%ss s", 60*2+5));
		System.out.println("Speed: " + DownloadUtils.speedify(24.84 * DownloadUtils.MB));
		System.out.println("Size: " + FileUtils.sizeify(545.4584 * 1024*1024));
	}
}
