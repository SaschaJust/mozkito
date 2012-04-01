/**
 * 
 */
package net.ownhero.dev.andama.exceptions;

import org.junit.Test;

/**
 * @author just
 * 
 */
public class ClassLoadingErrorTest {
	
	@Test
	public void testRecommendations() {
		final String className = "bleh.Blub";
		try {
			try {
				Class.forName(className);
			} catch (final ClassNotFoundException e) {
				throw new ClassLoadingError(e, className);
			}
		} catch (final ClassLoadingError e) {
			System.err.println(e.analyzeFailureCause());
		}
	}
	
	@Test
	public void testStaticErrors() {
		final String className = "net.ownhero.dev.andama.exceptions.Blub";
		try {
			try {
				Class.forName(className);
			} catch (final ClassNotFoundException e) {
				throw new ClassLoadingError(e, className);
			} catch (final ExceptionInInitializerError e) {
				throw new ClassLoadingError(e, className);
			}
		} catch (final ClassLoadingError e) {
			System.err.println(e.analyzeFailureCause());
		}
	}
	
}
