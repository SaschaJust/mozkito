package org.mozkito.issues.adaptive;


import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public abstract class Xpath2Data_with_JAVAX implements XPath  {

	
	
	public static void main(String[] args) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		NodeList finalnodes = null;
		ArrayList<String> xpath_query = new ArrayList<String>();
		xpath_query.add("//ns:*[1]/ns:*[@id ='jira']/ns:*[@id ='page']/ns:*[@id ='content']/ns:*[2]/ns:*[@id ='issue-content']/ns:*[2]/ns:*[1]/ns:*[@id ='viewissuesidebar']/ns:*[@id ='datesmodule']/ns:*[2]/ns:*[1]/ns:*[1]/ns:*[1]/ns:*[2]/ns:*[@id ='create-date']/ns:*[1]/text()");
		
		start_xpath("/Users/Eric/mozkito/mozkito-modules/mozkito-issues-adaptive/gen_files/file2.xml",
						xpath_query, finalnodes);
	}
	
	public static ArrayList<String> start_xpath (String filepath, ArrayList<String> xpath_query, NodeList finalnodes) 
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException{
				
				int query_size = xpath_query.size();				//groesse des uebergebenen xpathquery-arrays
				System.out.println(query_size);						
				
				ArrayList<String> resultArray = new ArrayList<String>();
				String query_data = new String();
				
				//sucht fuer jeden einzelnen xpath query die zugehoerigen daten und schreibt die in eine nodelist und
				//als string zusammen.... spaeter wird dann ein string array der daten aller queries ausgegeben
				for (int j=0; j<query_size; j++){										
				
			 		DocumentBuilderFactory domFactory = 
				    DocumentBuilderFactory.newInstance();
				          domFactory.setNamespaceAware(true); 
				    DocumentBuilder builder = domFactory.newDocumentBuilder();
				    Document doc = builder.parse(filepath);
				    
				    XPathFactory factory = XPathFactory.newInstance();
			        XPath xpath = factory.newXPath();
			        xpath.setNamespaceContext(new ODMNamespaceContext());  
			        XPathExpression expr = xpath.compile(xpath_query.get(j)); 
			        Object result = expr.evaluate(doc, XPathConstants.NODESET);
			        //hier werden die daten hinter dem xpath in die nodelist geschrieben
			        NodeList nodes = (NodeList) result;
			        System.out.println(nodes.getLength());
			        //die for schleife schreibt den text der nodelist in einen string zusammen
			        for(int i = 0; i < nodes.getLength(); i++){
			        	query_data = query_data + nodes.item(i).getNodeValue();
			        		//System.out.println(nodes.item(i).getTextContent());
			        		//System.out.println(nodes.item(i).getNodeName());
			        }
			        resultArray.add(query_data);
				}
				//		        for(int j = 0; j < blabla.size(); j++){
				//		        		blabla.get(j).replace(" ","");
				//		        		System.out.println(blabla.get(j));
				//		        }
				System.out.println(resultArray.size());	
				System.out.println(resultArray);	
		        return resultArray;
		        
	}

	public static ArrayList<String> start_xpath_string (String filepath, String xpath_query, NodeList finalnodes) 
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException{
				
				
				ArrayList<String> resultArray = new ArrayList<String>();
				String query_data = new String();
				
				//sucht fuer jeden einzelnen xpath query die zugehoerigen daten und schreibt die in eine nodelist und
				//als string zusammen.... spaeter wird dann ein string array der daten aller queries ausgegeben
				
			 		DocumentBuilderFactory domFactory = 
				    DocumentBuilderFactory.newInstance();
				          domFactory.setNamespaceAware(true); 
				    DocumentBuilder builder = domFactory.newDocumentBuilder();
				    Document doc = builder.parse(filepath);
				    
				    XPathFactory factory = XPathFactory.newInstance();
			        XPath xpath = factory.newXPath();
			        xpath.setNamespaceContext(new ODMNamespaceContext());  
			        XPathExpression expr = xpath.compile(xpath_query); 
			        Object result = expr.evaluate(doc, XPathConstants.NODESET);
			        //hier werden die daten hinter dem xpath in die nodelist geschrieben
			        NodeList nodes = (NodeList) result;
			        //System.out.println(nodes.getLength());
			        //die for schleife schreibt den text der nodelist in einen string zusammen
			        for(int i = 0; i < nodes.getLength(); i++){
			        	query_data = query_data + nodes.item(i).getNodeValue();
			        		//System.out.println(nodes.item(i).getTextContent());
			        		//System.out.println(nodes.item(i).getNodeName());
			        }
			        resultArray.add(query_data);
				//		        for(int j = 0; j < blabla.size(); j++){
				//		        		blabla.get(j).replace(" ","");
				//		        		System.out.println(blabla.get(j));
				//		        }
				//System.out.println(resultArray.size());	
				//System.out.println(resultArray);	
		        return resultArray;
		        
	}
}
