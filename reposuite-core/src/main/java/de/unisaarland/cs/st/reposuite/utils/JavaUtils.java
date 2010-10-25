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
	
	public static String arrayToString(Object[] objects) {
		return collectionToString(Arrays.asList(objects));
	}
	
	private static String checkDescent(Object object) {
		if (CollectionUtils.exists(Arrays.asList(object.getClass().getInterfaces()), new Predicate() {
			
			@Override
			public boolean evaluate(Object object) {
				return object.equals(Collection.class);
			}
		})) {
			return collectionToString((Collection<?>) object);
		} else if (CollectionUtils.exists(Arrays.asList(object.getClass().getInterfaces()), new Predicate() {
			
			@Override
			public boolean evaluate(Object object) {
				return object.equals(Map.class);
			}
		})) {
			return mapToString((Map<?, ?>) object);
		} else {
			return object.toString();
		}
	}
	
	public static String collectionToString(Collection<?> collection) {
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
	
	public static String mapToString(Map<?, ?> map) {
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
	
	public String getHandle() {
		return this.getClass().getSimpleName();
	}
}
