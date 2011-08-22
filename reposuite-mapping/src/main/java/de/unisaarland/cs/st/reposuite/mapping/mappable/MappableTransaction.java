/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.mappable;

import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;

/**
 * @author just
 * 
 */
public class MappableTransaction extends MappableEntity {
	
	/**
     * 
     */
	private static final long serialVersionUID = 3493346151115096823L;
	private RCSTransaction    transaction;
	
	public MappableTransaction(final RCSTransaction transaction) {
		this.setTransaction(transaction);
	}
	
	public RCSTransaction getTransaction() {
		return transaction;
	}
	
	public void setTransaction(RCSTransaction transaction) {
		this.transaction = transaction;
	}
	
	@Override
	public Class<?> getBaseType() {
		return RCSTransaction.class;
	}
	
	@Override
	public String getBodyText() {
		return transaction.getMessage();
	}
}
