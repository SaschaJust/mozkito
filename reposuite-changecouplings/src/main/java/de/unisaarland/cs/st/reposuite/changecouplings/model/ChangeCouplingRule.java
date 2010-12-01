package de.unisaarland.cs.st.reposuite.changecouplings.model;

import java.util.ArrayList;
import java.util.Collection;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;


public class ChangeCouplingRule implements Annotated, Comparable<ChangeCouplingRule> {
	
	private Integer[]         premise;
	private Integer           implication;
	private Integer           support;
	private Float             confidence;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2595605444114699401L;
	
	private ChangeCouplingRule() {
		
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
	
	public Integer[] getPremise() {
		return this.premise;
	}
	
	public Integer getSupport() {
		return this.support;
	}
	
	@Override
	public Collection<Annotated> saveFirst() {
		return new ArrayList<Annotated>(0);
	}
	
	@SuppressWarnings ("unused")
	private void setConfidence(final Float confidence) {
		this.confidence = confidence;
	}
	
	@SuppressWarnings ("unused")
	private void setImplication(final Integer implication) {
		this.implication = implication;
	}
	
	@SuppressWarnings ("unused")
	private void setPremise(final Integer[] premise) {
		this.premise = premise;
	}
	
	@SuppressWarnings ("unused")
	private void setSupport(final Integer support) {
		this.support = support;
	}
	
}
