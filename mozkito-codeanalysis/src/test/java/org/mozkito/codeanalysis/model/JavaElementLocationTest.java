package org.mozkito.codeanalysis.model;

import static org.junit.Assert.assertEquals;

import org.jdom2.Element;
import org.junit.Test;

public class JavaElementLocationTest {
	
	@Test
	public void testXML() {
		
		final JavaElementFactory elementFactory = new JavaElementFactory();
		final JavaElementLocationSet set = new JavaElementLocationSet(new JavaElementFactory());
		final JavaTypeDefinition javaType = new JavaTypeDefinition("org.mozkito.codeanalysis.model.TestClass");
		final JavaElementLocation location = set.addAnonymousClassDefinition(javaType,
		                                                                     "org.mozkito.codeanalysis.model.TestClass$1",
		                                                                     "org/mozkito/codeanalysis/model/TestClass.java",
		                                                                     20, 23, 43674, 20);
		
		final Element xmlRepresentation = location.getXMLRepresentation();
		final JavaElementLocation xmlLocation = JavaElementLocation.fromXMLRepresentation(xmlRepresentation,
		                                                                                  elementFactory);
		assertEquals(location, xmlLocation);
	}
	
}
