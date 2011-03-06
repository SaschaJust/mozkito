package de.unisaarland.cs.st.reposuite.ppa;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.persistence.HibernateUtil;
import de.unisaarland.cs.st.reposuite.ppa.internal.visitors.PersistingChangeOperationVisitor;
import de.unisaarland.cs.st.reposuite.ppa.internal.visitors.XMLChangeOperationVisitor;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.settings.DatabaseArguments;
import de.unisaarland.cs.st.reposuite.settings.FileArgument;
import de.unisaarland.cs.st.reposuite.settings.ListArgument;
import de.unisaarland.cs.st.reposuite.settings.LoggerArguments;
import de.unisaarland.cs.st.reposuite.settings.RepositoryArguments;
import de.unisaarland.cs.st.reposuite.settings.RepositorySettings;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;

public class ReposuiteDeltaInfo {
	
	private final RepositoryArguments repoSettings;
	@SuppressWarnings("unused")
	private final LoggerArguments     logSettings;
	private final DatabaseArguments   databaseSettings;
	private final HibernateUtil       hibernateUtil;
	private final FileArgument        asXML;
	private final ListArgument        testCaseTransactionArg;
	
	public ReposuiteDeltaInfo() {
		RepositorySettings settings = new RepositorySettings();
		this.repoSettings = settings.setRepositoryArg(true);
		this.databaseSettings = settings.setDatabaseArgs(false);
		this.logSettings = settings.setLoggerArg(true);
		this.testCaseTransactionArg = new ListArgument(
				settings,
				"testCaseTransactions",
				"List of transactions that will be passed for test case purposes. "
				+ "If this option is set, this module will start in test case mode. "
				+ "If will generate change operations to specified transactions, only;"
				+ "outputting result as XML either to sdtout (if option -DasXML not set) "
				+ "or to specified XML file.",
				null, false);
		
		this.asXML = new FileArgument(settings, "output.xml",
				"Instead of writing the source code change operations to the DB, output them as XML into this file.",
				null, false, true, false);
		
		settings.parseArguments();
		this.hibernateUtil = this.databaseSettings.getValue(false);
	}
	
	/**
	 * Run.
	 */
	public void run() {
		
		File xmlFile = this.asXML.getValue();
		Repository repository = this.repoSettings.getValue();
		ChangeOperationGenerator generator = new ChangeOperationGenerator(repository);
		
		Criteria criteria = this.hibernateUtil.createCriteria(RCSTransaction.class);
		HashSet<String> transactionsLimit = this.testCaseTransactionArg.getValue();
		if (transactionsLimit != null) {
			criteria.add(Restrictions.in("id", transactionsLimit));
			boolean stdout = false;
			if (xmlFile != null) {
				try {
					generator.registerVisitor(new XMLChangeOperationVisitor(new FileOutputStream(xmlFile)));
				} catch (IOException e) {
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
			} else {
				stdout = true;
			}
			
			if (stdout) {
				try {
					generator.registerVisitor(new XMLChangeOperationVisitor(System.out));
				} catch (ParserConfigurationException e) {
					throw new UnrecoverableError(e.getMessage(), e);
				}
			}
		} else {
			generator.registerVisitor(new PersistingChangeOperationVisitor(this.hibernateUtil));
		}
		
		@SuppressWarnings("unchecked") List<RCSTransaction> transactions = criteria.list();
		generator.handleTransactions(transactions);
		
		
		if (Logger.logInfo()) {
			Logger.info("Done. Terminating ...");
		}
	}
}
