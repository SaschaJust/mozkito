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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.Id;

import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.persistence.Criteria;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSFile;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;

public class FileChangeCoupling implements Comparable<FileChangeCoupling> {
	
	private Set<RCSFile>  premise;
	private RCSFile       implication;
	private final Integer      support;
	private final Double       confidence;
	
	public FileChangeCoupling(final Integer[] premise, final Integer implication, final Integer support,
			final Double confidence, final PersistenceUtil persistenceUtil) {
		this.premise = new HashSet<RCSFile>();
		
		boolean commit = false;
		if (!persistenceUtil.activeTransaction()) {
			persistenceUtil.beginTransaction();
			commit = true;
		}
		for (Integer fileId : premise) {
			
			RCSFile rcsFile = persistenceUtil.loadById((long) fileId, RCSFile.class);
			if (rcsFile == null) {
				throw new UnrecoverableError("Could not retrieve RCSFile with id " + fileId);
			}
			this.premise.add(rcsFile);
		}
		
		RCSFile rcsFile = persistenceUtil.loadById((long) implication, RCSFile.class);
		
		Criteria<RCSFile> criteria = persistenceUtil.createCriteria(RCSFile.class)
				.eq("generatedId", (long) implication);
		List<RCSFile> load = persistenceUtil.load(criteria);
		rcsFile = load.get(0);
		
		
		if (rcsFile == null) {
			throw new UnrecoverableError("Could not retrieve RCSFile with id " + implication);
		}
		this.implication = rcsFile;
		this.support = support;
		this.confidence = confidence;
		if (commit) {
			persistenceUtil.commitTransaction();
		}
	}
	
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
	
	public Double getConfidence() {
		return confidence;
	}
	
	public RCSFile getImplication() {
		return implication;
	}
	
	@Id
	public Set<RCSFile> getPremise() {
		return premise;
	}
	
	public Integer getSupport() {
		return support;
	}
	
	public SerialFileChangeCoupling serialize(RCSTransaction transaction) {
		Set<RCSTransaction> parents = transaction.getParents();
		List<String> premise = new LinkedList<String>();
		for (RCSFile file : getPremise()) {
			premise.add(file.getPath(transaction));
		}
		return new SerialFileChangeCoupling(premise, getImplication().getPath(transaction), getSupport(),
				getConfidence());
	}
	
	@Override
	public String toString() {
		return "ChangeCouplingRule [premise=" + Arrays.toString(premise.toArray(new RCSFile[premise.size()]))
				+ ", implication=" + implication + ", support=" + support + ", confidence=" + confidence + "]";
	}
	
}
