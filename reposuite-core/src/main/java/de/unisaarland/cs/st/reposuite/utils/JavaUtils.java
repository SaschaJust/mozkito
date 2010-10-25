/**
 * 
 */
package de.unisaarland.cs.st.reposuite.utils;

import java.util.Map;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class JavaUtils {
	
	public static String mapToString(Map<?, ?> map) {
		StringBuilder builder = new StringBuilder();
		
		if (map == null) {
			builder.append("[(null)]");
		} else {
			for (Object key : map.keySet()) {
				if (builder.length() > 0) {
					builder.append(",");
				} else {
					builder.append("[");
				}
				builder.append("[");
				builder.append(key.toString());
				builder.append(":");
				builder.append(map.get(key).toString());
				builder.append("]");
			}
			builder.append("]");
		}
		
		return builder.toString();
	}
}
