/**
 * 
 */
package de.unisaarland.cs.st.reposuite.persistence;

import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class PersistenceManager {
	
	private static Class<PersistenceUtil> middleware = null;
	
	/**
	 * @return
	 */
	public static Class<PersistenceUtil> getMiddleware() {
		return middleware;
	}
	
	/**
	 * @return
	 * @throws UninitializedDatabaseException 
	 */
	public static PersistenceUtil getUtil() throws UninitializedDatabaseException {
		try {
			return (PersistenceUtil) PersistenceManager.getMiddleware().getMethod("getInstance").invoke(null);
		} catch (Exception e) {
			throw new UninitializedDatabaseException(e);
		}
	}
	
	/**
	 * @param middleware
	 */
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
