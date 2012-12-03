package org.mozkito.codeanalysis.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Set;

import org.jdom2.Element;
import org.junit.Before;
import org.junit.Test;

import org.mozkito.codeanalysis.model.JavaElementLocation.LineCover;

public class JavaElementLocationTest {
	
	private JavaElementFactory     elementFactory;
	private JavaElementLocationSet set;
	private JavaTypeDefinition     javaType;
	private JavaElementLocation    location;
	
	@Before
	public void setup() {
		this.elementFactory = new JavaElementFactory();
		this.set = new JavaElementLocationSet(new JavaElementFactory());
		this.javaType = new JavaTypeDefinition("org.mozkito.codeanalysis.model.TestClass");
		this.location = this.set.addAnonymousClassDefinition(this.javaType,
		                                                     "org.mozkito.codeanalysis.model.TestClass$1",
		                                                     "org/mozkito/codeanalysis/model/TestClass.java", 20, 25,
		                                                     43674, 21);
		this.location.addCommentLines(22, 23);
	}
	
	@Test
	public void testCoversAllLines() {
		final ArrayList<Integer> lineList = new ArrayList<Integer>();
		lineList.add(20);
		assertEquals(LineCover.DEFINITION, this.location.coversAllLines(lineList));
		lineList.add(21);
		assertEquals(LineCover.DEF_AND_BODY, this.location.coversAllLines(lineList));
		lineList.add(22);
		assertEquals(LineCover.DEF_AND_BODY, this.location.coversAllLines(lineList));
		lineList.add(23);
		assertEquals(LineCover.DEF_AND_BODY, this.location.coversAllLines(lineList));
		lineList.add(24);
		assertEquals(LineCover.DEF_AND_BODY, this.location.coversAllLines(lineList));
		lineList.add(25);
		assertEquals(LineCover.DEF_AND_BODY, this.location.coversAllLines(lineList));
		lineList.add(26);
		assertEquals(LineCover.FALSE, this.location.coversAllLines(lineList));
		lineList.clear();
		lineList.add(19);
		assertEquals(LineCover.FALSE, this.location.coversAllLines(lineList));
		lineList.clear();
		lineList.add(22);
		assertEquals(LineCover.FALSE, this.location.coversAllLines(lineList));
		lineList.add(24);
		assertEquals(LineCover.BODY, this.location.coversAllLines(lineList));
	}
	
	@Test
	public void testCoversAnyLine() {
		final ArrayList<Integer> lineList = new ArrayList<Integer>();
		lineList.add(19);
		assertEquals(LineCover.FALSE, this.location.coversAnyLine(lineList));
		lineList.add(20);
		assertEquals(LineCover.DEFINITION, this.location.coversAnyLine(lineList));
		lineList.add(21);
		assertEquals(LineCover.DEF_AND_BODY, this.location.coversAnyLine(lineList));
		lineList.add(22);
		assertEquals(LineCover.DEF_AND_BODY, this.location.coversAnyLine(lineList));
		lineList.add(23);
		assertEquals(LineCover.DEF_AND_BODY, this.location.coversAnyLine(lineList));
		lineList.add(24);
		assertEquals(LineCover.DEF_AND_BODY, this.location.coversAnyLine(lineList));
		lineList.add(25);
		assertEquals(LineCover.DEF_AND_BODY, this.location.coversAnyLine(lineList));
		lineList.add(26);
		assertEquals(LineCover.DEF_AND_BODY, this.location.coversAnyLine(lineList));
		lineList.clear();
		lineList.add(22);
		assertEquals(LineCover.FALSE, this.location.coversAnyLine(lineList));
		lineList.add(24);
		assertEquals(LineCover.BODY, this.location.coversAnyLine(lineList));
	}
	
	@Test
	public void testCoversLine() {
		assertEquals(LineCover.FALSE, this.location.coversLine(19));
		assertEquals(LineCover.DEFINITION, this.location.coversLine(20));
		assertEquals(LineCover.BODY, this.location.coversLine(21));
		assertEquals(LineCover.FALSE, this.location.coversLine(22));
		assertEquals(LineCover.FALSE, this.location.coversLine(23));
		assertEquals(LineCover.BODY, this.location.coversLine(24));
		assertEquals(LineCover.BODY, this.location.coversLine(25));
		assertEquals(LineCover.FALSE, this.location.coversLine(26));
	}
	
	@Test
	public void testGetCommentLines() {
		final Set<Integer> commentLines = this.location.getCommentLines();
		assertEquals(2, commentLines.size());
		assertTrue(commentLines.contains(22));
		assertTrue(commentLines.contains(23));
	}
	
	@Test
	public void testXML() {
		final Element xmlRepresentation = this.location.getXMLRepresentation();
		final JavaElementLocation xmlLocation = JavaElementLocation.fromXMLRepresentation(xmlRepresentation,
		                                                                                  this.elementFactory);
		assertEquals(this.location, xmlLocation);
	}
	
}
