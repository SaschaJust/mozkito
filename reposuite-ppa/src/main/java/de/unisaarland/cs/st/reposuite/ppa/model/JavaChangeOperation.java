package de.unisaarland.cs.st.reposuite.ppa.model;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.rcs.elements.ChangeType;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSRevision;
import de.unisaarland.cs.st.reposuite.utils.specification.NoneNull;

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
	@SuppressWarnings("rawtypes")
	private JavaElementLocation changedElementLocation;
	
	/** The revision. */
	private RCSRevision         revision;
	
	@SuppressWarnings("unused")
	private JavaChangeOperation() {
	}
	
	/**
	 * Instantiates a new java change operation.
	 * 
	 * @param type
	 *            the type
	 * @param element
	 *            the element
	 * @param line
	 *            the line
	 */
	@SuppressWarnings("rawtypes")
	@NoneNull
	public JavaChangeOperation(final ChangeType type, final JavaElementLocation element, final RCSRevision revision) {
		setChangeType(type);
		setChangedElementLocation(element);
		setRevision(revision);
	}
	
	/**
	 * Gets the changed element.
	 * 
	 * @return the changed element
	 */
	@SuppressWarnings("rawtypes")
	public JavaElementLocation getChangedElementLocation() {
		return this.changedElementLocation;
	}
	
	/**
	 * Gets the changed file.
	 * 
	 * @return the changed file
	 */
	@Transient
	public String getChangedFile() {
		return this.changedElementLocation.getFilePath();
	}
	
	/**
	 * Gets the change type.
	 * 
	 * @return the change type
	 */
	public ChangeType getChangeType() {
		return this.changeType;
	}
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	public long getId() {
		return this.id;
	}
	
	public RCSRevision getRevision() {
		return this.revision;
	}
	
	public Element getXMLRepresentation(final Document document) {
		Element thisElement = document.createElement(this.changeType.toString());
		//		Attr revision = document.createAttribute("revision");
		//		revision.setNodeValue(this.getRevision().getTransaction().getId());
		//		thisElement.setAttributeNode(revision);
		thisElement.appendChild(getChangedElementLocation().getXMLRepresentation(document));
		return thisElement;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.reposuite.persistence.Annotated#saveFirst()
	 */
	@Override
	public Collection<Annotated> saveFirst() {
		HashSet<Annotated> set = new HashSet<Annotated>();
		set.add(getChangedElementLocation());
		return set;
	}
	
	/**
	 * Sets the changed element.
	 * 
	 * @param changedElement
	 *            the new changed element
	 */
	@SuppressWarnings("rawtypes")
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
	
	@SuppressWarnings("unused")
	private void setId(final long id) {
		this.id = id;
	}
	
	private void setRevision(final RCSRevision revision) {
		this.revision = revision;
	}
	
}
