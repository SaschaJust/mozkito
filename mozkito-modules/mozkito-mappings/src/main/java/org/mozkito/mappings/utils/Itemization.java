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

package org.mozkito.mappings.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.kanuni.annotations.simple.NotNegative;
import net.ownhero.dev.kanuni.conditions.Condition;

import org.mozkito.utilities.commons.JavaUtils;

/**
 * The Class Itemization.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class Itemization implements Collection<ItemizationEntry> {
	
	/** The itemization entries. */
	private final List<ItemizationEntry> itemizationEntries = new LinkedList<>();
	
	/** The identifier. */
	private final String                 identifier;
	
	/** The text. */
	private final String                 text;
	
	/**
	 * Instantiates a new itemization.
	 * 
	 * @param identifier
	 *            the identifier
	 * @param text
	 *            the text
	 */
	public Itemization(final String identifier, final String text) {
		// PRECONDITIONS
		
		try {
			this.identifier = identifier;
			this.text = text;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.identifier, "Field '%s' in '%s'.", "identifier", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
			Condition.notNull(this.text, "Field '%s' in '%s'.", "text", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	@Override
	public boolean add(@NotNegative final ItemizationEntry e) {
		// PRECONDITIONS
		Condition.notNull(this.itemizationEntries, "Field '%s' in '%s'.", "itemizationEntries", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.itemizationEntries.add(e);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(@NotNegative final Collection<? extends ItemizationEntry> c) {
		// PRECONDITIONS
		Condition.notNull(this.itemizationEntries, "Field '%s' in '%s'.", "itemizationEntries", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.itemizationEntries.addAll(c);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Collection#clear()
	 */
	@Override
	public void clear() {
		// PRECONDITIONS
		Condition.notNull(this.itemizationEntries, "Field '%s' in '%s'.", "itemizationEntries", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			this.itemizationEntries.clear();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(final Object o) {
		// PRECONDITIONS
		Condition.notNull(this.itemizationEntries, "Field '%s' in '%s'.", "itemizationEntries", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.itemizationEntries.contains(o);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(final Collection<?> c) {
		// PRECONDITIONS
		Condition.notNull(this.itemizationEntries, "Field '%s' in '%s'.", "itemizationEntries", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.itemizationEntries.containsAll(c);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the simple name of the class.
	 * 
	 * @return the simple name of the class.
	 */
	public final String getClassName() {
		return JavaUtils.getHandle(Itemization.class);
	}
	
	/**
	 * Gets the identifier.
	 * 
	 * @return the identifier
	 */
	public String getIdentifier() {
		// PRECONDITIONS
		
		try {
			return this.identifier;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.identifier, "Field '%s' in '%s'.", "identifier", getClass().getSimpleName());
		}
	}
	
	/**
	 * Gets the itemization entries.
	 * 
	 * @return the itemization entries
	 */
	public List<ItemizationEntry> getItemizationEntries() {
		// PRECONDITIONS
		
		try {
			return Collections.unmodifiableList(this.itemizationEntries);
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.itemizationEntries, "Field '%s' in '%s'.", "itemizationEntries",
			                  getClass().getSimpleName());
		}
	}
	
	/**
	 * Gets the text.
	 * 
	 * @return the text
	 */
	public String getText() {
		// PRECONDITIONS
		
		try {
			return this.text;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.text, "Field '%s' in '%s'.", "text", getClass().getSimpleName());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Collection#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		// PRECONDITIONS
		Condition.notNull(this.itemizationEntries, "Field '%s' in '%s'.", "itemizationEntries", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.itemizationEntries.isEmpty();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Collection#iterator()
	 */
	@Override
	public Iterator<ItemizationEntry> iterator() {
		// PRECONDITIONS
		Condition.notNull(this.itemizationEntries, "Field '%s' in '%s'.", "itemizationEntries", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.itemizationEntries.iterator();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(final Object o) {
		// PRECONDITIONS
		Condition.notNull(this.itemizationEntries, "Field '%s' in '%s'.", "itemizationEntries", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.itemizationEntries.remove(o);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(final Collection<?> c) {
		// PRECONDITIONS
		Condition.notNull(this.itemizationEntries, "Field '%s' in '%s'.", "itemizationEntries", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.itemizationEntries.removeAll(c);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	@Override
	public boolean retainAll(final Collection<?> c) {
		// PRECONDITIONS
		Condition.notNull(this.itemizationEntries, "Field '%s' in '%s'.", "itemizationEntries", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.itemizationEntries.retainAll(c);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Collection#size()
	 */
	@Override
	public int size() {
		// PRECONDITIONS
		Condition.notNull(this.itemizationEntries, "Field '%s' in '%s'.", "itemizationEntries", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.itemizationEntries.size();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Collection#toArray()
	 */
	@Override
	public Object[] toArray() {
		// PRECONDITIONS
		Condition.notNull(this.itemizationEntries, "Field '%s' in '%s'.", "itemizationEntries", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.itemizationEntries.toArray();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Collection#toArray(T[])
	 */
	@Override
	public <T> T[] toArray(final T[] a) {
		// PRECONDITIONS
		Condition.notNull(this.itemizationEntries, "Field '%s' in '%s'.", "itemizationEntries", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.itemizationEntries.toArray(a);
		} finally {
			// POSTCONDITIONS
		}
	}
}
