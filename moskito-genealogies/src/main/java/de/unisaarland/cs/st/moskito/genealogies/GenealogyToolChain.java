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
import net.ownhero.dev.andama.exceptions.SettingsParseError;
import net.ownhero.dev.andama.model.Chain;
import net.ownhero.dev.andama.model.Pool;
import net.ownhero.dev.andama.settings.Settings;
import net.ownhero.dev.andama.settings.arguments.LoggerArguments;
import net.ownhero.dev.andama.settings.requirements.Requirement;
import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.settings.GenealogyArguments;
import de.unisaarland.cs.st.moskito.genealogies.settings.GenealogySettings;
import de.unisaarland.cs.st.moskito.rcs.BranchFactory;

public class GenealogyToolChain extends Chain<Settings> {
	
	private final Pool               threadPool;
	private final GenealogyArguments genealogyArgs;
	private final LoggerArguments    loggerArg;
	
	public GenealogyToolChain() throws ArgumentRegistrationException, SettingsParseError {
		super(new GenealogySettings());
		
		this.threadPool = new Pool(GenealogyToolChain.class.getSimpleName(), this);
		final GenealogySettings settings = (GenealogySettings) getSettings();
		this.loggerArg = settings.setLoggerArg(Requirement.required);
		this.genealogyArgs = settings.setGenealogyArgs(Requirement.required);
	}
	
	@Override
	public void setup() {
		this.loggerArg.getValue();
		final CoreChangeGenealogy genealogy = this.genealogyArgs.getValue();
		
		final BranchFactory branchFactory = new BranchFactory(genealogy.getPersistenceUtil());
		
		new ChangeOperationReader(this.threadPool.getThreadGroup(), getSettings(), branchFactory);
		new GenealogyNodePersister(this.threadPool.getThreadGroup(), getSettings(), genealogy);
		new GenealogyDependencyPersister(this.threadPool.getThreadGroup(), getSettings(), genealogy);
		
	}
}
