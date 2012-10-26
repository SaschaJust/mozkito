/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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
 *******************************************************************************/
/**
 * 
 */
package org.mozkito.issues;

import org.mozkito.issues.tracker.ReportLink;
import org.mozkito.issues.tracker.Tracker;
import org.mozkito.issues.tracker.model.Report;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Transformer;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.kisa.Logger;

/**
 * The Class TrackerParser.
 *
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class TrackerParser extends Transformer<ReportLink, Report> {
	
	/**
	 * Instantiates a new tracker parser.
	 *
	 * @param threadGroup the thread group
	 * @param settings the settings
	 * @param tracker the tracker
	 */
	public TrackerParser(final Group threadGroup, final Settings settings, final Tracker tracker) {
		super(threadGroup, settings, false);
		
		new ProcessHook<ReportLink, Report>(this) {
			
			@Override
			public void process() {
				final ReportLink reportLink = getInputData();
				
				if (Logger.logDebug()) {
					Logger.debug("Parsing " + reportLink.toString() + ".");
				}
				
				final Report report = tracker.parse(reportLink);
				if (report == null) {
					skipOutputData(report);
				} else {
					provideOutputData(report);
				}
			}
		};
	}
}
