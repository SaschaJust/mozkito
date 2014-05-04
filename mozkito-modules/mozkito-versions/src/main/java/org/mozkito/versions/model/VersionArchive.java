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
package org.mozkito.versions.model;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.kanuni.annotations.string.Length;
import net.ownhero.dev.kisa.Logger;

import org.joda.time.DateTime;

import org.mozkito.database.Criteria;
import org.mozkito.database.Entity;
import org.mozkito.database.Layout;
import org.mozkito.database.Layout.TableType;
import org.mozkito.database.PersistenceUtil;
import org.mozkito.database.constraints.column.NotNull;
import org.mozkito.database.constraints.column.PrimaryKey;
import org.mozkito.database.exceptions.DatabaseException;
import org.mozkito.database.model.Column;
import org.mozkito.database.model.Table;
import org.mozkito.database.types.Type;
import org.mozkito.versions.elements.RevDependencyGraph;
import org.mozkito.versions.elements.RevDependencyGraph.EdgeType;
import org.mozkito.versions.exceptions.NotComparableException;

/**
 * The Class VersionArchive.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class VersionArchive implements Entity {
	
	/** The Constant LAYOUT. */
	public static final Layout<VersionArchive> LAYOUT           = new Layout<>();
	/** The Constant TABLE. */
	public static final Table                  MAIN_TABLE;
	
	static {
		try {
			MAIN_TABLE = new Table("version_archive", new Column("id", Type.getSerial(), new PrimaryKey(),
			                                                     new NotNull()), new Column("mozkito_version",
			                                                                                Type.getVarChar(255),
			                                                                                new NotNull()),
			                       new Column("mozkito_version", Type.getVarChar(32), new NotNull()),
			                       new Column("mining_timestamp", Type.getDateTime(), new NotNull()),
			                       new Column("settings", Type.getText(), new NotNull()),
			                       new Column("hostinfo", Type.getVarChar(255), new NotNull()));
			
			LAYOUT.addTable(MAIN_TABLE, TableType.MAIN);
			LAYOUT.makeImmutable();
		} catch (final DatabaseException e) {
			throw new UnrecoverableError(e);
		}
	}
	
	/** The Constant serialVersionUID. */
	private static final long                  serialVersionUID = -3701231007051514130L;
	
	/**
	 * Load persisted VersionArchive from DB.
	 * 
	 * @param persistenceUtil
	 *            the persistence util
	 * @return the version archive
	 */
	public static VersionArchive loadVersionArchive(final PersistenceUtil persistenceUtil) {
		List<VersionArchive> versionArchives;
		try {
			versionArchives = persistenceUtil.load(persistenceUtil.createCriteria(VersionArchive.class));
		} catch (final DatabaseException e) {
			throw new UnrecoverableError(e);
		}
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
	private Long                   id;
	
	/** The change sets. */
	private Map<String, ChangeSet> changeSets  = new HashMap<String, ChangeSet>();
	
	/** The mozkito version. */
	private String                 mozkitoVersion;
	
	/** The mozkito hash. */
	private String                 mozkitoHash;
	
	/** The used settings. */
	private String                 usedSettings;
	
	/** The mining date. */
	private DateTime               miningDate;
	
	/** The branches. */
	private Map<String, Branch>    branches    = new HashMap<String, Branch>();
	
	/** The host info. */
	private String                 hostInfo;
	
	/** The change set ids. */
	private Set<String>            changeSetIds;
	
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
	
	protected void addChangeSet(final ChangeSet changeSet) {
		SANITY: {
			assert this.changeSets != null;
		}
		
		this.changeSetIds.add(changeSet.getId());
		this.changeSets.put(changeSet.getId(), changeSet);
	}
	
	/**
	 * Compare change sets.
	 * 
	 * @param util
	 *            the util
	 * @param cs1
	 *            the cs1
	 * @param cs2
	 *            the cs2
	 * @return 0 is both change sets are equal, -1 if cs1 was applied before cs2, +1 if c1 was applied later than cs2.
	 * @throws NotComparableException
	 *             if both change sets were not applied in any common branch
	 */
	public int compareChangeSets(final PersistenceUtil util,
	                             final ChangeSet cs1,
	                             final ChangeSet cs2) throws NotComparableException {
		if (cs1.getId().equals(cs2.getId())) {
			return 0;
		}
		if (getRevDependencyGraph(util).existsPath(cs1.getId(), cs2.getId())) {
			return -1;
		}
		if (getRevDependencyGraph(util).existsPath(cs2.getId(), cs1.getId())) {
			return 1;
		}
		throw NotComparableException.format("The change sets %s and %s are not comparable. It seems both change sets were applied to different non-merging branches.",
		                                    cs1.toString(), cs2.toString());
	}
	
	/**
	 * Gets the branch.
	 * 
	 * @param util
	 *            the util
	 * @param name
	 *            the name
	 * @return the branch
	 */
	
	public synchronized Branch getBranch(final PersistenceUtil util,
	                                     final String name) {
		PRECONDITIONS: {
			if (util == null) {
				throw new NullPointerException();
			}
			if (name == null) {
				throw new NullPointerException();
			}
		}
		
		final Map<String, Branch> branches2 = getBranches(util);
		if (!branches2.containsKey(name)) {
			
			final Branch newBranch = new Branch(this, name);
			if (Logger.logDebug()) {
				Logger.debug("Creating new Branch " + newBranch.toString());
			}
			branches2.put(newBranch.getName(), newBranch);
		}
		return branches2.get(name);
	}
	
	/**
	 * Gets all branches.
	 * 
	 * @param util
	 *            the util
	 * @return the branches
	 */
	public Map<String, Branch> getBranches(final PersistenceUtil util) {
		if ((this.branches == null)) {
			this.branches = new HashMap<>();
			final Criteria<Branch> criteria = util.createCriteria(Branch.class);
			criteria.eq("version_archive", this.id);
			final List<Branch> load = util.load(criteria);
			final Set<String> branchSet = getBranches();
			
			for (final String branchId : branchSet) {
				final Branch branch = util.loadById(Branch.class, branchId);
				this.branches.put(branchId, branch);
			}
		}
		
		SANITY: {
			assert this.branches != null;
			assert this.branches.size() == this.branchIds.size();
		}
		
		return this.branches;
	}
	
	/**
	 * Gets the transaction by id.
	 * 
	 * @param util
	 *            the util
	 * @param id
	 *            the id
	 * @return the transaction by id
	 */
	
	public ChangeSet getChangeSetById(final PersistenceUtil util,
	                                  final String id) {
		return getChangeSets(util).get(id);
	}
	
	/**
	 * Gets the change sets.
	 * 
	 * @return the change sets
	 */
	public Set<String> getChangeSets() {
		return this.changeSetIds;
	}
	
	/**
	 * Gets the change sets.
	 * 
	 * @param util
	 *            the util
	 * @return the change sets
	 */
	public Map<String, ChangeSet> getChangeSets(final PersistenceUtil util) {
		if ((this.changeSets == null) || (getChangeSets().size() != this.changeSets.size())) {
			this.changeSets = new HashMap<>();
			util.loadByIds(ChangeSet.class, getChangeSets());
		}
		
		SANITY: {
			assert this.changeSets != null;
			assert this.changeSets.size() == this.changeSetIds.size();
		}
		return this.changeSets;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.persistence.Annotated#getClassName()
	 */
	@Override
	public String getClassName() {
		return VersionArchive.class.getSimpleName();
	}
	
	/**
	 * Gets the host info.
	 * 
	 * @return the host info
	 */
	public String getHostInfo() {
		return this.hostInfo;
	}
	
	/**
	 * Gets the generated id.
	 * 
	 * @return the generated id
	 */
	@Override
	public Long getId() {
		return this.id;
	}
	
	/**
	 * Gets the master branch.
	 * 
	 * @param util
	 *            the util
	 * @return the master branch
	 */
	
	public Branch getMasterBranch(final PersistenceUtil util) {
		return getBranch(util, Branch.MASTER_BRANCH_NAME);
	}
	
	/**
	 * Gets DateTime representing the local time stamp in the system on which the mining process was performed at the
	 * time the mining process was initiated.
	 * 
	 * @return the mining date
	 */
	
	public DateTime getMiningDate() {
		return this.miningDate;
	}
	
	/**
	 * Gets the last update java timestamp.
	 * 
	 * @return the last update java timestamp
	 */
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
	 * @param util
	 *            the util
	 * @return the rev dependency graph
	 */
	
	public RevDependencyGraph getRevDependencyGraph(final PersistenceUtil util) {
		if (this.revDepGraph == null) {
			this.revDepGraph = loadRevDependencyGraph(util);
		}
		return this.revDepGraph;
	}
	
	/**
	 * Gets the used settings.
	 * 
	 * @return the used settings
	 */
	public String getUsedSettings() {
		return this.usedSettings;
	}
	
	/**
	 * Load rev dependency graph.
	 * 
	 * @param util
	 *            the util
	 * @return the rev dependency graph
	 */
	private RevDependencyGraph loadRevDependencyGraph(final PersistenceUtil util) {
		try {
			if (this.revDepGraph == null) {
				this.revDepGraph = new RevDependencyGraph();
				for (final Branch branch : getBranches(util).values()) {
					this.revDepGraph.addBranch(branch.getName(), branch.getHead().getId());
				}
				for (final ChangeSet changeSet : getChangeSets(util).values()) {
					this.revDepGraph.addChangeSet(changeSet.getId());
					for (final String tagName : changeSet.getTags()) {
						this.revDepGraph.addTag(tagName, changeSet.getId());
					}
					if (changeSet.getBranchParent() != null) {
						this.revDepGraph.addEdge(changeSet.getBranchParent().getId(), changeSet.getId(),
						                         EdgeType.BRANCH_EDGE);
						for (final ChangeSet mergeParent : changeSet.getMergeParents()) {
							this.revDepGraph.addEdge(mergeParent.getId(), changeSet.getId(), EdgeType.MERGE_EDGE);
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
	 * @param id
	 *            the new generated id
	 */
	public void setGeneratedId(final Long id) {
		this.id = id;
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
