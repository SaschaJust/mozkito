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
package mapping;

import net.ownhero.dev.kanuni.instrumentation.KanuniAgent;
import de.unisaarland.cs.st.reposuite.mapping.Mapping;
import de.unisaarland.cs.st.reposuite.mapping.Scoring;
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
			Scoring scoring = new Scoring();
			scoring.setName(scoring.getClass().getSimpleName());
			scoring.start();
			scoring.join();
			
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
			throw new RuntimeException(e);
		}
	}
	
}
