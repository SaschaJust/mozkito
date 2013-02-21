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

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.kanuni.annotations.simple.NotNegative;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.collections.CollectionUtils;

import org.mozkito.mappings.mappable.FieldKey;
import org.mozkito.mappings.messages.Messages;
import org.mozkito.versions.model.ChangeSet;
import org.mozkito.versions.model.Handle;

/**
 * Class that wraps {@link ChangeSet} to be mapped.
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
	private ChangeSet         changeset;
	
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
	 * @param changeset
	 *            the transaction
	 */
	public MappableTransaction(final ChangeSet changeset) {
		super();
		
		setTransaction(changeset);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.mappable.MappableEntity#get(de .unisaarland.cs.st.reposuite.mapping.mappable.FieldKey)
	 */
	@Override
	public Object get(@NotNull final FieldKey key) {
		switch (key) {
			case AUTHOR:
				return getChangeSet().getAuthor();
			case BODY:
				return getChangeSet().getMessage();
			case CLOSED_TIMESTAMP:
				return getChangeSet().getTimestamp();
			case CLOSER:
				return getChangeSet().getAuthor();
			case CREATION_TIMESTAMP:
				return getChangeSet().getTimestamp();
			case FILE:
				// this should probably be a collection of mappings-files (own
				// class for mappings)
				return getChangeSet().getChangedFiles();
			case PATH:
				return getChangeSet().getChangedFiles();
			case RESOLUTION_TIMESTAMP:
				return getChangeSet().getTimestamp();
			case SUMMARY:
				return getChangeSet().getMessage();
			case ID:
				return getId();
			case CHANGER:
				return getChangeSet().getAuthor();
			case COMMENT:
				return getChangeSet().getMessage();
			case MODIFICATION_TIMESTAMP:
				return getChangeSet().getTimestamp();
			default:
				break;
		}
		
		throw new UnrecoverableError(
		                             Messages.getString("MappableEntity.unsupportedFieldKey", getClassName(), key.name())); //$NON-NLS-1$
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
					Logger.warn(Messages.getString("MappableEntity.notIndexable", key.name(), getClassName())); //$NON-NLS-1$
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
		return ChangeSet.class;
	}
	
	/**
	 * Gets the file.
	 * 
	 * @param index
	 *            the index
	 * @return the file
	 */
	@Transient
	public Handle getFile(@NotNegative final int index) {
		final Collection<Handle> changedFiles = getChangeSet().getChangedFiles();
		
		if (changedFiles.size() > index) {
			return (Handle) CollectionUtils.get(changedFiles, index);
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
		return getChangeSet().getId();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.mappable.MappableEntity#getText()
	 */
	@Override
	@Transient
	public String getText() {
		return getChangeSet().getMessage();
	}
	
	/**
	 * Gets the transaction.
	 * 
	 * @return the transaction
	 */
	@OneToOne (fetch = FetchType.LAZY)
	public ChangeSet getChangeSet() {
		return this.changeset;
	}
	
	/**
	 * Sets the transaction.
	 * 
	 * @param changeset
	 *            the new transaction
	 */
	public void setTransaction(final ChangeSet changeset) {
		this.changeset = changeset;
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
		
		builder.append(getClassName());
		builder.append(" [transaction="); //$NON-NLS-1$
		builder.append(getChangeSet());
		builder.append("]"); //$NON-NLS-1$
		
		return builder.toString();
	}
}
