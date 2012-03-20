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
 ******************************************************************************/
package de.unisaarland.cs.st.moskito.bugs.tracker.mantis;

import java.net.URI;
import java.net.URISyntaxException;

import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.bugs.tracker.OverviewParser;
import de.unisaarland.cs.st.moskito.bugs.tracker.Parser;
import de.unisaarland.cs.st.moskito.bugs.tracker.ReportLink;
import de.unisaarland.cs.st.moskito.bugs.tracker.Tracker;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class MantisTracker extends Tracker {
	
	// URL = https://issues.openbravo.com/print_bug_page.php?bug_id=19779
	
	/**
	 * 
	 */
	public MantisTracker() {
		
	}
	
	@Override
	public ReportLink getLinkFromId(final String bugId) {
		// PRECONDITIONS
		
		try {
			try {
				return new ReportLink(new URI(Tracker.bugIdRegex.replaceAll(this.fetchURI.toString() + this.pattern,
				                                                            bugId + "")), bugId);
			} catch (final URISyntaxException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				return null;
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
	@Override
	public OverviewParser getOverviewParser() {
		// PRECONDITIONS
		
		try {
			if (Logger.logError()) {
				Logger.error("Overview parsing not supported yet.");
			}
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Tracker#getParser()
	 */
	@Override
	public Parser getParser() {
		// PRECONDITIONS
		
		try {
			return new MantisParser();
		} finally {
			// POSTCONDITIONS
		}
	}
	
}
