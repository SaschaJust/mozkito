package de.unisaarland.cs.st.reposuite.rcs.model;

import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.Embeddable;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import de.unisaarland.cs.st.reposuite.persistence.Annotated;

@Embeddable
public class LongTuple implements Annotated {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8692461784697718949L;
	private Long              oldValue;
	private Long              newValue;
	
	/**
	 * 
	 */
	protected LongTuple() {
		
	}
	
	/**
	 * @param oldValue
	 * @param newValue
	 */
	@NoneNull
	public LongTuple(final Long oldValue, final Long newValue) {
		setOldValue(oldValue);
		setNewValue(newValue);
	}
	
	/**
	 * @return the newValue
	 */
	@Basic
	public Long getNewValue() {
		return this.newValue;
	}
	
	/**
	 * @return the oldValue
	 */
	@Basic
	public Long getOldValue() {
		return this.oldValue;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.persistence.Annotated#saveFirst()
	 */
	@Override
	public Collection<Annotated> saveFirst() {
		return null;
	}
	
	/**
	 * @param newValue
	 *            the newValue to set
	 */
	public void setNewValue(final Long newValue) {
		this.newValue = newValue;
	}
	
	/**
	 * @param oldValue
	 *            the oldValue to set
	 */
	public void setOldValue(final Long oldValue) {
		this.oldValue = oldValue;
	}
	
}
