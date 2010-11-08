/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.issuezilla;

import de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.BugReport;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class IssuezillaTracker extends Tracker {
	
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
	
}
