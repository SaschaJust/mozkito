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

package org.mozkito.mappings.engines.master;

import java.io.IOException;
import java.util.Properties;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetFactory;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kanuni.instrumentation.KanuniAgent;

import org.mozkito.issues.model.EnhancedReport;
import org.mozkito.issues.model.Report;
import org.mozkito.mappings.finder.Finder;
import org.mozkito.mappings.model.BugFixCandidate;
import org.mozkito.mappings.model.Candidate;
import org.mozkito.mappings.model.Composite;
import org.mozkito.mappings.model.Mapping;
import org.mozkito.mappings.model.Relation;
import org.mozkito.mappings.settings.MappingOptions;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.versions.model.ChangeSet;

/**
 * The Class Environment.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public final class Environment {
	
	static {
		KanuniAgent.initialize();
	}
	
	/**
	 * Candidate.
	 * 
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @return the candidate
	 * @throws Exception
	 *             the exception
	 */
	public static Candidate<Report, ChangeSet> candidate(final Report from,
	                                                     final ChangeSet to) throws Exception {
		
		if ((from != null) && (to != null)) {
			
			return new BugFixCandidate(from, to);
		} else {
			throw new Exception("Could not build Candidate.");
		}
	}
	
	/**
	 * Composite.
	 * 
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @return the composite
	 * @throws Exception
	 *             the exception
	 */
	public static Composite composite(final Report from,
	                                  final ChangeSet to) throws Exception {
		return new Composite(relation(from, to));
	}
	
	/**
	 * Load enhanced report.
	 * 
	 * @param util
	 *            the util
	 * @param report
	 *            the report
	 * @return the enhanced report
	 */
	public static EnhancedReport loadEnhancedReport(final PersistenceUtil util,
	                                                final Report report) {
		return loadEnhancedReport(util, report.getId());
	}
	
	/**
	 * Load enhanced report.
	 * 
	 * @param util
	 *            the util
	 * @param id
	 *            the id
	 * @return the enhanced report
	 */
	public static EnhancedReport loadEnhancedReport(final PersistenceUtil util,
	                                                final String id) {
		return util.loadById(id, EnhancedReport.class);
		
	}
	
	/**
	 * Load report.
	 * 
	 * @param util
	 *            the util
	 * @param id
	 *            the id
	 * @return the report
	 */
	public static Report loadReport(final PersistenceUtil util,
	                                final String id) {
		return util.loadById(id, Report.class);
	}
	
	/**
	 * Load transaction.
	 * 
	 * @param util
	 *            the util
	 * @param id
	 *            the id
	 * @return the rCS transaction
	 */
	public static ChangeSet loadTransaction(final PersistenceUtil util,
	                                        final String id) {
		return util.loadById(id, ChangeSet.class);
	}
	
	/**
	 * Mapping.
	 * 
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @return the mapping
	 * @throws Exception
	 *             the exception
	 */
	public static Mapping mapping(final Report from,
	                              final ChangeSet to) throws Exception {
		return new Mapping(composite(from, to));
	}
	
	/**
	 * Relation.
	 * 
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @return the relation
	 * @throws Exception
	 *             the exception
	 */
	public static Relation relation(final Report from,
	                                final ChangeSet to) throws Exception {
		return new Relation(candidate(from, to));
	}
	
	/**
	 * Setup.
	 * 
	 * @param resource
	 *            the resource
	 * @return the finder
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws SettingsParseError
	 *             the settings parse error
	 * @throws ArgumentSetRegistrationException
	 *             the argument set registration exception
	 * @throws ArgumentRegistrationException
	 *             the argument registration exception
	 */
	public static Finder setup(final String resource) throws IOException,
	                                                 SettingsParseError,
	                                                 ArgumentSetRegistrationException,
	                                                 ArgumentRegistrationException {
		final Properties properties = System.getProperties();
		properties.load(Environment.class.getResourceAsStream(resource));
		
		final Settings settings = new Settings();
		
		final MappingOptions mappingOptions = new MappingOptions(settings.getRoot(), Requirement.required, null, null);
		final ArgumentSet<Finder, MappingOptions> mappingArguments = ArgumentSetFactory.create(mappingOptions);
		final Finder finder = mappingArguments.getValue();
		
		return finder;
	}
	
	/**
	 * Instantiates a new environment.
	 */
	private Environment() {
	}
}
