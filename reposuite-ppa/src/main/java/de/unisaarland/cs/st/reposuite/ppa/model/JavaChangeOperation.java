package de.unisaarland.cs.st.reposuite.ppa.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.rcs.elements.ChangeType;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSRevision;

/**
 * The Class JavaChangeOperation.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
@Entity
public class JavaChangeOperation implements Annotated {
	
	/** The Constant serialVersionUID. */
	private static final long   serialVersionUID = 8988140924725401608L;
	
	/** The id. */
	private long                id;
	
	/** The change type. */
	private ChangeType          changeType;
	
	/** The changed element. */
	@SuppressWarnings ("rawtypes")
	private JavaElementLocation changedElementLocation;
	
	/** The revision. */
	private RCSRevision         revision;
	
	public JavaChangeOperation() {
		// FIXME remove
	}
	
	/**
	 * Instantiates a new java change operation.
	 * 
	 * @param type
	 *            the type
	 * @param element
	 *            the element
	 * @param revision
	 *            the revision
	 */
	@SuppressWarnings ("rawtypes")
	@NoneNull
	public JavaChangeOperation(final ChangeType type, final JavaElementLocation element, final RCSRevision revision) {
		setChangeType(type);
		setChangedElementLocation(element);
		setRevision(revision);
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		JavaChangeOperation other = (JavaChangeOperation) obj;
		if (getChangedElementLocation() == null) {
			if (other.getChangedElementLocation() != null) {
				return false;
			}
		} else if (!getChangedElementLocation().equals(other.getChangedElementLocation())) {
			return false;
		}
		if (getRevision() == null) {
			if (other.getRevision() != null) {
				return false;
			}
		} else if (!getRevision().equals(other.getRevision())) {
			return false;
		}
		return true;
	}
	
	/**
	 * Gets the changed element.
	 * 
	 * @return the changed element
	 */
	@SuppressWarnings ("rawtypes")
	@ManyToOne (cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	public JavaElementLocation getChangedElementLocation() {
		return changedElementLocation;
	}
	
	/**
	 * Gets the changed file.
	 * 
	 * @return the changed file
	 */
	@Transient
	public String getChangedPath() {
		return getChangedElementLocation().getFilePath();
	}
	
	/**
	 * Gets the change type.
	 * 
	 * @return the change type
	 */
	@Enumerated (EnumType.ORDINAL)
	public ChangeType getChangeType() {
		return changeType;
	}
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE)
	public long getId() {
		return id;
	}
	
	/**
	 * Gets the revision.
	 * 
	 * @return the revision
	 */
	@ManyToOne (cascade = {}, fetch = FetchType.LAZY)
	public RCSRevision getRevision() {
		return revision;
	}
	
	/**
	 * Gets the xML representation.
	 * 
	 * @param document
	 *            the document
	 * @return the xML representation
	 */
	@Transient
	public Element getXMLRepresentation(final Document document) {
		Element thisElement = document.createElement(getChangeType().toString());
		// Attr revision = document.createAttribute("revision");
		// revision.setNodeValue(this.getRevision().getTransaction().getId());
		// thisElement.setAttributeNode(revision);
		thisElement.appendChild(getChangedElementLocation().getXMLRepresentation(document));
		return thisElement;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getChangedElementLocation() == null)
		                                                                ? 0
		                                                                : getChangedElementLocation().hashCode());
		result = prime * result + ((getRevision() == null)
		                                                  ? 0
		                                                  : getRevision().hashCode());
		return result;
	}
	
	/**
	 * Sets the changed element.
	 * 
	 * @param changedElement
	 *            the new changed element
	 */
	@SuppressWarnings ("rawtypes")
	private void setChangedElementLocation(final JavaElementLocation changedElement) {
		changedElementLocation = changedElement;
	}
	
	/**
	 * Sets the change type.
	 * 
	 * @param changeType
	 *            the new change type
	 */
	private void setChangeType(final ChangeType changeType) {
		this.changeType = changeType;
	}
	
	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the new id
	 */
	@SuppressWarnings ("unused")
	private void setId(final long id) {
		this.id = id;
	}
	
	/**
	 * Sets the revision.
	 * 
	 * @param revision
	 *            the new revision
	 */
	private void setRevision(final RCSRevision revision) {
		this.revision = revision;
	}
	
}
