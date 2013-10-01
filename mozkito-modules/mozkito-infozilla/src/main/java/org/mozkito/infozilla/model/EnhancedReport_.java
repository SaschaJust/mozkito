/***********************************************************************************************************************
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
 **********************************************************************************************************************/

package org.mozkito.infozilla.model;

import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.SingularAttribute;

import org.mozkito.infozilla.Region;
import org.mozkito.infozilla.model.EnhancedReport.Type;
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

/**
 * The Class EnhancedReport_.
 */
@javax.persistence.metamodel.StaticMetamodel (value = org.mozkito.infozilla.model.EnhancedReport.class)
@javax.annotation.Generated (value = "org.apache.openjpa.persistence.meta.AnnotationProcessor6",
                             date = "Tue Oct 01 05:19:30 CEST 2013")
public class EnhancedReport_ {
	
	/** The attachments. */
	public static volatile MapAttribute<EnhancedReport, String, Attachment>      attachments;
	
	/** The code fragments. */
	public static volatile CollectionAttribute<EnhancedReport, SourceCode>       codeFragments;
	
	/** The expected behaviors. */
	public static volatile CollectionAttribute<EnhancedReport, ExpectedBehavior> expectedBehaviors;
	
	/** The extracted regions. */
	public static volatile MapAttribute<EnhancedReport, Region, Type>            extractedRegions;
	
	/** The filtered comments. */
	public static volatile ListAttribute<EnhancedReport, Comment>                filteredComments;
	
	/** The filtered description. */
	public static volatile SingularAttribute<EnhancedReport, String>             filteredDescription;
	
	/** The id. */
	public static volatile SingularAttribute<EnhancedReport, String>             id;
	
	/** The images. */
	public static volatile CollectionAttribute<EnhancedReport, Image>            images;
	
	/** The links. */
	public static volatile CollectionAttribute<EnhancedReport, Link>             links;
	
	/** The listings. */
	public static volatile CollectionAttribute<EnhancedReport, Listing>          listings;
	
	/** The logs. */
	public static volatile CollectionAttribute<EnhancedReport, Log>              logs;
	
	/** The observed behaviors. */
	public static volatile CollectionAttribute<EnhancedReport, ObservedBehavior> observedBehaviors;
	
	/** The original report. */
	public static volatile SingularAttribute<EnhancedReport, Report>             originalReport;
	
	/** The patches. */
	public static volatile CollectionAttribute<EnhancedReport, Patch>            patches;
	
	/** The stacktraces. */
	public static volatile CollectionAttribute<EnhancedReport, Stacktrace>       stacktraces;
	
	/** The steps to reproduce. */
	public static volatile CollectionAttribute<EnhancedReport, StepsToReproduce> stepsToReproduce;
}
