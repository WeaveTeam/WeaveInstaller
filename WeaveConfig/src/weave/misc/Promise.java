package weave.misc;

public class Promise
{
	public Promise(Function<Object, Object> func)
	{
		if (func != null)
			func.call();
	}
	
	private Function<Object, Object> handle = new Function<Object, Object>() {
		@Override
		public Object call(Object... args) {
//			Function onResolved = (Function) args[0];
//			Function onRejected = (Function) args[1];
//			Function resolve = (Function) args[2];
//			Function reject = (Function) args[3];
			return null;
		}
	};
	
	private Function<Object, Object> resolve = new Function<Object, Object>() {
		@Override
		public Object call(Object... args) {
			return args;
		}
	};
	private Function<Object, Object> reject = new Function<Object, Object>() {
		@Override
		public Object call(Object... args) {
			return args;
		}
	};
	
	public Promise resolve(final Object value)
	{
		return new Promise(new Function<Object, Object>() {
			@Override public Object call(Object... args) {
				return resolve.call(value);
			}
		});
	}
	
	public Promise reject(final Object value)
	{
		return new Promise(new Function<Object, Object>() {
			@Override public Object call(Object... args) {
				return reject.call(value);
			}
		});
	}
	
	public Promise then(final Function<Object, Object> onResolved, final Function<Object, Object> onRejected)
	{
		return new Promise(new Function<Object, Object>() {
			@Override public Object call(Object... args) {
				return handle.call(onResolved, onRejected, resolve, reject);
			}
		});
	}
}
