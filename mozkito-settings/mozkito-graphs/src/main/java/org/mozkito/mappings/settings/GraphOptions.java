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

package org.mozkito.mappings.settings;

import java.io.File;
import java.util.Map;

import com.tinkerpop.blueprints.Graph;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;

import org.mozkito.mappings.utils.graph.GraphManager;
import org.mozkito.mappings.utils.graph.GraphManager.GraphEnvironment;
import org.mozkito.mappings.utils.graph.GraphManager.GraphType;

/**
 * The Class GraphOptions.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class GraphOptions extends ArgumentSetOptions<Graph, ArgumentSet<Graph, GraphOptions>> {
	
	/** The Constant TAG. */
	private static final String TAG         = "graph";
	
	/** The Constant DESCRIPTION. */
	private static final String DESCRIPTION = "Graph database settings";
	
	/**
	 * Instantiates a new graph options.
	 * 
	 * @param argumentSet
	 *            the argument set
	 * @param requirements
	 *            the required
	 * @param name
	 *            the name
	 */
	public GraphOptions(final ArgumentSet<?, ?> argumentSet, final Requirement requirements, final String name) {
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
	public Graph init() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return GraphManager.createUtil(new GraphEnvironment(GraphType.TITAN_DB, new File(new File("."), "database")));
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
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'requirements' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
}
