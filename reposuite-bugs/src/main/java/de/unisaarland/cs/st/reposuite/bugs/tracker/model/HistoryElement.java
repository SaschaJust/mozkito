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
import de.unisaarland.cs.st.reposuite.rcs.model.DateTimeTuple;
import de.unisaarland.cs.st.reposuite.rcs.model.EnumTuple;
import de.unisaarland.cs.st.reposuite.rcs.model.Person;
import de.unisaarland.cs.st.reposuite.rcs.model.PersonContainer;
import de.unisaarland.cs.st.reposuite.rcs.model.PersonTuple;
import de.unisaarland.cs.st.reposuite.rcs.model.StringTuple;
import de.unisaarland.cs.st.reposuite.utils.JavaUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.Tuple;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Entity
@Table (name = "history_element")
public class HistoryElement implements Annotated, TextElement, Comparable<HistoryElement> {
	
	/**
	 * 
	 */
	private static final long          serialVersionUID    = -8882135636304256696L;
	
	private long                       id;
	
	private Map<String, StringTuple>   changedStringValues = new HashMap<String, StringTuple>();
	private Map<String, EnumTuple>     changedEnumValues   = new HashMap<String, EnumTuple>();
	private Map<String, DateTimeTuple> changedDateValues   = new HashMap<String, DateTimeTuple>();
	private Map<String, PersonTuple>   changedPersonValues = new HashMap<String, PersonTuple>();
	
	private DateTime                   timestamp;
	private long                       bugId;
	private PersonContainer            personContainer     = new PersonContainer();
	
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
	public HistoryElement(final long bugId, final Person author, final DateTime timestamp) {
		setBugId(bugId);
		setAuthor(author);
		setTimestamp(timestamp);
	}
	
	/**
	 * @param values
	 */
	@SuppressWarnings ("unchecked")
	public <T> Tuple<T, T> addChange(@NotNull final Map<String, Tuple<T, T>> values) {
		
		Tuple<T, T> ret = null;
		
		for (String fieldName : values.keySet()) {
			String lowerFieldName = fieldName.toLowerCase();
			
			Report report = new Report(0);
			Class<?> type = null;
			
			if (values.get(fieldName).getFirst() == null) {
				if (values.get(fieldName).getSecond() == null) {
					if (report.getField(lowerFieldName) != null) {
						type = report.getField(lowerFieldName).getClass();
					} else {
						Tuple<T, T> object = get(lowerFieldName);
						
						if (object != null) {
							type = object.getFirst() != null
							                                ? object.getClass().getClass()
							                                : object.getSecond().getClass();
						} else {
							if (Logger.logWarn()) {
								Logger.warn("HistoryElement tries to delete field that hasn't been set. Ignoring.");
							}
						}
					}
				} else {
					type = values.get(fieldName).getSecond().getClass();
				}
			} else {
				type = values.get(fieldName).getFirst().getClass();
			}
			
			if (type == String.class) {
				Map<String, StringTuple> stringValues = getChangedStringValues();
				StringTuple tuple = stringValues.put(lowerFieldName, new StringTuple((String) values.get(fieldName)
				                                                                                    .getFirst(),
				                                                                     (String) values.get(fieldName)
				                                                                                    .getSecond()));
				if (tuple != null) {
					ret = (Tuple<T, T>) new Tuple<String, String>(tuple.getOldValue(), tuple.getNewValue());
				}
				setChangedStringValues(stringValues);
			} else if (type == Person.class) {
				Map<String, PersonTuple> personValues = getChangedPersonValues();
				PersonTuple container = personValues.put(lowerFieldName, new PersonTuple((Person) values.get(fieldName)
				                                                                                        .getFirst(),
				                                                                         (Person) values.get(fieldName)
				                                                                                        .getSecond()));
				if (container != null) {
					ret = (Tuple<T, T>) new Tuple<Person, Person>(
					                                              container.getOldValue().getPersons().isEmpty()
					                                                                                            ? null
					                                                                                            : container.getOldValue()
					                                                                                                       .getPersons()
					                                                                                                       .iterator()
					                                                                                                       .next(),
					                                              container.getNewValue().getPersons().isEmpty()
					                                                                                            ? null
					                                                                                            : container.getNewValue()
					                                                                                                       .getPersons()
					                                                                                                       .iterator()
					                                                                                                       .next());
				}
				setChangedPersonValues(personValues);
			} else if (type.isEnum()) {
				Map<String, EnumTuple> enumValues = getChangedEnumValues();
				EnumTuple tuple = enumValues.put(lowerFieldName, new EnumTuple((Enum<?>) values.get(fieldName)
				                                                                               .getFirst(),
				                                                               (Enum<?>) values.get(fieldName)
				                                                                               .getSecond()));
				if (tuple != null) {
					ret = (Tuple<T, T>) new Tuple<Enum<?>, Enum<?>>(tuple.getOldValue(), tuple.getNewValue());
				}
				setChangedEnumValues(enumValues);
			} else if (type == DateTime.class) {
				Map<String, DateTimeTuple> dateValues = getChangedDateValues();
				DateTimeTuple tuple = dateValues.put(lowerFieldName, new DateTimeTuple((DateTime) values.get(fieldName)
				                                                                                        .getFirst(),
				                                                                       (DateTime) values.get(fieldName)
				                                                                                        .getSecond()));
				if (tuple != null) {
					ret = (Tuple<T, T>) new Tuple<DateTime, DateTime>(tuple.getOldValue(), tuple.getNewValue());
				}
				setChangedDateValues(dateValues);
			} else {
				throw new UnrecoverableError(values.get(fieldName).getClass().getCanonicalName()
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
	@SuppressWarnings ("serial")
	public void addChangedValue(final String field,
	                            final DateTime oldValue,
	                            final DateTime newValue) {
		addChange(new HashMap<String, Tuple<DateTime, DateTime>>() {
			
			{
				put(field, new Tuple<DateTime, DateTime>(oldValue, newValue));
			}
		});
	}
	
	/**
	 * @param field
	 * @param oldValue
	 * @param newValue
	 */
	@SuppressWarnings ("serial")
	public void addChangedValue(final String field,
	                            final Enum<?> oldValue,
	                            final Enum<?> newValue) {
		addChange(new HashMap<String, Tuple<Enum<?>, Enum<?>>>() {
			
			{
				put(field, new Tuple<Enum<?>, Enum<?>>(oldValue, newValue));
			}
		});
	}
	
	/**
	 * @param field
	 * @param oldValue
	 * @param newValue
	 */
	@SuppressWarnings ("serial")
	public void addChangedValue(final String field,
	                            final Person oldValue,
	                            final Person newValue) {
		addChange(new HashMap<String, Tuple<Person, Person>>() {
			
			{
				put(field, new Tuple<Person, Person>(oldValue, newValue));
			}
		});
	}
	
	/**
	 * @param field
	 * @param oldValue
	 * @param newValue
	 */
	@SuppressWarnings ("serial")
	public void addChangedValue(final String field,
	                            final String oldValue,
	                            final String newValue) {
		addChange(new HashMap<String, Tuple<String, String>>() {
			
			{
				put(field, new Tuple<String, String>(oldValue, newValue));
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
		if (this.bugId != other.bugId) {
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
	 * @param field
	 * @return
	 */
	@SuppressWarnings ("unchecked")
	public <T> Tuple<T, T> get(final String field) {
		String lowerFieldName = field.toLowerCase();
		
		if (getChangedStringValues().containsKey(lowerFieldName)) {
			return (Tuple<T, T>) new Tuple<String, String>(getChangedStringValues().get(lowerFieldName).getOldValue(),
			                                               getChangedStringValues().get(lowerFieldName).getNewValue());
		} else if (getChangedPersonValues().containsKey(lowerFieldName)) {
			return (Tuple<T, T>) new Tuple<Person, Person>(
			                                               getChangedPersonValues().get(lowerFieldName).getOldValue()
			                                                                       .isEmpty()
			                                                                                 ? null
			                                                                                 : getChangedPersonValues().get(lowerFieldName)
			                                                                                                           .getOldValue()
			                                                                                                           .getPersons()
			                                                                                                           .iterator()
			                                                                                                           .next(),
			                                               getChangedPersonValues().get(lowerFieldName).getNewValue()
			                                                                       .isEmpty()
			                                                                                 ? null
			                                                                                 : getChangedPersonValues().get(lowerFieldName)
			                                                                                                           .getNewValue()
			                                                                                                           .getPersons()
			                                                                                                           .iterator()
			                                                                                                           .next());
		} else if (getChangedEnumValues().containsKey(lowerFieldName)) {
			return (Tuple<T, T>) new Tuple<Enum<?>, Enum<?>>(getChangedEnumValues().get(lowerFieldName).getOldValue(),
			                                                 getChangedEnumValues().get(lowerFieldName).getNewValue());
		} else if (getChangedDateValues().containsKey(lowerFieldName)) {
			return (Tuple<T, T>) new Tuple<DateTime, DateTime>(
			                                                   getChangedDateValues().get(lowerFieldName).getOldValue(),
			                                                   getChangedDateValues().get(lowerFieldName).getNewValue());
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
	 * @return the bugId
	 */
	public long getBugId() {
		return this.bugId;
	}
	
	/**
	 * @return the changedDateValues
	 */
	// @ManyToOne (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	// @ManyToMany (cascade = CascadeType.ALL)
	@ElementCollection
	public Map<String, DateTimeTuple> getChangedDateValues() {
		return this.changedDateValues;
	}
	
	/**
	 * @return the changedEnumValues
	 */
	// @ManyToOne (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	// @ManyToMany (cascade = CascadeType.ALL)
	@ElementCollection
	public Map<String, EnumTuple> getChangedEnumValues() {
		return this.changedEnumValues;
	}
	
	/**
	 * @return the changedPersonValues
	 */
	@ManyToMany (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public Map<String, PersonTuple> getChangedPersonValues() {
		return this.changedPersonValues;
	}
	
	/**
	 * @return the changedStringValues
	 */
	// @ManyToOne (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	// @ManyToMany (cascade = CascadeType.ALL)
	@ElementCollection
	public Map<String, StringTuple> getChangedStringValues() {
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
		element.setBugId(getBugId());
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
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.bugs.tracker.model.TextElement#getText()
	 */
	@Override
	public String getText() {
		return null;
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
		result = prime * result + (int) (this.bugId ^ (this.bugId >>> 32));
		result = prime * result + ((this.timestamp == null)
		                                                   ? 0
		                                                   : this.timestamp.hashCode());
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
	 * @param bugId the bugId to set
	 */
	public void setBugId(final long bugId) {
		this.bugId = bugId;
	}
	
	/**
	 * @param changedDateValues
	 *            the changedDateValues to set
	 */
	private void setChangedDateValues(final Map<String, DateTimeTuple> changedDateValues) {
		this.changedDateValues = changedDateValues;
	}
	
	/**
	 * @param changedEnumValues
	 *            the changedEnumValues to set
	 */
	private void setChangedEnumValues(final Map<String, EnumTuple> changedEnumValues) {
		this.changedEnumValues = changedEnumValues;
	}
	
	/**
	 * @param changedPersonValues
	 *            the changedPersonValues to set
	 */
	private void setChangedPersonValues(final Map<String, PersonTuple> changedPersonValues) {
		this.changedPersonValues = changedPersonValues;
	}
	
	/**
	 * @param changedStringValues
	 *            the changedStringValues to set
	 */
	private void setChangedStringValues(final Map<String, StringTuple> changedStringValues) {
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
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.bugs.tracker.model.TextElement#getText()
	 */
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
		builder.append(", bugId=");
		builder.append(getBugId());
		builder.append(", personContainer=");
		builder.append(getPersonContainer());
		builder.append("]");
		return builder.toString();
	}
}
