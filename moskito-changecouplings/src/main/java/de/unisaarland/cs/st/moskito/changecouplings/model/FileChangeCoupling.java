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
package de.unisaarland.cs.st.moskito.changecouplings.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.Id;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.moskito.persistence.Criteria;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.rcs.model.RCSFile;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

public class FileChangeCoupling implements Comparable<FileChangeCoupling> {
	
	private final Set<RCSFile> premise;
	private final RCSFile      implication;
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
		for (final Integer fileId : premise) {
			
			final RCSFile rcsFile = persistenceUtil.loadById((long) fileId, RCSFile.class);
			if (rcsFile == null) {
				throw new UnrecoverableError("Could not retrieve RCSFile with id " + fileId);
			}
			this.premise.add(rcsFile);
		}
		
		RCSFile rcsFile = persistenceUtil.loadById((long) implication, RCSFile.class);
		
		final Criteria<RCSFile> criteria = persistenceUtil.createCriteria(RCSFile.class).eq("generatedId",
		                                                                                    (long) implication);
		final List<RCSFile> load = persistenceUtil.load(criteria);
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
		return this.confidence;
	}
	
	public RCSFile getImplication() {
		return this.implication;
	}
	
	@Id
	public Set<RCSFile> getPremise() {
		return this.premise;
	}
	
	public Integer getSupport() {
		return this.support;
	}
	
	public SerialFileChangeCoupling serialize(final RCSTransaction transaction) {
		final List<String> premise = new LinkedList<String>();
		for (final RCSFile file : getPremise()) {
			premise.add(file.getPath(transaction));
		}
		return new SerialFileChangeCoupling(premise, getImplication().getPath(transaction), getSupport(),
		                                    getConfidence());
	}
	
	@Override
	public String toString() {
		return "ChangeCouplingRule [premise=" + Arrays.toString(this.premise.toArray(new RCSFile[this.premise.size()]))
		        + ", implication=" + this.implication + ", support=" + this.support + ", confidence=" + this.confidence
		        + "]";
	}
	
}
