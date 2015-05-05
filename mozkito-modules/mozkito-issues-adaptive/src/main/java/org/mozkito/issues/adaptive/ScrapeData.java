package org.mozkito.issues.adaptive;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.http.client.ClientProtocolException;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ScrapeData {
	
	ArrayList<String> markerList = new ArrayList<String>();
	
	public static String BASIC_URL = "http://feeds.bbci.co.uk/news/technology/rss.xml?edition=int";
	
	
	//findet bzgl der gegeben marker die xpath ausdruecke zu den entsprechenden werten
//	public static ArrayList<String> find_path_marker (ArrayList<String> markerList, ArrayList<String> xpathExpressions) throws ClientProtocolException, IOException, JDOMException, KeyManagementException, NoSuchAlgorithmException{
//		
//		Document doc = Parse_with_JDOM2.test(BASIC_URL);
//		int size = markerList.size();
//		
//		for(int i = 0 ; i < size; i++){
//			String string = markerList.get(i);
//			ArrayList<String> tmpList = parse_test.initSearch(doc, string);
//			
//			//TODO!!!!
//			int size2 = tmpList.size();								//hier muss noch ueberlegt werden, wenn die markersuche 2 ergebnisse liefert,
//			xpathExpressions.add(tmpList.get(size2));				//welches genommen wird und nicht einfach nur das letzte
//			
//		}
//		
//		return xpathExpressions;
//	}
//	
//	//liefert zu einem wert eines auswahlsfeldes und zwei urls, den xpath zu der stelle an der sich die xmldocs unterscheiden
//	public static String find_path_compare (String compareString, String url1, String url2) throws ClientProtocolException, IOException, JDOMException, KeyManagementException, NoSuchAlgorithmException{
//			
//		Document doc1 = Parse_with_JDOM2.test(url1);										//baue xml doc zu url 1
//		Document doc2 = Parse_with_JDOM2.test(url2);										//baue xml doc zu url 2
//		String xpathExpression = CompareDocuments.compare(doc1, doc2, compareString);		// vergleiche beide dokumente
//		
//		return xpathExpression;
//	}
//	
//	
//	//liefert zu einer reihe xpath ausdruecke die dazugehoerigen werte in einem xml document
//	public static ArrayList<String> retrieve_marker_data (ArrayList<String> xpathExpressions) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException{
//		
//		String filepath = "/Users/Eric/mozkito/mozkito-modules/mozkito-issues-adaptive/gen_files/file2.xml";
//		NodeList finalnodes = null;
//		ArrayList<String> finalData = new ArrayList<String>();
//		int size = xpathExpressions.size();
//		for(int i = 0 ; i < size; i++){
//			ArrayList<String> tmpList = new ArrayList<String>();
//			tmpList.add(xpathExpressions.get(i));
//			//TODO!!! hier kriege ich eine string liste zurueck und muss mir quasi noch den besten string aussuchen, der den eigl daten am naechsten kommt
//			finalData.add(Xpath2Data_with_JAVAX.start_xpath (filepath,tmpList,finalnodes));
//		}
//		
//		return finalData;
//		
//	}
	
}
