/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.model.comparators;

import java.util.Comparator;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Comment;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class CommentComparator implements Comparator<Comment> {
	
	@Override
	public int compare(final Comment o1, final Comment o2) {
		return o1.compareTo(o2);
	}
	
}
