/**
 * 
 */
package de.unisaarland.cs.st.moskito.rcs.comparators;

import java.util.Comparator;

import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RCSTransactionTopologicalComparator implements Comparator<RCSTransaction> {
	
	@Override
	public int compare(final RCSTransaction arg0,
	                   final RCSTransaction arg1) {
		return arg0.compareTo(arg1);
	}
	
}
