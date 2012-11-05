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
package org.mozkito.mappings.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;

import org.mozkito.mappings.mappable.model.MappableEntity;
import org.mozkito.mappings.strategies.Strategy;
import org.mozkito.persistence.Annotated;

/**
 * The Class Composite.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
@Entity
public class Composite implements Annotated {
	
	/** The Constant serialVersionUID. */
	private static final long    serialVersionUID = 4247288063693897409L;
	
	/** The relation. */
	private Relation             relation;
	
	/** The strategies. */
	private Map<String, Boolean> strategies       = new HashMap<>();
	
	private long                 generatedId;
	
	/**
	 * Instantiates a new composite.
	 */
	@SuppressWarnings ("unused")
	private Composite() {
		this.relation = null;
	}
	
	/**
	 * Instantiates a new composite.
	 * 
	 * @param relation
	 *            the relation
	 */
	public Composite(final Relation relation) {
		this.relation = relation;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.model.IComposite#addStrategy(java.lang.String, java.lang.Boolean)
	 */
	@Transient
	public Composite addStrategy(@NotNull final Strategy strategy,
	                             final Boolean valid) {
		assert !getStrategies().containsKey(strategy.getHandle());
		getStrategies().put(strategy.getHandle(), valid);
		return this;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.model.IComposite#getClass1()
	 */
	@Transient
	public String getClass1() {
		// PRECONDITIONS
		
		try {
			return getRelation().getClass1();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.model.IComposite#getClass2()
	 */
	@Transient
	public String getClass2() {
		// PRECONDITIONS
		
		try {
			return getRelation().getClass2();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.model.IComposite#getFrom()
	 */
	@Transient
	public final MappableEntity getFrom() {
		return getRelation().getFrom();
	}
	
	/**
	 * @return the generatedId
	 */
	@Id
	@GeneratedValue
	public long getGeneratedId() {
		return this.generatedId;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.model.IComposite#getHandle()
	 */
	@Transient
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
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.model.IComposite#getRelation()
	 */
	@ManyToOne (fetch = FetchType.EAGER, cascade = {})
	public final Relation getRelation() {
		return this.relation;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.model.IComposite#getStrategies()
	 */
	public final Map<String, Boolean> getStrategies() {
		return this.strategies;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.model.IComposite#getTo()
	 */
	@Transient
	public final MappableEntity getTo() {
		return getRelation().getTo();
	}
	
	/**
	 * @param generatedId
	 *            the generatedId to set
	 */
	public void setGeneratedId(final long generatedId) {
		// PRECONDITIONS
		Condition.notNull(generatedId, "Argument '%s' in '%s'.", "generatedId", getClass().getSimpleName());
		
		try {
			this.generatedId = generatedId;
		} finally {
			// POSTCONDITIONS
			CompareCondition.equals(this.generatedId, generatedId,
			                        "After setting a value, the corresponding field has to hold the same value as used as a parameter within the setter.");
		}
	}
	
	/**
	 * Sets the relation.
	 * 
	 * @param relation
	 *            the relation to set
	 */
	public final void setRelation(final Relation relation) {
		this.relation = relation;
	}
	
	/**
	 * Sets the strategies.
	 * 
	 * @param strategies
	 *            the strategies to set
	 */
	public final void setStrategies(final Map<String, Boolean> strategies) {
		this.strategies = strategies;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append(getHandle());
		builder.append(" [relation="); //$NON-NLS-1$
		builder.append(this.relation);
		builder.append(", strategies="); //$NON-NLS-1$
		builder.append(JavaUtils.mapToString(this.strategies));
		builder.append("]"); //$NON-NLS-1$
		return builder.toString();
	}
}
