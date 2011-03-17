package de.unisaarland.cs.st.reposuite.rcs.model;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;

@Embeddable
public class DateTimeTuple implements Annotated {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8037500943691455328L;
	private DateTime          oldValue;
	private DateTime          newValue;
	
	/**
	 * 
	 */
	protected DateTimeTuple() {
		
	}
	
	/**
	 * @param oldValue
	 * @param newValue
	 */
	@NoneNull
	public DateTimeTuple(final Date oldValue, final Date newValue) {
		setOldValue(new DateTime(oldValue));
		setNewValue(new DateTime(newValue));
	}
	
	/**
	 * @param oldValue
	 * @param newValue
	 */
	@NoneNull
	public DateTimeTuple(final DateTime oldValue, final DateTime newValue) {
		setOldValue(oldValue);
		setNewValue(newValue);
	}
	
	/**
	 * @return the newValue
	 */
	@Basic
	@Column (name = "newValue")
	@Temporal (TemporalType.TIMESTAMP)
	protected Date getJavaNewValue() {
		return (getNewValue() != null ? getNewValue().toDate() : null);
	}
	
	/**
	 * @return the oldValue
	 */
	@Basic
	@Column (name = "oldValue")
	@Temporal (TemporalType.TIMESTAMP)
	protected Date getJavaOldValue() {
		return getOldValue() != null ? getOldValue().toDate() : null;
	}
	
	/**
	 * @return the newValue
	 */
	@Transient
	public DateTime getNewValue() {
		return this.newValue;
	}
	
	/**
	 * @return the oldValue
	 */
	@Transient
	public DateTime getOldValue() {
		return this.oldValue;
	}
	
	@Override
	public Collection<Annotated> saveFirst() {
		return null;
	}
	
	/**
	 * @param date
	 */
	protected void setJavaNewValue(final Date date) {
		setNewValue(date != null ? new DateTime(date) : null);
	}
	
	/**
	 * @param date
	 */
	protected void setJavaOldValue(final Date date) {
		setOldValue(date != null ? new DateTime(date) : null);
	}
	
	/**
	 * @param newValue
	 *            the newValue to set
	 */
	public void setNewValue(final DateTime newValue) {
		this.newValue = newValue;
	}
	
	/**
	 * @param oldValue
	 *            the oldValue to set
	 */
	public void setOldValue(final DateTime oldValue) {
		this.oldValue = oldValue;
	}
	
}
