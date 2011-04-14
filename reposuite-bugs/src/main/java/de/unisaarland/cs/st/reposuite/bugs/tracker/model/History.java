/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;

import org.joda.time.DateTime;
import org.joda.time.Interval;

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
	
	/**
	 * 
	 */
	private static final long         serialVersionUID = 1720480073428317973L;
	private long                      id;
	private SortedSet<HistoryElement> elements         = new TreeSet<HistoryElement>(new HistoryElementComparator());
	
	/**
	 * @param element
	 */
	@Transient
	public boolean add(@NotNull final HistoryElement element) {
		boolean ret = false;
		SortedSet<HistoryElement> elements = getElements();
		ret = elements.add(element);
		setElements(elements);
		return ret;
	}
	
	/**
	 * @param dateTime
	 * @return
	 */
	@Transient
	public History after(@NotNull final DateTime dateTime) {
		History history = new History();
		Iterator<HistoryElement> iterator = getElements().iterator();
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
	public History before(@NotNull final DateTime dateTime) {
		History history = new History();
		Iterator<HistoryElement> iterator = getElements().iterator();
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
	@NoneNull
	public History get(final DateTime from,
	                   final DateTime to) {
		History history = new History();
		Iterator<HistoryElement> iterator = getElements().iterator();
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
		Iterator<HistoryElement> iterator = getElements().iterator();
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
		Iterator<HistoryElement> iterator = getElements().iterator();
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
		Iterator<HistoryElement> iterator = getElements().iterator();
		while (iterator.hasNext()) {
			HistoryElement element = iterator.next();
			HistoryElement value = element.getForField(field);
			if (!value.isEmpty()) {
				history.add(value);
			}
		}
		return history;
	}
	
	/**
	 * @return the elements
	 */
	@OrderBy
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
	 * @param fieldName
	 * @param element
	 * @return
	 */
	public Object getOldValue(final String fieldName,
	                          final HistoryElement element) {
		History history = get(fieldName);
		SortedSet<HistoryElement> elements = history.getElements();
		ArrayList<HistoryElement> list = new ArrayList<HistoryElement>(elements);
		Object object = null;
		
		int index = list.indexOf(element);
		
		if ((index > 0) && (index < list.size())) {
			object = list.get(index);
		} else {
			object = new Report().getField(fieldName);
		}
		
		return object;
	}
	
	/**
	 * @return
	 */
	@Transient
	public boolean isEmpty() {
		return getElements().isEmpty();
	}
	
	/**
	 * @return
	 */
	public Iterator<HistoryElement> iterator() {
		return getElements().iterator();
	}
	
	/**
	 * @return
	 */
	@Transient
	public HistoryElement last() {
		return getElements().last();
	}
	
	/**
	 * @param element
	 */
	private boolean remove(final HistoryElement element) {
		boolean ret;
		SortedSet<HistoryElement> set = getElements();
		ret = set.remove(element);
		setElements(set);
		return ret;
	}
	
	/**
	 * @param report
	 * @return
	 */
	public Report rollback(@NotNull final Report report,
	                       @NotNull final DateTime timestamp) {
		if (report.getCreationTimestamp().isBefore(timestamp)) {
			try {
				History history = after(timestamp);
				Report newReport = report.clone();
				LinkedList<HistoryElement> list = new LinkedList<HistoryElement>(history.getElements());
				ListIterator<HistoryElement> iterator = list.listIterator(list.size());
				
				while (iterator.hasPrevious()) {
					HistoryElement element = iterator.previous();
					Set<String> fields = element.getFields();
					for (String fieldName : fields) {
						newReport.setField(fieldName, getOldValue(fieldName, element));
					}
					newReport.getHistory().remove(element);
				}
				
				SortedSet<Comment> comments = newReport.getComments();
				SortedSet<Comment> newComments = new TreeSet<Comment>();
				
				for (Comment comment : comments) {
					if (!comment.getTimestamp().isAfter(timestamp)) {
						newComments.add(comment);
					}
				}
				
				newReport.setComments(newComments);
				
				return newReport;
			} catch (CloneNotSupportedException e) {
			}
		}
		
		return null;
	}
	
	/**
	 * @param elements
	 *            the elements to set
	 */
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
	@Transient
	public int size() {
		return getElements().size();
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
	
	/**
	 * @param interval
	 * @return
	 */
	public History whithin(final Interval interval) {
		History history = new History();
		for (HistoryElement element : getElements()) {
			if (interval.contains(element.getTimestamp())) {
				history.add(element);
			}
		}
		return history;
	}
}
