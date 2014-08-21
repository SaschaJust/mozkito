package org.mozkito.issues.adaptive;

import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.jdom2.DocType;
import org.jdom2.Element;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderSAX2Factory;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.SAXException;


/**
 * @author Eric Gliemmo
 *
 */
public class Parse_with_JDOM2 {
	
	
	public static void main(String[] args) throws Exception{
		String url = "https://bugzilla.mozilla.org/show_bug.cgi?id=828871";
		start_parse(url);
	}
	
	public static void start_parse(String url) throws MalformedURLException, JDOMException, IOException{
		
		
		final SAXBuilder saxBuilder = new SAXBuilder(new XMLReaderSAX2Factory(false,
		                                                                      "org.ccil.cowan.tagsoup.Parser"));
		//final Document document = saxBuilder.build(new URL("https://bugzilla.mozilla.org/show_bug.cgi?id=828871"));
		//final Document document = saxBuilder.build(new URL("http://feeds.bbci.co.uk/news/technology/rss.xml?edition=int"));
		// final Document document = saxBuilder.build(new URL("https://issues.mozkito.org/browse/MOZKITO-113"));
																//baut das JDOM2 Document aus der URL, also das XML Dokument
		//final Document document = saxBuilder.build(new URL("https://jira.codehaus.org/browse/XSTR-752"));
		final Document document = saxBuilder.build(new URL(url));
		
		XMLOutputter out = new XMLOutputter();
		out.output( document, System.out );						// gibt die XML aus
		
		//bearbeite den doctype (vor allem bei bugzilla kann die public id nicht geparst werden)
		DocType doctype = document.getDocType();
		doctype.setPublicID(null);
		doctype.setSystemID(null);
		
		
		String filepath = "/Users/Eric/mozkito/mozkito-modules/mozkito-issues-adaptive/gen_files/file2.xml";  
		write_genfile(document, filepath);													//speichere xml-doc lokal als file
		
		//System.out.println("+++++++++++++++++++++++");
		
		int deep = 1;
		boolean found = false;											
		Element root = document.getRootElement();											//nimmt sich das Rootelement
		
		ArrayList<String> finalPredecessors = new ArrayList<String>();						//Liste der Tags die Vorgänger des gesuchten sind
		find_markers(root, deep, finalPredecessors, found);									//ruft die Methode zum suchen der Marker auf
		//System.out.println(finalPredecessors);
		//System.out.println("+++++++++++++++++++++++");
		
		String XPath = "";
		if (finalPredecessors.size()>0){
				XPath = make_xpath_query(finalPredecessors);
				System.out.println(XPath);														//funktioniert nicht wirklich, nur für //*
		} else {
			System.out.println("nothing found");
		}
		
		//starte die datensuche mit dem filepath und dem xpathquery
		try {
			Xpath2Data_with_JAVAX.start_xpath(filepath, XPath);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//sucht einen bestimmten marker und mekrt sich den pfad dorthin
	private static void find_markers (Element root, int deep, ArrayList<String> finalPredecessors,boolean found){
		List<Element> Children = build_children_list(root);
		System.out.println(Children);
		System.out.println("LEVEL:" + deep);
		if (!Children.isEmpty()){
			for (Element child : Children) {
				System.out.println("Child:" + child.getName());
	            if (!hasChildren(child) && checkString(child.getText())){
	            	for(int x=0; x<deep+1; x++){
	            		finalPredecessors.add(child.getName());
	            	    if (child.getParentElement()!=null){
	            	    	child = child.getParentElement();
	            	    }
	            	}
	            	Collections.reverse(finalPredecessors);
	            	found=true;
	            }
	            if(found==false){
	            	find_markers(child,deep+1,finalPredecessors,found);
	            }
	        }
		}
	}
	//Gibt zu einem Element die Liste der Kinder zurück
	private static List<Element> build_children_list (Element parent){
		
		List<Element> childrenList = parent.getChildren();     //baut die Liste der Kinder zusammen
		
		if (childrenList == null) {
			return null;
		} else {
			return childrenList;							// falls nicht null gibt diese aus
		}
		
		
	}
	//testet ob ein element kinder hat
	private static boolean hasChildren (Element parent){
		
		List<Element> childrenList = parent.getChildren();				//liste der kinder
		
		if (!childrenList.isEmpty()) {
			return true;
		}	
		return false;		
	}
	
	//Testet ob der Inhalt eines Tags einen Marker enthält
	private static boolean checkString (String string){
		
		if (string.contains("Nobody; OK to take it and work on it")) {	
			
			return true;
		}
		if (string.contains("$$title_marker$$")) {
			
			return true;
		} else {
			return false;
		}
	}
	
	//liefert zu einer finalPredecessors Liste den entsprechenden XPath_Query
	private static String make_xpath_query (ArrayList<String> finalPredecessors){
		
		int size = finalPredecessors.size();
		String Xpath_query = "//ns:" + finalPredecessors.get(0);
		
		for(int tmp = 1; tmp < size; tmp++){
			Xpath_query = Xpath_query + "/ns:" + finalPredecessors.get(tmp);
		}
		Xpath_query = Xpath_query + "/text()";
		
		return Xpath_query;
	}
	
	//liefert zu einer finalPredecessors Liste den entsprechenden XPath_Query
	private static void write_genfile (Document doc, String string){
		
		XMLOutputter xmlOutput = new XMLOutputter();  
		  
		try {
			xmlOutput.output(doc, System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		  
		xmlOutput.setFormat(Format.getPrettyFormat());  
		try {
			xmlOutput.output(doc, new FileWriter(string));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
