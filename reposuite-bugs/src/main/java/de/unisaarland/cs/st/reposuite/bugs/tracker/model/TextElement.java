/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.model;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.persistence.model.Person;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public interface TextElement {
	
	public Person getAuthor();
	
	public String getText();
	
	public DateTime getTimestamp();
}
