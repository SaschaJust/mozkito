/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/
package org.mozkito.issues.tracker.model;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.MinSize;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;

import org.joda.time.DateTime;

import org.mozkito.persistence.Annotated;
import org.mozkito.persistence.model.DateTimeTuple;
import org.mozkito.persistence.model.EnumTuple;
import org.mozkito.persistence.model.Person;
import org.mozkito.persistence.model.PersonContainer;
import org.mozkito.persistence.model.PersonTuple;
import org.mozkito.persistence.model.StringTuple;

/**
 * The Class HistoryElement.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
@Entity
@Table (name = "history_element")
public class HistoryElement implements Annotated, TextElement, Comparable<HistoryElement> {
	
	/** The Constant serialVersionUID. */
	private static final long          serialVersionUID    = -8882135636304256696L;
	
	/** The id. */
	private long                       id;
	
	/** The changed string values. */
	private Map<String, StringTuple>   changedStringValues = new HashMap<String, StringTuple>();
	
	/** The changed enum values. */
	private Map<String, EnumTuple>     changedEnumValues   = new HashMap<String, EnumTuple>();
	
	/** The changed date values. */
	private Map<String, DateTimeTuple> changedDateValues   = new HashMap<String, DateTimeTuple>();
	
	/** The changed person values. */
	private Map<String, PersonTuple>   changedPersonValues = new HashMap<String, PersonTuple>();
	
	/** The timestamp. */
	private DateTime                   timestamp;
	
	/** The bug id. */
	private String                     bugId;
	
	/** The person container. */
	private PersonContainer            personContainer     = new PersonContainer();
	
	/**
	 * used by PersistenceUtil.
	 */
	protected HistoryElement() {
		
	}
	
	/**
	 * Instantiates a new history element.
	 * 
	 * @param bugId
	 *            the bug id
	 * @param author
	 *            the author
	 * @param timestamp
	 *            the timestamp
	 */
	@NoneNull
	public HistoryElement(final String bugId, final Person author, final DateTime timestamp) {
		setBugId(bugId);
		setAuthor(author);
		setTimestamp(timestamp);
	}
	
	/**
	 * Adds the change.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param values
	 *            the values
	 * @return the tuple
	 */
	@SuppressWarnings ("unchecked")
	public <T> Tuple<T, T> addChange(@NotNull final Map<String, Tuple<T, T>> values) {
		
		Tuple<T, T> ret = null;
		
		for (final String fieldName : values.keySet()) {
			final String lowerFieldName = fieldName.toLowerCase();
			
			final Report report = new Report("<unknown>");
			Class<?> type = null;
			
			if (values.get(fieldName).getFirst() == null) {
				if (values.get(fieldName).getSecond() == null) {
					if (report.getField(lowerFieldName) != null) {
						type = report.getField(lowerFieldName).getClass();
					} else {
						final Tuple<?, ?> object = get(lowerFieldName);
						
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
			
			assert (type != null);
			
			if (type == String.class) {
				final Map<String, StringTuple> stringValues = getChangedStringValues();
				final StringTuple tuple = stringValues.put(lowerFieldName,
				                                           new StringTuple((String) values.get(fieldName).getFirst(),
				                                                           (String) values.get(fieldName).getSecond()));
				if (tuple != null) {
					ret = (Tuple<T, T>) new Tuple<String, String>(tuple.getOldValue(), tuple.getNewValue());
				}
				setChangedStringValues(stringValues);
			} else if (type == Person.class) {
				final Map<String, PersonTuple> personValues = getChangedPersonValues();
				final PersonTuple container = personValues.put(lowerFieldName,
				                                               new PersonTuple((Person) values.get(fieldName)
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
				final Map<String, EnumTuple> enumValues = getChangedEnumValues();
				final EnumTuple tuple = enumValues.put(lowerFieldName, new EnumTuple((Enum<?>) values.get(fieldName)
				                                                                                     .getFirst(),
				                                                                     (Enum<?>) values.get(fieldName)
				                                                                                     .getSecond()));
				if (tuple != null) {
					ret = (Tuple<T, T>) new Tuple<Enum<?>, Enum<?>>(tuple.getOldValue(), tuple.getNewValue());
				}
				setChangedEnumValues(enumValues);
			} else if (type == DateTime.class) {
				final Map<String, DateTimeTuple> dateValues = getChangedDateValues();
				final DateTimeTuple tuple = dateValues.put(lowerFieldName,
				                                           new DateTimeTuple((DateTime) values.get(fieldName)
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
	 * Adds the changed value.
	 * 
	 * @param field
	 *            the field
	 * @param oldValue
	 *            the old value
	 * @param newValue
	 *            the new value
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
	 * Adds the changed value.
	 * 
	 * @param field
	 *            the field
	 * @param oldValue
	 *            the old value
	 * @param newValue
	 *            the new value
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
	 * Adds the changed value.
	 * 
	 * @param field
	 *            the field
	 * @param oldValue
	 *            the old value
	 * @param newValue
	 *            the new value
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
	 * Adds the changed value.
	 * 
	 * @param field
	 *            the field
	 * @param oldValue
	 *            the old value
	 * @param newValue
	 *            the new value
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
	@Override
	@Transient
	public int compareTo(final HistoryElement object) {
		if (object == null) {
			return 1;
		}
		return getTimestamp().compareTo(object.getTimestamp());
	}
	
	/**
	 * Contains.
	 * 
	 * @param fieldName
	 *            the field name
	 * @return true, if successful
	 */
	@Transient
	public boolean contains(@NotNull @MinSize (min = 2) final String fieldName) {
		final String lowerFieldName = fieldName.toLowerCase();
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
		final HistoryElement other = (HistoryElement) obj;
		if (getBugId() != other.getBugId()) {
			return false;
		}
		if (getTimestamp() == null) {
			if (other.getTimestamp() != null) {
				return false;
			}
		} else if (!getTimestamp().equals(other.getTimestamp())) {
			return false;
		}
		return true;
	}
	
	/**
	 * Gets the.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param field
	 *            the field
	 * @return the tuple
	 */
	public <T> Tuple<?, ?> get(final String field) {
		final String lowerFieldName = field.toLowerCase();
		
		if (getChangedStringValues().containsKey(lowerFieldName)) {
			return new Tuple<String, String>(getChangedStringValues().get(lowerFieldName).getOldValue(),
			                                 getChangedStringValues().get(lowerFieldName).getNewValue());
		} else if (getChangedPersonValues().containsKey(lowerFieldName)) {
			return new Tuple<Person, Person>(
			                                 getChangedPersonValues().get(lowerFieldName).getOldValue().isEmpty()
			                                                                                                     ? null
			                                                                                                     : getChangedPersonValues().get(lowerFieldName)
			                                                                                                                               .getOldValue()
			                                                                                                                               .getPersons()
			                                                                                                                               .iterator()
			                                                                                                                               .next(),
			                                 getChangedPersonValues().get(lowerFieldName).getNewValue().isEmpty()
			                                                                                                     ? null
			                                                                                                     : getChangedPersonValues().get(lowerFieldName)
			                                                                                                                               .getNewValue()
			                                                                                                                               .getPersons()
			                                                                                                                               .iterator()
			                                                                                                                               .next());
		} else if (getChangedEnumValues().containsKey(lowerFieldName)) {
			return new Tuple<Enum<?>, Enum<?>>(getChangedEnumValues().get(lowerFieldName).getOldValue(),
			                                   getChangedEnumValues().get(lowerFieldName).getNewValue());
		} else if (getChangedDateValues().containsKey(lowerFieldName)) {
			return new Tuple<DateTime, DateTime>(getChangedDateValues().get(lowerFieldName).getOldValue(),
			                                     getChangedDateValues().get(lowerFieldName).getNewValue());
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.model.TextElement#getAuthor()
	 */
	@Override
	// @ManyToOne (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@Transient
	public Person getAuthor() {
		return getPersonContainer().get("author");
	}
	
	/**
	 * Gets the bug id.
	 * 
	 * @return the bugId
	 */
	public String getBugId() {
		return this.bugId;
	}
	
	/**
	 * Gets the changed date values.
	 * 
	 * @return the changedDateValues
	 */
	// @ManyToOne (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	// @ManyToMany (cascade = CascadeType.ALL)
	@ElementCollection
	public Map<String, DateTimeTuple> getChangedDateValues() {
		return this.changedDateValues;
	}
	
	/**
	 * Gets the changed enum values.
	 * 
	 * @return the changedEnumValues
	 */
	// @ManyToOne (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	// @ManyToMany (cascade = CascadeType.ALL)
	@ElementCollection
	public Map<String, EnumTuple> getChangedEnumValues() {
		return this.changedEnumValues;
	}
	
	/**
	 * Gets the changed person values.
	 * 
	 * @return the changedPersonValues
	 */
	// @ManyToMany (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@ElementCollection
	public Map<String, PersonTuple> getChangedPersonValues() {
		return this.changedPersonValues;
	}
	
	/**
	 * Gets the changed string values.
	 * 
	 * @return the changedStringValues
	 */
	// @ManyToOne (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	// @ManyToMany (cascade = CascadeType.ALL)
	@ElementCollection
	public Map<String, StringTuple> getChangedStringValues() {
		return this.changedStringValues;
	}
	
	/**
	 * Gets the fields.
	 * 
	 * @return the fields
	 */
	@Transient
	public Set<String> getFields() {
		final HashSet<String> set = new HashSet<String>();
		set.addAll(getChangedDateValues().keySet());
		set.addAll(getChangedEnumValues().keySet());
		set.addAll(getChangedDateValues().keySet());
		set.addAll(getChangedPersonValues().keySet());
		set.addAll(getChangedStringValues().keySet());
		return set;
	}
	
	/**
	 * Gets the for field.
	 * 
	 * @param field
	 *            the field
	 * @return the for field
	 */
	@Transient
	public HistoryElement getForField(final String field) {
		final String lowerFieldName = field.toLowerCase();
		final HistoryElement element = new HistoryElement();
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
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.Annotated#getHandle()
	 */
	public final String getHandle() {
		return JavaUtils.getHandle(HistoryElement.class);
	}
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE)
	public long getId() {
		return this.id;
	}
	
	/**
	 * Gets the java timestamp.
	 * 
	 * @return the java timestamp
	 */
	@Temporal (TemporalType.TIMESTAMP)
	@Column (name = "timestamp")
	private Date getJavaTimestamp() {
		return getTimestamp().toDate();
	}
	
	/**
	 * Gets the person container.
	 * 
	 * @return the personContainer
	 */
	@OneToOne (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public PersonContainer getPersonContainer() {
		return this.personContainer;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.model.TextElement#getText()
	 */
	@Override
	@Transient
	public String getText() {
		return null;
	}
	
	/**
	 * Gets the timestamp.
	 * 
	 * @return the timestamp
	 */
	@Override
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
		result = (prime * result) + getBugId().hashCode();
		result = (prime * result) + ((getTimestamp() == null)
		                                                     ? 0
		                                                     : getTimestamp().hashCode());
		return result;
	}
	
	/**
	 * Checks if is empty.
	 * 
	 * @return true, if is empty
	 */
	@Transient
	public boolean isEmpty() {
		return getChangedDateValues().isEmpty() && getChangedEnumValues().isEmpty()
		        && getChangedPersonValues().isEmpty() && getChangedStringValues().isEmpty();
	}
	
	/**
	 * Sets the author.
	 * 
	 * @param author
	 *            the new author
	 */
	public void setAuthor(final Person author) {
		getPersonContainer().add("author", author);
	}
	
	/**
	 * Sets the bug id.
	 * 
	 * @param bugId
	 *            the bugId to set
	 */
	public void setBugId(final String bugId) {
		this.bugId = bugId;
	}
	
	/**
	 * Sets the changed date values.
	 * 
	 * @param changedDateValues
	 *            the changedDateValues to set
	 */
	private void setChangedDateValues(final Map<String, DateTimeTuple> changedDateValues) {
		this.changedDateValues = changedDateValues;
	}
	
	/**
	 * Sets the changed enum values.
	 * 
	 * @param changedEnumValues
	 *            the changedEnumValues to set
	 */
	private void setChangedEnumValues(final Map<String, EnumTuple> changedEnumValues) {
		this.changedEnumValues = changedEnumValues;
	}
	
	/**
	 * Sets the changed person values.
	 * 
	 * @param changedPersonValues
	 *            the changedPersonValues to set
	 */
	private void setChangedPersonValues(final Map<String, PersonTuple> changedPersonValues) {
		this.changedPersonValues = changedPersonValues;
	}
	
	/**
	 * Sets the changed string values.
	 * 
	 * @param changedStringValues
	 *            the changedStringValues to set
	 */
	private void setChangedStringValues(final Map<String, StringTuple> changedStringValues) {
		this.changedStringValues = changedStringValues;
	}
	
	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the id to set
	 */
	public void setId(final long id) {
		this.id = id;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.model.TextElement#getText()
	 */
	/**
	 * Sets the java timestamp.
	 * 
	 * @param timestamp
	 *            the new java timestamp
	 */
	@SuppressWarnings ("unused")
	private void setJavaTimestamp(final Date timestamp) {
		this.timestamp = new DateTime(timestamp);
	}
	
	/**
	 * Sets the person container.
	 * 
	 * @param personContainer
	 *            the personContainer to set
	 */
	public void setPersonContainer(final PersonContainer personContainer) {
		this.personContainer = personContainer;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.model.TextElement#getText()
	 */
	/**
	 * Sets the timestamp.
	 * 
	 * @param timestamp
	 *            the timestamp to set
	 */
	public void setTimestamp(@NotNull final DateTime timestamp) {
		this.timestamp = timestamp;
	}
	
	/**
	 * Size.
	 * 
	 * @return the int
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
		final StringBuilder builder = new StringBuilder();
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
