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
	
	RCSTransactionTopologicalComparator other = new RCSTransactionTopologicalComparator();
	
	@Override
	public int compare(final RCSTransaction o1,
	                   final RCSTransaction o2) {
		try {
			Integer id1 = Integer.parseInt(o1.getOriginalId());
			Integer id2 = Integer.parseInt(o2.getOriginalId());
			
			int retval = id1.compareTo(id2);
			int otherval = this.other.compare(o1, o2);
			if (retval != otherval) {
				System.err.println("COMPERROR: " + o1.getId() + "/" + o1.getOriginalId() + " vs " + o2.getId() + "/"
				        + o2.getOriginalId() + " TOPO:" + otherval + " ORIGINALID:" + retval);
			}
			return retval;
		} catch (NumberFormatException e) {
			throw new UnrecoverableError("This comparator is not valid for non-integer original ids.", e);
		}
	}
}
