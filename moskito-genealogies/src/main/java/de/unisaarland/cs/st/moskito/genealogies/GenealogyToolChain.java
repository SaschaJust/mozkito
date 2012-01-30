/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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

package de.unisaarland.cs.st.moskito.genealogies;

import net.ownhero.dev.andama.model.AndamaChain;
import net.ownhero.dev.andama.model.AndamaPool;
import net.ownhero.dev.andama.settings.LoggerArguments;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.settings.GenealogyArguments;
import de.unisaarland.cs.st.moskito.genealogies.settings.GenealogySettings;
import de.unisaarland.cs.st.moskito.rcs.BranchFactory;

public class GenealogyToolChain extends AndamaChain {
	
	private final AndamaPool         threadPool;
	private final GenealogyArguments genealogyArgs;
	
	public GenealogyToolChain() {
		super(new GenealogySettings());
		
		this.threadPool = new AndamaPool(GenealogyToolChain.class.getSimpleName(), this);
		final GenealogySettings settings = (GenealogySettings) getSettings();
		final LoggerArguments loggerArg = settings.setLoggerArg(false);
		loggerArg.getValue();
		this.genealogyArgs = settings.setGenealogyArgs(true);
		settings.parseArguments();
	}
	
	@Override
	public void run() {
		
		setup();
		this.threadPool.execute();
		
		if (Logger.logInfo()) {
			Logger.info("Terminating.");
		}
	}
	
	@Override
	public void setup() {
		final CoreChangeGenealogy genealogy = this.genealogyArgs.getValue();
		
		final BranchFactory branchFactory = new BranchFactory(genealogy.getPersistenceUtil());
		
		new ChangeOperationReader(this.threadPool.getThreadGroup(), getSettings(), branchFactory);
		new GenealogyNodePersister(this.threadPool.getThreadGroup(), getSettings(), genealogy);
		new GenealogyDependencyPersister(this.threadPool.getThreadGroup(), getSettings(), genealogy);
		
	}
}
