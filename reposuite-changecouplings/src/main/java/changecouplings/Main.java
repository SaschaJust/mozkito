/**
 * 
 */
package changecouplings;

import de.unisaarland.cs.st.reposuite.changecouplings.ChangeCouplings;
import net.ownhero.dev.kisa.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class Main {
	
	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		try {
			ChangeCouplings changeCouplings = new ChangeCouplings();
			changeCouplings.setName(changeCouplings.getClass().getSimpleName());
			changeCouplings.start();
			changeCouplings.join();
			if (Logger.logInfo()) {
				Logger.info("ChangeCouplings.Main: All done. cerio!");
			}
		} catch (InterruptedException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new RuntimeException();
		}
	}
	
}
