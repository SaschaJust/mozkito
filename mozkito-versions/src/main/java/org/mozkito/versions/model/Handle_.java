/**
 * Generated by OpenJPA MetaModel Generator Tool.
 **/

package org.mozkito.versions.model;

import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.SingularAttribute;

/**
 * The Class Handle_.
 * 
 * @author "Kim Herzig <herzig@cs.uni-saarland.de>"
 */
@javax.persistence.metamodel.StaticMetamodel (value = org.mozkito.versions.model.Handle.class)
@javax.annotation.Generated (value = "org.apache.openjpa.persistence.meta.AnnotationProcessor6",
                             date = "Thu Jan 10 17:52:36 CET 2013")
public class Handle_ {
	
	/** The archive. */
	public static volatile SingularAttribute<Handle, VersionArchive> archive;
	
	/** The changed names. */
	public static volatile MapAttribute<Handle, RCSRevision, String> changedNames;
	
	/** The generated id. */
	public static volatile SingularAttribute<Handle, Long>           generatedId;
}
