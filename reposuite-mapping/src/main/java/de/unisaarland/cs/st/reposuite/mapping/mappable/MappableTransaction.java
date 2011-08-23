package de.unisaarland.cs.st.reposuite.mapping.mappable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.ownhero.dev.kanuni.annotations.simple.NotNegative;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.collections.CollectionUtils;

import de.unisaarland.cs.st.reposuite.rcs.model.RCSFile;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
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
	
	/**
	 * @return
	 */
	public String getBody() {
		return transaction.getMessage();
	}
	
	/**
	 * @return
	 */
	public String getId() {
		return getTransaction().getId();
	}
	
	/**
	 * @param index
	 * @return
	 */
	public RCSFile getFile(@NotNegative int index) {
		Collection<RCSFile> changedFiles = getTransaction().getChangedFiles();
		if (changedFiles.size() > index) {
			return (RCSFile) CollectionUtils.get(changedFiles, index);
		} else {
			return null;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity#get(de
	 * .unisaarland.cs.st.reposuite.mapping.mappable.FieldKey)
	 */
	@Override
	public Object get(@NotNull FieldKey key) {
		switch (key) {
			case ID:
				return getId();
			case BODY:
				return getBody();
			default:
				return null;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity#get(de
	 * .unisaarland.cs.st.reposuite.mapping.mappable.FieldKey, int)
	 */
	@Override
	public Object get(@NotNull FieldKey key, @NotNegative int index) {
		switch (key) {
			case FILE:
				return getFile(index);
			default:
				if (Logger.logWarn()) {
					Logger.warn("Field " + key.name() + " is not indexable on " + getHandle() + ".");
				}
				return get(key);
		}
	}
	
	@SuppressWarnings("serial")
	@Override
	public Set<FieldKey> supported() {
		// TODO complete this
		return new HashSet<FieldKey>() {
			
			{
				add(FieldKey.ID);
			}
		};
	}
	
	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return null;
	}
}
