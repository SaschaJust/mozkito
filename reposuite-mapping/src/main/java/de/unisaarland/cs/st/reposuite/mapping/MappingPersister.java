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
/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaSink;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.mapping.model.PersistentMapping;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class MappingPersister extends AndamaSink<PersistentMapping> {
	
	private final PersistenceUtil persistenceUtil;
	private int                   i;
	
	/**
	 * @param threadGroup
	 * @param name
	 * @param settings
	 */
	public MappingPersister(final AndamaGroup threadGroup, final MappingSettings settings,
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
		this.persistenceUtil.shutdown();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThread#beforeExecution()
	 */
	@Override
	public void beforeExecution() {
		super.beforeExecution();
		
		this.persistenceUtil.beginTransaction();
		this.i = 0;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * net.ownhero.dev.andama.threads.OnlyInputConnectable#process(java.lang
	 * .Object)
	 */
	@Override
	public void process(final PersistentMapping mapping) throws UnrecoverableError, Shutdown {
		if (Logger.logDebug()) {
			Logger.debug("Storing " + mapping);
		}
		
		if ((++this.i % 50) == 0) {
			this.persistenceUtil.commitTransaction();
			this.persistenceUtil.beginTransaction();
		}
		
		this.persistenceUtil.save(mapping);
	}
	
}
