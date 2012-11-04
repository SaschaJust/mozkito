/*******************************************************************************
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
 ******************************************************************************/
package org.mozkito.mappings.mappable.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kisa.Logger;

import org.jsoup.Jsoup;
import org.mozkito.issues.tracker.model.Comment;
import org.mozkito.issues.tracker.model.HistoryElement;
import org.mozkito.issues.tracker.model.Report;
import org.mozkito.mappings.mappable.FieldKey;
import org.mozkito.persistence.Annotated;
import org.mozkito.persistence.model.Person;

/**
 * Class that wraps {@link Report} to be mapped.
 * 
 * @see MappableEntity
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
@Entity
@Access (AccessType.PROPERTY)
@DiscriminatorValue ("MAPPABLEREPORT")
public class MappableReport extends MappableEntity implements Annotated {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1097712059403322470L;
	
	/** The report. */
	private Report            report;
	
	/**
	 * Instantiates a new mappable report.
	 * 
	 * @deprecated used only by persistence utility
	 */
	@Deprecated
	public MappableReport() {
		super();
	}
	
	/**
	 * Instantiates a new mappable report.
	 * 
	 * @param report
	 *            the report
	 */
	public MappableReport(final Report report) {
		super();
		setReport(report);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.mappable.MappableEntity#get(de .unisaarland.cs.st.reposuite.mapping.mappable.FieldKey)
	 */
	@Override
	@Transient
	public Object get(final FieldKey key) {
		switch (key) {
			case AUTHOR:
				return getReport().getSubmitter();
			case BODY:
				return getReport().getDescription();
			case CHANGER:
				final Set<Person> persons = new HashSet<Person>();
				
				final Iterator<HistoryElement> iterator = getReport().getHistory().getElements().iterator();
				
				while (iterator.hasNext()) {
					persons.add(iterator.next().getAuthor());
				}
				
				return persons;
			case COMMENT:
				return getReport().getComments();
			case TYPE:
				return getReport().getType();
			case CLOSED_TIMESTAMP:
				// TODO this might be wrong
				return getReport().getResolutionTimestamp();
			case CLOSER:
				return getReport().getResolver();
			case CREATION_TIMESTAMP:
				return getReport().getCreationTimestamp();
			case ID:
				return getReport().getId();
			case MODIFICATION_TIMESTAMP:
				return getReport().getLastUpdateTimestamp();
			case RESOLUTION_TIMESTAMP:
				return getReport().getResolutionTimestamp();
			case SUMMARY:
				return getReport().getSummary();
			default:
				break;
		}
		
		throw new UnrecoverableError(getClass().getSimpleName() + " does not support field key: " + key.name());
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.mappable.MappableEntity#get(de .unisaarland.cs.st.reposuite.mapping.mappable.FieldKey,
	 * int)
	 */
	@Override
	@Transient
	public Object get(final FieldKey key,
	                  final int index) {
		switch (key) {
			case COMMENT:
				if (getReport().getComments().size() > index) {
					
					final Iterator<Comment> iterator = getReport().getComments().iterator();
					Comment comment = null;
					
					for (int i = 0; i <= index; ++i) {
						comment = iterator.next();
					}
					
					return comment;
				} else {
					return null;
				}
			case CHANGER:
				final Set<Person> persons = new HashSet<Person>();
				
				final Iterator<HistoryElement> iterator = getReport().getHistory().getElements().iterator();
				
				while (iterator.hasNext()) {
					persons.add(iterator.next().getAuthor());
				}
				
				final Iterator<Person> iterator2 = persons.iterator();
				Person person = null;
				
				for (int i = 0; i <= index; ++i) {
					person = iterator2.next();
				}
				
				return person;
			default:
				if (Logger.logWarn()) {
					Logger.warn("Field " + key.name() + " is not indexable on " + getHandle() + ".");
				}
				return get(key);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.mappable.MappableEntity#getBaseType ()
	 */
	@Override
	@Transient
	public Class<?> getBaseType() {
		return Report.class;
	}
	
	/**
	 * Gets the body.
	 * 
	 * @return the body
	 */
	@Transient
	public String getBody() {
		return getReport().getDescription();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.mappable.model.MappableEntity#getId()
	 */
	@Override
	public String getId() {
		// TODO: access mapping.selectors.reportRegex.tag to strip everything but the id number
		return getReport().getId().replaceAll("[^0-9]", "");
	}
	
	/**
	 * Gets the report.
	 * 
	 * @return the report
	 */
	@OneToOne (fetch = FetchType.LAZY)
	public Report getReport() {
		return this.report;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.mappable.MappableEntity#getText()
	 */
	@Override
	@Transient
	public String getText() {
		final StringBuilder builder = new StringBuilder();
		
		builder.append(getReport().getSubject()).append(FileUtils.lineSeparator);
		builder.append(getReport().getSummary()).append(FileUtils.lineSeparator);
		builder.append(getReport().getDescription()).append(FileUtils.lineSeparator);
		
		for (final Comment comment : getReport().getComments()) {
			builder.append(comment.getMessage()).append(FileUtils.lineSeparator);;
		}
		
		return Jsoup.parse(builder.toString()).text();
	}
	
	/**
	 * Sets the report.
	 * 
	 * @param report
	 *            the new report
	 */
	public void setReport(final Report report) {
		this.report = report;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.mappable.MappableEntity#supported ()
	 */
	@SuppressWarnings ("serial")
	@Override
	@Transient
	public Set<FieldKey> supported() {
		// TODO complete this
		return new HashSet<FieldKey>() {
			
			{
				add(FieldKey.ID);
				add(FieldKey.AUTHOR);
				add(FieldKey.BODY);
				add(FieldKey.CHANGER);
				add(FieldKey.CLOSED_TIMESTAMP);
				add(FieldKey.RESOLUTION_TIMESTAMP);
			}
		};
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("MappableReport [report=");
		builder.append(this.report);
		builder.append("]");
		return builder.toString();
	}
}