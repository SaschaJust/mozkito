package de.unisaarland.cs.st.reposuite.infozilla.filters.chain;

import java.util.List;

public interface FilterChain {
	
	/**
	 * @return the enumerations
	 */
	public List<?> getEnumerations();
	
	/**
	 * @return the inputText
	 */
	public String getInputText();
	
	/**
	 * @return the outputText
	 */
	public String getOutputText();
	
	/**
	 * @return the patches
	 */
	public List<?> getPatches();
	
	/**
	 * @return the regions
	 */
	public List<?> getRegions();
	
	/**
	 * @return the traces
	 */
	public List<?> getTraces();
	
	/**
	 * @param inputText the inputText to set
	 */
	public void setInputText(String inputText);
	
}
