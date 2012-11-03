/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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
package org.mozkito.mappings.elements;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;

import org.mozkito.mappings.mappable.model.MappableEntity;
import org.mozkito.mappings.model.Candidate;
import org.mozkito.mappings.selectors.Selector;

/**
 * A factory for creating Candidate objects.
 * 
 * @param <FROM>
 *            the generic type
 * @param <TO>
 *            the generic type
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class CandidateFactory<FROM, TO> {
	
	/** The factories. */
	private static Map<Set<Class<? extends MappableEntity>>, CandidateFactory<?, ?>> factories = new HashMap<>();
	
	/**
	 * Gets the simple name of the class.
	 * 
	 * @return the simple name of the class.
	 */
	public static final String getHandle() {
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
	@SuppressWarnings ("unchecked")
	public static final <ONE extends MappableEntity, OTHER extends MappableEntity> CandidateFactory<ONE, OTHER> getInstance(final Class<ONE> from,
	                                                                                                                        final Class<OTHER> to) {
		@SuppressWarnings ("serial")
		final HashSet<Class<? extends MappableEntity>> set = new HashSet<Class<? extends MappableEntity>>() {
			
			{
				add(from);
				add(to);
			}
		};
		
		if (!CandidateFactory.factories.containsKey(set)) {
			CandidateFactory.factories.put(set, new CandidateFactory<ONE, OTHER>());
		}
		
		return (CandidateFactory<ONE, OTHER>) CandidateFactory.factories.get(set);
	}
	
	/** The candidates. */
	private final Map<Set<String>, Candidate> candidates = new HashMap<>();
	
	/**
	 * Gets the candidate.
	 * 
	 * @param one
	 *            the one
	 * @param other
	 *            the other
	 * @param selectors
	 *            the selector
	 * @return the candidate
	 */
	public final Candidate getCandidate(final @NotNull MappableEntity one,
	                                    @NotNull final MappableEntity other,
	                                    final Set<Selector> selectors) {
		@SuppressWarnings ("serial")
		final HashSet<String> set = new HashSet<String>() {
			
			{
				add(one.getId());
				add(other.getId());
			}
		};
		
		if (!this.candidates.containsKey(set)) {
			this.candidates.put(set, new Candidate(new Tuple<MappableEntity, MappableEntity>(one, other), selectors));
		}
		
		return this.candidates.get(set);
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
	public final boolean isKnown(final @NotNull MappableEntity one,
	                             @NotNull final MappableEntity other) {
		@SuppressWarnings ("serial")
		final HashSet<String> set = new HashSet<String>() {
			
			{
				add(one.getId());
				add(other.getId());
			}
		};
		
		return this.candidates.containsKey(set);
	}
	
}
