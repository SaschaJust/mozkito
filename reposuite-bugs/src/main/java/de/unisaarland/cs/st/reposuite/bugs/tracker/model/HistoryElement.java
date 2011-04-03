/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.model;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
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
import de.unisaarland.cs.st.reposuite.utils.Tuple;

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
	private static final long          serialVersionUID    = -8882135636304256696L;
	
	private long                       id;
	
	private Map<String, StringTuple>   changedStringValues = new HashMap<String, StringTuple>();
	private Map<String, EnumTuple>     changedEnumValues   = new HashMap<String, EnumTuple>();
	private Map<String, DateTimeTuple> changedDateValues   = new HashMap<String, DateTimeTuple>();
	private Map<String, PersonTuple>   changedPersonValues = new HashMap<String, PersonTuple>();
	
	private DateTime                   timestamp;
	private Report                     bugReport;
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
	public HistoryElement(final Person author, final DateTime timestamp, final Map<String, Tuple<?, ?>> values) {
		setAuthor(author);
		setTimestamp(timestamp);
		
		addChange(values);
	}
	
	/**
	 * @param values
	 */
	public void addChange(@NotNull final Map<String, Tuple<?, ?>> values) {
		for (String fieldName : values.keySet()) {
			String lowerFieldName = fieldName.toLowerCase();
			if (values.get(lowerFieldName).getFirst() instanceof String) {
				getChangedStringValues().put(lowerFieldName,
				                             new StringTuple((String) values.get(lowerFieldName).getFirst(),
				                                             (String) values.get(lowerFieldName).getSecond()));
			} else if (values.get(lowerFieldName).getFirst() instanceof Person) {
				getChangedPersonValues().put(lowerFieldName,
				                             new PersonTuple((Person) values.get(lowerFieldName).getFirst(),
				                                             (Person) values.get(lowerFieldName).getSecond()));
			} else if (values.get(lowerFieldName).getFirst() instanceof Enum) {
				getChangedEnumValues().put(lowerFieldName,
				                           new EnumTuple((Enum<?>) values.get(lowerFieldName).getFirst(),
				                                         (Enum<?>) values.get(lowerFieldName).getSecond()));
			} else if (values.get(lowerFieldName).getFirst() instanceof DateTime) {
				getChangedDateValues().put(lowerFieldName,
				                           new DateTimeTuple((DateTime) values.get(lowerFieldName).getFirst(),
				                                             (DateTime) values.get(lowerFieldName).getSecond()));
			} else if (values.get(lowerFieldName).getFirst() instanceof Date) {
				getChangedDateValues().put(lowerFieldName,
				                           new DateTimeTuple((Date) values.get(lowerFieldName).getFirst(),
				                                             (Date) values.get(lowerFieldName).getSecond()));
			} else {
				throw new UnrecoverableError(values.get(lowerFieldName).getFirst().getClass().getCanonicalName()
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
	@NoneNull
	public void addChangedValue(@MinSize (min = 2) final String field,
	                            final DateTime oldValue,
	                            final DateTime newValue) {
		getChangedDateValues().put(field.toLowerCase(), new DateTimeTuple(oldValue, newValue));
	}
	
	/**
	 * @param field
	 * @param oldValue
	 * @param newValue
	 */
	@Transient
	@NoneNull
	public void addChangedValue(@MinSize (min = 2) final String field,
	                            final Enum<?> oldValue,
	                            final Enum<?> newValue) {
		getChangedEnumValues().put(field.toLowerCase(), new EnumTuple(oldValue, newValue));
	}
	
	/**
	 * @param field
	 * @param oldValue
	 * @param newValue
	 */
	public void addChangedValue(final String field,
	                            final Person oldValue,
	                            final Person newValue) {
		getChangedPersonValues().put(field, new PersonTuple(oldValue, newValue));
	}
	
	/**
	 * @param field
	 * @param oldValue
	 * @param newValue
	 */
	@Transient
	@NoneNull
	public void addChangedValue(@MinSize (min = 2) final String field,
	                            final PersonContainer oldValue,
	                            final PersonContainer newValue) {
		getChangedPersonValues().put(field, new PersonTuple(oldValue, newValue));
	}
	
	/**
	 * @param field
	 * @param oldValue
	 * @param newValue
	 */
	@Transient
	@NoneNull
	public void addChangedValue(@MinSize (min = 2) final String field,
	                            final String oldValue,
	                            final String newValue) {
		getChangedStringValues().put(field, new StringTuple(oldValue, newValue));
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
	private Map<String, DateTimeTuple> getChangedDateValues() {
		return this.changedDateValues;
	}
	
	/**
	 * @return the changedEnumValues
	 */
	// @ManyToOne (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	// @ManyToMany (cascade = CascadeType.ALL)
	@ElementCollection
	private Map<String, EnumTuple> getChangedEnumValues() {
		return this.changedEnumValues;
	}
	
	/**
	 * @return the changedPersonValues
	 */
	// @ManyToMany (cascade = CascadeType.ALL)
	@ElementCollection
	public Map<String, PersonTuple> getChangedPersonValues() {
		return this.changedPersonValues;
	}
	
	/**
	 * @return the changedStringValues
	 */
	// @ManyToOne (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	// @ManyToMany (cascade = CascadeType.ALL)
	@ElementCollection
	private Map<String, StringTuple> getChangedStringValues() {
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
		if (getChangedStringValues().containsKey(lowerFieldName)) {
			element.getChangedStringValues().put(lowerFieldName, getChangedStringValues().get(lowerFieldName));
		} else if (getChangedPersonValues().containsKey(lowerFieldName)) {
			element.addChangedValue(lowerFieldName, getChangedPersonValues().get(lowerFieldName).getOldValue(),
			                        getChangedPersonValues().get(lowerFieldName).getNewValue());
		} else if (getChangedEnumValues().containsKey(lowerFieldName)) {
			element.getChangedEnumValues().put(lowerFieldName, getChangedEnumValues().get(lowerFieldName));
		} else if (getChangedDateValues().containsKey(lowerFieldName)) {
			element.getChangedDateValues().put(lowerFieldName, getChangedDateValues().get(lowerFieldName));
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
		return getTimestamp().toDate();
	}
	
	/**
	 * @param fieldName
	 * @return
	 */
	@Transient
	public Object getNewValue(final String fieldName) {
		String lowerFieldName = fieldName.toLowerCase();
		if (getChangedStringValues().containsKey(lowerFieldName)) {
			return getChangedStringValues().get(lowerFieldName).getNewValue();
		} else if (getChangedPersonValues().containsKey(lowerFieldName)) {
			return getChangedPersonValues().get(lowerFieldName).getNewValue();
		} else if (getChangedEnumValues().containsKey(lowerFieldName)) {
			return getChangedEnumValues().get(lowerFieldName).getNewValue();
		} else if (getChangedDateValues().containsKey(lowerFieldName)) {
			return getChangedDateValues().get(lowerFieldName).getNewValue();
		} else {
			return null;
		}
	}
	
	/**
	 * @param fieldName
	 * @return
	 */
	@Transient
	public Object getOldValue(final String fieldName) {
		String lowerFieldName = fieldName.toLowerCase();
		if (getChangedStringValues().containsKey(lowerFieldName)) {
			return getChangedStringValues().get(lowerFieldName).getOldValue();
		} else if (getChangedPersonValues().containsKey(lowerFieldName)) {
			return getChangedPersonValues().get(lowerFieldName).getOldValue();
		} else if (getChangedEnumValues().containsKey(lowerFieldName)) {
			return getChangedEnumValues().get(lowerFieldName).getOldValue();
		} else if (getChangedDateValues().containsKey(lowerFieldName)) {
			return getChangedDateValues().get(lowerFieldName).getOldValue();
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
	 * @return
	 */
	@Transient
	public Collection<PersonContainer> getPersonContainers() {
		LinkedList<PersonContainer> list = new LinkedList<PersonContainer>();
		list.add(getPersonContainer());
		for (String key : getChangedPersonValues().keySet()) {
			list.add(getChangedPersonValues().get(key).getOldValue());
			list.add(getChangedPersonValues().get(key).getNewValue());
		}
		return list;
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
	@SuppressWarnings ("unused")
	private void setChangedDateValues(final Map<String, DateTimeTuple> changedDateValues) {
		this.changedDateValues = changedDateValues;
	}
	
	/**
	 * @param changedEnumValues
	 *            the changedEnumValues to set
	 */
	@SuppressWarnings ("unused")
	private void setChangedEnumValues(final Map<String, EnumTuple> changedEnumValues) {
		this.changedEnumValues = changedEnumValues;
	}
	
	/**
	 * @param changedPersonValues
	 *            the changedPersonValues to set
	 */
	public void setChangedPersonValues(final Map<String, PersonTuple> changedPersonValues) {
		this.changedPersonValues = changedPersonValues;
	}
	
	/**
	 * @param changedStringValues
	 *            the changedStringValues to set
	 */
	@SuppressWarnings ("unused")
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
		return getChangedDateValues().size() + getChangedEnumValues().size() + getChangedPersonValues().size()
		        + getChangedStringValues().size();
	}
	
}
