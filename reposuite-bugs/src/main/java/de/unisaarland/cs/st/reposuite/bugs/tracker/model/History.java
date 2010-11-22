/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.comparators.HistoryElementComparator;
import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.rcs.model.Person;
import de.unisaarland.cs.st.reposuite.utils.JavaUtils;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Entity
public class History implements Annotated {
	
	private long                      id;
	private SortedSet<HistoryElement> elements = new TreeSet<HistoryElement>();
	
	/**
	 * @param element
	 */
	@Transient
	public void add(final HistoryElement element) {
		this.elements.add(element);
	}
	
	/**
	 * @param dateTime
	 * @return
	 */
	@Transient
	public History after(final DateTime dateTime) {
		History history = new History();
		Iterator<HistoryElement> iterator = this.elements.iterator();
		while (iterator.hasNext()) {
			HistoryElement element = iterator.next();
			if (element.getTimestamp().isAfter(dateTime)) {
				history.add(element);
			}
		}
		return history;
	}
	
	/**
	 * @param dateTime
	 * @return
	 */
	@Transient
	public History before(final DateTime dateTime) {
		History history = new History();
		Iterator<HistoryElement> iterator = this.elements.iterator();
		while (iterator.hasNext()) {
			HistoryElement element = iterator.next();
			if (element.getTimestamp().isBefore(dateTime)) {
				history.add(element);
			}
		}
		return history;
	}
	
	/**
	 * @param from
	 * @param to
	 * @return
	 */
	@Transient
	public History get(final DateTime from, final DateTime to) {
		History history = new History();
		Iterator<HistoryElement> iterator = this.elements.iterator();
		while (iterator.hasNext()) {
			HistoryElement element = iterator.next();
			if ((element.getTimestamp().compareTo(from) >= 0) && (element.getTimestamp().compareTo(to) <= 0)) {
				history.add(element);
			}
		}
		return history;
	}
	
	/**
	 * @param bugId
	 * @return
	 */
	@Transient
	public History get(final long bugId) {
		History history = new History();
		Iterator<HistoryElement> iterator = this.elements.iterator();
		while (iterator.hasNext()) {
			HistoryElement element = iterator.next();
			if (element.getBugReport().getId() == bugId) {
				history.add(element);
			}
		}
		return history;
	}
	
	/**
	 * @param author
	 * @return
	 */
	@Transient
	public History get(final Person author) {
		History history = new History();
		Iterator<HistoryElement> iterator = this.elements.iterator();
		while (iterator.hasNext()) {
			HistoryElement element = iterator.next();
			if (element.getAuthor().equals(author)) {
				history.add(element);
			}
		}
		return history;
	}
	
	/**
	 * @param field
	 * @return
	 */
	@Transient
	public History get(final String field) {
		History history = new History();
		Iterator<HistoryElement> iterator = this.elements.iterator();
		while (iterator.hasNext()) {
			HistoryElement element = iterator.next();
			history.add(element.getForField(field));
		}
		return history;
	}
	
	/**
	 * @return the elements
	 */
	@Sort (type = SortType.COMPARATOR, comparator = HistoryElementComparator.class)
	@ManyToMany (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public SortedSet<HistoryElement> getElements() {
		return this.elements;
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
	@Transient
	public boolean isEmpty() {
		return this.elements.isEmpty();
	}
	
	/**
	 * @return
	 */
	public Iterator<HistoryElement> iterator() {
		return this.elements.iterator();
	}
	
	/**
	 * @return
	 */
	@Transient
	public HistoryElement last() {
		return this.elements.last();
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
	 * @param elements
	 *            the elements to set
	 */
	@SuppressWarnings ("unused")
	private void setElements(final SortedSet<HistoryElement> elements) {
		this.elements = elements;
	}
	
	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(final long id) {
		this.id = id;
	}
	
	/**
	 * @return
	 */
	public int size() {
		return this.elements.size();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	@Transient
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("History [elements=");
		builder.append(JavaUtils.collectionToString(getElements()));
		builder.append("]");
		return builder.toString();
	}
}
