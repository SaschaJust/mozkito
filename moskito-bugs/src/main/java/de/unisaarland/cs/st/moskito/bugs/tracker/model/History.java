/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.model;

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

import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNegative;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.CompareCondition;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import de.unisaarland.cs.st.moskito.bugs.tracker.model.comparators.HistoryElementComparator;
import de.unisaarland.cs.st.moskito.persistence.Annotated;
import de.unisaarland.cs.st.moskito.persistence.model.Person;

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
	private long                      bugId;
	
	private SortedSet<HistoryElement> elements         = new TreeSet<HistoryElement>(new HistoryElementComparator());
	
	/**
	 * should be used by persistence util only
	 */
	public History() {
		
	}
	
	/**
	 * @param bugId
	 */
	public History(@NotNegative final long bugId) {
		setBugId(bugId);
	}
	
	/**
	 * @param element
	 */
	@Transient
	public boolean add(@NotNull final HistoryElement element) {
		CompareCondition.equals(getBugId(), element.getBugId(),
		                        "HistoryElements may never be added to the History of a different report: %s -> %s",
		                        element, this);
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
		History history = new History(getBugId());
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
		History history = new History(getBugId());
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
	 * @return
	 */
	@Transient
	public HistoryElement first() {
		return getElements().first();
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
		History history = new History(getBugId());
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
		History history = new History(getBugId());
		Iterator<HistoryElement> iterator = getElements().iterator();
		while (iterator.hasNext()) {
			HistoryElement element = iterator.next();
			if (element.getBugId() == bugId) {
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
		History history = new History(getBugId());
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
	 * @param timestamp
	 * @return
	 */
	public HistoryElement get(final Person author,
	                          final DateTime timestamp) {
		return (HistoryElement) CollectionUtils.find(getElements(), new Predicate() {
			
			@Override
			public boolean evaluate(final Object object) {
				HistoryElement element = (HistoryElement) object;
				
				return element.getTimestamp().equals(timestamp) && element.getAuthor().equals(author);
			}
		});
	}
	
	/**
	 * @param field
	 * @return
	 */
	@Transient
	public History get(final String field) {
		History history = new History(getBugId());
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
	 * @return the bugId
	 */
	public long getBugId() {
		return this.bugId;
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
		
		if ((index >= 0) && (index < list.size())) {
			object = list.get(index).get(fieldName).getFirst();
		} else {
			object = new Report(0).getField(fieldName);
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
	 * @param bugId
	 *            the bugId to set
	 */
	private void setBugId(final long bugId) {
		this.bugId = bugId;
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
	@SuppressWarnings ("unused")
	private void setId(final long id) {
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
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("History [bugId=");
		builder.append(getBugId());
		builder.append(", elements=");
		builder.append(JavaUtils.collectionToString(getElements()));
		builder.append("]");
		return builder.toString();
	}
	
	/**
	 * @param interval
	 * @return
	 */
	public History whithin(final Interval interval) {
		History history = new History(getBugId());
		for (HistoryElement element : getElements()) {
			if (interval.contains(element.getTimestamp())) {
				history.add(element);
			}
		}
		return history;
	}
}