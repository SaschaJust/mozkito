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

package org.mozkito.infozilla.model.source;

import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.joda.time.DateTime;

import org.mozkito.infozilla.elements.Inlineable;
import org.mozkito.infozilla.model.attachment.Attachment;
import org.mozkito.persistence.Annotated;
import org.mozkito.persons.model.Person;
import org.mozkito.utilities.commons.JavaUtils;

/**
 * The Class SourceCode.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
@Entity
public class SourceCode implements Annotated, Inlineable {
	
	/**
	 * The Enum Type.
	 */
	public static enum Type {
		
		/** The java. */
		JAVA;
	}
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8082413221134362120L;
	
	/** The code. */
	private String            code;
	
	/** The end position. */
	private Integer           endPosition;
	
	/** The id. */
	private int               id;
	
	/** The origin. */
	private Attachment        origin;
	
	/** The start position. */
	private Integer           startPosition;
	
	/** The type. */
	private Type              type;
	
	/** The posted on. */
	private DateTime          postedOn;
	
	/** The posted by. */
	private Person            postedBy;
	
	/**
	 * Instantiates a new source code.
	 * 
	 * @deprecated must only be used by JPA
	 */
	@Deprecated
	public SourceCode() {
		// stub
	}
	
	/**
	 * Instantiates a new source code.
	 * 
	 * @param code
	 *            the code
	 * @param type
	 *            the type
	 * @param origin
	 *            the origin
	 */
	public SourceCode(final String code, final Type type, final Attachment origin) {
		super();
		setCode(code);
		setType(type);
		setOrigin(origin);
		setEndPosition(null);
		setStartPosition(null);
	}
	
	/**
	 * Instantiates a new source code.
	 * 
	 * @param code
	 *            the code
	 * @param type
	 *            the type
	 * @param startPosition
	 *            the start position
	 * @param endPosition
	 *            the end position
	 */
	public SourceCode(final String code, final Type type, final Integer startPosition, final Integer endPosition) {
		super();
		setCode(code);
		setType(type);
		setOrigin(null);
		setEndPosition(endPosition);
		setStartPosition(startPosition);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.persistence.Annotated#getClassName()
	 */
	@Override
	@Transient
	public String getClassName() {
		return JavaUtils.getHandle(this);
	}
	
	/**
	 * Gets the code.
	 * 
	 * @return the code
	 */
	@Basic
	public String getCode() {
		return this.code;
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
	 * Gets the origin.
	 * 
	 * @return the origin
	 */
	@ManyToOne (cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
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
		return this.postedOn;
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
	 * Gets the type.
	 * 
	 * @return the type
	 */
	@Enumerated (EnumType.STRING)
	public Type getType() {
		return this.type;
	}
	
	/**
	 * Sets the code.
	 * 
	 * @param code
	 *            the code to set
	 */
	public void setCode(final String code) {
		this.code = code;
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
		builder.append("SourceCode (");
		builder.append(getType());
		builder.append(")").append(System.getProperty("line.separator"));
		final String code = getCode();
		final String output = code;
		// final CodeFormatter formatter = ToolFactory.createCodeFormatter(null);
		// formatter.format(1, null, 1, 1, 1, "");
		// final TextEdit textEdit = formatter.format(CodeFormatter.K_UNKNOWN, code, 0, code.length(), 0, null);
		//
		// final IDocument doc = new Document(code);
		// try {
		// textEdit.apply(doc);
		// output = doc.get();
		// } catch (final MalformedTreeException e) {
		// e.printStackTrace();
		// } catch (final BadLocationException e) {
		// e.printStackTrace();
		// }
		
		builder.append(output);
		builder.append("]");
		return builder.toString();
	}
	
}
