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
package de.unisaarland.cs.st.moskito.bugs.tracker.jira;

import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.ownhero.dev.ioda.DateTimeUtils;
import net.ownhero.dev.kisa.Logger;

import org.joda.time.DateTime;

import com.atlassian.jira.rest.client.IssueRestClient;
import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.NullProgressMonitor;
import com.atlassian.jira.rest.client.RestClientException;
import com.atlassian.jira.rest.client.domain.Attachment;
import com.atlassian.jira.rest.client.domain.BasicComponent;
import com.atlassian.jira.rest.client.domain.BasicResolution;
import com.atlassian.jira.rest.client.domain.BasicUser;
import com.atlassian.jira.rest.client.domain.Field;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.IssueLink;

import de.unisaarland.cs.st.moskito.bugs.tracker.Parser;
import de.unisaarland.cs.st.moskito.bugs.tracker.ReportLink;
import de.unisaarland.cs.st.moskito.bugs.tracker.Tracker;
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
public class JiraParser implements Parser {
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getAssignedTo()
	 */
	
	private JiraRestClient restClient;
	private Issue          issue;
	private DateTime       fetchTime;
	
	public JiraParser(final JiraRestClient restClient) {
		// PRECONDITIONS
		
		try {
			this.restClient = restClient;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	@Override
	public Person getAssignedTo() {
		// PRECONDITIONS
		
		try {
			//
			final BasicUser assignee = this.issue.getAssignee();
			return new Person(assignee.getDisplayName(), assignee.getName(), null);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getAttachmentEntries()
	 */
	@Override
	public List<AttachmentEntry> getAttachmentEntries() {
		// PRECONDITIONS
		
		final List<AttachmentEntry> result = new LinkedList<AttachmentEntry>();
		
		try {
			int counter = 0;
			for (final Attachment attachment : this.issue.getAttachments()) {
				final AttachmentEntry aEntry = new AttachmentEntry(String.valueOf(counter++));
				final BasicUser author = attachment.getAuthor();
				aEntry.setAuthor(new Person(author.getDisplayName(), author.getName(), null));
				aEntry.setFilename(attachment.getFilename());
				aEntry.setTimestamp(attachment.getCreationDate());
				try {
					aEntry.setLink(attachment.getSelf().toURL());
				} catch (final MalformedURLException e) {
					if (Logger.logError()) {
						Logger.error(e.getMessage(), e);
					}
				}
				aEntry.setMime(attachment.getMimeType());
				aEntry.setSize(attachment.getSize());
				result.add(aEntry);
			}
			return result;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getCategory()
	 */
	@Override
	public String getCategory() {
		// PRECONDITIONS
		
		try {
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getComments()
	 */
	@Override
	public SortedSet<Comment> getComments() {
		// PRECONDITIONS
		
		final SortedSet<Comment> result = new TreeSet<Comment>();
		try {
			int counter = 0;
			for (final com.atlassian.jira.rest.client.domain.Comment comment : this.issue.getComments()) {
				final BasicUser jiraAuthor = comment.getAuthor();
				final Person author = new Person(jiraAuthor.getDisplayName(), jiraAuthor.getName(), null);
				result.add(new Comment(counter++, author, comment.getCreationDate(), comment.getBody()));
			}
			return result;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getComponent()
	 */
	
	@Override
	public String getComponent() {
		// PRECONDITIONS
		
		try {
			final StringBuilder components = new StringBuilder();
			final Iterator<BasicComponent> componentIter = this.issue.getComponents().iterator();
			while (componentIter.hasNext()) {
				components.append(componentIter.next().getName());
				if (componentIter.hasNext()) {
					components.append(",");
				}
			}
			if (components.length() > 0) {
				return components.toString();
			} else {
				return null;
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getCreationTimestamp()
	 */
	
	@Override
	public DateTime getCreationTimestamp() {
		// PRECONDITIONS
		
		try {
			return this.issue.getCreationDate();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getDescription()
	 */
	@Override
	public String getDescription() {
		// PRECONDITIONS
		
		try {
			return this.issue.getSummary();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getHistoryElement(int)
	 */
	@Override
	public DateTime getFetchTime() {
		// PRECONDITIONS
		
		try {
			return this.fetchTime;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getId()
	 */
	@Override
	public SortedSet<HistoryElement> getHistoryElements() {
		// PRECONDITIONS
		
		try {
			// FIXME use HTML parser
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getNumberOfComments()
	 */
	@Override
	public String getId() {
		// PRECONDITIONS
		
		try {
			return this.issue.getKey();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getPriority()
	 */
	@Override
	public Set<String> getKeywords() {
		// PRECONDITIONS
		
		try {
			final Set<String> result = new HashSet<String>();
			final Field field = this.issue.getField("labels");
			if (field.getValue() != null) {
				final String fields = field.getValue().toString();
				final String[] split = fields.replaceAll("[", "").replaceAll("]", "").replaceAll("\"", "").split(",");
				for (final String s : split) {
					result.add(s);
				}
			}
			return result;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getProduct()
	 */
	@Override
	public DateTime getLastUpdateTimestamp() {
		// PRECONDITIONS
		
		try {
			return this.issue.getUpdateDate();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getSeverity()
	 */
	@Override
	public Priority getPriority() {
		// PRECONDITIONS
		
		try {
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getSiblings()
	 */
	@Override
	public String getProduct() {
		// PRECONDITIONS
		
		try {
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getStatus()
	 */
	@Override
	public Resolution getResolution() {
		// PRECONDITIONS
		
		try {
			final BasicResolution basicResolution = this.issue.getResolution();
			if ((basicResolution != null) && (!basicResolution.getName().isEmpty())) {
				final String resolutionString = basicResolution.getName().toLowerCase();
				if (resolutionString.equals("unresolved")) {
					return Resolution.UNRESOLVED;
				} else if (resolutionString.equals("fixed")) {
					return Resolution.RESOLVED;
				} else if (resolutionString.equals("won't fix")) {
					return Resolution.WONT_FIX;
				} else if (resolutionString.equals("duplicate")) {
					return Resolution.DUPLICATE;
				} else if (resolutionString.equals("incomplete")) {
					return Resolution.UNRESOLVED;
				} else if (resolutionString.equals("cannot reproduce")) {
					return Resolution.WORKS_FOR_ME;
				} else if (resolutionString.equals("not a bug")) {
					return Resolution.INVALID;
				} else {
					return Resolution.UNKNOWN;
				}
			}
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getSubject()
	 */
	@Override
	public DateTime getResolutionTimestamp() {
		// PRECONDITIONS
		
		try {
			final Field field = this.issue.getField("resolved");
			if (field != null) {
				final String dateStr = field.getValue().toString();
				if (!dateStr.isEmpty()) {
					return DateTimeUtils.parseDate(dateStr);
				}
			}
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getSubmitter()
	 */
	@Override
	public Person getResolver() {
		// PRECONDITIONS
		
		try {
			// FIXME requires history parsing
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getSummary()
	 */
	@Override
	public String getScmFixVersion() {
		// PRECONDITIONS
		
		try {
			return getScmFixVersion();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getType()
	 */
	@Override
	public Severity getSeverity() {
		// PRECONDITIONS
		
		try {
			if ((this.issue.getPriority() != null) && (!this.issue.getPriority().getName().isEmpty())) {
				final String priority = this.issue.getPriority().getName().toLowerCase();
				if (priority.equals("blocker")) {
					return Severity.BLOCKER;
				} else if (priority.equals("critical")) {
					return Severity.CRITICAL;
				} else if (priority.equals("major")) {
					return Severity.MAJOR;
				} else if (priority.equals("minor")) {
					return Severity.MINOR;
				} else if (priority.equals("trivial")) {
					return Severity.TRIVIAL;
				} else if (priority.equals("")) {
					return null;
				} else {
					return Severity.UNKNOWN;
				}
			}
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getVersion()
	 */
	@Override
	public Set<Long> getSiblings() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			for (final IssueLink link : this.issue.getIssueLinks()) {
				
			}
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.bugs.tracker.Parser#setTracker(de.unisaarland.cs.st.moskito.bugs.tracker.Tracker)
	 */
	
	@Override
	public Status getStatus() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.bugs.tracker.Parser#setXMLReport(de.unisaarland.cs.st.moskito.bugs.tracker.XmlReport
	 * )
	 */
	
	@Override
	public String getSubject() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	@Override
	public Person getSubmitter() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	@Override
	public String getSummary() {
		// PRECONDITIONS
		
		try {
			return this.issue.getSummary();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	@Override
	public Type getType() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	@Override
	public String getVersion() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	@Override
	public void setTracker(final Tracker tracker) {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
		} finally {
			// POSTCONDITIONS
		}
	}
	
	@Override
	public boolean setURI(final ReportLink reportLink) {
		// PRECONDITIONS
		
		try {
			
			final IssueRestClient issueClient = this.restClient.getIssueClient();
			this.issue = issueClient.getIssue(reportLink.getBugId(), new NullProgressMonitor());
			if (this.issue == null) {
				return false;
			}
			this.fetchTime = new DateTime();
			return true;
			
		} catch (final RestClientException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return false;
		} finally {
			// POSTCONDITIONS
		}
	}
}
