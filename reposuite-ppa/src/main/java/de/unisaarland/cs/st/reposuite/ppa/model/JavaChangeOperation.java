/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.ppa.model;

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
import javax.persistence.Transient;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kisa.Logger;

import org.jdom.Attribute;
import org.jdom.Element;

import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceManager;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.rcs.elements.ChangeType;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSRevision;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;

/**
 * The Class JavaChangeOperation.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
@Entity
public class JavaChangeOperation implements Annotated {
	
	public static String      TRANSACTION_TAG_NAME = "transaction";
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID     = 8988140924725401608L;
	
	/**
	 * Creates an JavaChangeOperation instance by parsing a corresponding XML
	 * representation.
	 * 
	 * @param element
	 *            the element
	 * @return the java change operation if successfull. Otherwise returns
	 *         <node>null</code>
	 */
	public static JavaChangeOperation fromXMLRepresentation(final org.jdom.Element element) {
		
		ChangeType changeType = null;
		RCSRevision revision = null;
		JavaElementLocation location = null;
		
		try {
			changeType = ChangeType.valueOf(element.getName());
		} catch (IllegalArgumentException e) {
			if (Logger.logWarn()) {
				Logger.warn("Could not detect ChangeType of JavaChangeOperation. Unknown value '" + element.getName()
				            + "'. Returning null.");
			}
			return null;
		}
		
		Attribute revAttribute = element.getAttribute(TRANSACTION_TAG_NAME);
		String transaction_id = revAttribute.getValue();
		
		org.jdom.Element javaElementChild = element.getChild(JavaElementLocation.JAVA_ELEMENT_LOCATION_TAG);
		
		location = JavaElementLocation.fromXMLRepresentation(javaElementChild);
		
		if (location == null) {
			if (Logger.logWarn()) {
				Logger.warn("Could not extract JavaElementLocation from XML. Returning null.");
			}
			return null;
		}
		
		String changedPath = location.getFilePath();
		
		try {
			PersistenceUtil persistenceUtil = PersistenceManager.getUtil();
			RCSTransaction transaction = persistenceUtil.loadById(transaction_id, RCSTransaction.class);
			if (!changedPath.startsWith("/")) {
				changedPath = "/" + changedPath;
			}
			revision = transaction.getRevisionForPath(changedPath);
		} catch (UninitializedDatabaseException e) {
			throw new UnrecoverableError("Could not retrieve RCSTransaction. Database uninitialized!", e);
		}
		
		if (revision == null) {
			if (Logger.logWarn()) {
				Logger.warn("Could not extract revision from XML. Returning null.");
			}
		}
		
		return new JavaChangeOperation(changeType, location, revision);
		
	}
	
	/** The id. */
	private long                id;
	
	/** The change type. */
	private ChangeType          changeType;
	
	/** The changed element. */
	private JavaElementLocation changedElementLocation;
	
	/** The revision. */
	private RCSRevision         revision;
	
	private boolean       essential = true;
	
	@Deprecated
	public JavaChangeOperation() {
		
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
	 * @return the xML representation
	 */
	@Transient
	public Element getXMLRepresentation() {
		Element thisElement = new Element(getChangeType().toString());
		thisElement.setAttribute(TRANSACTION_TAG_NAME, getRevision().getTransaction().getId());
		thisElement.addContent(getChangedElementLocation().getXMLRepresentation());
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
	
	@Column (columnDefinition = "boolean default 'TRUE'")
	public boolean isEssential() {
		return essential;
	}
	
	/**
	 * Sets the changed element.
	 * 
	 * @param changedElement
	 *            the new changed element
	 */
	protected void setChangedElementLocation(final JavaElementLocation changedElement) {
		changedElementLocation = changedElement;
	}
	
	/**
	 * Sets the change type.
	 * 
	 * @param changeType
	 *            the new change type
	 */
	protected void setChangeType(final ChangeType changeType) {
		this.changeType = changeType;
	}
	
	public void setEssential(final boolean isEssential) {
		essential = isEssential;
	}
	
	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the new id
	 */
	protected void setId(final long id) {
		this.id = id;
	}
	
	/**
	 * Sets the revision.
	 * 
	 * @param revision
	 *            the new revision
	 */
	protected void setRevision(final RCSRevision revision) {
		this.revision = revision;
	}
	
}
