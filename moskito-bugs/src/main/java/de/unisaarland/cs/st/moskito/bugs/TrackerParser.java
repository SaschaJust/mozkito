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

import java.net.URI;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Transformer;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.bugs.tracker.Tracker;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.Report;
import de.unisaarland.cs.st.moskito.bugs.tracker.settings.TrackerSettings;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class TrackerParser extends Transformer<URI, Report> {
	
	/**
	 * @param threadGroup
	 * @param tracker
	 */
	public TrackerParser(final Group threadGroup, final TrackerSettings settings, final Tracker tracker) {
		super(threadGroup, settings, false);
		
		new ProcessHook<URI, Report>(this) {
			
			@Override
			public void process() {
				final URI uri = getInputData();
				
				if (Logger.logDebug()) {
					Logger.debug("Parsing " + uri.toASCIIString() + ".");
				}
				
				final Report report = tracker.parse(uri);
				if (report == null) {
					skipOutputData(report);
				} else {
					provideOutputData(report);
				}
			}
		};
	}
}
