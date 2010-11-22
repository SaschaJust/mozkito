/**
 * 
 */
package core;

import de.unisaarland.cs.st.reposuite.Core;
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
			Core core = new Core();
			core.setName(core.getClass().getSimpleName());
			core.start();
			core.join();
		} catch (InterruptedException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new RuntimeException();
		}
	}
	
}
