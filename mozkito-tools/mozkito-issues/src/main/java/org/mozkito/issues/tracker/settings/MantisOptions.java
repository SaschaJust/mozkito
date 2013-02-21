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
package org.mozkito.issues.tracker.settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.InputFileArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.issues.exceptions.InvalidParameterException;
import org.mozkito.issues.model.IssueTracker;
import org.mozkito.issues.tracker.Tracker;
import org.mozkito.issues.tracker.mantis.MantisTracker;

/**
 * The Class MantisOptions.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class MantisOptions extends ArgumentSetOptions<Boolean, ArgumentSet<Boolean, MantisOptions>> implements
        ITrackerOptions {
	
	/** The csv options. */
	private net.ownhero.dev.hiari.settings.InputFileArgument.Options csvOptions;
	private Collection<String>                                       links = null;
	
	/**
	 * Instantiates a new mantis options.
	 * 
	 * @param trackerOptions
	 *            the tracker options
	 * @param requirement
	 *            the requirement
	 */
	@NoneNull
	public MantisOptions(final TrackerOptions trackerOptions, final Requirement requirement) {
		super(trackerOptions.getArgumentSet(), "mantis", "Necessary arguments to connect and parse mantis reports.",
		      requirement);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init(java.util.Map)
	 */
	@Override
	@NoneNull
	public Boolean init() {
		// PRECONDITIONS
		
		try {
			final File csvFile = getSettings().getArgument(this.csvOptions).getValue();
			
			if (csvFile.exists()) {
				final Collection<String> links = new HashSet<>();
				try (final BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
					String nextLine;
					nextLine = reader.readLine();
					if (nextLine == null) {
						throw new UnrecoverableError(
						                             String.format("Specified mantis overview CSV file %s is empty! CSV must contain HEADER line.",
						                                           csvFile.getAbsolutePath()));
					}
					int idIndex = -1;
					String[] lineParts = nextLine.split(",");
					for (int i = 0; i < lineParts.length; ++i) {
						if ("id".equals(lineParts[i].toLowerCase().replaceAll("\"", ""))) {
							idIndex = i;
							break;
						}
					}
					if (idIndex < 0) {
						throw new UnrecoverableError(
						                             String.format("Specified mantis overview CSV file %s must contain HEADER line INCLUDING 'Id' column. Please make sure to generate overview file matching this specification.",
						                                           csvFile.getAbsolutePath()));
					}
					while ((nextLine = reader.readLine()) != null) {
						lineParts = nextLine.split(",");
						links.add(lineParts[idIndex].replaceAll("\"", ""));
					}
				} catch (final IOException e) {
					throw new UnrecoverableError(e);
				}
				if (links.isEmpty()) {
					if (Logger.logWarn()) {
						Logger.warn("Specified mantis overview CSV file %s contains HEADER line only.");
					}
				}
				if (Logger.logInfo()) {
					Logger.info("Added %s bug IDs from overview CSV file %s.", String.valueOf(links.size()),
					            csvFile.getAbsolutePath());
				}
				
				this.links = links;
			}
			return true;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#requirements(net.ownhero.dev.hiari.settings.ArgumentSet)
	 */
	@Override
	@NoneNull
	public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> set) throws ArgumentRegistrationException,
	                                                                            SettingsParseError {
		// PRECONDITIONS
		
		try {
			
			final Map<String, IOptions<?, ?>> map = new HashMap<String, IOptions<?, ?>>();
			
			this.csvOptions = new InputFileArgument.Options(
			                                                set,
			                                                "mantisCSV",
			                                                "CSV overview file generated using Mantis itself. Parsing overview pages automatically can .",
			                                                null, Requirement.optional);
			map.put(this.csvOptions.getName(), this.csvOptions);
			
			return map;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.settings.ITrackerOptions#setup(java.net.URI, java.lang.String, java.lang.String,
	 * net.ownhero.dev.ioda.ProxyConfig)
	 */
	@Override
	public Tracker setup(final IssueTracker issueTracker,
	                     final URI trackerUri,
	                     final String trackerUser,
	                     final String trackerPassword) {
		// PRECONDITIONS
		
		try {
			getSettings().getArgumentSet(this).getValue();
			final MantisTracker tracker = new MantisTracker(issueTracker);
			if (this.links != null) {
				tracker.setReportIds(this.links);
			}
			tracker.setup(trackerUri, trackerUser, trackerPassword);
			return tracker;
		} catch (final InvalidParameterException e) {
			throw new UnrecoverableError(e);
			
		} finally {
			// POSTCONDITIONS
		}
	}
	
}
