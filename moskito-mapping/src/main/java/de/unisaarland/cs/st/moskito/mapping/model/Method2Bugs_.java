/**
 * Generated by OpenJPA MetaModel Generator Tool.
 **/

package de.unisaarland.cs.st.moskito.mapping.model;

import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import de.unisaarland.cs.st.moskito.bugs.tracker.model.Report;
import de.unisaarland.cs.st.moskito.rcs.model.RCSFile;

/**
 * The Class Method2Bugs_.
 */
@javax.persistence.metamodel.StaticMetamodel (value = de.unisaarland.cs.st.moskito.mapping.model.Method2Bugs.class)
@javax.annotation.Generated (value = "org.apache.openjpa.persistence.meta.AnnotationProcessor6",
                             date = "Fri Sep 02 15:37:56 CEST 2011")
public class Method2Bugs_ {
	
	/** The file. */
	public static volatile SingularAttribute<Method2Bugs, RCSFile> file;
	
	/** The reports. */
	public static volatile SetAttribute<Method2Bugs, Report>       reports;
}
