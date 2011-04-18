package de.unisaarland.cs.st.reposuite.infozilla.Filters;

import java.util.ArrayList;
import java.util.List;

import de.unisaarland.cs.st.reposuite.infozilla.Elements.Enumerations.Enumeration;
import de.unisaarland.cs.st.reposuite.infozilla.Elements.Patch.Patch;
import de.unisaarland.cs.st.reposuite.infozilla.Elements.SourceCode.Java.CodeRegion;
import de.unisaarland.cs.st.reposuite.infozilla.Elements.StackTraces.Java.StackTrace;
import de.unisaarland.cs.st.reposuite.infozilla.Helpers.RegExHelper;

/**
 * Class for runnning the complete filter chain on an eclipse input and gathering the results
 * @author Nicolas Bettenburg
 *
 */
public class FilterChainEclipsePS implements FilterChain {
	
	// Private Attributes
	private final FilterPatches        patchFilter;
	private final FilterStackTraceJAVA stacktraceFilter;
	private final FilterSourceCodeJAVA sourcecodeFilter;
	private final FilterEnumeration    enumFilter;
	
	private String                     inputText  = "";
	private String                     outputText = "";
	
	private List<Patch>                patches;
	private List<StackTrace>           traces;
	private List<CodeRegion>           regions;
	private List<Enumeration>          enumerations;
	
	// Constructor runs the experiments
	public FilterChainEclipsePS(final String inputText) {
		this.patchFilter = new FilterPatches();
		this.stacktraceFilter = new FilterStackTraceJAVA();
		this.sourcecodeFilter = new FilterSourceCodeJAVA(FilterChainEclipsePS.class.getResource("Java_CodeDB.txt"));
		this.enumFilter = new FilterEnumeration();
		
		this.inputText = RegExHelper.makeLinuxNewlines(inputText);
		this.outputText = this.inputText;
		
		this.patches = this.patchFilter.runFilter(this.outputText);
		this.outputText = this.patchFilter.getOutputText();
		
		this.traces = this.stacktraceFilter.runFilter(this.outputText);
		this.outputText = this.stacktraceFilter.getOutputText();
		
		this.regions = new ArrayList<CodeRegion>();
		this.enumerations = new ArrayList<Enumeration>();
	}
	
	public FilterChainEclipsePS(final String inputText, final boolean runPatches, final boolean runTraces,
	        final boolean runSource, final boolean runEnums) {
		this.patchFilter = new FilterPatches();
		this.stacktraceFilter = new FilterStackTraceJAVA();
		this.sourcecodeFilter = new FilterSourceCodeJAVA(FilterChainEclipsePS.class.getResource("Java_CodeDB.txt"));
		this.enumFilter = new FilterEnumeration();
		
		this.inputText = RegExHelper.makeLinuxNewlines(inputText);
		this.outputText = this.inputText;
		
		if (runPatches) {
			this.patches = this.patchFilter.runFilter(this.outputText);
		} else {
			this.patches = new ArrayList<Patch>();
		}
		
		this.outputText = this.patchFilter.getOutputText();
		
		if (runTraces) {
			this.traces = this.stacktraceFilter.runFilter(this.outputText);
		} else {
			this.traces = new ArrayList<StackTrace>();
		}
		
		this.outputText = this.stacktraceFilter.getOutputText();
		
		if (runSource) {
			this.regions = this.sourcecodeFilter.runFilter(this.outputText);
			this.outputText = this.sourcecodeFilter.getOutputText();
		} else {
			this.regions = new ArrayList<CodeRegion>();
		}
		
		if (runEnums) {
			this.enumerations = this.enumFilter.runFilter(this.outputText);
			// The output of the filter chain
			this.outputText = this.sourcecodeFilter.getOutputText();
		} else {
			this.enumerations = new ArrayList<Enumeration>();
		}
		
	}
	
	/**
	 * @return the enumerations
	 */
	public List<Enumeration> getEnumerations() {
		return this.enumerations;
	}
	
	/**
	 * @return the inputText
	 */
	public String getInputText() {
		return this.inputText;
	}
	
	/**
	 * @return the outputText
	 */
	public String getOutputText() {
		return this.outputText;
	}
	
	/**
	 * @return the patches
	 */
	public List<Patch> getPatches() {
		return this.patches;
	}
	
	/**
	 * @return the regions
	 */
	public List<CodeRegion> getRegions() {
		return this.regions;
	}
	
	/**
	 * @return the traces
	 */
	public List<StackTrace> getTraces() {
		return this.traces;
	}
	
	/**
	 * @param inputText the inputText to set
	 */
	public void setInputText(final String inputText) {
		this.inputText = inputText;
	}
	
}
