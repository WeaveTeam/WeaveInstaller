package weave.misc;

public abstract class Function
{
	protected Object[] arguments = null;
	
	public Function()
	{
		
	}
	
	public void call()
	{
		arguments = new Object[0];
		run();
	}
	public void call(Object[] args)
	{
		arguments = args;
		run();
	}
	
	public abstract void run();
}
