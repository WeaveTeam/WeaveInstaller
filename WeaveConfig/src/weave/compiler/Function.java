package weave.compiler;

import weave.Globals;

public abstract class Function<ReturnType, ArgType> extends Globals
{
	@SuppressWarnings("unchecked")
	public abstract ReturnType call(ArgType ...arguments);
}
