/***********************************************************************************************************************
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
 **********************************************************************************************************************/
package org.mozkito.genealogies.settings;

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

import org.mozkito.genealogies.core.CoreChangeGenealogy;
import org.mozkito.genealogies.utils.ChangeGenealogyUtils;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.settings.DatabaseOptions;

/**
 * The Class GenealogyOptions.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class GenealogyOptions extends
        ArgumentSetOptions<CoreChangeGenealogy, ArgumentSet<CoreChangeGenealogy, GenealogyOptions>> {
	
	/** The database options. */
	private final DatabaseOptions databaseOptions;
	
	/** The graph db option. */
	private Options               graphDbOption;
	
	/** The persistence util. */
	private PersistenceUtil       persistenceUtil;
	
	/**
	 * Instantiates a new genealogy options.
	 * 
	 * @param argumentSet
	 *            the argument set
	 * @param requirements
	 *            the requirements
	 * @param databaseOptions
	 *            the database options
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
