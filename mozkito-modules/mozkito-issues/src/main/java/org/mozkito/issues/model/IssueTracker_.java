/**
 * Generated by OpenJPA MetaModel Generator Tool.
 **/

package org.mozkito.issues.model;

import java.util.Date;

import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.SingularAttribute;

/**
 * The Class IssueTracker_.
 */
@javax.persistence.metamodel.StaticMetamodel (value = org.mozkito.issues.model.IssueTracker.class)
@javax.annotation.Generated (value = "org.apache.openjpa.persistence.meta.AnnotationProcessor6",
                             date = "Mon Jan 14 13:43:14 CET 2013")
public class IssueTracker_ {
	
	/** The change sets. */
	public static volatile MapAttribute<IssueTracker, String, Report> reports;
	
	/** The generated id. */
	public static volatile SingularAttribute<IssueTracker, Long>      generatedId;
	
	/** The mining java date. */
	public static volatile SingularAttribute<IssueTracker, Date>      miningJavaDate;
	
	/** The mozkito hash. */
	public static volatile SingularAttribute<IssueTracker, String>    mozkitoHash;
	
	/** The mozkito version. */
	public static volatile SingularAttribute<IssueTracker, String>    mozkitoVersion;
	
	/** The used settings. */
	public static volatile SingularAttribute<IssueTracker, String>    usedSettings;
	
	/** The host info. */
	public static volatile SingularAttribute<IssueTracker, String>    hostInfo;
}