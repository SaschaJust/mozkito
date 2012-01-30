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

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.settings.AndamaArgumentSet;
import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.settings.DirectoryArgument;
import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.utils.ChangeGenealogyUtils;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.settings.DatabaseArguments;

public class GenealogyArguments extends AndamaArgumentSet<CoreChangeGenealogy> {
	
	private DirectoryArgument graphDBArg;
	private DatabaseArguments dbArgs;
	
	public GenealogyArguments(AndamaSettings settings, boolean isRequired, String unit) {
		super();
		graphDBArg = new DirectoryArgument(
		                                   settings,
		                                   "genealogy.graphdb",
		                                   "Directory in which to store the GraphDB (if exists, load graphDB from this dir)",
		                                   null, true, true);
		dbArgs = new DatabaseArguments(settings, isRequired, unit);
	}
	
	@Override
	public CoreChangeGenealogy getValue() {
		
		PersistenceUtil persistenceUtil = this.dbArgs.getValue();
		if (persistenceUtil == null) {
			throw new UnrecoverableError("Could not connect to database!");
		}
		return ChangeGenealogyUtils.readFromDB(graphDBArg.getValue(), persistenceUtil);
	}
	
}
