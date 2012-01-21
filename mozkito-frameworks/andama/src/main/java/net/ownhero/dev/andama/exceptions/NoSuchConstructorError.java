/**
 * 
 */
package net.ownhero.dev.andama.exceptions;

import java.lang.reflect.Constructor;

/**
 * @author just
 * 
 */
public class NoSuchConstructorError extends UnrecoverableError {
	
	public NoSuchConstructorError(final String message, final Throwable t, final Constructor<?> constructor,
	        final Object... arguments) {
		// TODO Auto-generated constructor stub
	}
	
	public NoSuchConstructorError(final Throwable t, final Constructor<?> constructor, final Object... arguments) {
		// TODO Auto-generated constructor stub
	}
}
