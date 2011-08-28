/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.ppa;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaSink;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;

/**
 * The Class ChangeOperationPersister.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class ChangeOperationPersister extends AndamaSink<JavaChangeOperation> {
	
	private final PersistenceUtil persistenceUtil;
	private String                lastTransactionId;
	
	/**
	 * Instantiates a new change operation persister.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 */
	public ChangeOperationPersister(final AndamaGroup threadGroup, final AndamaSettings settings,
	        final PersistenceUtil persistenceUtil) {
		super(threadGroup, settings, false);
		this.persistenceUtil = persistenceUtil;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThread#afterExecution()
	 */
	@Override
	public void afterExecution() {
		super.afterExecution();
		
		this.persistenceUtil.commitTransaction();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThread#beforeExecution()
	 */
	@Override
	public void beforeExecution() {
		super.beforeExecution();
		
		this.persistenceUtil.beginTransaction();
		this.lastTransactionId = "";
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * net.ownhero.dev.andama.threads.OnlyInputConnectable#process(java.lang
	 * .Object)
	 */
	@Override
	public void process(final JavaChangeOperation currentOperation) throws UnrecoverableError, Shutdown {
		if (Logger.logDebug()) {
			Logger.debug("Storing " + currentOperation);
		}
		
		String currentTransactionId = currentOperation.getRevision().getTransaction().getId();
		
		if (this.lastTransactionId.equals("")) {
			this.lastTransactionId = currentTransactionId;
		}
		if (!currentTransactionId.equals(this.lastTransactionId)) {
			this.persistenceUtil.commitTransaction();
			this.lastTransactionId = currentTransactionId;
			this.persistenceUtil.beginTransaction();
		}
		this.persistenceUtil.save(currentOperation);
	}
}
