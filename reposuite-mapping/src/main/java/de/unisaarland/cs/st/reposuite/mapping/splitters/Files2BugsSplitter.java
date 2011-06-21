/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.splitters;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.exceptions.Shutdown;
import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.mapping.model.File2Bugs;
import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.persistence.Criteria;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceManager;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSFile;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class Files2BugsSplitter extends MappingSplitter {
	
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
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.mapping.splitters.MappingSplitter#
	 * getDescription()
	 */
	@Override
	public String getDescription() {
		return "Creates a table that puts files into relation to their bugs.";
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.splitters.MappingSplitter#process
	 * ()
	 */
	@Override
	public List<Annotated> process() {
		List<Annotated> ret = new LinkedList<Annotated>();
		PersistenceUtil util;
		try {
			util = PersistenceManager.getUtil();
			
			@SuppressWarnings ("unchecked")
			List<Object[]> result = util.executeNativeSelectQuery(PersistenceManager.getNativeQuery(util, "files2bugs"));
			Criteria<RCSFile> fileCriteria;
			Criteria<Report> reportCriteria;
			long fileid = -1, tmp = -1, bugid = -1;
			RCSFile file = null;
			Set<Report> reports = new HashSet<Report>();
			
			for (Object[] entries : result) {
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
		} catch (UninitializedDatabaseException e) {
			throw new Shutdown(e);
		}
		
		return ret;
	}
	
}
