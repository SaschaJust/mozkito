/**
 * 
 */
package callgraph;

import de.unisaarland.cs.st.reposuite.callgraph.CallGraphToolChain;
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
			CallGraphToolChain toolChain = new CallGraphToolChain();
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
