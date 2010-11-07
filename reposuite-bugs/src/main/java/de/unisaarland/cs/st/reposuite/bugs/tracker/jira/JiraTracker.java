/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.jira;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;

import org.dom4j.Document;

import de.unisaarland.cs.st.reposuite.bugs.tracker.DocumentIterator;
import de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.BugReport;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.Tuple;

/**
 * The Class JiraTracker.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class JiraTracker extends Tracker {
	
	private File overalXML = null;
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker#checkRAW(java.lang
	 * .String)
	 */
	@Override
	public boolean checkRAW(final String rawString) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker#checkXML(org.dom4j
	 * .Document)
	 */
	@Override
	public boolean checkXML(final Document second) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker#createDocument(java
	 * .lang.String)
	 */
	@Override
	public Document createDocument(final String second) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String fetch(final Long id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public URI getLinkFromId(final Long bugId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Long getNextId() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker#fetch()
	 */
	@Override
	public DocumentIterator iterator() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker#parse(org.dom4j.Document
	 * )
	 */
	@Override
	public BugReport parse(final Document document) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	 * The given uri can either point to an overall XML or an pattern string
	 * that contains one or multiple {@link
	 * de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker#bugIdPlaceholder}
	 * that will be replaced by a bug id while fetching bug reports. If the
	 * string contains no such place holder, the uri will be considered to point
	 * to an overall XML file
	 * @see
	 * de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker#setup(java.net.URI,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void setup(final URI fetchURI, final URI overviewURI, final String pattern, final String username,
			final String password, final Long startAt, final Long stopAt) {
		
		super.setup(fetchURI, overviewURI, pattern, username, password, startAt, stopAt);
		if (!Tracker.bugIdRegex.matches(fetchURI.toString())) {
			try {
				Tuple<String, String> fetchSource = this.fetchSource(fetchURI);
				if (!fetchSource.getFirst().equals("XML")) {
					if (Logger.logError()) {
						Logger.error("Expected overall Jira bug file in XML format. Got format: "
								+ fetchSource.getFirst());
					}
					return;
				}
				this.overalXML = FileUtils.createRandomFile();
				FileOutputStream writer = new FileOutputStream(this.overalXML);
				writer.write(fetchSource.getSecond().getBytes());
				writer.flush();
				writer.close();
			} catch (Exception e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				return;
			}
		}
	}
	
}
