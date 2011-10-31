/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.mapping.mappable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;

import net.ownhero.dev.ioda.FileUtils;

import org.apache.commons.collections.CollectionUtils;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;

/**
 * @author just
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
		Map<FieldKey, Object> ret = new HashMap<FieldKey, Object>();
		
		for (FieldKey key : keys) {
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
		
		for (FieldKey key : keys) {
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
		StringBuilder builder = new StringBuilder();
		Object o = null;
		
		for (FieldKey key : keys) {
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
	
	/**
	 * @param key
	 * @return
	 */
	@Transient
	public int getSize(final FieldKey key) {
		Object o = get(key);
		return o != null
		                ? CollectionUtils.size(o)
		                : -1;
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
	
	/**
	 * @return
	 */
	@Transient
	public abstract Set<FieldKey> supported();
}
