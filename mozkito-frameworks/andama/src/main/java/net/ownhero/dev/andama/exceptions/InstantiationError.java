/**
 * 
 */
package net.ownhero.dev.andama.exceptions;

import java.lang.reflect.Constructor;

/**
 * @author just
 * 
 */
public class InstantiationError extends UnrecoverableError {
	
	public InstantiationError(final String message, final Throwable t, final Class<?> clazz,
	        final Constructor<?> constructor, final Object... arguments) {
		// TODO Auto-generated constructor stub
	}
	
	public InstantiationError(final Throwable t, final Class<?> clazz, final Constructor<?> constructor,
	        final Object... arguments) {
		// TODO Auto-generated constructor stub
	}
}
