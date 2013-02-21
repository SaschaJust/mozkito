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
package org.mozkito.issues.model;

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

import org.mozkito.issues.elements.Priority;
import org.mozkito.issues.elements.Resolution;
import org.mozkito.issues.elements.Severity;
import org.mozkito.issues.elements.Status;
import org.mozkito.issues.elements.Type;
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
	 * 
	 * @deprecated Use {@link #EnhancedReport(Report)}. This method exists to fulfill OpenJPA requirements.
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
	
	/**
	 * Adds the attachment entry.
	 * 
	 * @param entry
	 *            the entry
	 */
	@Transient
	public void addAttachmentEntry(final AttachmentEntry entry) {
		getReport().addAttachmentEntry(entry);
	}
	
	/**
	 * Adds the comment.
	 * 
	 * @param comment
	 *            the comment
	 * @return true, if successful
	 */
	@Transient
	public boolean addComment(final Comment comment) {
		return getReport().addComment(comment);
	}
	
	/**
	 * Adds the keyword.
	 * 
	 * @param keyword
	 *            the keyword
	 * @return true, if successful
	 */
	@Transient
	public boolean addKeyword(final String keyword) {
		return getReport().addKeyword(keyword);
	}
	
	/**
	 * Adds the sibling.
	 * 
	 * @param sibling
	 *            the sibling
	 * @return true, if successful
	 */
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
	
	/**
	 * Compare to.
	 * 
	 * @param o
	 *            the o
	 * @return the int
	 */
	public int compareTo(final Report o) {
		return getReport().compareTo(o);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
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
	
	/**
	 * Gets the assigned to.
	 * 
	 * @return the assigned to
	 */
	@Transient
	public Person getAssignedTo() {
		return getReport().getAssignedTo();
	}
	
	/**
	 * Gets the attachment entries.
	 * 
	 * @return the attachment entries
	 */
	@Transient
	public List<AttachmentEntry> getAttachmentEntries() {
		return getReport().getAttachmentEntries();
	}
	
	/**
	 * Gets the category.
	 * 
	 * @return the category
	 */
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
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.Annotated#getHandle()
	 */
	@Override
	public final String getClassName() {
		return JavaUtils.getHandle(EnhancedReport.class);
	}
	
	/**
	 * Gets the comments.
	 * 
	 * @return the comments
	 */
	@Transient
	public SortedSet<Comment> getComments() {
		return getReport().getComments();
	}
	
	/**
	 * Gets the component.
	 * 
	 * @return the component
	 */
	@Transient
	public String getComponent() {
		return getReport().getComponent();
	}
	
	/**
	 * Gets the creation timestamp.
	 * 
	 * @return the creation timestamp
	 */
	@Transient
	public DateTime getCreationTimestamp() {
		return getReport().getCreationTimestamp();
	}
	
	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	@Transient
	public String getDescription() {
		return getReport().getDescription();
	}
	
	/**
	 * Gets the field.
	 * 
	 * @param lowerFieldName
	 *            the lower field name
	 * @return the field
	 */
	@Transient
	public Object getField(final String lowerFieldName) {
		return getReport().getField(lowerFieldName);
	}
	
	/**
	 * Gets the hash.
	 * 
	 * @return the hash
	 */
	@Transient
	public byte[] getHash() {
		return getReport().getHash();
	}
	
	/**
	 * Gets the history.
	 * 
	 * @return the history
	 */
	@Transient
	public History getHistory() {
		return getReport().getHistory();
	}
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Transient
	public String getId() {
		return getReport().getId();
	}
	
	/**
	 * Gets the keywords.
	 * 
	 * @return the keywords
	 */
	@Transient
	public Set<String> getKeywords() {
		return getReport().getKeywords();
	}
	
	/**
	 * Gets the last fetch.
	 * 
	 * @return the last fetch
	 */
	@Transient
	public DateTime getLastFetch() {
		return getReport().getLastFetch();
	}
	
	/**
	 * Gets the last update timestamp.
	 * 
	 * @return the last update timestamp
	 */
	@Transient
	public DateTime getLastUpdateTimestamp() {
		return getReport().getLastUpdateTimestamp();
	}
	
	/**
	 * Gets the person container.
	 * 
	 * @return the person container
	 */
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
	
	/**
	 * Gets the priority.
	 * 
	 * @return the priority
	 */
	@Transient
	public Priority getPriority() {
		return getReport().getPriority();
	}
	
	/**
	 * Gets the product.
	 * 
	 * @return the product
	 */
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
	
	/**
	 * Gets the resolution.
	 * 
	 * @return the resolution
	 */
	@Transient
	public Resolution getResolution() {
		return getReport().getResolution();
	}
	
	/**
	 * Gets the resolution timestamp.
	 * 
	 * @return the resolution timestamp
	 */
	@Transient
	public DateTime getResolutionTimestamp() {
		return getReport().getResolutionTimestamp();
	}
	
	/**
	 * Gets the resolver.
	 * 
	 * @return the resolver
	 */
	@Transient
	public Person getResolver() {
		return getReport().getResolver();
	}
	
	/**
	 * Gets the scm fix version.
	 * 
	 * @return the scm fix version
	 */
	@Transient
	public String getScmFixVersion() {
		return getReport().getScmFixVersion();
	}
	
	/**
	 * Gets the severity.
	 * 
	 * @return the severity
	 */
	@Transient
	public Severity getSeverity() {
		return getReport().getSeverity();
	}
	
	/**
	 * Gets the siblings.
	 * 
	 * @return the siblings
	 */
	@Transient
	public SortedSet<String> getSiblings() {
		return getReport().getSiblings();
	}
	
	/**
	 * Gets the status.
	 * 
	 * @return the status
	 */
	@Transient
	public Status getStatus() {
		return getReport().getStatus();
	}
	
	/**
	 * Gets the subject.
	 * 
	 * @return the subject
	 */
	@Transient
	public String getSubject() {
		return getReport().getSubject();
	}
	
	/**
	 * Gets the submitter.
	 * 
	 * @return the submitter
	 */
	@Transient
	public Person getSubmitter() {
		return getReport().getSubmitter();
	}
	
	/**
	 * Gets the summary.
	 * 
	 * @return the summary
	 */
	@Transient
	public String getSummary() {
		return getReport().getSummary();
	}
	
	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	@Transient
	public Type getType() {
		return getReport().getType();
	}
	
	/**
	 * Gets the version.
	 * 
	 * @return the version
	 */
	@Transient
	public String getVersion() {
		return getReport().getVersion();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getReport().hashCode();
	}
	
	/**
	 * Sets the assigned to.
	 * 
	 * @param assignedTo
	 *            the new assigned to
	 */
	@Transient
	public void setAssignedTo(final Person assignedTo) {
		getReport().setAssignedTo(assignedTo);
	}
	
	/**
	 * Sets the attachment entries.
	 * 
	 * @param attachmentEntries
	 *            the new attachment entries
	 */
	@Transient
	public void setAttachmentEntries(final List<AttachmentEntry> attachmentEntries) {
		getReport().setAttachmentEntries(attachmentEntries);
	}
	
	/**
	 * Sets the category.
	 * 
	 * @param category
	 *            the new category
	 */
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
	
	/**
	 * Sets the comments.
	 * 
	 * @param comments
	 *            the new comments
	 * 
	 * @deprecated use {@link #addComment(Comment)} to add comments. This method exists due to OpenJPA requirements.
	 */
	@Deprecated
	public void setComments(final SortedSet<Comment> comments) {
		getReport().setComments(comments);
	}
	
	/**
	 * Sets the component.
	 * 
	 * @param component
	 *            the new component
	 */
	@Transient
	public void setComponent(final String component) {
		getReport().setComponent(component);
	}
	
	/**
	 * Sets the creation timestamp.
	 * 
	 * @param creationTimestamp
	 *            the new creation timestamp
	 */
	@Transient
	public void setCreationTimestamp(final DateTime creationTimestamp) {
		getReport().setCreationTimestamp(creationTimestamp);
	}
	
	/**
	 * Sets the description.
	 * 
	 * @param description
	 *            the new description
	 */
	@Transient
	public void setDescription(final String description) {
		getReport().setDescription(description);
	}
	
	/**
	 * Sets the field.
	 * 
	 * @param fieldName
	 *            the field name
	 * @param fieldValue
	 *            the field value
	 */
	@Transient
	public void setField(final String fieldName,
	                     final Object fieldValue) {
		getReport().setField(fieldName, fieldValue);
	}
	
	/**
	 * Sets the hash.
	 * 
	 * @param hash
	 *            the new hash
	 */
	@Transient
	public void setHash(final byte[] hash) {
		getReport().setHash(hash);
	}
	
	/**
	 * Sets the keywords.
	 * 
	 * @param keywords
	 *            the new keywords
	 */
	@Transient
	public void setKeywords(final Set<String> keywords) {
		getReport().setKeywords(keywords);
	}
	
	/**
	 * Sets the last fetch.
	 * 
	 * @param lastFetch
	 *            the new last fetch
	 */
	@Transient
	public void setLastFetch(final DateTime lastFetch) {
		getReport().setLastFetch(lastFetch);
	}
	
	/**
	 * Sets the last update timestamp.
	 * 
	 * @param lastUpdateTimestamp
	 *            the new last update timestamp
	 */
	@Transient
	public void setLastUpdateTimestamp(final DateTime lastUpdateTimestamp) {
		getReport().setLastUpdateTimestamp(lastUpdateTimestamp);
	}
	
	/**
	 * Sets the person container.
	 * 
	 * @param personContainer
	 *            the new person container
	 */
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
	
	/**
	 * Sets the priority.
	 * 
	 * @param priority
	 *            the new priority
	 */
	@Transient
	public void setPriority(final Priority priority) {
		getReport().setPriority(priority);
	}
	
	/**
	 * Sets the product.
	 * 
	 * @param product
	 *            the new product
	 */
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
	
	/**
	 * Sets the resolution.
	 * 
	 * @param resolution
	 *            the new resolution
	 */
	@Transient
	public void setResolution(final Resolution resolution) {
		getReport().setResolution(resolution);
	}
	
	/**
	 * Sets the resolution timestamp.
	 * 
	 * @param resolutionTimestamp
	 *            the new resolution timestamp
	 */
	@Transient
	public void setResolutionTimestamp(final DateTime resolutionTimestamp) {
		getReport().setResolutionTimestamp(resolutionTimestamp);
	}
	
	/**
	 * Sets the resolver.
	 * 
	 * @param resolver
	 *            the new resolver
	 */
	@Transient
	public void setResolver(final Person resolver) {
		getReport().setResolver(resolver);
	}
	
	/**
	 * Sets the scm fix version.
	 * 
	 * @param scmFixVersion
	 *            the new scm fix version
	 */
	@Transient
	public void setScmFixVersion(final String scmFixVersion) {
		getReport().setScmFixVersion(scmFixVersion);
	}
	
	/**
	 * Sets the severity.
	 * 
	 * @param severity
	 *            the new severity
	 */
	@Transient
	public void setSeverity(final Severity severity) {
		getReport().setSeverity(severity);
	}
	
	/**
	 * Sets the siblings.
	 * 
	 * @param siblings
	 *            the new siblings
	 */
	@Transient
	public void setSiblings(final SortedSet<String> siblings) {
		getReport().setSiblings(siblings);
	}
	
	/**
	 * Sets the status.
	 * 
	 * @param status
	 *            the new status
	 */
	@Transient
	public void setStatus(final Status status) {
		getReport().setStatus(status);
	};
	
	/**
	 * Sets the subject.
	 * 
	 * @param subject
	 *            the new subject
	 */
	@Transient
	public void setSubject(final String subject) {
		getReport().setSubject(subject);
	}
	
	/**
	 * Sets the submitter.
	 * 
	 * @param submitter
	 *            the new submitter
	 */
	@Transient
	public void setSubmitter(final Person submitter) {
		getReport().setSubmitter(submitter);
	}
	
	/**
	 * Sets the summary.
	 * 
	 * @param summary
	 *            the new summary
	 */
	@Transient
	public void setSummary(final String summary) {
		getReport().setSummary(summary);
	}
	
	/**
	 * Sets the type.
	 * 
	 * @param type
	 *            the new type
	 */
	@Transient
	public void setType(final Type type) {
		getReport().setType(type);
	}
	
	/**
	 * Sets the version.
	 * 
	 * @param version
	 *            the new version
	 */
	@Transient
	public void setVersion(final String version) {
		getReport().setVersion(version);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
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
