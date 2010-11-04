/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.model;

import java.lang.reflect.Field;
import java.util.Collection;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class HistoryElement implements Annotated, Comparable<HistoryElement> {
	
	private Field     field;
	private Object    oldValue;
	private Object    newValue;
	private DateTime  timestamp;
	private BugReport bugReport;
	
	/**
	 * @param field
	 * @param oldValue
	 * @param newValue
	 * @param timestamp
	 */
	public HistoryElement(final BugReport bugReport, final Field field, final Object oldValue, final Object newValue,
	        final DateTime timestamp) {
		this.bugReport = bugReport;
		this.field = field;
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.timestamp = timestamp;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final HistoryElement o) {
		if (o == null) {
			return 1;
		} else {
			return this.timestamp.compareTo(o.timestamp);
		}
	}
	
	/**
	 * @return the bugReport
	 */
	public BugReport getBugReport() {
		return this.bugReport;
	}
	
	/**
	 * @return the field
	 */
	public Field getField() {
		return this.field;
	}
	
	/**
	 * @return the newValue
	 */
	public Object getNewValue() {
		return this.newValue;
	}
	
	/**
	 * @return the oldValue
	 */
	public Object getOldValue() {
		return this.oldValue;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.persistence.Annotated#getSaveFirst()
	 */
	@Override
	public Collection<Annotated> getSaveFirst() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * @return the timestamp
	 */
	public DateTime getTimestamp() {
		return this.timestamp;
	}
	
	/**
	 * @param bugReport
	 *            the bugReport to set
	 */
	public void setBugReport(final BugReport bugReport) {
		this.bugReport = bugReport;
	}
	
	/**
	 * @param field
	 *            the field to set
	 */
	public void setField(final Field field) {
		this.field = field;
	}
	
	/**
	 * @param newValue
	 *            the newValue to set
	 */
	public void setNewValue(final Object newValue) {
		this.newValue = newValue;
	}
	
	/**
	 * @param oldValue
	 *            the oldValue to set
	 */
	public void setOldValue(final Object oldValue) {
		this.oldValue = oldValue;
	}
	
	/**
	 * @param timestamp
	 *            the timestamp to set
	 */
	public void setTimestamp(final DateTime timestamp) {
		this.timestamp = timestamp;
	}
	
}
