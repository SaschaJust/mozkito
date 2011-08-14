package de.unisaarland.cs.st.reposuite.ppa.model;

import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(JavaElementLocation.class)
public class JavaElementLocation_ {
	
	public static volatile SingularAttribute<JavaElementLocation, Long>        id;
	public static volatile SingularAttribute<JavaElementLocation, Integer>     startLine;
	public static volatile SingularAttribute<JavaElementLocation, Integer>     endLine;
	public static volatile SingularAttribute<JavaElementLocation, Integer>     position;
	public static volatile SingularAttribute<JavaElementLocation, JavaElement> element;
	public static volatile SingularAttribute<JavaElementLocation, String>      filePath;
	public static volatile SingularAttribute<JavaElementLocation, Integer>     bodyStartLine;
	public static volatile SetAttribute<JavaElementLocation, Integer>          commentLines;
}
