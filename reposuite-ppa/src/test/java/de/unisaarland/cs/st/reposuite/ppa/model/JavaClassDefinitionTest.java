/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
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
