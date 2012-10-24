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
package de.unisaarland.cs.st.mozkito.genealogies.settings;

import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.DirectoryArgument;
import net.ownhero.dev.hiari.settings.DirectoryArgument.Options;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import de.unisaarland.cs.st.mozkito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.mozkito.genealogies.utils.ChangeGenealogyUtils;
import de.unisaarland.cs.st.mozkito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.mozkito.settings.DatabaseOptions;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class GenealogyOptions extends
        ArgumentSetOptions<CoreChangeGenealogy, ArgumentSet<CoreChangeGenealogy, GenealogyOptions>> {
	
	private final DatabaseOptions databaseOptions;
	private Options               graphDbOption;
	private PersistenceUtil       persistenceUtil;
	
	/**
	 * @param argumentSet
	 * @param name
	 * @param description
	 * @param requirements
	 */
	public GenealogyOptions(final ArgumentSet<?, ?> argumentSet, final Requirement requirements,
	        final DatabaseOptions databaseOptions) {
		super(argumentSet, "genealogy", "Options used to setup the genealogy connection.", requirements);
		this.databaseOptions = databaseOptions;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
	 */
	@Override
	public CoreChangeGenealogy init() {
		// PRECONDITIONS
		
		try {
			final DirectoryArgument graphDbArgument = getSettings().getArgument(this.graphDbOption);
			this.persistenceUtil = getSettings().getArgumentSet(this.databaseOptions).getValue();
			
			return ChangeGenealogyUtils.readFromDB(graphDbArgument.getValue(), this.persistenceUtil);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#requirements(net.ownhero.dev.hiari.settings.ArgumentSet)
	 */
	@Override
	public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> set) throws ArgumentRegistrationException,
	                                                                            SettingsParseError {
		// PRECONDITIONS
		
		try {
			final Map<String, IOptions<?, ?>> map = new HashMap<String, IOptions<?, ?>>();
			
			this.graphDbOption = new DirectoryArgument.Options(
			                                                   set,
			                                                   "graphdb",
			                                                   "Directory in which to store the GraphDB (if exists, load graphDB from this dir)",
			                                                   null, Requirement.required, true);
			
			map.put(this.graphDbOption.getName(), this.graphDbOption);
			map.put(this.databaseOptions.getName(), this.databaseOptions);
			
			return map;
		} finally {
			// POSTCONDITIONS
		}
	}
	
}
