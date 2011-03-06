/**
 * 
 */
package PPA;

import de.unisaarland.cs.st.reposuite.ppa.PPAToolChain;
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
			PPAToolChain toolChain = new PPAToolChain();
			toolChain.setName(toolChain.getClass().getSimpleName());
			toolChain.start();
			toolChain.join();
			if (Logger.logInfo()) {
				Logger.info("PPA.Main: All done. cerio!");
			}
		} catch (InterruptedException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new RuntimeException();
		}
		//		ReposuiteDeltaInfo core = new ReposuiteDeltaInfo();
		//		core.run();
	}
}
