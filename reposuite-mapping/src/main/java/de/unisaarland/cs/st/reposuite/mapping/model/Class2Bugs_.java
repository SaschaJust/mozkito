/** 
 *  Generated by OpenJPA MetaModel Generator Tool.
**/

package de.unisaarland.cs.st.reposuite.mapping.model;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSFile;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

@javax.persistence.metamodel.StaticMetamodel
(value=de.unisaarland.cs.st.reposuite.mapping.model.Class2Bugs.class)
@javax.annotation.Generated
(value="org.apache.openjpa.persistence.meta.AnnotationProcessor6",date="Fri Sep 02 15:37:56 CEST 2011")
public class Class2Bugs_ {
    public static volatile SingularAttribute<Class2Bugs,RCSFile> file;
    public static volatile SetAttribute<Class2Bugs,Report> reports;
}
