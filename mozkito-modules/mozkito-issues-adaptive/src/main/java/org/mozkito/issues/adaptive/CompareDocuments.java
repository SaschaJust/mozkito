package org.mozkito.issues.adaptive;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class CompareDocuments {
	
	
	public static String compare (Document document1, Document document2, String tofind){
		
		String filepath = "/Users/Eric/mozkito/mozkito-modules/mozkito-issues-adaptive/gen_files/file2.xml";  
		int deep = 1;
		boolean found = false;											
		Element root = document1.getRootElement();
		root.setAttribute("number", String.valueOf(1));				//root-element mit atrribut 0 versehen//nimmt sich das Rootelement
		//System.out.println(root + " name: " + root.getName() + " text: " + root.getText());
		
		ArrayList<String> finalPredecessors = new ArrayList<String>();						//Liste der Tags die Vorgänger des gesuchten sind
		ArrayList<String> finalAttributes = new ArrayList<String>();						//liste der attribute der tags für xpath
		parse_test.find_markers(root, deep, finalPredecessors, finalAttributes, found, tofind);
		
		String XPath = "";
		XPath = parse_test.make_xpath_query(finalPredecessors, finalAttributes);
		
		NodeList nodes1 = null;
		NodeList nodes2 = null;
		try {
			Xpath2Data_with_JAVAX.start_xpath(filepath, XPath, nodes1);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		parse_test.write_genfile(document2, filepath);
		
		try {
			Xpath2Data_with_JAVAX.start_xpath(filepath, XPath, nodes2);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean diff = false;
		for(int i = 0; i < nodes1.getLength(); i++){
        	if (!nodes1.item(i).getNodeValue().equals(nodes2.item(i).getNodeValue())){
        		diff = true;
        	};
        		//System.out.println(nodes.item(i).getNodeName());
        }
		if (diff = true){
			return XPath;
		} else {
			return "nicht gefunden";
		}
		
	}
	
	
}
