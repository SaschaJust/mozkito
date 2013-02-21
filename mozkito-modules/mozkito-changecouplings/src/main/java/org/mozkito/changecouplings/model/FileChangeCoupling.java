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
package org.mozkito.changecouplings.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Id;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;

import org.mozkito.persistence.Criteria;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.versions.exceptions.NoSuchHandleException;
import org.mozkito.versions.model.Handle;

/**
 * The Class FileChangeCoupling.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class FileChangeCoupling implements Comparable<FileChangeCoupling> {
	
	/** The premise. */
	private final Set<Handle> premise;
	
	/** The implication. */
	private final Handle      implication;
	
	/** The support. */
	private final Integer     support;
	
	/** The confidence. */
	private final Double      confidence;
	
	/**
	 * Instantiates a new file change coupling.
	 * 
	 * @param premise
	 *            the premise
	 * @param implication
	 *            the implication
	 * @param support
	 *            the support
	 * @param confidence
	 *            the confidence
	 * @param persistenceUtil
	 *            the persistence util
	 */
	public FileChangeCoupling(final Long[] premise, final Long implication, final Integer support,
	        final Double confidence, final PersistenceUtil persistenceUtil) {
		this.premise = new HashSet<Handle>();
		
		boolean commit = false;
		if (!persistenceUtil.activeTransaction()) {
			persistenceUtil.beginTransaction();
			commit = true;
		}
		for (final Long fileId : premise) {
			
			final Handle handle = persistenceUtil.loadById((long) fileId, Handle.class);
			if (handle == null) {
				throw new UnrecoverableError("Could not retrieve File with id " + fileId);
			}
			this.premise.add(handle);
		}
		
		Handle handle = persistenceUtil.loadById((long) implication, Handle.class);
		
		final Criteria<Handle> criteria = persistenceUtil.createCriteria(Handle.class).eq("generatedId",
		                                                                                  (long) implication);
		final List<Handle> load = persistenceUtil.load(criteria);
		handle = load.get(0);
		
		if (handle == null) {
			throw new UnrecoverableError("Could not retrieve File with id " + implication);
		}
		this.implication = handle;
		this.support = support;
		this.confidence = confidence;
		if (commit) {
			persistenceUtil.commitTransaction();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final FileChangeCoupling o) {
		if (getConfidence() < o.getConfidence()) {
			return 1;
		} else if (getConfidence() > o.getConfidence()) {
			return -1;
		} else {
			if (getSupport() > o.getSupport()) {
				return -1;
			} else if (getSupport() < o.getSupport()) {
				return 1;
			} else {
				if (getPremise().size() > o.getPremise().size()) {
					return -1;
				} else if (getPremise().size() < o.getPremise().size()) {
					return 1;
				} else {
					try {
						return getImplication().getLatestPath().compareTo(o.getImplication().getLatestPath());
					} catch (final NoSuchHandleException e) {
						throw new UnrecoverableError(e);
					}
				}
			}
		}
	}
	
	/**
	 * Gets the confidence.
	 * 
	 * @return the confidence
	 */
	public Double getConfidence() {
		return this.confidence;
	}
	
	/**
	 * Gets the implication.
	 * 
	 * @return the implication
	 */
	public Handle getImplication() {
		return this.implication;
	}
	
	/**
	 * Gets the premise.
	 * 
	 * @return the premise
	 */
	@Id
	public Set<Handle> getPremise() {
		return this.premise;
	}
	
	/**
	 * Gets the support.
	 * 
	 * @return the support
	 */
	public Integer getSupport() {
		return this.support;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ChangeCouplingRule [premise=" + Arrays.toString(this.premise.toArray(new Handle[this.premise.size()]))
		        + ", implication=" + this.implication + ", support=" + this.support + ", confidence=" + this.confidence
		        + "]";
	}
	
}
