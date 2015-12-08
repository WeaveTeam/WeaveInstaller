package weave.compiler;

public class Token 
{
	public static final int TYPE_EOF = -1;
	public static final int TYPE_VALUE = 0;
	public static final int TYPE_VARIABLE = (1 << 0);
	
	public int type = 0;
	public Object value = null;
	
	public Token(int type, Object value)
	{
		this.type = type;
		this.value = value;
	}
	
	@Override
	public String toString() 
	{
		StringBuffer sb = new StringBuffer();
		
		switch (type) {
		case TYPE_VALUE:
			sb.append("VALUE(").append(value).append(")");
			break;
		case TYPE_VARIABLE:
			sb.append("VARIABLE(").append(value).append(")");
			break;
		case TYPE_EOF:
			sb.append("END OF FILE");
			break;
		}
		return sb.toString();
	}
}
