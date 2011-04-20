/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.model;

import java.net.URL;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.rcs.model.Person;
import de.unisaarland.cs.st.reposuite.rcs.model.PersonContainer;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Embeddable
public class AttachmentEntry implements Annotated {
	
	private static final long serialVersionUID = 5533493175917492442L;
	
	private PersonContainer   personContainer  = new PersonContainer();
	long                      size;
	private String            mime;
	private DateTime          timestamp;
	private DateTime          deltaTS;
	private String            id;
	private String            description;
	private URL               link;
	private String            filename;
	
	public AttachmentEntry(final String attachId) {
		id = attachId;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AttachmentEntry)) {
			return false;
		}
		AttachmentEntry other = (AttachmentEntry) obj;
		if (link == null) {
			if (other.link != null) {
				return false;
			}
		} else if (!link.equals(other.link)) {
			return false;
		}
		return true;
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
		return deltaTS;
	}
	
	/**
	 * @return the description
	 */
	@Basic
	public String getDescription() {
		return description;
	}
	
	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
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
	public URL getLink() {
		return link;
	}
	
	/**
	 * @return the mime
	 */
	@Basic
	public String getMime() {
		return mime;
	}
	
	/**
	 * @return the personContainer
	 */
	@ManyToOne (cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY)
	public PersonContainer getPersonContainer() {
		return personContainer;
	}
	
	/**
	 * @return the size
	 */
	@Basic
	public long getSize() {
		return size;
	}
	
	/**
	 * @return the timestamp
	 */
	@Transient
	public DateTime getTimestamp() {
		return timestamp;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((link == null)
		                                         ? 0
		                                         : link.hashCode());
		return result;
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
	public void setLink(final URL link) {
		this.link = link;
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
}
