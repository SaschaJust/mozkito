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
import net.ownhero.dev.andama.settings.BooleanArgument;
import net.ownhero.dev.andama.settings.LoggerArguments;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.settings.GenealogyArguments;
import de.unisaarland.cs.st.moskito.genealogies.settings.GenealogySettings;
import de.unisaarland.cs.st.moskito.rcs.BranchFactory;

public class GenealogyToolChain extends AndamaChain {
	
	private final AndamaPool         threadPool;
	private final GenealogyArguments genealogyArgs;
	private CoreChangeGenealogy      genealogy;
	private final BooleanArgument    infoArg;
	
	public GenealogyToolChain() {
		super(new GenealogySettings());
		
		this.threadPool = new AndamaPool(GenealogyToolChain.class.getSimpleName(), this);
		final GenealogySettings settings = (GenealogySettings) getSettings();
		final LoggerArguments loggerArg = settings.setLoggerArg(false);
		
		this.infoArg = new BooleanArgument(settings, "genealogyInfoOnly", "Only prints standard genealogy infos",
		                                   "false", false);
		
		loggerArg.getValue();
		this.genealogyArgs = settings.setGenealogyArgs(true);
		settings.parseArguments();
	}
	
	@Override
	public void run() {
		
		if (this.infoArg.getValue()) {
			
			this.genealogy = this.genealogyArgs.getValue();
			if (Logger.logInfo()) {
				Logger.info("Statistic on change genealogy graph:");
				Logger.info("Number of vertices: " + this.genealogy.vertexSize());
				Logger.info("Number of edges: " + this.genealogy.edgeSize());
			}
			
			return;
		}
		setup();
		this.threadPool.execute();
		
		if (Logger.logInfo()) {
			Logger.info("Terminating.");
		}
		this.genealogy.getTransactionLayer().close();
		this.genealogy.close();
	}
	
	@Override
	public void setup() {
		this.genealogy = this.genealogyArgs.getValue();
		
		final BranchFactory branchFactory = new BranchFactory(this.genealogy.getPersistenceUtil());
		
		new ChangeOperationReader(this.threadPool.getThreadGroup(), getSettings(), branchFactory);
		new GenealogyNodePersister(this.threadPool.getThreadGroup(), getSettings(), this.genealogy);
		new GenealogyDependencyPersister(this.threadPool.getThreadGroup(), getSettings(), this.genealogy);
		
	}
}
