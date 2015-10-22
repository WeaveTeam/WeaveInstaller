package weave.compiler;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class Console 
{
	private Map<String, String> includes = null;
	private Map<String, Object> ram = null;
	private List<String> calls = null;
	
	private Compiler compiler = null;
	
	public Console() 
	{
		compiler = new Compiler();
	}
	
	public static Console _instance = null;
	public static Console getConsole()
	{
		if( _instance == null )
			_instance = new Console();
		return _instance;
	}
	
	public void exec(String script)
	{
		compiler.parseTokens(script);
		
	}
}