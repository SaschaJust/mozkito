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
package de.unisaarland.cs.st.moskito.bugs.tracker.sourceforge;

import java.net.URI;
import java.util.Set;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import de.unisaarland.cs.st.moskito.bugs.exceptions.InvalidParameterException;
import de.unisaarland.cs.st.moskito.bugs.tracker.Parser;
import de.unisaarland.cs.st.moskito.bugs.tracker.ReportLink;
import de.unisaarland.cs.st.moskito.bugs.tracker.Tracker;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Type;

/**
 * The Class SourceforgeTracker.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class SourceforgeTracker extends Tracker {
	
	/** The issue links. */
	
	private Long groupId;
	private Long atId;
	private Type bugType;
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Tracker#getParser()
	 */
	@Override
	public Parser getParser() {
		// PRECONDITIONS
		
		try {
			return new SourceforgeParser(this.bugType);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.OverviewParser#getReportLinks()
	 */
	@Override
	public Set<ReportLink> getReportLinks() {
		// PRECONDITIONS
		
		try {
			final SourceforgeOverviewParser overviewParser = new SourceforgeOverviewParser(this.atId, this.groupId);
			if (!overviewParser.parseOverview()) {
				throw new UnrecoverableError(
				                             "Could not parse sourceforge overview to extract report IDs. See earlier errors.");
			}
			return overviewParser.getReportLinks();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public void setup(@NotNull final URI fetchURI,
	                  final String username,
	                  final String password,
	                  final Long groupId,
	                  final Long atId,
	                  final Type bugType) throws InvalidParameterException {
		this.groupId = groupId;
		this.atId = atId;
		this.bugType = bugType;
		super.setup(fetchURI, username, password);
	}
}
