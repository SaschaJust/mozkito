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
package org.mozkito.mappings.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;

import org.mozkito.mappings.strategies.Strategy;
import org.mozkito.persistence.Annotated;
import org.mozkito.utilities.commons.JavaUtils;

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
	
	/**
	 * Instantiates a new composite.
	 * 
	 * @deprecated this constructor should only be called by OpenJPA.
	 */
	@Deprecated
	public Composite() {
		// openjpa
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
	/**
	 * Adds the strategy.
	 * 
	 * @param strategy
	 *            the strategy
	 * @param valid
	 *            the valid
	 * @return the composite
	 */
	@Transient
	public Composite addStrategy(@NotNull final Strategy strategy,
	                             final Boolean valid) {
		assert !getStrategies().containsKey(strategy.getClassName());
		getStrategies().put(strategy.getClassName(), valid);
		return this;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Composite other = (Composite) obj;
		if (getRelation() == null) {
			if (other.getRelation() != null) {
				return false;
			}
		} else if (!getRelation().equals(other.getRelation())) {
			return false;
		}
		return true;
	}
	
	/**
	 * Gets the handle.
	 * 
	 * @return the handle
	 */
	@Override
	@Transient
	public final String getClassName() {
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
	 * @see org.mozkito.mapping.model.IComposite#getFrom()
	 */
	/**
	 * Gets the from.
	 * 
	 * @return the from
	 */
	@Transient
	public org.mozkito.persistence.Entity getFrom() {
		return getRelation().getFrom();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.model.IComposite#getRelation()
	 */
	/**
	 * Gets the relation.
	 * 
	 * @return the relation
	 */
	@Id
	@ManyToOne (fetch = FetchType.EAGER, cascade = {})
	public Relation getRelation() {
		return this.relation;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.model.IComposite#getStrategies()
	 */
	/**
	 * Gets the strategies.
	 * 
	 * @return the strategies
	 */
	@ElementCollection
	public Map<String, Boolean> getStrategies() {
		return this.strategies;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.model.IComposite#getTo()
	 */
	/**
	 * Gets the to.
	 * 
	 * @return the to
	 */
	@Transient
	public org.mozkito.persistence.Entity getTo() {
		return getRelation().getTo();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((getRelation() == null)
		                                                    ? 0
		                                                    : getRelation().hashCode());
		return result;
	}
	
	/**
	 * Sets the relation.
	 * 
	 * @param relation
	 *            the relation to set
	 */
	public void setRelation(final Relation relation) {
		this.relation = relation;
	}
	
	/**
	 * Sets the strategies.
	 * 
	 * @param strategies
	 *            the strategies to set
	 */
	public void setStrategies(final Map<String, Boolean> strategies) {
		this.strategies = strategies;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append(getClassName());
		builder.append(" [relation="); //$NON-NLS-1$
		builder.append(getRelation());
		builder.append(", strategies="); //$NON-NLS-1$
		builder.append(JavaUtils.mapToString(getStrategies()));
		builder.append("]"); //$NON-NLS-1$
		return builder.toString();
	}
}
