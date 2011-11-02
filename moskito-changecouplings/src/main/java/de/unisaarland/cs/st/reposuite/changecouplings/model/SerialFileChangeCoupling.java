package de.unisaarland.cs.st.reposuite.changecouplings.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SerialFileChangeCoupling implements Serializable {
	
	/**
	 * 
	 */
	private static final long       serialVersionUID = -5562424840980704091L;
	
	private final ArrayList<String> premise = new ArrayList<String>();
	private final String            implication;
	private final Integer           support;
	private final Double            confidence;
	
	protected SerialFileChangeCoupling(List<String> premise, final String implication,
			final Integer support, final Double confidence) {
		this.getPremise().addAll(premise);
		this.implication = new String(implication);
		this.confidence = new Double(confidence);
		this.support = new Integer(support);
	}
	
	public Double getConfidence() {
		return confidence;
	}
	
	public String getImplication() {
		return implication;
	}
	
	public ArrayList<String> getPremise() {
		return premise;
	}
	
	public Integer getSupport() {
		return support;
	}
}
