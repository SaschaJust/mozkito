/**
 * 
 */
package net.ownhero.dev.ioda;

import java.io.UnsupportedEncodingException;
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
	
	private static final byte[] HEX_CHAR_TABLE = { (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4',
	        (byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd',
	        (byte) 'e', (byte) 'f'            };
	
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
	
	public static String byteArrayToHexString(final byte[] raw) throws UnsupportedEncodingException {
		byte[] hex = new byte[2 * raw.length];
		int index = 0;
		
		for (byte b : raw) {
			int v = b & 0xFF;
			hex[index++] = JavaUtils.HEX_CHAR_TABLE[v >>> 4];
			hex[index++] = JavaUtils.HEX_CHAR_TABLE[v & 0xF];
		}
		return new String(hex, "ASCII");
	}
	
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
