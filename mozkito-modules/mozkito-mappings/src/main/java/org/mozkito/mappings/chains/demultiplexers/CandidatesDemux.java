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
package org.mozkito.mappings.chains.demultiplexers;

import net.ownhero.dev.andama.threads.Demultiplexer;
import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.hiari.settings.Settings;

import org.mozkito.mappings.model.Candidate;

/**
 * The Class CandidatesDemux.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class CandidatesDemux extends Demultiplexer<Candidate> {
	
	/**
	 * Instantiates a new candidates demux.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 */
	public CandidatesDemux(final Group threadGroup, final Settings settings) {
		super(threadGroup, settings, false);
	}
	
}
