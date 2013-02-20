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
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.mozkito.issues.tracker.model.comparators.HistoryElementComparator;
import org.mozkito.persistence.Annotated;
import org.mozkito.persistence.model.Person;

/**
 * The Class History.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
@Entity
public class History implements Annotated, Iterable<HistoryElement> {
	
	/** The Constant serialVersionUID. */
	private static final long         serialVersionUID = 1720480073428317973L;
	
	/** The bug id. */
	private Report                    report;
	
	/** The elements. */
	private SortedSet<HistoryElement> elements         = new TreeSet<HistoryElement>(new HistoryElementComparator());
	
	/**
	 * should be used by persistence util only.
	 */
	public History() {
		
	}
	
	/**
	 * Instantiates a new history.
	 * 
	 * @param report
	 *            the report
	 */
	History(@NotNull final Report report) {
		setReport(report);
	}
	
	/**
	 * Adds the.
	 * 
	 * @param element
	 *            the element
	 * @return true, if successful
	 */
	@Transient
	protected boolean add(@NotNull final HistoryElement element) {
		boolean ret = false;
		final SortedSet<HistoryElement> elements = getElements();
		ret = elements.add(element);
		setElements(elements);
		return ret;
	}
	
	/**
	 * After.
	 * 
	 * @param dateTime
	 *            the date time
	 * @return the history
	 */
	@Transient
	public History after(@NotNull final DateTime dateTime) {
		final History history = new History(getReport());
		final Iterator<HistoryElement> iterator = getElements().iterator();
		while (iterator.hasNext()) {
			final HistoryElement element = iterator.next();
			if (element.getTimestamp().isAfter(dateTime)) {
				history.add(element);
			}
		}
		return history;
	}
	
	/**
	 * Before.
	 * 
	 * @param dateTime
	 *            the date time
	 * @return the history
	 */
	@Transient
	public History before(@NotNull final DateTime dateTime) {
		final History history = new History(getReport());
		final Iterator<HistoryElement> iterator = getElements().iterator();
		while (iterator.hasNext()) {
			final HistoryElement element = iterator.next();
			if (element.getTimestamp().isBefore(dateTime)) {
				history.add(element);
			}
		}
		return history;
	}
	
	/**
	 * First.
	 * 
	 * @return the history element
	 */
	@Transient
	public HistoryElement first() {
		return getElements().first();
	}
	
	/**
	 * Gets the.
	 * 
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @return the history
	 */
	@Transient
	@NoneNull
	public History get(final DateTime from,
	                   final DateTime to) {
		final History history = new History(getReport());
		final Iterator<HistoryElement> iterator = getElements().iterator();
		while (iterator.hasNext()) {
			final HistoryElement element = iterator.next();
			if ((element.getTimestamp().compareTo(from) >= 0) && (element.getTimestamp().compareTo(to) <= 0)) {
				history.add(element);
			}
		}
		return history;
	}
	
	/**
	 * Gets the.
	 * 
	 * @param author
	 *            the author
	 * @return the history
	 */
	@Transient
	public History get(final Person author) {
		final History history = new History(getReport());
		final Iterator<HistoryElement> iterator = getElements().iterator();
		while (iterator.hasNext()) {
			final HistoryElement element = iterator.next();
			if (element.getAuthor().equals(author)) {
				history.add(element);
			}
		}
		return history;
	}
	
	/**
	 * Gets the.
	 * 
	 * @param author
	 *            the author
	 * @param timestamp
	 *            the timestamp
	 * @return the history element
	 */
	public HistoryElement get(final Person author,
	                          final DateTime timestamp) {
		return (HistoryElement) CollectionUtils.find(getElements(), new Predicate() {
			
			@Override
			public boolean evaluate(final Object object) {
				final HistoryElement element = (HistoryElement) object;
				
				return element.getTimestamp().equals(timestamp) && element.getAuthor().equals(author);
			}
		});
	}
	
	/**
	 * Gets the.
	 * 
	 * @param field
	 *            the field
	 * @return the history
	 */
	@Transient
	public History get(final String field) {
		final History history = new History(getReport());
		final Iterator<HistoryElement> iterator = getElements().iterator();
		while (iterator.hasNext()) {
			final HistoryElement element = iterator.next();
			final HistoryElement value = element.getForField(field);
			if (!value.isEmpty()) {
				history.add(value);
			}
		}
		return history;
	}
	
	/**
	 * Gets the by bug id.
	 * 
	 * @param bugId
	 *            the bug id
	 * @return the by bug id
	 */
	@Transient
	public History getByBugId(final String bugId) {
		final History history = new History(getReport());
		final Iterator<HistoryElement> iterator = getElements().iterator();
		while (iterator.hasNext()) {
			final HistoryElement element = iterator.next();
			if (element.getBugId().equals(bugId)) {
				history.add(element);
			}
		}
		return history;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.Annotated#getHandle()
	 */
	@Override
	public final String getClassName() {
		return JavaUtils.getHandle(History.class);
	}
	
	/**
	 * Gets the elements.
	 * 
	 * @return the elements
	 */
	@OrderBy
	@ManyToMany (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public SortedSet<HistoryElement> getElements() {
		return this.elements;
	}
	
	/**
	 * Gets the old value.
	 * 
	 * @param fieldName
	 *            the field name
	 * @param element
	 *            the element
	 * @return the old value
	 */
	public Object getOldValue(final String fieldName,
	                          final HistoryElement element) {
		final History history = get(fieldName);
		final SortedSet<HistoryElement> elements = history.getElements();
		final ArrayList<HistoryElement> list = new ArrayList<HistoryElement>(elements);
		Object object = null;
		
		final int index = list.indexOf(element);
		
		if ((index >= 0) && (index < list.size())) {
			object = list.get(index).get(fieldName).getFirst();
		} else {
			object = Report.getDefaultField(fieldName);
		}
		
		return object;
	}
	
	/**
	 * Gets the bug id.
	 * 
	 * @return the bugId
	 */
	@Id
	@OneToOne (fetch = FetchType.EAGER, cascade = {})
	public Report getReport() {
		return this.report;
	}
	
	/**
	 * Checks if is empty.
	 * 
	 * @return true, if is empty
	 */
	@Transient
	public boolean isEmpty() {
		return getElements().isEmpty();
	}
	
	/**
	 * Iterator.
	 * 
	 * @return the iterator
	 */
	@Override
	public Iterator<HistoryElement> iterator() {
		return getElements().iterator();
	}
	
	/**
	 * Last.
	 * 
	 * @return the history element
	 */
	@Transient
	public HistoryElement last() {
		return getElements().last();
	}
	
	/**
	 * Removes the.
	 * 
	 * @param element
	 *            the element
	 * @return true, if successful
	 */
	private boolean remove(final HistoryElement element) {
		boolean ret;
		final SortedSet<HistoryElement> set = getElements();
		ret = set.remove(element);
		setElements(set);
		return ret;
	}
	
	/**
	 * Rollback.
	 * 
	 * @param report
	 *            the report
	 * @param timestamp
	 *            the timestamp
	 * @return the report
	 */
	@SuppressWarnings ("deprecation")
	public Report rollback(@NotNull final Report report,
	                       @NotNull final DateTime timestamp) {
		if (report.getCreationTimestamp().isBefore(timestamp)) {
			try {
				final History history = after(timestamp);
				final Report newReport = report.clone();
				final LinkedList<HistoryElement> list = new LinkedList<HistoryElement>(history.getElements());
				final ListIterator<HistoryElement> iterator = list.listIterator(list.size());
				
				while (iterator.hasPrevious()) {
					final HistoryElement element = iterator.previous();
					final Set<String> fields = element.getFields();
					for (final String fieldName : fields) {
						newReport.setField(fieldName, getOldValue(fieldName, element));
					}
					newReport.getHistory().remove(element);
				}
				
				final SortedSet<Comment> comments = newReport.getComments();
				final SortedSet<Comment> newComments = new TreeSet<Comment>();
				
				for (final Comment comment : comments) {
					if (!comment.getTimestamp().isAfter(timestamp)) {
						newComments.add(comment);
					}
				}
				
				newReport.setComments(newComments);
				
				return newReport;
			} catch (final CloneNotSupportedException ignore) {
				// ignore
			}
		}
		
		return null;
	}
	
	/**
	 * Sets the elements.
	 * 
	 * @param elements
	 *            the elements to set
	 */
	private void setElements(final SortedSet<HistoryElement> elements) {
		this.elements = elements;
	}
	
	/**
	 * Sets the bug id.
	 * 
	 * @param report
	 *            the new report
	 */
	private void setReport(final Report report) {
		this.report = report;
	}
	
	/**
	 * Size.
	 * 
	 * @return the int
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
		final StringBuilder builder = new StringBuilder();
		builder.append("History [bugId=");
		builder.append(getReport());
		builder.append(", elements=");
		builder.append(JavaUtils.collectionToString(getElements()));
		builder.append("]");
		return builder.toString();
	}
	
	/**
	 * Whithin.
	 * 
	 * @param interval
	 *            the interval
	 * @return the history
	 */
	public History whithin(final Interval interval) {
		final History history = new History(getReport());
		for (final HistoryElement element : getElements()) {
			if (interval.contains(element.getTimestamp())) {
				history.add(element);
			}
		}
		return history;
	}
}
