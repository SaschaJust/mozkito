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
package org.mozkito.codeanalysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.model.Chain;
import net.ownhero.dev.andama.model.Pool;
import net.ownhero.dev.hiari.settings.ArgumentFactory;
import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetFactory;
import net.ownhero.dev.hiari.settings.BooleanArgument;
import net.ownhero.dev.hiari.settings.ListArgument;
import net.ownhero.dev.hiari.settings.OutputFileArgument;
import net.ownhero.dev.hiari.settings.SetArgument;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.codeanalysis.model.JavaElementFactory;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.settings.DatabaseOptions;
import org.mozkito.versions.Repository;
import org.mozkito.versions.settings.RepositoryOptions;

/**
 * The Class PPAToolChain.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class PPAToolChain extends Chain<Settings> {
	
	/** The thread pool. */
	private final Pool                                    threadPool;
	
	/** The test case transaction arg. */
	private final SetArgument                             testCaseTransactionArgument;
	
	/** The codeanalysis arg. */
	private final BooleanArgument                         ppaArgument;
	
	/** The as xml. */
	private final OutputFileArgument                      asXML;
	
	/** The persistence middleware util. */
	private PersistenceUtil                               persistenceUtil;
	
	/** The package filter arg. */
	private ListArgument                                  packageFilterArgument;
	
	/** The database options. */
	private DatabaseOptions                               databaseOptions;
	
	/** The repository arguments. */
	private ArgumentSet<Repository, RepositoryOptions>    repositoryArguments;
	
	/** The database arguments. */
	private ArgumentSet<PersistenceUtil, DatabaseOptions> databaseArguments;
	
	/**
	 * Instantiates a new pPA tool chain.
	 * 
	 * @param settings
	 *            the settings
	 */
	public PPAToolChain(final Settings settings) {
		super(settings);
		
		this.threadPool = new Pool(PPAToolChain.class.getSimpleName(), this);
		
		try {
			
			this.databaseOptions = new DatabaseOptions(settings.getRoot(), Requirement.required, "codeanalysis");
			
			this.databaseArguments = ArgumentSetFactory.create(this.databaseOptions);
			
			this.repositoryArguments = ArgumentSetFactory.create(new RepositoryOptions(settings.getRoot(),
			                                                                           Requirement.required,
			                                                                           this.databaseOptions));
			
			this.testCaseTransactionArgument = ArgumentFactory.create(new SetArgument.Options(
			                                                                                  settings.getRoot(),
			                                                                                  "testCaseTransactions",
			                                                                                  "List of transactions that will be passed for test case purposes. "
			                                                                                          + "If this option is set, this module will start in test case mode. "
			                                                                                          + "It will generate change operations to specified transactions, only;"
			                                                                                          + "outputting result as XML either to sdtout (if option -DasXML not set) "
			                                                                                          + "or to specified XML file.",
			                                                                                  new HashSet<String>(),
			                                                                                  Requirement.optional));
			
			this.ppaArgument = ArgumentFactory.create(new BooleanArgument.Options(
			                                                                      settings.getRoot(),
			                                                                      "codeanalysis",
			                                                                      "If set to true, this module will use the PPA tool.",
			                                                                      true, Requirement.optional));
			
			this.asXML = ArgumentFactory.create(new OutputFileArgument.Options(
			                                                                   settings.getRoot(),
			                                                                   "outputXml",
			                                                                   "Instead of writing the source code change operations to the DB, output them as XML into this file.",
			                                                                   null, Requirement.optional, true));
			
			this.packageFilterArgument = ArgumentFactory.create(new ListArgument.Options(
			                                                                             settings.getRoot(),
			                                                                             "packageFilter",
			                                                                             "Generate only those change operations that change definitions and classes for these packages. (entries are separated using ',')",
			                                                                             new ArrayList<String>(0),
			                                                                             Requirement.optional));
		} catch (final ArgumentRegistrationException e) {
			throw new Shutdown(e.getMessage(), e);
		} catch (final ArgumentSetRegistrationException e) {
			throw new Shutdown(e.getMessage(), e);
		} catch (final SettingsParseError e) {
			throw new Shutdown(e.getMessage(), e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.toolchain.RepoSuiteToolchain#setup()
	 */
	@Override
	public void setup() {
		this.persistenceUtil = this.databaseArguments.getValue();
		final File xmlFile = this.asXML.getValue();
		final Repository repository = this.repositoryArguments.getValue();
		
		final JavaElementFactory elementFactory = new JavaElementFactory(this.persistenceUtil);
		
		// the xml file set, create XMLSinkThread. Otherwise the persistence
		// middleware persister thread
		if (xmlFile != null) {
			boolean stdout = false;
			if (!xmlFile.canWrite()) {
				if (Logger.logError()) {
					Logger.error("Cannot write XML document to file: " + "Writing to sstdout!");
				}
				stdout = true;
			} else {
				try {
					new PPAXMLTransformer(this.threadPool.getThreadGroup(), getSettings(),
					                      new FileOutputStream(xmlFile));
				} catch (final FileNotFoundException e) {
					if (Logger.logError()) {
						Logger.error("Cannot write XML document to file: " + e.getMessage() + FileUtils.lineSeparator
						        + "Writing to sstdout!");
					}
					stdout = true;
				} catch (final ParserConfigurationException e) {
					if (Logger.logError()) {
						Logger.error("Cannot write XML document to file: " + e.getMessage() + FileUtils.lineSeparator
						        + "Writing to sstdout!");
					}
					stdout = true;
				}
			}
			
			if (stdout) {
				try {
					new PPAXMLTransformer(this.threadPool.getThreadGroup(), getSettings(), System.out);
				} catch (final ParserConfigurationException e) {
					throw new UnrecoverableError(e.getMessage(), e);
				}
			}
			
		} else {
			new PPAPersister(this.threadPool.getThreadGroup(), getSettings(), this.persistenceUtil);
		}
		
		String[] packageFilter = new String[0];
		final List<String> packageFilterList = this.packageFilterArgument.getValue();
		if (packageFilterList != null) {
			packageFilter = packageFilterList.toArray(new String[packageFilterList.size()]);
		}
		
		// generate the change operation reader
		new PPASource(this.threadPool.getThreadGroup(), getSettings(), this.persistenceUtil,
		              this.testCaseTransactionArgument.getValue());
		new PPATransformer(this.threadPool.getThreadGroup(), getSettings(), repository, this.ppaArgument.getValue(),
		                   elementFactory, packageFilter);
		
		if (Logger.logDebug()) {
			Logger.debug("Setup done.");
		}
		
	}
}
