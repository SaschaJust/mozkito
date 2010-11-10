/**
 * 
 */
package de.unisaarland.cs.st.reposuite.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class JavaUtils {
	
	public static boolean AnyNull(final Object... objects) {
		for (Object object : objects) {
			if (object == null) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param objects
	 * @return
	 */
	public static String arrayToString(final Object[] objects) {
		return collectionToString(Arrays.asList(objects));
	}
	
	/**
	 * @param object
	 * @return
	 */
	private static String checkDescent(final Object object) {
		if (CollectionUtils.exists(Arrays.asList(object.getClass().getInterfaces()), new Predicate() {
			
			@Override
			public boolean evaluate(final Object object) {
				return object.equals(Collection.class);
			}
		})) {
			return collectionToString((Collection<?>) object);
		} else if (CollectionUtils.exists(Arrays.asList(object.getClass().getInterfaces()), new Predicate() {
			
			@Override
			public boolean evaluate(final Object object) {
				return object.equals(Map.class);
			}
		})) {
			return mapToString((Map<?, ?>) object);
		} else {
			return object.toString();
		}
	}
	
	/**
	 * @param collection
	 * @return
	 */
	public static String collectionToString(final Collection<?> collection) {
		StringBuilder builder = new StringBuilder();
		
		if (collection == null) {
			builder.append("[(null)]");
		} else {
			builder.append("[");
			for (Object object : collection) {
				if (builder.length() > 1) {
					builder.append(",");
				}
				builder.append(checkDescent(object));
			}
			builder.append("]");
		}
		return builder.toString();
	}
	
	public static String[] enumToArray(final Enum<?> e) {
		String[] retval = new String[e.getDeclaringClass().getEnumConstants().length];
		
		for (int i = 0; i < retval.length; ++i) {
			retval[i] = e.getDeclaringClass().getEnumConstants()[i].toString();
		}
		return retval;
	}
	
	public static String enumToString(final Enum<?> e) {
		Object[] enumConstants = e.getDeclaringClass().getEnumConstants();
		return arrayToString(enumConstants);
	}
	
	/**
	 * @param map
	 * @return
	 */
	public static String mapToString(final Map<?, ?> map) {
		StringBuilder builder = new StringBuilder();
		
		if (map == null) {
			builder.append("[(null)]");
		} else {
			builder.append("[");
			for (Object key : map.keySet()) {
				if (builder.length() > 1) {
					builder.append(",");
				}
				
				builder.append("[");
				builder.append(checkDescent(key));
				builder.append(":");
				builder.append(checkDescent(map.get(key)));
				builder.append("]");
			}
			builder.append("]");
		}
		
		return builder.toString();
	}
	
	/**
	 * @return the simple class name
	 */
	public String getHandle() {
		return this.getClass().getSimpleName();
	}
}
