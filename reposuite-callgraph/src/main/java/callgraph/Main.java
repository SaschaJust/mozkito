/**
 * 
 */
package callgraph;

import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.callgraph.CallGraphToolChain;

/**
 * @author just
 * 
 */
public class Main {
	
	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		CallGraphToolChain toolChain = new CallGraphToolChain();
		// toolChain.setName(toolChain.getClass().getSimpleName());
		// toolChain.start();
		// toolChain.join();
		toolChain.run();
		if (Logger.logInfo()) {
			Logger.info("PPA.Main: All done. cerio!");
		}
	}
}
