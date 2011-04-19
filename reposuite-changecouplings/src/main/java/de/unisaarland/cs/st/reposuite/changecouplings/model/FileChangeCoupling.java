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
			RCSFile rcsFile = persistenceUtil.fetchRCSFile((long) fileId);
			if (rcsFile == null) {
				throw new UnrecoverableError("Could not retrieve RCSFile with id " + fileId);
			}
			this.premise.add(rcsFile);
		}
		
		RCSFile rcsFile = persistenceUtil.fetchRCSFile((long) implication);
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
	
	@Override
	public String toString() {
		return "ChangeCouplingRule [premise=" + Arrays.toString(premise.toArray(new RCSFile[premise.size()]))
		        + ", implication=" + implication + ", support=" + support + ", confidence=" + confidence + "]";
	}
	
}
