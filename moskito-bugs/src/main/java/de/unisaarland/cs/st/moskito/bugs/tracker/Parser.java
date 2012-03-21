/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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
 *******************************************************************************/
package de.unisaarland.cs.st.moskito.bugs.tracker;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Priority;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Resolution;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Severity;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Status;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Type;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.AttachmentEntry;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.Comment;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.HistoryElement;
import de.unisaarland.cs.st.moskito.persistence.model.Person;

/**
 * The Interface Parser.
 *
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public interface Parser {
	
	/**
	 * Gets the assigned to.
	 *
	 * @return the assigned to
	 */
	Person getAssignedTo();
	
	/**
	 * Gets the attachment entries.
	 *
	 * @return the attachment entries
	 */
	List<AttachmentEntry> getAttachmentEntries();
	
	/**
	 * Gets the category.
	 *
	 * @return the category
	 */
	String getCategory();
	
	/**
	 * Gets the comments.
	 *
	 * @return the comments
	 */
	SortedSet<Comment> getComments();
	
	/**
	 * Gets the component.
	 *
	 * @return the component
	 */
	String getComponent();
	
	/**
	 * Gets the creation timestamp.
	 *
	 * @return the creation timestamp
	 */
	DateTime getCreationTimestamp();
	
	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	String getDescription();
	
	/**
	 * Gets the fetch time.
	 *
	 * @return the fetch time
	 */
	DateTime getFetchTime();
	
	/**
	 * Gets the history elements.
	 *
	 * @return the history elements
	 */
	SortedSet<HistoryElement> getHistoryElements();
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	String getId();
	
	/**
	 * Gets the keywords.
	 *
	 * @return the keywords
	 */
	Set<String> getKeywords();
	
	/**
	 * Gets the last update timestamp.
	 *
	 * @return the last update timestamp
	 */
	DateTime getLastUpdateTimestamp();
	
	/**
	 * Gets the priority.
	 *
	 * @return the priority
	 */
	Priority getPriority();
	
	/**
	 * Gets the product.
	 *
	 * @return the product
	 */
	String getProduct();
	
	/**
	 * Gets the resolution.
	 *
	 * @return the resolution
	 */
	Resolution getResolution();
	
	/**
	 * Gets the resolution timestamp.
	 *
	 * @return the resolution timestamp
	 */
	DateTime getResolutionTimestamp();
	
	/**
	 * Gets the resolver.
	 *
	 * @return the resolver
	 */
	Person getResolver();
	
	/**
	 * Gets the scm fix version.
	 *
	 * @return the scm fix version
	 */
	String getScmFixVersion();
	
	/**
	 * Gets the severity.
	 *
	 * @return the severity
	 */
	Severity getSeverity();
	
	/**
	 * Gets the siblings.
	 *
	 * @return the siblings
	 */
	Set<String> getSiblings();
	
	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	Status getStatus();
	
	/**
	 * Gets the subject.
	 *
	 * @return the subject
	 */
	String getSubject();
	
	/**
	 * Gets the submitter.
	 *
	 * @return the submitter
	 */
	Person getSubmitter();
	
	/**
	 * Gets the summary.
	 *
	 * @return the summary
	 */
	String getSummary();
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	Type getType();
	
	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	String getVersion();
	
	/**
	 * Sets the tracker.
	 *
	 * @param tracker the new tracker
	 */
	void setTracker(Tracker tracker);
	
	/**
	 * Sets the uri.
	 *
	 * @param reportLink the report link
	 * @return true, if successful
	 */
	boolean setURI(ReportLink reportLink);
}
