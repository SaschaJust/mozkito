/**
 * 
 */
package de.unisaarland.cs.st.reposuite.persistence;

import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class PersistenceManager {
	
	private static Class<PersistenceUtil> middleware = null;
	
	public static Class<PersistenceUtil> getMiddleware() {
		return middleware;
	}
	
	public static void registerMiddleware(final Class<PersistenceUtil> middleware) {
		if (PersistenceManager.middleware == null) {
			PersistenceManager.middleware = middleware;
		} else {
			if (Logger.logWarn()) {
				Logger.warn("Cannot register middleware " + middleware.getCanonicalName()
				        + " because a middleware is already registered: "
				        + PersistenceManager.middleware.getCanonicalName());
			}
		}
	}
}
