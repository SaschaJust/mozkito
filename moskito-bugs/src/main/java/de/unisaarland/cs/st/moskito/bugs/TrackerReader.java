/*******************************************************************************
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
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.moskito.bugs;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Source;
import de.unisaarland.cs.st.moskito.bugs.tracker.ReportLink;
import de.unisaarland.cs.st.moskito.bugs.tracker.Tracker;
import de.unisaarland.cs.st.moskito.bugs.tracker.settings.TrackerSettings;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class TrackerReader extends Source<ReportLink> {
	
	/**
	 * @param threadGroup
	 * @param tracker
	 */
	public TrackerReader(final Group threadGroup, final TrackerSettings settings, final Tracker tracker) {
		super(threadGroup, settings, false);
		
		new ProcessHook<ReportLink, ReportLink>(this) {
			
			@Override
			public void process() {
				final ReportLink bugURI = tracker.getNextReportLink();
				
				if (bugURI != null) {
					providePartialOutputData(bugURI);
				} else {
					setCompleted();
				}
			}
		};
	}
	
}
