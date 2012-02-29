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
import net.ownhero.dev.andama.threads.Transformer;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.bugs.tracker.RawReport;
import de.unisaarland.cs.st.moskito.bugs.tracker.Tracker;
import de.unisaarland.cs.st.moskito.bugs.tracker.XmlReport;
import de.unisaarland.cs.st.moskito.bugs.tracker.settings.TrackerSettings;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class TrackerXMLTransformer extends Transformer<RawReport, XmlReport> {
	
	public TrackerXMLTransformer(final Group threadGroup, final TrackerSettings settings, final Tracker tracker) {
		super(threadGroup, settings, false);
		
		new ProcessHook<RawReport, XmlReport>(this) {
			
			@Override
			public void process() {
				final RawReport rawReport = getInputData();
				
				if (Logger.logDebug()) {
					Logger.debug("Converting " + rawReport + " to XML.");
				}
				
				final XmlReport xmlReport = tracker.createDocument(rawReport);
				if (xmlReport == null) {
					if (Logger.logWarn()) {
						Logger.warn("Skipping report " + rawReport.getId() + " diue to XML transformation errors.");
					}
					skipOutputData();
				} else {
					provideOutputData(xmlReport);
				}
			}
		};
	}
	
}
