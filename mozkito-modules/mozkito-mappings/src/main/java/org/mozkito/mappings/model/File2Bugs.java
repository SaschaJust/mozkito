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
package org.mozkito.mappings.model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.mozkito.issues.model.Report;
import org.mozkito.persistence.Annotated;
import org.mozkito.persistence.Criteria;
import org.mozkito.persistence.DatabaseType;
import org.mozkito.persistence.PersistenceManager;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.utilities.commons.JavaUtils;
import org.mozkito.utilities.io.FileUtils;
import org.mozkito.versions.exceptions.NoSuchHandleException;
import org.mozkito.versions.model.Handle;

/**
 * The Class File2Bugs.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
@Entity
public class File2Bugs implements Annotated {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5780165055568852588L;
	
	static {
		PersistenceManager.registerNativeQuery(DatabaseType.POSTGRESQL,
		                                       "files2bugsarray", //$NON-NLS-1$
		                                       "SELECT changedfile_id AS file_id, array_length(issues, 1) AS bug_count, issues AS bug_ids           " //$NON-NLS-1$
		                                               + "FROM (                                                                                " //$NON-NLS-1$
		                                               + "SELECT changedfile_id, ARRAY(                                                         " //$NON-NLS-1$
		                                               + "	SELECT reportid                                                                     " //$NON-NLS-1$
		                                               + "	FROM revision AS revisions                                                       " //$NON-NLS-1$
		                                               + "	INNER JOIN rcsbugmapping AS mapping                                                 " //$NON-NLS-1$
		                                               + "		ON (revisions.transaction_id = mapping.changesetid)                           " //$NON-NLS-1$
		                                               + "	WHERE revisions.changedfile_id = A.changedfile_id                                   " //$NON-NLS-1$
		                                               + ") AS issues                                                                             " //$NON-NLS-1$
		                                               + "FROM revision AS A                                                                 " //$NON-NLS-1$
		                                               + "ORDER BY changedfile_id                                                               " //$NON-NLS-1$
		                                               + ") innerquery                                                                          " //$NON-NLS-1$
		                                               + "WHERE array_length(issues, 1) > 0                                                       " //$NON-NLS-1$
		                                               + "GROUP BY file_id, issues;                                                               "); //$NON-NLS-1$
		
		PersistenceManager.registerNativeQuery(DatabaseType.POSTGRESQL,
		                                       "files2bugs", "SELECT changedfile_id, reportid " //$NON-NLS-1$//$NON-NLS-2$ 
		                                               + "FROM revision AS revision " + "JOIN rcsbugmapping AS mapping " //$NON-NLS-1$//$NON-NLS-2$
		                                               + "  ON (revision.transaction_id = mapping.changesetid) " + "ORDER BY changedfile_id"); //$NON-NLS-1$ //$NON-NLS-2$
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
		                                                                                              "files2bugs")); //$NON-NLS-1$
		Criteria<Handle> fileCriteria;
		Criteria<Report> reportCriteria;
		long fileid = -1, tmp = -1, bugid = -1;
		Handle handle = null;
		final Set<Report> reports = new HashSet<Report>();
		
		for (final Object[] entries : result) {
			tmp = (Long) entries[0];
			bugid = (Long) entries[1];
			
			if (tmp != fileid) {
				if (!reports.isEmpty()) {
					ret.add(new File2Bugs(handle, reports));
					reports.clear();
				}
				
				fileid = tmp;
				fileCriteria = util.createCriteria(Handle.class).eq("generatedId", fileid); //$NON-NLS-1$
				handle = util.load(fileCriteria).iterator().next();
			}
			
			reportCriteria = util.createCriteria(Report.class).eq("id", bugid); //$NON-NLS-1$
			reports.addAll(util.load(reportCriteria));
		}
		
		if (!reports.isEmpty()) {
			ret.add(new File2Bugs(handle, reports));
			reports.clear();
		}
		
		return ret;
	}
	
	/** The file. */
	Handle      handle;
	
	/** The reports. */
	Set<Report> reports;
	
	/**
	 * used by persistence provider only.
	 */
	public File2Bugs() {
	}
	
	/**
	 * Instantiates a new file2 issues.
	 * 
	 * @param handle
	 *            the file
	 * @param reports
	 *            the reports
	 */
	public File2Bugs(final Handle handle, final Set<Report> reports) {
		setFile(handle);
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
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.Annotated#getHandle()
	 */
	@Override
	public final String getClassName() {
		return JavaUtils.getHandle(File2Bugs.class);
	}
	
	/**
	 * Gets the file.
	 * 
	 * @return the file
	 */
	@OneToOne (cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.LAZY)
	public Handle getFile() {
		return this.handle;
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
	 * @param handle
	 *            the file to set
	 */
	public void setFile(final Handle handle) {
		this.handle = handle;
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
		try {
			builder.append(getFile().getLatestPath()).append(","); //$NON-NLS-1$
		} catch (final NoSuchHandleException e1) {
			// TODO @just please consider the case that handle.getPath does not find the file
		}
		builder.append(getReports().size()).append(","); //$NON-NLS-1$
		final StringBuilder b = new StringBuilder();
		for (final Report report : getReports()) {
			if (b.length() > 0) {
				b.append(" "); //$NON-NLS-1$
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
		builder.append("File2Bugs [file="); //$NON-NLS-1$
		builder.append(getFile().getGeneratedId());
		builder.append(", reports="); //$NON-NLS-1$
		final StringBuilder b = new StringBuilder();
		for (final Report report : getReports()) {
			if (b.length() > 0) {
				b.append(","); //$NON-NLS-1$
			}
			b.append(report.getId());
		}
		builder.append(b);
		builder.append("]"); //$NON-NLS-1$
		return builder.toString();
	}
	
}
