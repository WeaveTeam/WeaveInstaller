package weave.compiler;

import static weave.utils.TraceUtils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weave.Settings;
import weave.core.Function;
import weave.utils.ReflectionUtils;

public class Compiler
{
	public static final String IN_VARS 		= "VARS";
	public static final String IN_PARAMS 	= "PARAMS";
	public static final String IN_EXPR 		= "EXPR";		// Single expression
	public static final String IN_STMT 		= "STMT";		// Single or multiple expressions
	
	public static final String STMT_IMPORT 		= "import";
	public static final String STMT_VAR 		= "var";
	public static final String STMT_IF 			= "if";
	public static final String STMT_ELSE 		= "else";
	public static final String STMT_FOR 		= "for";
	public static final String STMT_FOREACH 	= "foreach";
	public static final String STMT_IN 			= "in";
	public static final String STMT_DO 			= "do";
	public static final String STMT_WHILE 		= "while";
	
	public static final String OP_ADD 			= "+";
	public static final String OP_SUB 			= "-";
	public static final String OP_MULT 			= "*";
	public static final String OP_DIV 			= "/";
	public static final String OP_MOD			= "%";
	public static final String OP_NEW 			= "new";
	public static final String OP_INSTANCEOF 	= "instanceof";
	
	public static final String OP_BITWISE_NOT 		= "~";
	public static final String OP_BITWISE_AND 		= "&";
	public static final String OP_BITWISE_OR 		= "|";
	public static final String OP_BITWISE_XOR 		= "^";
	public static final String OP_BITWISE_LSHIFT 	= "<<";
	public static final String OP_BITWISE_RSHIFT 	= ">>";
	
	public static final String OP_LOGICAL_NOT 		= "!";
	public static final String OP_LOGICAL_AND 		= "&&";
	public static final String OP_LOGICAL_OR 		= "||";
	public static final String OP_LOGICAL_LSHIFT 	= "<<<";
	public static final String OP_LOGICAL_RSHIFT 	= ">>>";
	
	public static final String OP_COMPARE_LT 		= "<";
	public static final String OP_COMPARE_GT 		= ">";
	public static final String OP_COMPARE_LTEQ 		= "<=";
	public static final String OP_COMPARE_GTEQ 		= ">=";
	public static final String OP_COMPARE_EQ 		= "==";
	public static final String OP_COMPARE_EQMC 		= "===";
	public static final String OP_COMPARE_NEQ 		= "!=";
	public static final String OP_COMPARE_NEQMC		= "!==";
	
	public static final String OP_ASSN_EQ			= "=";
	public static final String OP_ASSN_ADDEQ		= "+=";
	public static final String OP_ASSN_SUBEQ		= "-=";
	public static final String OP_ASSN_MULTEQ		= "*=";
	public static final String OP_ASSN_DIVEQ		= "/=";
	public static final String OP_ASSN_MODEQ		= "%=";
	public static final String OP_ASSN_BIT_LSHIFTEQ	= "<<=";
	public static final String OP_ASSN_BIT_RSHIFTEQ	= ">>=";
	public static final String OP_ASSN_BIT_NOTEQ	= "~=";
	public static final String OP_ASSN_BIT_ANDEQ	= "&=";
	public static final String OP_ASSN_BIT_OREQ		= "|=";
	public static final String OP_ASSN_BIT_XOREQ	= "^=";
	public static final String OP_ASSN_LOG_LSHIFTEQ	= "<<<=";
	public static final String OP_ASSN_LOG_RSHIFTEQ	= ">>>=";

	public static final String OP_SPACE			= " ";
	public static final String OP_OPEN_PAREN	= "(";
	public static final String OP_CLOSE_PAREN	= ")";
	public static final String OP_OPEN_ARRAY	= "[";
	public static final String OP_CLOSE_ARRAY	= "]";
	public static final String OP_OPEN_BRACKET	= "{";
	public static final String OP_CLOSE_BRACKET	= "}";
	public static final String OP_QUOTE			= "\"";
	public static final String OP_COMMA			= ",";
	public static final String OP_SEMICOLON		= ";";
	
	private static final String[] TOKEN_ARRAY = {
		STMT_IMPORT, STMT_VAR, STMT_IF, STMT_ELSE, STMT_FOR, STMT_FOREACH, STMT_IN, STMT_DO, STMT_WHILE,
		// Arithmetic
		OP_ADD, OP_SUB, OP_MULT, OP_DIV, OP_MOD, OP_NEW, OP_INSTANCEOF,
		// Bitwise
		OP_BITWISE_NOT, OP_BITWISE_AND, OP_BITWISE_OR, OP_BITWISE_XOR, OP_BITWISE_LSHIFT, OP_BITWISE_RSHIFT,
		// Logical
		OP_LOGICAL_NOT, OP_LOGICAL_AND, OP_LOGICAL_OR, OP_LOGICAL_LSHIFT, OP_LOGICAL_RSHIFT,
		// Comparison
		OP_COMPARE_EQ, OP_COMPARE_EQMC, OP_COMPARE_GT, OP_COMPARE_GTEQ, OP_COMPARE_LT, OP_COMPARE_LTEQ, OP_COMPARE_NEQ, OP_COMPARE_NEQMC,
		// Assignment
		OP_ASSN_EQ, OP_ASSN_ADDEQ, OP_ASSN_SUBEQ, OP_ASSN_MULTEQ, OP_ASSN_DIVEQ, OP_ASSN_MODEQ, OP_ASSN_BIT_LSHIFTEQ, OP_ASSN_BIT_RSHIFTEQ,
		OP_ASSN_BIT_NOTEQ, OP_ASSN_BIT_ANDEQ, OP_ASSN_BIT_OREQ, OP_ASSN_BIT_XOREQ, OP_ASSN_LOG_LSHIFTEQ, OP_ASSN_LOG_RSHIFTEQ,
		// Symbols
		OP_SPACE, OP_OPEN_PAREN, OP_CLOSE_PAREN, OP_OPEN_ARRAY, OP_CLOSE_ARRAY, OP_OPEN_BRACKET, OP_CLOSE_BRACKET, OP_QUOTE, OP_COMMA, OP_SEMICOLON
	};
	
	@SuppressWarnings("unused")
	private static final String[] VARS_SEPARATORS = {
		OP_COMMA
	};
	@SuppressWarnings("unused")
	private static final String[] PARAMS_SEPARATORS = {
		OP_COMMA
	};
	@SuppressWarnings("unused")
	private static final String[] STMT_SEPARATORS = {
		OP_SEMICOLON
	};
	@SuppressWarnings("unused")
	private static final String[] EXPR_SEPARATORS = {
		OP_ASSN_EQ, OP_SEMICOLON
	};
	@SuppressWarnings("unused")
	private static final String[] TOKEN_SEPARATORS = {
		OP_SPACE, OP_OPEN_PAREN, OP_OPEN_ARRAY, OP_OPEN_BRACKET
	};
	
	public static final String[][] STMT_PATTERNS = {
		{STMT_IMPORT, IN_EXPR},
		{STMT_VAR, IN_VARS},
		{STMT_IF, IN_PARAMS, IN_STMT},
		{STMT_IF, IN_PARAMS, IN_STMT, STMT_ELSE, IN_STMT},
		{STMT_FOR, IN_PARAMS, IN_STMT},
		{STMT_FOREACH, IN_PARAMS, IN_STMT},
		{STMT_WHILE, IN_PARAMS, IN_STMT},
		{STMT_DO, IN_STMT, STMT_WHILE, IN_PARAMS}
	};
	
	public static final String[][] EXPR_PATTERNS = {
		{}
	};
	
	public static final String[] OP_PRECEDENCE = {
		OP_BITWISE_NOT, OP_LOGICAL_NOT, OP_NEW,
		OP_MULT, OP_DIV, OP_MOD,
		OP_ADD, OP_SUB,
		OP_BITWISE_LSHIFT, OP_BITWISE_RSHIFT, OP_LOGICAL_LSHIFT, OP_LOGICAL_RSHIFT,
		OP_COMPARE_LT, OP_COMPARE_LTEQ, OP_COMPARE_GT, OP_COMPARE_GTEQ,
		OP_INSTANCEOF,
		OP_COMPARE_EQ, OP_COMPARE_EQMC, OP_COMPARE_NEQ, OP_COMPARE_NEQMC,
		OP_BITWISE_AND, OP_BITWISE_XOR, OP_BITWISE_OR,
		OP_LOGICAL_AND, OP_LOGICAL_OR,
		OP_ASSN_EQ, OP_ASSN_MULTEQ, OP_ASSN_DIVEQ, OP_ASSN_ADDEQ, OP_ASSN_SUBEQ, OP_ASSN_MODEQ,
		OP_ASSN_BIT_ANDEQ, OP_ASSN_BIT_NOTEQ, OP_ASSN_BIT_XOREQ, OP_ASSN_BIT_OREQ, OP_ASSN_BIT_LSHIFTEQ, OP_ASSN_BIT_RSHIFTEQ,
		OP_ASSN_LOG_LSHIFTEQ, OP_ASSN_LOG_RSHIFTEQ
	};
	
	public Map<String, Function<Object, Object>> OPERATIONS = null;
	
	public static void main(String ...args)
	{
		Settings.init();
		
		Compiler compiler = new Compiler();
		System.out.println("\nAdded: " + compiler.OPERATIONS.get(OP_ADD).call(-4));
		System.out.println("Subtract: " + compiler.OPERATIONS.get(OP_SUB).call(10));
		System.out.println("Multiply: " + compiler.OPERATIONS.get(OP_MULT).call(2, 4));
		System.out.println("Divide: " + compiler.OPERATIONS.get(OP_DIV).call(94, 3));
		System.out.println("Modulo: " + compiler.OPERATIONS.get(OP_MOD).call(11, 3));
		System.out.println("Instanceof: " + compiler.OPERATIONS.get(OP_INSTANCEOF).call(32.43, Object.class));
	}
	
	public Compiler()
	{
		initialize();
	}
	
	public List<Token> parseTokens(String expr)
	{
		int len = expr.length();
		List<Token> tokenList = new ArrayList<Token>();
		
		for( int i = 0; i < len; i++ )
			tokenList.add(parseToken(expr, i));
		
		return tokenList;
	}
	
	public Token parseToken(String expr, int index)
	{
		return null;
	}
	
	public Boolean isToken(String str)
	{
		List<String> possibleTokens = new ArrayList<String>();
		for( int i = 0; i < TOKEN_ARRAY.length; i++ )
			if( TOKEN_ARRAY[i].contains(str) )
				possibleTokens.add(TOKEN_ARRAY[i]);
		return possibleTokens.size() == 1;
	}
	
	private Number numVal(Number n)
	{
		if( n.doubleValue() % 1 == 0 )
			return n.intValue();
		return n.doubleValue();
	}
	private Number absNumVal(Number n, int sign)
	{
		if( n.doubleValue() % 1 == 0 )
			return Math.abs(n.intValue()) * sign;
		return Math.abs(n.doubleValue()) * sign;
	}
	
	public void initialize()
	{
		OPERATIONS = new HashMap<String, Function<Object, Object>>();
		OPERATIONS.put(OP_ADD, new Function<Object, Object>() {
			@Override public Object call(Object ...arguments) {
				Number d = 0;
				if( arguments.length == 1 )
				{
					d = (Number) arguments[0];
					return absNumVal(d, 1);
				}
				else if( arguments.length == 2 )
				{
					Number  x = (Number) arguments[0],
							y = (Number) arguments[1];
					d = x.doubleValue() + y.doubleValue();
					return numVal(d);
				}
				else return d;
			}
		});
		OPERATIONS.put(OP_SUB, new Function<Object, Object>() {
			@Override public Object call(Object ...arguments) {
				Number d = 0;
				if( arguments.length == 1 )
				{
					d = (Number) arguments[0];
					return absNumVal(d, -1);
				}
				else if( arguments.length == 2 )
				{
					Number  x = (Number) arguments[0],
							y = (Number) arguments[1];
					d = x.doubleValue() - y.doubleValue();
					return numVal(d);
				}
				else return d;
			}
		});
		OPERATIONS.put(OP_MULT, new Function<Object, Object>() {
			@Override public Object call(Object ...arguments) {
				Number d = 0;
				if( arguments.length == 2 )
				{
					Number  x = (Number) arguments[0],
							y = (Number) arguments[1];
					d = x.doubleValue() * y.doubleValue();
					return numVal(d);
				}
				else return d;
			}
		});
		OPERATIONS.put(OP_DIV, new Function<Object, Object>() {
			@Override public Object call(Object ...arguments) {
				Number d = 0;
				if( arguments.length == 2 )
				{
					Number  x = (Number) arguments[0],
							y = (Number) arguments[1];
					d = x.doubleValue() / y.doubleValue();
					return numVal(d);
				}
				else return d;
			}
		});
		OPERATIONS.put(OP_MOD, new Function<Object, Object>() {
			@Override public Object call(Object ...arguments) {
				Number d = 0;
				if( arguments.length == 2 )
				{
					Number  x = (Number) arguments[0],
							y = (Number) arguments[1];
					d = x.doubleValue() % y.doubleValue();
					return numVal(d);
				}
				else return d;
			}
		});
		OPERATIONS.put(OP_NEW, new Function<Object, Object>() {
			@Override public Object call(Object ...arguments) {
				String p = (String)arguments[0];
				String c = (String)arguments[1];
				Object[] a = new Object[] {};
				Class<?>[] l = new Class<?>[] {};
				
				try {
					a = Arrays.copyOfRange(arguments, 2, arguments.length);
					l = new Class<?>[a.length];
					for( int i = 0; i < a.length; i++ )
						l[i] = a[i].getClass();
				} catch(ArrayIndexOutOfBoundsException e) {
					// Not an error, just nullary constructor
				}
				
				try {
					return ReflectionUtils.reflectConstructor(p, c, l, a);
				} catch (Exception e) {
					trace(STDERR, e);
				}
				return null;
			}
		});
		OPERATIONS.put(OP_INSTANCEOF, new Function<Object, Object>() {
			@Override public Object call(Object ...arguments) {
				Boolean b = false;
				if( arguments.length == 2 )
				{
					Object x = arguments[0];
					Class<?> y = (Class<?>) arguments[1];
					
					b = y.isInstance(x);
				}
				return b;
			}
		});
	}
}
