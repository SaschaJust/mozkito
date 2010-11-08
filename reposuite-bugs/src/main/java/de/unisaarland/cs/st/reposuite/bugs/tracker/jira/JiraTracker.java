/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.jira;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;

import de.unisaarland.cs.st.reposuite.bugs.exceptions.InvalidParameterException;
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
	
	private File overalXML;
	
	@Override
	public boolean checkRAW(final String rawReport) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean checkXML(final org.jdom.Document xmlReport) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public org.jdom.Document createDocument(final String rawReport) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public BugReport parse(final org.jdom.Document document) {
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
	        final String password, final Long startAt, final Long stopAt) throws InvalidParameterException {
		
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
