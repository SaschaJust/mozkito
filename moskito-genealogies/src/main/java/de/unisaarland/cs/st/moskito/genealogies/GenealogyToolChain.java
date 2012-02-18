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

import net.ownhero.dev.andama.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.model.Chain;
import net.ownhero.dev.andama.model.Pool;
import net.ownhero.dev.andama.settings.arguments.BooleanArgument;
import net.ownhero.dev.andama.settings.arguments.LoggerArguments;
import net.ownhero.dev.andama.settings.requirements.Requirement;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.settings.GenealogyArguments;
import de.unisaarland.cs.st.moskito.genealogies.settings.GenealogySettings;
import de.unisaarland.cs.st.moskito.rcs.BranchFactory;

public class GenealogyToolChain extends Chain<GenealogySettings> {
	
	private final Pool               threadPool;
	private final GenealogyArguments genealogyArgs;
	private CoreChangeGenealogy      genealogy;
	private final BooleanArgument    infoArg;
	private LoggerArguments          loggerArg;
	
	public GenealogyToolChain() {
		super(new GenealogySettings());
		
		this.threadPool = new Pool(GenealogyToolChain.class.getSimpleName(), this);
		final GenealogySettings settings = getSettings();
		try {
			this.loggerArg = settings.setLoggerArg(Requirement.optional);
			this.infoArg = new BooleanArgument(settings.getRootArgumentSet(), "genealogyInfoOnly",
			                                   "Only prints standard genealogy infos", "false", Requirement.required);
			
			this.genealogyArgs = settings.setGenealogyArgs(Requirement.required);
		} catch (final ArgumentRegistrationException e) {
			throw new Shutdown(e.getMessage(), e);
		}
		
	}
	
	@Override
	public void setup() {
		this.loggerArg.getValue();
		if (this.infoArg.getValue()) {
			
			this.genealogy = this.genealogyArgs.getValue();
			if (Logger.logInfo()) {
				Logger.info("Statistic on change genealogy graph:");
				Logger.info("Number of vertices: " + this.genealogy.vertexSize());
				Logger.info("Number of edges: " + this.genealogy.edgeSize());
				
				Logger.info("Statistic on change genealogy transaction layer:");
				Logger.info("Number of vertices: " + this.genealogy.getTransactionLayer().vertexSize());
				Logger.info("Number of edges: " + this.genealogy.getTransactionLayer().edgeSize());
			}
		} else {
			this.genealogy = this.genealogyArgs.getValue();
			
			final BranchFactory branchFactory = new BranchFactory(this.genealogy.getPersistenceUtil());
			
			new ChangeOperationReader(this.threadPool.getThreadGroup(), getSettings(), branchFactory);
			new GenealogyNodePersister(this.threadPool.getThreadGroup(), getSettings(), this.genealogy);
			new GenealogyDependencyPersister(this.threadPool.getThreadGroup(), getSettings(), this.genealogy);
		}
	}
}
