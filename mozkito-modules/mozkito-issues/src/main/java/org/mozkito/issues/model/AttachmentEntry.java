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
package org.mozkito.issues.model;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;

import org.joda.time.DateTime;

import org.mozkito.persistence.Annotated;
import org.mozkito.persons.model.Person;
import org.mozkito.persons.model.PersonContainer;
import org.mozkito.utilities.commons.JavaUtils;

/**
 * The Class AttachmentEntry.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
@Entity
public class AttachmentEntry implements Annotated {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5533493175917492442L;
	
	/** The person container. */
	private PersonContainer   personContainer  = new PersonContainer();
	
	/** The size. */
	long                      size;
	
	/** The mime. */
	private String            mime;
	
	/** The timestamp. */
	private DateTime          timestamp;
	
	/** The delta ts. */
	private DateTime          deltaTS;
	
	/** The id. */
	private String            id;
	
	/** The description. */
	private String            description;
	
	/** The filename. */
	private String            filename;
	
	/** The link. */
	private String            link;
	
	/**
	 * @deprecated should be used by persistence util only.
	 */
	@Deprecated
	public AttachmentEntry() {
		// ignore
	}
	
	/**
	 * Instantiates a new attachment entry.
	 * 
	 * @param attachId
	 *            the attach id
	 */
	public AttachmentEntry(final String attachId) {
		this.id = attachId;
	}
	
	/**
	 * Gets the author.
	 * 
	 * @return the author
	 */
	@Transient
	public Person getAuthor() {
		return getPersonContainer().get("author");
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.Annotated#getHandle()
	 */
	/**
	 * Gets the class name.
	 * 
	 * @return the class name
	 */
	public final String getClassName() {
		return JavaUtils.getHandle(AttachmentEntry.class);
	}
	
	/**
	 * Gets the delta ts.
	 * 
	 * @return the deltaTS
	 */
	@Transient
	public DateTime getDeltaTS() {
		return this.deltaTS;
	}
	
	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	@Basic
	@Lob
	@Column (length = 0)
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * Gets the filename.
	 * 
	 * @return the filename
	 */
	public String getFilename() {
		return this.filename;
	}
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	public String getId() {
		return this.id;
	}
	
	/**
	 * Gets the java delta ts.
	 * 
	 * @return the java delta ts
	 */
	@Column (name = "deltats")
	@Temporal (TemporalType.TIMESTAMP)
	public Date getJavaDeltaTS() {
		return getDeltaTS() != null
		                           ? getDeltaTS().toDate()
		                           : null;
	}
	
	/**
	 * Gets the java timestamp.
	 * 
	 * @return the java timestamp
	 */
	@Column (name = "timestamp")
	@Temporal (TemporalType.TIMESTAMP)
	public Date getJavaTimestamp() {
		return getTimestamp() != null
		                             ? getTimestamp().toDate()
		                             : null;
	}
	
	/**
	 * Gets the link.
	 * 
	 * @return the link
	 */
	@Basic
	public String getLink() {
		return this.link;
	}
	
	/**
	 * Gets the mime.
	 * 
	 * @return the mime
	 */
	@Basic
	public String getMime() {
		return this.mime;
	}
	
	/**
	 * Gets the person container.
	 * 
	 * @return the personContainer
	 */
	@ManyToOne (cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY)
	public PersonContainer getPersonContainer() {
		return this.personContainer;
	}
	
	/**
	 * Gets the size.
	 * 
	 * @return the size
	 */
	@Basic
	public long getSize() {
		return this.size;
	}
	
	/**
	 * Gets the timestamp.
	 * 
	 * @return the timestamp
	 */
	@Transient
	public DateTime getTimestamp() {
		return this.timestamp;
	}
	
	/**
	 * Sets the author.
	 * 
	 * @param author
	 *            the author to set
	 */
	public void setAuthor(@NotNull final Person author) {
		getPersonContainer().add("author", author);
		setPersonContainer(getPersonContainer());
	}
	
	/**
	 * Sets the delta ts.
	 * 
	 * @param deltaTS
	 *            the deltaTS to set
	 */
	public void setDeltaTS(final DateTime deltaTS) {
		this.deltaTS = deltaTS;
	}
	
	/**
	 * Sets the description.
	 * 
	 * @param description
	 *            the description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}
	
	/**
	 * Sets the filename.
	 * 
	 * @param filename
	 *            the filename to set
	 */
	public void setFilename(final String filename) {
		this.filename = filename;
	}
	
	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the id to set
	 */
	public void setId(final String id) {
		this.id = id;
	}
	
	/**
	 * Sets the java delta ts.
	 * 
	 * @param deltaTS
	 *            the new java delta ts
	 */
	public final void setJavaDeltaTS(final Date deltaTS) {
		setDeltaTS(new DateTime(deltaTS));
	}
	
	/**
	 * Sets the java timestamp.
	 * 
	 * @param timestamp
	 *            the new java timestamp
	 */
	public void setJavaTimestamp(final Date timestamp) {
		setTimestamp(new DateTime(timestamp));
	}
	
	/**
	 * Sets the link.
	 * 
	 * @param link
	 *            the link to set
	 */
	public void setLink(final String link) {
		this.link = link;
	}
	
	/**
	 * Sets the link.
	 * 
	 * @param url
	 *            the new link
	 */
	@Transient
	public void setLink(final URL url) {
		setLink(url.toString());
	}
	
	/**
	 * Sets the mime.
	 * 
	 * @param mime
	 *            the mime to set
	 */
	public void setMime(final String mime) {
		this.mime = mime;
	}
	
	/**
	 * Sets the person container.
	 * 
	 * @param personContainer
	 *            the personContainer to set
	 */
	public void setPersonContainer(final PersonContainer personContainer) {
		this.personContainer = personContainer;
	}
	
	/**
	 * Sets the size.
	 * 
	 * @param size
	 *            the size to set
	 */
	public void setSize(final long size) {
		this.size = size;
	}
	
	/**
	 * Sets the timestamp.
	 * 
	 * @param timestamp
	 *            the timestamp to set
	 */
	public void setTimestamp(final DateTime timestamp) {
		this.timestamp = timestamp;
	}
	
	/**
	 * To uri.
	 * 
	 * @return the uRI
	 * @throws URISyntaxException
	 */
	public URI toURI() throws URISyntaxException {
		// try {
		// final String string = URLEncoder.encode(getLink(), URL_DEFAULT_ENCODING);
		return new URI(getLink().replace(" ", "+"));
		// } catch (final UnsupportedEncodingException e) {
		// throw new URISyntaxException(getLink(), e.getMessage(), 0);
		// }
	}
	
	/**
	 * To url.
	 * 
	 * @return the uRL
	 * @throws MalformedURLException
	 */
	public URL toURL() throws MalformedURLException {
		return new URL(getLink().replace(" ", "+"));
	}
}
