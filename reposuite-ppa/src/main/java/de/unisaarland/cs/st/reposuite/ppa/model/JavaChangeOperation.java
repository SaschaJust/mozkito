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
	
	/**
	 * Instantiates a new java change operation.
	 */
	@SuppressWarnings ("unused")
	private JavaChangeOperation() {
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
		if (this.changedElementLocation == null) {
			if (other.changedElementLocation != null) {
				return false;
			}
		} else if (!this.changedElementLocation.equals(other.changedElementLocation)) {
			return false;
		}
		if (this.revision == null) {
			if (other.revision != null) {
				return false;
			}
		} else if (!this.revision.equals(other.revision)) {
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
		return this.changedElementLocation;
	}
	
	/**
	 * Gets the changed file.
	 * 
	 * @return the changed file
	 */
	@Transient
	public String getChangedPath() {
		return this.changedElementLocation.getFilePath();
	}
	
	/**
	 * Gets the change type.
	 * 
	 * @return the change type
	 */
	@Enumerated (EnumType.ORDINAL)
	public ChangeType getChangeType() {
		return this.changeType;
	}
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE)
	public long getId() {
		return this.id;
	}
	
	/**
	 * Gets the revision.
	 * 
	 * @return the revision
	 */
	@ManyToOne (cascade = {}, fetch = FetchType.LAZY)
	public RCSRevision getRevision() {
		return this.revision;
	}
	
	/**
	 * Gets the xML representation.
	 * 
	 * @param document
	 *            the document
	 * @return the xML representation
	 */
	public Element getXMLRepresentation(final Document document) {
		Element thisElement = document.createElement(this.changeType.toString());
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
		result = prime * result + ((this.changedElementLocation == null)
		                                                                ? 0
		                                                                : this.changedElementLocation.hashCode());
		result = prime * result + ((this.revision == null)
		                                                  ? 0
		                                                  : this.revision.hashCode());
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
		this.changedElementLocation = changedElement;
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
