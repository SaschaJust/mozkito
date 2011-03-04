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
	private int                 saveCount = 0;
	
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
		this.saveCount = 0;
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
		if (Logger.logDebug()) {
			Logger.debug("SaveORUpdate: " + change.toString());
		}
		this.hibernate.saveOrUpdate(change);
		++this.saveCount;
		if (this.saveCount > 10000) {
			if (Logger.logInfo()) {
				Logger.info("Committing change operations");
			}
			this.hibernate.commitTransaction();
			this.hibernate.beginTransaction();
			this.saveCount = 0;
		}
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
		if (Logger.logInfo()) {
			Logger.info("Committing change operations");
		}
		this.hibernate.commitTransaction();
		this.hibernate.beginTransaction();
	}
}
