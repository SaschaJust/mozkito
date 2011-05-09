package de.unisaarland.cs.st.reposuite.ppa;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.ppa.internal.visitors.ChangeOperationVisitor;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.ppa.utils.PPAUtils;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteSourceThread;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;

/**
 * The Class ChangeOperationReader.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class ChangeOperationReader extends RepoSuiteSourceThread<JavaChangeOperation> implements ChangeOperationVisitor {
	
	/** The repository. */
	private final Repository           repository;
	
	/** The transactions. */
	private final List<RCSTransaction> transactions;
	
	private final String               startWith;
	
	private boolean                    usePPA;
	
	/**
	 * Instantiates a new change operation reader.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 * @param repository
	 *            the repository
	 * @param transactions
	 *            the transactions
	 * @param startWith
	 *            the transaction is to start with. If null, start with the
	 *            first transaction
	 * @param ppa
	 */
	public ChangeOperationReader(final RepoSuiteThreadGroup threadGroup, final RepoSuiteSettings settings,
	        final Repository repository, final List<RCSTransaction> transactions, final String startWith,
	        final Boolean usePPA) {
		super(threadGroup, ChangeOperationReader.class.getSimpleName(), settings);
		if (usePPA == null) {
			this.usePPA = true;
		} else {
			this.usePPA = usePPA;
		}
		this.repository = repository;
		this.transactions = transactions;
		this.startWith = startWith;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.ppa.internal.visitors.ChangeOperationVisitor
	 * #endVisit()
	 */
	@Override
	public void endVisit() {
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		Set<ChangeOperationVisitor> visitors = new HashSet<ChangeOperationVisitor>();
		visitors.add(this);
		int size = this.transactions.size();
		int counter = 0;
		
		boolean consider = true;
		if (this.startWith != null) {
			consider = false;
		}
		
		for (RCSTransaction transaction : this.transactions) {
			
			if (!consider) {
				if (transaction.getId().equals(this.startWith)) {
					consider = true;
					size = size - counter;
					counter = 0;
				} else {
					++counter;
					continue;
				}
			}
			
			if (Logger.logInfo()) {
				Logger.info("Computing change operations for transaction `" + transaction.getId() + "` (" + (++counter)
				        + "/" + size + ")");
			}
			if (this.usePPA) {
				PPAUtils.generateChangeOperations(this.repository, transaction, visitors);
			} else {
				PPAUtils.generateChangeOperationsNOPPA(this.repository, transaction, visitors);
			}
		}
		if (Logger.logInfo()) {
			Logger.info("All done. Finishing.");
		}
		this.finish();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.ppa.internal.visitors.ChangeOperationVisitor
	 * #visit(de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation)
	 */
	@Override
	public void visit(final JavaChangeOperation change) {
		try {
			if (this.getOutputStorage().getNumReaders() < 1) {
				throw new UnrecoverableError("No readers connected to output storage! Terminating!");
			}
			this.write(change);
		} catch (InterruptedException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			this.shutdown();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.ppa.internal.visitors.ChangeOperationVisitor
	 * #visit(de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction)
	 */
	@Override
	public void visit(final RCSTransaction transaction) {
	}
}
