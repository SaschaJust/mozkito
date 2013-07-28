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

package org.mozkito.mappings.utils.graph;

import static com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration.INDEX_BACKEND_KEY;
import static com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration.STORAGE_DIRECTORY_KEY;

import java.io.File;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration;
import com.tinkerpop.blueprints.KeyIndexableGraph;

/**
 * The Class GraphManager.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class GraphManager {
	
	/**
	 * The Class GraphEnvironment.
	 * 
	 * @author Sascha Just <sascha.just@mozkito.org>
	 */
	public static class GraphEnvironment {
		
		/** The type. */
		private final GraphType type;
		
		/** The remote. */
		private final boolean   remote;
		
		/** The directory. */
		private File            directory = null;
		
		/**
		 * Instantiates a new graph environment.
		 * 
		 * @param type
		 *            the type
		 * @param directory
		 *            the directory
		 */
		public GraphEnvironment(@NotNull final GraphType type, @NotNull final File directory) {
			this.remote = false;
			this.directory = directory;
			this.type = type;
		}
		
		/**
		 * Gets the directory.
		 * 
		 * @return the directory
		 */
		public final File getDirectory() {
			PRECONDITIONS: {
				// none
			}
			
			try {
				return this.directory;
			} finally {
				POSTCONDITIONS: {
					// none
				}
			}
		}
		
		/**
		 * Gets the type.
		 * 
		 * @return the type
		 */
		public final GraphType getType() {
			PRECONDITIONS: {
				// none
			}
			
			try {
				return this.type;
			} finally {
				POSTCONDITIONS: {
					Condition.notNull(this.type, "Field '%s' in '%s'.", "type", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
		
		/**
		 * Checks if is remote.
		 * 
		 * @return the remote
		 */
		public final boolean isRemote() {
			PRECONDITIONS: {
				// none
			}
			
			try {
				return this.remote;
			} finally {
				POSTCONDITIONS: {
					Condition.notNull(this.remote, "Field '%s' in '%s'.", "remote", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
		
	}
	
	/**
	 * The Enum GraphType.
	 */
	public static enum GraphType {
		
		/** The titan db. */
		TITAN_DB,
		/** The NE o4 j. */
		NEO4J,
		/** The jung. */
		JUNG;
	}
	
	/** The Constant INDEX_NAME. */
	public static final String INDEX_NAME = "search";
	
	/**
	 * Creates the titan db graph.
	 * 
	 * @param environment
	 *            the environment
	 * @return the titan graph
	 */
	public static final TitanGraph createTitanDBGraph(@NotNull final GraphEnvironment environment) {
		PRECONDITIONS: {
			Condition.notNull(environment, "Argument '%s' in '%s'.", "environment", GraphManager.class.getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			if (!GraphType.TITAN_DB.equals(environment.getType())) {
				// TODO error
				throw new RuntimeException();
			}
			
			if (environment.isRemote()) {
				// TODO error
				throw new RuntimeException();
			}
		}
		
		final BaseConfiguration config = new BaseConfiguration();
		final Configuration storage = config.subset(GraphDatabaseConfiguration.STORAGE_NAMESPACE);
		// configuring local backend
		storage.setProperty(GraphDatabaseConfiguration.STORAGE_BACKEND_KEY, "local");
		storage.setProperty(GraphDatabaseConfiguration.STORAGE_DIRECTORY_KEY, environment.getDirectory()
		                                                                                 .getAbsolutePath());
		// configuring elastic search index
		final Configuration index = storage.subset(GraphDatabaseConfiguration.INDEX_NAMESPACE).subset(INDEX_NAME);
		index.setProperty(INDEX_BACKEND_KEY, "elasticsearch");
		index.setProperty("local-mode", true);
		index.setProperty("client-only", false);
		index.setProperty(STORAGE_DIRECTORY_KEY, environment.getDirectory().getAbsolutePath() + File.separator + "es");
		
		final TitanGraph graph = TitanFactory.open(config);
		return graph;
	}
	
	/**
	 * Creates the util.
	 * 
	 * @param environment
	 *            the environment
	 * @return the graph
	 */
	public static final KeyIndexableGraph createUtil(@NotNull final GraphEnvironment environment) {
		SANITY: {
			assert environment.getType() != null;
		}
		
		switch (environment.getType()) {
			case TITAN_DB:
				return createTitanDBGraph(environment);
			default:
				// TODO error
		}
		
		assert false;
		return null;
	}
}
