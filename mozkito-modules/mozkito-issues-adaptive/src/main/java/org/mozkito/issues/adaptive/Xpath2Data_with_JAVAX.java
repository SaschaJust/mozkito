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

	
	
	public static void main(String[] args) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
				start_xpath("/Users/Eric/mozkito/mozkito-modules/mozkito-issues-adaptive/gen_files/file2.xml",
						"//ns:html/ns:body/ns:div/ns:section/ns:div/ns:div/ns:header/ns:div/ns:header/ns:div/ns:div/ns:h1/ns:title/text()");
	}
	
	public static void start_xpath (String filepath, String xpath_query) 
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException{
		
				
		 		DocumentBuilderFactory domFactory = 
			    DocumentBuilderFactory.newInstance();
			          domFactory.setNamespaceAware(true); 
			    DocumentBuilder builder = domFactory.newDocumentBuilder();
			    Document doc = builder.parse(filepath);
			    
			    XPathFactory factory = XPathFactory.newInstance();
		        XPath xpath = factory.newXPath();
		        xpath.setNamespaceContext(new ODMNamespaceContext());  // <---
		        XPathExpression expr = xpath.compile(xpath_query);  // <----
		        Object result = expr.evaluate(doc, XPathConstants.NODESET);
		        NodeList nodes = (NodeList) result;
		        System.out.println(nodes.getLength());
		        for(int i = 0; i < nodes.getLength(); i++){
		        	System.out.println(nodes.item(i).getNodeName());
		        	System.out.println(nodes.item(i).getNodeValue());
		        }
	}

}
