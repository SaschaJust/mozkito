/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.bugzilla;

import java.net.URI;

import org.dom4j.Document;

import de.unisaarland.cs.st.reposuite.bugs.tracker.DocumentIterator;
import de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.BugReport;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class BugzillaTracker extends Tracker {
	
	@Override
	public boolean checkRAW(final String rawString) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean checkXML(final Document second) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public Document createDocument(final String second) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public DocumentIterator iterator() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public BugReport parse(final Document document) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void setup(final URI uri, final String baseUrl, final String filter, final String username,
	        final String password, final String startAt, final String stopAt) {
		// TODO Auto-generated method stub
		super.setup(uri, baseUrl, filter, username, password, startAt, stopAt);
	}
}
