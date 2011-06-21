/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.changecouplings.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Id;

import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.persistence.Criteria;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaMethodDefinition;

public class MethodChangeCoupling implements Comparable<MethodChangeCoupling> {
	
	private final Set<JavaMethodDefinition> premise;
	private final JavaMethodDefinition      implication;
	private final Integer                   support;
	private final Double                    confidence;
	
	public MethodChangeCoupling(final String[] premise, final String implication, final Integer support,
	        final Double confidence, final PersistenceUtil persistenceUtil) {
		this.premise = new HashSet<JavaMethodDefinition>();
		
		boolean commit = false;
		if (!persistenceUtil.activeTransaction()) {
			persistenceUtil.beginTransaction();
			commit = true;
		}
		for (String p : premise) {
			
			Criteria<JavaMethodDefinition> criteria = persistenceUtil.createCriteria(JavaMethodDefinition.class)
			                                                         .eq("fullQualifiedName", p);
			List<JavaMethodDefinition> defs = persistenceUtil.load(criteria);
			if ((defs == null) || (defs.size() != 1)) {
				throw new UnrecoverableError("Could not retrieve RCSFile with id " + p);
			}
			this.premise.add(defs.get(0));
		}
		
		Criteria<JavaMethodDefinition> criteria = persistenceUtil.createCriteria(JavaMethodDefinition.class)
		                                                         .eq("fullQualifiedName", implication);
		List<JavaMethodDefinition> defs = persistenceUtil.load(criteria);
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
	
	public Double getConfidence() {
		return confidence;
	}
	
	public JavaMethodDefinition getImplication() {
		return implication;
	}
	
	@Id
	public Set<JavaMethodDefinition> getPremise() {
		return premise;
	}
	
	public Integer getSupport() {
		return support;
	}
	
	@Override
	public String toString() {
		return "ChangeCouplingRule [premise="
		        + Arrays.toString(premise.toArray(new JavaMethodDefinition[premise.size()])) + ", implication="
		        + implication + ", support=" + support + ", confidence=" + confidence + "]";
	}
	
}
