/**
 * Generated by OpenJPA MetaModel Generator Tool.
 **/

package org.mozkito.codeanalysis.model;

import javax.persistence.metamodel.SingularAttribute;

import org.mozkito.versions.elements.ChangeType;
import org.mozkito.versions.model.RCSRevision;

@javax.persistence.metamodel.StaticMetamodel (value = org.mozkito.codeanalysis.model.JavaChangeOperation.class)
@javax.annotation.Generated (value = "org.apache.openjpa.persistence.meta.AnnotationProcessor6",
                             date = "Fri Mar 30 11:02:50 CEST 2012")
public class JavaChangeOperation_ {
	
	public static volatile SingularAttribute<JavaChangeOperation, ChangeType>          changeType;
	public static volatile SingularAttribute<JavaChangeOperation, JavaElementLocation> changedElementLocation;
	public static volatile SingularAttribute<JavaChangeOperation, Boolean>             essential;
	public static volatile SingularAttribute<JavaChangeOperation, Long>                id;
	public static volatile SingularAttribute<JavaChangeOperation, RCSRevision>         revision;
}
