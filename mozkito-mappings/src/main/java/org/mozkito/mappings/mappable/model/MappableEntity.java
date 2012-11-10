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
package org.mozkito.mappings.mappable.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kanuni.conditions.Condition;

import org.apache.commons.collections.CollectionUtils;

import org.mozkito.mappings.mappable.FieldKey;
import org.mozkito.persistence.Annotated;

/**
 * Superclass that is used to wrap around classes that shall be mapped. Since inheritance based annotations do not work
 * on interfaces we can't simply use {@link Annotated} here.
 * 
 * Access to the internal data is used through access with {@link FieldKey}s. The corresponding data is mangled to fit
 * the proper format.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
@Entity
@Access (AccessType.PROPERTY)
@Inheritance (strategy = InheritanceType.JOINED)
@DiscriminatorColumn (name = "TYPE", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue ("MAPPABLEENTITY")
public abstract class MappableEntity implements Annotated {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2350328785752088197L;
	
	/** The generated id. */
	private long              generatedId;
	
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
		final MappableEntity other = (MappableEntity) obj;
		if (!getId().equals(other.getId())) {
			return false;
		}
		if (getBaseType() != other.getBaseType()) {
			return false;
		}
		return true;
	}
	
	/**
	 * Gets the.
	 * 
	 * @param key
	 *            the key
	 * @return the object
	 */
	@Transient
	public abstract Object get(FieldKey key);
	
	/**
	 * Gets the.
	 * 
	 * @param key
	 *            the key
	 * @param index
	 *            the index
	 * @return the object
	 */
	@Transient
	public abstract Object get(FieldKey key,
	                           int index);
	
	/**
	 * Gets the all.
	 * 
	 * @param keys
	 *            the keys
	 * @return the all
	 */
	@Transient
	public Map<FieldKey, Object> getAll(final FieldKey... keys) {
		final Map<FieldKey, Object> ret = new HashMap<FieldKey, Object>();
		
		for (final FieldKey key : keys) {
			ret.put(key, get(key));
		}
		
		return ret;
	}
	
	/**
	 * Gets the any.
	 * 
	 * @param keys
	 *            the keys
	 * @return the any
	 */
	@Transient
	public Object getAny(final FieldKey... keys) {
		Object ret = null;
		
		for (final FieldKey key : keys) {
			ret = get(key);
			if (ret != null) {
				return ret;
			}
		}
		
		return null;
	}
	
	/**
	 * Gets the as one string.
	 * 
	 * @param keys
	 *            the keys
	 * @return the as one string
	 */
	@Transient
	public String getAsOneString(final FieldKey... keys) {
		final StringBuilder builder = new StringBuilder();
		Object o = null;
		
		for (final FieldKey key : keys) {
			if ((o = get(key)) != null) {
				builder.append(o.toString());
				builder.append(FileUtils.lineSeparator);
			}
		}
		
		return builder.toString();
	}
	
	/**
	 * Gets the base type.
	 * 
	 * @return the base type
	 */
	@Transient
	public abstract Class<?> getBaseType();
	
	/**
	 * Gets the generated id.
	 * 
	 * @return the generated id
	 */
	@Id
	@GeneratedValue
	@Access (AccessType.PROPERTY)
	public long getGeneratedId() {
		return this.generatedId;
	}
	
	/**
	 * Gets the handle.
	 * 
	 * @return the handle
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
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Transient
	public abstract String getId();
	
	/**
	 * Gets the size.
	 * 
	 * @param key
	 *            the key
	 * @return the size
	 */
	@Transient
	public int getSize(final FieldKey key) {
		final Object o = get(key);
		return o != null
		                ? CollectionUtils.size(o)
		                : -1;
	}
	
	/**
	 * Gets the text.
	 * 
	 * @return A composition of all text fields
	 */
	@Transient
	public abstract String getText();
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + (getId().hashCode() ^ (getId().hashCode() >>> 32));
		result = (prime * result) + getBaseType().hashCode();
		return result;
	}
	
	/**
	 * Sets the generated id.
	 * 
	 * @param generatedId
	 *            the generatedId to set
	 */
	public final void setGeneratedId(final long generatedId) {
		this.generatedId = generatedId;
	}
	
	/**
	 * Supported.
	 * 
	 * @return the sets the
	 */
	@Transient
	public abstract Set<FieldKey> supported();
}
