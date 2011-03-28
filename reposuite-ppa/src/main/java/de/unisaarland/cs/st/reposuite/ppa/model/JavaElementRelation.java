package de.unisaarland.cs.st.reposuite.ppa.model;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;

/**
 * The Class JavaElementRelation.
 * 
 * @author Kim Herzig <kim@cs.uni-saarland.de>
 */
@Entity
public class JavaElementRelation implements Annotated {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2282343106812955356L;
	
	/** The id. */
	private long              id;
	
	/** The parent. */
	private JavaElement    parent;
	
	/** The child. */
	private JavaElement    child;
	
	private final TreeSet<RCSTransaction> added            = new TreeSet<RCSTransaction>();
	private final TreeSet<RCSTransaction> deleted          = new TreeSet<RCSTransaction>();
	
	
	/**
	 * Instantiates a new java element relation.
	 * 
	 * @param parent
	 *            the parent
	 * @param child
	 *            the child
	 */
	@NoneNull
	public JavaElementRelation(JavaElement parent, JavaElement child) {
		this.setParent(parent);
		this.setChild(child);
	}
	
	
	
	/**
	 * Adds a transaction marking an point in time this relation stops being valid.
	 *
	 * @param when the when
	 */
	public void addEnd(RCSTransaction when){
		this.getDeleted().add(when);
	}
	
	/**
	 * Adds a transaction to be marked as adding point for this relation.
	 */
	public void addStart(RCSTransaction when){
		this.getAdded().add(when);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		JavaElementRelation other = (JavaElementRelation) obj;
		if (child == null) {
			if (other.child != null) return false;
		} else if (!child.equals(other.child)) return false;
		if (parent == null) {
			if (other.parent != null) return false;
		} else if (!parent.equals(other.parent)) return false;
		return true;
	}
	
	@OneToMany (cascade = {}, fetch = FetchType.LAZY)
	public Set<RCSTransaction> getAdded() {
		return added;
	}
	
	/**
	 * Gets the child.
	 * 
	 * @return the child
	 */
	@ManyToOne (cascade = {}, fetch = FetchType.LAZY)
	public JavaElement getChild() {
		return child;
	}
	
	@OneToMany (cascade = {}, fetch = FetchType.LAZY)
	public Set<RCSTransaction> getDeleted() {
		return deleted;
	}
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}
	
	/**
	 * Gets the parent.
	 * 
	 * @return the parent
	 */
	@ManyToOne (cascade = { CascadeType.PERSIST }, fetch = FetchType.LAZY)
	public JavaElement getParent() {
		return parent;
	}
	
	public Node getXMLRepresentation(Document document) {
		Element thisElement = document.createElement("JavaElementRelation");
		
		StringBuilder added = new StringBuilder();
		for (RCSTransaction t : this.getAdded()) {
			added.append(" ");
			added.append(t.getId());
		}
		
		StringBuilder deleted = new StringBuilder();
		for (RCSTransaction t : this.getDeleted()) {
			deleted.append(" ");
			deleted.append(t.getId());
		}
		
		thisElement.setAttribute("added", added.toString());
		thisElement.setAttribute("deleted", deleted.toString());
		
		thisElement.setAttribute("parent", this.getParent().getFullQualifiedName());
		thisElement.setAttribute("child", this.getChild().getFullQualifiedName());
		return thisElement;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((child == null)
				? 0
				: child.hashCode());
		result = prime * result + ((parent == null)
				? 0
				: parent.hashCode());
		return result;
	}
	
	
	
	public boolean isValid(RCSTransaction when) {
		// TODO check condition that size of added and deleted should differ
		// more than one
		if ((added.isEmpty()) && (deleted.isEmpty())) {
			// unlimted life time
			return true;
		} else if (added.isEmpty()) {
			// check the delete transaction. if when smaller then it covers.
			return (when.compareTo(deleted.first()) < 0);
		} else {
			RCSTransaction[] startPoints = added.toArray(new RCSTransaction[added.size()]);
			RCSTransaction[] endPoints = deleted.toArray(new RCSTransaction[deleted.size()]);
			if (added.first().compareTo(deleted.first()) < 0) {
				// start date is known
				for (int i = 0; i < startPoints.length; ++i) {
					if (i < endPoints.length) {
						// corresponding end point exists. Check if in interval
						if ((startPoints[i].compareTo(when) <= 0) && (endPoints[i].compareTo(when)) > 0) {
							return true;
						}
					} else {
						// no end point anymore. Just check if added before
						if (startPoints[i].compareTo(when) <= 0) {
							return true;
						}
					}
				}
			} else {
				// start date is unknown. If the first end point is before when,
				// it's valid
				if (endPoints[0].compareTo(when) > 0) {
					return true;
				} else {
					// check intervals
					for (int i = 0; i < startPoints.length; ++i) {
						if ((i + 1) < endPoints.length) {
							// corresponding end point exists. Check if in
							// interval
							if ((startPoints[i].compareTo(when) <= 0) && (endPoints[i + 1].compareTo(when)) > 0) {
								return true;
							}
						} else {
							// no end point anymore. Just check if added before
							if (startPoints[i].compareTo(when) <= 0) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.persistence.Annotated#saveFirst()
	 */
	@Override
	public Collection<Annotated> saveFirst() {
		return null;
	}
	
	
	
	@SuppressWarnings ("unused")
	private void setAdded(Set<RCSTransaction> added) {
		this.added.addAll(added);
	}
	
	
	
	/**
	 * Sets the child.
	 * 
	 * @param child
	 *            the new child
	 */
	@NoneNull
	protected void setChild(JavaElement child) {
		this.child = child;
	}
	
	@SuppressWarnings ("unused")
	private void setDeleted(Set<RCSTransaction> deleted) {
		this.deleted.addAll(deleted);
	}
	
	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the new id
	 */
	@SuppressWarnings ("unused")
	private void setId(long id) {
		this.id = id;
	}
	
	/**
	 * Sets the parent.
	 * 
	 * @param parent
	 *            the new parent
	 */
	@NoneNull
	private void setParent(JavaElement parent) {
		this.parent = parent;
	}
	
}
