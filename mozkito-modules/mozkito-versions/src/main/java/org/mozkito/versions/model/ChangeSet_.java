/**
 * Generated by OpenJPA MetaModel Generator Tool.
 **/

package org.mozkito.versions.model;

import java.util.Date;

import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import org.mozkito.persons.model.PersonContainer;

/**
 * The Class ChangeSet_.
 */
@javax.persistence.metamodel.StaticMetamodel (value = org.mozkito.versions.model.ChangeSet.class)
@javax.annotation.Generated (value = "org.apache.openjpa.persistence.meta.AnnotationProcessor6",
                             date = "Mon Jan 14 13:43:14 CET 2013")
public class ChangeSet_ {
	
	/** The atomic. */
	public static volatile SingularAttribute<ChangeSet, Boolean>                              atomic;
	
	/** The branch indices. */
	public static volatile MapAttribute<ChangeSet, String, Long>                              branchIndices;
	
	/** The branch parent. */
	public static volatile SingularAttribute<ChangeSet, org.mozkito.versions.model.ChangeSet> branchParent;
	
	/** The children. */
	public static volatile SetAttribute<ChangeSet, org.mozkito.versions.model.ChangeSet>      children;
	
	/** The id. */
	public static volatile SingularAttribute<ChangeSet, String>                               id;
	
	/** The java timestamp. */
	public static volatile SingularAttribute<ChangeSet, Date>                                 javaTimestamp;
	
	/** The merge parent. */
	public static volatile SingularAttribute<ChangeSet, org.mozkito.versions.model.ChangeSet> mergeParent;
	
	/** The message. */
	public static volatile SingularAttribute<ChangeSet, String>                               message;
	
	/** The original id. */
	public static volatile SingularAttribute<ChangeSet, String>                               originalId;
	
	/** The persons. */
	public static volatile SingularAttribute<ChangeSet, PersonContainer>                      persons;
	
	/** The revisions. */
	public static volatile CollectionAttribute<ChangeSet, Revision>                           revisions;
	
	/** The tags. */
	public static volatile SetAttribute<ChangeSet, String>                                    tags;
	
	/** The version archive. */
	public static volatile SingularAttribute<ChangeSet, VersionArchive>                       versionArchive;
}
