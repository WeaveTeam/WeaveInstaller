package weave.misc;

public abstract class Function
{
	protected Object[] arguments = null;
	
	public Function()
	{
		
	}
	
	public Object call()
	{
		arguments = new Object[0];
		return run();
	}
	public Object call(Object[] args)
	{
		arguments = args;
		return run();
	}
	
	public abstract Object run();
}
