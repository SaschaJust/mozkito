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
package de.unisaarland.cs.st.moskito.ppa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.model.Chain;
import net.ownhero.dev.andama.model.Pool;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.arguments.BooleanArgument;
import net.ownhero.dev.hiari.settings.arguments.ListArgument;
import net.ownhero.dev.hiari.settings.arguments.OutputFileArgument;
import net.ownhero.dev.hiari.settings.arguments.SetArgument;
import net.ownhero.dev.hiari.settings.arguments.StringArgument;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.registerable.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.ppa.model.JavaElementFactory;
import de.unisaarland.cs.st.moskito.rcs.Repository;
import de.unisaarland.cs.st.moskito.settings.DatabaseArguments;
import de.unisaarland.cs.st.moskito.settings.RepositoryArguments;
import de.unisaarland.cs.st.moskito.settings.RepositorySettings;

/**
 * The Class PPAToolChain.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class PPAToolChain extends Chain<Settings> {
	
	/** The thread pool. */
	private final Pool                threadPool;
	
	/** The repo settings. */
	private final RepositoryArguments repoSettings;
	
	/** The database settings. */
	private final DatabaseArguments   databaseSettings;
	
	/** The test case transaction arg. */
	private final SetArgument         testCaseTransactionArg;
	
	private final BooleanArgument     ppaArg;
	
	/** The as xml. */
	private final OutputFileArgument  asXML;
	
	/** The persistence middleware util. */
	private PersistenceUtil           persistenceUtil;
	
	/** The start with. */
	private final StringArgument      startWithArg;
	
	private ListArgument              packageFilterArg;
	
	/**
	 * Instantiates a new pPA tool chain.
	 * 
	 * @throws ArgumentRegistrationException
	 * @throws SettingsParseError
	 */
	public PPAToolChain() {
		super(new RepositorySettings());
		
		this.threadPool = new Pool(PPAToolChain.class.getSimpleName(), this);
		final RepositorySettings settings = (RepositorySettings) getSettings();
		
		try {
			this.repoSettings = settings.setRepositoryArg(Requirement.required);
			this.databaseSettings = settings.setDatabaseArgs(Requirement.optional, "ppa");
			settings.setLoggerArg(Requirement.required);
			this.testCaseTransactionArg = new SetArgument(
			                                              settings.getRootArgumentSet(),
			                                              "testCaseTransactions",
			                                              "List of transactions that will be passed for test case purposes. "
			                                                      + "If this option is set, this module will start in test case mode. "
			                                                      + "It will generate change operations to specified transactions, only;"
			                                                      + "outputting result as XML either to sdtout (if option -DasXML not set) "
			                                                      + "or to specified XML file.", null,
			                                              Requirement.optional);
			
			this.ppaArg = new BooleanArgument(settings.getRootArgumentSet(), "ppa",
			                                  "If set to true, this module will use the PPA tool.", "true",
			                                  Requirement.optional);
			
			this.asXML = new OutputFileArgument(
			                                    settings.getRootArgumentSet(),
			                                    "output.xml",
			                                    "Instead of writing the source code change operations to the DB, output them as XML into this file.",
			                                    null, Requirement.optional, true);
			
			this.packageFilterArg = new ListArgument(
			                                         settings.getRootArgumentSet(),
			                                         "ppa.package.filter",
			                                         "Generate only those change operations that change definitions and classes for these packages. (entries are separated using ',')",
			                                         null, Requirement.optional);
			
			this.startWithArg = new StringArgument(settings.getRootArgumentSet(), "startTransaction",
			                                       "Use this transaction ID as the first one.", null,
			                                       Requirement.optional);
		} catch (final ArgumentRegistrationException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new Shutdown(e.getMessage(), e);
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.toolchain.RepoSuiteToolchain#setup()
	 */
	@Override
	public void setup() {
		this.persistenceUtil = this.databaseSettings.getValue();
		if (this.persistenceUtil == null) {
			if (Logger.logError()) {
				Logger.error("Could not connect to database!");
			}
			
			throw new Shutdown();
		}
		
		final File xmlFile = this.asXML.getValue();
		this.repoSettings.setPersistenceUtil(this.persistenceUtil);
		final Repository repository = this.repoSettings.getValue();
		
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
		final List<String> packageFilterList = this.packageFilterArg.getValue();
		if (packageFilterList != null) {
			packageFilter = packageFilterList.toArray(new String[packageFilterList.size()]);
		}
		
		// generate the change operation reader
		new PPASource(this.threadPool.getThreadGroup(), getSettings(), this.persistenceUtil,
		              this.startWithArg.getValue(), this.testCaseTransactionArg.getValue());
		new PPATransformer(this.threadPool.getThreadGroup(), getSettings(), repository, this.ppaArg.getValue(),
		                   elementFactory, packageFilter);
		
		if (Logger.logDebug()) {
			Logger.debug("Setup done.");
		}
		
	}
}
