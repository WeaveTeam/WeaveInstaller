
package weave.core;

public abstract class Function<ReturnType, ArgType>
{
	public abstract ReturnType call(ArgType ...arguments);
}
