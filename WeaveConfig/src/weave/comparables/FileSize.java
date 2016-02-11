package weave.comparables;

public class FileSize implements Comparable<FileSize>
{
	private Long size;
	
	public FileSize(long s)
	{
		size = s;
	}
	
	public Long getSize()
	{
		return size;
	}

	@Override
	public int compareTo(FileSize o)
	{
		long a = getSize();
		long b = o.getSize();
		if (a < b) return -1;
		if (a > b) return 1;
		return 0;
	}
}
