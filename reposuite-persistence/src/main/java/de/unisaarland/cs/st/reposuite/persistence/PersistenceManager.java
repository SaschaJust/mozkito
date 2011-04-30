/**
 * 
 */
package de.unisaarland.cs.st.reposuite.persistence;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class PersistenceManager {
	
	private static Class<PersistenceUtil>                  middleware    = null;
	
	private static Map<String, Map<String, String>>        nativeQueries = new HashMap<String, Map<String, String>>();
	private static Map<Class<?>, Map<String, Criteria<?>>> storedQueries = new HashMap<Class<?>, Map<String, Criteria<?>>>();
	
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
		String databaseType = util.getType().toLowerCase();
		
		if (nativeQueries.containsKey(databaseType) && nativeQueries.get(databaseType).containsKey(id)) {
			return nativeQueries.get(databaseType).get(id);
		} else {
			return null;
		}
	}
	
	/**
	 * @param id
	 * @return
	 */
	@SuppressWarnings ("unchecked")
	public static <T> Criteria<T> getStoredQuery(final String id,
	                                             final Class<T> clazz) {
		return (Criteria<T>) storedQueries.get(clazz).get(id);
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
	
	/**
	 * @param type
	 * @param id
	 * @param query
	 * @return
	 */
	public static String registerNativeQuery(final String type,
	                                         final String id,
	                                         final String query) {
		String databaseType = type.toLowerCase();
		if (!nativeQueries.containsKey(databaseType)) {
			nativeQueries.put(databaseType, new HashMap<String, String>());
		}
		
		Map<String, String> map = nativeQueries.get(databaseType);
		return map.put(id, query);
	}
	
	/**
	 * @param <T>
	 * @param query
	 */
	public static <T> void registerPreparedQuery(final PreparedQuery<T> query) {
		
	}
	
	/**
	 * @param id
	 * @param query
	 */
	@SuppressWarnings ("unchecked")
	public static <T> void registerQuery(final String id,
	                                     final Criteria<T> criteria) {
		Type actualTypeArgument = ((ParameterizedType) criteria.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		Class<T> actualRawTypeArgument = null;
		if (actualTypeArgument instanceof ParameterizedType) {
			actualRawTypeArgument = (Class<T>) ((ParameterizedType) actualTypeArgument).getRawType();
		} else {
			actualRawTypeArgument = (Class<T>) actualTypeArgument;
		}
		
		if (!storedQueries.containsKey(actualRawTypeArgument)) {
			storedQueries.put(actualRawTypeArgument, new HashMap<String, Criteria<?>>());
		}
		
		storedQueries.get(actualRawTypeArgument).put(id, criteria);
	}
}
