/**
 * 
 */
package net.ownhero.dev.andama.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class AndamaUtils {
	
	public final static String lineSeparator = System.getProperty("line.separator");
	public final static String fileSeparator = System.getProperty("file.separator");
	
	/**
	 * @param object
	 * @return
	 */
	private static String checkDescent(final Object object) {
		if (object == null) {
			return "(null)";
		}
		
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
			int i = 0;
			for (Object object : collection) {
				++i;
				if (builder.length() > 1) {
					builder.append(",");
				}
				builder.append(checkDescent(object));
				if (i == 10) {
					builder.append("...");
					break;
				}
			}
			builder.append("]");
		}
		return builder.toString();
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
}
