package de.unisaarland.cs.st.reposuite.ppa.model;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;
import org.junit.Test;

public class JavaMethodCallTest {
	
	@Test
	public void testXML() {
		List<String> argList = new ArrayList<String>(2);
		argList.add("String");
		argList.add("Object");
		JavaMethodCall orgCall = new JavaMethodCall(JavaMethodCall.class.getName(), "foo", argList);
		Element xmlRepresentation = orgCall.getXMLRepresentation();
		JavaMethodCall xmlCall = JavaMethodCall.fromXMLRepresentation(xmlRepresentation);
		assertEquals(orgCall, xmlCall);
		
		assertEquals(null, JavaClassDefinition.fromXMLRepresentation(xmlRepresentation));
		assertEquals(null, JavaMethodDefinition.fromXMLRepresentation(xmlRepresentation));
		
	}
}
