/**
 * 
 */
package de.unisaarland.cs.st.reposuite.infozilla.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.infozilla.model.image.Image;
import de.unisaarland.cs.st.reposuite.infozilla.model.log.Log;
import de.unisaarland.cs.st.reposuite.infozilla.model.patch.Patch;
import de.unisaarland.cs.st.reposuite.infozilla.model.stacktrace.Stacktrace;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class EnhancedReport {
	
	private static void parse(final Report report,
	                          final EnhancedReport enhancedReport) {
		// TODO Auto-generated method stub
		
	}
	
	Report                       originalReport;
	Report                       modifiedReport;
	
	Collection<Patch>            patches           = new LinkedList<Patch>();
	
	Collection<Image>            images            = new LinkedList<Image>();
	
	Collection<Stacktrace>       stacktraces       = new LinkedList<Stacktrace>();
	
	Collection<Log>              logs              = new LinkedList<Log>();
	
	Collection<StepsToReproduce> stepsToReproduce  = new LinkedList<StepsToReproduce>();
	
	Collection<ObservedBehavior> observedBehaviors = new LinkedList<ObservedBehavior>();
	
	Collection<ExpectedBehavior> expectedBehaviors = new LinkedList<ExpectedBehavior>();
	Collection<Link>             links             = new LinkedList<Link>();
	Map<String, Attachment>      attachments       = new HashMap<String, Attachment>();
	
	/**
	 * 
	 */
	public EnhancedReport(final Report report) {
		setReport(report);
		parse(report, this);
	}
	
	/**
	 * @return the attachments
	 */
	public Map<String, Attachment> getAttachments() {
		return this.attachments;
	}
	
	/**
	 * @return the expectedBehaviors
	 */
	public Collection<ExpectedBehavior> getExpectedBehaviors() {
		return this.expectedBehaviors;
	}
	
	/**
	 * @return the images
	 */
	public Collection<Image> getImages() {
		return this.images;
	}
	
	/**
	 * @return the links
	 */
	public Collection<Link> getLinks() {
		return this.links;
	}
	
	/**
	 * @return the logs
	 */
	public Collection<Log> getLogs() {
		return this.logs;
	}
	
	/**
	 * @return the modifiedReport
	 */
	public Report getModifiedReport() {
		return this.modifiedReport;
	}
	
	/**
	 * @return the observedBehaviors
	 */
	public Collection<ObservedBehavior> getObservedBehaviors() {
		return this.observedBehaviors;
	}
	
	/**
	 * @return the originalReport
	 */
	public Report getOriginalReport() {
		return this.originalReport;
	}
	
	/**
	 * @return the patches
	 */
	public Collection<Patch> getPatches() {
		return this.patches;
	}
	
	/**
	 * @return the originalReport
	 */
	public Report getReport() {
		return this.originalReport;
	}
	
	/**
	 * @return the stacktraces
	 */
	public Collection<Stacktrace> getStacktraces() {
		return this.stacktraces;
	}
	
	/**
	 * @return the stepsToReproduce
	 */
	public Collection<StepsToReproduce> getStepsToReproduce() {
		return this.stepsToReproduce;
	}
	
	/**
	 * @param attachments the attachments to set
	 */
	public void setAttachments(final Map<String, Attachment> attachments) {
		this.attachments = attachments;
	}
	
	/**
	 * @param expectedBehaviors the expectedBehaviors to set
	 */
	public void setExpectedBehaviors(final Collection<ExpectedBehavior> expectedBehaviors) {
		this.expectedBehaviors = expectedBehaviors;
	}
	
	/**
	 * @param images the images to set
	 */
	public void setImages(final Collection<Image> images) {
		this.images = images;
	}
	
	/**
	 * @param links the links to set
	 */
	public void setLinks(final Collection<Link> links) {
		this.links = links;
	}
	
	/**
	 * @param logs the logs to set
	 */
	public void setLogs(final Collection<Log> logs) {
		this.logs = logs;
	}
	
	/**
	 * @param modifiedReport the modifiedReport to set
	 */
	public void setModifiedReport(final Report modifiedReport) {
		this.modifiedReport = modifiedReport;
	}
	
	/**
	 * @param observedBehaviors the observedBehaviors to set
	 */
	public void setObservedBehaviors(final Collection<ObservedBehavior> observedBehaviors) {
		this.observedBehaviors = observedBehaviors;
	}
	
	/**
	 * @param originalReport the originalReport to set 
	 */
	public void setOriginalReport(final Report originalReport) {
		this.originalReport = originalReport;
	}
	
	/**
	 * @param patches the patches to set
	 */
	public void setPatches(final Collection<Patch> patches) {
		this.patches = patches;
	}
	
	/**
	 * @param originalReport the originalReport to set
	 */
	public void setReport(final Report report) {
		this.originalReport = report;
	}
	
	/**
	 * @param stacktraces the stacktraces to set
	 */
	public void setStacktraces(final Collection<Stacktrace> stacktraces) {
		this.stacktraces = stacktraces;
	}
	
	/**
	 * @param stepsToReproduce the stepsToReproduce to set
	 */
	public void setStepsToReproduce(final Collection<StepsToReproduce> stepsToReproduce) {
		this.stepsToReproduce = stepsToReproduce;
	}
	
}
