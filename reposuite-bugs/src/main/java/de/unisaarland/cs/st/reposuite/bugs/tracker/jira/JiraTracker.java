/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.jira;

import java.io.FilenameFilter;
import java.net.URI;
import java.net.URL;

import de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class JiraTracker extends Tracker {
	
	public JiraTracker(final URI uri, final URL url, final FilenameFilter filter) {
		super(uri, url, filter);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void parse() {
		// TODO Auto-generated method stub
		
	}
	
}
