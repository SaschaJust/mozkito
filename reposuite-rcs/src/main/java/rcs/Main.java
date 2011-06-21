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
package rcs;

import net.ownhero.dev.kanuni.instrumentation.KanuniAgent;
import de.unisaarland.cs.st.reposuite.RCS;
import de.unisaarland.cs.st.reposuite.Graph;
import net.ownhero.dev.kisa.Logger;

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
