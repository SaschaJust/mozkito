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
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla;

import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.ClassFinder;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.bugs.tracker.OverviewParser;
import de.unisaarland.cs.st.moskito.bugs.tracker.Parser;
import de.unisaarland.cs.st.moskito.bugs.tracker.ReportLink;
import de.unisaarland.cs.st.moskito.bugs.tracker.Tracker;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class BugzillaTracker extends Tracker {
	
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
			return new BugzillaOverviewParser(this);
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
			
			// FIXME get version number by new dynamic argument
			final String bugzillaVersion = "4.0.4";
			
			// load all BugzillaParsers
			try {
				final Collection<Class<? extends BugzillaParser>> parserClasses = ClassFinder.getClassesExtendingClass(BugzillaParser.class.getPackage(),
				                                                                                                       BugzillaParser.class,
				                                                                                                       Modifier.ABSTRACT
				                                                                                                               | Modifier.INTERFACE
				                                                                                                               | Modifier.PRIVATE);
				for (final Class<? extends BugzillaParser> parserClass : parserClasses) {
					if (!Modifier.isAbstract(parserClass.getModifiers())) {
						parserClass.newInstance();
					}
				}
			} catch (final Exception e) {
				throw new UnrecoverableError(e);
			}
			
			// get the correct parser and set tracker.
			return BugzillaParser.getParser(bugzillaVersion);
			
		} finally {
			// POSTCONDITIONS
		}
	}
}
