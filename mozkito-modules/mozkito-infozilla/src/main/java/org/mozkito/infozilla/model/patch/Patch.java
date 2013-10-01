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
/**
 * 
 */
package org.mozkito.infozilla.model.patch;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

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

import net.ownhero.dev.regex.Regex;

import org.joda.time.DateTime;

import org.mozkito.infozilla.elements.Attachable;
import org.mozkito.infozilla.elements.Inlineable;
import org.mozkito.infozilla.model.attachment.Attachment;
import org.mozkito.persistence.Annotated;
import org.mozkito.persons.model.Person;
import org.mozkito.utilities.commons.JavaUtils;

/**
 * The Class UnifiedDiff.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
@Entity
public class Patch implements Annotated, Attachable, Inlineable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8943429916879656072L;
	
	/** The end position. */
	private Integer           endPosition;
	
	/** The hunks. */
	private List<PatchHunk>   hunks            = new LinkedList<>();
	
	/** The id. */
	private int               id;
	
	/** The index. */
	private String            index;
	
	/** The modified file. */
	private String            modifiedFile;
	
	/** The new timestamp. */
	private DateTime          newTimestamp;
	
	/** The old timestamp. */
	private DateTime          oldTimestamp;
	
	/** The origin. */
	private Attachment        origin;
	
	/** The original file. */
	private String            originalFile;
	
	/** The start position. */
	private Integer           startPosition;
	
	/** The posted on. */
	private DateTime          postedOn;
	
	/** The posted by. */
	private Person            postedBy;
	
	/**
	 * Adds the hunk.
	 * 
	 * @param hunk
	 *            the hunk
	 */
	@Transient
	public void addHunk(final PatchHunk hunk) {
		getHunks().add(hunk);
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
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.infozilla.elements.Inlineable#getEndPosition()
	 */
	@Override
	@Basic
	public Integer getEndPosition() {
		return this.endPosition;
	}
	
	/**
	 * Gets the hunks.
	 * 
	 * @return the hunks
	 */
	@OneToMany (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public List<PatchHunk> getHunks() {
		return this.hunks;
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
	 * Gets the index.
	 * 
	 * @return the index
	 */
	@Basic
	public String getIndex() {
		return this.index;
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
	 * Gets the modified file.
	 * 
	 * @return the modified file
	 */
	@Basic
	public String getModifiedFile() {
		return this.modifiedFile;
	}
	
	/**
	 * Gets the new java timestamp.
	 * 
	 * @return the new java timestamp
	 */
	@Column (name = "newTimestamp")
	@Temporal (TemporalType.TIMESTAMP)
	public Date getNewJavaTimestamp() {
		return getNewTimestamp() != null
		                                ? getNewTimestamp().toDate()
		                                : null;
	}
	
	/**
	 * Gets the new timestamp.
	 * 
	 * @return the newTimestamp
	 */
	@Transient
	public DateTime getNewTimestamp() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.newTimestamp;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the old java timestamp.
	 * 
	 * @return the old java timestamp
	 */
	@Temporal (TemporalType.TIMESTAMP)
	@Column (name = "oldTimestamp")
	public Date getOldJavaTimestamp() {
		return getNewTimestamp() != null
		                                ? getNewTimestamp().toDate()
		                                : null;
	}
	
	/**
	 * Gets the old timestamp.
	 * 
	 * @return the oldTimestamp
	 */
	@Transient
	public DateTime getOldTimestamp() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.oldTimestamp;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the origin.
	 * 
	 * @return the origin
	 */
	@ManyToOne (cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	public Attachment getOrigin() {
		return this.origin;
	}
	
	/**
	 * Gets the original file.
	 * 
	 * @return the original file
	 */
	@Basic
	public String getOriginalFile() {
		return this.originalFile;
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
		return this.postedOn;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.infozilla.elements.Inlineable#getStartPosition()
	 */
	@Override
	@Basic
	public Integer getStartPosition() {
		return this.startPosition;
	}
	
	/**
	 * Plus minus line to filename.
	 * 
	 * @param input
	 *            the input
	 * @return the string
	 */
	public String PlusMinusLineToFilename(final String input) {
		String temp = input;
		final String pmreg = "([-]{3}|[+]{3})([ \\r\\n\\t]({filename}.*?)[ \\t])";
		final Regex regex = new Regex(pmreg, Pattern.MULTILINE);
		
		if ((regex.find(input)) != null) {
			temp = regex.getGroup("filename").trim();
		}
		
		return temp;
	}
	
	/**
	 * Sets the end position.
	 * 
	 * @param endPosition
	 *            the new end position
	 */
	public void setEndPosition(final int endPosition) {
		this.endPosition = endPosition;
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
	 * Sets the hunks.
	 * 
	 * @param hunks
	 *            the hunks to set
	 */
	public void setHunks(final List<PatchHunk> hunks) {
		this.hunks = hunks;
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
	 * Sets the index.
	 * 
	 * @param index
	 *            the index to set
	 */
	public void setIndex(final String index) {
		this.index = index;
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
	 * Sets the modified file.
	 * 
	 * @param modifiedFile
	 *            the new modified file
	 */
	public void setModifiedFile(final String modifiedFile) {
		this.modifiedFile = modifiedFile;
	}
	
	/**
	 * Sets the new java timestamp.
	 * 
	 * @param date
	 *            the new new java timestamp
	 */
	public void setNewJavaTimestamp(final Date date) {
		setNewTimestamp(date != null
		                            ? new DateTime(date)
		                            : null);
	}
	
	/**
	 * Sets the new timestamp.
	 * 
	 * @param newTimestamp
	 *            the newTimestamp to set
	 */
	public void setNewTimestamp(final DateTime newTimestamp) {
		this.newTimestamp = newTimestamp;
	}
	
	/**
	 * Sets the old java timestamp.
	 * 
	 * @param date
	 *            the new old java timestamp
	 */
	public void setOldJavaTimestamp(final Date date) {
		setNewTimestamp(date != null
		                            ? new DateTime(date)
		                            : null);
	}
	
	/**
	 * Sets the old timestamp.
	 * 
	 * @param oldTimestamp
	 *            the oldTimestamp to set
	 */
	public void setOldTimestamp(final DateTime oldTimestamp) {
		this.oldTimestamp = oldTimestamp;
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
	 * Sets the original file.
	 * 
	 * @param originalFile
	 *            the new original file
	 */
	public void setOriginalFile(final String originalFile) {
		this.originalFile = originalFile;
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
	 *            the new start position
	 */
	public void setStartPosition(final int startPosition) {
		this.startPosition = startPosition;
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
		
		builder.append("Patch [index=");
		builder.append(getIndex());
		builder.append(", originalFile=");
		builder.append(getOriginalFile());
		builder.append(", modifiedFile=");
		builder.append(getModifiedFile());
		builder.append(", oldTimestamp=");
		builder.append(getOldTimestamp());
		builder.append(", newTimestamp=");
		builder.append(getNewTimestamp());
		builder.append(", hunks=");
		builder.append(JavaUtils.collectionToString(getHunks()));
		builder.append("]");
		
		return builder.toString();
	}
	
}
