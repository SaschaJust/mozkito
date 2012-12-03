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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kanuni.annotations.compare.Less;
import net.ownhero.dev.kanuni.annotations.meta.Marker;
import net.ownhero.dev.kanuni.annotations.simple.NotNegative;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.annotations.simple.Positive;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kanuni.conditions.StringCondition;

/**
 * The Class EnumerationEntry.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class EnumerationEntry {
	
	/** The identifier. */
	private final String            identifier;
	
	/** The start. */
	private final int               start;
	
	/** The end. */
	private int                     end;
	
	/** The parent. */
	private final Enumeration       parent;
	
	/** The child enumerations. */
	private final List<Enumeration> childEnumerations = new LinkedList<>();
	
	/** The child itemizations. */
	private final List<Itemization> childItemizations = new LinkedList<>();
	
	/**
	 * Instantiates a new enumeration entry.
	 * 
	 * @param enumeration
	 *            the enumeration
	 * @param identifier
	 *            the identifier
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 */
	public EnumerationEntry(@NotNull final Enumeration enumeration, @NotNull final String identifier,
	        @NotNegative @Less final int start, @Positive @Marker final int end) {
		// PRECONDITIONS
		
		try {
			this.identifier = identifier;
			this.parent = enumeration;
			this.start = start;
			this.end = end;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.identifier, "Field '%s' in '%s'.", "this.identifier", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			Condition.notNull(this.parent, "Field '%s' in '%s'.", "this.parent", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			CompareCondition.positive(this.end, "Field '%s' in '%s'.", "this.end", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Adds the.
	 * 
	 * @param enumeration
	 *            the enumeration
	 * @return true, if successful
	 */
	public boolean add(@NotNull final Enumeration enumeration) {
		// PRECONDITIONS
		Condition.notNull(this.childEnumerations, "Field '%s' in '%s'.", "childEnumerations", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.childEnumerations.add(enumeration);
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.childEnumerations, "Field '%s' in '%s'.", "childEnumerations", //$NON-NLS-1$ //$NON-NLS-2$
			                  getClass().getSimpleName());
		}
	}
	
	/**
	 * Adds the.
	 * 
	 * @param itemization
	 *            the itemization
	 * @return true, if successful
	 */
	public boolean add(@NotNull final Itemization itemization) {
		// PRECONDITIONS
		Condition.notNull(this.childItemizations, "Field '%s' in '%s'.", "childEnumerations", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			return this.childItemizations.add(itemization);
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.childItemizations, "Field '%s' in '%s'.", "childEnumerations", //$NON-NLS-1$ //$NON-NLS-2$
			                  getClass().getSimpleName());
		}
	}
	
	/**
	 * Gets the child enumerations.
	 * 
	 * @return the child enumerations
	 */
	public final List<Enumeration> getChildEnumerations() {
		// PRECONDITIONS
		
		try {
			return Collections.unmodifiableList(this.childEnumerations);
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.childEnumerations, "Field '%s' in '%s'.", "childEnumerations",
			                  getClass().getSimpleName());
		}
	}
	
	/**
	 * Gets the child itemizations.
	 * 
	 * @return the child itemizations
	 */
	public final List<Itemization> getChildItemizations() {
		// PRECONDITIONS
		
		try {
			return Collections.unmodifiableList(this.childItemizations);
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.childItemizations, "Field '%s' in '%s'.", "childItemizations",
			                  getClass().getSimpleName());
		}
	}
	
	/**
	 * Gets the end.
	 * 
	 * @return the end
	 */
	public int getEnd() {
		// PRECONDITIONS
		
		try {
			return this.end;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.end, "Field '%s' in '%s'.", "end", getClass().getSimpleName());
		}
	}
	
	/**
	 * Gets the simple name of the class.
	 * 
	 * @return the simple name of the class.
	 */
	public final String getHandle() {
		return JavaUtils.getHandle(EnumerationEntry.class);
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
	 * Gets the parent.
	 * 
	 * @return the parent
	 */
	public Enumeration getParent() {
		// PRECONDITIONS
		
		try {
			return this.parent;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.parent, "Field '%s' in '%s'.", "parent", getClass().getSimpleName());
		}
	}
	
	/**
	 * Gets the start.
	 * 
	 * @return the start
	 */
	public int getStart() {
		// PRECONDITIONS
		
		try {
			return this.start;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.start, "Field '%s' in '%s'.", "start", getClass().getSimpleName());
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
			StringCondition.minLength(getParent().getText(), this.end, "Invalid end values specified.");
			return getParent().getText().substring(this.start, this.end);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the end.
	 * 
	 * @param i
	 *            the new end
	 */
	public void setEnd(final int i) {
		// PRECONDITIONS
		
		try {
			this.end = i;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		
		builder.append("EnumerationEntry [identifier=");
		builder.append(this.identifier);
		builder.append(", start=");
		builder.append(this.start);
		builder.append(", end=");
		builder.append(this.end);
		builder.append("]");
		builder.append(FileUtils.lineSeparator);
		builder.append(getText());
		
		return builder.toString();
	}
}
