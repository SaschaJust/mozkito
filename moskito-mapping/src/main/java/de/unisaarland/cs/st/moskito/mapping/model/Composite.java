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
package de.unisaarland.cs.st.moskito.mapping.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.persistence.Transient;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.annotations.string.NotEmptyString;
import net.ownhero.dev.kanuni.conditions.Condition;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity;
import de.unisaarland.cs.st.moskito.persistence.Annotated;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class Composite implements Annotated, IComposite {
	
	private static final long    serialVersionUID = 4247288063693897409L;
	private Relation             relation;
	private Map<String, Boolean> strategies       = new HashMap<>();
	
	/**
     * 
     */
	public Composite() {
		this.relation = null;
	}
	
	public Composite(final Relation relation) {
		this.relation = relation;
	}
	
	/* (non-Javadoc)
     * @see de.unisaarland.cs.st.moskito.mapping.model.IComposite#addStrategy(java.lang.String, java.lang.Boolean)
     */
	@Override
    @Transient
	public void addStrategy(@NotNull @NotEmptyString final String strategyName,
	                        final Boolean valid) {
		getStrategies().put(strategyName, valid);
	}
	
	/* (non-Javadoc)
     * @see de.unisaarland.cs.st.moskito.mapping.model.IComposite#getFrom()
     */
	@Override
    public final MappableEntity getFrom() {
		return getRelation().getFrom();
	}
	
	/* (non-Javadoc)
     * @see de.unisaarland.cs.st.moskito.mapping.model.IComposite#getHandle()
     */
	@Override
    public final String getHandle() {
		// PRECONDITIONS
		
		final StringBuilder builder = new StringBuilder();
		
		try {
			final LinkedList<Class<?>> list = new LinkedList<Class<?>>();
			Class<?> clazz = getClass();
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
			                  "Local variable '%s' in '%s:%s'.", "builder", getClass().getSimpleName(), "getHandle"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}
	
	/* (non-Javadoc)
     * @see de.unisaarland.cs.st.moskito.mapping.model.IComposite#getRelation()
     */
	@Override
    public final Relation getRelation() {
		return this.relation;
	}
	
	/* (non-Javadoc)
     * @see de.unisaarland.cs.st.moskito.mapping.model.IComposite#getStrategies()
     */
	@Override
    public final Map<String, Boolean> getStrategies() {
		return this.strategies;
	}
	
	/* (non-Javadoc)
     * @see de.unisaarland.cs.st.moskito.mapping.model.IComposite#getTo()
     */
	@Override
    public final MappableEntity getTo() {
		return getRelation().getTo();
	}
	
	/**
	 * @param relation
	 *            the relation to set
	 */
	public final void setRelation(final Relation relation) {
		this.relation = relation;
	}
	
	/**
	 * @param strategies
	 *            the strategies to set
	 */
	public final void setStrategies(final Map<String, Boolean> strategies) {
		this.strategies = strategies;
	}
}
