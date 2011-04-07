package de.unisaarland.cs.st.reposuite.ppa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import de.unisaarland.cs.st.reposuite.Core;
import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.persistence.HibernateUtil;
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
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;

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
	
	/** The hibernate util. */
	private HibernateUtil             hibernateUtil;
	
	/** The shutdown. */
	private boolean                   shutdown;
	
	/** The start with. */
	private final StringArgument      startWithArg;
	
	/**
	 * Instantiates a new pPA tool chain.
	 */
	public PPAToolChain() {
		super(new RepositorySettings());
		this.threadPool = new RepoSuiteThreadPool(Core.class.getSimpleName(), this);
		RepositorySettings settings = (RepositorySettings) getSettings();
		
		this.repoSettings = settings.setRepositoryArg(true);
		this.databaseSettings = settings.setDatabaseArgs(false);
		settings.setLoggerArg(true);
		this.testCaseTransactionArg = new ListArgument(settings, "testCaseTransactions",
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
		                                       "Use this transaction ID as the first one.",
		                                       null, false);
		
		settings.parseArguments();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		if (!this.shutdown) {
			setup();
			if (!this.shutdown) {
				this.threadPool.execute();
			}
		}
		if (Logger.logInfo()) {
			Logger.info("Done. Terminating ...");
		}
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteToolchain#setup()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setup() {
		if (!this.databaseSettings.getValue()) {
			if (Logger.logError()) {
				Logger.error("Could not connect to database!");
			}
		}
		try {
			this.hibernateUtil = HibernateUtil.getInstance();
		} catch (UninitializedDatabaseException e1) {
			throw new UnrecoverableError(e1);
		}
		
		File xmlFile = this.asXML.getValue();
		Repository repository = this.repoSettings.getValue();
		
		//get the transactions to be processed
		List<RCSTransaction> transactions = new LinkedList<RCSTransaction>();
		Criteria criteria = this.hibernateUtil.createCriteria(RCSTransaction.class);
		HashSet<String> transactionLimit = this.testCaseTransactionArg.getValue();
		if (transactionLimit != null) {
			criteria.add(Restrictions.in("id", transactionLimit));
		}
		transactions.addAll(criteria.list());
		
		
		//generate the change operation reader
		new ChangeOperationReader(this.threadPool.getThreadGroup(), getSettings(), repository, transactions,
		                          this.startWithArg.getValue(), this.ppaArg.getValue());
		
		//the xml file set, create XMLSinkThread. Otherwise the Hibernate persister thread
		if (xmlFile != null) {
			boolean stdout = false;
			if (!xmlFile.canWrite()) {
				if (Logger.logError()) {
					Logger.error("Cannot write XML document to file: " + "Writing to sstdout!");
				}
				stdout = true;
			} else {
				try {
					new PPAXMLSink(this.threadPool.getThreadGroup(), getSettings(), new FileOutputStream(xmlFile));
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
					new PPAXMLSink(this.threadPool.getThreadGroup(), getSettings(), System.out);
				} catch (ParserConfigurationException e) {
					throw new UnrecoverableError(e.getMessage(), e);
				}
			}
			
		} else {
			new ChangeOperationPersister(this.threadPool.getThreadGroup(), getSettings());
		}
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
		this.threadPool.shutdown();
		this.shutdown = true;
	}
}
