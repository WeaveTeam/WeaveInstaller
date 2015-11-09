package weave.compiler;

public class Promise
{
	protected Object result = null;
	protected Object error = null;
	protected Handler handler = null;
	
//	public static void main(String ...args)
//	{
//		Function<Object, Object> f1 = new Function<Object, Object>() {
//			@Override
//			public Object call(Object... arguments) {
//				return null;
//			}
//		};
//		Function<Object, Object> f2 = new Function<Object, Object>() {
//			@Override
//			public Object call(Object... arguments) {
//				return null;
//			}
//		};
//		Function<Object, Object> f3 = new Function<Object, Object>() {
//			@Override
//			public Object call(Object... arguments) {
//				return null;
//			}
//		};
//		Function<Object, Object> error = new Function<Object, Object>() {
//			@Override
//			public Object call(Object... arguments) {
//				return null;
//			}
//		};
//		Promise promise = new Promise(new Function<Object, Object>() {
//			@Override
//			public Object call(Object... arguments) {
//				return null;
//			}
//		});
//		
//		promise
//			.then(f1, error)
//			.then(f2, error)
//			.then(f3, error);
//	}
	
	public Promise(Function<Object, Object> resolver)
	{
		if( resolver != null )
			resolver.call(this.resolve, this.reject);
	}

	private static Function<Object, Object> noop = new Function<Object, Object>() {
		@Override
		public Object call(Object... arguments) {
			return arguments;
		}
	};
	
	public Function<Object, Object> resolve = new Function<Object, Object>() {
		@Override
		public Object call(Object... arguments) 
		{
			Object result = null;
			Promise.this.result = null;
			Promise.this.error = null;
			
			if( arguments.length == 1 )
				result = arguments[0];
			
			if( result instanceof Promise )
				((Promise) result).then(Promise.this.resolve, Promise.this.reject);
			else {
				Promise.this.result = result;
				callHandler.call();
			}
			return null;
		}
	};
	
	public Function<Object, Object> reject = new Function<Object, Object>() {
		@Override
		public Object call(Object... arguments)
		{
			Object error = null;
			Promise.this.result = null;
			Promise.this.error = null;
			
			if( arguments.length == 1 )
				error = arguments[0];
			
			Promise.this.error = error;
			callHandler.call();
			return null;
		}
	};
	
	private Function<Object, Object> callHandler = new Function<Object, Object>() {
		@Override
		public Object call(Object... arguments) {
			if( Promise.this.result != null )
				Promise.this.handler.onResult(Promise.this.result);
			else if( Promise.this.error != null )
				Promise.this.handler.onError(Promise.this.error);
			return null;
		}
	};
	
	public Promise then(Function<Object, Object> onFulfilled)
	{
		return then(onFulfilled, null);
	}

	public Promise then(Function<Object, Object> onFulfilled, Function<Object, Object> onRejected)
	{
		if( onFulfilled == null )
			onFulfilled = noop;
		if( onRejected == null )
			onRejected = noop;
		
//		Promise next = new Promise(this);
//		handler = new Handler(next, onFulfilled, onRejected);
//		return next;
		return null;
	}
}

class Handler
{
	private Promise next = null;
	private Function<Object, Object> onFulfilled = null;
	private Function<Object, Object> onRejected = null;

	public Handler(Promise next, Function<Object, Object> onFulfilled, Function<Object, Object> onRejected) 
	{
		this.next = next;
		this.onFulfilled = onFulfilled;
		this.onRejected = onRejected;
	}
	
	public void onResult(Object result)
	{
		next.resolve.call(onFulfilled.call(result));
	}
	
	public void onError(Object error)
	{
		next.reject.call(onRejected.call(error));
	}
}
