
package weave.core;

public abstract class Function<ReturnType, ArgType>
{
	@SuppressWarnings("unchecked")
	public abstract ReturnType call(ArgType ...arguments);
}
