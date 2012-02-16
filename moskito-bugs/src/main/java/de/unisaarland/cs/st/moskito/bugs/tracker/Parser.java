/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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
package de.unisaarland.cs.st.moskito.bugs.tracker;

import java.util.Set;

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
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public interface Parser {
	
	Person getAssignedTo();
	
	AttachmentEntry getAttachment(int index);
	
	String getCategory();
	
	Comment getComment(int index);
	
	Set<Comment> getComments();
	
	String getComponent();
	
	DateTime getCreationTimestamp();
	
	String getDescription();
	
	HistoryElement getHistoryElement(int index);
	
	int getHistoryLength();
	
	Long getId();
	
	int getNumberOfAttachments();
	
	int getNumberOfComments();
	
	Priority getPriority();
	
	String getProduct();
	
	Resolution getResolution();
	
	DateTime getResolutionTimestamp();
	
	Person getResolver();
	
	Severity getSeverity();
	
	Set<Long> getSiblings();
	
	Status getStatus();
	
	String getSubject();
	
	Person getSubmitter();
	
	String getSummary();
	
	Type getType();
	
	String getVersion();
	
	void setTracker(Tracker tracker);
	
	void setXMLReport(XmlReport report);
}