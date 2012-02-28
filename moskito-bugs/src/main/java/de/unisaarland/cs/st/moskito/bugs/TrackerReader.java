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
import net.ownhero.dev.andama.threads.Source;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.exceptions.FetchException;
import net.ownhero.dev.ioda.exceptions.UnsupportedProtocolException;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.bugs.tracker.RawReport;
import de.unisaarland.cs.st.moskito.bugs.tracker.Tracker;
import de.unisaarland.cs.st.moskito.bugs.tracker.settings.TrackerSettings;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class TrackerReader extends Source<RawReport> {
	
	/**
	 * @param threadGroup
	 * @param tracker
	 */
	public TrackerReader(final Group threadGroup, final TrackerSettings settings, final Tracker tracker) {
		super(threadGroup, settings, false);
		
		new ProcessHook<RawReport, RawReport>(this) {
			
			@Override
			public void process() {
				final Long bugId = tracker.getNextId();
				
				try {
					if (bugId != null) {
						
						if (Logger.logDebug()) {
							Logger.debug("Fetching " + bugId + ".");
						}
						
						final URI newURI = tracker.getLinkFromId(bugId);
						final RawReport source = tracker.fetchSource(newURI);
						
						if (source == null) {
							
							if (Logger.logWarn()) {
								Logger.warn("Skipping " + bugId + ". Fetch returned null.");
							}
							
							skipOutputData(source);
						} else {
							
							if (Logger.logDebug()) {
								Logger.debug("Providing " + bugId + ".");
							}
							
							providePartialOutputData(source);
						}
					} else {
						setCompleted();
					}
				} catch (final FetchException e) {
					throw new UnrecoverableError(e);
				} catch (final UnsupportedProtocolException e) {
					throw new UnrecoverableError(e);
				}
			}
		};
	}
	
}
