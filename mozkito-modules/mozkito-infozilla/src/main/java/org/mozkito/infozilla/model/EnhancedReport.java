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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import org.mozkito.infozilla.Region;
import org.mozkito.infozilla.model.attachment.Attachment;
import org.mozkito.infozilla.model.image.Image;
import org.mozkito.infozilla.model.itemization.ExpectedBehavior;
import org.mozkito.infozilla.model.itemization.Listing;
import org.mozkito.infozilla.model.itemization.ObservedBehavior;
import org.mozkito.infozilla.model.itemization.StepsToReproduce;
import org.mozkito.infozilla.model.link.Link;
import org.mozkito.infozilla.model.log.Log;
import org.mozkito.infozilla.model.patch.Patch;
import org.mozkito.infozilla.model.source.SourceCode;
import org.mozkito.infozilla.model.stacktrace.Stacktrace;
import org.mozkito.issues.model.Comment;
import org.mozkito.issues.model.Report;
import org.mozkito.persistence.Annotated;
import org.mozkito.utilities.commons.JavaUtils;
import org.mozkito.utilities.io.FileUtils;

/**
 * The Class EnhancedReport.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
@Entity
public class EnhancedReport implements Annotated {
	
	/**
	 * The Enum Type.
	 */
	public static enum Type {
		
		/** The sourcecode. */
		SOURCECODE,
		/** The listing. */
		LISTING,
		/** The log. */
		LOG,
		/** The patch. */
		PATCH,
		/** The stacktrace. */
		STACKTRACE;
	}
	
	/** The extracted regions. */
	private Map<Region, Type> extractedRegions = new HashMap<>();
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6200370567492281526L;
	
	/**
	 * Empty.
	 * 
	 * @return the enhanced report
	 */
	public static EnhancedReport empty() {
		return new EnhancedReport(null);
	}
	
	/**
	 * Merge.
	 * 
	 * @param into
	 *            the into
	 * @param from
	 *            the from
	 * @return the enhanced report
	 */
	public static EnhancedReport merge(final EnhancedReport into,
	                                   final EnhancedReport from) {
		PRECONDITIONS: {
			if (into == null) {
				throw new NullPointerException();
			}
			if (from == null) {
				throw new NullPointerException();
			}
		}
		
		SANITY: {
			assert into.getId() != null;
			assert from.getId() == null;
		}
		
		synchronized (into) {
			SANITTY: {
				assert from.getFilteredDescription() == null;
				assert from.getFilteredComments().isEmpty();
				
				for (final Entry<String, Attachment> entry : from.getAttachments().entrySet()) {
					assert !into.getAttachments().containsKey(entry.getKey());
				}
				
				assert !CollectionUtils.containsAny(into.getCodeFragments(), from.getCodeFragments());
				assert !CollectionUtils.containsAny(into.getExpectedBehaviors(), from.getExpectedBehaviors());
				assert !CollectionUtils.containsAny(into.getImages(), from.getImages());
				assert !CollectionUtils.containsAny(into.getLinks(), from.getLinks());
				assert !CollectionUtils.containsAny(into.getListings(), from.getListings());
				assert !CollectionUtils.containsAny(into.getLogs(), from.getLogs());
				assert !CollectionUtils.containsAny(into.getObservedBehaviors(), from.getObservedBehaviors());
				assert !CollectionUtils.containsAny(into.getPatches(), from.getPatches());
				assert !CollectionUtils.containsAny(into.getStacktraces(), from.getStacktraces());
				assert !CollectionUtils.containsAny(into.getStepsToReproduce(), from.getStepsToReproduce());
			}
			
			// add attachments to the original report
			into.getAttachments().putAll(from.getAttachments());
			
			into.getCodeFragments().addAll(from.getCodeFragments());
			
			into.getExpectedBehaviors().addAll(from.getExpectedBehaviors());
			
			into.getImages().addAll(from.getImages());
			
			into.getLinks().addAll(from.getLinks());
			
			into.getListings().addAll(from.getListings());
			
			into.getLogs().addAll(from.getLogs());
			
			into.getObservedBehaviors().addAll(from.getObservedBehaviors());
			
			into.getPatches().addAll(from.getPatches());
			
			into.getStacktraces().addAll(from.getStacktraces());
			
			into.getStepsToReproduce().addAll(from.getStepsToReproduce());
			
			// we do not merge descriptions or comments since these have to be set by the InlineFilterManager
		}
		
		return into;
	}
	
	/** The attachments. */
	private Map<String, Attachment>      attachments       = new HashMap<String, Attachment>();
	
	/** The code fragments. */
	private Collection<SourceCode>       codeFragments     = new LinkedList<>();
	
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
	
	/** The filtered description. */
	private String                       filteredDescription;
	
	/** The filtered comments. */
	private List<Comment>                filteredComments  = new LinkedList<>();
	
	/**
	 * @deprecated must only be used by JPA
	 */
	@Deprecated
	public EnhancedReport() {
		// stub
	}
	
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
	@Transient
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
	public Collection<SourceCode> getCodeFragments() {
		// PRECONDITIONS
		
		try {
			return this.codeFragments;
		} finally {
			// POSTCONDITIONS
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
	 * Gets the extracted regions.
	 * 
	 * @return the extractedRegions
	 */
	public Map<Region, Type> getExtractedRegions() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.extractedRegions;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the filtered comments.
	 * 
	 * @return the filteredComments
	 */
	public List<Comment> getFilteredComments() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.filteredComments;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the filtered description.
	 * 
	 * @return the filteredDescription
	 */
	public String getFilteredDescription() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.filteredDescription;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
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
	 *            the codeFragments to set
	 */
	public void setCodeFragments(final Collection<SourceCode> codeFragments) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.codeFragments = codeFragments;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the code fragments.
	 * 
	 * @param results
	 *            the new code fragments
	 */
	public void setCodeFragments(final List<SourceCode> results) {
		// PRECONDITIONS
		
		try {
			this.codeFragments = results;
		} finally {
			// POSTCONDITIONS
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
	 * Sets the extracted regions.
	 * 
	 * @param extractedRegions
	 *            the extractedRegions to set
	 */
	public void setExtractedRegions(final Map<Region, Type> extractedRegions) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.extractedRegions = extractedRegions;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the filtered comments.
	 * 
	 * @param filteredComments
	 *            the filteredComments to set
	 */
	public void setFilteredComments(final List<Comment> filteredComments) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.filteredComments = filteredComments;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the filtered description.
	 * 
	 * @param filteredDescription
	 *            the filteredDescription to set
	 */
	public void setFilteredDescription(final String filteredDescription) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.filteredDescription = filteredDescription;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
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
		builder.append("EnhancedReport [id=");
		builder.append(getId()).append("]");
		if (CollectionUtils.exists(Arrays.asList(new Collection<?>[] { getStacktraces(), getPatches(),
		                                   getCodeFragments(), getLogs(), getListings() }), new Predicate() {
			                           
			                           @Override
			                           public boolean evaluate(final Object object) {
				                           final Collection<?> c = (Collection<?>) object;
				                           return !c.isEmpty();
			                           }
		                           })) {
			builder.append(FileUtils.lineSeparator).append("## filtered Description=").append(getFilteredDescription());
			
			builder.append(FileUtils.lineSeparator).append("## filtered Comments=");
			for (final Comment comment : getFilteredComments()) {
				
				builder.append(FileUtils.lineSeparator).append('[').append(comment.getId()).append("] ")
				       .append(comment.getMessage());
			}
			
			builder.append(FileUtils.lineSeparator).append("## Stacktraces=");
			for (final Stacktrace stacktrace : getStacktraces()) {
				builder.append(FileUtils.lineSeparator).append(stacktrace);
			}
			
			builder.append(FileUtils.lineSeparator).append("## Patches=");
			for (final Patch patch : getPatches()) {
				builder.append(FileUtils.lineSeparator).append(patch);
			}
			
			builder.append(FileUtils.lineSeparator).append("## Code Fragments=");
			for (final SourceCode sourceCode : getCodeFragments()) {
				builder.append(FileUtils.lineSeparator).append(sourceCode);
			}
			
			builder.append(FileUtils.lineSeparator).append("## Logs=");
			for (final Log log : getLogs()) {
				builder.append(FileUtils.lineSeparator).append(log);
			}
			
			builder.append(FileUtils.lineSeparator).append("## Links=");
			for (final Link link : getLinks()) {
				builder.append(FileUtils.lineSeparator).append(link);
			}
			
			builder.append(FileUtils.lineSeparator).append("## Listings=");
			for (final Listing listing : getListings()) {
				builder.append(FileUtils.lineSeparator).append(listing);
			}
		}
		
		return builder.toString();
	}
	
}
