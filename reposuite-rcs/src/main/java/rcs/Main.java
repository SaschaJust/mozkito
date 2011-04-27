/**
 * 
 */
package rcs;

import net.ownhero.dev.kanuni.instrumentation.KanuniAgent;
import de.unisaarland.cs.st.reposuite.RCS;
import de.unisaarland.cs.st.reposuite.Graph;
import de.unisaarland.cs.st.reposuite.utils.Logger;

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
			RCS rCS = new RCS();
			rCS.setName(rCS.getClass().getSimpleName());
			rCS.start();
			rCS.join();
			Graph graph = new Graph();
			graph.setName(graph.getClass().getSimpleName());
			graph.start();
			graph.join();
			
			if (Logger.logInfo()) {
				Logger.info("RCS.Main: All done. cerio!");
			}
		} catch (InterruptedException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new RuntimeException();
		}
	}
	
}
