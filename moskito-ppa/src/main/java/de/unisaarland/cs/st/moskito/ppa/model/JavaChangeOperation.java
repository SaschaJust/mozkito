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
package de.unisaarland.cs.st.moskito.ppa.model;

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

import org.jdom2.Attribute;
import org.jdom2.Element;

import de.unisaarland.cs.st.moskito.persistence.Annotated;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.rcs.elements.ChangeType;
import de.unisaarland.cs.st.moskito.rcs.model.RCSRevision;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

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
	 * Creates an JavaChangeOperation instance by parsing a corresponding XML representation.
	 * 
	 * @param element
	 *            the element
	 * @return the java change operation if successfull. Otherwise returns <node>null</code>
	 */
	public static JavaChangeOperation fromXMLRepresentation(final Element element,
	                                                        final PersistenceUtil persistenceUtil) {
		
		ChangeType changeType = null;
		RCSRevision revision = null;
		JavaElementLocation location = null;
		
		try {
			changeType = ChangeType.valueOf(element.getName());
		} catch (final IllegalArgumentException e) {
			if (Logger.logWarn()) {
				Logger.warn("Could not detect ChangeType of JavaChangeOperation. Unknown value '" + element.getName()
				        + "'. Returning null.");
			}
			return null;
		}
		
		final Attribute revAttribute = element.getAttribute(TRANSACTION_TAG_NAME);
		final String transaction_id = revAttribute.getValue();
		
		final Element javaElementChild = element.getChild(JavaElementLocation.JAVA_ELEMENT_LOCATION_TAG);
		
		location = JavaElementLocation.fromXMLRepresentation(javaElementChild);
		
		if (location == null) {
			if (Logger.logWarn()) {
				Logger.warn("Could not extract JavaElementLocation from XML. Returning null.");
			}
			return null;
		}
		
		String changedPath = location.getFilePath();
		
		final RCSTransaction transaction = persistenceUtil.loadById(transaction_id, RCSTransaction.class);
		if (!changedPath.startsWith("/")) {
			changedPath = "/" + changedPath;
		}
		revision = transaction.getRevisionForPath(changedPath);
		
		if (revision == null) {
			if (Logger.logWarn()) {
				Logger.warn("Could not extract revision from XML. Returning null.");
			}
		}
		
		return new JavaChangeOperation(changeType, location, revision);
		
	}
	
	/** The id. */
	private long                id;
	private boolean             setId     = false;
	
	/** The change type. */
	private ChangeType          changeType;
	
	/** The changed element. */
	private JavaElementLocation changedElementLocation;
	
	/** The revision. */
	private RCSRevision         revision;
	
	private boolean             essential = true;
	
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
		
		final JavaChangeOperation other = (JavaChangeOperation) obj;
		if (!this.setId) {
			
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
		return getId() == other.getId();
	}
	
	/**
	 * Gets the changed element.
	 * 
	 * @return the changed element
	 */
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
		return getChangedElementLocation().getFilePath();
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
	@ManyToOne (cascade = {}, fetch = FetchType.EAGER)
	public RCSRevision getRevision() {
		return this.revision;
	}
	
	/**
	 * Gets the xML representation.
	 * 
	 * @return the xML representation
	 */
	@Transient
	public Element getXMLRepresentation() {
		final Element thisElement = new Element(getChangeType().toString());
		thisElement.setAttribute(TRANSACTION_TAG_NAME, getRevision().getTransaction().getId());
		thisElement.addContent(getChangedElementLocation().getXMLRepresentation());
		return thisElement;
	}
	
	@Override
	public int hashCode() {
		int result = 1;
		if (!this.setId) {
			final int prime = 31;
			result = (prime * result) + ((getChangedElementLocation() == null)
			                                                                  ? 0
			                                                                  : getChangedElementLocation().hashCode());
			result = (prime * result) + ((getRevision() == null)
			                                                    ? 0
			                                                    : getRevision().hashCode());
			return result;
		}
		final int prime = 101;
		result = (prime * result) + ((int) getId());
		return result;
	}
	
	@NoneNull
	public boolean isAfter(final JavaChangeOperation other) {
		return getRevision().getTransaction().getTimestamp()
		                    .isAfter(other.getRevision().getTransaction().getTimestamp());
	}
	
	@NoneNull
	public boolean isBefore(final JavaChangeOperation other) {
		return getRevision().getTransaction().getTimestamp()
		                    .isBefore(other.getRevision().getTransaction().getTimestamp());
	}
	
	@Column (columnDefinition = "boolean default 'TRUE'")
	public boolean isEssential() {
		return this.essential;
	}
	
	/**
	 * Sets the changed element.
	 * 
	 * @param changedElement
	 *            the new changed element
	 */
	protected void setChangedElementLocation(final JavaElementLocation changedElement) {
		this.changedElementLocation = changedElement;
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
		this.essential = isEssential;
	}
	
	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the new id
	 */
	protected void setId(final long id) {
		this.id = id;
		this.setId = true;
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
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		
		sb.append(getId());
		sb.append(": ");
		sb.append(getChangeType().toString());
		if (getChangedElementLocation() != null) {
			sb.append(" <path = ");
			sb.append(getChangedElementLocation().getFilePath());
			if (getChangedElementLocation().getElement() != null) {
				sb.append(", element: ");
				sb.append(getChangedElementLocation().getElement().getFullQualifiedName());
			}
		}
		sb.append(", transaction: ");
		sb.append(getRevision().getTransaction().getId());
		sb.append(">");
		return sb.toString();
	}
	
}
