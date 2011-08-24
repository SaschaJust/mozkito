/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.mapping.mappable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.ioda.FileUtils;

import org.apache.commons.collections.CollectionUtils;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;

/**
 * @author just
 * 
 */
public abstract class MappableEntity implements Annotated {
	
	/**
     * 
     */
	private static final long serialVersionUID = 2350328785752088197L;
	
	public abstract Class<?> getBaseType();
	
	public abstract Object get(FieldKey key);
	
	public abstract Object get(FieldKey key, int index);
	
	/**
	 * @return
	 */
	public String getHandle() {
		return this.getClass().getSimpleName();
	}
	
	/**
	 * @param keys
	 * @return
	 */
	public Object getAny(FieldKey... keys) {
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
	 * @return A composition of all text fields
	 */
	public abstract String getText();
	
	/**
	 * @param key
	 * @return
	 */
	public int getSize(FieldKey key) {
		Object o = get(key);
		return o != null ? CollectionUtils.size(o) : -1;
	}
	
	/**
	 * @param keys
	 * @return
	 */
	public Map<FieldKey, Object> getAll(FieldKey... keys) {
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
	public String getAsOneString(FieldKey... keys) {
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
	
	public abstract Set<FieldKey> supported();
}
