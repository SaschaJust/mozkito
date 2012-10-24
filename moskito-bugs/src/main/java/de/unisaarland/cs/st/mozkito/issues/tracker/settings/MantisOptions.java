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
 *******************************************************************************/
package de.unisaarland.cs.st.mozkito.issues.tracker.settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.InputFileArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.ProxyConfig;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.mozkito.issues.exceptions.InvalidParameterException;
import de.unisaarland.cs.st.mozkito.issues.tracker.Tracker;
import de.unisaarland.cs.st.mozkito.issues.tracker.mantis.MantisTracker;

/**
 * The Class MantisOptions.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class MantisOptions extends ArgumentSetOptions<Tracker, ArgumentSet<Tracker, MantisOptions>> implements
        ITrackerOptions {
	
	/** The tracker. */
	private MantisTracker                                            tracker;
	private net.ownhero.dev.hiari.settings.InputFileArgument.Options csvOptions;
	
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
	public Tracker init() {
		// PRECONDITIONS
		
		try {
			final File csvFile = getSettings().getArgument(this.csvOptions).getValue();
			this.tracker = new MantisTracker();
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
						if (lineParts[i].toLowerCase().replaceAll("\"", "").equals("id")) {
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
				this.tracker.setReportIds(links);
			}
			return this.tracker;
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
	 * @see de.unisaarland.cs.st.mozkito.bugs.tracker.settings.ITrackerOptions#setup(java.net.URI, java.lang.String,
	 * java.lang.String, net.ownhero.dev.ioda.ProxyConfig)
	 */
	@Override
	public void setup(final URI trackerUri,
	                  final String trackerUser,
	                  final String trackerPassword,
	                  final ProxyConfig proxyConfig) {
		// PRECONDITIONS
		
		try {
			this.tracker.setup(trackerUri, trackerUser, trackerPassword, proxyConfig);
		} catch (final InvalidParameterException e) {
			throw new UnrecoverableError(e);
			
		} finally {
			// POSTCONDITIONS
		}
	}
	
}
