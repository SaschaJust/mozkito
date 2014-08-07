package org.mozkito.issues.adaptive;


import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public abstract class Xpath2Data_with_JAVAX implements XPath  {

	
	public static void main(String[] args) 
			   throws ParserConfigurationException, SAXException, 
			          IOException, XPathExpressionException {

			    DocumentBuilderFactory domFactory = 
			    DocumentBuilderFactory.newInstance();
			          domFactory.setNamespaceAware(true); 
			    DocumentBuilder builder = domFactory.newDocumentBuilder();
			    Document doc = builder.parse("/Users/Eric/file.xml");
			    
			    /*XPath xpath = XPathFactory.newInstance().newXPath();
			       // XPath Query for showing all nodes value
			    XPathExpression expr = xpath.compile("//rss/channel/item/title");

			    Object result = expr.evaluate(doc, XPathConstants.NODESET);
			    NodeList nodes = (NodeList) result;
			    for (int i = 0; i < nodes.getLength(); i++) {
			     System.out.println(nodes.item(i)); 
			    }
			    if (nodes.getLength()==0){
			    	System.out.println("fuck you");
			    }*/
			    XPathFactory factory = XPathFactory.newInstance();
		        XPath xpath = factory.newXPath();
		        xpath.setNamespaceContext(new ODMNamespaceContext());  // <---
		        XPathExpression expr = xpath.compile("//ns:rss/ns:channel/ns:item/ns:title/text()");  // <----
		        Object result = expr.evaluate(doc, XPathConstants.NODESET);
		        NodeList nodes = (NodeList) result;
		        System.out.println(nodes.getLength());
		        for(int i = 0; i < nodes.getLength(); i++){
		        	System.out.println(nodes.item(i).getNodeName());
		        	System.out.println(nodes.item(i).getNodeValue());
		        }
			  }
}
