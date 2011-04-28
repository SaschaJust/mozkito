/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.model;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.dom4j.Document;
import org.w3c.dom.html.HTMLDocument;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.exceptions.Shutdown;
import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.output.Displayable;
import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.persistence.Criteria;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceManager;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSFile;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Entity
public class RCSFile2Bugs implements Annotated, Displayable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5780165055568852588L;
	RCSFile                   file;
	
	Set<Report>               reports;
	
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
		List<RCSFile2Bugs> ret = new LinkedList<RCSFile2Bugs>();
		PersistenceUtil util;
		try {
			util = PersistenceManager.getUtil();
			
			@SuppressWarnings ("unchecked")
			List<Object[]> result = util.executeNativeSelectQuery(PersistenceManager.getNativeQuery(util, "files2bugs"));
			for (Object[] entries : result) {
				Criteria<RCSFile> fileCriteria = util.createCriteria(RCSFile.class).eq("generatedId", entries[0]);
				List<RCSFile> list = util.load(fileCriteria);
				Criteria<Report> reportCriteria = util.createCriteria(Report.class).in("id", Arrays.asList(entries[2]));
				List<Report> reports = util.load(reportCriteria);
				ret.add(new RCSFile2Bugs(list.iterator().next(), new HashSet<Report>(reports)));
			}
		} catch (UninitializedDatabaseException e) {
			throw new Shutdown(e);
		}
		
		return ret;
	}
	
	/**
	 * @param file
	 * @param reports
	 */
	public RCSFile2Bugs(final RCSFile file, final Set<Report> reports) {
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
		if (!(obj instanceof RCSFile2Bugs)) {
			return false;
		}
		RCSFile2Bugs other = (RCSFile2Bugs) obj;
		if (file == null) {
			if (other.file != null) {
				return false;
			}
		} else if (!file.equals(other.file)) {
			return false;
		}
		return true;
	}
	
	/**
	 * @return the file
	 */
	@OneToOne (cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.LAZY)
	public RCSFile getFile() {
		return file;
	}
	
	/**
	 * @return the reports
	 */
	@OneToMany (cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.LAZY)
	public Set<Report> getReports() {
		return reports;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((file == null)
		                                         ? 0
		                                         : file.hashCode());
		return result;
	}
	
	/**
	 * @param file
	 *            the file to set
	 */
	public void setFile(final RCSFile file) {
		this.file = file;
	}
	
	/**
	 * @param reports
	 *            the reports to set
	 */
	public void setReports(final Set<Report> reports) {
		this.reports = reports;
	}
	
	@Override
	public String toCSV() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void toCSV(final OutputStream stream) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public HTMLDocument toHTML() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void toHTML(final OutputStream stream) {
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RCSFile2Bugs [file=");
		builder.append(file.getGeneratedId());
		builder.append(", reports=");
		StringBuilder b = new StringBuilder();
		for (Report report : getReports()) {
			if (b.length() > 0) {
				b.append(",");
			}
			b.append(report.getId());
		}
		builder.append(b);
		builder.append("]");
		return builder.toString();
	}
	
	@Override
	public String toTerm() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void toTerm(final OutputStream stream) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String toText() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void toText(final OutputStream stream) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Document toXML() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void toXML(final OutputStream stream) {
		// TODO Auto-generated method stub
		
	}
	
}
