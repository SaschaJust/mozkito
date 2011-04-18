package de.unisaarland.cs.st.reposuite.infozilla.Filters;

import java.util.ArrayList;
import java.util.List;

import de.unisaarland.cs.st.reposuite.infozilla.Elements.Enumerations.Enumeration;
import de.unisaarland.cs.st.reposuite.infozilla.Elements.Patch.Patch;
import de.unisaarland.cs.st.reposuite.infozilla.Elements.SourceCode.Java.CodeRegion;
import de.unisaarland.cs.st.reposuite.infozilla.Elements.StackTraces.Talkback.TalkbackTrace;
import de.unisaarland.cs.st.reposuite.infozilla.Helpers.RegExHelper;

/**
 * Class for runnning the complete filter chain on an mozilla input and gathering the results
 * @author Nicolas Bettenburg
 *
 */
public class FilterChainMozilla implements FilterChain {
	
	// Private Attributes
	private final FilterPatches        patchFilter;
	private final FilterTalkBack       stacktraceFilter;
	private final FilterSourceCodeJAVA sourcecodeFilter;
	private final FilterEnumeration    enumFilter;
	
	private String                     inputText  = "";
	private String                     outputText = "";
	
	private List<Patch>                patches;
	private List<TalkbackTrace>        traces;
	private List<CodeRegion>           regions;
	private List<Enumeration>          enumerations;
	
	// Constructor runs the experiments
	public FilterChainMozilla(final String inputText) {
		this.patchFilter = new FilterPatches();
		this.patchFilter.setRelaxed(true);
		
		this.stacktraceFilter = new FilterTalkBack();
		this.sourcecodeFilter = new FilterSourceCodeJAVA(FilterChainMozilla.class.getResource("Java_CodeDB.txt"));
		this.enumFilter = new FilterEnumeration();
		
		this.inputText = RegExHelper.makeLinuxNewlines(inputText);
		this.outputText = this.inputText;
		
		this.patches = this.patchFilter.runFilter(this.outputText);
		this.outputText = this.patchFilter.getOutputText();
		
		this.traces = this.stacktraceFilter.runFilter(this.outputText);
		this.outputText = this.stacktraceFilter.getOutputText();
		
		this.regions = this.sourcecodeFilter.runFilter(this.outputText);
		this.outputText = this.sourcecodeFilter.getOutputText();
		
		this.enumerations = this.enumFilter.runFilter(this.outputText);
		// The output of the filter chain
		this.outputText = this.sourcecodeFilter.getOutputText();
	}
	
	public FilterChainMozilla(final String inputText, final boolean runPatches, final boolean runTraces,
	        final boolean runSource, final boolean runEnums) {
		this.patchFilter = new FilterPatches();
		// patchFilter.setRelaxed(true);
		this.stacktraceFilter = new FilterTalkBack();
		this.sourcecodeFilter = new FilterSourceCodeJAVA(FilterChainMozilla.class.getResource("Java_CodeDB.txt"));
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
			this.traces = new ArrayList<TalkbackTrace>();
		}
		
		this.outputText = this.stacktraceFilter.getOutputText();
		
		if (runSource) {
			this.regions = this.sourcecodeFilter.runFilter(this.outputText);
		} else {
			this.regions = new ArrayList<CodeRegion>();
		}
		
		this.outputText = this.sourcecodeFilter.getOutputText();
		
		if (runEnums) {
			this.enumerations = this.enumFilter.runFilter(this.outputText);
		} else {
			this.enumerations = new ArrayList<Enumeration>();
		}
		
		// The output of the filter chain
		this.outputText = this.sourcecodeFilter.getOutputText();
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
	public List<TalkbackTrace> getTraces() {
		return this.traces;
	}
	
	/**
	 * @param inputText the inputText to set
	 */
	public void setInputText(final String inputText) {
		this.inputText = inputText;
	}
	
}
