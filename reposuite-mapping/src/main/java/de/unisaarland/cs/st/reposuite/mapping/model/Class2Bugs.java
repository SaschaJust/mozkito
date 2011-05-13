/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.model;

import java.io.IOException;
import java.io.OutputStream;
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
import net.ownhero.dev.ioda.FileUtils;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Entity
public class Class2Bugs implements Annotated, Displayable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5780165055568852588L;
	RCSFile                   file;
	
	Set<Report>               reports;
	
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
	 * @return
	 */
	public static List<Class2Bugs> getBugCounts() {
		List<Class2Bugs> ret = new LinkedList<Class2Bugs>();
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
						ret.add(new Class2Bugs(file, reports));
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
				ret.add(new Class2Bugs(file, reports));
				reports.clear();
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
	public Class2Bugs(final RCSFile file, final Set<Report> reports) {
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
		if (!(obj instanceof Class2Bugs)) {
			return false;
		}
		Class2Bugs other = (Class2Bugs) obj;
		if (this.file == null) {
			if (other.file != null) {
				return false;
			}
		} else if (!this.file.equals(other.file)) {
			return false;
		}
		return true;
	}
	
	/**
	 * @return the file
	 */
	@OneToOne (cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.LAZY)
	public RCSFile getFile() {
		return this.file;
	}
	
	/**
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
		result = prime * result + ((this.file == null)
		                                              ? 0
		                                              : this.file.hashCode());
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
		StringBuilder builder = new StringBuilder();
		builder.append(this.file.getLatestPath()).append(",");
		builder.append(getReports().size()).append(",");
		StringBuilder b = new StringBuilder();
		for (Report report : getReports()) {
			if (b.length() > 0) {
				b.append(" ");
			}
			b.append(report.getId());
		}
		builder.append(b).append(FileUtils.lineSeparator);
		return builder.toString();
	}
	
	@Override
	public void toCSV(final OutputStream stream) throws IOException {
		stream.write(toCSV().getBytes());
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
		builder.append(this.file.getGeneratedId());
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
