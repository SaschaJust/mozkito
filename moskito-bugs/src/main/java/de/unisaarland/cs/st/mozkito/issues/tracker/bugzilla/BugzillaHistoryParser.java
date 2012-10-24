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
package de.unisaarland.cs.st.mozkito.issues.tracker.bugzilla;

import java.util.SortedSet;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.mozkito.issues.tracker.model.HistoryElement;
import de.unisaarland.cs.st.mozkito.persistence.model.Person;

/**
 * The Interface BugzillaHistoryParser.
 *
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public interface BugzillaHistoryParser {
	
	/**
	 * Gets the history.
	 *
	 * @return the history
	 */
	SortedSet<HistoryElement> getHistory();
	
	/**
	 * Gets the resolution timestamp.
	 *
	 * @return the resolution timestamp
	 */
	DateTime getResolutionTimestamp();
	
	/**
	 * Gets the resolver.
	 *
	 * @return the resolver
	 */
	Person getResolver();
	
	/**
	 * Checks for parsed.
	 *
	 * @return true, if successful
	 */
	boolean hasParsed();
	
	/**
	 * Parses the.
	 *
	 * @return true, if successful
	 */
	boolean parse();
	
}
