/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *******************************************************************************/
package org.mozkito.changecouplings.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Id;

import org.mozkito.persistence.Criteria;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.versions.model.File;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;

/**
 * The Class FileChangeCoupling.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class FileChangeCoupling implements Comparable<FileChangeCoupling> {
	
	/** The premise. */
	private final Set<File> premise;
	
	/** The implication. */
	private final File      implication;
	
	/** The support. */
	private final Integer      support;
	
	/** The confidence. */
	private final Double       confidence;
	
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
		this.premise = new HashSet<File>();
		
		boolean commit = false;
		if (!persistenceUtil.activeTransaction()) {
			persistenceUtil.beginTransaction();
			commit = true;
		}
		for (final Long fileId : premise) {
			
			final File rcsFile = persistenceUtil.loadById((long) fileId, File.class);
			if (rcsFile == null) {
				throw new UnrecoverableError("Could not retrieve File with id " + fileId);
			}
			this.premise.add(rcsFile);
		}
		
		File rcsFile = persistenceUtil.loadById((long) implication, File.class);
		
		final Criteria<File> criteria = persistenceUtil.createCriteria(File.class).eq("generatedId",
		                                                                                    (long) implication);
		final List<File> load = persistenceUtil.load(criteria);
		rcsFile = load.get(0);
		
		if (rcsFile == null) {
			throw new UnrecoverableError("Could not retrieve File with id " + implication);
		}
		this.implication = rcsFile;
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
					return getImplication().getLatestPath().compareTo(o.getImplication().getLatestPath());
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
	public File getImplication() {
		return this.implication;
	}
	
	/**
	 * Gets the premise.
	 * 
	 * @return the premise
	 */
	@Id
	public Set<File> getPremise() {
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
		return "ChangeCouplingRule [premise=" + Arrays.toString(this.premise.toArray(new File[this.premise.size()]))
		        + ", implication=" + this.implication + ", support=" + this.support + ", confidence=" + this.confidence
		        + "]";
	}
	
}
