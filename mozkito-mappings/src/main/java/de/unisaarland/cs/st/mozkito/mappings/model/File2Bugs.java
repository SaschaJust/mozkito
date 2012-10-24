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
package de.unisaarland.cs.st.mozkito.mappings.model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import net.ownhero.dev.ioda.FileUtils;
import de.unisaarland.cs.st.mozkito.issues.tracker.model.Report;
import de.unisaarland.cs.st.mozkito.persistence.Annotated;
import de.unisaarland.cs.st.mozkito.persistence.Criteria;
import de.unisaarland.cs.st.mozkito.persistence.PersistenceManager;
import de.unisaarland.cs.st.mozkito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.mozkito.versions.model.RCSFile;

/**
 * The Class File2Bugs.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
@Entity
public class File2Bugs implements Annotated {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5780165055568852588L;
	
	static {
		PersistenceManager.registerNativeQuery("postgresql",
		                                       "files2bugsarray",
		                                       "SELECT changedfile_id AS file_id, array_length(bugs, 1) AS bug_count, bugs AS bug_ids           "
		                                               + "FROM (                                                                                "
		                                               + "SELECT changedfile_id, ARRAY(                                                         "
		                                               + "	SELECT reportid                                                                     "
		                                               + "	FROM rcsrevision AS revisions                                                       "
		                                               + "	INNER JOIN rcsbugmapping AS mapping                                                 "
		                                               + "		ON (revisions.transaction_id = mapping.transactionid)                           "
		                                               + "	WHERE revisions.changedfile_id = A.changedfile_id                                   "
		                                               + ") AS bugs                                                                             "
		                                               + "FROM rcsrevision AS A                                                                 "
		                                               + "ORDER BY changedfile_id                                                               "
		                                               + ") innerquery                                                                          "
		                                               + "WHERE array_length(bugs, 1) > 0                                                       "
		                                               + "GROUP BY file_id, bugs;                                                               ");
		PersistenceManager.registerNativeQuery("postgresql", "files2bugs", "SELECT changedfile_id, reportid "
		        + "FROM rcsrevision AS revision " + "JOIN rcsbugmapping AS mapping "
		        + "  ON (revision.transaction_id = mapping.transactionid) " + "ORDER BY changedfile_id");
	}
	
	/**
	 * Gets the bug counts.
	 * 
	 * @param util
	 *            the util
	 * @return the bug counts
	 */
	public static List<File2Bugs> getBugCounts(final PersistenceUtil util) {
		final List<File2Bugs> ret = new LinkedList<File2Bugs>();
		
		@SuppressWarnings ("unchecked")
		final List<Object[]> result = util.executeNativeSelectQuery(PersistenceManager.getNativeQuery(util,
		                                                                                              "files2bugs"));
		Criteria<RCSFile> fileCriteria;
		Criteria<Report> reportCriteria;
		long fileid = -1, tmp = -1, bugid = -1;
		RCSFile file = null;
		final Set<Report> reports = new HashSet<Report>();
		
		for (final Object[] entries : result) {
			tmp = (Long) entries[0];
			bugid = (Long) entries[1];
			
			if (tmp != fileid) {
				if (!reports.isEmpty()) {
					ret.add(new File2Bugs(file, reports));
					reports.clear();
				}
				
				fileid = tmp;
				fileCriteria = util.createCriteria(RCSFile.class).eq("generatedId", fileid);
				file = util.load(fileCriteria).iterator().next();
			}
			
			reportCriteria = util.createCriteria(Report.class).eq("id", bugid);
			reports.addAll(util.load(reportCriteria));
		}
		
		if (!reports.isEmpty()) {
			ret.add(new File2Bugs(file, reports));
			reports.clear();
		}
		
		return ret;
	}
	
	/** The file. */
	RCSFile     file;
	
	/** The reports. */
	Set<Report> reports;
	
	/**
	 * used by persistence provider only.
	 */
	public File2Bugs() {
	}
	
	/**
	 * Instantiates a new file2 bugs.
	 * 
	 * @param file
	 *            the file
	 * @param reports
	 *            the reports
	 */
	public File2Bugs(final RCSFile file, final Set<Report> reports) {
		setFile(file);
		setReports(reports);
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
		if (!(obj instanceof File2Bugs)) {
			return false;
		}
		final File2Bugs other = (File2Bugs) obj;
		if (getFile() == null) {
			if (other.getFile() != null) {
				return false;
			}
		} else if (!getFile().equals(other.getFile())) {
			return false;
		}
		return true;
	}
	
	/**
	 * Gets the file.
	 * 
	 * @return the file
	 */
	@OneToOne (cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.LAZY)
	public RCSFile getFile() {
		return this.file;
	}
	
	/**
	 * Gets the reports.
	 * 
	 * @return the reports
	 */
	@OneToMany (cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.LAZY)
	public Set<Report> getReports() {
		return this.reports;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((getFile() == null)
		                                                ? 0
		                                                : getFile().hashCode());
		return result;
	}
	
	/**
	 * Sets the file.
	 * 
	 * @param file
	 *            the file to set
	 */
	public void setFile(final RCSFile file) {
		this.file = file;
	}
	
	/**
	 * Sets the reports.
	 * 
	 * @param reports
	 *            the reports to set
	 */
	public void setReports(final Set<Report> reports) {
		this.reports = reports;
	}
	
	/**
	 * To csv.
	 * 
	 * @return the string
	 */
	public String toCSV() {
		final StringBuilder builder = new StringBuilder();
		builder.append(getFile().getLatestPath()).append(",");
		builder.append(getReports().size()).append(",");
		final StringBuilder b = new StringBuilder();
		for (final Report report : getReports()) {
			if (b.length() > 0) {
				b.append(" ");
			}
			b.append(report.getId());
		}
		builder.append(b).append(FileUtils.lineSeparator);
		return builder.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("RCSFile2Bugs [file=");
		builder.append(getFile().getGeneratedId());
		builder.append(", reports=");
		final StringBuilder b = new StringBuilder();
		for (final Report report : getReports()) {
			if (b.length() > 0) {
				b.append(",");
			}
			b.append(report.getId());
		}
		builder.append(b);
		builder.append("]");
		return builder.toString();
	}
	
}
