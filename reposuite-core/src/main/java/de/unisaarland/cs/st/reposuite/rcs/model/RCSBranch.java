/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.model;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Entity
public class RCSBranch implements Annotated {
	
	private long           generatedId;
	private String         name;
	private RCSBranch      parent;
	private RCSTransaction begin;
	private RCSTransaction end;
	
	protected RCSBranch() {
		
	}
	
	/**
	 * @param name
	 *            might be null
	 * @param parent
	 *            might be null
	 * @param begin
	 *            might NOT be null
	 */
	public RCSBranch(final String name, final RCSBranch parent, final RCSTransaction begin) {
		this.name = name;
		this.parent = parent;
		this.begin = begin;
	}
	
	/**
	 * @return the begin
	 */
	@OneToOne (fetch = FetchType.LAZY)
	public RCSTransaction getBegin() {
		return this.begin;
	}
	
	/**
	 * @return the end
	 */
	@OneToOne (fetch = FetchType.LAZY)
	public RCSTransaction getEnd() {
		return this.end;
	}
	
	/**
	 * @return the generatedId
	 */
	@SuppressWarnings ("unused")
	@Id
	@GeneratedValue (strategy = GenerationType.AUTO)
	private long getGeneratedId() {
		return this.generatedId;
	}
	
	/**
	 * @return
	 */
	@Transient
	public String getHandle() {
		return RCSBranch.class.getSimpleName();
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * @return the parent
	 */
	@OneToOne (fetch = FetchType.LAZY)
	public RCSBranch getParent() {
		return this.parent;
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
	 * @param begin
	 *            the begin to set
	 */
	public void setBegin(final RCSTransaction begin) {
		this.begin = begin;
	}
	
	/**
	 * @param end
	 *            the end to set
	 */
	public void setEnd(final RCSTransaction end) {
		this.end = end;
	}
	
	/**
	 * @param generatedId
	 *            the generatedId to set
	 */
	@SuppressWarnings ("unused")
	private void setGeneratedId(final long generatedId) {
		this.generatedId = generatedId;
	}
	
	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}
	
	/**
	 * @param parent
	 *            the parent to set
	 */
	public void setParent(final RCSBranch parent) {
		this.parent = parent;
	}
	
}
