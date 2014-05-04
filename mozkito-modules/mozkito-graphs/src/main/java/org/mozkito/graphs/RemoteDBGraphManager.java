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

package org.mozkito.graphs;

import com.tinkerpop.blueprints.KeyIndexableGraph;

import org.mozkito.database.DatabaseEnvironment;

/**
 * The Class RemoteDBGraphManager.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public abstract class RemoteDBGraphManager extends GraphManager {
	
	/** The database environment. */
	private final DatabaseEnvironment databaseEnvironment = null;
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.graphs.GraphManager#createIndex(org.mozkito.graphs.GraphIndex)
	 */
	@Override
	public abstract void createIndex(final GraphIndex graphIndex);
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.graphs.GraphManager#createUtil()
	 */
	@Override
	public abstract KeyIndexableGraph createUtil();
	
	/**
	 * Gets the database environment if any.
	 * 
	 * @return the database environment or null if the underlying database is file-based.
	 */
	public DatabaseEnvironment getDatabaseEnvironment() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.databaseEnvironment;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.graphs.GraphManager#getGraph()
	 */
	@Override
	public abstract <T extends KeyIndexableGraph> T getGraph();
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.graphs.GraphManager#isFileBased()
	 */
	@Override
	public boolean isFileBased() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return false;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.graphs.GraphManager#isLocal()
	 */
	@Override
	public abstract boolean isLocal();
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.graphs.GraphManager#provides()
	 */
	@Override
	public abstract GraphType provides();
	
}
