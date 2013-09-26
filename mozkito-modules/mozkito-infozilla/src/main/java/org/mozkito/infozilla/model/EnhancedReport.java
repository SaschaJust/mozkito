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

import javax.persistence.Entity;
import javax.persistence.Id;

import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;

import org.mozkito.infozilla.model.attachment.Attachment;
import org.mozkito.infozilla.model.image.Image;
import org.mozkito.infozilla.model.itemization.ExpectedBehavior;
import org.mozkito.infozilla.model.itemization.Listing;
import org.mozkito.infozilla.model.itemization.ObservedBehavior;
import org.mozkito.infozilla.model.itemization.StepsToReproduce;
import org.mozkito.infozilla.model.link.Link;
import org.mozkito.infozilla.model.log.Log;
import org.mozkito.infozilla.model.patch.Patch;
import org.mozkito.infozilla.model.stacktrace.Stacktrace;
import org.mozkito.issues.model.Report;
import org.mozkito.persistence.Annotated;
import org.mozkito.utilities.commons.JavaUtils;

/**
 * The Class EnhancedReport.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
@Entity
public class EnhancedReport implements Annotated {
	
	/** The Constant serialVersionUID. */
	private static final long            serialVersionUID  = -6200370567492281526L;
	
	/** The attachments. */
	private Map<String, Attachment>      attachments       = new HashMap<String, Attachment>();
	
	/** The code fragments. */
	private Collection<String>           codeFragments     = new LinkedList<>();
	
	/** The expected behaviors. */
	private Collection<ExpectedBehavior> expectedBehaviors = new LinkedList<ExpectedBehavior>();
	
	/** The images. */
	private Collection<Image>            images            = new LinkedList<Image>();
	
	/** The links. */
	private Collection<Link>             links             = new LinkedList<Link>();
	
	/** The listings. */
	private Collection<Listing>          listings          = new LinkedList<>();
	
	/** The logs. */
	private Collection<Log>              logs              = new LinkedList<Log>();
	
	/** The observed behaviors. */
	private Collection<ObservedBehavior> observedBehaviors = new LinkedList<ObservedBehavior>();
	
	/** The original report. */
	private Report                       originalReport;
	
	/** The patches. */
	private Collection<Patch>            patches           = new LinkedList<Patch>();
	
	/** The stacktraces. */
	private Collection<Stacktrace>       stacktraces       = new LinkedList<Stacktrace>();
	
	/** The steps to reproduce. */
	private Collection<StepsToReproduce> stepsToReproduce  = new LinkedList<StepsToReproduce>();
	
	/** The id. */
	private String                       id;
	
	/**
	 * Instantiates a new enhanced report.
	 * 
	 * @param id
	 *            the id
	 */
	public EnhancedReport(final String id) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// body
			this.id = id;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
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
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.persistence.Annotated#getClassName()
	 */
	@Override
	public String getClassName() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return JavaUtils.getHandle(this);
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the code fragments.
	 * 
	 * @return the code fragments
	 */
	public Collection<String> getCodeFragments() {
		// PRECONDITIONS
		
		try {
			return this.codeFragments;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.codeFragments, "Field '%s' in '%s'.", "codeFragments", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
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
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	public String getId() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.id;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
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
	 * Gets the listings.
	 * 
	 * @return the listings
	 */
	public Collection<Listing> getListings() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.listings;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
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
	 * @param attachments
	 *            the attachments to set
	 */
	public void setAttachments(final Map<String, Attachment> attachments) {
		this.attachments = attachments;
	}
	
	/**
	 * Sets the code fragments.
	 * 
	 * @param codeFragments
	 *            the new code fragments
	 */
	public void setCodeFragments(final Collection<String> codeFragments) {
		// PRECONDITIONS
		Condition.notNull(codeFragments, "Argument '%s' in '%s'.", "codeFragments", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			this.codeFragments = codeFragments;
		} finally {
			// POSTCONDITIONS
			CompareCondition.equals(this.codeFragments, codeFragments,
			                        "After setting a value, the corresponding field has to hold the same value as used as a parameter within the setter."); //$NON-NLS-1$
		}
	}
	
	/**
	 * Sets the expected behaviors.
	 * 
	 * @param expectedBehaviors
	 *            the expectedBehaviors to set
	 */
	public void setExpectedBehaviors(final Collection<ExpectedBehavior> expectedBehaviors) {
		this.expectedBehaviors = expectedBehaviors;
	}
	
	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the id to set
	 */
	public void setId(final String id) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.id = id;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the images.
	 * 
	 * @param images
	 *            the images to set
	 */
	public void setImages(final Collection<Image> images) {
		this.images = images;
	}
	
	/**
	 * Sets the links.
	 * 
	 * @param links
	 *            the links to set
	 */
	public void setLinks(final Collection<Link> links) {
		this.links = links;
	}
	
	/**
	 * Sets the listings.
	 * 
	 * @param listings
	 *            the listings to set
	 */
	public void setListings(final Collection<Listing> listings) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.listings = listings;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the logs.
	 * 
	 * @param logs
	 *            the logs to set
	 */
	public void setLogs(final Collection<Log> logs) {
		this.logs = logs;
	}
	
	/**
	 * Sets the observed behaviors.
	 * 
	 * @param observedBehaviors
	 *            the observedBehaviors to set
	 */
	public void setObservedBehaviors(final Collection<ObservedBehavior> observedBehaviors) {
		this.observedBehaviors = observedBehaviors;
	}
	
	/**
	 * Sets the original report.
	 * 
	 * @param originalReport
	 *            the originalReport to set
	 */
	public void setOriginalReport(final Report originalReport) {
		this.originalReport = originalReport;
	}
	
	/**
	 * Sets the patches.
	 * 
	 * @param patches
	 *            the patches to set
	 */
	public void setPatches(final Collection<Patch> patches) {
		this.patches = patches;
	}
	
	/**
	 * Sets the report.
	 * 
	 * @param report
	 *            the new report
	 */
	public void setReport(final Report report) {
		this.originalReport = report;
	}
	
	/**
	 * Sets the stacktraces.
	 * 
	 * @param stacktraces
	 *            the stacktraces to set
	 */
	public void setStacktraces(final Collection<Stacktrace> stacktraces) {
		this.stacktraces = stacktraces;
	}
	
	/**
	 * Sets the steps to reproduce.
	 * 
	 * @param stepsToReproduce
	 *            the stepsToReproduce to set
	 */
	public void setStepsToReproduce(final Collection<StepsToReproduce> stepsToReproduce) {
		this.stepsToReproduce = stepsToReproduce;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("EnhancedReport [attachments=");
		builder.append(JavaUtils.mapToString(this.attachments));
		builder.append(", codeFragments=");
		builder.append(JavaUtils.collectionToString(this.codeFragments));
		builder.append(", expectedBehaviors=");
		builder.append(JavaUtils.collectionToString(this.expectedBehaviors));
		builder.append(", images=");
		builder.append(JavaUtils.collectionToString(this.images));
		builder.append(", links=");
		builder.append(JavaUtils.collectionToString(this.links));
		builder.append(", listings=");
		builder.append(JavaUtils.collectionToString(this.listings));
		builder.append(", logs=");
		builder.append(JavaUtils.collectionToString(this.logs));
		builder.append(", observedBehaviors=");
		builder.append(JavaUtils.collectionToString(this.observedBehaviors));
		builder.append(", originalReport=");
		builder.append(this.originalReport);
		builder.append(", patches=");
		builder.append(JavaUtils.collectionToString(this.patches));
		builder.append(", stacktraces=");
		builder.append(JavaUtils.collectionToString(this.stacktraces));
		builder.append(", stepsToReproduce=");
		builder.append(JavaUtils.collectionToString(this.stepsToReproduce));
		builder.append(", id=");
		builder.append(this.id);
		builder.append("]");
		return builder.toString();
	}
	
}
