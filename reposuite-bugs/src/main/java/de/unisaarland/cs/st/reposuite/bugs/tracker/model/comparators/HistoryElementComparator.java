/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.model.comparators;

import java.util.Comparator;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.HistoryElement;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class HistoryElementComparator implements Comparator<HistoryElement> {
	
	@Override
	public int compare(final HistoryElement arg0, final HistoryElement arg1) {
		return arg0.compareTo(arg1);
	}
	
}
