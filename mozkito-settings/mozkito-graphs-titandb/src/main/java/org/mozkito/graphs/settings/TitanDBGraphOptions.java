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

package org.mozkito.graphs.settings;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.DirectoryArgument;
import net.ownhero.dev.hiari.settings.EnumArgument;
import net.ownhero.dev.hiari.settings.EnumArgument.Options;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;

import org.mozkito.graphs.DatabaseBackend;
import org.mozkito.graphs.GraphManager;
import org.mozkito.graphs.GraphType;
import org.mozkito.graphs.SearchBackend;
import org.mozkito.graphs.TitanDBGraphManager;

/**
 * The Class GraphOptions.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class TitanDBGraphOptions extends
        ArgumentSetOptions<GraphManager, ArgumentSet<GraphManager, TitanDBGraphOptions>> {
	
	/** The Constant TAG. */
	private static final String             TAG         = "titandb";
	
	/** The Constant DESCRIPTION. */
	private static final String             DESCRIPTION = "TitanDB graph database settings";
	
	private EnumArgument.Options<GraphType> graphTypeOptions;
	
	private DirectoryArgument.Options       directoryOptions;
	
	private EnumArgument<GraphType>         graphTypeArgument;
	
	private DirectoryArgument               directoryArgument;
	
	private Options<SearchBackend>          searchBackendOptions;
	
	private Options<DatabaseBackend>        databaseBackendOptions;
	
	/**
	 * Instantiates a new graph options.
	 * 
	 * @param argumentSet
	 *            the argument set
	 * @param requirements
	 *            the required
	 */
	public TitanDBGraphOptions(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
		super(argumentSet, TAG, DESCRIPTION, requirements);
		
		PRECONDITIONS: {
			// none
		}
		
		try {
			// body
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
	 */
	@Override
	public GraphManager init() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.graphTypeArgument = getSettings().getArgument(this.graphTypeOptions);
			this.graphTypeArgument.getValue();
			this.directoryArgument = getSettings().getArgument(this.directoryOptions);
			final File directory = this.directoryArgument.getValue();
			
			return new TitanDBGraphManager(directory);
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#requirements(net.ownhero.dev.hiari.settings.ArgumentSet)
	 */
	@Override
	public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> argumentSet) throws ArgumentRegistrationException,
	                                                                                    SettingsParseError {
		PRECONDITIONS: {
			// none
		}
		
		try {
			final Map<String, IOptions<?, ?>> map = new HashMap<>();
			
			this.directoryOptions = new DirectoryArgument.Options(argumentSet, "directory",
			                                                      "Used for file database backends like BerkeleyDB",
			                                                      null, Requirement.optional, false);
			map.put(this.directoryOptions.getName(), this.directoryOptions);
			
			this.searchBackendOptions = new EnumArgument.Options<SearchBackend>(argumentSet, "search",
			                                                                    "Graph database search index backend",
			                                                                    SearchBackend.LUCENE,
			                                                                    Requirement.optional);
			map.put(this.searchBackendOptions.getName(), this.searchBackendOptions);
			
			this.databaseBackendOptions = new EnumArgument.Options<DatabaseBackend>(argumentSet, "backend",
			                                                                        "Graph database backend",
			                                                                        DatabaseBackend.BERKELEYDB,
			                                                                        Requirement.optional);
			map.put(this.databaseBackendOptions.getName(), this.databaseBackendOptions);
			
			return map;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
}
