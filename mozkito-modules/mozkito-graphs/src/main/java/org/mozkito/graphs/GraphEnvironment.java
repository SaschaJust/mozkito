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

import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;

/**
 * The Class GraphEnvironment.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class GraphEnvironment {
	
	/** The type. */
	private final GraphType type;
	
	/** The remote. */
	private final boolean   remote;
	
	/** The directory. */
	private final File      directory = null;
	
	/**
	 * Instantiates a new graph environment.
	 * 
	 * @param type
	 *            the type
	 */
	public GraphEnvironment(@NotNull final GraphType type) {
		this.remote = false;
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
