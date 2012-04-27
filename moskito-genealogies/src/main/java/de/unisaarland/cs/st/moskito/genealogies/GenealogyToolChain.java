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
 *******************************************************************************/

package de.unisaarland.cs.st.moskito.genealogies;

import net.ownhero.dev.andama.model.Chain;
import net.ownhero.dev.andama.model.Pool;
import net.ownhero.dev.hiari.settings.ArgumentFactory;
import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetFactory;
import net.ownhero.dev.hiari.settings.BooleanArgument;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.settings.GenealogyOptions;
import de.unisaarland.cs.st.moskito.rcs.BranchFactory;
import de.unisaarland.cs.st.moskito.settings.DatabaseOptions;

public class GenealogyToolChain extends Chain<Settings> {
	
	private final Pool                                               threadPool;
	private final ArgumentSet<CoreChangeGenealogy, GenealogyOptions> genealogyArgs;
	private CoreChangeGenealogy                                      genealogy;
	private final BooleanArgument                                    infoArgument;
	private BooleanArgument                                          noTestArgument;
	
	public GenealogyToolChain(final Settings settings) {
		super(settings);
		
		this.threadPool = new Pool(GenealogyToolChain.class.getSimpleName(), this);
		try {
			
			this.infoArgument = ArgumentFactory.create(new BooleanArgument.Options(
			                                                                       settings.getRoot(),
			                                                                       "infoOnly",
			                                                                       "Only prints standard genealogy infos",
			                                                                       false, Requirement.required));
			
			this.noTestArgument = ArgumentFactory.create(new BooleanArgument.Options(
			                                                                         settings.getRoot(),
			                                                                         "ignoreTests",
			                                                                         "Set to FALSE if you want to include test cases into the genealogy graph.",
			                                                                         true, Requirement.required));
			
			final DatabaseOptions databaseOptions = new DatabaseOptions(settings.getRoot(), Requirement.required, "ppa");
			final GenealogyOptions genealogyOptions = new GenealogyOptions(settings.getRoot(), Requirement.required,
			                                                               databaseOptions);
			this.genealogyArgs = ArgumentSetFactory.create(genealogyOptions);
			
		} catch (final ArgumentRegistrationException e) {
			throw new UnrecoverableError(e);
		} catch (final SettingsParseError e) {
			throw new UnrecoverableError(e);
		} catch (final ArgumentSetRegistrationException e) {
			throw new UnrecoverableError(e);
		} finally {
			// POSTCONDITION
		}
		
	}
	
	@Override
	public void setup() {
		if (this.infoArgument.getValue()) {
			
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
			
			new ChangeOperationReader(this.threadPool.getThreadGroup(), getSettings(), branchFactory,
			                          this.noTestArgument.getValue());
			new GenealogyNodePersister(this.threadPool.getThreadGroup(), getSettings(), this.genealogy);
			new GenealogyDependencyPersister(this.threadPool.getThreadGroup(), getSettings(), this.genealogy);
		}
	}
}
