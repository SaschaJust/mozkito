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
package org.mozkito.versions.model;

import java.io.IOException;
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

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.annotations.string.Length;
import net.ownhero.dev.kisa.Logger;

import org.joda.time.DateTime;
import org.mozkito.persistence.Annotated;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.versions.RevDependencyGraph;
import org.mozkito.versions.RevDependencyGraph.EdgeType;

/**
 * The Class VersionArchive.
 */
@Entity
@Table (name = "version_archive")
public class VersionArchive implements Annotated {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3701231007051514130L;
	
	/**
	 * Load persisted VersionArchive from DB
	 * 
	 * @param persistenceUtil
	 *            the persistence util
	 * @return the version archive
	 */
	public static VersionArchive loadVersionArchive(final PersistenceUtil persistenceUtil) {
		final List<VersionArchive> versionArchives = persistenceUtil.load(persistenceUtil.createCriteria(VersionArchive.class));
		if (versionArchives.isEmpty()) {
			throw new NoSuchElementException(
			                                 "There exists no persisted VersionArchive instance. Please run mozkito-versions first.");
		}
		if (versionArchives.size() > 1) {
			throw new UnrecoverableError(
			                             "Found more than one persisted VersionArchive instance. This is unexpected. Multiple VersionArchives in the same database are not supported.");
		}
		return versionArchives.get(0);
	}
	
	/** The rev dep graph. */
	private RevDependencyGraph     revDepGraph = null;
	
	/** The generated id. */
	private long                   generatedId;
	private Map<String, ChangeSet> changeSets;
	
	private String                 mozkitoVersion;
	private String                 mozkitoHash;
	private String                 usedSettings;
	private DateTime               miningDate;
	private Map<String, Branch>    branches;
	private String                 hostInfo;
	
	/**
	 * Instantiates a new version archive.
	 * 
	 * @deprecated exists for OpenJPA. Please use {@link #VersionArchive(RevDependencyGraph)} instead.
	 */
	@Deprecated
	public VersionArchive() {
	}
	
	/**
	 * Instantiates a new version archive.
	 * 
	 * @param revDependencyGraph
	 *            the rev dependency graph
	 */
	public VersionArchive(final RevDependencyGraph revDependencyGraph) {
		this.revDepGraph = revDependencyGraph;
		setChangeSets(new HashMap<String, ChangeSet>());
		setBranches(new HashMap<String, Branch>());
	}
	
	/**
	 * Adds the change set.
	 * 
	 * @param changeSet
	 *            the change set
	 */
	@Transient
	protected void addChangeSet(final ChangeSet changeSet) {
		this.changeSets.put(changeSet.getId(), changeSet);
	}
	
	/**
	 * Gets the branch.
	 * 
	 * @param name
	 *            the name
	 * @return the branch
	 */
	@Transient
	public synchronized Branch getBranch(@NotNull final String name) {
		if (!this.branches.containsKey(name)) {
			
			final Branch newBranch = new Branch(this, name);
			if (Logger.logDebug()) {
				Logger.debug("Creating new Branch " + newBranch.toString());
			}
			this.branches.put(newBranch.getName(), newBranch);
		}
		return this.branches.get(name);
	}
	
	/**
	 * Gets all branches.
	 * 
	 * @return the branches
	 */
	@OneToMany (cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, targetEntity = Branch.class)
	public Map<String, Branch> getBranches() {
		return this.branches;
	}
	
	/**
	 * Gets the transaction by id.
	 * 
	 * @param id
	 *            the id
	 * @return the transaction by id
	 */
	@Transient
	public ChangeSet getChangeSetById(final String id) {
		return this.changeSets.get(id);
	}
	
	/**
	 * Gets the change sets.
	 * 
	 * @return the change sets
	 */
	//
	@OneToMany (cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, targetEntity = ChangeSet.class)
	public Map<String, ChangeSet> getChangeSets() {
		return this.changeSets;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.Annotated#getHandle()
	 */
	@Override
	@Transient
	public String getClassName() {
		return VersionArchive.class.getSimpleName();
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
	 * Gets the master branch.
	 * 
	 * @return the master branch
	 */
	@Transient
	public Branch getMasterBranch() {
		return getBranch(Branch.MASTER_BRANCH_NAME);
	}
	
	/**
	 * Gets DateTime representing the local time stamp in the system on which the mining process was performed at the
	 * time the mining process was initiated
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
	@Column (length = 40)
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
	 * Gets the rev dependency graph.
	 * 
	 * @return the rev dependency graph
	 */
	@Transient
	public RevDependencyGraph getRevDependencyGraph() {
		if (this.revDepGraph == null) {
			this.revDepGraph = loadRevDependencyGraph();
		}
		return this.revDepGraph;
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
	
	private RevDependencyGraph loadRevDependencyGraph() {
		try {
			if (this.revDepGraph == null) {
				this.revDepGraph = new RevDependencyGraph();
				for (final Branch branch : getBranches().values()) {
					this.revDepGraph.addBranch(branch.getName(), branch.getHead().getId());
				}
				for (final ChangeSet changeSet : getChangeSets().values()) {
					this.revDepGraph.addChangeSet(changeSet.getId());
					for (final String tagName : changeSet.getTags()) {
						this.revDepGraph.addTag(tagName, changeSet.getId());
					}
					if (changeSet.getBranchParent() != null) {
						this.revDepGraph.addEdge(changeSet.getBranchParent().getId(), changeSet.getId(),
						                         EdgeType.BRANCH_EDGE);
						if (changeSet.getMergeParent() != null) {
							this.revDepGraph.addEdge(changeSet.getMergeParent().getId(), changeSet.getId(),
							                         EdgeType.MERGE_EDGE);
						}
					}
					
				}
			}
			return this.revDepGraph;
		} catch (final IOException e) {
			throw new UnrecoverableError(e);
		}
	}
	
	/**
	 * Sets the branches.
	 * 
	 * @param branches
	 *            the branches
	 */
	public void setBranches(final Map<String, Branch> branches) {
		this.branches = branches;
	}
	
	/**
	 * Sets the change sets.
	 * 
	 * @param changeSets
	 *            the change sets
	 */
	public void setChangeSets(final Map<String, ChangeSet> changeSets) {
		this.changeSets = changeSets;
	}
	
	/**
	 * Sets the ged id.
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
	 * Sets the rev dependency graph.
	 * 
	 * @param revDepGraph
	 *            the new rev dependency graph
	 */
	public void setRevDependencyGraph(final RevDependencyGraph revDepGraph) {
		this.revDepGraph = revDepGraph;
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
