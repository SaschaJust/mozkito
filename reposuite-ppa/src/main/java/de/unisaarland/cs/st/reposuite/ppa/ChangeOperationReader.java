package de.unisaarland.cs.st.reposuite.ppa;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.ppa.internal.visitors.ChangeOperationVisitor;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElementCache;
import de.unisaarland.cs.st.reposuite.ppa.utils.PPAUtils;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteSourceThread;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;
import de.unisaarland.cs.st.reposuite.utils.Logger;

public class ChangeOperationReader extends RepoSuiteSourceThread<JavaChangeOperation> implements ChangeOperationVisitor {
	
	private final Repository repository;
	private final List<RCSTransaction> transactions;
	
	public ChangeOperationReader(final RepoSuiteThreadGroup threadGroup, final RepoSuiteSettings settings,
			final Repository repository, final List<RCSTransaction> transactions) {
		super(threadGroup, ChangeOperationReader.class.getSimpleName(), settings);
		this.repository = repository;
		this.transactions = transactions;
	}
	
	@Override
	public void endVisit() {
	}
	
	@Override
	public void run() {
		Set<ChangeOperationVisitor> visitors = new HashSet<ChangeOperationVisitor>();
		visitors.add(this);
		int size = this.transactions.size();
		int counter = 0;
		
		synchronized (JavaElementCache.classDefs) {
			try {
				JavaElementCache.classDefs.wait();
			} catch (InterruptedException e) {
				throw new UnrecoverableError(e.getMessage(), e);
			}
		}
		
		for (RCSTransaction transaction : this.transactions) {
			if (Logger.logInfo()) {
				Logger.info("Computing change operations for transaction `" + transaction.getId() + "` (" + (++counter)
						+ "/" + size + ")");
			}
			PPAUtils.generateChangeOperations(this.repository, transaction, visitors);
		}
		if (Logger.logInfo()) {
			Logger.info("All done. Finishing.");
		}
		finish();
	}
	
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
			shutdown();
		}
	}
	
	@Override
	public void visit(final RCSTransaction transaction) {
	}
}
