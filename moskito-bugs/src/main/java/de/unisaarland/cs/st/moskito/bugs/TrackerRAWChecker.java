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
package de.unisaarland.cs.st.moskito.bugs;

import net.ownhero.dev.andama.threads.AndamaFilter;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.bugs.tracker.RawReport;
import de.unisaarland.cs.st.moskito.bugs.tracker.Tracker;
import de.unisaarland.cs.st.moskito.bugs.tracker.settings.TrackerSettings;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class TrackerRAWChecker extends AndamaFilter<RawReport> {
	
	public TrackerRAWChecker(final AndamaGroup threadGroup, final TrackerSettings settings, final Tracker tracker) {
		super(threadGroup, settings, false);
		
		new ProcessHook<RawReport, RawReport>(this) {
			
			@Override
			public void process() {
				RawReport rawReport = getInputData();
				
				if (tracker.checkRAW(rawReport)) {
					if (Logger.logDebug()) {
						Logger.debug("RAW report " + rawReport + " passed analysis.");
					}
					provideOutputData(rawReport);
				} else {
					if (Logger.logWarn()) {
						Logger.warn("Skipping report " + rawReport + " due to errors in raw string.");
					}
					skipOutputData(rawReport);
				}
			}
		};
	}
}
