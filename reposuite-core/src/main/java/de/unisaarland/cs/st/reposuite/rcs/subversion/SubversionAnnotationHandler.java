/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.subversion;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.ISVNAnnotateHandler;

import de.unisaarland.cs.st.reposuite.rcs.AnnotationEntry;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class SubversionAnnotationHandler implements ISVNAnnotateHandler {
	
	private final List<AnnotationEntry> list = new LinkedList<AnnotationEntry>();
	
	/**
	 * @return the resulting list of {@link AnnotationEntry}s
	 */
	public List<AnnotationEntry> getResults() {
		return this.list;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tmatesoft.svn.core.wc.ISVNAnnotateHandler#handleEOF()
	 */
	@Override
	public void handleEOF() {
		// nothing to do here		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tmatesoft.svn.core.wc.ISVNAnnotateHandler#handleLine(java.util.Date,
	 * long, java.lang.String, java.lang.String)
	 */
	@Override
	public void handleLine(Date date, long revision, String author, String line) throws SVNException {
		this.list.add(new AnnotationEntry(revision + "", author, new DateTime(date), line));
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tmatesoft.svn.core.wc.ISVNAnnotateHandler#handleLine(java.util.Date,
	 * long, java.lang.String, java.lang.String, java.util.Date, long,
	 * java.lang.String, java.lang.String, int)
	 */
	@Override
	public void handleLine(Date date, long revision, String author, String line, Date mergedDate, long mergedRevision,
	        String mergedAuthor, String mergedPath, int lineNumber) throws SVNException {
		if (revision > mergedRevision) {
			this.list.add(new AnnotationEntry(revision + "", author, new DateTime(date), line));
		} else {
			this.list.add(new AnnotationEntry(mergedRevision + "", mergedAuthor, new DateTime(mergedDate), line,
			        mergedPath));
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tmatesoft.svn.core.wc.ISVNAnnotateHandler#handleRevision(java.util
	 * .Date, long, java.lang.String, java.io.File)
	 */
	@Override
	public boolean handleRevision(Date date, long revision, String author, File contents) throws SVNException {
		return false;
	}
}
