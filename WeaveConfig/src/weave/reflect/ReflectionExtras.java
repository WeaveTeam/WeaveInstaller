package weave.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import weave.utils.ObjectUtils;

public class ReflectionExtras 
{
	@Reflectable
	public static String getAllMethods(String pkg, String clazz) throws ClassNotFoundException
	{
		Annotation[] annotations = null;
		StringBuilder sb = new StringBuilder();
		Class<?> c = Class.forName(pkg + "." + clazz);
		Class<?> interfaces[] = c.getInterfaces();
	
		while( c != null )
		{
			annotations = c.getAnnotations();
			sb.append(ObjectUtils.toString(annotations, ", ") + " ");
			
			sb.append(c.getName() + "\n[\n");
			Method[] methods = c.getDeclaredMethods();
			for( int i = 0; i < methods.length; i++ )
			{
				annotations = methods[i].getAnnotations();
				sb.append("\t" + ObjectUtils.toString(annotations, " ") + " ");
				sb.append(methods[i].toString() + "\n");
			}
			sb.append("]\n\n");
			
			interfaces = c.getInterfaces();
			for( int i = 0; i < interfaces.length; i++ ) {
				sb.append("Canonical: " + interfaces[i].getCanonicalName() + "\n");
				sb.append("[\n");
				Method[] imethods = interfaces[i].getDeclaredMethods();
				for( int j = 0; j < imethods.length; j++ ) {
					annotations = imethods[j].getAnnotations();
					sb.append("\t[ ");
					for( int k = 0; k < annotations.length; k++ )
						sb.append(annotations[k] + " ");
					sb.append("] ");
					sb.append(imethods[j].toString() + "\n");
				}
				sb.append("]\n\n");
			}
			
			c = c.getSuperclass();
		}
		
		return sb.toString();
	}

	@Reflectable
	public static String getAllFields(String pkg, String clazz) throws ClassNotFoundException
	{
		Annotation[] annotations = null;
		StringBuilder sb = new StringBuilder();
		Class<?> c = Class.forName(pkg + "." + clazz);
		Class<?> interfaces[] = c.getInterfaces();
		
		while( c != null )
		{
			annotations = c.getAnnotations();
			sb.append("[ ");
			for( int i = 0; i < annotations.length; i++ )
				sb.append(annotations[i] + " ");
			sb.append("] ");
			
			sb.append(c.getName() + "\n[\n");
			Field[] field = c.getDeclaredFields();
			for( int i = 0; i < field.length; i++ )
			{
				annotations = field[i].getAnnotations();
				sb.append("\t[ ");
				for( int j = 0; j < annotations.length; j++ ) 
					sb.append(annotations[j] + " ");
				sb.append("] ");
				sb.append(field[i].toString() + "\n");
			}
			sb.append("]\n\n");
			
			interfaces = c.getInterfaces();
			for( int i = 0; i < interfaces.length; i++ ) {
				sb.append("Canonical: " + interfaces[i].getCanonicalName() + "\n");
				sb.append("[\n");
				Field[] ifield = interfaces[i].getDeclaredFields();
				for( int j = 0; j < ifield.length; j++ ) {
					annotations = ifield[j].getAnnotations();
					sb.append("\t[ ");
					for( int k = 0; k < annotations.length; k++ )
						sb.append(annotations[k] + " ");
					sb.append("] ");
					sb.append(ifield[j].toString() + "\n");
				}
				sb.append("]\n\n");
			}
			
			c = c.getSuperclass();
		}
		
		return sb.toString();
	}
}
