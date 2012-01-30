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

import javax.xml.parsers.ParserConfigurationException;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.model.AndamaChain;
import net.ownhero.dev.andama.model.AndamaPool;
import net.ownhero.dev.andama.settings.BooleanArgument;
import net.ownhero.dev.andama.settings.ListArgument;
import net.ownhero.dev.andama.settings.OutputFileArgument;
import net.ownhero.dev.andama.settings.StringArgument;
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
public class PPAToolChain extends AndamaChain {
	
	/** The thread pool. */
	private final AndamaPool          threadPool;
	
	/** The repo settings. */
	private final RepositoryArguments repoSettings;
	
	/** The database settings. */
	private final DatabaseArguments   databaseSettings;
	
	/** The test case transaction arg. */
	private final ListArgument        testCaseTransactionArg;
	
	private final BooleanArgument     ppaArg;
	
	/** The as xml. */
	private final OutputFileArgument  asXML;
	
	/** The persistence middleware util. */
	private PersistenceUtil           persistenceUtil;
	
	/** The start with. */
	private final StringArgument      startWithArg;
	
	/**
	 * Instantiates a new pPA tool chain.
	 */
	public PPAToolChain() {
		super(new RepositorySettings());
		
		this.threadPool = new AndamaPool(PPAToolChain.class.getSimpleName(), this);
		RepositorySettings settings = (RepositorySettings) getSettings();
		
		this.repoSettings = settings.setRepositoryArg(true);
		this.databaseSettings = settings.setDatabaseArgs(false, "ppa");
		settings.setLoggerArg(true);
		this.testCaseTransactionArg = new ListArgument(
		                                               settings,
		                                               "testCaseTransactions",
		                                               "List of transactions that will be passed for test case purposes. "
		                                                       + "If this option is set, this module will start in test case mode. "
		                                                       + "If will generate change operations to specified transactions, only;"
		                                                       + "outputting result as XML either to sdtout (if option -DasXML not set) "
		                                                       + "or to specified XML file.", null, false);
		
		this.ppaArg = new BooleanArgument(settings, "ppa", "If set to true, this module will use the PPA tool.",
		                                  "false", false);
		
		this.asXML = new OutputFileArgument(
		                                    settings,
		                                    "output.xml",
		                                    "Instead of writing the source code change operations to the DB, output them as XML into this file.",
		                                    null, false, true);
		
		this.startWithArg = new StringArgument(settings, "startTransaction",
		                                       "Use this transaction ID as the first one.", null, false);
		
		settings.parseArguments();
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		if (Logger.logDebug()) {
			Logger.debug("Setting up ...");
		}
		setup();
		if (Logger.logDebug()) {
			Logger.debug("Ececuting ...");
		}
		this.threadPool.execute();
		
		if (Logger.logInfo()) {
			Logger.info("Terminating ...");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.toolchain.RepoSuiteToolchain#setup()
	 */
	@Override
	public void setup() {
		persistenceUtil = this.databaseSettings.getValue();
		if (persistenceUtil == null) {
			if (Logger.logError()) {
				Logger.error("Could not connect to database!");
			}
			
			throw new Shutdown();
		}
		
		File xmlFile = this.asXML.getValue();
		Repository repository = this.repoSettings.getValue();
		
		JavaElementFactory elementFactory = new JavaElementFactory(this.persistenceUtil);
		
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
				} catch (FileNotFoundException e) {
					if (Logger.logError()) {
						Logger.error("Cannot write XML document to file: " + e.getMessage() + FileUtils.lineSeparator
						        + "Writing to sstdout!");
					}
					stdout = true;
				} catch (ParserConfigurationException e) {
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
				} catch (ParserConfigurationException e) {
					throw new UnrecoverableError(e.getMessage(), e);
				}
			}
			
		} else {
			new PPAPersister(this.threadPool.getThreadGroup(), getSettings(), this.persistenceUtil);
		}
		
		// generate the change operation reader
		new PPASource(this.threadPool.getThreadGroup(), getSettings(), persistenceUtil, this.startWithArg.getValue(),
		              testCaseTransactionArg.getValue());
		new PPATransformer(this.threadPool.getThreadGroup(), getSettings(), repository, this.ppaArg.getValue(),
		                   elementFactory);
		
		if (Logger.logDebug()) {
			Logger.debug("Setup done.");
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.toolchain.RepoSuiteToolchain#shutdown()
	 */
	@Override
	public void shutdown() {
		this.threadPool.shutdown();
	}
}
