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
