/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.model;

import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Index;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;

/**
 * The Class RCSBranch.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
@Entity
@Table (name = "rcsbranch")
public class RCSBranch implements Annotated, Comparable<RCSBranch> {
	
	private static final long serialVersionUID = 5419737140470855522L;
	
	private long              generatedId;
	private String            name;
	private RCSBranch         parent           = null;
	private RCSTransaction    begin            = null;
	private RCSTransaction    end              = null;
	public static RCSBranch   MASTER           = new RCSBranch("master", true);
	private boolean           open             = false;
	private String            mergedIn         = null;
	
	/**
	 * Instantiates a new rCS branch.
	 */
	protected RCSBranch() {
		
	}
	
	/**
	 * Instantiates a new rCS branch.
	 * 
	 * @param name
	 *            the name
	 */
	public RCSBranch(final String name) {
		setName(name);
	}
	
	/**
	 * Instantiates a new rCS branch.
	 *
	 * @param name might be null
	 * @param open the open
	 */
	private RCSBranch(final String name, final boolean open) {
		setName(name);
		markOpen();
	}
	
	/**
	 * Instantiates a new rCS branch.
	 * 
	 * @param name
	 *            might be null
	 * @param parent
	 *            might be null
	 */
	public RCSBranch(final String name, final RCSBranch parent) {
		setName(name);
		setParent(parent);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final RCSBranch other) {
		RCSBranch p = getParent();
		if (this.equals(other)) {
			return 0;
		}
		if (other.equals(MASTER)) {
			return 1;
		} else if (this.equals(MASTER)) {
			return -1;
		}
		while ((p != null) && (!p.equals(MASTER))) {
			if (p.equals(other)) {
				return 1;
			}
			p = p.getParent();
		}
		RCSBranch c = other.getParent();
		while ((c != null) && (!c.equals(MASTER))) {
			if (c.equals(this)) {
				return -1;
			}
			c = c.getParent();
		}
		return 0;
	}
	
	/**
	 * Gets the begin.
	 * 
	 * @return the begin
	 */
	@OneToOne (fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	public RCSTransaction getBegin() {
		return begin;
	}
	
	/**
	 * Gets the end.
	 * 
	 * @return the end
	 */
	@OneToOne (fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	public RCSTransaction getEnd() {
		return end;
	}
	
	/**
	 * Gets the generated id.
	 * 
	 * @return the generatedId
	 */
	@Id
	@Index (name = "idx_branchid")
	@Column (name = "id", nullable = false)
	@GeneratedValue (strategy = GenerationType.AUTO)
	private long getGeneratedId() {
		return generatedId;
	}
	
	/**
	 * Gets the handle.
	 * 
	 * @return the handle
	 */
	@Transient
	public String getHandle() {
		return RCSBranch.class.getSimpleName();
	}
	
	public String getMergedIn() {
		return mergedIn;
	}
	
	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	@Basic
	@Index (name = "idx_name")
	public String getName() {
		return name;
	}
	
	@SuppressWarnings("unused")
	private boolean getOpen() {
		return open;
	}
	
	/**
	 * Gets the parent.
	 * 
	 * @return the parent
	 */
	@OneToOne (fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	public RCSBranch getParent() {
		return parent;
	}
	
	/**
	 * @return
	 */
	@Transient
	public boolean hasParent() {
		return getParent() != null;
	}
	
	@Transient
	public boolean isOpen() {
		return open;
	}
	
	@Transient
	public void markOpen() {
		mergedIn = null;
		setOpen(true);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.persistence.Annotated#getSaveFirst()
	 */
	@Override
	@Transient
	public Collection<Annotated> saveFirst() {
		return null;
	}
	
	/**
	 * Sets the begin.
	 * 
	 * @param begin
	 *            the begin to set
	 */
	public void setBegin(final RCSTransaction begin) {
		this.begin = begin;
	}
	
	/**
	 * Sets the end.
	 * 
	 * @param end
	 *            the end to set
	 */
	public void setEnd(final RCSTransaction end) {
		this.end = end;
	}
	
	/**
	 * Sets the generated id.
	 * 
	 * @param generatedId
	 *            the generatedId to set
	 */
	@SuppressWarnings ("unused")
	private void setGeneratedId(final long generatedId) {
		this.generatedId = generatedId;
	}
	
	public void setMergedIn(final String mergedIn) {
		if (!isOpen()) {
			this.mergedIn = mergedIn;
		}
	}
	
	/**
	 * Sets the name.
	 * 
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}
	
	private void setOpen(final boolean b) {
		open = b;
	}
	
	/**
	 * Sets the parent.
	 * 
	 * @param parent
	 *            the parent to set
	 */
	private void setParent(final RCSBranch parent) {
		this.parent = parent;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RCSBranch [id=" + getGeneratedId() + ", name=" + getName() + ", parent=" + getParent() + "]";
	}
	
}
