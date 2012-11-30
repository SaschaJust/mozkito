/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/
package org.mozkito.mappings.mappable.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.kanuni.annotations.simple.NotNegative;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.collections.CollectionUtils;

import org.mozkito.mappings.mappable.FieldKey;
import org.mozkito.mappings.messages.Messages;
import org.mozkito.versions.model.File;
import org.mozkito.versions.model.Transaction;

/**
 * Class that wraps {@link Transaction} to be mapped.
 * 
 * @see MappableEntity
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
@Entity
@DiscriminatorValue ("MAPPABLETRANSACTION")
public class MappableTransaction extends MappableEntity {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3493346151115096823L;
	
	/** The transaction. */
	private Transaction    transaction;
	
	/**
	 * Instantiates a new mappable transaction.
	 * 
	 * @deprecated used only by persistence utility
	 */
	@Deprecated
	public MappableTransaction() {
		super();
	}
	
	/**
	 * Instantiates a new mappable transaction.
	 * 
	 * @param transaction
	 *            the transaction
	 */
	public MappableTransaction(final Transaction transaction) {
		super();
		
		setTransaction(transaction);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.mappable.MappableEntity#get(de .unisaarland.cs.st.reposuite.mapping.mappable.FieldKey)
	 */
	@Override
	public Object get(@NotNull final FieldKey key) {
		switch (key) {
			case AUTHOR:
				return getTransaction().getAuthor();
			case BODY:
				return getTransaction().getMessage();
			case CLOSED_TIMESTAMP:
				return getTransaction().getTimestamp();
			case CLOSER:
				return getTransaction().getAuthor();
			case CREATION_TIMESTAMP:
				return getTransaction().getTimestamp();
			case FILE:
				// this should probably be a collection of mappings-files (own
				// class for mappings)
				return getTransaction().getChangedFiles();
			case PATH:
				return getTransaction().getChangedFiles();
			case RESOLUTION_TIMESTAMP:
				return getTransaction().getTimestamp();
			case SUMMARY:
				return getTransaction().getMessage();
			case ID:
				return getId();
			case CHANGER:
				return getTransaction().getAuthor();
			case COMMENT:
				return getTransaction().getMessage();
			case MODIFICATION_TIMESTAMP:
				return getTransaction().getTimestamp();
			default:
				break;
		}
		
		throw new UnrecoverableError(Messages.getString("MappableEntity.unsupportedFieldKey", getHandle(), key.name())); //$NON-NLS-1$
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.mappable.MappableEntity#get(de .unisaarland.cs.st.reposuite.mapping.mappable.FieldKey,
	 * int)
	 */
	@Override
	public Object get(@NotNull final FieldKey key,
	                  @NotNegative final int index) {
		switch (key) {
			case FILE:
				return getFile(index);
			case PATH:
				return getFile(index);
			default:
				if (Logger.logWarn()) {
					Logger.warn(Messages.getString("MappableEntity.notIndexable", key.name(), getHandle())); //$NON-NLS-1$
				}
				return get(key);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.mappable.MappableEntity#getBaseType ()
	 */
	@Override
	@Transient
	public Class<?> getBaseType() {
		return Transaction.class;
	}
	
	/**
	 * Gets the file.
	 * 
	 * @param index
	 *            the index
	 * @return the file
	 */
	@Transient
	public File getFile(@NotNegative final int index) {
		final Collection<File> changedFiles = getTransaction().getChangedFiles();
		
		if (changedFiles.size() > index) {
			return (File) CollectionUtils.get(changedFiles, index);
		} else {
			return null;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.mappable.model.MappableEntity#getId()
	 */
	@Override
	@Transient
	public String getId() {
		return getTransaction().getId();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.mappable.MappableEntity#getText()
	 */
	@Override
	@Transient
	public String getText() {
		return getTransaction().getMessage();
	}
	
	/**
	 * Gets the transaction.
	 * 
	 * @return the transaction
	 */
	@OneToOne (fetch = FetchType.LAZY)
	public Transaction getTransaction() {
		return this.transaction;
	}
	
	/**
	 * Sets the transaction.
	 * 
	 * @param transaction
	 *            the new transaction
	 */
	public void setTransaction(final Transaction transaction) {
		this.transaction = transaction;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.mappable.MappableEntity#supported ()
	 */
	@SuppressWarnings ("serial")
	@Override
	public Set<FieldKey> supported() {
		return new HashSet<FieldKey>() {
			
			{
				add(FieldKey.AUTHOR);
				add(FieldKey.BODY);
				add(FieldKey.CLOSED_TIMESTAMP);
				add(FieldKey.CLOSER);
				add(FieldKey.CREATION_TIMESTAMP);
				add(FieldKey.FILE);
				add(FieldKey.ID);
				add(FieldKey.PATH);
				add(FieldKey.RESOLUTION_TIMESTAMP);
				add(FieldKey.SUMMARY);
			}
		};
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		
		builder.append(getHandle());
		builder.append(" [transaction="); //$NON-NLS-1$
		builder.append(getTransaction());
		builder.append("]"); //$NON-NLS-1$
		
		return builder.toString();
	}
}
