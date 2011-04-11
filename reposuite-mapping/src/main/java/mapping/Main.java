/**
 * 
 */
package mapping;

import net.ownhero.dev.kanuni.instrumentation.KanuniAgent;
import de.unisaarland.cs.st.reposuite.mapping.Mapping;
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
			Mapping mapping = new Mapping();
			mapping.setName(mapping.getClass().getSimpleName());
			mapping.start();
			mapping.join();
			if (Logger.logInfo()) {
				Logger.info("Mappings.Main: All done. cerio!");
			}
		} catch (InterruptedException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new RuntimeException();
		}
	}
	
}
