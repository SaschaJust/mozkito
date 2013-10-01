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
package org.mozkito.infozilla.model.patch;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
import org.mozkito.persistence.Annotated;
import org.mozkito.persons.model.Person;
import org.mozkito.utilities.commons.JavaUtils;
import org.mozkito.utilities.io.FileUtils;

/**
 * The Class PatchHunk.
 */
@Entity
public class PatchHunk implements Annotated, Inlineable {
	
	/**
	 * The Class Builder.
	 */
	public static class Builder {
		
		/** The text. */
		private final List<PatchTextElement> elements = new LinkedList<>();
		
		/** The new lenght. */
		private Integer                      newLenght;
		
		/** The new start. */
		private Integer                      newStart;
		
		/** The old length. */
		private Integer                      oldLength;
		
		/** The old start. */
		private Integer                      oldStart;
		
		/**
		 * Adds the element.
		 * 
		 * @param element
		 *            the element
		 * @return the builder
		 */
		public Builder addElement(final PatchTextElement element) {
			this.elements.add(element);
			return this;
		}
		
		/**
		 * Adds the elements.
		 * 
		 * @param elements
		 *            the elements
		 * @return the builder
		 */
		public Builder addElements(final Collection<PatchTextElement> elements) {
			this.elements.addAll(elements);
			return this;
		}
		
		/**
		 * Adds the elements.
		 * 
		 * @param elements
		 *            the elements
		 * @return the builder
		 */
		public Builder addElements(final PatchTextElement... elements) {
			if (elements == null) {
				throw new NullPointerException("Argument 'elements' must not be null.");
			}
			for (final PatchTextElement element : elements) {
				this.elements.add(element);
			}
			return this;
		}
		
		/**
		 * Creates the.
		 * 
		 * @return the patch hunk
		 */
		public PatchHunk create() {
			return new PatchHunk(this.elements, this.oldStart, this.newStart, this.oldLength, this.newLenght);
		}
		
		/**
		 * New end.
		 * 
		 * @param newLenth
		 *            the new lenth
		 * @return the builder
		 */
		public Builder newEnd(final Integer newLenth) {
			this.newLenght = newLenth;
			return this;
		}
		
		/**
		 * New start.
		 * 
		 * @param newStart
		 *            the new start
		 * @return the builder
		 */
		public Builder newStart(final Integer newStart) {
			this.newStart = newStart;
			return this;
		}
		
		/**
		 * Old end.
		 * 
		 * @param oldLength
		 *            the old length
		 * @return the builder
		 */
		public Builder oldEnd(final Integer oldLength) {
			this.oldLength = oldLength;
			return this;
		}
		
		/**
		 * Old start.
		 * 
		 * @param oldStart
		 *            the old start
		 * @return the builder
		 */
		public Builder oldStart(final Integer oldStart) {
			this.oldStart = oldStart;
			return this;
		}
	}
	
	/** The Constant serialVersionUID. */
	private static final long      serialVersionUID = 1401913036213504817L;
	
	/** The text. */
	private List<PatchTextElement> elements         = new LinkedList<>();
	
	/** The id. */
	private int                    id;
	
	/** The new lenght. */
	private Integer                newLenght;
	
	/** The new start. */
	private Integer                newStart;
	
	/** The old length. */
	private Integer                oldLength;
	
	/** The old start. */
	private Integer                oldStart;
	
	/** The start position. */
	private Integer                startPosition;
	
	/** The end position. */
	private Integer                endPosition;
	
	/** The posted on. */
	private DateTime               postedOn;
	
	/** The posted by. */
	private Person                 postedBy;
	
	/** The origin. */
	private Attachment             origin;
	
	/**
	 * Instantiates a new patch hunk.
	 * 
	 * @deprecated must only be used by JPA
	 */
	@Deprecated
	public PatchHunk() {
		// stub
	}
	
	/**
	 * Instantiates a new patch hunk.
	 * 
	 * @param elements
	 *            the elements
	 * @param oldStart
	 *            the old start
	 * @param newStart
	 *            the new start
	 * @param oldLength
	 *            the old length
	 * @param newLenght
	 *            the new lenght
	 */
	public PatchHunk(final List<PatchTextElement> elements, final Integer oldStart, final Integer newStart,
	        final Integer oldLength, final Integer newLenght) {
		super();
		this.elements = elements;
		this.oldStart = oldStart;
		this.newStart = newStart;
		this.oldLength = oldLength;
		this.newLenght = newLenght;
	}
	
	/**
	 * Gets the added.
	 * 
	 * @return the added
	 */
	@Transient
	public Collection<PatchTextElement> getAdded() {
		final Collection<PatchTextElement> collection = new LinkedList<>();
		for (final PatchTextElement element : getElements()) {
			if (PatchTextElement.Type.ADDED.equals(element.getType())) {
				collection.add(element);
			}
		}
		return collection;
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
	 * Gets the deleted.
	 * 
	 * @return the deleted
	 */
	@Transient
	public Collection<PatchTextElement> getDeleted() {
		final Collection<PatchTextElement> collection = new LinkedList<>();
		for (final PatchTextElement element : getElements()) {
			if (PatchTextElement.Type.REMOVED.equals(element.getType())) {
				collection.add(element);
			}
		}
		return collection;
	}
	
	/**
	 * Gets the elements.
	 * 
	 * @return the elements
	 */
	@OneToMany (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public List<PatchTextElement> getElements() {
		return this.elements;
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
	 * Gets the new lenght.
	 * 
	 * @return the newLenght
	 */
	@Basic
	public Integer getNewLenght() {
		return this.newLenght;
	}
	
	/**
	 * Gets the new start.
	 * 
	 * @return the newStart
	 */
	@Basic
	public Integer getNewStart() {
		return this.newStart;
	}
	
	/**
	 * Gets the old length.
	 * 
	 * @return the oldLength
	 */
	@Basic
	public Integer getOldLength() {
		return this.oldLength;
	}
	
	/**
	 * Gets the old start.
	 * 
	 * @return the oldStart
	 */
	@Basic
	public Integer getOldStart() {
		return this.oldStart;
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
	 * Sets the elements.
	 * 
	 * @param elements
	 *            the elements to set
	 */
	public void setElements(final List<PatchTextElement> elements) {
		this.elements = elements;
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
	 * Sets the new lenght.
	 * 
	 * @param newLenght
	 *            the newLenght to set
	 */
	public void setNewLenght(final Integer newLenght) {
		this.newLenght = newLenght;
	}
	
	/**
	 * Sets the new start.
	 * 
	 * @param newStart
	 *            the newStart to set
	 */
	public void setNewStart(final Integer newStart) {
		this.newStart = newStart;
	}
	
	/**
	 * Sets the old length.
	 * 
	 * @param oldLength
	 *            the oldLength to set
	 */
	public void setOldLength(final Integer oldLength) {
		this.oldLength = oldLength;
	}
	
	/**
	 * Sets the old start.
	 * 
	 * @param oldStart
	 *            the oldStart to set
	 */
	public void setOldStart(final Integer oldStart) {
		this.oldStart = oldStart;
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
		this.postedOn = postedOn;
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
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		
		builder.append("@@ -");
		builder.append(getOldStart());
		builder.append(",");
		builder.append(getOldLength());
		builder.append(", +");
		builder.append(getNewStart());
		builder.append(",");
		builder.append(getNewLenght());
		builder.append(" @@");
		for (final PatchTextElement element : getElements()) {
			builder.append(FileUtils.lineSeparator);
			builder.append(element);
		}
		return builder.toString();
	}
	
}
