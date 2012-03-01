/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.moskito;

import net.ownhero.dev.andama.model.Chain;
import net.ownhero.dev.andama.model.Pool;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.registerable.ArgumentRegistrationException;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.rcs.BranchFactory;
import de.unisaarland.cs.st.moskito.rcs.Repository;
import de.unisaarland.cs.st.moskito.settings.RepositorySettings;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class Graph extends Chain<RepositorySettings> {
	
	private final Pool            threadPool;
	private final PersistenceUtil persistenceUtil;
	private final Repository      repository;
	private final BranchFactory   branchFactory;
	
	/**
	 * @param settings
	 * @throws ArgumentRegistrationException
	 * @throws SettingsParseError
	 */
	public Graph(final RepositorySettings settings, final PersistenceUtil persistenceUtil, final Repository repository) {
		super(settings);
		this.threadPool = new Pool(RepositoryToolchain.class.getSimpleName(), this);
		this.persistenceUtil = persistenceUtil;
		this.repository = repository;
		this.branchFactory = new BranchFactory(persistenceUtil);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.toolchain.RepoSuiteToolchain#setup()
	 */
	@Override
	public void setup() {
		new GraphReader(this.threadPool.getThreadGroup(), getSettings(), this.persistenceUtil);
		new GraphBuilder(this.threadPool.getThreadGroup(), getSettings(), this.repository, this.persistenceUtil,
		                 this.branchFactory);
	}
}
