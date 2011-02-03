package de.unisaarland.cs.st.reposuite.ppa.internal.visitors;

import de.unisaarland.cs.st.reposuite.persistence.HibernateUtil;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * The Class PersistingChangeOperationVisitor.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class PersistingChangeOperationVisitor implements ChangeOperationVisitor {
	
	/** The hibernate. */
	private final HibernateUtil hibernate;
	
	/** The seen transaction. */
	private boolean             seenTransaction = false;
	
	/**
	 * Instantiates a new persisting change operation visitor.
	 * 
	 * @param hibernate
	 *            the hibernate
	 */
	public PersistingChangeOperationVisitor(final HibernateUtil hibernate) {
		this.hibernate = hibernate;
		hibernate.beginTransaction();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.ppa.internal.visitors.ChangeOperationVisitor
	 * #endVisit()
	 */
	@Override
	public void endVisit() {
		if (Logger.logInfo()) {
			Logger.info("Committing change operations");
		}
		this.hibernate.commitTransaction();
		this.hibernate.beginTransaction();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.ppa.internal.visitors.ChangeOperationVisitor
	 * #visit(de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation)
	 */
	@Override
	public void visit(final JavaChangeOperation change) {
		this.hibernate.saveOrUpdate(change);
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.ppa.internal.visitors.ChangeOperationVisitor
	 * #visit(de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction)
	 */
	@Override
	public void visit(final RCSTransaction transaction) {
		if (this.seenTransaction) {
			if (Logger.logInfo()) {
				Logger.info("Committing change operations");
			}
			this.hibernate.commitTransaction();
			this.hibernate.beginTransaction();
		}
		this.seenTransaction = true;
	}
	
}
