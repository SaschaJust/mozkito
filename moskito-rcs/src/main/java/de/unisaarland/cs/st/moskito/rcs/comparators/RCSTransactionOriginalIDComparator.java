/**
 * 
 */
package de.unisaarland.cs.st.moskito.rcs.comparators;

import java.util.Comparator;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RCSTransactionOriginalIDComparator implements Comparator<RCSTransaction> {
	
	@Override
	public int compare(final RCSTransaction o1,
	                   final RCSTransaction o2) {
		try {
			Integer id1 = Integer.parseInt(o1.getOriginalId());
			Integer id2 = Integer.parseInt(o2.getOriginalId());
			
			return id1.compareTo(id2);
		} catch (NumberFormatException e) {
			throw new UnrecoverableError("This comparator is not valid for non-integer original ids.", e);
		}
	}
	
}
