/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.moskito;

import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaSink;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.moskito.settings.RepositorySettings;

/**
 * This class is a end point for the {@link RepositoryToolchain} tool chain in case no database
 * connection is used. The data received from the previous node is void sinked.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepositoryVoidSink extends AndamaSink<RCSTransaction> {
	
	/**
	 * @see RepoSuiteSinkThread
	 * @param threadGroup
	 * @param settings
	 */
	public RepositoryVoidSink(final AndamaGroup threadGroup, final RepositorySettings settings) {
		super(threadGroup, settings, false);
	}
}
