/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.ppa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.persistence.Criteria;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceManager;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElementFactory;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.settings.BooleanArgument;
import de.unisaarland.cs.st.reposuite.settings.DatabaseArguments;
import de.unisaarland.cs.st.reposuite.settings.ListArgument;
import de.unisaarland.cs.st.reposuite.settings.OutputFileArgument;
import de.unisaarland.cs.st.reposuite.settings.RepositoryArguments;
import de.unisaarland.cs.st.reposuite.settings.RepositorySettings;
import de.unisaarland.cs.st.reposuite.settings.StringArgument;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadPool;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteToolchain;

/**
 * The Class PPAToolChain.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class PPAToolChain extends RepoSuiteToolchain {
	
	/** The thread pool. */
	private final RepoSuiteThreadPool threadPool;
	
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
	
	/** The shutdown. */
	private boolean                   shutdown;
	
	/** The start with. */
	private final StringArgument      startWithArg;
	
	/**
	 * Instantiates a new pPA tool chain.
	 */
	public PPAToolChain() {
		super(new RepositorySettings());
		threadPool = new RepoSuiteThreadPool(PPAToolChain.class.getSimpleName(), this);
		RepositorySettings settings = (RepositorySettings) getSettings();
		
		repoSettings = settings.setRepositoryArg(true);
		databaseSettings = settings.setDatabaseArgs(false, "ppa");
		settings.setLoggerArg(true);
		testCaseTransactionArg = new ListArgument(
		                                          settings,
		                                          "testCaseTransactions",
		                                          "List of transactions that will be passed for test case purposes. "
		                                          + "If this option is set, this module will start in test case mode. "
		                                          + "If will generate change operations to specified transactions, only;"
		                                          + "outputting result as XML either to sdtout (if option -DasXML not set) "
		                                          + "or to specified XML file.", null, false);
		
		ppaArg = new BooleanArgument(settings, "ppa", "If set to true, this module will use the PPA tool.",
		                             "false", false);
		
		asXML = new OutputFileArgument(
		                               settings,
		                               "output.xml",
		                               "Instead of writing the source code change operations to the DB, output them as XML into this file.",
		                               null, false, true);
		
		startWithArg = new StringArgument(settings, "startTransaction",
		                                  "Use this transaction ID as the first one.", null, false);
		
		settings.parseArguments();
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		if (!shutdown) {
			setup();
			if (!shutdown) {
				threadPool.execute();
			}
		}
		if (Logger.logInfo()) {
			Logger.info("Done. Terminating ...");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteToolchain#setup()
	 */
	@SuppressWarnings ("unchecked")
	@Override
	public void setup() {
		if (!databaseSettings.getValue()) {
			if (Logger.logError()) {
				Logger.error("Could not connect to database!");
			}
		}
		try {
			persistenceUtil = PersistenceManager.getUtil();
		} catch (UninitializedDatabaseException e1) {
			throw new UnrecoverableError(e1);
		}
		
		File xmlFile = asXML.getValue();
		Repository repository = repoSettings.getValue();
		
		// get the transactions to be processed
		List<RCSTransaction> transactions = new LinkedList<RCSTransaction>();
		@SuppressWarnings ("rawtypes")
		Criteria criteria = persistenceUtil.createCriteria(RCSTransaction.class);
		HashSet<String> transactionLimit = testCaseTransactionArg.getValue();
		if (transactionLimit != null) {
			criteria.in("id", transactionLimit);
		}
		transactions.addAll(persistenceUtil.load(criteria));
		
		JavaElementFactory.init(persistenceUtil);
		
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
					new PPAXMLTransformer(threadPool.getThreadGroup(), getSettings(),
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
					new PPAXMLTransformer(threadPool.getThreadGroup(), getSettings(), System.out);
				} catch (ParserConfigurationException e) {
					throw new UnrecoverableError(e.getMessage(), e);
				}
			}
			
		} else {
			new ChangeOperationPersister(threadPool.getThreadGroup(), getSettings(), persistenceUtil);
		}
		
		// generate the change operation reader
		new ChangeOperationReader(threadPool.getThreadGroup(), getSettings(), repository, transactions,
		                          startWithArg.getValue(), ppaArg.getValue());
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.RepoSuiteToolchain#shutdown()
	 */
	@Override
	public void shutdown() {
		
		if (Logger.logInfo()) {
			Logger.info("Toolchain shutdown.");
		}
		threadPool.shutdown();
		shutdown = true;
	}
}
