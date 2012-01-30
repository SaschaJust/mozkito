/*******************************************************************************
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
 ******************************************************************************/
package de.unisaarland.cs.st.moskito.mapping.mappable.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import net.ownhero.dev.kanuni.annotations.simple.NotNegative;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.collections.CollectionUtils;

import de.unisaarland.cs.st.moskito.mapping.mappable.FieldKey;
import de.unisaarland.cs.st.moskito.rcs.model.RCSFile;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

/**
 * Class that wraps {@link RCSTransaction} to be mapped.
 * 
 * @see MappableEntity
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Entity
@DiscriminatorValue ("MAPPABLETRANSACTION")
public class MappableTransaction extends MappableEntity {
	
	/**
     * 
     */
	private static final long serialVersionUID = 3493346151115096823L;
	private RCSTransaction    transaction;
	
	/**
	 * @deprecated used only by persistence utility
	 */
	@Deprecated
	public MappableTransaction() {
		super();
	}
	
	/**
	 * @param transaction
	 */
	public MappableTransaction(final RCSTransaction transaction) {
		super();
		
		setTransaction(transaction);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.mappable.MappableEntity#get(de
	 * .unisaarland.cs.st.reposuite.mapping.mappable.FieldKey)
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
			default:
				return null;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.mappable.MappableEntity#get(de
	 * .unisaarland.cs.st.reposuite.mapping.mappable.FieldKey, int)
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
					Logger.warn("Field " + key.name() + " is not indexable on " + getHandle() + ".");
				}
				return get(key);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.mappable.MappableEntity#getBaseType ()
	 */
	@Override
	@Transient
	public Class<?> getBaseType() {
		return RCSTransaction.class;
	}
	
	/**
	 * @param index
	 * @return
	 */
	@Transient
	public RCSFile getFile(@NotNegative final int index) {
		final Collection<RCSFile> changedFiles = getTransaction().getChangedFiles();
		
		if (changedFiles.size() > index) {
			return (RCSFile) CollectionUtils.get(changedFiles, index);
		} else {
			return null;
		}
	}
	
	/**
	 * @return
	 */
	@Override
	@Transient
	public String getId() {
		return getTransaction().getId();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.mappable.MappableEntity#getText()
	 */
	@Override
	@Transient
	public String getText() {
		return getTransaction().getMessage();
	}
	
	/**
	 * @return
	 */
	@OneToOne (fetch = FetchType.LAZY)
	public RCSTransaction getTransaction() {
		return this.transaction;
	}
	
	/**
	 * @param transaction
	 */
	public void setTransaction(final RCSTransaction transaction) {
		this.transaction = transaction;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.mappable.MappableEntity#supported ()
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
}
