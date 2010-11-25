/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.rcs.model.Person;
import de.unisaarland.cs.st.reposuite.rcs.model.PersonContainer;
import de.unisaarland.cs.st.reposuite.utils.Condition;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Entity
@Table (name = "history_element")
public class HistoryElement implements Annotated, Comparable<HistoryElement> {
	
	/**
     * 
     */
	private static final long               serialVersionUID    = -8882135636304256696L;
	
	private long                            id;
	
	private Map<String, ArrayList<String>>  changedStringValues = new HashMap<String, ArrayList<String>>();
	private Map<String, ArrayList<Integer>> changedEnumValues   = new HashMap<String, ArrayList<Integer>>();
	private Map<String, ArrayList<Date>>    changedDateValues   = new HashMap<String, ArrayList<Date>>();
	private PersonContainer                 oldPersons           = new PersonContainer();
	private PersonContainer                 newPersons          = new PersonContainer();
	
	private DateTime                        timestamp;
	private Report                          bugReport;
	private PersonContainer                 personContainer     = new PersonContainer();
	
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
	        final Map<String, ArrayList<?>> values) {
		Condition.notNull(author);
		Condition.notNull(bugReport);
		Condition.notNull(timestamp);
		Condition.notNull(values);
		
		setAuthor(author);
		setBugReport(bugReport);
		setTimestamp(timestamp);
		
		addChange(values);
	}
	
	/**
	 * @param values
	 */
	@SuppressWarnings ("unchecked")
	public void addChange(final Map<String, ArrayList<?>> values) {
		Condition.notNull(values);
		
		for (String fieldName : values.keySet()) {
			String lowerFieldName = fieldName.toLowerCase();
			if (values.get(lowerFieldName).get(0) instanceof String) {
				this.changedStringValues.put(lowerFieldName, (ArrayList<String>) values.get(lowerFieldName));
			} else if (values.get(lowerFieldName).get(0) instanceof Person) {
				this.oldPersons.add(lowerFieldName, (Person) values.get(lowerFieldName).get(0));
				this.newPersons.add(lowerFieldName, (Person) values.get(lowerFieldName).get(1));
			} else if (values.get(lowerFieldName).get(0) instanceof Integer) {
				this.changedEnumValues.put(lowerFieldName, (ArrayList<Integer>) values.get(lowerFieldName));
			} else if (values.get(lowerFieldName).get(0) instanceof Date) {
				this.changedDateValues.put(lowerFieldName, (ArrayList<Date>) values.get(lowerFieldName));
			} else {
				throw new UnrecoverableError(values.get(lowerFieldName).get(0).getClass().getCanonicalName()
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
		Condition.notNull(field);
		Condition.greater(field.length(), 1);
		Condition.notNull(oldValue);
		Condition.notNull(newValue);
		
		this.changedDateValues.put(field.toLowerCase(), new ArrayList<Date>(2) {
			
			/**
                     * 
                     */
			private static final long serialVersionUID = -3679507437553394832L;
			
			{
				add(oldValue.toDate());
				add(newValue.toDate());
			}
		});
	}
	
	/**
	 * @param field
	 * @param oldValue
	 * @param newValue
	 */
	@Transient
	public void addChangedValue(final String field, final Enum<?> oldValue, final Enum<?> newValue) {
		Condition.notNull(field);
		Condition.greater(field.length(), 1);
		Condition.notNull(oldValue);
		Condition.notNull(newValue);
		
		this.changedEnumValues.put(field.toLowerCase(), new ArrayList<Integer>(2) {
			
			/**
             * 
             */
			private static final long serialVersionUID = 5042265831412117769L;
			
			{
				add(oldValue.ordinal());
				add(newValue.ordinal());
			}
		});
	}
	
	/**
	 * @param field
	 * @param oldValue
	 * @param newValue
	 */
	@Transient
	public void addChangedValue(final String field, final Person oldValue, final Person newValue) {
		Condition.notNull(field);
		Condition.greater(field.length(), 1);
		Condition.notNull(oldValue);
		Condition.notNull(newValue);
		
		this.oldPersons.add(field, oldValue);
		this.newPersons.add(field, newValue);
	}
	
	/**
	 * @param field
	 * @param oldValue
	 * @param newValue
	 */
	@Transient
	public void addChangedValue(final String field, final String oldValue, final String newValue) {
		Condition.notNull(field);
		Condition.greater(field.length(), 1);
		Condition.notNull(oldValue);
		Condition.notNull(newValue);
		
		this.changedStringValues.put(field.toLowerCase(), new ArrayList<String>(2) {
			
			private static final long serialVersionUID = -3944857390937106108L;
			
			{
				add(oldValue);
				add(newValue);
			}
		});
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
	 * @param string
	 * @return
	 */
	@Transient
	public boolean contains(final String fieldName) {
		Condition.notNull(fieldName);
		Condition.greater(fieldName.length(), 1);
		
		String lowerFieldName = fieldName.toLowerCase();
		return getChangedDateValues().containsKey(lowerFieldName) || getChangedEnumValues().containsKey(lowerFieldName)
		        || this.oldPersons.contains(lowerFieldName) || getChangedStringValues().containsKey(lowerFieldName);
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
		if (!(obj instanceof HistoryElement)) {
			return false;
		}
		HistoryElement other = (HistoryElement) obj;
		if (this.changedDateValues == null) {
			if (other.changedDateValues != null) {
				return false;
			}
		} else if (!this.changedDateValues.equals(other.changedDateValues)) {
			return false;
		}
		if (this.changedEnumValues == null) {
			if (other.changedEnumValues != null) {
				return false;
			}
		} else if (!this.changedEnumValues.equals(other.changedEnumValues)) {
			return false;
		}
		if (this.changedStringValues == null) {
			if (other.changedStringValues != null) {
				return false;
			}
		} else if (!this.changedStringValues.equals(other.changedStringValues)) {
			return false;
		}
		if (this.id != other.id) {
			return false;
		}
		if (this.newPersons == null) {
			if (other.newPersons != null) {
				return false;
			}
		} else if (!this.newPersons.equals(other.newPersons)) {
			return false;
		}
		if (this.oldPersons == null) {
			if (other.oldPersons != null) {
				return false;
			}
		} else if (!this.oldPersons.equals(other.oldPersons)) {
			return false;
		}
		if (this.personContainer == null) {
			if (other.personContainer != null) {
				return false;
			}
		} else if (!this.personContainer.equals(other.personContainer)) {
			return false;
		}
		if (this.timestamp == null) {
			if (other.timestamp != null) {
				return false;
			}
		} else if (!this.timestamp.equals(other.timestamp)) {
			return false;
		}
		return true;
	}
	
	/**
	 * @return
	 */
	// @ManyToOne (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@Transient
	public Person getAuthor() {
		return this.personContainer.get("author");
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
	// @ManyToOne (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@ElementCollection
	private Map<String, ArrayList<Date>> getChangedDateValues() {
		return this.changedDateValues;
	}
	
	/**
	 * @return the changedEnumValues
	 */
	// @ManyToOne (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@ElementCollection
	private Map<String, ArrayList<Integer>> getChangedEnumValues() {
		return this.changedEnumValues;
	}
	
	/**
	 * @return the changedStringValues
	 */
	// @ManyToOne (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@ElementCollection
	private Map<String, ArrayList<String>> getChangedStringValues() {
		return this.changedStringValues;
	}
	
	/**
	 * @param field
	 * @return
	 */
	@Transient
	public HistoryElement getForField(final String field) {
		String lowerFieldName = field.toLowerCase();
		HistoryElement element = new HistoryElement();
		if (this.changedStringValues.containsKey(lowerFieldName)) {
			element.getChangedStringValues().put(lowerFieldName, this.changedStringValues.get(lowerFieldName));
		} else if (this.oldPersons.contains(lowerFieldName)) {
			element.addChangedValue(lowerFieldName, this.oldPersons.get(lowerFieldName),
			        this.newPersons.get(lowerFieldName));
		} else if (this.changedEnumValues.containsKey(lowerFieldName)) {
			element.getChangedEnumValues().put(lowerFieldName, this.changedEnumValues.get(lowerFieldName));
		} else if (this.changedDateValues.containsKey(lowerFieldName)) {
			element.getChangedDateValues().put(lowerFieldName, this.changedDateValues.get(lowerFieldName));
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
	 * @return the newPersons
	 */
	@OneToOne
	protected PersonContainer getNewPersons() {
		return this.newPersons;
	}
	
	/**
	 * @param fieldName
	 * @return
	 */
	@Transient
	public Object getNewValue(final String fieldName) {
		String lowerFieldName = fieldName.toLowerCase();
		if (this.changedStringValues.containsKey(lowerFieldName)) {
			return getChangedStringValues().get(lowerFieldName).get(1);
		} else if (this.newPersons.contains(lowerFieldName)) {
			return this.newPersons.get(lowerFieldName);
		} else if (this.changedEnumValues.containsKey(lowerFieldName)) {
			try {
				Class<?> e = Class.forName(this.getClass().getPackage().getName() + "."
				        + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1));
				if (e.isEnum()) {
					return e.getEnumConstants()[getChangedEnumValues().get(lowerFieldName).get(1)];
				} else {
					throw new UnrecoverableError("Found none enum constant in enum container for field: " + fieldName);
				}
			} catch (ClassNotFoundException e) {
				throw new UnrecoverableError("Found none enum constant in enum container for field: " + fieldName, e);
			}
		} else if (this.changedDateValues.containsKey(lowerFieldName)) {
			return getChangedDateValues().get(lowerFieldName).get(1);
		} else {
			return null;
		}
	}
	
	/**
	 * @return the oldPersons
	 */
	@OneToOne
	protected PersonContainer getOldPersons() {
		return this.oldPersons;
	}
	
	/**
	 * @param fieldName
	 * @return
	 */
	@Transient
	public Object getOldValue(final String fieldName) {
		String lowerFieldName = fieldName.toLowerCase();
		if (this.changedStringValues.containsKey(lowerFieldName)) {
			return getChangedStringValues().get(lowerFieldName).get(0);
		} else if (this.oldPersons.contains(lowerFieldName)) {
			return this.oldPersons.get(lowerFieldName);
		} else if (this.changedEnumValues.containsKey(lowerFieldName)) {
			try {
				Class<?> e = Class.forName(this.getClass().getPackage().getName() + "."
				        + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1));
				if (e.isEnum()) {
					return e.getEnumConstants()[getChangedEnumValues().get(lowerFieldName).get(0)];
				} else {
					throw new UnrecoverableError("Found none enum constant in enum container.");
				}
			} catch (ClassNotFoundException e) {
				throw new UnrecoverableError("Found none enum constant in enum container.", e);
			}
		} else if (this.changedDateValues.containsKey(lowerFieldName)) {
			return getChangedDateValues().get(lowerFieldName).get(0);
		} else {
			return null;
		}
	}
	
	/**
	 * @return the personContainer
	 */
	@OneToOne
	public PersonContainer getPersonContainer() {
		return this.personContainer;
	}
	
	/**
	 * @return the timestamp
	 */
	@Transient
	public DateTime getTimestamp() {
		return this.timestamp;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.changedDateValues == null) ? 0 : this.changedDateValues.hashCode());
		result = prime * result + ((this.changedEnumValues == null) ? 0 : this.changedEnumValues.hashCode());
		result = prime * result + ((this.changedStringValues == null) ? 0 : this.changedStringValues.hashCode());
		result = prime * result + (int) (this.id ^ (this.id >>> 32));
		result = prime * result + ((this.newPersons == null) ? 0 : this.newPersons.hashCode());
		result = prime * result + ((this.oldPersons == null) ? 0 : this.oldPersons.hashCode());
		result = prime * result + ((this.personContainer == null) ? 0 : this.personContainer.hashCode());
		result = prime * result + ((this.timestamp == null) ? 0 : this.timestamp.hashCode());
		return result;
	}
	
	/**
	 * @return
	 */
	@Transient
	public boolean isEmpty() {
		return this.changedDateValues.isEmpty() && this.changedEnumValues.isEmpty() && this.oldPersons.isEmpty()
		        && this.changedStringValues.isEmpty();
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
	 * @param author
	 */
	public void setAuthor(final Person author) {
		this.personContainer.add("author", author);
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
	private void setChangedDateValues(final Map<String, ArrayList<Date>> changedDateValues) {
		this.changedDateValues = changedDateValues;
	}
	
	/**
	 * @param changedEnumValues
	 *            the changedEnumValues to set
	 */
	@SuppressWarnings ("unused")
	private void setChangedEnumValues(final Map<String, ArrayList<Integer>> changedEnumValues) {
		this.changedEnumValues = changedEnumValues;
	}
	
	/**
	 * @param changedStringValues
	 *            the changedStringValues to set
	 */
	@SuppressWarnings ("unused")
	private void setChangedStringValues(final Map<String, ArrayList<String>> changedStringValues) {
		this.changedStringValues = changedStringValues;
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
	 * @param newPersons
	 *            the newPersons to set
	 */
	protected void setNewPersons(final PersonContainer newPersons) {
		this.newPersons = newPersons;
	}
	
	/**
	 * @param oldPersons
	 *            the oldPersons to set
	 */
	protected void setOldPersons(final PersonContainer oldPerons) {
		this.oldPersons = oldPerons;
	}
	
	/**
	 * @param personContainer
	 *            the personContainer to set
	 */
	public void setPersonContainer(final PersonContainer personContainer) {
		this.personContainer = personContainer;
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
		return this.changedDateValues.size() + this.changedEnumValues.size() + this.oldPersons.size()
		        + this.changedStringValues.size();
	}
	
}
