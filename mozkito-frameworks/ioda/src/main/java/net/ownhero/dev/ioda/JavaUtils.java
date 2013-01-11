/*******************************************************************************
 * Copyright 2013 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package net.ownhero.dev.ioda;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import net.ownhero.dev.kanuni.conditions.Condition;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

/**
 * The Class JavaUtils.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public final class JavaUtils {
	
	/** The Constant HEX_CHAR_TABLE. */
	private static final byte[] HEX_CHAR_TABLE = { (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4',
	        (byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd',
	        (byte) 'e', (byte) 'f'            };
	
	/**
	 * Returns true if any of the submitted objects is null.
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
	 * 
	 * @param array
	 *            the array you request the string representation for
	 * @return the string representation of the array {@link JavaUtils#collectionToString(Collection)}.
	 * 
	 *         Note that this method may be called recursively, if the entries are or contain arrays as well. If an
	 *         entry is a <code>Collection</code>, a <code>Map</code> or an <code>Array</code>, the according
	 *         <code>collectionToString()</code>, <code>mapToString()</code> or <code>arrayToString()</code> method is
	 *         called. Otherwise, we call the <code>toString()</code> method of the object.
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
	 * Internal method used by {@link JavaUtils#collectionToString(Collection)},.
	 * 
	 * @param object
	 *            the object that shall be represented as a String
	 * @return the string representation of the object {@link JavaUtils#arrayToString(Object[])} and
	 *         {@link JavaUtils#mapToString(Map)}. Checks the entries to implement {@link Collection} or {@link Map} or
	 *         be of type array. In this case the according methods in {@link JavaUtils} are called to get their String
	 *         representations. Otherwise, the objects <code>toString()</code> method is called.
	 */
	private static String checkDescent(final Object object) {
		if (object == null) {
			return "(null)";
		}
		
		if (CollectionUtils.exists(Arrays.asList(object.getClass().getInterfaces()), new Predicate() {
			
			@Override
			public boolean evaluate(final Object o) {
				return o.equals(Collection.class);
			}
		})) {
			return collectionToString((Collection<?>) object);
		} else if (CollectionUtils.exists(Arrays.asList(object.getClass().getInterfaces()), new Predicate() {
			
			@Override
			public boolean evaluate(final Object o) {
				return o.equals(Map.class);
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
	 * Enum to array.
	 * 
	 * @param e
	 *            the e
	 * @return the string[]
	 */
	public static String[] enumToArray(final Enum<?> e) {
		final String[] retval = new String[e.getDeclaringClass().getEnumConstants().length];
		
		for (int i = 0; i < retval.length; ++i) {
			retval[i] = e.getDeclaringClass().getEnumConstants()[i].toString();
		}
		return retval;
	}
	
	/**
	 * Enum to string.
	 * 
	 * @param e
	 *            the e
	 * @return the string
	 */
	public static String enumToString(final Enum<?> e) {
		final Object[] enumConstants = e.getDeclaringClass().getEnumConstants();
		return arrayToString(enumConstants);
	}
	
	/**
	 * Gets the calling class.
	 * 
	 * @return the calling class
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 */
	public static Class<?> getCallingClass() throws ClassNotFoundException {
		final Throwable throwable = new Throwable();
		throwable.fillInStackTrace();
		
		final StackTraceElement[] stackTrace = throwable.getStackTrace();
		return Class.forName(stackTrace[1].getClassName());
	}
	
	/**
	 * Gets the handle.
	 * 
	 * @param clazz
	 *            the clazz
	 * @return the handle
	 */
	public static final String getHandle(final Class<?> clazz) {
		// PRECONDITIONS
		
		final StringBuilder builder = new StringBuilder();
		
		try {
			final LinkedList<Class<?>> list = new LinkedList<Class<?>>();
			Class<?> tmpClass = clazz;
			list.add(tmpClass);
			
			while ((tmpClass = tmpClass.getEnclosingClass()) != null) {
				list.addFirst(tmpClass);
			}
			
			for (final Class<?> c : list) {
				if (builder.length() > 0) {
					builder.append('.');
				}
				
				builder.append(c.getSimpleName());
			}
			
			return builder.toString();
		} finally {
			// POSTCONDITIONS
			Condition.notNull(builder, "Local variable '%s' in '%s:%s'.", "builder", JavaUtils.class, "getHandle"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}
	
	/**
	 * Gets the handle.
	 * 
	 * @param o
	 *            the o
	 * @return the handle
	 */
	public static final String getHandle(final Object o) {
		return getHandle(o.getClass());
	}
	
	/**
	 * Gets the this method name.
	 * 
	 * @return the this method name
	 */
	public static String getThisMethodName() {
		final Throwable throwable = new Throwable();
		throwable.fillInStackTrace();
		
		final StackTraceElement[] stackTrace = throwable.getStackTrace();
		return stackTrace[1].getMethodName();
	}
	
	/**
	 * Map to string.
	 * 
	 * @param map
	 *            the map
	 * @return the string
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
	 * To hex string.
	 * 
	 * @param b
	 *            the b
	 * @return the string
	 */
	public static final String toHexString(final byte b) {
		return "0x" + (b < 16
		                     ? '0'
		                     : "") + Integer.toHexString(b);
	}
	
	/**
	 * To hex string.
	 * 
	 * @param array
	 *            the array
	 * @return the string
	 */
	public static String toHexString(final byte[] array) {
		final StringBuilder builder = new StringBuilder();
		
		for (final byte b : array) {
			if (builder.length() > 0) {
				builder.append(' ');
			}
			builder.append(toHexString(b));
		}
		
		return builder.toString();
	}
}
