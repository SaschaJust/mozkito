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
package ppa;

import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.ppa.PPAToolChain;

/**
 * @author just
 * 
 */
public class Main {
	
	// static {
	// KanuniAgent.initialize();
	// }
	
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
		// ReposuiteDeltaInfo core = new ReposuiteDeltaInfo();
		// core.run();
	}
}
