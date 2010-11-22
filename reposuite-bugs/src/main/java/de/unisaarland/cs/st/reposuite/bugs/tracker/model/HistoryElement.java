/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.model;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.rcs.model.Person;
import de.unisaarland.cs.st.reposuite.utils.Condition;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Entity
@Table (name = "history_element")
public class HistoryElement implements Annotated, Comparable<HistoryElement> {
	
	private long                                           id;
	private String                                         field;
	
	private Map<String, PersistentTuple<String, String>>   changedStringValues = new HashMap<String, PersistentTuple<String, String>>();
	private Map<String, PersistentTuple<Person, Person>>   changedPersonValues = new HashMap<String, PersistentTuple<Person, Person>>();
	private Map<String, PersistentTuple<Integer, Integer>> changedEnumValues   = new HashMap<String, PersistentTuple<Integer, Integer>>();
	
	private Map<String, PersistentTuple<Date, Date>>       changedDateValues   = new HashMap<String, PersistentTuple<Date, Date>>();
	
	private DateTime                                       timestamp;
	
	private Report                                         bugReport;
	
	private Person                                         author;
	
	/**
	 * used by hibernate
	 */
	protected HistoryElement() {
		
	}
	
	/**
	 * @param field
	 * @param oldValue
	 * @param newValue
	 * @param timestamp
	 */
	public HistoryElement(final Person author, final Report bugReport, final DateTime timestamp,
	        final Map<String, PersistentTuple<?, ?>> values) {
		Condition.notNull(author);
		Condition.notNull(bugReport);
		Condition.notNull(timestamp);
		Condition.notNull(values);
		
		setAuthor(author);
		setBugReport(bugReport);
		setTimestamp(timestamp);
		
		addChange(values);
	}
	
	@SuppressWarnings ("unchecked")
	public void addChange(final Map<String, PersistentTuple<?, ?>> values) {
		for (String fieldName : values.keySet()) {
			if (values.get(fieldName).getFirst() instanceof String) {
				this.changedStringValues.put(fieldName, (PersistentTuple<String, String>) values.get(fieldName));
			} else if (values.get(fieldName).getFirst() instanceof Person) {
				this.changedPersonValues.put(fieldName, (PersistentTuple<Person, Person>) values.get(fieldName));
			} else if (values.get(fieldName).getFirst() instanceof Integer) {
				this.changedEnumValues.put(fieldName, (PersistentTuple<Integer, Integer>) values.get(fieldName));
			} else if (values.get(fieldName).getFirst() instanceof Date) {
				this.changedDateValues.put(fieldName, (PersistentTuple<Date, Date>) values.get(fieldName));
			} else {
				throw new UnrecoverableError(values.get(fieldName).getFirst().getClass().getCanonicalName()
				        + " is not supported for " + HistoryElement.class.getSimpleName() + ".");
			}
		}
	}
	
	/**
	 * @param field
	 * @param oldValue
	 * @param newValue
	 */
	@Transient
	public void addChangedValue(final String field, final DateTime oldValue, final DateTime newValue) {
		this.changedDateValues.put(field, new PersistentTuple<Date, Date>(oldValue.toDate(), newValue.toDate()));
	}
	
	/**
	 * @param field
	 * @param oldValue
	 * @param newValue
	 */
	@Transient
	public void addChangedValue(final String field, final Enum<?> oldValue, final Enum<?> newValue) {
		this.changedEnumValues
		        .put(field, new PersistentTuple<Integer, Integer>(oldValue.ordinal(), newValue.ordinal()));
	}
	
	/**
	 * @param field
	 * @param oldValue
	 * @param newValue
	 */
	@Transient
	public void addChangedValue(final String field, final Person oldValue, final Person newValue) {
		this.changedPersonValues.put(field, new PersistentTuple<Person, Person>(oldValue, newValue));
	}
	
	/**
	 * @param field
	 * @param oldValue
	 * @param newValue
	 */
	@Transient
	public void addChangedValue(final String field, final String oldValue, final String newValue) {
		this.changedStringValues.put(field, new PersistentTuple<String, String>(oldValue, newValue));
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Annotated)
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
	
	/**
	 * @return
	 */
	@ManyToOne (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public Person getAuthor() {
		return this.author;
	}
	
	/**
	 * @return
	 */
	@ManyToOne (cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	public Report getBugReport() {
		return this.bugReport;
	}
	
	/**
	 * @return the changedDateValues
	 */
	@ManyToOne (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Map<String, PersistentTuple<Date, Date>> getChangedDateValues() {
		return this.changedDateValues;
	}
	
	/**
	 * @return the changedEnumValues
	 */
	@ManyToOne (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Map<String, PersistentTuple<Integer, Integer>> getChangedEnumValues() {
		return this.changedEnumValues;
	}
	
	/**
	 * @return the changedPersonValues
	 */
	@ManyToOne (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Map<String, PersistentTuple<Person, Person>> getChangedPersonValues() {
		return this.changedPersonValues;
	}
	
	/**
	 * @return the bugReport
	 */
	/**
	 * @return the changedStringValues
	 */
	@ManyToOne (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Map<String, PersistentTuple<String, String>> getChangedStringValues() {
		return this.changedStringValues;
	}
	
	/**
	 * @return the field
	 */
	@Basic
	public String getField() {
		return this.field;
	}
	
	/**
	 * @param field
	 * @return
	 */
	public HistoryElement getForField(final String field) {
		HistoryElement element = new HistoryElement();
		if (this.changedStringValues.containsKey(field)) {
			element.getChangedStringValues().put(field, this.changedStringValues.get(field));
		} else if (this.changedPersonValues.containsKey(field)) {
			element.getChangedPersonValues().put(field, this.changedPersonValues.get(field));
		} else if (this.changedEnumValues.containsKey(field)) {
			element.getChangedEnumValues().put(field, this.changedEnumValues.get(field));
		} else if (this.changedDateValues.containsKey(field)) {
			element.getChangedDateValues().put(field, this.changedDateValues.get(field));
		}
		return element;
	}
	
	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE)
	public long getId() {
		return this.id;
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
	
	/**
	 * @param name
	 * @return
	 */
	public Object getNewValue(final String name) {
		if (this.changedStringValues.containsKey(this.field)) {
			return getChangedStringValues().get(this.field).getFirst();
		} else if (this.changedPersonValues.containsKey(this.field)) {
			return getChangedPersonValues().get(this.field).getFirst();
		} else if (this.changedEnumValues.containsKey(this.field)) {
			return getChangedEnumValues().get(this.field).getFirst();
		} else if (this.changedDateValues.containsKey(this.field)) {
			return getChangedDateValues().get(this.field).getFirst();
		} else {
			return null;
		}
	}
	
	/**
	 * @param field
	 * @return
	 */
	public Object getOldValue(final String field) {
		if (this.changedStringValues.containsKey(this.field)) {
			return getChangedStringValues().get(this.field).getSecond();
		} else if (this.changedPersonValues.containsKey(this.field)) {
			return getChangedPersonValues().get(this.field).getSecond();
		} else if (this.changedEnumValues.containsKey(this.field)) {
			return getChangedEnumValues().get(this.field).getSecond();
		} else if (this.changedDateValues.containsKey(this.field)) {
			return getChangedDateValues().get(this.field).getSecond();
		} else {
			return null;
		}
		
	}
	
	/**
	 * @return the timestamp
	 */
	@Transient
	public DateTime getTimestamp() {
		return this.timestamp;
	}
	
	/**
	 * @return
	 */
	public boolean isEmpty() {
		return this.changedDateValues.isEmpty() && this.changedEnumValues.isEmpty()
		        && this.changedPersonValues.isEmpty() && this.changedStringValues.isEmpty();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.persistence.Annotated#saveFirst()
	 */
	@Override
	@Transient
	public Collection<Annotated> saveFirst() {
		return null;
	}
	
	/**
	 * @param field
	 *            the field to set
	 */
	public void setAnnotated(final String field) {
		this.setField(field);
	}
	
	/**
	 * @param author
	 */
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
	
	/**
	 * @param changedDateValues
	 *            the changedDateValues to set
	 */
	@SuppressWarnings ("unused")
	private void setChangedDateValues(final Map<String, PersistentTuple<Date, Date>> changedDateValues) {
		this.changedDateValues = changedDateValues;
	}
	
	/**
	 * @param changedEnumValues
	 *            the changedEnumValues to set
	 */
	@SuppressWarnings ("unused")
	private void setChangedEnumValues(final Map<String, PersistentTuple<Integer, Integer>> changedEnumValues) {
		this.changedEnumValues = changedEnumValues;
	}
	
	/**
	 * @param changedPersonValues
	 *            the changedPersonValues to set
	 */
	@SuppressWarnings ("unused")
	private void setChangedPersonValues(final Map<String, PersistentTuple<Person, Person>> changedPersonValues) {
		this.changedPersonValues = changedPersonValues;
	}
	
	/**
	 * @param changedStringValues
	 *            the changedStringValues to set
	 */
	@SuppressWarnings ("unused")
	private void setChangedStringValues(final Map<String, PersistentTuple<String, String>> changedStringValues) {
		this.changedStringValues = changedStringValues;
	}
	
	/**
	 * @param field
	 *            the field to set
	 */
	public void setField(final String field) {
		this.field = field;
	}
	
	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(final long id) {
		this.id = id;
	}
	
	/**
	 * @param timestamp
	 */
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
	
	/**
	 * @return
	 */
	public int size() {
		return this.changedDateValues.size() + this.changedEnumValues.size() + this.changedPersonValues.size()
		        + this.changedStringValues.size();
	}
	
}
