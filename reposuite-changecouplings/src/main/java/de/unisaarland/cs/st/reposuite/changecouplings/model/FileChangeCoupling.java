package de.unisaarland.cs.st.reposuite.changecouplings.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Id;

import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSFile;

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
		for (Integer fileId : premise) {
			
			RCSFile rcsFile = persistenceUtil.loadById((long) fileId, RCSFile.class);
			if (rcsFile == null) {
				throw new UnrecoverableError("Could not retrieve RCSFile with id " + fileId);
			}
			this.premise.add(rcsFile);
		}
		
		RCSFile rcsFile = persistenceUtil.loadById((long) implication, RCSFile.class);;
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
				} else if (getImplication().getGeneratedId() < o.getImplication().getGeneratedId()) {
					return -1;
				} else if (getImplication().getGeneratedId() > o.getImplication().getGeneratedId()) {
					return 1;
				} else {
					return 0;
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
	
	@Override
	public String toString() {
		return "ChangeCouplingRule [premise=" + Arrays.toString(this.premise.toArray(new RCSFile[this.premise.size()]))
		        + ", implication=" + this.implication + ", support=" + this.support + ", confidence=" + this.confidence
		        + "]";
	}
	
}
