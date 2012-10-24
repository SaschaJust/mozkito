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

import org.mozkito.codeanalysis.model.JavaMethodDefinition;
import org.mozkito.persistence.Criteria;
import org.mozkito.persistence.PersistenceUtil;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;

/**
 * The Class MethodChangeCoupling.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class MethodChangeCoupling implements Comparable<MethodChangeCoupling> {
	
	/** The premise. */
	private final Set<JavaMethodDefinition> premise;
	
	/** The implication. */
	private final JavaMethodDefinition      implication;
	
	/** The support. */
	private final Integer                   support;
	
	/** The confidence. */
	private final Double                    confidence;
	
	protected MethodChangeCoupling(final Set<JavaMethodDefinition> premise, final JavaMethodDefinition implication,
	        final Integer support, final Double confidence) {
		this.premise = premise;
		this.implication = implication;
		this.support = support;
		this.confidence = confidence;
	}
	
	/**
	 * Instantiates a new method change coupling.
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
	public MethodChangeCoupling(final String[] premise, final String implication, final Integer support,
	        final Double confidence, final PersistenceUtil persistenceUtil) {
		this.premise = new HashSet<JavaMethodDefinition>();
		
		boolean commit = false;
		if (!persistenceUtil.activeTransaction()) {
			persistenceUtil.beginTransaction();
			commit = true;
		}
		for (final String p : premise) {
			
			final Criteria<JavaMethodDefinition> criteria = persistenceUtil.createCriteria(JavaMethodDefinition.class)
			                                                               .eq("fullQualifiedName", p);
			final List<JavaMethodDefinition> defs = persistenceUtil.load(criteria);
			if ((defs == null) || (defs.size() != 1)) {
				throw new UnrecoverableError("Could not retrieve JavaMethodDefinition with fullQualifiedName " + p);
			}
			this.premise.add(defs.get(0));
		}
		
		final Criteria<JavaMethodDefinition> criteria = persistenceUtil.createCriteria(JavaMethodDefinition.class)
		                                                               .eq("fullQualifiedName", implication);
		final List<JavaMethodDefinition> defs = persistenceUtil.load(criteria);
		if ((defs == null) || (defs.size() != 1)) {
			throw new UnrecoverableError("Could not retrieve RCSFile with id " + implication);
		}
		this.implication = defs.get(0);
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
	public int compareTo(final MethodChangeCoupling o) {
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
					return getImplication().getFullQualifiedName().compareTo(o.getImplication().getFullQualifiedName());
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
	public JavaMethodDefinition getImplication() {
		return this.implication;
	}
	
	/**
	 * Gets the premise.
	 * 
	 * @return the premise
	 */
	@Id
	public Set<JavaMethodDefinition> getPremise() {
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
		return "ChangeCouplingRule [premise="
		        + Arrays.toString(this.premise.toArray(new JavaMethodDefinition[this.premise.size()]))
		        + ", implication=" + this.implication + ", support=" + this.support + ", confidence=" + this.confidence
		        + "]";
	}
	
}
