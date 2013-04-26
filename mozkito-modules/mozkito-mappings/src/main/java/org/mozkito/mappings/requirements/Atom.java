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
package org.mozkito.mappings.requirements;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;

import org.apache.commons.collections.CollectionUtils;

import org.mozkito.mappings.mappable.FieldKey;
import org.mozkito.mappings.mappable.model.MappableEntity;
import org.mozkito.utilities.commons.JavaUtils;

/**
 * The atom expression evaluates to true if the given criteria are met. Evaluates to false otherwise.
 * 
 * If a type is given, the {@link Atom#check(Class, Class, Index)} method checks if the instance corresponding to the
 * given index matches the given type/class. If a/multiple {@link FieldKey}(s) is/are given, the check method is looking
 * up if the instance corresponding to the given index supports this type of field (determined by {@link FieldKey}).
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public final class Atom extends Expression {
	
	/** The idx. */
	private final Index         idx;
	
	/** The keys. */
	private final Set<FieldKey> keys = new HashSet<FieldKey>();
	
	/** The type. */
	private Class<?>            type;
	
	/**
	 * Instantiates a new atom.
	 * 
	 * @param idx
	 *            the index the atom refers to
	 * @param type
	 *            the type that the corresponding entity is required to match
	 */
	public Atom(@NotNull final Index idx, final Class<?> type) {
		this.idx = idx;
		this.type = type;
	}
	
	/**
	 * Instantiates a new atom.
	 * 
	 * @param idx
	 *            the index the atom refers to
	 * @param key
	 *            the {@link FieldKey} the entity has to support
	 */
	public Atom(@NotNull final Index idx, final FieldKey key) {
		this.idx = idx;
		this.keys.add(key);
	}
	
	/**
	 * Instantiates a new atom.
	 * 
	 * @param idx
	 *            the index the atom refers to
	 * @param keys
	 *            the {@link FieldKey}s the entity has to support
	 */
	public Atom(@NotNull final Index idx, final FieldKey... keys) {
		this.idx = idx;
		CollectionUtils.addAll(this.keys, keys);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.requirements.Expression#check( java.lang.Class, java.lang.Class,
	 * org.mozkito.mapping.requirements.Index)
	 */
	@Override
	public boolean check(final Class<? extends MappableEntity> target1,
	                     final Class<? extends MappableEntity> target2,
	                     final Index oneEquals) {
		switch (this.idx) {
			case FROM:
				if (this.type != null) {
					try {
						return target1.newInstance().getBaseType() == this.type;
					} catch (final Exception e) {
						throw new UnrecoverableError(e.getMessage(), e);
					}
				} else {
					try {
						return target1.newInstance().supported().containsAll(this.keys);
					} catch (final Exception e) {
						throw new UnrecoverableError(e.getMessage(), e);
					}
				}
			case TO:
				if (this.type != null) {
					try {
						return target2.newInstance().getBaseType() == this.type;
					} catch (final Exception e) {
						throw new UnrecoverableError(e.getMessage(), e);
					}
				} else {
					try {
						return target2.newInstance().supported().containsAll(this.keys);
					} catch (final Exception e) {
						throw new UnrecoverableError(e.getMessage(), e);
					}
				}
			case ONE:
				if (oneEquals.equals(Index.FROM)) {
					if (this.type != null) {
						try {
							return target1.newInstance().getBaseType() == this.type;
						} catch (final Exception e) {
							throw new UnrecoverableError(e.getMessage(), e);
						}
					} else {
						try {
							return target1.newInstance().supported().containsAll(this.keys);
						} catch (final Exception e) {
							throw new UnrecoverableError(e.getMessage(), e);
						}
					}
				} else {
					if (this.type != null) {
						try {
							return target2.newInstance().getBaseType() == this.type;
						} catch (final Exception e) {
							throw new UnrecoverableError(e.getMessage(), e);
						}
					} else {
						try {
							return target2.newInstance().supported().containsAll(this.keys);
						} catch (final Exception e) {
							throw new UnrecoverableError(e.getMessage(), e);
						}
					}
				}
			case OTHER:
				if (!oneEquals.equals(Index.FROM)) {
					if (this.type != null) {
						try {
							return target1.newInstance().getBaseType() == this.type;
						} catch (final Exception e) {
							throw new UnrecoverableError(e.getMessage(), e);
						}
					} else {
						try {
							return target1.newInstance().supported().containsAll(this.keys);
						} catch (final Exception e) {
							throw new UnrecoverableError(e.getMessage(), e);
						}
					}
				} else {
					if (this.type != null) {
						try {
							return target2.newInstance().getBaseType() == this.type;
						} catch (final Exception e) {
							throw new UnrecoverableError(e.getMessage(), e);
						}
					} else {
						try {
							return target2.newInstance().supported().containsAll(this.keys);
						} catch (final Exception e) {
							throw new UnrecoverableError(e.getMessage(), e);
						}
					}
				}
			default:
				break;
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.requirements.Expression#getFailureCause (java.lang.Class, java.lang.Class,
	 * org.mozkito.mapping.requirements.Index)
	 */
	@Override
	public List<Expression> getFailureCause(final Class<? extends MappableEntity> target1,
	                                        final Class<? extends MappableEntity> target2,
	                                        final Index oneEquals) {
		return check(target1, target2, oneEquals)
		                                         ? null
		                                         : new LinkedList<Expression>() {
			                                         
			                                         private static final long serialVersionUID = 1L;
			                                         
			                                         {
				                                         add(Atom.this);
			                                         }
		                                         };
	}
	
	/**
	 * Gets the idx.
	 * 
	 * @return the index the atom refers to
	 */
	public Index getIdx() {
		return this.idx;
	}
	
	/**
	 * Gets the keys.
	 * 
	 * @return the {@link FieldKey}s the atom refers to, if any. Returns an empty set if none were specified.
	 */
	public Set<FieldKey> getKeys() {
		return this.keys;
	}
	
	/**
	 * Gets the type.
	 * 
	 * @return the type the atom refers to. Returns <code>null</code> otherwise.
	 */
	public Class<?> getType() {
		return this.type;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.requirements.Expression#toString()
	 */
	@Override
	public String toString() {
		if (this.type != null) {
			return "(" + this.idx.name() + "<type> = " + this.type.getSimpleName() + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} else {
			return "(" + this.idx.name() + "<fields> ⊇ " + JavaUtils.collectionToString(this.keys) + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}
}
