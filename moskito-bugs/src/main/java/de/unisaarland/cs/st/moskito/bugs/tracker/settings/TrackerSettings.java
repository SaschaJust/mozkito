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
package de.unisaarland.cs.st.moskito.bugs.tracker.settings;

import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.registerable.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import de.unisaarland.cs.st.moskito.settings.DatabaseOptions;

/**
 * The Class TrackerSettings.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class TrackerSettings extends Settings {
	
	/** The Constant debug. */
	public static final boolean debug = Boolean.parseBoolean(System.getProperty("debug"));
	
	/**
	 * Add the settings set for the database.
	 * 
	 * @param requirement
	 *            the requirement
	 * @param unit
	 *            the unit
	 * @return the database arguments
	 * @throws ArgumentRegistrationException
	 *             the argument registration exception
	 */
	public DatabaseOptions setDatabaseArgs(final Requirement requirement,
	                                         final String unit) throws ArgumentRegistrationException {
		final DatabaseOptions minerDatabaseArguments = new DatabaseOptions(getRootArgumentSet(), requirement, unit);
		return minerDatabaseArguments;
	}
	
	/**
	 * Sets the tracker args.
	 * 
	 * @param requirement
	 *            the requirement
	 * @return the tracker arguments
	 * @throws ArgumentRegistrationException
	 *             the argument registration exception
	 */
	public TrackerArguments setTrackerArgs(final Requirement requirement) throws ArgumentRegistrationException {
		final TrackerArguments trackerArguments = new TrackerArguments(getRootArgumentSet(), requirement);
		return trackerArguments;
	}
	
}
