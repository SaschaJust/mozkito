package de.unisaarland.cs.st.reposuite.ppa.model;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;
import org.junit.Test;

public class JavaMethodDefinitionTest {
	
	@Test
	public void testXML() {
		List<String> argList = new ArrayList<String>(2);
		argList.add("String");
		argList.add("Object");
		JavaMethodDefinition orgDef = new JavaMethodDefinition(JavaMethodDefinition.class.getName(), "foo", argList);
		Element xmlRepresentation = orgDef.getXMLRepresentation();
		JavaMethodDefinition xmlDef = JavaMethodDefinition.fromXMLRepresentation(xmlRepresentation);
		assertEquals(orgDef, xmlDef);
		
		assertEquals(null, JavaMethodCall.fromXMLRepresentation(xmlRepresentation));
		assertEquals(null, JavaClassDefinition.fromXMLRepresentation(xmlRepresentation));
	}
	
}
