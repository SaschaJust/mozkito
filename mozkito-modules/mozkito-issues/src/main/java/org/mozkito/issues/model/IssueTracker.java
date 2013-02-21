/*******************************************************************************
 * Copyright 2013 Kim Herzig, Sascha Just
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
package org.mozkito.issues.model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.kanuni.annotations.string.Length;

import org.joda.time.DateTime;

import org.mozkito.persistence.Annotated;
import org.mozkito.persistence.PersistenceUtil;

/**
 * The Class VersionArchive.
 */
@Entity
@Table (name = "issue_tracker")
public class IssueTracker implements Annotated {
	
	/**
     * 
     */
	private static final long serialVersionUID = 3945460141965526156L;
	
	/**
	 * Load persisted VersionArchive from DB.
	 * 
	 * @param persistenceUtil
	 *            the persistence util
	 * @return the version archive
	 */
	public static IssueTracker loadIssueTracker(final PersistenceUtil persistenceUtil) {
		final List<IssueTracker> issueTrackers = persistenceUtil.load(persistenceUtil.createCriteria(IssueTracker.class));
		if (issueTrackers.isEmpty()) {
			throw new NoSuchElementException(
			                                 "There exists no persisted IssueTracker instance. Please run mozkito-issues first.");
		}
		if (issueTrackers.size() > 1) {
			throw new UnrecoverableError(
			                             "Found more than one persisted IssueTracker instance. This is unexpected. Multiple IssueTrackers in the same database are not supported.");
		}
		return issueTrackers.get(0);
	}
	
	/** The generated id. */
	private long                generatedId;
	
	/** The change sets. */
	private Map<String, Report> reports = new HashMap<String, Report>();
	
	/** The mozkito version. */
	private String              mozkitoVersion;
	
	/** The mozkito hash. */
	private String              mozkitoHash;
	
	/** The used settings. */
	private String              usedSettings;
	
	/** The mining date. */
	private DateTime            miningDate;
	
	/** The host info. */
	private String              hostInfo;
	
	/**
	 * Instantiates a new version archive.
	 * 
	 */
	public IssueTracker() {
	}
	
	/**
	 * Adds the change set.
	 * 
	 * @param report
	 *            the report
	 */
	@Transient
	protected void addReport(final Report report) {
		getReports().put(report.getId(), report);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.Annotated#getHandle()
	 */
	@Override
	@Transient
	public String getClassName() {
		return IssueTracker.class.getSimpleName();
	}
	
	/**
	 * Gets the generated id.
	 * 
	 * @return the generated id
	 */
	@Id
	@Column (name = "id")
	@GeneratedValue (strategy = GenerationType.SEQUENCE)
	public long getGeneratedId() {
		return this.generatedId;
	}
	
	/**
	 * Gets the host info.
	 * 
	 * @return the host info
	 */
	@Basic
	@Lob
	public String getHostInfo() {
		return this.hostInfo;
	}
	
	/**
	 * Gets DateTime representing the local time stamp in the system on which the mining process was performed at the
	 * time the mining process was initiated.
	 * 
	 * @return the mining date
	 */
	@Transient
	public DateTime getMiningDate() {
		return this.miningDate;
	}
	
	/**
	 * Gets the last update java timestamp.
	 * 
	 * @return the last update java timestamp
	 */
	@Temporal (TemporalType.TIMESTAMP)
	@Column (name = "miningDate")
	private Date getMiningJavaDate() {
		return getMiningDate() != null
		                              ? getMiningDate().toDate()
		                              : null;
	}
	
	/**
	 * Gets the mozkito hash.
	 * 
	 * @return the mozkito hash
	 */
	@Basic
	@Column (length = 51)
	public String getMozkitoHash() {
		return this.mozkitoHash;
	}
	
	/**
	 * Gets the mozkito version.
	 * 
	 * @return the mozkito version
	 */
	public String getMozkitoVersion() {
		return this.mozkitoVersion;
	}
	
	/**
	 * Gets the report by id.
	 * 
	 * @param id
	 *            the id
	 * @return the report by id
	 */
	@Transient
	public Report getReportById(final String id) {
		return getReports().get(id);
	}
	
	/**
	 * Gets the change sets.
	 * 
	 * @return the change sets
	 */
	//
	@OneToMany (cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, targetEntity = Report.class)
	public Map<String, Report> getReports() {
		return this.reports;
	}
	
	/**
	 * Gets the used settings.
	 * 
	 * @return the used settings
	 */
	@Basic
	@Lob
	@Column (length = 0)
	public String getUsedSettings() {
		return this.usedSettings;
	}
	
	/**
	 * Sets the generated id.
	 * 
	 * @param generatedId
	 *            the new generated id
	 */
	public void setGeneratedId(final long generatedId) {
		this.generatedId = generatedId;
	}
	
	/**
	 * Sets the host info.
	 * 
	 * @param hostInfo
	 *            the new host info
	 */
	public void setHostInfo(final String hostInfo) {
		this.hostInfo = hostInfo;
	}
	
	/**
	 * Sets the mining date.
	 * 
	 * @param miningDate
	 *            the new mining date
	 */
	@Transient
	public void setMiningDate(final DateTime miningDate) {
		this.miningDate = miningDate;
	}
	
	/**
	 * Sets the mining java date.
	 * 
	 * @param date
	 *            the new mining java date
	 */
	@SuppressWarnings ("unused")
	private void setMiningJavaDate(final Date date) {
		setMiningDate(date != null
		                          ? new DateTime(date)
		                          : null);
	}
	
	/**
	 * Sets the mozkito hash.
	 * 
	 * @param mozkitoHash
	 *            the new mozkito hash
	 */
	public void setMozkitoHash(@Length (length = 40) final String mozkitoHash) {
		this.mozkitoHash = mozkitoHash;
	}
	
	/**
	 * Sets the mozkito version.
	 * 
	 * @param mozkitoVersion
	 *            the new mozkito version
	 */
	public void setMozkitoVersion(final String mozkitoVersion) {
		this.mozkitoVersion = mozkitoVersion;
	}
	
	/**
	 * Sets the reports.
	 * 
	 * @param reports
	 *            the reports
	 */
	@OneToMany (cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	public void setReports(final Map<String, Report> reports) {
		this.reports = reports;
	}
	
	/**
	 * Sets the used settings.
	 * 
	 * @param usedSettings
	 *            the new used settings
	 */
	public void setUsedSettings(final String usedSettings) {
		this.usedSettings = usedSettings;
	}
	
}
