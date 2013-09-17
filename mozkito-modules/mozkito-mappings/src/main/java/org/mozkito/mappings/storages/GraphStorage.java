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

package org.mozkito.mappings.storages;

import com.tinkerpop.blueprints.Graph;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class GraphStorage extends Storage {
	
	private Graph graph = null;
	
	/**
	 * Instantiates a new graph storage.
	 * 
	 * @param graph
	 *            the graph
	 */
	public GraphStorage(@NotNull final Graph graph) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// body
			this.graph = graph;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.mappings.register.Node#getDescription()
	 */
	@Override
	public String getDescription() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'getDescription' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * @return the graph
	 */
	public final Graph getGraph() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.graph;
		} finally {
			POSTCONDITIONS: {
				Condition.notNull(this.graph, "Field '%s' in '%s'.", "graph", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
}
