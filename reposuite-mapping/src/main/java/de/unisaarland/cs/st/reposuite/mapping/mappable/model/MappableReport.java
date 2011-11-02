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
package de.unisaarland.cs.st.reposuite.mapping.mappable.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Comment;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.HistoryElement;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.mapping.mappable.FieldKey;
import de.unisaarland.cs.st.reposuite.persistence.model.Person;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class MappableReport extends MappableEntity {
	
	/**
     * 
     */
	private static final long serialVersionUID = 1097712059403322470L;
	private Report            report;
	
	/**
	 * @param report
	 */
	public MappableReport(final Report report) {
		this.setReport(report);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity#get(de
	 * .unisaarland.cs.st.reposuite.mapping.mappable.FieldKey)
	 */
	@Override
	public Object get(final FieldKey key) {
		switch (key) {
			case AUTHOR:
				return getReport().getSubmitter();
			case BODY:
				return getReport().getDescription();
			case CHANGER:
				Set<Person> persons = new HashSet<Person>();
				
				Iterator<HistoryElement> iterator = getReport().getHistory().getElements().iterator();
				
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
		
		throw new UnrecoverableError(this.getClass().getSimpleName() + " does not support field key: " + key.name());
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity#get(de
	 * .unisaarland.cs.st.reposuite.mapping.mappable.FieldKey, int)
	 */
	@Override
	public Object get(final FieldKey key,
	                  final int index) {
		switch (key) {
			case COMMENT:
				if (getReport().getComments().size() > index) {
					
					Iterator<Comment> iterator = getReport().getComments().iterator();
					Comment comment = null;
					
					for (int i = 0; i <= index; ++i) {
						comment = iterator.next();
					}
					
					return comment;
				} else {
					return null;
				}
			case CHANGER:
				Set<Person> persons = new HashSet<Person>();
				
				Iterator<HistoryElement> iterator = getReport().getHistory().getElements().iterator();
				
				while (iterator.hasNext()) {
					persons.add(iterator.next().getAuthor());
				}
				
				Iterator<Person> iterator2 = persons.iterator();
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
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity#getBaseType
	 * ()
	 */
	@Override
	public Class<?> getBaseType() {
		return Report.class;
	}
	
	/**
	 * @return
	 */
	public String getBody() {
		return this.report.getDescription();
	}
	
	/**
	 * @return
	 */
	public Report getReport() {
		return this.report;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity#getText()
	 */
	@Override
	public String getText() {
		StringBuilder builder = new StringBuilder();
		
		builder.append(getReport().getSubject()).append(FileUtils.lineSeparator);
		builder.append(getReport().getSummary()).append(FileUtils.lineSeparator);
		builder.append(getReport().getDescription()).append(FileUtils.lineSeparator);
		
		for (Comment comment : getReport().getComments()) {
			builder.append(comment.getMessage()).append(FileUtils.lineSeparator);;
		}
		
		return builder.toString();
	}
	
	/**
	 * @param report
	 */
	public void setReport(final Report report) {
		this.report = report;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity#supported
	 * ()
	 */
	@SuppressWarnings ("serial")
	@Override
	public Set<FieldKey> supported() {
		// TODO complete this
		return new HashSet<FieldKey>() {
			
			{
				add(FieldKey.ID);
				add(FieldKey.AUTHOR);
				add(FieldKey.BODY);
				add(FieldKey.CHANGER);
				add(FieldKey.CLOSED_TIMESTAMP);
			}
		};
	}
}
