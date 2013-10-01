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

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.joda.time.DateTime;

import org.mozkito.infozilla.elements.Inlineable;
import org.mozkito.infozilla.model.attachment.Attachment;
import org.mozkito.infozilla.model.itemization.Listing.Type;
import org.mozkito.persistence.Annotated;
import org.mozkito.persons.model.Person;
import org.mozkito.utilities.commons.JavaUtils;

/**
 * The Class ListingEntry.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
@Entity
public class ListingEntry implements Annotated, Inlineable, Iterable<Listing> {
	
	/** The type. */
	private Type              type;
	
	/** The ordinal. */
	private Integer           ordinal;
	
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
	
	/** The stop. */
	private String            stop;
	
	/** The posted on. */
	private DateTime          postedOn;
	
	/** The posted by. */
	private Person            postedBy;
	
	/** The origin. */
	private Attachment        origin;
	
	/**
	 * Instantiates a new listing entry.
	 * 
	 * @deprecated must only be used by JPA
	 */
	@Deprecated
	public ListingEntry() {
		// stub
	}
	
	/**
	 * Instantiates a new listing entry.
	 * 
	 * @param identifier
	 *            the identifier
	 * @param stop
	 *            the stop
	 * @param type
	 *            the type
	 * @param startPosition
	 *            the start position
	 * @param endPosition
	 *            the end position
	 * @param text
	 *            the text
	 */
	public ListingEntry(final String identifier, final String stop, final Type type, final Integer startPosition,
	        final Integer endPosition, final String text) {
		super();
		this.identifier = identifier;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		this.text = text;
		this.stop = stop;
		this.type = type;
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
		return this.endPosition;
	}
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue (strategy = GenerationType.AUTO)
	public int getId() {
		return this.id;
	}
	
	/**
	 * Gets the identifier.
	 * 
	 * @return the identifier
	 */
	@Basic
	public String getIdentifier() {
		return this.identifier;
	}
	
	/**
	 * Gets the java posted on.
	 * 
	 * @return the java posted on
	 */
	@Temporal (TemporalType.TIMESTAMP)
	@Column (name = "postedOn")
	public Date getJavaPostedOn() {
		return getPostedOn() != null
		                            ? getPostedOn().toDate()
		                            : null;
	}
	
	/**
	 * Gets the ordinal.
	 * 
	 * @return the ordinal
	 */
	@Basic
	public Integer getOrdinal() {
		return this.ordinal;
	}
	
	/**
	 * Gets the origin.
	 * 
	 * @return the origin
	 */
	public Attachment getOrigin() {
		return this.origin;
	}
	
	/**
	 * Gets the posted by.
	 * 
	 * @return the postedBy
	 */
	@ManyToOne
	public Person getPostedBy() {
		return this.postedBy;
	}
	
	/**
	 * Gets the posted on.
	 * 
	 * @return the postedOn
	 */
	@Transient
	public DateTime getPostedOn() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.postedOn;
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
		return this.startPosition;
	}
	
	/**
	 * Gets the stop.
	 * 
	 * @return the stop
	 */
	@Basic
	public String getStop() {
		return this.stop;
	}
	
	/**
	 * Gets the sub listings.
	 * 
	 * @return the subListings
	 */
	@OneToMany (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public List<Listing> getSubListings() {
		return this.subListings;
	}
	
	/**
	 * Gets the text.
	 * 
	 * @return the text
	 */
	@Basic
	public String getText() {
		return this.text;
	}
	
	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	@Enumerated (EnumType.STRING)
	public Type getType() {
		return this.type;
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
		this.endPosition = endPosition;
	}
	
	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the id to set
	 */
	public void setId(final int id) {
		this.id = id;
	}
	
	/**
	 * Sets the identifier.
	 * 
	 * @param identifier
	 *            the identifier to set
	 */
	public void setIdentifier(final String identifier) {
		this.identifier = identifier;
	}
	
	/**
	 * Sets the java posted on.
	 * 
	 * @param timestamp
	 *            the new java posted on
	 */
	public void setJavaPostedOn(final Date timestamp) {
		if (timestamp != null) {
			setPostedOn(new DateTime(timestamp));
		} else {
			setPostedOn(null);
		}
	}
	
	/**
	 * Sets the ordinal.
	 * 
	 * @param ordinal
	 *            the ordinal to set
	 */
	public void setOrdinal(final Integer ordinal) {
		this.ordinal = ordinal;
	}
	
	/**
	 * Sets the origin.
	 * 
	 * @param origin
	 *            the origin to set
	 */
	public void setOrigin(final Attachment origin) {
		this.origin = origin;
	}
	
	/**
	 * Sets the posted by.
	 * 
	 * @param postedBy
	 *            the postedBy to set
	 */
	public void setPostedBy(final Person postedBy) {
		this.postedBy = postedBy;
	}
	
	/**
	 * Sets the posted on.
	 * 
	 * @param postedOn
	 *            the postedOn to set
	 */
	public void setPostedOn(final DateTime postedOn) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.postedOn = postedOn;
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
		this.startPosition = startPosition;
	}
	
	/**
	 * Sets the stop.
	 * 
	 * @param stop
	 *            the stop to set
	 */
	public void setStop(final String stop) {
		this.stop = stop;
	}
	
	/**
	 * Sets the sub listings.
	 * 
	 * @param subListings
	 *            the subListings to set
	 */
	public void setSubListings(final List<Listing> subListings) {
		this.subListings = subListings;
	}
	
	/**
	 * Sets the text.
	 * 
	 * @param text
	 *            the text to set
	 */
	public void setText(final String text) {
		this.text = text;
	}
	
	/**
	 * Sets the type.
	 * 
	 * @param type
	 *            the type to set
	 */
	public void setType(final Type type) {
		this.type = type;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		
		builder.append("ListingEntry [type=");
		builder.append(getText());
		builder.append(", identifier=");
		builder.append(getIdentifier());
		builder.append(", stop=");
		builder.append(getStop());
		builder.append(", ordinal=");
		builder.append(getOrdinal());
		builder.append(", text=");
		builder.append(getText());
		builder.append("]");
		
		for (final Listing listing : getSubListings()) {
			builder.append(System.getProperty("line.separator")).append(listing);
		}
		return builder.toString();
	}
	
}
