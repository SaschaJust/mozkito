package de.unisaarland.cs.st.reposuite.infozilla.filters.chain;

import java.util.ArrayList;
import java.util.List;

import de.unisaarland.cs.st.reposuite.infozilla.Elements.Patch.Patch;
import de.unisaarland.cs.st.reposuite.infozilla.Elements.SourceCode.Java.CodeRegion;
import de.unisaarland.cs.st.reposuite.infozilla.Elements.StackTraces.Java.StackTrace;
import de.unisaarland.cs.st.reposuite.infozilla.Helpers.RegExHelper;
import de.unisaarland.cs.st.reposuite.infozilla.filters.enumeration.EnumerationFilter;
import de.unisaarland.cs.st.reposuite.infozilla.filters.patch.UnifiedDiffPatchFilter;
import de.unisaarland.cs.st.reposuite.infozilla.filters.sourcecode.JavaSourceCodeFilter;
import de.unisaarland.cs.st.reposuite.infozilla.filters.stacktrace.JavaStackTraceFilter;
import de.unisaarland.cs.st.reposuite.infozilla.model.itemization.Itemization;

/**
 * Class for runnning the complete filter chain on an eclipse input and gathering the results
 * @author Nicolas Bettenburg
 *
 */
public class FilterChainEclipsePS implements FilterChain {
	
	// Private Attributes
	private final UnifiedDiffPatchFilter        unifiedDiffPatchFilter;
	private final JavaStackTraceFilter stacktraceFilter;
	private final JavaSourceCodeFilter sourcecodeFilter;
	private final EnumerationFilter    enumFilter;
	
	private String                     inputText  = "";
	private String                     outputText = "";
	
	private List<Patch>                patches;
	private List<StackTrace>           traces;
	private List<CodeRegion>           regions;
	private List<Itemization>          enumerations;
	
	// Constructor runs the experiments
	public FilterChainEclipsePS(final String inputText) {
		this.unifiedDiffPatchFilter = new UnifiedDiffPatchFilter();
		this.stacktraceFilter = new JavaStackTraceFilter();
		this.sourcecodeFilter = new JavaSourceCodeFilter(FilterChainEclipsePS.class.getResource("Java_CodeDB.txt"));
		this.enumFilter = new EnumerationFilter();
		
		this.inputText = RegExHelper.makeLinuxNewlines(inputText);
		this.outputText = this.inputText;
		
		this.patches = this.unifiedDiffPatchFilter.runFilter(this.outputText);
		this.outputText = this.unifiedDiffPatchFilter.getOutputText();
		
		this.traces = this.stacktraceFilter.runFilter(this.outputText);
		this.outputText = this.stacktraceFilter.getOutputText();
		
		this.regions = new ArrayList<CodeRegion>();
		this.enumerations = new ArrayList<Itemization>();
	}
	
	public FilterChainEclipsePS(final String inputText, final boolean runPatches, final boolean runTraces,
	        final boolean runSource, final boolean runEnums) {
		this.unifiedDiffPatchFilter = new UnifiedDiffPatchFilter();
		this.stacktraceFilter = new JavaStackTraceFilter();
		this.sourcecodeFilter = new JavaSourceCodeFilter(FilterChainEclipsePS.class.getResource("Java_CodeDB.txt"));
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
			this.enumerations = new ArrayList<Itemization>();
		}
		
	}
	
	/**
	 * @return the enumerations
	 */
	public List<Itemization> getEnumerations() {
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
