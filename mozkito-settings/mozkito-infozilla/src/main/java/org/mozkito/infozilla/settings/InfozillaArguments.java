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
package org.mozkito.infozilla.settings;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.DirectoryArgument;
import net.ownhero.dev.hiari.settings.DirectoryArgument.Options;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;

import org.mozkito.infozilla.InfozillaEnvironment;
import org.mozkito.infozilla.filters.Filter;
import org.mozkito.infozilla.managers.IManager;

/**
 * The Class InfozillaArguments.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class InfozillaArguments extends
        ArgumentSetOptions<InfozillaEnvironment, ArgumentSet<InfozillaEnvironment, InfozillaArguments>> {
	
	/** The filters. */
	private Set<Filter<?>>       filters = new HashSet<Filter<?>>();
	
	/** The attachment directory options. */
	private Options              attachmentDirectoryOptions;
	
	/** The filter options. */
	private FilterOptions        filterOptions;
	
	/** The directory. */
	private File                 directory;
	
	/** The manager options. */
	private FilterManagerOptions managerOptions;
	
	/** The managers. */
	private Set<IManager>  managers;
	
	/**
	 * Instantiates a new infozilla arguments.
	 * 
	 * @param argumentSet
	 *            the argument set
	 * @param requirements
	 *            the requirements
	 */
	public InfozillaArguments(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
		super(argumentSet, "infozilla", "description", requirements);
		
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
	 */
	@Override
	public InfozillaEnvironment init() {
		final DirectoryArgument directoryArgument = getSettings().getArgument(this.attachmentDirectoryOptions);
		this.directory = directoryArgument.getValue();
		
		final ArgumentSet<Set<Filter<?>>, FilterOptions> filterArgumentSet = getSettings().getArgumentSet(this.filterOptions);
		this.filters = filterArgumentSet.getValue();
		
		final ArgumentSet<Set<IManager>, FilterManagerOptions> managerArgumentSet = getSettings().getArgumentSet(this.managerOptions);
		this.managers = managerArgumentSet.getValue();
		
		return new InfozillaEnvironment(this.directory, this.managers, this.filters);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#requirements(net.ownhero.dev.hiari.settings.ArgumentSet)
	 */
	@Override
	public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> set) throws ArgumentRegistrationException,
	                                                                            SettingsParseError {
		PRECONDITIONS: {
			if (set == null) {
				throw new NullPointerException();
			}
		}
		
		final HashMap<String, IOptions<?, ?>> map = new HashMap<String, IOptions<?, ?>>();
		
		this.attachmentDirectoryOptions = new DirectoryArgument.Options(set, "attachmentDirectory",
		                                                                "local file cache for attachments", null,
		                                                                Requirement.required, true);
		map.put(this.attachmentDirectoryOptions.getName(), this.attachmentDirectoryOptions);
		
		this.filterOptions = new FilterOptions(set, Requirement.required);
		map.put(this.filterOptions.getName(), this.filterOptions);
		
		this.managerOptions = new FilterManagerOptions(set, Requirement.required);
		map.put(this.managerOptions.getName(), this.managerOptions);
		
		return map;
	}
	
}
