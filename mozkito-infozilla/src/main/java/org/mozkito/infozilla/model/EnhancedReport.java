/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
/**
 * 
 */
package org.mozkito.infozilla.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.mozkito.infozilla.model.attachment.Attachment;
import org.mozkito.infozilla.model.image.Image;
import org.mozkito.infozilla.model.itemization.ExpectedBehavior;
import org.mozkito.infozilla.model.itemization.ObservedBehavior;
import org.mozkito.infozilla.model.itemization.StepsToReproduce;
import org.mozkito.infozilla.model.link.Link;
import org.mozkito.infozilla.model.log.Log;
import org.mozkito.infozilla.model.patch.Patch;
import org.mozkito.infozilla.model.stacktrace.Stacktrace;
import org.mozkito.issues.tracker.model.Report;


/**
 * The Class EnhancedReport.
 *
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class EnhancedReport {
	
	/**
	 * Parses the.
	 *
	 * @param report the report
	 * @param enhancedReport the enhanced report
	 */
	private static void parse(final Report report,
	                          final EnhancedReport enhancedReport) {
		// TODO Auto-generated method stub
		
	}
	
	/** The original report. */
	Report                       originalReport;
	
	/** The modified report. */
	Report                       modifiedReport;
	
	/** The patches. */
	Collection<Patch>            patches           = new LinkedList<Patch>();
	
	/** The images. */
	Collection<Image>            images            = new LinkedList<Image>();
	
	/** The stacktraces. */
	Collection<Stacktrace>       stacktraces       = new LinkedList<Stacktrace>();
	
	/** The logs. */
	Collection<Log>              logs              = new LinkedList<Log>();
	
	/** The steps to reproduce. */
	Collection<StepsToReproduce> stepsToReproduce  = new LinkedList<StepsToReproduce>();
	
	/** The observed behaviors. */
	Collection<ObservedBehavior> observedBehaviors = new LinkedList<ObservedBehavior>();
	
	/** The expected behaviors. */
	Collection<ExpectedBehavior> expectedBehaviors = new LinkedList<ExpectedBehavior>();
	
	/** The links. */
	Collection<Link>             links             = new LinkedList<Link>();
	
	/** The attachments. */
	Map<String, Attachment>      attachments       = new HashMap<String, Attachment>();
	
	/**
	 * Instantiates a new enhanced report.
	 *
	 * @param report the report
	 */
	public EnhancedReport(final Report report) {
		super();
		
		setReport(report);
		parse(report, this);
	}
	
	/**
	 * Gets the attachments.
	 *
	 * @return the attachments
	 */
	public Map<String, Attachment> getAttachments() {
		return this.attachments;
	}
	
	/**
	 * Gets the expected behaviors.
	 *
	 * @return the expectedBehaviors
	 */
	public Collection<ExpectedBehavior> getExpectedBehaviors() {
		return this.expectedBehaviors;
	}
	
	/**
	 * Gets the images.
	 *
	 * @return the images
	 */
	public Collection<Image> getImages() {
		return this.images;
	}
	
	/**
	 * Gets the links.
	 *
	 * @return the links
	 */
	public Collection<Link> getLinks() {
		return this.links;
	}
	
	/**
	 * Gets the logs.
	 *
	 * @return the logs
	 */
	public Collection<Log> getLogs() {
		return this.logs;
	}
	
	/**
	 * Gets the modified report.
	 *
	 * @return the modifiedReport
	 */
	public Report getModifiedReport() {
		return this.modifiedReport;
	}
	
	/**
	 * Gets the observed behaviors.
	 *
	 * @return the observedBehaviors
	 */
	public Collection<ObservedBehavior> getObservedBehaviors() {
		return this.observedBehaviors;
	}
	
	/**
	 * Gets the original report.
	 *
	 * @return the originalReport
	 */
	public Report getOriginalReport() {
		return this.originalReport;
	}
	
	/**
	 * Gets the patches.
	 *
	 * @return the patches
	 */
	public Collection<Patch> getPatches() {
		return this.patches;
	}
	
	/**
	 * Gets the report.
	 *
	 * @return the originalReport
	 */
	public Report getReport() {
		return this.originalReport;
	}
	
	/**
	 * Gets the stacktraces.
	 *
	 * @return the stacktraces
	 */
	public Collection<Stacktrace> getStacktraces() {
		return this.stacktraces;
	}
	
	/**
	 * Gets the steps to reproduce.
	 *
	 * @return the stepsToReproduce
	 */
	public Collection<StepsToReproduce> getStepsToReproduce() {
		return this.stepsToReproduce;
	}
	
	/**
	 * Sets the attachments.
	 *
	 * @param attachments the attachments to set
	 */
	public void setAttachments(final Map<String, Attachment> attachments) {
		this.attachments = attachments;
	}
	
	/**
	 * Sets the expected behaviors.
	 *
	 * @param expectedBehaviors the expectedBehaviors to set
	 */
	public void setExpectedBehaviors(final Collection<ExpectedBehavior> expectedBehaviors) {
		this.expectedBehaviors = expectedBehaviors;
	}
	
	/**
	 * Sets the images.
	 *
	 * @param images the images to set
	 */
	public void setImages(final Collection<Image> images) {
		this.images = images;
	}
	
	/**
	 * Sets the links.
	 *
	 * @param links the links to set
	 */
	public void setLinks(final Collection<Link> links) {
		this.links = links;
	}
	
	/**
	 * Sets the logs.
	 *
	 * @param logs the logs to set
	 */
	public void setLogs(final Collection<Log> logs) {
		this.logs = logs;
	}
	
	/**
	 * Sets the modified report.
	 *
	 * @param modifiedReport the modifiedReport to set
	 */
	public void setModifiedReport(final Report modifiedReport) {
		this.modifiedReport = modifiedReport;
	}
	
	/**
	 * Sets the observed behaviors.
	 *
	 * @param observedBehaviors the observedBehaviors to set
	 */
	public void setObservedBehaviors(final Collection<ObservedBehavior> observedBehaviors) {
		this.observedBehaviors = observedBehaviors;
	}
	
	/**
	 * Sets the original report.
	 *
	 * @param originalReport the originalReport to set
	 */
	public void setOriginalReport(final Report originalReport) {
		this.originalReport = originalReport;
	}
	
	/**
	 * Sets the patches.
	 *
	 * @param patches the patches to set
	 */
	public void setPatches(final Collection<Patch> patches) {
		this.patches = patches;
	}
	
	/**
	 * Sets the report.
	 *
	 * @param report the new report
	 */
	public void setReport(final Report report) {
		this.originalReport = report;
	}
	
	/**
	 * Sets the stacktraces.
	 *
	 * @param stacktraces the stacktraces to set
	 */
	public void setStacktraces(final Collection<Stacktrace> stacktraces) {
		this.stacktraces = stacktraces;
	}
	
	/**
	 * Sets the steps to reproduce.
	 *
	 * @param stepsToReproduce the stepsToReproduce to set
	 */
	public void setStepsToReproduce(final Collection<StepsToReproduce> stepsToReproduce) {
		this.stepsToReproduce = stepsToReproduce;
	}
	
}
