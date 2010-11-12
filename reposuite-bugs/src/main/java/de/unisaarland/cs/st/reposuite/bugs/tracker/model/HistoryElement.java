/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.model;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.rcs.model.Person;
import de.unisaarland.cs.st.reposuite.utils.Condition;
import de.unisaarland.cs.st.reposuite.utils.Tuple;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Entity
@Table (name = "history_element")
public class HistoryElement implements Annotated, Comparable<HistoryElement> {
	
	private Field     field;
	private Map<Field, Tuple<Object, Object>> changedValues = new HashMap<Field, Tuple<Object, Object>>();
	private DateTime  timestamp;
	private Report   bugReport;
	private Person   author;
	
	/**
	 * used by hibernate
	 */
	@SuppressWarnings ("unused")
	private HistoryElement() {
		
	}
	
	/**
	 * @param field
	 * @param oldValue
	 * @param newValue
	 * @param timestamp
	 */
	public HistoryElement(final Person author, final Report bugReport, final Field field, final Object oldValue,
			final Object newValue,
			final DateTime timestamp) {
		Condition.notNull(author);
		Condition.notNull(bugReport);
		Condition.notNull(timestamp);
		
		if ((field != null) || (oldValue != null) || (newValue != null)) {
			Condition.notNull(field);
			Condition.notNull(oldValue);
			Condition.notNull(newValue);
		}
		
		this.setAuthor(author);
		this.bugReport = bugReport;
		this.field = field;
		if ((field != null) || (oldValue != null) || (newValue != null)) {
			this.getChangedValues().put(field, new Tuple<Object, Object>(oldValue, newValue));
		}
		this.timestamp = timestamp;
	}
	
	/**
	 * Adds the changed value.
	 * 
	 * @param field
	 *            the field
	 * @param oldValue
	 *            the old value
	 * @param newValue
	 *            the new value
	 * @return true, if successful
	 */
	@Transient
	public boolean addChangedValue(final Field field, final Object oldValue, final Object newValue){
		if (this.getChangedValues().containsKey(field)) {
			return false;
		}
		this.changedValues.put(field, new Tuple<Object, Object>(oldValue, newValue));
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Transient
	@Override
	public int compareTo(final HistoryElement object) {
		if (object == null) {
			return 1;
		} else {
			return this.timestamp.compareTo(object.timestamp);
		}
	}
	
	@ManyToOne
	public Person getAuthor() {
		return this.author;
	}
	
	/**
	 * @return the bugReport
	 */
	@ManyToOne (cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	public Report getBugReport() {
		return this.bugReport;
	}
	
	public Map<Field, Tuple<Object, Object>> getChangedValues() {
		return this.changedValues;
	}
	
	
	
	/**
	 * @return the field
	 */
	@Basic
	public Field getField() {
		return this.field;
	}
	
	/**
	 * @return
	 */
	@SuppressWarnings ("unused")
	@Temporal (TemporalType.TIMESTAMP)
	@Column (name = "timestamp")
	private Date getJavaTimestamp() {
		return this.timestamp.toDate();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.persistence.Annotated#getSaveFirst()
	 */
	@Override
	@Transient
	public Collection<Annotated> getSaveFirst() {
		return null;
	}
	
	
	/**
	 * @return the timestamp
	 */
	@Transient
	public DateTime getTimestamp() {
		return this.timestamp;
	}
	
	public void setAuthor(final Person author) {
		this.author = author;
	}
	
	
	
	/**
	 * @param bugReport
	 *            the bugReport to set
	 */
	public void setBugReport(final Report bugReport) {
		this.bugReport = bugReport;
	}
	
	public void setChangedValues(final Map<Field, Tuple<Object, Object>> changedValues) {
		this.changedValues = changedValues;
	}
	
	/**
	 * @param field
	 *            the field to set
	 */
	public void setField(final Field field) {
		this.field = field;
	}
	
	@SuppressWarnings ("unused")
	private void setJavaTimestamp(final Date timestamp) {
		this.timestamp = new DateTime(timestamp);
	}
	
	/**
	 * @param timestamp
	 *            the timestamp to set
	 */
	public void setTimestamp(final DateTime timestamp) {
		this.timestamp = timestamp;
	}
	
	
}
