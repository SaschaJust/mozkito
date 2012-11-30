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
 *******************************************************************************/
/**
 * 
 */
package org.mozkito.issues.tracker.model;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kanuni.conditions.CompareCondition;

import org.joda.time.DateTime;

import org.mozkito.issues.tracker.elements.Priority;
import org.mozkito.issues.tracker.elements.Resolution;
import org.mozkito.issues.tracker.elements.Severity;
import org.mozkito.issues.tracker.elements.Status;
import org.mozkito.issues.tracker.elements.Type;
import org.mozkito.persistence.Annotated;
import org.mozkito.persistence.model.Person;
import org.mozkito.persistence.model.PersonContainer;

/**
 * The Class Report.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
@Entity
@Table (name = "enhancedreport")
public class EnhancedReport implements Annotated, Comparable<EnhancedReport> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4258778761866638091L;
	
	/** The report. */
	private Report            report;
	
	/** The classified type. */
	private Type              classifiedType;
	
	/** The predicted type. */
	private Type              predictedType;
	
	/**
	 * Instantiates a new enhanced report.
	 */
	@Deprecated
	public EnhancedReport() {
	}
	
	/**
	 * Instantiates a new enhanced report.
	 * 
	 * @param report
	 *            the report
	 */
	public EnhancedReport(final Report report) {
		setReport(report);
	}
	
	@Transient
	public void addAttachmentEntry(final AttachmentEntry entry) {
		getReport().addAttachmentEntry(entry);
	}
	
	@Transient
	public boolean addComment(final Comment comment) {
		return getReport().addComment(comment);
	}
	
	@Transient
	public boolean addHistoryElement(final HistoryElement historyElement) {
		return getReport().addHistoryElement(historyElement);
	}
	
	@Transient
	public boolean addKeyword(final String keyword) {
		return getReport().addKeyword(keyword);
	}
	
	@Transient
	public boolean addSibling(final String sibling) {
		return getReport().addSibling(sibling);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final EnhancedReport o) {
		// PRECONDITIONS
		
		try {
			return getReport().compareTo(o.getReport());
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public int compareTo(final Report o) {
		return getReport().compareTo(o);
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final EnhancedReport other = (EnhancedReport) obj;
		return getReport().equals(other.getReport());
	}
	
	@Transient
	public Person getAssignedTo() {
		return getReport().getAssignedTo();
	}
	
	@Transient
	public List<AttachmentEntry> getAttachmentEntries() {
		return getReport().getAttachmentEntries();
	}
	
	@Transient
	public String getCategory() {
		return getReport().getCategory();
	}
	
	/**
	 * Gets the classified type.
	 * 
	 * @return the classified type
	 */
	@Enumerated (EnumType.STRING)
	public Type getClassifiedType() {
		// PRECONDITIONS
		
		try {
			return this.classifiedType;
		} finally {
			// POSTCONDITIONS
			// Condition.notNull(this.classifiedType, "Field '%s' in '%s'.", "classifiedType",
			// getClass().getSimpleName());
		}
	}
	
	@Transient
	public SortedSet<Comment> getComments() {
		return getReport().getComments();
	}
	
	@Transient
	public String getComponent() {
		return getReport().getComponent();
	}
	
	@Transient
	public DateTime getCreationTimestamp() {
		return getReport().getCreationTimestamp();
	}
	
	@Transient
	public String getDescription() {
		return getReport().getDescription();
	}
	
	@Transient
	public Object getField(final String lowerFieldName) {
		return getReport().getField(lowerFieldName);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.Annotated#getHandle()
	 */
	public final String getHandle() {
		return JavaUtils.getHandle(EnhancedReport.class);
	}
	
	@Transient
	public byte[] getHash() {
		return getReport().getHash();
	}
	
	@Transient
	public History getHistory() {
		return getReport().getHistory();
	}
	
	@Transient
	public String getId() {
		return getReport().getId();
	}
	
	@Transient
	public Set<String> getKeywords() {
		return getReport().getKeywords();
	}
	
	@Transient
	public DateTime getLastFetch() {
		return getReport().getLastFetch();
	}
	
	@Transient
	public DateTime getLastUpdateTimestamp() {
		return getReport().getLastUpdateTimestamp();
	}
	
	@Transient
	public PersonContainer getPersonContainer() {
		return getReport().getPersonContainer();
	}
	
	/**
	 * Gets the predicted type.
	 * 
	 * @return the predicted type
	 */
	@Enumerated (EnumType.STRING)
	public Type getPredictedType() {
		// PRECONDITIONS
		
		try {
			return this.predictedType;
		} finally {
			// POSTCONDITIONS
			// Condition.notNull(this.predictedType, "Field '%s' in '%s'.", "predictedType",
			// getClass().getSimpleName());
		}
	}
	
	@Transient
	public Priority getPriority() {
		return getReport().getPriority();
	}
	
	@Transient
	public String getProduct() {
		return getReport().getProduct();
	}
	
	/**
	 * Gets the report.
	 * 
	 * @return the report
	 */
	@OneToOne (cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@Id
	public Report getReport() {
		// PRECONDITIONS
		
		try {
			return this.report;
		} finally {
			// POSTCONDITIONS
			// Condition.notNull(this.report, "Field '%s' in '%s'.", "report", getClass().getSimpleName());
		}
	}
	
	@Transient
	public Resolution getResolution() {
		return getReport().getResolution();
	}
	
	@Transient
	public DateTime getResolutionTimestamp() {
		return getReport().getResolutionTimestamp();
	}
	
	@Transient
	public Person getResolver() {
		return getReport().getResolver();
	}
	
	@Transient
	public String getScmFixVersion() {
		return getReport().getScmFixVersion();
	}
	
	@Transient
	public Severity getSeverity() {
		return getReport().getSeverity();
	}
	
	@Transient
	public SortedSet<String> getSiblings() {
		return getReport().getSiblings();
	}
	
	@Transient
	public Status getStatus() {
		return getReport().getStatus();
	}
	
	@Transient
	public String getSubject() {
		return getReport().getSubject();
	}
	
	@Transient
	public Person getSubmitter() {
		return getReport().getSubmitter();
	}
	
	@Transient
	public String getSummary() {
		return getReport().getSummary();
	}
	
	@Transient
	public Type getType() {
		return getReport().getType();
	}
	
	@Transient
	public String getVersion() {
		return getReport().getVersion();
	}
	
	@Override
	public int hashCode() {
		return getReport().hashCode();
	}
	
	@Transient
	public void setAssignedTo(final Person assignedTo) {
		getReport().setAssignedTo(assignedTo);
	}
	
	@Transient
	public void setAttachmentEntries(final List<AttachmentEntry> attachmentEntries) {
		getReport().setAttachmentEntries(attachmentEntries);
	}
	
	@Transient
	public void setCategory(final String category) {
		getReport().setCategory(category);
	}
	
	/**
	 * Sets the classified type.
	 * 
	 * @param classifiedType
	 *            the new classified type
	 * @deprecated Since the classified type of an EnhancedReport involves manual classification, this method should not
	 *             be called. You might override manual classified data. The method exists for the persistence layer and
	 *             import tools.
	 */
	@Deprecated
	public void setClassifiedType(final Type classifiedType) {
		// PRECONDITIONS
		try {
			this.classifiedType = classifiedType;
		} finally {
			// POSTCONDITIONS
			CompareCondition.equals(this.classifiedType, classifiedType,
			                        "After setting a value, the corresponding field has to hold the same value as used as a parameter within the setter.");
		}
	}
	
	@Deprecated
	public void setComments(final SortedSet<Comment> comments) {
		getReport().setComments(comments);
	}
	
	@Transient
	public void setComponent(final String component) {
		getReport().setComponent(component);
	}
	
	@Transient
	public void setCreationTimestamp(final DateTime creationTimestamp) {
		getReport().setCreationTimestamp(creationTimestamp);
	}
	
	@Transient
	public void setDescription(final String description) {
		getReport().setDescription(description);
	}
	
	@Transient
	public void setField(final String fieldName,
	                     final Object fieldValue) {
		getReport().setField(fieldName, fieldValue);
	}
	
	@Transient
	public void setHash(final byte[] hash) {
		getReport().setHash(hash);
	}
	
	@Transient
	public void setKeywords(final Set<String> keywords) {
		getReport().setKeywords(keywords);
	}
	
	@Transient
	public void setLastFetch(final DateTime lastFetch) {
		getReport().setLastFetch(lastFetch);
	}
	
	@Transient
	public void setLastUpdateTimestamp(final DateTime lastUpdateTimestamp) {
		getReport().setLastUpdateTimestamp(lastUpdateTimestamp);
	}
	
	@Transient
	public void setPersonContainer(final PersonContainer personContainer) {
		getReport().setPersonContainer(personContainer);
	}
	
	/**
	 * Sets the predicted type.
	 * 
	 * @param predictedType
	 *            the new predicted type
	 */
	public void setPredictedType(final Type predictedType) {
		// PRECONDITIONS
		try {
			this.predictedType = predictedType;
		} finally {
			// POSTCONDITIONS
			CompareCondition.equals(this.predictedType, predictedType,
			                        "After setting a value, the corresponding field has to hold the same value as used as a parameter within the setter.");
		}
	}
	
	@Transient
	public void setPriority(final Priority priority) {
		getReport().setPriority(priority);
	}
	
	@Transient
	public void setProduct(final String product) {
		getReport().setProduct(product);
	}
	
	/**
	 * Sets the report.
	 * 
	 * @param report
	 *            the new report
	 */
	public void setReport(final Report report) {
		// PRECONDITIONS
		try {
			this.report = report;
		} finally {
			// POSTCONDITIONS
			CompareCondition.equals(this.report, report,
			                        "After setting a value, the corresponding field has to hold the same value as used as a parameter within the setter.");
		}
	}
	
	@Transient
	public void setResolution(final Resolution resolution) {
		getReport().setResolution(resolution);
	}
	
	@Transient
	public void setResolutionTimestamp(final DateTime resolutionTimestamp) {
		getReport().setResolutionTimestamp(resolutionTimestamp);
	}
	
	@Transient
	public void setResolver(final Person resolver) {
		getReport().setResolver(resolver);
	}
	
	@Transient
	public void setScmFixVersion(final String scmFixVersion) {
		getReport().setScmFixVersion(scmFixVersion);
	}
	
	@Transient
	public void setSeverity(final Severity severity) {
		getReport().setSeverity(severity);
	}
	
	@Transient
	public void setSiblings(final SortedSet<String> siblings) {
		getReport().setSiblings(siblings);
	}
	
	@Transient
	public void setStatus(final Status status) {
		getReport().setStatus(status);
	};
	
	@Transient
	public void setSubject(final String subject) {
		getReport().setSubject(subject);
	}
	
	@Transient
	public void setSubmitter(final Person submitter) {
		getReport().setSubmitter(submitter);
	}
	
	@Transient
	public void setSummary(final String summary) {
		getReport().setSummary(summary);
	}
	
	@Transient
	public void setType(final Type type) {
		getReport().setType(type);
	}
	
	@Transient
	public void setVersion(final String version) {
		getReport().setVersion(version);
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(": [ classifiedType=");
		sb.append(getClassifiedType());
		sb.append(", predictedType=");
		sb.append(getPredictedType());
		sb.append(", ");
		sb.append(getReport().toString());
		sb.append("]");
		return sb.toString();
	}
	
}
