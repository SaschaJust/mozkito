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

import java.io.File;

import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.KeyIndexableGraph;
import com.tinkerpop.blueprints.Parameter;

/**
 * The Class LocalFileDBGraphManager.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public abstract class LocalFileDBGraphManager extends GraphManager {
	
	/** The directory. */
	private File directory = null;
	
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
	 * @see org.mozkito.graphs.GraphManager#createKeyIndex(java.lang.String, java.lang.Class,
	 *      com.tinkerpop.blueprints.Parameter<?,?>[])
	 */
	@SuppressWarnings ("javadoc")
	@Override
	public abstract <X extends Element> void createKeyIndex(final String key,
	                                                        final Class<X> elementClass,
	                                                        final Parameter<?, ?>... indexParameters);
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.graphs.GraphManager#createUtil()
	 */
	@Override
	public abstract KeyIndexableGraph createUtil();
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.graphs.GraphManager#getFileHandle()
	 */
	@Override
	public final File getFileHandle() {
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
	public final boolean isFileBased() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return true;
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
	public final boolean isLocal() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return true;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.graphs.GraphManager#provides()
	 */
	@Override
	public abstract GraphType provides();
	
	/**
	 * Sets the directory.
	 * 
	 * @param directory
	 *            the new directory
	 */
	protected final void setDirectory(final File directory) {
		PRECONDITIONS: {
			if (directory == null) {
				throw new NullPointerException("Directory must not be null.");
			}
		}
		
		this.directory = directory;
		
		POSTCONDITIONS: {
			assert this.directory != null;
		}
	}
	
}
