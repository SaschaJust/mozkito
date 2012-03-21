/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.model;

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
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.moskito.persistence.Annotated;
import de.unisaarland.cs.st.moskito.persistence.model.Person;
import de.unisaarland.cs.st.moskito.persistence.model.PersonContainer;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Entity
public class AttachmentEntry implements Annotated {
	
	private static final long serialVersionUID = 5533493175917492442L;
	
	private PersonContainer   personContainer  = new PersonContainer();
	long                      size;
	private String            mime;
	private DateTime          timestamp;
	private DateTime          deltaTS;
	private String            id;
	private String            description;
	private String            filename;
	private String            link;
	
	/**
	 * should be used by persistence util only
	 */
	public AttachmentEntry() {
		
	}
	
	/**
	 * @param attachId
	 */
	public AttachmentEntry(final String attachId) {
		this.id = attachId;
	}
	
	/**
	 * @return the author
	 */
	@Transient
	public Person getAuthor() {
		return getPersonContainer().get("author");
	}
	
	/**
	 * @return the deltaTS
	 */
	public DateTime getDeltaTS() {
		return this.deltaTS;
	}
	
	/**
	 * @return the description
	 */
	@Basic
	@Column (columnDefinition = "TEXT")
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * @return the filename
	 */
	public String getFilename() {
		return this.filename;
	}
	
	/**
	 * @return the id
	 */
	@Id
	public String getId() {
		return this.id;
	}
	
	/**
	 * @return
	 */
	@Column (name = "timestamp")
	@Temporal (TemporalType.TIMESTAMP)
	public Date getJavaTimestamp() {
		return getTimestamp() != null
		                             ? getTimestamp().toDate()
		                             : null;
	}
	
	/**
	 * @return the link
	 */
	@Basic
	public String getLink() {
		return this.link;
	}
	
	/**
	 * @return the mime
	 */
	@Basic
	public String getMime() {
		return this.mime;
	}
	
	/**
	 * @return the personContainer
	 */
	@ManyToOne (cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY)
	public PersonContainer getPersonContainer() {
		return this.personContainer;
	}
	
	/**
	 * @return the size
	 */
	@Basic
	public long getSize() {
		return this.size;
	}
	
	/**
	 * @return the timestamp
	 */
	@Transient
	public DateTime getTimestamp() {
		return this.timestamp;
	}
	
	/**
	 * @param author
	 *            the author to set
	 */
	public void setAuthor(@NotNull final Person author) {
		getPersonContainer().add("author", author);
		setPersonContainer(getPersonContainer());
	}
	
	/**
	 * @param deltaTS
	 *            the deltaTS to set
	 */
	public void setDeltaTS(final DateTime deltaTS) {
		this.deltaTS = deltaTS;
	}
	
	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}
	
	/**
	 * @param filename
	 *            the filename to set
	 */
	public void setFilename(final String filename) {
		this.filename = filename;
	}
	
	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(final String id) {
		this.id = id;
	}
	
	/**
	 * @param timestamp
	 */
	public void setJavaTimestamp(final Date timestamp) {
		setTimestamp(new DateTime(timestamp));
	}
	
	/**
	 * @param link
	 *            the link to set
	 */
	public void setLink(final String link) {
		this.link = link;
	}
	
	/**
	 * @param url
	 */
	@Transient
	public void setLink(final URL url) {
		setLink(url.toString());
	}
	
	/**
	 * @param mime
	 *            the mime to set
	 */
	public void setMime(final String mime) {
		this.mime = mime;
	}
	
	/**
	 * @param personContainer
	 *            the personContainer to set
	 */
	public void setPersonContainer(final PersonContainer personContainer) {
		this.personContainer = personContainer;
	}
	
	/**
	 * @param size
	 *            the size to set
	 */
	public void setSize(final long size) {
		this.size = size;
	}
	
	/**
	 * @param timestamp
	 *            the timestamp to set
	 */
	public void setTimestamp(final DateTime timestamp) {
		this.timestamp = timestamp;
	}
	
	/**
	 * @return
	 */
	public URI toURI() {
		try {
			return new URI(getLink());
		} catch (URISyntaxException e) {
			return null;
		}
	}
	
	/**
	 * @return
	 */
	public URL toURL() {
		try {
			return new URL(getLink());
		} catch (MalformedURLException e) {
			return null;
		}
	}
}
