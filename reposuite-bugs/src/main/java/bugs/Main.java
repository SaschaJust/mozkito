/**
 * 
 */
package bugs;

import net.ownhero.dev.kanuni.instrumentation.KanuniAgent;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.bugs.Bugs;
import de.unisaarland.cs.st.reposuite.exceptions.Shutdown;

/**
 * @author just
 * 
 */
public class Main {
	
	static {
		KanuniAgent.initialize();
	}
	
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
