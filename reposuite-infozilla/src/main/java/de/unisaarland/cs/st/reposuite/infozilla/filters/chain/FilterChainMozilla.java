package de.unisaarland.cs.st.reposuite.infozilla.filters.chain;

import java.util.ArrayList;
import java.util.List;

import de.unisaarland.cs.st.reposuite.infozilla.Elements.Enumerations.Enumeration;
import de.unisaarland.cs.st.reposuite.infozilla.Elements.Patch.Patch;
import de.unisaarland.cs.st.reposuite.infozilla.Elements.SourceCode.Java.CodeRegion;
import de.unisaarland.cs.st.reposuite.infozilla.Elements.StackTraces.Talkback.TalkbackTrace;
import de.unisaarland.cs.st.reposuite.infozilla.Helpers.RegExHelper;
import de.unisaarland.cs.st.reposuite.infozilla.filters.enumeration.EnumerationFilter;
import de.unisaarland.cs.st.reposuite.infozilla.filters.patch.UnifiedDiffPatchFilter;
import de.unisaarland.cs.st.reposuite.infozilla.filters.sourcecode.JavaSourceCodeFilter;
import de.unisaarland.cs.st.reposuite.infozilla.filters.stacktrace.TalkBackFilter;

/**
 * Class for runnning the complete filter chain on an mozilla input and gathering the results
 * @author Nicolas Bettenburg
 *
 */
public class FilterChainMozilla implements FilterChain {
	
	// Private Attributes
	private final UnifiedDiffPatchFilter        unifiedDiffPatchFilter;
	private final TalkBackFilter       stacktraceFilter;
	private final JavaSourceCodeFilter sourcecodeFilter;
	private final EnumerationFilter    enumFilter;
	
	private String                     inputText  = "";
	private String                     outputText = "";
	
	private List<Patch>                patches;
	private List<TalkbackTrace>        traces;
	private List<CodeRegion>           regions;
	private List<Enumeration>          enumerations;
	
	// Constructor runs the experiments
	public FilterChainMozilla(final String inputText) {
		this.unifiedDiffPatchFilter = new UnifiedDiffPatchFilter();
		this.unifiedDiffPatchFilter.setRelaxed(true);
		
		this.stacktraceFilter = new TalkBackFilter();
		this.sourcecodeFilter = new JavaSourceCodeFilter(FilterChainMozilla.class.getResource("Java_CodeDB.txt"));
		this.enumFilter = new EnumerationFilter();
		
		this.inputText = RegExHelper.makeLinuxNewlines(inputText);
		this.outputText = this.inputText;
		
		this.patches = this.unifiedDiffPatchFilter.runFilter(this.outputText);
		this.outputText = this.unifiedDiffPatchFilter.getOutputText();
		
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
		this.unifiedDiffPatchFilter = new UnifiedDiffPatchFilter();
		// unifiedDiffPatchFilter.setRelaxed(true);
		this.stacktraceFilter = new TalkBackFilter();
		this.sourcecodeFilter = new JavaSourceCodeFilter(FilterChainMozilla.class.getResource("Java_CodeDB.txt"));
		this.enumFilter = new EnumerationFilter();
		
		this.inputText = RegExHelper.makeLinuxNewlines(inputText);
		this.outputText = this.inputText;
		
		if (runPatches) {
			this.patches = this.unifiedDiffPatchFilter.runFilter(this.outputText);
		} else {
			this.patches = new ArrayList<Patch>();
		}
		
		this.outputText = this.unifiedDiffPatchFilter.getOutputText();
		
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
