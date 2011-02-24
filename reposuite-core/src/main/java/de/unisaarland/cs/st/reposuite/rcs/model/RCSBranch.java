/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.model;

import java.util.Collection;
import java.util.TreeSet;

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
	 * Checks if any of the given transactions was committed into this branch.
	 * 
	 * @param tIds
	 *            the transaction ids to check for
	 * @return A sorted set of transactions committed into this branch
	 */
	@Transient
	public TreeSet<RCSTransaction> containsAnyTransaction(final Collection<String> tIds) {
		//TODO add test case
		TreeSet<RCSTransaction> result = new TreeSet<RCSTransaction>();
		for(String id : tIds){
			RCSTransaction t = this.containsTransaction(id);
			if(t != null){
				result.add(t);
			}
		}
		return result;
	}
	
	/**
	 * Checks if the given transaction id was committed in this branch. Returns
	 * the RCSTransaction if found, otherwise <code>null</code>.
	 * 
	 * @param tId
	 *            the t id
	 * @return the transaction if found. Otherwise <code>null</code>
	 */
	@Transient
	public RCSTransaction containsTransaction(final String tId) {
		//TODO add test case
		if (this.getBegin().getId().equals(tId)) {
			return this.getBegin();
		}
		
		RCSTransaction current = this.getEnd();
		while(!current.equals(this.getBegin())){
			if (current.getId().equals(tId)) {
				return current;
			}
			current = current.getParent(this);
		}
		return null;
	}
	
	/**
	 * Gets the begin.
	 * 
	 * @return the begin
	 */
	@OneToOne (fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	public RCSTransaction getBegin() {
		return this.begin;
	}
	
	/**
	 * Gets the end.
	 * 
	 * @return the end
	 */
	@OneToOne (fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	public RCSTransaction getEnd() {
		return this.end;
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
		return this.generatedId;
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
		return this.mergedIn;
	}
	
	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	@Basic
	@Index (name = "idx_name")
	public String getName() {
		return this.name;
	}
	
	@SuppressWarnings("unused")
	private boolean getOpen() {
		return this.open;
	}
	
	/**
	 * Gets the parent.
	 * 
	 * @return the parent
	 */
	@OneToOne (fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	public RCSBranch getParent() {
		return this.parent;
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
		return this.open;
	}
	
	@Transient
	public void markOpen() {
		this.mergedIn = null;
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
		this.open = b;
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
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("RCSBranch [id=");
		sb.append(getGeneratedId());
		sb.append(", name=");
		sb.append(getName());
		sb.append(", parent=");
		if (getParent() != null) {
			sb.append(getParent());
		} else {
			sb.append("null");
		}
		sb.append(", end=");
		if (getEnd() != null) {
			sb.append(getEnd().getId());
		} else {
			sb.append("null");
		}
		sb.append(", mergedIn=");
		sb.append(getMergedIn());
		sb.append("]");
		return sb.toString();
	}
	
}
