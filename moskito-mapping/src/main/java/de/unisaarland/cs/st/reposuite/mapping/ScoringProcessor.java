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
package de.unisaarland.cs.st.reposuite.mapping;

import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaTransformer;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.mapping.elements.Candidate;
import de.unisaarland.cs.st.reposuite.mapping.finder.MappingFinder;
import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class ScoringProcessor extends AndamaTransformer<Candidate, MapScore> {
	
	/**
	 * @param threadGroup
	 * @param settings
	 * @param persistenceUtil
	 */
	public ScoringProcessor(final AndamaGroup threadGroup, final MappingSettings settings, final MappingFinder finder) {
		super(threadGroup, settings, false);
		final MapScore zeroScore = new MapScore(null, null);
		
		new ProcessHook<Candidate, MapScore>(this) {
			
			@Override
			public void process() {
				Candidate candidate = getInputData();
				
				if (Logger.logDebug()) {
					Logger.debug("Processing mapping for " + candidate.getFrom() + " to " + candidate.getTo() + ".");
				}
				
				MapScore score = finder.score(candidate.getFrom(), candidate.getTo());
				
				if (score.compareTo(zeroScore) > 0) {
					if (Logger.logInfo()) {
						Logger.info("Providing for store operation: " + candidate.getFrom() + " -> "
						        + candidate.getTo() + " (score: " + score + ").");
					}
					provideOutputData(score);
				} else {
					if (Logger.logDebug()) {
						Logger.debug("Discarding " + candidate.getFrom() + " -> " + candidate.getTo()
						        + " due to non-positive score (" + score + ").");
					}
					skipOutputData(score);
				}
				
			}
		};
	}
}
