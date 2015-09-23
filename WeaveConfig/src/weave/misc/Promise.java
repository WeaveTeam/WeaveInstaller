package weave.misc;

public class Promise
{
	public Promise(Function func)
	{
		if (func != null)
			func.call();
	}
	
	private Function handle = new Function() {
		@SuppressWarnings("unused")
		@Override
		public Object call(Object... args) {
			Function onResolved = (Function) args[0];
			Function onRejected = (Function) args[1];
			Function resolve = (Function) args[2];
			Function reject = (Function) args[3];
			return null;
		}
	};
	
	private Function resolve = new Function() {
		@Override
		public Object call(Object... args) {
			return args;
		}
	};
	private Function reject = new Function() {
		@Override
		public Object call(Object... args) {
			return args;
		}
	};
	
	public Promise resolve(final Object value)
	{
		return new Promise(new Function() {
			@Override public Object call(Object... args) {
				return resolve.call(value);
			}
		});
	}
	
	public Promise reject(final Object value)
	{
		return new Promise(new Function() {
			@Override public Object call(Object... args) {
				return reject.call(value);
			}
		});
	}
	
	public Promise then(final Function onResolved, final Function onRejected)
	{
		return new Promise(new Function() {
			@Override public Object call(Object... args) {
				return handle.call(onResolved, onRejected, resolve, reject);
			}
		});
	}
}
