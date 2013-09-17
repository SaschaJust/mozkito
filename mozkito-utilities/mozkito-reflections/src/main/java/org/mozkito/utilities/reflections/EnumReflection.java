/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/

package org.mozkito.utilities.reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import sun.reflect.ConstructorAccessor;
import sun.reflect.ReflectionFactory;

/**
 * The Class Enum.
 * 
 * @param <E>
 *            the element type
 * 
 *            The code base originates from Dr. Heinz M. Kabutz in Java Specialists' Newsletter on 'Of Hacking Enums and
 *            Modifying "final static" Fields'. See: http://www.javaspecialists.eu/archive/Issue161.html
 * @author Dr. Heinz M. Kabutz
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class EnumReflection<E extends Enum<E>> {
	
	/**
	 * The Class Memento.
	 */
	private class Memento {
		
		/** The values. */
		private final E[]               values;
		
		/** The saved switch field values. */
		private final Map<Field, int[]> savedSwitchFieldValues = new HashMap<Field, int[]>();
		
		/**
		 * Instantiates a new memento.
		 * 
		 * @throws IllegalAccessException
		 *             the illegal access exception
		 */
		private Memento() throws IllegalAccessException {
			try {
				this.values = values().clone();
				for (final Field switchField : EnumReflection.this.switchFields) {
					switchField.setAccessible(true);
					final int[] switchArray = (int[]) switchField.get(null);
					this.savedSwitchFieldValues.put(switchField, switchArray.clone());
				}
			} catch (final Exception e) {
				throw new IllegalArgumentException("Could not create the class", e);
			}
		}
		
		/**
		 * Undo.
		 * 
		 * @throws NoSuchFieldException
		 *             the no such field exception
		 * @throws IllegalAccessException
		 *             the illegal access exception
		 */
		private void undo() throws NoSuchFieldException, IllegalAccessException {
			final Field valuesField = findValuesField();
			Reflections.setStaticFinalField(valuesField, this.values);
			
			for (int i = 0; i < this.values.length; i++) {
				setOrdinal(this.values[i], i);
			}
			
			// reset all of the constants defined inside the enum
			final Map<String, E> valuesMap = new HashMap<String, E>();
			for (final E e : this.values) {
				valuesMap.put(e.name(), e);
			}
			final Field[] constantEnumFields = EnumReflection.this.clazz.getDeclaredFields();
			for (final Field constantEnumField : constantEnumFields) {
				final E en = valuesMap.get(constantEnumField.getName());
				if (en != null) {
					Reflections.setStaticFinalField(constantEnumField, en);
				}
			}
			
			for (final Map.Entry<Field, int[]> entry : this.savedSwitchFieldValues.entrySet()) {
				final Field field = entry.getKey();
				final int[] mappings = entry.getValue();
				Reflections.setStaticFinalField(field, mappings);
			}
		}
	}
	
	/** The Constant EMPTY_CLASS_ARRAY. */
	private static final Class<?>[] EMPTY_CLASS_ARRAY  = new Class<?>[0];
	
	/** The Constant EMPTY_OBJECT_ARRAY. */
	private static final Object[]   EMPTY_OBJECT_ARRAY = new Object[0];
	
	/** The Constant VALUES_FIELD. */
	private static final String     VALUES_FIELD       = "ENUM$VALUES";
	
	/** The Constant ORDINAL_FIELD. */
	private static final String     ORDINAL_FIELD      = "ordinal";
	
	/** The reflection. */
	private final ReflectionFactory reflection         = ReflectionFactory.getReflectionFactory();
	
	/** The clazz. */
	private final Class<E>          clazz;
	
	/** The switch fields. */
	private final Collection<Field> switchFields;
	
	/** The undo stack. */
	private final Deque<Memento>    undoStack          = new LinkedList<Memento>();
	
	/**
	 * Construct an EnumBuster for the given enum class and keep the switch statements of the classes specified in
	 * switchUsers in sync with the enum values.
	 * 
	 * @param clazz
	 *            the clazz
	 * @param switchUsers
	 *            the switch users
	 */
	public EnumReflection(final Class<E> clazz, final Class<?>... switchUsers) {
		try {
			this.clazz = clazz;
			this.switchFields = findRelatedSwitchFields(switchUsers);
			// for (final Field switchField : this.switchFields) {
			// switchField.setAccessible(true);
			// final Field modifiersField = Field.class.getDeclaredField("modifiers");
			// modifiersField.setAccessible(true);
			// int modifiers = modifiersField.getInt(switchField);
			// modifiers &= ~Modifier.PRIVATE;
			// modifiers |= Modifier.PUBLIC;
			// modifiersField.setInt(switchField, modifiers);
			// final ReflectionFactory rf = ReflectionFactory.getReflectionFactory();
			// final FieldAccessor fa = rf.newFieldAccessor(switchField, false);
			// final Object object = fa.get(switchField);
			// final int[] switchValue = (int[]) switchField.get(null);
			// if (switchValue == null) {
			// final s
			// System.err.println("error");
			// }
			// }
		} catch (final Exception e) {
			throw new IllegalArgumentException("Could not create the class", e);
		}
	}
	
	/**
	 * This method adds the given enum into the array inside the enum class. If the enum already contains that
	 * particular value, then the value is overwritten with our enum. Otherwise it is added at the end of the array.
	 * 
	 * In addition, if there is a constant field in the enum class pointing to an enum with our value, then we replace
	 * that with our enum instance.
	 * 
	 * The ordinal is either set to the existing position or to the last value.
	 * 
	 * Warning: This should probably never be called, since it can cause permanent changes to the enum values. Use only
	 * in extreme conditions.
	 * 
	 * @param e
	 *            the enum to add
	 */
	public void addByValue(final E e) {
		try {
			this.undoStack.push(new Memento());
			final Field valuesField = findValuesField();
			
			// we get the current Enum[]
			final E[] values = values();
			for (int i = 0; i < values.length; i++) {
				final E value = values[i];
				if (value.name().equals(e.name())) {
					setOrdinal(e, value.ordinal());
					values[i] = e;
					replaceConstant(e);
					return;
				}
			}
			
			// we did not find it in the existing array, thus
			// append it to the array
			final E[] newValues = Arrays.copyOf(values, values.length + 1);
			newValues[newValues.length - 1] = e;
			Reflections.setStaticFinalField(valuesField, newValues);
			
			final int ordinal = newValues.length - 1;
			setOrdinal(e, ordinal);
			addSwitchCase();
		} catch (final Exception ex) {
			throw new IllegalArgumentException("Could not set the enum", ex);
		}
	}
	
	/**
	 * The only time we ever add a new enum is at the end. Thus all we need to do is expand the switch map arrays by one
	 * empty slot.
	 */
	private void addSwitchCase() {
		try {
			for (final Field switchField : this.switchFields) {
				int[] switches = (int[]) switchField.get(null);
				switches = Arrays.copyOf(switches, switches.length + 1);
				Reflections.setStaticFinalField(switchField, switches);
			}
		} catch (final Exception e) {
			throw new IllegalArgumentException("Could not fix switch", e);
		}
	}
	
	/**
	 * Blank out constant.
	 * 
	 * @param e
	 *            the e
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws NoSuchFieldException
	 *             the no such field exception
	 */
	private void blankOutConstant(final E e) throws IllegalAccessException, NoSuchFieldException {
		final Field[] fields = this.clazz.getDeclaredFields();
		for (final Field field : fields) {
			if (field.getName().equals(e.name())) {
				Reflections.setStaticFinalField(field, null);
			}
		}
	}
	
	/**
	 * Construct enum.
	 * 
	 * @param clazz
	 *            the clazz
	 * @param ca
	 *            the ca
	 * @param value
	 *            the value
	 * @param ordinal
	 *            the ordinal
	 * @param additional
	 *            the additional
	 * @return the e
	 * @throws Exception
	 *             the exception
	 */
	private E constructEnum(final Class<E> clazz,
	                        final ConstructorAccessor ca,
	                        final String value,
	                        final int ordinal,
	                        final Object[] additional) throws Exception {
		final Object[] parms = new Object[additional.length + 2];
		parms[0] = value;
		parms[1] = ordinal;
		System.arraycopy(additional, 0, parms, 2, additional.length);
		return clazz.cast(ca.newInstance(parms));
	}
	
	/**
	 * We delete the enum from the values array and set the constant pointer to null.
	 * 
	 * @param e
	 *            the enum to delete from the type.
	 * @return true if the enum was found and deleted; false otherwise
	 */
	public boolean deleteByValue(final E e) {
		PRECONDITIONS: {
			if (e == null) {
				throw new NullPointerException();
			}
		}
		
		try {
			this.undoStack.push(new Memento());
			// we get the current E[]
			final E[] values = values();
			for (int i = 0; i < values.length; i++) {
				final E value = values[i];
				if (value.name().equals(e.name())) {
					final E[] newValues = Arrays.copyOf(values, values.length - 1);
					System.arraycopy(values, i + 1, newValues, i, values.length - i - 1);
					for (int j = i; j < newValues.length; j++) {
						setOrdinal(newValues[j], j);
					}
					final Field valuesField = findValuesField();
					Reflections.setStaticFinalField(valuesField, newValues);
					removeSwitchCase(i);
					blankOutConstant(e);
					return true;
				}
			}
		} catch (final Exception ex) {
			throw new IllegalArgumentException("Could not set the enum", ex);
		}
		return false;
	}
	
	/**
	 * Find constructor accessor.
	 * 
	 * @param additionalParameterTypes
	 *            the additional parameter types
	 * @param clazz
	 *            the clazz
	 * @return the constructor accessor
	 * @throws NoSuchMethodException
	 *             the no such method exception
	 */
	private ConstructorAccessor findConstructorAccessor(final Class[] additionalParameterTypes,
	                                                    final Class<E> clazz) throws NoSuchMethodException {
		final Class[] parameterTypes = new Class[additionalParameterTypes.length + 2];
		parameterTypes[0] = String.class;
		parameterTypes[1] = int.class;
		System.arraycopy(additionalParameterTypes, 0, parameterTypes, 2, additionalParameterTypes.length);
		final Constructor<E> cstr = clazz.getDeclaredConstructor(parameterTypes);
		return this.reflection.newConstructorAccessor(cstr);
	}
	
	/**
	 * Find related switch fields.
	 * 
	 * @param switchUsers
	 *            the switch users
	 * @return the collection
	 */
	private Collection<Field> findRelatedSwitchFields(final Class[] switchUsers) {
		final Collection<Field> result = new ArrayList<Field>();
		try {
			for (final Class switchUser : switchUsers) {
				final Class[] clazzes = switchUser.getDeclaredClasses();
				for (final Class suspect : clazzes) {
					result.addAll(fixSwitch(suspect));
				}
				// result.addAll(fixSwitch(switchUser));
			}
		} catch (final Exception e) {
			throw new IllegalArgumentException("Could not fix switch", e);
		}
		return result;
	}
	
	/**
	 * Method to find the values field, set it to be accessible, and return it.
	 * 
	 * @return the values array field for the enum.
	 * @throws NoSuchFieldException
	 *             if the field could not be found
	 */
	private Field findValuesField() throws NoSuchFieldException {
		// first we find the static final array that holds
		// the values in the enum class
		final Field valuesField = this.clazz.getDeclaredField(VALUES_FIELD);
		// we mark it to be public
		valuesField.setAccessible(true);
		return valuesField;
	}
	
	/**
	 * @param result
	 * @param targetClass
	 * @return
	 */
	private Collection<Field> fixSwitch(final Class targetClass) {
		final Field[] fields = targetClass.getDeclaredFields();
		final Collection<Field> result = new ArrayList<>();
		for (final Field field : fields) {
			if (field.getName().startsWith("$SWITCH_TABLE$")) {
				// + this.clazz.getSimpleName())) {
				
				field.setAccessible(true);
				result.add(field);
			}
		}
		return result;
	}
	
	/**
	 * Make a new enum instance, without adding it to the values array and using the default ordinal of 0.
	 * 
	 * @param value
	 *            the value
	 * @return the e
	 */
	public E make(final String value) {
		return make(value, 0, EMPTY_CLASS_ARRAY, EMPTY_OBJECT_ARRAY);
	}
	
	/**
	 * Make a new enum instance with the given ordinal.
	 * 
	 * @param value
	 *            the value
	 * @param ordinal
	 *            the ordinal
	 * @return the e
	 */
	public E make(final String value,
	              final int ordinal) {
		return make(value, ordinal, EMPTY_CLASS_ARRAY, EMPTY_OBJECT_ARRAY);
	}
	
	/**
	 * Make a new enum instance with the given value, ordinal and additional parameters. The additionalTypes is used to
	 * match the constructor accurately.
	 * 
	 * @param value
	 *            the value
	 * @param ordinal
	 *            the ordinal
	 * @param additionalTypes
	 *            the additional types
	 * @param additional
	 *            the additional
	 * @return the e
	 */
	public E make(final String value,
	              final int ordinal,
	              final Class[] additionalTypes,
	              final Object[] additional) {
		try {
			this.undoStack.push(new Memento());
			final ConstructorAccessor ca = findConstructorAccessor(additionalTypes, this.clazz);
			return constructEnum(this.clazz, ca, value, ordinal, additional);
		} catch (final Exception e) {
			throw new IllegalArgumentException("Could not create enum", e);
		}
	}
	
	/**
	 * Removes the switch case.
	 * 
	 * @param ordinal
	 *            the ordinal
	 */
	private void removeSwitchCase(final int ordinal) {
		try {
			for (final Field switchField : this.switchFields) {
				final int[] switches = (int[]) switchField.get(null);
				final int[] newSwitches = Arrays.copyOf(switches, switches.length - 1);
				System.arraycopy(switches, ordinal + 1, newSwitches, ordinal, switches.length - ordinal - 1);
				Reflections.setStaticFinalField(switchField, newSwitches);
			}
		} catch (final Exception e) {
			throw new IllegalArgumentException("Could not fix switch", e);
		}
	}
	
	/**
	 * Replace constant.
	 * 
	 * @param e
	 *            the e
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws NoSuchFieldException
	 *             the no such field exception
	 */
	private void replaceConstant(final E e) throws IllegalAccessException, NoSuchFieldException {
		final Field[] fields = this.clazz.getDeclaredFields();
		for (final Field field : fields) {
			if (field.getName().equals(e.name())) {
				Reflections.setStaticFinalField(field, e);
			}
		}
	}
	
	/**
	 * Undo the state right back to the beginning when the EnumBuster was created.
	 */
	public void restore() {
		while (undo()) {
			//
		}
	}
	
	/**
	 * Sets the ordinal.
	 * 
	 * @param e
	 *            the e
	 * @param ordinal
	 *            the ordinal
	 * @throws NoSuchFieldException
	 *             the no such field exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 */
	private void setOrdinal(final E e,
	                        final int ordinal) throws NoSuchFieldException, IllegalAccessException {
		final Field[] fields = Enum.class.getDeclaredFields();
		for (final Field field : fields) {
			System.err.println(field);
		}
		final Field ordinalField = Enum.class.getDeclaredField(ORDINAL_FIELD);
		ordinalField.setAccessible(true);
		ordinalField.set(e, ordinal);
	}
	
	/**
	 * Undo the previous operation.
	 * 
	 * @return true, if successful
	 */
	public boolean undo() {
		try {
			final Memento memento = this.undoStack.poll();
			if (memento == null) {
				return false;
			}
			memento.undo();
			return true;
		} catch (final Exception e) {
			throw new IllegalStateException("Could not undo", e);
		}
	}
	
	/**
	 * Values.
	 * 
	 * @return the e[]
	 * @throws NoSuchFieldException
	 *             the no such field exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 */
	@SuppressWarnings ("unchecked")
	private E[] values() throws NoSuchFieldException, IllegalAccessException {
		final Field valuesField = findValuesField();
		return (E[]) valuesField.get(null);
	}
	
}
