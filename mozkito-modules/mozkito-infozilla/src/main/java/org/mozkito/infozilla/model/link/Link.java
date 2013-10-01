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
package org.mozkito.infozilla.model.link;

import java.net.MalformedURLException;
import java.net.URL;
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
 * The Class Link.
 */
@Entity
public class Link implements Annotated, Inlineable {
	
	/**
	 * The Enum Kind.
	 */
	public static enum Kind {
		
		/** The REPOSITORY. */
		REPOSITORY,
		/** The TRACKER. */
		TRACKER,
		/** The WEB. */
		WEB;
	}
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2748102304953716136L;
	
	/** The end position. */
	private Integer           endPosition;
	
	/** The id. */
	private int               id;
	
	/** The kind. TODO set this with some heuristics */
	private Kind              kind             = Kind.WEB;
	
	/** The posted by. */
	private Person            postedBy;
	
	/** The posted on. */
	private DateTime          postedOn;
	
	/** The origin. */
	private Attachment        origin;
	
	/** The start position. */
	private Integer           startPosition;
	
	/** The string representation. */
	private String            stringRepresentation;
	
	/** The url. */
	private URL               url;
	
	/** The verified. */
	private boolean           verified;
	
	/** The link description. */
	private String            linkDescription;
	
	/** The scheme. */
	private String            scheme;
	
	/**
	 * Instantiates a new link.
	 * 
	 * @param startPosition
	 *            the start position
	 * @param endPosition
	 *            the end position
	 * @param url
	 *            the url
	 * @param stringRepresentation
	 *            the string representation
	 * @param scheme
	 *            the scheme
	 * @param linkDescription
	 *            the link description
	 */
	public Link(final int startPosition, final int endPosition, final URL url, final String stringRepresentation,
	        final String scheme, final String linkDescription) {
		super();
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		this.url = url;
		this.stringRepresentation = stringRepresentation;
		this.scheme = scheme;
		this.linkDescription = linkDescription;
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
	 * Gets the kind.
	 * 
	 * @return the kind
	 */
	@Enumerated (EnumType.STRING)
	public Kind getKind() {
		return this.kind;
	}
	
	/**
	 * Gets the link description.
	 * 
	 * @return the linkDescription
	 */
	@Basic
	public String getLinkDescription() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.linkDescription;
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
	public Attachment getOrigin() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.origin;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the posted by.
	 * 
	 * @return the postedBy
	 */
	@ManyToOne (cascade = { CascadeType.REFRESH }, fetch = FetchType.LAZY)
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
	 * Gets the posted on java.
	 * 
	 * @return the posted on java
	 */
	@Column (name = "postedOn")
	@Temporal (TemporalType.TIMESTAMP)
	public Date getPostedOnJava() {
		return getPostedOn() != null
		                            ? getPostedOn().toDate()
		                            : null;
	}
	
	/**
	 * Gets the scheme.
	 * 
	 * @return the scheme
	 */
	@Basic
	public String getScheme() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.scheme;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
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
	 * Gets the string representation.
	 * 
	 * @return the stringRepresentation
	 */
	@Basic
	public String getStringRepresentation() {
		return this.stringRepresentation;
	}
	
	/**
	 * Gets the url.
	 * 
	 * @return the url
	 */
	@Transient
	public URL getUrl() {
		return this.url;
	}
	
	/**
	 * Gets the url string.
	 * 
	 * @return the url string
	 */
	public String getUrlString() {
		return getUrl().toExternalForm();
	}
	
	/**
	 * Checks if is verified.
	 * 
	 * @return the verified
	 */
	public boolean isVerified() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.verified;
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
	 * Sets the kind.
	 * 
	 * @param kind
	 *            the kind to set
	 */
	public void setKind(final Kind kind) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.kind = kind;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the link description.
	 * 
	 * @param linkDescription
	 *            the linkDescription to set
	 */
	public void setLinkDescription(final String linkDescription) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.linkDescription = linkDescription;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the origin.
	 * 
	 * @param origin
	 *            the origin to set
	 */
	public void setOrigin(final Attachment origin) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.origin = origin;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
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
	 * Sets the posted on java.
	 * 
	 * @param postedOn
	 *            the new posted on java
	 */
	public void setPostedOnJava(final Date postedOn) {
		setPostedOn(postedOn != null
		                            ? new DateTime(postedOn)
		                            : null);
	}
	
	/**
	 * Sets the scheme.
	 * 
	 * @param scheme
	 *            the scheme to set
	 */
	public void setScheme(final String scheme) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.scheme = scheme;
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
	 * Sets the string representation.
	 * 
	 * @param stringRepresentation
	 *            the stringRepresentation to set
	 */
	public void setStringRepresentation(final String stringRepresentation) {
		this.stringRepresentation = stringRepresentation;
	}
	
	/**
	 * Sets the url.
	 * 
	 * @param url
	 *            the url to set
	 */
	public void setUrl(final URL url) {
		this.url = url;
	}
	
	/**
	 * Sets the url string.
	 * 
	 * @param url
	 *            the new url string
	 */
	@Deprecated
	public void setUrlString(final String url) {
		try {
			if (url != null) {
				setUrl(new URL(url));
			} else {
				setUrl(null);
			}
		} catch (final MalformedURLException ignore) {
			// ignore
			assert false : "this should only be used by JPA and thus not set invalid URLs.";
		}
	}
	
	/**
	 * Sets the verified.
	 * 
	 * @param b
	 *            the new verified
	 */
	public void setVerified(final boolean b) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.verified = b;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Link [kind=");
		builder.append(this.kind);
		builder.append(", postedBy=");
		builder.append(this.postedBy);
		builder.append(", postedOn=");
		builder.append(this.postedOn);
		builder.append(", url=");
		builder.append(this.url);
		builder.append(", verified=");
		builder.append(this.verified);
		builder.append("]");
		return builder.toString();
	}
}
