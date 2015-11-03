
package weave.core;

public class Promise
{
	protected Object result = null;
	protected Object error = null;
	protected Handler handler = null;
	protected Promise relevantContext = null;
	
	public static void main(String ...args)
	{
		Function<Object, Object> f1 = new Function<Object, Object>() {
			@Override
			public Object call(Object... arguments) {
				String s = "in f1 coming from (" + arguments.toString() + ")";
				System.out.println(s);
				return s;
			}
		};
		Function<Object, Object> f2 = new Function<Object, Object>() {
			@Override
			public Object call(Object... arguments) {
				String s = "in f2 coming from (" + arguments.toString() + ")";
				System.out.println(s);
				return s;
			}
		};
		Function<Object, Object> f3 = new Function<Object, Object>() {
			@Override
			public Object call(Object... arguments) {
				String s = "in f3 coming from (" + arguments.toString() + ")";
				System.out.println(s);
				return s;
			}
		};
		Function<Object, Object> error = new Function<Object, Object>() {
			@Override
			public Object call(Object... arguments) {
				System.out.println("error");
				return "in error";
			}
		};
		
		new Promise(f1)
			.then(f2, error)
			.then(f3, error);
	}
	
	private static Function<Object, Object> noop = new Function<Object, Object>() {
		@Override public Object call(Object... arguments) { return arguments; }
	};
	
	public Promise(Promise context)
	{
		this(context, null);
	}
	
	public Promise(Function<Object, Object> resolver)
	{
		this(null, resolver);
	}
	
	public Promise(Promise context, Function<Object, Object> resolver)
	{
		if( context != null )
			this.relevantContext = context;
		
		if( resolver == null )
			this.setResult.call(this.relevantContext);
		else
			resolver.call(this.setResult, this.setError);
	}
	
	public Function<Object, Object> setResult = new Function<Object, Object>() {
		@Override
		public Object call(Object... arguments) 
		{
			Object result = ( arguments.length == 1 ) ? arguments[0] : null;
			Promise.this.result = null;
			Promise.this.error = null;
			
			if( result != null && result instanceof Promise )
				((Promise) result).then(Promise.this.setResult, Promise.this.setError);
			else {
				Promise.this.result = result;
				callHandler.call();
			}
			return null;
		}
	};
	
	public Function<Object, Object> setError = new Function<Object, Object>() {
		@Override
		public Object call(Object... arguments)
		{
			Object error = ( arguments.length == 1 ) ? arguments[0] : null;
			Promise.this.result = null;
			Promise.this.error = null;
			
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

	public Promise then(Function<Object, Object> onFulfilled, Function<Object, Object> onRejected)
	{
		if( onFulfilled == null )
			onFulfilled = noop;
		if( onRejected == null )
			onRejected = noop;
		
		Promise next = new Promise(this);
		handler = new Handler(next, onFulfilled, onRejected);
		return next;
	}
	
	public Promise done(Function<Object, Object> onFulfilled)
	{
		return then(onFulfilled, null);
	}
	
	public Promise fail(Function<Object, Object> onRejected)
	{
		return then(null, onRejected);
	}
	
	protected class Handler
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
			next.setResult.call(onFulfilled.call(result));
		}
		
		public void onError(Object error)
		{
			next.setError.call(onRejected.call(error));
		}
	}
}