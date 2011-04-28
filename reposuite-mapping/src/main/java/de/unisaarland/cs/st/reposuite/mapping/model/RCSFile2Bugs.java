/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.model;

import java.util.List;
import java.util.Set;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.exceptions.Shutdown;
import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.persistence.Criteria;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceManager;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSFile;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class RCSFile2Bugs {
	
	RCSFile     file;
	Set<Report> reports;
	
	static {
		PersistenceManager.registerNativeQuery("postgresql",
		                                       "files2bugs",
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
	}
	
	/**
	 * @return
	 */
	public static List<RCSFile2Bugs> getBugCounts() {
		PersistenceUtil util;
		try {
			util = PersistenceManager.getUtil();
			System.err.println("Executing query: " + PersistenceManager.getNativeQuery(util, "files2bugs"));
			@SuppressWarnings ("unchecked")
			List<Object[]> result = util.executeNativeSelectQuery(PersistenceManager.getNativeQuery(util, "files2bugs"));
			for (Object[] entries : result) {
				Criteria<RCSFile> criteria = util.createCriteria(RCSFile.class).eq("generatedId", entries[0]);
				List<RCSFile> list = util.load(criteria);
				System.err.println(list.iterator().next().getLatestPath() + ", " + entries[1] + ", " + entries[2]);
			}
		} catch (UninitializedDatabaseException e) {
			throw new Shutdown(e);
		}
		
		return null;
	}
}
