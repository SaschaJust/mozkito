/*******************************************************************************
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
 ******************************************************************************/
package de.unisaarland.cs.st.moskito.mapping.mappable.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
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

import org.apache.commons.collections.CollectionUtils;

import de.unisaarland.cs.st.moskito.mapping.mappable.FieldKey;
import de.unisaarland.cs.st.moskito.persistence.Annotated;

/**
 * Superclass that is used to wrap around classes that shall be mapped. Since inheritance based annotations do not work
 * on interfaces we can't simply use {@link Annotated} here.
 * 
 * Access to the internal data is used through access with {@link FieldKey}s. The corresponding data is mangled to fit
 * the proper format.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Entity
@Access (AccessType.PROPERTY)
@Inheritance (strategy = InheritanceType.JOINED)
@DiscriminatorColumn (name = "TYPE", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue ("MAPPABLEENTITY")
public abstract class MappableEntity implements Annotated {
	
	private static final long serialVersionUID = 2350328785752088197L;
	private long              generatedId;
	private int               test;
	
	/**
	 * @param key
	 * @return
	 */
	@Transient
	public abstract Object get(FieldKey key);
	
	/**
	 * @param key
	 * @param index
	 * @return
	 */
	@Transient
	public abstract Object get(FieldKey key,
	                           int index);
	
	/**
	 * @param keys
	 * @return
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
	 * @param keys
	 * @return
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
	 * @param keys
	 * @return
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
	 * @return
	 */
	@Transient
	public abstract Class<?> getBaseType();
	
	/**
	 * @return
	 */
	@Id
	@GeneratedValue
	@Access (AccessType.PROPERTY)
	public long getGeneratedId() {
		return this.generatedId;
	}
	
	/**
	 * @return
	 */
	@Transient
	public String getHandle() {
		return this.getClass().getSimpleName();
	}
	
	@Transient
	public abstract String getId();
	
	/**
	 * @param key
	 * @return
	 */
	@Transient
	public int getSize(final FieldKey key) {
		final Object o = get(key);
		return o != null
		                ? CollectionUtils.size(o)
		                : -1;
	}
	
	@Basic
	@Access (AccessType.PROPERTY)
	public int getTest() {
		return this.test;
	}
	
	/**
	 * @return A composition of all text fields
	 */
	@Transient
	public abstract String getText();
	
	/**
	 * @param generatedId
	 *            the generatedId to set
	 */
	public final void setGeneratedId(final long generatedId) {
		this.generatedId = generatedId;
	}
	
	public void setTest(final int test) {
		this.test = test;
	}
	
	/**
	 * @return
	 */
	@Transient
	public abstract Set<FieldKey> supported();
}
