/** 
 *  Generated by OpenJPA MetaModel Generator Tool.
**/

package org.mozkito.codeanalysis.model;

import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

@javax.persistence.metamodel.StaticMetamodel
(value=org.mozkito.codeanalysis.model.JavaElementLocation.class)
@javax.annotation.Generated
(value="org.apache.openjpa.persistence.meta.AnnotationProcessor6",date="Fri Mar 30 11:02:50 CEST 2012")
public class JavaElementLocation_ {
    public static volatile SingularAttribute<JavaElementLocation,Integer> bodyStartLine;
    public static volatile SetAttribute<JavaElementLocation,Integer> commentLines;
    public static volatile SingularAttribute<JavaElementLocation,JavaElement> element;
    public static volatile SingularAttribute<JavaElementLocation,Integer> endLine;
    public static volatile SingularAttribute<JavaElementLocation,String> filePath;
    public static volatile SingularAttribute<JavaElementLocation,Long> id;
    public static volatile SingularAttribute<JavaElementLocation,Integer> position;
    public static volatile SingularAttribute<JavaElementLocation,Integer> startLine;
}