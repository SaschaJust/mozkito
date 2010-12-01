package de.unisaarland.cs.st.reposuite.changecouplings.model;

import javax.persistence.Id;

public class ChangeCouplingRule implements Comparable<ChangeCouplingRule> {
	
	private final Integer[]         premise;
	private final Integer           implication;
	private final Integer           support;
	private final Float             confidence;
	
	private ChangeCouplingRule(final Integer[] premise, final Integer implication, final Integer support,
			final Float confidence) {
		this.premise = premise;
		this.implication = implication;
		this.support = support;
		this.confidence = confidence;
	}
	
	@Override
	public int compareTo(final ChangeCouplingRule o) {
		if(this.getConfidence() < o.getConfidence()){
			return -1;
		}else if (this.getConfidence() >  o.getConfidence()){
			return 1;
		}else{
			if(this.getSupport() > o.getSupport()){
				return -1;
			}else if(this.getSupport() < o.getSupport()){
				return 1;
			}else{
				return 0;
			}
		}
	}
	
	public Float getConfidence() {
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
}
