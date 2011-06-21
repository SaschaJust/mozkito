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
package de.unisaarland.cs.st.reposuite;

import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.settings.RepositorySettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteSinkThread;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;
import net.ownhero.dev.kisa.Logger;

/**
 * This class is a end point for the {@link RCS} tool chain in case no database
 * connection is used. The data received from the previous node is void sinked.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepositoryVoidSink extends RepoSuiteSinkThread<RCSTransaction> {
	
	/**
	 * @see RepoSuiteSinkThread
	 * @param threadGroup
	 * @param settings
	 */
	public RepositoryVoidSink(final RepoSuiteThreadGroup threadGroup, final RepositorySettings settings) {
		super(threadGroup, RepositoryVoidSink.class.getSimpleName(), settings);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		if (!checkConnections() || !checkNotShutdown()) {
			return;
		}
		
		RCSTransaction currentTransaction;
		try {
			while (!isShutdown() && ((currentTransaction = read()) != null)) {
				
				if (Logger.logDebug()) {
					Logger.debug("Taking " + currentTransaction + " from input connector and forgetting it.");
				}
				
			}
			
			finish();
		} catch (InterruptedException e) {
			
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			shutdown();
		}
	}
	
}
