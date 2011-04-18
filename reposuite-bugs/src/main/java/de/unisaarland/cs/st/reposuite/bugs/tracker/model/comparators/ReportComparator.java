/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.model.comparators;

import java.util.Comparator;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class ReportComparator implements Comparator<Report> {
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(final Report o1,
	                   final Report o2) {
		return o1.compareTo(o2);
	}
	
}
