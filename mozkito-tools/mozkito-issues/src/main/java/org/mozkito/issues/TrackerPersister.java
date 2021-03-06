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
package org.mozkito.issues;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.PostExecutionHook;
import net.ownhero.dev.andama.threads.PreExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Sink;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.issues.model.Report;
import org.mozkito.issues.tracker.Tracker;
import org.mozkito.persistence.PersistenceUtil;

/**
 * The Class TrackerPersister.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class TrackerPersister extends Sink<Report> {
	
	/**
	 * Instantiates a new tracker persister.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 * @param tracker
	 *            the tracker
	 * @param persistenceUtil
	 *            the persistence util
	 */
	public TrackerPersister(final Group threadGroup, final Settings settings, final Tracker tracker,
	        final PersistenceUtil persistenceUtil) {
		super(threadGroup, settings, false);
		
		new PreExecutionHook<Report, Report>(this) {
			
			@Override
			public void preExecution() {
				persistenceUtil.beginTransaction();
			}
		};
		
		new ProcessHook<Report, Report>(this) {
			
			@Override
			public void process() {
				final Report bugReport = getInputData();
				
				if (Logger.logDebug()) {
					Logger.debug("Storing " + bugReport);
				}
				persistenceUtil.saveOrUpdate(bugReport);
			}
		};
		
		new PostExecutionHook<Report, Report>(this) {
			
			@Override
			public void postExecution() {
				persistenceUtil.commitTransaction();
				persistenceUtil.shutdown();
			}
		};
	}
}
