package weave.reflect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import weave.utils.ReflectionUtils;

/**
 * Indicates to the {@link ReflectionUtils} that a method or field can 
 * be reflected.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
public @interface Reflectable 
{
	
}
