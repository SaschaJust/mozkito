package de.unisaarland.cs.st.reposuite.ppa.model;

import static org.junit.Assert.assertEquals;

import org.jdom.Element;
import org.junit.Test;

public class JavaClassDefinitionTest {
	
	@Test
	public void testXML() {
		
		JavaClassDefinition orgDef = new JavaClassDefinition(JavaClassDefinition.class.getName());
		Element xmlRepresentation = orgDef.getXMLRepresentation();
		JavaClassDefinition xmlDef = JavaClassDefinition.fromXMLRepresentation(xmlRepresentation);
		assertEquals(orgDef, xmlDef);
		
		assertEquals(null, JavaMethodCall.fromXMLRepresentation(xmlRepresentation));
		assertEquals(null, JavaMethodDefinition.fromXMLRepresentation(xmlRepresentation));
		
	}
}
