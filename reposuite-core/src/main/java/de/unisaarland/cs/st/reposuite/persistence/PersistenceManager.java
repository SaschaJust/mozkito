/**
 * 
 */
package de.unisaarland.cs.st.reposuite.persistence;

import java.util.HashMap;
import java.util.Map;

import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class PersistenceManager {
	
	private static Class<PersistenceUtil>           middleware    = null;
	
	private static Map<String, Map<String, String>> nativeQueries = new HashMap<String, Map<String, String>>();
	
	/**
	 * @return
	 */
	public static Class<PersistenceUtil> getMiddleware() {
		return middleware;
	}
	
	/**
	 * @param util
	 * @param id
	 * @return
	 */
	public static String getNativeQuery(final PersistenceUtil util,
	                                    final String id) {
		if (nativeQueries.containsKey(util.getType()) && nativeQueries.get(util.getType()).containsKey(id)) {
			return nativeQueries.get(util.getType()).get(id);
		} else {
			return null;
		}
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
	
	public static String registerNativeQuery(final String type,
	                                         final String id,
	                                         final String query) {
		if (!nativeQueries.containsKey(type)) {
			nativeQueries.put(type, new HashMap<String, String>());
		}
		
		Map<String, String> map = nativeQueries.get(type);
		return map.put(id, query);
	}
}
