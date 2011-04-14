/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.model;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.MinSize;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.rcs.model.Person;
import de.unisaarland.cs.st.reposuite.rcs.model.PersonContainer;
import de.unisaarland.cs.st.reposuite.utils.JavaUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;

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
	private static final long            serialVersionUID    = -8882135636304256696L;
	
	private long                         id;
	
	private Map<String, String>          changedStringValues = new HashMap<String, String>();
	private Map<String, Enum<?>>         changedEnumValues   = new HashMap<String, Enum<?>>();
	private Map<String, Date>            changedDateValues   = new HashMap<String, Date>();
	private Map<String, PersonContainer> changedPersonValues = new HashMap<String, PersonContainer>();
	
	private DateTime                     timestamp;
	private Report                       bugReport;
	private PersonContainer              personContainer     = new PersonContainer();
	
	/**
	 * used by PersistenceUtil
	 */
	protected HistoryElement() {
		
	}
	
	/**
	 * @param field
	 * @param oldValue
	 * @param newValue
	 * @param timestamp
	 */
	@NoneNull
	public HistoryElement(final Person author, final DateTime timestamp, final Map<String, ?> values) {
		setAuthor(author);
		setTimestamp(timestamp);
		
		addChange(values);
	}
	
	/**
	 * @param values
	 */
	@SuppressWarnings ("unchecked")
	public <T> T addChange(@NotNull final Map<String, T> values) {
		
		T ret = null;
		
		for (String fieldName : values.keySet()) {
			String lowerFieldName = fieldName.toLowerCase();
			
			Report report = new Report();
			Class<?> type = null;
			
			if (values.get(fieldName) == null) {
				if (report.getField(lowerFieldName) != null) {
					type = report.getField(lowerFieldName).getClass();
				} else {
					Object object = get(lowerFieldName);
					if (object != null) {
						type = object.getClass();
					} else {
						if (Logger.logWarn()) {
							Logger.warn("HistoryElement tries to delete field that hasn't been set. Ignoring.");
						}
					}
				}
			} else {
				type = values.get(fieldName).getClass();
			}
			
			if (type == String.class) {
				Map<String, String> stringValues = getChangedStringValues();
				ret = (T) stringValues.put(lowerFieldName, (String) values.get(lowerFieldName));
				setChangedStringValues(stringValues);
			} else if (type == Person.class) {
				Map<String, PersonContainer> personValues = getChangedPersonValues();
				PersonContainer container = personValues.put(lowerFieldName,
				                                             (PersonContainer) values.get(lowerFieldName));
				if (container != null) {
					ret = (T) container.get(lowerFieldName);
				}
				setChangedPersonValues(personValues);
			} else if (type.isEnum()) {
				Map<String, Enum<?>> enumValues = getChangedEnumValues();
				ret = (T) enumValues.put(lowerFieldName, (Enum<?>) values.get(lowerFieldName));
				setChangedEnumValues(enumValues);
			} else if (type == DateTime.class) {
				Map<String, Date> dateValues = getChangedDateValues();
				Date date = dateValues.put(lowerFieldName, ((DateTime) values.get(lowerFieldName)).toDate());
				if (date != null) {
					ret = (T) new DateTime(date);
				}
				setChangedDateValues(dateValues);
			} else if (type == Date.class) {
				Map<String, Date> dateValues = getChangedDateValues();
				ret = (T) dateValues.put(lowerFieldName, (Date) values.get(lowerFieldName));
				setChangedDateValues(dateValues);
			} else {
				throw new UnrecoverableError(values.get(lowerFieldName).getClass().getCanonicalName()
				        + " is not supported for " + HistoryElement.class.getSimpleName() + ".");
			}
		}
		return ret;
	}
	
	/**
	 * @param field
	 * @param oldValue
	 * @param newValue
	 */
	@Transient
	@NoneNull
	public DateTime addChangedValue(@MinSize (min = 2) final String field,
	                                final DateTime newValue) {
		Map<String, Date> dateValues = getChangedDateValues();
		DateTime ret = new DateTime(dateValues.put(field.toLowerCase(), newValue.toDate()));
		setChangedDateValues(dateValues);
		
		return ret;
	}
	
	/**
	 * @param field
	 * @param oldValue
	 * @param newValue
	 */
	@Transient
	@NoneNull
	public Enum<?> addChangedValue(@MinSize (min = 2) final String field,
	                               final Enum<?> newValue) {
		Map<String, Enum<?>> enumValues = getChangedEnumValues();
		Enum<?> ret = enumValues.put(field.toLowerCase(), newValue);
		setChangedEnumValues(enumValues);
		return ret;
	}
	
	/**
	 * @param field
	 * @param removed
	 * @param added
	 */
	@Deprecated
	public void addChangedValue(final String field,
	                            final Enum<?> removed,
	                            final Enum<?> added) {
		addChangedValue(field, added);
	}
	
	/**
	 * @param field
	 * @param oldValue
	 * @param newValue
	 */
	@Transient
	@NoneNull
	public PersonContainer addChangedValue(final String field,
	                                       final Person newValue) {
		Map<String, PersonContainer> personValues = getChangedPersonValues();
		@SuppressWarnings ("serial")
		PersonContainer ret = personValues.put(field, new PersonContainer() {
			
			{
				add("field", newValue);
			}
		});
		setChangedPersonValues(personValues);
		return ret;
	}
	
	/**
	 * @param field
	 * @param oldValue
	 * @param newValue
	 */
	@Deprecated
	public void addChangedValue(final String field,
	                            final Person oldValue,
	                            final Person newValue) {
		addChangedValue(field, newValue);
	}
	
	/**
	 * @param field
	 * @param oldValue
	 * @param newValue
	 */
	@Transient
	@NoneNull
	public PersonContainer addChangedValue(@MinSize (min = 2) final String field,
	                                       final PersonContainer newValue) {
		Map<String, PersonContainer> personValues = getChangedPersonValues();
		PersonContainer ret = personValues.put(field, newValue);
		setChangedPersonValues(personValues);
		return ret;
	}
	
	/**
	 * @param field
	 * @param oldValue
	 * @param newValue
	 * @return 
	 */
	@Transient
	@NoneNull
	public String addChangedValue(@MinSize (min = 2) final String field,
	                              final String newValue) {
		Map<String, String> stringValues = getChangedStringValues();
		String ret = stringValues.put(field, newValue);
		setChangedStringValues(stringValues);
		return ret;
	}
	
	/**
	 * @param field
	 * @param removed
	 * @param added
	 */
	@Deprecated
	public void addChangedValue(final String field,
	                            final String removed,
	                            final String added) {
		addChangedValue(field, added);
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
			return getTimestamp().compareTo(object.getTimestamp());
		}
	}
	
	/**
	 * @param string
	 * @return
	 */
	@Transient
	public boolean contains(@NotNull @MinSize (min = 2) final String fieldName) {
		String lowerFieldName = fieldName.toLowerCase();
		return getChangedDateValues().containsKey(lowerFieldName) || getChangedEnumValues().containsKey(lowerFieldName)
		        || getChangedPersonValues().containsKey(lowerFieldName)
		        || getChangedStringValues().containsKey(lowerFieldName);
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
		if (getChangedDateValues() == null) {
			if (other.getChangedDateValues() != null) {
				return false;
			}
		} else if (!getChangedDateValues().equals(other.getChangedDateValues())) {
			return false;
		}
		if (getChangedEnumValues() == null) {
			if (other.getChangedEnumValues() != null) {
				return false;
			}
		} else if (!this.getChangedEnumValues().equals(other.getChangedEnumValues())) {
			return false;
		}
		if (this.getChangedPersonValues() == null) {
			if (other.getChangedPersonValues() != null) {
				return false;
			}
		} else if (!this.getChangedPersonValues().equals(other.getChangedPersonValues())) {
			return false;
		}
		if (this.getChangedStringValues() == null) {
			if (other.getChangedStringValues() != null) {
				return false;
			}
		} else if (!this.getChangedStringValues().equals(other.getChangedStringValues())) {
			return false;
		}
		if (getPersonContainer() == null) {
			if (other.getPersonContainer() != null) {
				return false;
			}
		} else if (!this.getPersonContainer().equals(other.getPersonContainer())) {
			return false;
		}
		if (this.getTimestamp() == null) {
			if (other.getTimestamp() != null) {
				return false;
			}
		} else if (!this.getTimestamp().equals(other.getTimestamp())) {
			return false;
		}
		return true;
	}
	
	/**
	 * @param field
	 * @return
	 */
	public Object get(final String field) {
		String lowerFieldName = field.toLowerCase();
		
		if (getChangedStringValues().containsKey(lowerFieldName)) {
			return getChangedStringValues().get(lowerFieldName);
		} else if (getChangedPersonValues().containsKey(lowerFieldName)) {
			return getChangedPersonValues().get(lowerFieldName);
		} else if (getChangedEnumValues().containsKey(lowerFieldName)) {
			return getChangedEnumValues().get(lowerFieldName);
		} else if (getChangedDateValues().containsKey(lowerFieldName)) {
			return getChangedDateValues().get(lowerFieldName);
		}
		return null;
	}
	
	/**
	 * @return
	 */
	// @ManyToOne (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@Transient
	public Person getAuthor() {
		return getPersonContainer().get("author");
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
	// @ManyToMany (cascade = CascadeType.ALL)
	@ElementCollection
	private Map<String, Date> getChangedDateValues() {
		return this.changedDateValues;
	}
	
	/**
	 * @return the changedEnumValues
	 */
	// @ManyToOne (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	// @ManyToMany (cascade = CascadeType.ALL)
	@ElementCollection
	private Map<String, Enum<?>> getChangedEnumValues() {
		return this.changedEnumValues;
	}
	
	/**
	 * @return the changedPersonValues
	 */
	@ManyToMany (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public Map<String, PersonContainer> getChangedPersonValues() {
		return this.changedPersonValues;
	}
	
	/**
	 * @return the changedStringValues
	 */
	// @ManyToOne (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	// @ManyToMany (cascade = CascadeType.ALL)
	@ElementCollection
	private Map<String, String> getChangedStringValues() {
		return this.changedStringValues;
	}
	
	/**
	 * @return
	 */
	@Transient
	public Set<String> getFields() {
		HashSet<String> set = new HashSet<String>();
		set.addAll(getChangedDateValues().keySet());
		set.addAll(getChangedEnumValues().keySet());
		set.addAll(getChangedDateValues().keySet());
		set.addAll(getChangedPersonValues().keySet());
		
		return set;
	}
	
	/**
	 * @param field
	 * @return
	 */
	@Transient
	public HistoryElement getForField(final String field) {
		String lowerFieldName = field.toLowerCase();
		HistoryElement element = new HistoryElement();
		if (getChangedStringValues().containsKey(lowerFieldName)) {
			element.getChangedStringValues().put(lowerFieldName, getChangedStringValues().get(lowerFieldName));
		} else if (getChangedPersonValues().containsKey(lowerFieldName)) {
			element.getChangedPersonValues().put(lowerFieldName, getChangedPersonValues().get(lowerFieldName));
		} else if (getChangedEnumValues().containsKey(lowerFieldName)) {
			element.getChangedEnumValues().put(lowerFieldName, getChangedEnumValues().get(lowerFieldName));
		} else if (getChangedDateValues().containsKey(lowerFieldName)) {
			element.getChangedDateValues().put(lowerFieldName, getChangedDateValues().get(lowerFieldName));
		}
		element.setTimestamp(getTimestamp());
		element.setBugReport(getBugReport());
		element.setAuthor(getAuthor());
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
		return getTimestamp().toDate();
	}
	
	/**
	 * @return the personContainer
	 */
	@OneToOne (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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
		result = prime * result + ((getChangedDateValues() == null)
		                                                           ? 0
		                                                           : getChangedDateValues().hashCode());
		result = prime * result + ((getChangedEnumValues() == null)
		                                                           ? 0
		                                                           : getChangedEnumValues().hashCode());
		result = prime * result + ((getChangedPersonValues() == null)
		                                                             ? 0
		                                                             : getChangedPersonValues().hashCode());
		result = prime * result + ((getChangedStringValues() == null)
		                                                             ? 0
		                                                             : getChangedStringValues().hashCode());
		result = prime * result + ((getPersonContainer() == null)
		                                                         ? 0
		                                                         : getPersonContainer().hashCode());
		result = prime * result + ((getTimestamp() == null)
		                                                   ? 0
		                                                   : getTimestamp().hashCode());
		return result;
	}
	
	/**
	 * @return
	 */
	@Transient
	public boolean isEmpty() {
		return getChangedDateValues().isEmpty() && getChangedEnumValues().isEmpty()
		        && getChangedPersonValues().isEmpty() && getChangedStringValues().isEmpty();
	}
	
	/**
	 * @param author
	 */
	public void setAuthor(final Person author) {
		getPersonContainer().add("author", author);
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
	private void setChangedDateValues(final Map<String, Date> changedDateValues) {
		this.changedDateValues = changedDateValues;
	}
	
	/**
	 * @param changedEnumValues
	 *            the changedEnumValues to set
	 */
	private void setChangedEnumValues(final Map<String, Enum<?>> changedEnumValues) {
		this.changedEnumValues = changedEnumValues;
	}
	
	/**
	 * @param changedPersonValues
	 *            the changedPersonValues to set
	 */
	private void setChangedPersonValues(final Map<String, PersonContainer> changedPersonValues) {
		this.changedPersonValues = changedPersonValues;
	}
	
	/**
	 * @param changedStringValues
	 *            the changedStringValues to set
	 */
	private void setChangedStringValues(final Map<String, String> changedStringValues) {
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
	public void setTimestamp(@NotNull final DateTime timestamp) {
		this.timestamp = timestamp;
	}
	
	/**
	 * @return
	 */
	public int size() {
		return getChangedDateValues().size() + getChangedEnumValues().size() + getChangedPersonValues().size()
		        + getChangedStringValues().size();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HistoryElement [changedStringValues=");
		builder.append(JavaUtils.mapToString(getChangedStringValues()));
		builder.append(", changedEnumValues=");
		builder.append(JavaUtils.mapToString(getChangedEnumValues()));
		builder.append(", changedDateValues=");
		builder.append(JavaUtils.mapToString(getChangedDateValues()));
		builder.append(", changedPersonValues=");
		builder.append(JavaUtils.mapToString(getChangedPersonValues()));
		builder.append(", timestamp=");
		builder.append(getTimestamp());
		builder.append(", bugReport=");
		builder.append(getBugReport() != null
		                                     ? getBugReport().getId()
		                                     : "(unset)");
		builder.append(", personContainer=");
		builder.append(getPersonContainer());
		builder.append("]");
		return builder.toString();
	}
	
}
