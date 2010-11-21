/**
 * 
 */
package bugs;

import de.unisaarland.cs.st.reposuite.bugs.Bugs;
import de.unisaarland.cs.st.reposuite.exceptions.Shutdown;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author just
 * 
 */
public class Main {
	
	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		try {
			Bugs bugs = new Bugs();
			bugs.setName(bugs.getClass().getSimpleName());
			bugs.start();
			bugs.join();
		} catch (InterruptedException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new Shutdown();
		}
	}
	
}
