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

package org.mozkito.infozilla.model.itemization;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
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
 * The Class ListingEntry.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
@Entity
public class ListingEntry implements Annotated, Inlineable, Iterable<Listing> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1815282586177047074L;
	
	/** The end position. */
	private Integer           endPosition;
	
	/** The id. */
	private int               id;
	
	/** The identifier. */
	private String            identifier;
	
	/** The start position. */
	private Integer           startPosition;
	
	/** The sub listings. */
	private List<Listing>     subListings      = new LinkedList<>();
	
	/** The text. */
	private String            text;
	
	/**
	 * @param identifier
	 * @param startPosition
	 * @param endPosition
	 * @param text
	 */
	public ListingEntry(final String identifier, final Integer startPosition, final Integer endPosition,
	        final String text) {
		super();
		this.identifier = identifier;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		this.text = text;
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
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue (strategy = GenerationType.AUTO)
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
	 * Gets the identifier.
	 * 
	 * @return the identifier
	 */
	@Basic
	public String getIdentifier() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.identifier;
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
	 * Gets the sub listings.
	 * 
	 * @return the subListings
	 */
	@OneToMany (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public List<Listing> getSubListings() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.subListings;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the text.
	 * 
	 * @return the text
	 */
	@Basic
	public String getText() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.text;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Listing> iterator() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return getSubListings().iterator();
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
	 * Sets the identifier.
	 * 
	 * @param identifier
	 *            the identifier to set
	 */
	public void setIdentifier(final String identifier) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.identifier = identifier;
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
	 * Sets the sub listings.
	 * 
	 * @param subListings
	 *            the subListings to set
	 */
	public void setSubListings(final List<Listing> subListings) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.subListings = subListings;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the text.
	 * 
	 * @param text
	 *            the text to set
	 */
	public void setText(final String text) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.text = text;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
}
