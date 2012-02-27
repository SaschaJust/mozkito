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

package de.unisaarland.cs.st.moskito.genealogies.settings;

import net.ownhero.dev.andama.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.settings.ArgumentSet;
import net.ownhero.dev.andama.settings.arguments.DirectoryArgument;
import net.ownhero.dev.andama.settings.requirements.Requirement;
import net.ownhero.dev.kanuni.conditions.Condition;
import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.utils.ChangeGenealogyUtils;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.settings.DatabaseArguments;

public class GenealogyArguments extends ArgumentSet<CoreChangeGenealogy> {
	
	private final DirectoryArgument graphDBArg;
	private final DatabaseArguments dbArgs;
	
	/**
	 * @param argumentSet
	 * @param requirement
	 * @param unit
	 * @throws ArgumentRegistrationException
	 */
	public GenealogyArguments(final ArgumentSet<?> argumentSet, final Requirement requirement, final String unit)
	        throws ArgumentRegistrationException {
		super(argumentSet, "GenealogyArguments", requirement);
		
		this.graphDBArg = new DirectoryArgument(
		                                        this,
		                                        "genealogy.graphdb",
		                                        "Directory in which to store the GraphDB (if exists, load graphDB from this dir)",
		                                        null, Requirement.required, true);
		this.dbArgs = new DatabaseArguments(argumentSet, requirement, unit);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.ArgumentSet#init()
	 */
	@Override
	protected boolean init() {
		boolean ret = false;
		
		try {
			if (!isInitialized()) {
				synchronized (this) {
					if (!isInitialized()) {
						final PersistenceUtil persistenceUtil = this.dbArgs.getValue();
						if (persistenceUtil == null) {
							throw new UnrecoverableError("Could not connect to database!");
						}
						setCachedValue(ChangeGenealogyUtils.readFromDB(this.graphDBArg.getValue(), persistenceUtil));
						ret = true;
					} else {
						ret = true;
					}
				}
			} else {
				ret = true;
			}
			
			return ret;
		} finally {
			if (ret) {
				Condition.check(isInitialized(), "If init() returns true, the %s has to be set to initialized.",
				                getHandle());
			}
		}
	}
}
