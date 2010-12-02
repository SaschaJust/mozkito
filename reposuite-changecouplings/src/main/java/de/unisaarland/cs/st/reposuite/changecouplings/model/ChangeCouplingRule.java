package de.unisaarland.cs.st.reposuite.changecouplings.model;

import java.util.Arrays;

import javax.persistence.Id;

public class ChangeCouplingRule implements Comparable<ChangeCouplingRule> {
	
	private final Integer[] premise;
	private final Integer   implication;
	private final Integer   support;
	private final Double    confidence;
	
	public ChangeCouplingRule(final Integer[] premise, final Integer implication, final Integer support,
			final Double confidence) {
		this.premise = premise;
		this.implication = implication;
		this.support = support;
		this.confidence = confidence;
	}
	
	@Override
	public int compareTo(final ChangeCouplingRule o) {
		if(this.getConfidence() < o.getConfidence()){
			return 1;
		} else if (this.getConfidence() > o.getConfidence()) {
			return -1;
		}else{
			if(this.getSupport() > o.getSupport()){
				return -1;
			}else if(this.getSupport() < o.getSupport()){
				return 1;
			}else{
				if (this.getPremise().length > o.getPremise().length) {
					return -1;
				} else if (this.getPremise().length < o.getPremise().length) {
					return 1;
				} else {
					return this.getImplication().compareTo(o.getImplication());
				}
			}
		}
	}
	
	public Double getConfidence() {
		return this.confidence;
	}
	
	public Integer getImplication() {
		return this.implication;
	}
	
	@Id
	public Integer[] getPremise() {
		return this.premise;
	}
	
	public Integer getSupport() {
		return this.support;
	}
	
	@Override
	public String toString() {
		return "ChangeCouplingRule [premise=" + Arrays.toString(this.premise) + ", implication=" + this.implication
		+ ", support=" + this.support + ", confidence=" + this.confidence + "]";
	}
	
}
