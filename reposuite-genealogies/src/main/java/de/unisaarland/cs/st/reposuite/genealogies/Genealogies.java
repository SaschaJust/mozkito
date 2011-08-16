package de.unisaarland.cs.st.reposuite.genealogies;

import java.util.List;

import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.genealogies.transaction.TransactionChangeGenealogy;
import de.unisaarland.cs.st.reposuite.persistence.Criteria;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceManager;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.settings.DatabaseArguments;
import de.unisaarland.cs.st.reposuite.settings.DirectoryArgument;
import de.unisaarland.cs.st.reposuite.settings.EnumArgument;
import de.unisaarland.cs.st.reposuite.settings.RepositoryArguments;
import de.unisaarland.cs.st.reposuite.settings.RepositorySettings;

public class Genealogies {
	
	private final DirectoryArgument   graphDBArg;
	private final EnumArgument        granularityArg;
	private final DatabaseArguments   databaseArgs;
	private PersistenceUtil           persistenceUtil;
	private final RepositoryArguments repositoryArg;
	private final Repository          repository;
	
	public Genealogies() {
		
		RepositorySettings settings = new RepositorySettings();
		settings.setLoggerArg(false);
		databaseArgs = settings.setDatabaseArgs(true, "ppa");
		repositoryArg = settings.setRepositoryArg(true);
		
		graphDBArg = new DirectoryArgument(settings, "genealogy.graphdb",
				"Directory in which to store the GraphDB (if exists, load graphDB from this dir)", null, true, true);
		
		granularityArg = new EnumArgument(settings, "genealogy.granularity",
				"The granularity level to base the ChangeGenealogy on.",
				ChangeGenealogyGranularity.TRANSACTION.toString(), true, ChangeGenealogyGranularity.getStringValues());
		
		settings.parseArguments();
		databaseArgs.getValue();
		repository = repositoryArg.getValue();
		persistenceUtil = null;
		try {
			persistenceUtil = PersistenceManager.getUtil();
		} catch (UninitializedDatabaseException e1) {
			throw new UnrecoverableError(e1.getMessage(), e1);
		}
	}
	
	public void run() {
		
		Criteria<RCSTransaction> transactionCriteria = persistenceUtil.createCriteria(RCSTransaction.class);
		List<RCSTransaction> transactions = persistenceUtil.load(transactionCriteria);
		
		GenealogyAnalyzer genealogyAnalyzer = new GenealogyAnalyzer(repository);

		switch (ChangeGenealogyGranularity.valueOf(granularityArg.getValue())) {
			case TRANSACTION:
				TransactionChangeGenealogy genealogy = new TransactionChangeGenealogy(graphDBArg.getValue(),
				        persistenceUtil, genealogyAnalyzer);
				genealogy.addTransactions(transactions);
				break;
			default:
				if (Logger.logError()) {
					Logger.error("Granularity level " + granularityArg.getValue()
							+ " not yet implemented or not supported!");
				}
				break;
		}
		
	}
}
