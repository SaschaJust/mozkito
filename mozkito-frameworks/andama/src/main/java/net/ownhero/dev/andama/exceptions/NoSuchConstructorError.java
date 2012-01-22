/**
 * 
 */
package net.ownhero.dev.andama.exceptions;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import net.ownhero.dev.andama.utils.AndamaUtils;

/**
 * @author just
 * 
 */
public class NoSuchConstructorError extends UnrecoverableError {
	
	private static final long serialVersionUID = -8912323102598389733L;
	static private String     defaultMessage   = "";
	private final Object[]    arguments;
	private final Class<?>    clazz;
	
	/**
	 * @param t
	 * @param constructor
	 * @param arguments
	 */
	public NoSuchConstructorError(final NoSuchMethodException t, final Class<?> clazz, final Object... arguments) {
		this(defaultMessage, t, clazz, arguments);
	}
	
	/**
	 * @param message
	 * @param t
	 * @param constructor
	 * @param arguments
	 */
	public NoSuchConstructorError(final String message, final NoSuchMethodException t, final Class<?> clazz,
	        final Object... arguments) {
		this.clazz = clazz;
		this.arguments = arguments;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * net.ownhero.dev.andama.exceptions.UnrecoverableError#analyzeFailureCause
	 * ()
	 */
	@Override
	public String analyzeFailureCause() {
		final StringBuilder builder = new StringBuilder();
		builder.append("The class ").append(getClazz().getCanonicalName()).append(" does not have a constructor: ")
		       .append(getClazz().getSimpleName()).append('(');
		
		builder.append(')').append(AndamaUtils.lineSeparator);
		if ((getClazz().getModifiers() & Modifier.ABSTRACT) != 0) {
			builder.append("The class is abstract.");
		} else if (getClazz().isInterface()) {
			builder.append("The class is an interface.");
		} else if (getClazz().isAnnotation()) {
			builder.append("The class is an annotation.");
		} else {
			for (final Constructor<?> constructor : getClazz().getConstructors()) {
				final int modifiers = constructor.getModifiers();
				builder.append("   ");
				
				builder.append(getModifierString(modifiers));
				
				builder.append(getClazz().getSimpleName()).append('(');
				StringBuilder sb = new StringBuilder();
				
				for (final Type type : constructor.getGenericParameterTypes()) {
					if (sb.length() > 0) {
						sb.append(", ");
					}
					sb.append(type);
				}
				builder.append(sb);
				builder.append(")");
				sb = new StringBuilder();
				for (final Type exceptionType : constructor.getGenericExceptionTypes()) {
					if (sb.length() > 0) {
						sb.append(", ");
					} else {
						sb.append(" throws ");
					}
					sb.append(exceptionType);
				}
				builder.append(sb);
				
				builder.append(';').append(AndamaUtils.lineSeparator);
			}
			
		}
		return builder.append(AndamaUtils.lineSeparator).toString();
	}
	
	/**
	 * @return
	 */
	public Object[] getArguments() {
		return this.arguments;
	}
	
	/**
	 * @return
	 */
	public Class<?> getClazz() {
		return this.clazz;
	}
	
	private String getModifierString(final int modifiers) {
		final StringBuilder builder = new StringBuilder();
		
		if ((modifiers & Modifier.PRIVATE) != 0) {
			builder.append("private ");
		} else if ((modifiers & Modifier.PUBLIC) != 0) {
			builder.append("public ");
		} else if ((modifiers & Modifier.PROTECTED) != 0) {
			builder.append("static ");
		}
		
		if ((modifiers & Modifier.STATIC) != 0) {
			builder.append("static ");
		} else if ((modifiers & Modifier.FINAL) != 0) {
			builder.append("final ");
		}
		
		if ((modifiers & Modifier.ABSTRACT) != 0) {
			builder.append("abstract ");
		}
		
		if ((modifiers & Modifier.SYNCHRONIZED) != 0) {
			builder.append("synchronized ");
		}
		
		if ((modifiers & Modifier.VOLATILE) != 0) {
			builder.append("volatile ");
		}
		
		if ((modifiers & Modifier.TRANSIENT) != 0) {
			builder.append("transient ");
		}
		
		if ((modifiers & Modifier.NATIVE) != 0) {
			builder.append("native ");
		}
		
		return builder.toString();
	}
}
