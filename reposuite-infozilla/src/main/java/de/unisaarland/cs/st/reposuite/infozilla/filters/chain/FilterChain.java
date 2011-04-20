package de.unisaarland.cs.st.reposuite.infozilla.filters.chain;

import java.util.List;

public interface FilterChain {
	
	/**
	 * @return the outputText
	 */
	public String getOutputText();
	
	/**
	 * @return the patches
	 */
	public List<?> getPatches();
	
	/**
	 * @return the traces
	 */
	public List<?> getTraces();
	
	/**
	 * @return the regions
	 */
	public List<?> getRegions();
	
	/**
	 * @return the enumerations
	 */
	public List<?> getEnumerations();
	
	/**
	 * @param inputText the inputText to set
	 */
	public void setInputText(String inputText);
	
	/**
	 * @return the inputText
	 */
	public String getInputText();
	
}
