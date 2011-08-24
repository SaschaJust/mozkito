/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping;

import java.util.List;

import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.persistence.Criteria;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteSourceThread;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class ScoringReader extends RepoSuiteSourceThread<RCSTransaction> {
	
	private final PersistenceUtil persistenceUtil;
	
	/**
	 * @param threadGroup
	 * @param name
	 * @param settings
	 * @param persistenceUtil
	 */
	public ScoringReader(final RepoSuiteThreadGroup threadGroup, final RepoSuiteSettings settings,
	        final PersistenceUtil persistenceUtil) {
		super(threadGroup, ScoringReader.class.getSimpleName(), settings);
		this.persistenceUtil = persistenceUtil;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		try {
			
			if (!checkConnections() || !checkNotShutdown()) {
				return;
			}
			
			if (Logger.logInfo()) {
				Logger.info("Starting " + getHandle());
			}
			
			Criteria<RCSTransaction> criteria = this.persistenceUtil.createCriteria(RCSTransaction.class);
			List<RCSTransaction> list = this.persistenceUtil.load(criteria);
			
			for (RCSTransaction transaction : list) {
				if (Logger.logDebug()) {
					Logger.debug("Providing " + transaction.getId() + ".");
				}
				
				write(transaction);
			}
			
			finish();
		} catch (Exception e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			shutdown();
		}
	}
	
}
