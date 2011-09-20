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
package de.unisaarland.cs.st.reposuite.mapping.requirements;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.mapping.mappable.FieldKey;
import de.unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity;

public class Atom extends Expression {
	
	private final Index         idx;
	private Class<?>            type;
	private final Set<FieldKey> keys = new HashSet<FieldKey>();
	
	/**
	 * @param idx
	 * @param type
	 */
	public Atom(final Index idx, final Class<?> type) {
		this.idx = idx;
		this.type = type;
	}
	
	/**
	 * @param idx
	 * @param key
	 */
	public Atom(final Index idx, final FieldKey key) {
		this.idx = idx;
		this.keys.add(key);
	}
	
	/**
	 * @param idx
	 * @param keys
	 */
	public Atom(final Index idx, final FieldKey... keys) {
		this.idx = idx;
		CollectionUtils.addAll(this.keys, keys);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.requirements.Expression#check(
	 * java.lang.Class, java.lang.Class,
	 * de.unisaarland.cs.st.reposuite.mapping.requirements.Index)
	 */
	@Override
	public boolean check(final Class<? extends MappableEntity> target1,
	                     final Class<? extends MappableEntity> target2,
	                     final Index oneEquals) {
		switch (this.idx) {
			case FROM:
				if (this.type != null) {
					return target1 == this.type;
				} else {
					try {
						return target1.newInstance().supported().containsAll(this.keys);
					} catch (Exception e) {
						throw new UnrecoverableError(e.getMessage(), e);
					}
				}
			case TO:
				if (this.type != null) {
					return target2 == this.type;
				} else {
					try {
						return target2.newInstance().supported().containsAll(this.keys);
					} catch (Exception e) {
						throw new UnrecoverableError(e.getMessage(), e);
					}
				}
			case ONE:
				if (oneEquals.equals(Index.FROM)) {
					if (this.type != null) {
						return target1 == this.type;
					} else {
						try {
							return target1.newInstance().supported().containsAll(this.keys);
						} catch (Exception e) {
							throw new UnrecoverableError(e.getMessage(), e);
						}
					}
				} else {
					if (this.type != null) {
						return target2 == this.type;
					} else {
						try {
							return target2.newInstance().supported().containsAll(this.keys);
						} catch (Exception e) {
							throw new UnrecoverableError(e.getMessage(), e);
						}
					}
				}
			case OTHER:
				if (!oneEquals.equals(Index.FROM)) {
					if (this.type != null) {
						return target1 == this.type;
					} else {
						try {
							return target1.newInstance().supported().containsAll(this.keys);
						} catch (Exception e) {
							throw new UnrecoverableError(e.getMessage(), e);
						}
					}
				} else {
					if (this.type != null) {
						return target2 == this.type;
					} else {
						try {
							return target2.newInstance().supported().containsAll(this.keys);
						} catch (Exception e) {
							throw new UnrecoverableError(e.getMessage(), e);
						}
					}
				}
			default:
				break;
		}
		return false;
	}
	
	/**
	 * @return
	 */
	public Index getIdx() {
		return this.idx;
	}
	
	/**
	 * @return
	 */
	public Set<FieldKey> getKeys() {
		return this.keys;
	}
	
	/**
	 * @return
	 */
	public Class<?> getType() {
		return this.type;
	}
}
