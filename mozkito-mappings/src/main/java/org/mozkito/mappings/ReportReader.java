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
package org.mozkito.mappings;

import java.util.Iterator;
import java.util.List;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.PreExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Source;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.issues.tracker.model.Report;
import org.mozkito.persistence.Criteria;
import org.mozkito.persistence.PersistenceUtil;

/**
 * The Class ReportReader.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class ReportReader extends Source<Report> {
	
	/** The iterator. */
	private Iterator<Report> iterator;
	
	/**
	 * Instantiates a new report reader.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 * @param persistenceUtil
	 *            the persistence util
	 */
	public ReportReader(final Group threadGroup, final Settings settings, final PersistenceUtil persistenceUtil) {
		super(threadGroup, settings, false);
		
		new PreExecutionHook<Report, Report>(this) {
			
			@Override
			public void preExecution() {
				final Criteria<Report> criteria = persistenceUtil.createCriteria(Report.class);
				final List<Report> list = persistenceUtil.load(criteria);
				ReportReader.this.iterator = list.iterator();
			}
		};
		
		new ProcessHook<Report, Report>(this) {
			
			@Override
			public void process() {
				if (ReportReader.this.iterator.hasNext()) {
					final Report report = ReportReader.this.iterator.next();
					
					if (Logger.logInfo()) {
						Logger.info("Providing " + report);
					}
					
					providePartialOutputData(report);
				} else {
					provideOutputData(null, true);
				}
			}
		};
	}
}
