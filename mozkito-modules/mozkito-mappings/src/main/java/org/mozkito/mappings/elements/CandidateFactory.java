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
package org.mozkito.mappings.elements;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;

import org.mozkito.mappings.model.Candidate;
import org.mozkito.mappings.selectors.Selector;
import org.mozkito.persistence.Entity;
import org.mozkito.utilities.loading.classpath.ClassFinder;
import org.mozkito.utilities.loading.classpath.exceptions.WrongClassSearchMethodException;

/**
 * A factory for creating Candidate objects.
 * 
 * @param <FROM>
 *            the generic type
 * @param <TO>
 *            the generic type
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class CandidateFactory<FROM extends Entity, TO extends Entity> {
	
	/** The factories. */
	private static Map<Set<Class<? extends Entity>>, CandidateFactory<?, ?>> factories = new HashMap<>();
	
	/**
	 * Gets the simple name of the class.
	 * 
	 * @return the simple name of the class.
	 */
	public static final String getClassName() {
		// PRECONDITIONS
		
		final StringBuilder builder = new StringBuilder();
		
		try {
			final LinkedList<Class<?>> list = new LinkedList<Class<?>>();
			Class<?> clazz = CandidateFactory.class;
			list.add(clazz);
			
			while ((clazz = clazz.getEnclosingClass()) != null) {
				list.addFirst(clazz);
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
			Condition.notNull(builder,
			                  "Local variable '%s' in '%s:%s'.", "builder", CandidateFactory.class, "getHandle"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}
	
	/**
	 * Gets the single instance of CandidateFactory.
	 * 
	 * @param <ONE>
	 *            the generic type
	 * @param <OTHER>
	 *            the generic type
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @return single instance of CandidateFactory
	 */
	@SuppressWarnings ({ "unchecked", "rawtypes" })
	public static final <ONE extends Entity, OTHER extends Entity> CandidateFactory<ONE, OTHER> getInstance(final Class<ONE> from,
	                                                                                                        final Class<OTHER> to) {
		@SuppressWarnings ("serial")
		final HashSet<Class<? extends Entity>> set = new HashSet<Class<? extends Entity>>() {
			
			{
				add(from);
				add(to);
			}
		};
		
		if (!CandidateFactory.factories.containsKey(set)) {
			Collection<Class<? extends Candidate>> collection = null;
			try {
				collection = ClassFinder.getClassesExtendingClass(Candidate.class.getPackage(), Candidate.class,
				                                                  Modifier.ABSTRACT | Modifier.PRIVATE
				                                                          | Modifier.INTERFACE);
			} catch (ClassNotFoundException | WrongClassSearchMethodException | IOException e1) {
				return null;
			}
			for (final Class<? extends Candidate> c : collection) {
				
				try {
					final Method getToMethod = c.getMethod("getTo", new Class<?>[0]);
					final Method getFromMethod = c.getMethod("getFrom", new Class<?>[0]);
					
					if (to.equals(getToMethod.getReturnType()) && from.equals(getFromMethod.getReturnType())) {
						CandidateFactory.factories.put(set,
						                               new CandidateFactory<ONE, OTHER>(
						                                                                (Class<Candidate<ONE, OTHER>>) c));
					}
					
				} catch (NoSuchMethodException | SecurityException e) {
					return null;
				}
				
			}
			
		}
		
		return (CandidateFactory<ONE, OTHER>) CandidateFactory.factories.get(set);
	}
	
	/** The candidates. */
	private final Map<Set<String>, Candidate<FROM, TO>> candidates     = new HashMap<>();
	private Class<Candidate<FROM, TO>>                  implementation = null;
	
	/**
	 * @param c
	 */
	public CandidateFactory(final Class<Candidate<FROM, TO>> c) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// body
			this.implementation = c;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Adds the.
	 * 
	 * @param one
	 *            the one
	 * @param other
	 *            the other
	 * @param votingSelectors
	 *            the selectors
	 * @return the candidate
	 */
	public final Candidate<FROM, TO> add(@NotNull final FROM one,
	                                     @NotNull final TO other,
	                                     final Set<Selector> votingSelectors) {
		@SuppressWarnings ("serial")
		final HashSet<String> set = new HashSet<String>() {
			
			{
				add(one.getIDString());
				add(other.getIDString());
			}
		};
		
		if (!this.candidates.containsKey(set)) {
			Constructor<Candidate<FROM, TO>> constructor;
			try {
				constructor = this.implementation.getConstructor(new Class<?>[] { one.getClass(), other.getClass() });
				final Candidate<FROM, TO> candidate = constructor.newInstance(one, other);
				candidate.addSelectors(votingSelectors);
				return this.candidates.put(set, candidate);
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
			        | IllegalArgumentException | InvocationTargetException e) {
				// error
			}
			return null;
		} else {
			return this.candidates.get(set);
		}
	}
	
	/**
	 * Checks if is known.
	 * 
	 * @param one
	 *            the one
	 * @param other
	 *            the other
	 * @return true, if is known
	 */
	public final boolean contains(@NotNull final FROM one,
	                              @NotNull final TO other) {
		@SuppressWarnings ("serial")
		final HashSet<String> set = new HashSet<String>() {
			
			{
				add(one.getIDString());
				add(other.getIDString());
			}
		};
		
		return this.candidates.containsKey(set);
	}
	
	/**
	 * Gets the candidate.
	 * 
	 * @param one
	 *            the one
	 * @param other
	 *            the other
	 * @return the candidate
	 */
	public final Candidate<FROM, TO> get(@NotNull final FROM one,
	                                     @NotNull final TO other) {
		@SuppressWarnings ("serial")
		final HashSet<String> set = new HashSet<String>() {
			
			{
				add(one.getIDString());
				add(other.getIDString());
			}
		};
		
		if (this.candidates.containsKey(set)) {
			return this.candidates.get(set);
		} else {
			return null;
		}
	}
	
}
