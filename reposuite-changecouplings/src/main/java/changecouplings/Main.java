/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
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
