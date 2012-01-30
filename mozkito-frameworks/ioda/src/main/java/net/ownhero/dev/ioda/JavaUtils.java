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
	
	/**
	 * Returns true if any of the submitted objects is null
	 * 
	 * @param objects
	 *            array of objects to be tested for being null
	 * @return true iff any of the objects in the array is null
	 */
	public static boolean AnyNull(final Object... objects) {
		for (final Object object : objects) {
			if (object == null) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns a string representing the array with its contents. The representation starts with a '[' followed by a
	 * comma separated list of entries, followed by a ']'. Internally, the array is converted to a list and processed by
	 * {@link JavaUtils#collectionToString(Collection)}.
	 * 
	 * Note that this method may be called recursively, if the entries are or contain arrays as well. If an entry is a
	 * <code>Collection</code>, a <code>Map</code> or an <code>Array</code>, the according
	 * <code>collectionToString()</code>, <code>mapToString()</code> or <code>arrayToString()</code> method is called.
	 * Otherwise, we call the <code>toString()</code> method of the object.
	 * 
	 * @param array
	 *            the array you request the string representation for
	 * @return the string representation of the array
	 */
	public static String arrayToString(final Object[] array) {
		return collectionToString(Arrays.asList(array));
	}
	
	/**
	 * This method converts a <code>byte</code> array to the hexadecimal representation in an ASCII encoded string.
	 * 
	 * @param raw
	 *            the byte[] array that shall be converted
	 * @return an ASCII string containing the hex representation of the byte array
	 * @throws UnsupportedEncodingException
	 *             If the 'ASCII' charset is not supported
	 */
	public static String byteArrayToHexString(final byte[] raw) throws UnsupportedEncodingException {
		final byte[] hex = new byte[2 * raw.length];
		int index = 0;
		
		for (final byte b : raw) {
			final int v = b & 0xFF;
			hex[index++] = JavaUtils.HEX_CHAR_TABLE[v >>> 4];
			hex[index++] = JavaUtils.HEX_CHAR_TABLE[v & 0xF];
		}
		return new String(hex, "ASCII");
	}
	
	/**
	 * Internal method used by {@link JavaUtils#collectionToString(Collection)},
	 * {@link JavaUtils#arrayToString(Object[])} and {@link JavaUtils#mapToString(Map)}. Checks the entries to implement
	 * {@link Collection} or {@link Map} or be of type array. In this case the according methods in {@link JavaUtils}
	 * are called to get their String representations. Otherwise, the objects <code>toString()</code> method is called.
	 * 
	 * @param object
	 *            the object that shall be represented as a String
	 * @return the string representation of the object
	 * 
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
		} else if (object.getClass().isArray()) {
			return arrayToString((Object[]) object);
		} else {
			return object.toString();
		}
	}
	
	/**
	 * Returns a string representing the collection with its contents. The representation starts with a '[' followed by
	 * a comma separated list of entries, followed by a ']'.
	 * 
	 * Note that this method may be called recursively, if the entries are or contain collections as well. If an entry
	 * is a <code>Collection</code>, a <code>Map</code> or an <code>Array</code>, the according
	 * <code>collectionToString()</code>, <code>mapToString()</code> or <code>arrayToString()</code> method is called.
	 * Otherwise, we call the <code>toString()</code> method of the object.
	 * 
	 * @param collection
	 *            the collection you request the string representation of
	 * @return the string representation of the collection
	 */
	public static String collectionToString(final Collection<?> collection) {
		final StringBuilder builder = new StringBuilder();
		
		if (collection == null) {
			builder.append("[(null)]");
		} else {
			builder.append("[");
			int i = 0;
			for (final Object object : collection) {
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
	 * @param e
	 * @return
	 */
	public static String[] enumToArray(final Enum<?> e) {
		final String[] retval = new String[e.getDeclaringClass().getEnumConstants().length];
		
		for (int i = 0; i < retval.length; ++i) {
			retval[i] = e.getDeclaringClass().getEnumConstants()[i].toString();
		}
		return retval;
	}
	
	/**
	 * @param e
	 * @return
	 */
	public static String enumToString(final Enum<?> e) {
		final Object[] enumConstants = e.getDeclaringClass().getEnumConstants();
		return arrayToString(enumConstants);
	}
	
	/**
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static Class<?> getCallingClass() throws ClassNotFoundException {
		final Throwable throwable = new Throwable();
		throwable.fillInStackTrace();
		
		final StackTraceElement[] stackTrace = throwable.getStackTrace();
		return Class.forName(stackTrace[1].getClassName());
	}
	
	/**
	 * @return
	 */
	public static String getThisMethodName() {
		final Throwable throwable = new Throwable();
		throwable.fillInStackTrace();
		
		final StackTraceElement[] stackTrace = throwable.getStackTrace();
		return stackTrace[1].getMethodName();
	}
	
	/**
	 * @param map
	 * @return
	 */
	public static String mapToString(final Map<?, ?> map) {
		final StringBuilder builder = new StringBuilder();
		
		if (map == null) {
			builder.append("[(null)]");
		} else {
			builder.append("[");
			for (final Object key : map.keySet()) {
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
