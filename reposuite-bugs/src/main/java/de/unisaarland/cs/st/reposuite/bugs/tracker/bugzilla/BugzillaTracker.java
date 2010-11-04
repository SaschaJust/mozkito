/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.bugzilla;

import java.io.FilenameFilter;
import java.net.URI;
import java.net.URL;

import de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class BugzillaTracker extends Tracker {
	
	public BugzillaTracker(final URI uri, final URL url, final FilenameFilter filter) {
		super(uri, url, filter);
	}
	
	@Override
	public void parse() {
		// TODO Auto-generated method stub
		
	}
	
}
