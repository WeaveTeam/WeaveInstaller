package weave.compiler;

public class Token 
{

}

class VarsToken extends Token
{
	
}

class ParamsToken extends Token
{
	
}

class ExprToken extends Token
{
	
}

class StmtToken extends ExprToken
{
	
}

class LiteralToken extends Token
{
	Object value = null;
	
	public LiteralToken(String s)
	{
		value = s;
	}
	public LiteralToken(Number n)
	{
		value = n;
	}
}