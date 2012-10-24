/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *******************************************************************************/
/**
 * 
 */
package org.mozkito.issues;

import org.mozkito.issues.tracker.ReportLink;
import org.mozkito.issues.tracker.Tracker;
import org.mozkito.issues.tracker.model.Report;
import org.mozkito.persistence.PersistenceUtil;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Source;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.kisa.Logger;

/**
 * The Class TrackerReader.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class TrackerReader extends Source<ReportLink> {
	
	/**
	 * Instantiates a new tracker reader.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 * @param tracker
	 *            the tracker
	 */
	public TrackerReader(final Group threadGroup, final Settings settings, final Tracker tracker,
	        final PersistenceUtil persistenceUtil) {
		super(threadGroup, settings, false);
		
		new ProcessHook<ReportLink, ReportLink>(this) {
			
			@Override
			public void process() {
				final ReportLink bugURI = tracker.getNextReportLink();
				if (bugURI != null) {
					if (Logger.logDebug()) {
						Logger.debug("Checking if bug report with id %s is persisted already. If so skipping bug report ...",
						             bugURI.getBugId());
					}
					
					final Report report = persistenceUtil.loadById(bugURI.getBugId(), Report.class);
					if (report != null) {
						if (Logger.logInfo()) {
							Logger.info("Skipping already persisted bug report %s.", report.getId());
						}
						skipOutputData();
					} else {
						providePartialOutputData(bugURI);
					}
				} else {
					setCompleted();
				}
			}
		};
	}
}
