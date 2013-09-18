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
package org.mozkito.infozilla.model.itemization;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.mozkito.infozilla.elements.Inlineable;
import org.mozkito.persistence.Annotated;
import org.mozkito.utilities.commons.JavaUtils;

/**
 * This class encapsulates an Itemization as created by FilterEnumerations filter.
 * 
 * @author Nicolas Bettenburg
 * 
 */
@Entity
public class Listing implements Annotated, Inlineable, List<ListingEntry> {
	
	/**
	 * The Enum Type.
	 */
	public static enum Type {
		
		/** The enumeration. */
		ENUMERATION,
		/** The itemization. */
		ITEMIZATION;
	}
	
	/** The Constant serialVersionUID. */
	private static final long  serialVersionUID = -1382858134520737804L;
	
	/** The end position. */
	private Integer            endPosition;
	
	/** The entries. */
	private List<ListingEntry> entries          = new LinkedList<>();
	
	/** The id. */
	private int                id;
	
	/** The start position. */
	private Integer            startPosition;
	
	/** The type. */
	private Type               type;
	
	/**
	 * @param type
	 * @param startPosition
	 * @param endPosition
	 * @param entries
	 */
	public Listing(final Type type, final Integer startPosition, final Integer endPosition) {
		super();
		this.type = type;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		this.entries = this.entries;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.util.List#add(int, java.lang.Object)
	 */
	@Override
	@Transient
	public void add(final int index,
	                final ListingEntry element) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			getEntries().add(index, element);
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.util.List#add(java.lang.Object)
	 */
	@Override
	@Transient
	public boolean add(final ListingEntry e) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return getEntries().add(e);
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.util.List#addAll(java.util.Collection)
	 */
	@Override
	@Transient
	public boolean addAll(final Collection<? extends ListingEntry> c) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return getEntries().addAll(c);
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.util.List#addAll(int, java.util.Collection)
	 */
	@Override
	@Transient
	public boolean addAll(final int index,
	                      final Collection<? extends ListingEntry> c) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return getEntries().addAll(index, c);
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.util.List#clear()
	 */
	@Override
	@Transient
	public void clear() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			getEntries().clear();
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.util.List#contains(java.lang.Object)
	 */
	@Override
	@Transient
	public boolean contains(final Object o) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return getEntries().contains(o);
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.util.List#containsAll(java.util.Collection)
	 */
	@Override
	@Transient
	public boolean containsAll(final Collection<?> c) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return getEntries().containsAll(c);
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.util.List#get(int)
	 */
	@Override
	@Transient
	public ListingEntry get(final int index) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return getEntries().get(index);
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.persistence.Annotated#getClassName()
	 */
	@Override
	@Transient
	public String getClassName() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return JavaUtils.getHandle(this);
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the end position.
	 * 
	 * @return the endPosition
	 */
	@Basic
	public Integer getEndPosition() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.endPosition;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the entries.
	 * 
	 * @return the entries
	 */
	@OneToMany (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public List<ListingEntry> getEntries() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.entries;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue (strategy = GenerationType.AUTO)
	@Access (AccessType.PROPERTY)
	public int getId() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.id;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the start position.
	 * 
	 * @return the startPosition
	 */
	@Basic
	public Integer getStartPosition() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.startPosition;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	@Enumerated (EnumType.STRING)
	public Type getType() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.type;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	@Override
	@Transient
	public int indexOf(final Object o) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return getEntries().indexOf(o);
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.util.List#isEmpty()
	 */
	@Override
	@Transient
	public boolean isEmpty() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return getEntries().isEmpty();
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.util.List#iterator()
	 */
	@Override
	@Transient
	public Iterator<ListingEntry> iterator() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return getEntries().iterator();
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.util.List#lastIndexOf(java.lang.Object)
	 */
	@Override
	@Transient
	public int lastIndexOf(final Object o) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return getEntries().lastIndexOf(o);
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.util.List#listIterator()
	 */
	@Override
	@Transient
	public ListIterator<ListingEntry> listIterator() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return getEntries().listIterator();
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.util.List#listIterator(int)
	 */
	@Override
	@Transient
	public ListIterator<ListingEntry> listIterator(final int index) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return getEntries().listIterator(index);
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.util.List#remove(int)
	 */
	@Override
	@Transient
	public ListingEntry remove(final int index) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return getEntries().remove(index);
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.util.List#remove(java.lang.Object)
	 */
	@Override
	@Transient
	public boolean remove(final Object o) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return getEntries().remove(o);
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.util.List#removeAll(java.util.Collection)
	 */
	@Override
	@Transient
	public boolean removeAll(final Collection<?> c) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return getEntries().removeAll(c);
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.util.List#retainAll(java.util.Collection)
	 */
	@Override
	@Transient
	public boolean retainAll(final Collection<?> c) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return getEntries().retainAll(c);
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.util.List#set(int, java.lang.Object)
	 */
	@Override
	@Transient
	public ListingEntry set(final int index,
	                        final ListingEntry element) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return getEntries().set(index, element);
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the end position.
	 * 
	 * @param endPosition
	 *            the endPosition to set
	 */
	public void setEndPosition(final Integer endPosition) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.endPosition = endPosition;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the entries.
	 * 
	 * @param entries
	 *            the entries to set
	 */
	public void setEntries(final List<ListingEntry> entries) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.entries = entries;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the id to set
	 */
	public void setId(final int id) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.id = id;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the start position.
	 * 
	 * @param startPosition
	 *            the startPosition to set
	 */
	@Basic
	public void setStartPosition(final Integer startPosition) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.startPosition = startPosition;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the type.
	 * 
	 * @param type
	 *            the type to set
	 */
	public void setType(final Type type) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.type = type;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.util.List#size()
	 */
	@Override
	@Transient
	public int size() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return getEntries().size();
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.util.List#subList(int, int)
	 */
	@Override
	@Transient
	public List<ListingEntry> subList(final int fromIndex,
	                                  final int toIndex) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return getEntries().subList(fromIndex, toIndex);
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.util.List#toArray()
	 */
	@Override
	@Transient
	public Object[] toArray() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return getEntries().toArray();
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.util.List#toArray(T[])
	 */
	@Override
	@Transient
	public <T> T[] toArray(final T[] a) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return getEntries().toArray(a);
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
}
