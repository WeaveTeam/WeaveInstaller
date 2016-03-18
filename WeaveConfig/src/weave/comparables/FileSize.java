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
		return Long.compare(getSize(), o.getSize());
	}
}
